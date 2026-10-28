package co.edu.uco.ucotestapi.infraestructura.input.controladores;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import com.jayway.jsonpath.JsonPath;

import co.edu.uco.ucotestapi.PruebaDeIntegracion;

/** C-21 (A-20, A-45): todo problem+json lleva un {@code traceId} no vacío, el mismo que aparece en el log. */
@ExtendWith(OutputCaptureExtension.class)
class TrazabilidadIntegracionTest extends PruebaDeIntegracion {

	@Test
	void un422LlevaElTraceIdQueAparecioEnElLog(CapturedOutput salida) throws Exception {
		MockHttpServletResponse respuesta = mvc.perform(
				post("/api/v1/grupos/{g}/notas/{n}/calificaciones", UUID.randomUUID(), clase.parcial1())
						.with(docente(clase.cuentaCarlos()))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"estudianteId":"%s","centesimas":435}""".formatted(clase.ana())))
				.andReturn().getResponse();

		assertThat(respuesta.getStatus()).isEqualTo(422);
		esperarTraceIdEnRespuestaYLog(respuesta, salida, "Regla incumplida: M-NOTA-R08");
	}

	@Test
	void un401DeLaCadenaDeSeguridadTambienLoLleva(CapturedOutput salida) throws Exception {
		MockHttpServletResponse respuesta = mvc.perform(get("/api/v1/docentes/yo/grupos")).andReturn().getResponse();

		assertThat(respuesta.getStatus()).isEqualTo(401);
		esperarTraceIdEnRespuestaYLog(respuesta, salida, "Petición sin token válido");
	}

	@Test
	void unTraceparentEntranteSeContinua() throws Exception {
		String traza = "4bf92f3577b34da6a3ce929d0e0e4736";
		MockHttpServletResponse respuesta = mvc.perform(get("/api/v1/docentes/yo/grupos")
				.header("traceparent", "00-%s-00f067aa0ba902b7-01".formatted(traza))).andReturn().getResponse();

		assertThat((String) JsonPath.read(respuesta.getContentAsString(), "$.traceId")).isEqualTo(traza);
	}

	private static void esperarTraceIdEnRespuestaYLog(MockHttpServletResponse respuesta, CapturedOutput salida,
			String lineaDeLog) throws Exception {
		String traceId = JsonPath.read(respuesta.getContentAsString(), "$.traceId");
		assertThat(traceId).matches("[0-9a-f]{32}");
		assertThat(salida.getOut().lines().filter(linea -> linea.contains(lineaDeLog)))
				.anySatisfy(linea -> assertThat(linea).contains(traceId));
	}
}
