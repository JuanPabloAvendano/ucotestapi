package co.edu.uco.ucotestapi.infraestructura.input.controladores;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import co.edu.uco.ucotestapi.infraestructura.output.serviciosexternos.messagecatalog.CatalogoDeMensajesLocal;
import co.edu.uco.ucotestapi.servicios.input.dto.request.RegistrarNotaAEstudianteRequest;
import co.edu.uco.ucotestapi.servicios.input.dto.response.RegistrarNotaAEstudianteResponse;
import co.edu.uco.ucotestapi.servicios.input.ports.RegistrarNotaAEstudianteInputPort;

/**
 * Prueba de la capa web sola (EP-20, A-47): lectura estricta del cuerpo y de la ruta (C-2, C-17, A-26, A-60,
 * A-62 (1)) y autorización con los roles de Keycloak (C-20, A-48). El puerto de entrada es un stub que registra lo
 * que recibe: aquí no hay persistencia que doblar (A-50).
 */
@WebMvcTest(RegistrarNotaAEstudianteControlador.class)
@Import({ ConfiguracionDeSeguridad.class, FabricaDeProblemas.class, ManejadorDeErrores.class,
		CatalogoDeMensajesLocal.class, RegistrarNotaAEstudianteControladorWebMvcTest.PuertoDePrueba.class })
class RegistrarNotaAEstudianteControladorWebMvcTest {

	private static final UUID GRUPO = UUID.fromString("0192f3a1-7b2c-7d4e-9a1b-3c5d6e7f8a01");
	private static final UUID NOTA = UUID.fromString("0192f3a1-7b2c-7d4e-9a1b-3c5d6e7f8a02");
	private static final String ESTUDIANTE = "0192f3a1-7b2c-7d4e-9a1b-3c5d6e7f8a9b";

	@TestConfiguration(proxyBeanMethods = false)
	static class PuertoDePrueba {

		static final List<RegistrarNotaAEstudianteRequest> RECIBIDAS = new CopyOnWriteArrayList<>();

		@Bean
		RegistrarNotaAEstudianteInputPort puertoDePrueba() {
			return (request, cuenta) -> {
				RECIBIDAS.add(request);
				return new RegistrarNotaAEstudianteResponse(UUID.randomUUID(), request.centesimas(), "ok");
			};
		}
	}

	@Autowired
	MockMvc mvc;

	@BeforeEach
	void limpiar() {
		PuertoDePrueba.RECIBIDAS.clear();
	}

	@Test
	void unCuerpoBienFormadoLlegaAlPuertoComoCentesimas() throws Exception {
		mvc.perform(registrar("{\"estudianteId\":\"%s\",\"centesimas\":435}".formatted(ESTUDIANTE)).with(docente()))
				.andExpect(status().isCreated());

		assertThat(PuertoDePrueba.RECIBIDAS).singleElement()
				.isEqualTo(new RegistrarNotaAEstudianteRequest(GRUPO, NOTA, UUID.fromString(ESTUDIANTE), 435));
	}

	@ParameterizedTest(name = "C-2/C-17: {0}")
	@ValueSource(strings = {
			"{\"estudianteId\":\"ESTUDIANTE\",\"centesimas\":435.5}",
			"{\"estudianteId\":\"ESTUDIANTE\",\"centesimas\":\"435\"}",
			"{\"estudianteId\":\"ESTUDIANTE\"}",
			"{\"centesimas\":435}",
			"{\"estudianteId\":null,\"centesimas\":435}",
			"{\"estudianteId\":\"ESTUDIANTE\",\"centesimas\":null}",
			"{\"estudianteId\":\"no-es-uuid\",\"centesimas\":435}",
			"{\"estudianteId\":\"ESTUDIANTE\",",
			"[]" })
	void unCuerpoMalFormadoEs400Generico(String cuerpo) throws Exception {
		mvc.perform(registrar(cuerpo.replace("ESTUDIANTE", ESTUDIANTE)).with(docente()))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.codigo").value("M-GEN-400"))
				.andExpect(jsonPath("$.type").value("urn:ucotest:M-GEN-400"))
				.andExpect(jsonPath("$.detail").value("La solicitud no tiene el formato esperado."))
				.andExpect(jsonPath("$.traceId").exists());

		assertThat(PuertoDePrueba.RECIBIDAS).isEmpty();
	}

	@Test
	void unaRutaConIdsQueNoSonUuidEs400Generico() throws Exception {
		mvc.perform(post("/api/v1/grupos/{g}/notas/{n}/calificaciones", "abc", NOTA)
				.with(docente())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"estudianteId\":\"%s\",\"centesimas\":435}".formatted(ESTUDIANTE)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.codigo").value("M-GEN-400"));
	}

	@Test
	void otroTipoDeContenidoConservaSu415ConLaFormaDeErrores() throws Exception {
		mvc.perform(post("/api/v1/grupos/{g}/notas/{n}/calificaciones", GRUPO, NOTA)
				.with(docente())
				.contentType(MediaType.TEXT_PLAIN)
				.content("435"))
				.andExpect(status().isUnsupportedMediaType())
				.andExpect(jsonPath("$.codigo").value("M-GEN-415"));
	}

	@Test
	void c20SinTokenEs401ConProblemJson() throws Exception {
		mvc.perform(registrar("{\"estudianteId\":\"%s\",\"centesimas\":435}".formatted(ESTUDIANTE)))
				.andExpect(status().isUnauthorized())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.codigo").value("M-GEN-401"));
	}

	@Test
	void c20ConTokenSinRolDocenteEs403ConProblemJson() throws Exception {
		mvc.perform(registrar("{\"estudianteId\":\"%s\",\"centesimas\":435}".formatted(ESTUDIANTE))
				.with(tokenConRoles("ESTUDIANTE")))
				.andExpect(status().isForbidden())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.codigo").value("M-GEN-403"));

		assertThat(PuertoDePrueba.RECIBIDAS).isEmpty();
	}

	private static MockHttpServletRequestBuilder registrar(String cuerpo) {
		return post("/api/v1/grupos/{g}/notas/{n}/calificaciones", GRUPO, NOTA)
				.contentType(MediaType.APPLICATION_JSON)
				.content(cuerpo);
	}

	private static RequestPostProcessor docente() {
		return tokenConRoles("DOCENTE");
	}

	/** Los roles pasan por el mismo convertidor de producción que lee {@code realm_access.roles} (A-48). */
	private static RequestPostProcessor tokenConRoles(String... roles) {
		return jwt().jwt(token -> token.subject("sub-carlos").claim("realm_access", Map.of("roles", List.of(roles))))
				.authorities(ConfiguracionDeSeguridad.rolesDelRealm());
	}
}
