package co.edu.uco.ucotestapi.infraestructura.input.controladores;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import co.edu.uco.ucotestapi.dominio.mensajes.CodigoMensaje;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.CatalogoDeMensajes;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

/**
 * Arma el problem+json de §5 (RFC 9457) con {@code codigo}, {@code detail} del message catalog y {@code traceId}
 * (A-20, A-45). Lo usan el advice y los manejadores 401/403 de la cadena de seguridad, que no pasan por el advice
 * (A-62 (2)).
 */
@Component
public class FabricaDeProblemas {

	/** Respaldo si el catálogo falla dentro del manejo de otro error (A-62 (3), COH-49). */
	static final String TEXTO_DE_RESPALDO = "No pudimos procesar la solicitud. Intenta de nuevo más tarde.";

	private static final Logger LOG = LoggerFactory.getLogger(FabricaDeProblemas.class);

	private final CatalogoDeMensajes catalogo;
	private final ObjectProvider<Tracer> trazas;
	private final JsonMapper json;

	public FabricaDeProblemas(CatalogoDeMensajes catalogo, ObjectProvider<Tracer> trazas, JsonMapper json) {
		this.catalogo = Objects.requireNonNull(catalogo, "catalogo");
		this.trazas = Objects.requireNonNull(trazas, "trazas");
		this.json = Objects.requireNonNull(json, "json");
	}

	public ProblemDetail crear(HttpStatusCode estado, CodigoMensaje codigo) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, texto(codigo));
		problema.setType(URI.create("urn:ucotest:" + codigo.valor()));
		HttpStatus conocido = HttpStatus.resolve(estado.value());
		if (conocido != null) {
			problema.setTitle(conocido.getReasonPhrase());
		}
		problema.setProperty("codigo", codigo.valor());
		problema.setProperty("traceId", traceIdActual());
		return problema;
	}

	/** Código genérico por estado HTTP (A-37: {@code M-GEN-nnn}); los estados sin texto propio caen en 400 o 500. */
	public CodigoMensaje genericoPara(HttpStatusCode estado) {
		return switch (estado.value()) {
			case 400 -> CodigoMensaje.M_GEN_400;
			case 401 -> CodigoMensaje.M_GEN_401;
			case 403 -> CodigoMensaje.M_GEN_403;
			case 404 -> CodigoMensaje.M_GEN_404;
			case 405 -> CodigoMensaje.M_GEN_405;
			case 406 -> CodigoMensaje.M_GEN_406;
			case 415 -> CodigoMensaje.M_GEN_415;
			default -> estado.is4xxClientError() ? CodigoMensaje.M_GEN_400 : CodigoMensaje.M_GEN_500;
		};
	}

	/** Para la cadena de filtros, donde no hay {@code ResponseEntity} que Spring MVC convierta. */
	public void escribir(HttpServletResponse respuesta, ProblemDetail problema) throws IOException {
		respuesta.setStatus(problema.getStatus());
		// JSON siempre va en UTF-8 (RFC 8259 §8.1): Jackson escribe bytes y no hace falta el parámetro charset.
		respuesta.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
		json.writeValue(respuesta.getOutputStream(), problema);
	}

	/** Sin span activo va vacío, nunca {@code null} (A-45). */
	public String traceIdActual() {
		Tracer tracer = trazas.getIfAvailable();
		Span actual = tracer == null ? null : tracer.currentSpan();
		return actual == null ? "" : actual.context().traceId();
	}

	private String texto(CodigoMensaje codigo) {
		try {
			return catalogo.porCodigo(codigo.valor()).texto();
		}
		catch (RuntimeException fallo) {
			LOG.error("El catálogo de mensajes falló al responder {}", codigo.valor(), fallo);
			return TEXTO_DE_RESPALDO;
		}
	}
}
