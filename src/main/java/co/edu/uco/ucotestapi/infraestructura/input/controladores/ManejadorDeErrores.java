package co.edu.uco.ucotestapi.infraestructura.input.controladores;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import co.edu.uco.ucotestapi.dominio.mensajes.CodigoMensaje;
import co.edu.uco.ucotestapi.dominio.reglas.excepciones.ReglaIncumplida;
import co.edu.uco.ucotestapi.servicios.casosuso.CalificacionNoEncontrada;
import co.edu.uco.ucotestapi.servicios.casosuso.ConsultaNoPermitida;

/**
 * Único advice (A-44). Extiende {@link ResponseEntityExceptionHandler} y sobrescribe sus métodos en lugar de
 * declarar {@code @ExceptionHandler} para las mismas excepciones, que darían «Ambiguous @ExceptionHandler» al
 * arrancar (A-62 (2), COH-57). Así 404, 405 y 415 conservan su estado con la forma de §5, y el manejador general
 * de {@code Exception} solo recibe lo realmente inesperado (COH-51).
 */
@RestControllerAdvice
public class ManejadorDeErrores extends ResponseEntityExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ManejadorDeErrores.class);

	private final FabricaDeProblemas problemas;

	public ManejadorDeErrores(FabricaDeProblemas problemas) {
		this.problemas = Objects.requireNonNull(problemas, "problemas");
	}

	@ExceptionHandler(ReglaIncumplida.class)
	public ResponseEntity<ProblemDetail> reglaIncumplida(ReglaIncumplida regla) {
		LOG.info("Regla incumplida: {}", regla.codigo().valor());
		return responder(HttpStatus.UNPROCESSABLE_CONTENT, regla.codigo());
	}

	@ExceptionHandler(ConsultaNoPermitida.class)
	public ResponseEntity<ProblemDetail> consultaNoPermitida(ConsultaNoPermitida rechazo) {
		LOG.info("Consulta no permitida");
		return responder(HttpStatus.FORBIDDEN, CodigoMensaje.M_GEN_403);
	}

	@ExceptionHandler(CalificacionNoEncontrada.class)
	public ResponseEntity<ProblemDetail> calificacionNoEncontrada(CalificacionNoEncontrada rechazo) {
		LOG.info("Calificación no encontrada");
		return responder(HttpStatus.NOT_FOUND, CodigoMensaje.M_GEN_404);
	}

	/** Lo inesperado: sin detalles internos en la respuesta, con traza completa en el log (C-4). */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ProblemDetail> inesperado(Exception fallo) {
		LOG.error("Error inesperado", fallo);
		return responder(HttpStatus.INTERNAL_SERVER_ERROR, CodigoMensaje.M_GEN_500);
	}

	/** JSON roto, campo ausente o {@code null}, 435.5 o "435" (A-26, A-60, A-62 (1)). */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		return handleExceptionInternal(ex, null, headers, HttpStatus.BAD_REQUEST, request);
	}

	/** {@code grupoId} o {@code notaId} que no son UUID (A-60). */
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatusCode status, WebRequest request) {
		return handleExceptionInternal(ex, null, headers, HttpStatus.BAD_REQUEST, request);
	}

	/** Valores de la petición que faltan, como {@code notaId} en la consulta de estudiantes (A-60). */
	@Override
	protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		return handleExceptionInternal(ex, null, headers, HttpStatus.BAD_REQUEST, request);
	}

	/** Todos los demás errores de Spring MVC pasan por aquí: conservan su estado y toman la forma de §5. */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode statusCode, WebRequest request) {
		if (statusCode.is5xxServerError()) {
			LOG.error("Error de Spring MVC con estado {}", statusCode.value(), ex);
		}
		else {
			LOG.info("Petición rechazada con estado {}: {}", statusCode.value(), ex.getMessage());
		}
		ProblemDetail problema = problemas.crear(statusCode, problemas.genericoPara(statusCode));
		return ResponseEntity.status(statusCode).headers(headers).body(problema);
	}

	private ResponseEntity<ProblemDetail> responder(HttpStatus estado, CodigoMensaje codigo) {
		return ResponseEntity.status(estado).body(problemas.crear(estado, codigo));
	}
}
