package co.edu.uco.ucotestapi.infraestructura.input.controladores;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.jayway.jsonpath.JsonPath;

import co.edu.uco.ucotestapi.PruebaDeIntegracion;

/**
 * C-1 y los pares cumple/viola C-3, C-5..C-16 contra Postgres real (A-47), por la cadena completa del backend.
 * Cada «viola» cambia una sola cosa del caso feliz (§4).
 */
@ExtendWith(OutputCaptureExtension.class)
class RegistrarNotaAEstudianteIntegracionTest extends PruebaDeIntegracion {

	@Test
	void c1RegistraLaCalificacionYResponde201ConLocationYMensaje() throws Exception {
		String respuesta = registrarAAna("435")
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", startsWith("/api/v1/grupos/%s/notas/%s/calificaciones/"
						.formatted(clase.arquitectura01(), clase.parcial1()))))
				.andExpect(jsonPath("$.centesimas").value(435))
				.andExpect(jsonPath("$.mensaje").value("La calificación se registró con éxito."))
				.andReturn().getResponse().getContentAsString();

		UUID id = UUID.fromString(JsonPath.read(respuesta, "$.id"));
		assertThat(id.version()).as("UUID v7 (A-4)").isEqualTo(7);
		assertThat(jdbc.queryForObject("select centesimas from nota_estudiante where id = ?", Integer.class, id))
				.isEqualTo(435);
		assertThat(avisos.enviados()).singleElement()
				.satisfies(aviso -> assertThat(aviso.calificacionId()).isEqualTo(id));
	}

	@Nested
	class C5R9 {
		@Test
		void cumple() throws Exception {
			registrarAAna("500").andExpect(status().isCreated());
		}

		@Test
		void viola() throws Exception {
			esperar(registrarAAna("501"), "M-NOTA-R09", "La calificación debe estar entre 0.00 y 5.00.");
			esperar(registrarAAna("-1"), "M-NOTA-R09", null);
		}
	}

	@Nested
	class C6R0 {
		@Test
		void viola() throws Exception {
			esperar(registrar("sub-sin-cuenta", clase.arquitectura01(), clase.parcial1(), clase.ana(), "435"),
					"M-NOTA-R00", null);
		}
	}

	@Nested
	class C7R7 {
		@Test
		void viola() throws Exception {
			esperar(registrar(clase.cuentaDiana(), clase.arquitectura01(), clase.parcial1(), clase.ana(), "435"),
					"M-NOTA-R07", null);
		}
	}

	@Nested
	class C8R8 {
		@Test
		void viola() throws Exception {
			esperar(registrar(clase.cuentaCarlos(), UUID.randomUUID(), clase.parcial1(), clase.ana(), "435"),
					"M-NOTA-R08", null);
		}
	}

	@Nested
	class C9R3 {
		@Test
		void viola() throws Exception {
			esperar(registrar(clase.cuentaCarlos(), clase.grupoVencido(), clase.parcialVencido(), clase.ana(), "435"),
					"M-NOTA-R03", null);
		}
	}

	@Nested
	class C10R2 {
		@Test
		void viola() throws Exception {
			esperar(registrar(clase.cuentaPedro(), clase.arquitectura01(), clase.parcial1(), clase.ana(), "435"),
					"M-NOTA-R02", null);
		}
	}

	@Nested
	class C11R4 {
		@Test
		void violaConEvaluacionDeOtroGrupo() throws Exception {
			esperar(registrar(clase.cuentaCarlos(), clase.arquitectura01(), clase.tallerDePedro(), clase.ana(), "435"),
					"M-NOTA-R04", null);
		}

		@Test
		void violaConEvaluacionInexistente() throws Exception {
			esperar(registrar(clase.cuentaCarlos(), clase.arquitectura01(), UUID.randomUUID(), clase.ana(), "435"),
					"M-NOTA-R04", null);
		}
	}

	@Nested
	class C12R5 {
		@Test
		void cumpleElUltimoDiaDelRegistro() throws Exception {
			reloj.fijar(LocalDate.of(2026, 10, 31));
			registrarAAna("435").andExpect(status().isCreated());
		}

		@Test
		void violaAlDiaSiguiente() throws Exception {
			reloj.fijar(LocalDate.of(2026, 11, 1));
			esperar(registrarAAna("435"), "M-NOTA-R05", null);
		}
	}

	@Nested
	class C13R1 {
		@Test
		void violaConEstudianteInexistenteInactivoONoMatriculadoYElMismoCodigo() throws Exception {
			esperar(registrar(clase.cuentaCarlos(), clase.arquitectura01(), clase.parcial1(), UUID.randomUUID(), "435"),
					"M-NOTA-R01", "El estudiante no se puede calificar en este grupo.");
			esperar(registrar(clase.cuentaCarlos(), clase.arquitectura01(), clase.parcial1(), clase.luisInactivo(), "435"),
					"M-NOTA-R01", null);
			esperar(registrar(clase.cuentaCarlos(), clase.arquitectura01(), clase.parcial1(), clase.martaSinMatricula(),
					"435"), "M-NOTA-R01", null);
		}
	}

	@Nested
	class C14R6 {
		@Test
		void violaSiYaTieneCalificacion() throws Exception {
			datos.calificacion(clase.parcial1(), clase.ana(), 400);

			esperar(registrarAAna("435"), "M-NOTA-R06", null);
			assertThat(jdbc.queryForObject("select centesimas from nota_estudiante where nota_id = ? and estudiante_id = ?",
					Integer.class, clase.parcial1(), clase.ana())).isEqualTo(400);
		}
	}

	@Nested
	class C15R11YC4 {
		/** Un fallo técnico real de Postgres al insertar, sin dobles de persistencia (A-47): un trigger que lo rechaza. */
		@Test
		void unFalloTecnicoAlGuardarEs500GenericoYNoGuardaNada(CapturedOutput salida) throws Exception {
			jdbc.execute("""
					create or replace function rechazar_insercion() returns trigger as $$
					begin raise exception 'disco lleno simulado'; end; $$ language plpgsql""");
			jdbc.execute("create trigger rechazar before insert on nota_estudiante for each row execute function rechazar_insercion()");
			try {
				String cuerpo = registrarAAna("435")
						.andExpect(status().isInternalServerError())
						.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
						.andExpect(jsonPath("$.codigo").value("M-GEN-500"))
						.andExpect(jsonPath("$.detail").value(
								"Ocurrió un error inesperado. Si se repite, comparte el código de seguimiento."))
						.andReturn().getResponse().getContentAsString();
				assertThat(cuerpo).as("sin detalles internos (C-4)").doesNotContain("disco lleno");
			}
			finally {
				jdbc.execute("drop trigger rechazar on nota_estudiante");
			}
			assertThat(datos.calificacionesDe(clase.parcial1(), clase.ana())).isZero();
			assertThat(salida.getOut()).contains("Error inesperado");
		}
	}

	@Nested
	class C16R12 {
		@Test
		void unAvisoQueFallaNoCambiaEl201YQuedaEnElLog(CapturedOutput salida) throws Exception {
			avisos.fallarConElSiguiente();

			registrarAAna("435").andExpect(status().isCreated());

			assertThat(datos.calificacionesDe(clase.parcial1(), clase.ana())).isEqualTo(1);
			assertThat(salida.getOut()).contains("WARN").contains("Aviso no enviado (R-12 informar éxito)");
		}
	}

	@Test
	void c17UnDecimalEnCentesimasEs400YNoGuarda() throws Exception {
		registrarAAna("435.5")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.codigo").value("M-GEN-400"));
		assertThat(datos.calificacionesDe(clase.parcial1(), clase.ana())).isZero();
	}

	private ResultActions registrarAAna(String centesimas) throws Exception {
		return registrar(clase.cuentaCarlos(), clase.arquitectura01(), clase.parcial1(), clase.ana(), centesimas);
	}

	private ResultActions registrar(String cuenta, UUID grupo, UUID evaluacion, UUID estudiante, String centesimas)
			throws Exception {
		return mvc.perform(post("/api/v1/grupos/{g}/notas/{n}/calificaciones", grupo, evaluacion)
				.with(docente(cuenta))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"estudianteId":"%s","centesimas":%s}""".formatted(estudiante, centesimas)));
	}

	private void esperar(ResultActions respuesta, String codigo, String detalle) throws Exception {
		respuesta.andExpect(status().isUnprocessableContent())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.codigo").value(codigo))
				.andExpect(jsonPath("$.type").value("urn:ucotest:" + codigo))
				.andExpect(jsonPath("$.status").value(422));
		if (detalle != null) {
			respuesta.andExpect(jsonPath("$.detail").value(detalle));
		}
		assertThat(jdbc.queryForObject("select count(*) from nota_estudiante where centesimas <> 400", Integer.class))
				.as("una regla incumplida no guarda nada").isZero();
	}
}
