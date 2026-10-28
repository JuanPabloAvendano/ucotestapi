package co.edu.uco.ucotestapi.infraestructura.input.controladores;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.edu.uco.ucotestapi.PruebaDeIntegracion;

/** Las consultas mínimas de A-24 con la forma de Q-13 y el destino del {@code Location} (Q-15). */
class ConsultasDelDocenteIntegracionTest extends PruebaDeIntegracion {

	@Test
	void losGruposDelDocenteVienenPaginados() throws Exception {
		mvc.perform(get("/api/v1/docentes/yo/grupos").with(docente(clase.cuentaCarlos())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.total").value(2))
				.andExpect(jsonPath("$.pagina").value(0))
				.andExpect(jsonPath("$.tamano").value(20))
				.andExpect(jsonPath("$.contenido[0].nombre").value("Arquitectura de Software 01"))
				.andExpect(jsonPath("$.contenido[0].inicio").value("2026-08-01"))
				.andExpect(jsonPath("$.contenido[0].fin").value("2026-12-15"));
	}

	@Test
	void lasEvaluacionesYLosEstudiantesDelGrupoConTieneNota() throws Exception {
		datos.calificacion(clase.parcial1(), clase.ana(), 400);

		mvc.perform(get("/api/v1/grupos/{g}/notas", clase.arquitectura01()).with(docente(clase.cuentaCarlos())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.contenido[0].id").value(clase.parcial1().toString()))
				.andExpect(jsonPath("$.contenido[0].descripcion").value("Parcial 1"));
		mvc.perform(get("/api/v1/grupos/{g}/estudiantes", clase.arquitectura01()).param("notaId", clase.parcial1().toString())
				.with(docente(clase.cuentaCarlos())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.total").value(2))
				.andExpect(jsonPath("$.contenido[0].nombre").value("Ana"))
				.andExpect(jsonPath("$.contenido[0].tieneNota").value(true))
				.andExpect(jsonPath("$.contenido[1].tieneNota").value(false));
	}

	@Test
	void unGrupoAjenoEs403() throws Exception {
		mvc.perform(get("/api/v1/grupos/{g}/notas", clase.grupoDePedro()).with(docente(clase.cuentaCarlos())))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.codigo").value("M-GEN-403"));
	}

	@Test
	void sinNotaIdLaConsultaDeEstudiantesEs400() throws Exception {
		mvc.perform(get("/api/v1/grupos/{g}/estudiantes", clase.arquitectura01()).with(docente(clase.cuentaCarlos())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.codigo").value("M-GEN-400"));
	}

	@Test
	void elLocationDelRegistroSeConsultaY404SiNoExiste() throws Exception {
		UUID id = datos.calificacion(clase.parcial1(), clase.ana(), 435);

		mvc.perform(get("/api/v1/grupos/{g}/notas/{n}/calificaciones/{id}", clase.arquitectura01(), clase.parcial1(), id)
				.with(docente(clase.cuentaCarlos())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estudianteId").value(clase.ana().toString()))
				.andExpect(jsonPath("$.centesimas").value(435));
		mvc.perform(get("/api/v1/grupos/{g}/notas/{n}/calificaciones/{id}", clase.arquitectura01(), clase.parcial1(),
				UUID.randomUUID()).with(docente(clase.cuentaCarlos())))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.codigo").value("M-GEN-404"));
		mvc.perform(get("/api/v1/grupos/{g}/notas/{n}/calificaciones/{id}", clase.arquitectura01(), clase.parcial1(), id)
				.with(docente(clase.cuentaPedro())))
				.andExpect(status().isForbidden());
	}
}
