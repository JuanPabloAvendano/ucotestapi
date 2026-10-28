package co.edu.uco.ucotestapi.dominio.reglas;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

import co.edu.uco.ucotestapi.dominio.calificacion.Calificacion;
import co.edu.uco.ucotestapi.dominio.calificacion.FechasDeRegistro;
import co.edu.uco.ucotestapi.dominio.calificacion.RangoCalificacion;
import co.edu.uco.ucotestapi.dominio.cuenta.CuentaId;
import co.edu.uco.ucotestapi.dominio.docente.Docente;
import co.edu.uco.ucotestapi.dominio.docente.DocenteId;
import co.edu.uco.ucotestapi.dominio.estudiante.Estudiante;
import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteGrupo;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteGrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.Grupo;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.Nota;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;
import co.edu.uco.ucotestapi.dominio.grupo.Periodo;
import co.edu.uco.ucotestapi.dominio.mensajes.CodigoMensaje;
import co.edu.uco.ucotestapi.dominio.reglas.excepciones.ReglaIncumplida;

/**
 * Pares cumple/viola de §4 a nivel de dominio (U-06): cada caso cambia una sola cosa de una solicitud válida y
 * espera el código de esa regla, lo que también prueba el orden de A-29.
 */
class ReglasDelRegistroTest {

	private static final LocalDate HOY = LocalDate.of(2026, 10, 25);

	private final ReglasDelRegistro reglas = new ReglasDelRegistro();

	private final Docente carlos = Docente.existente(new DocenteId(UUID.randomUUID()), new CuentaId("sub-carlos"),
			"Carlos", true);
	private final Grupo arquitectura = Grupo.existente(new GrupoId(UUID.randomUUID()), "Arquitectura 01",
			new Periodo(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 12, 15)), carlos.id());
	private final Nota parcial = Nota.existente(new NotaId(UUID.randomUUID()), arquitectura.id(), "Parcial 1");
	private final Estudiante ana = Estudiante.existente(new EstudianteId(UUID.randomUUID()), new CuentaId("sub-ana"),
			"Ana", true);
	private final EstudianteGrupo matricula = EstudianteGrupo.existente(new EstudianteGrupoId(UUID.randomUUID()),
			ana.id(), arquitectura.id());

	private SolicitudDeRegistro valida() {
		return new SolicitudDeRegistro(carlos, arquitectura, parcial, ana, matricula, new Calificacion(435),
				new RangoCalificacion(0, 500),
				new FechasDeRegistro(LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 31)), HOY);
	}

	private SolicitudDeRegistro con(UnaryOperator<Campos> cambio) {
		Campos c = cambio.apply(new Campos(valida()));
		return new SolicitudDeRegistro(c.docente, c.grupo, c.evaluacion, c.estudiante, c.matricula, c.calificacion,
				c.solicitud.rango(), c.solicitud.fechas(), c.hoy);
	}

	@Test
	void unaSolicitudValidaCumpleTodasLasReglas() {
		assertThatCode(() -> reglas.exigirAntesDelBloqueo(valida())).doesNotThrowAnyException();
	}

	@Test
	void r9CalificacionFueraDeRango() {
		esperar(con(c -> c.calificacion(new Calificacion(501))), CodigoMensaje.M_NOTA_R09);
		esperar(con(c -> c.calificacion(new Calificacion(-1))), CodigoMensaje.M_NOTA_R09);
	}

	@Test
	void r0DocenteQueNoExiste() {
		esperar(con(c -> c.docente(Docente.noExiste())), CodigoMensaje.M_NOTA_R00);
	}

	@Test
	void r7DocenteInactivo() {
		esperar(con(c -> c.docente(Docente.existente(carlos.id(), carlos.cuenta(), "Carlos", false))),
				CodigoMensaje.M_NOTA_R07);
	}

	@Test
	void r8GrupoQueNoExiste() {
		esperar(con(c -> c.grupo(Grupo.noExiste())), CodigoMensaje.M_NOTA_R08);
	}

	@Test
	void r3GrupoFueraDeSuPeriodo() {
		esperar(con(c -> c.hoy(LocalDate.of(2026, 12, 16))), CodigoMensaje.M_NOTA_R03);
	}

	@Test
	void r2DocenteNoAsignado() {
		Docente pedro = Docente.existente(new DocenteId(UUID.randomUUID()), new CuentaId("sub-pedro"), "Pedro", true);
		esperar(con(c -> c.docente(pedro)), CodigoMensaje.M_NOTA_R02);
	}

	@Test
	void r4EvaluacionDeOtroGrupoOInexistente() {
		esperar(con(c -> c.evaluacion(Nota.existente(parcial.id(), new GrupoId(UUID.randomUUID()), "Otro"))),
				CodigoMensaje.M_NOTA_R04);
		esperar(con(c -> c.evaluacion(Nota.noExiste())), CodigoMensaje.M_NOTA_R04);
	}

	@Test
	void r5FueraDeLasFechasDeRegistro() {
		esperar(con(c -> c.hoy(LocalDate.of(2026, 11, 1))), CodigoMensaje.M_NOTA_R05);
	}

	@Test
	void r1MismoCodigoParaSusTresCondiciones() {
		esperar(con(c -> c.estudiante(Estudiante.noExiste())), CodigoMensaje.M_NOTA_R01);
		esperar(con(c -> c.estudiante(Estudiante.existente(ana.id(), ana.cuenta(), "Ana", false))),
				CodigoMensaje.M_NOTA_R01);
		esperar(con(c -> c.matricula(EstudianteGrupo.noExiste())), CodigoMensaje.M_NOTA_R01);
	}

	@Test
	void r6EstudianteQueYaTieneCalificacion() {
		assertThatCode(() -> reglas.exigirSinCalificacionPrevia(new CalificacionPrevia(false)))
				.doesNotThrowAnyException();
		assertThatThrownBy(() -> reglas.exigirSinCalificacionPrevia(new CalificacionPrevia(true)))
				.isInstanceOfSatisfying(ReglaIncumplida.class,
						regla -> org.assertj.core.api.Assertions.assertThat(regla.codigo()).isEqualTo(CodigoMensaje.M_NOTA_R06));
	}

	@Test
	void unaMatriculaQueDesaparecioAntesDelBloqueoRespondeComoR1() {
		assertThatThrownBy(() -> reglas.exigirMatriculaBloqueada(EstudianteGrupo.noExiste()))
				.isInstanceOfSatisfying(ReglaIncumplida.class,
						regla -> org.assertj.core.api.Assertions.assertThat(regla.codigo()).isEqualTo(CodigoMensaje.M_NOTA_R01));
	}

	private void esperar(SolicitudDeRegistro solicitud, CodigoMensaje codigo) {
		assertThatThrownBy(() -> reglas.exigirAntesDelBloqueo(solicitud))
				.isInstanceOfSatisfying(ReglaIncumplida.class,
						regla -> org.assertj.core.api.Assertions.assertThat(regla.codigo()).isEqualTo(codigo));
	}

	private static final class Campos {
		final SolicitudDeRegistro solicitud;
		Docente docente;
		Grupo grupo;
		Nota evaluacion;
		Estudiante estudiante;
		EstudianteGrupo matricula;
		Calificacion calificacion;
		LocalDate hoy;

		Campos(SolicitudDeRegistro s) {
			solicitud = s;
			docente = s.docente();
			grupo = s.grupo();
			evaluacion = s.evaluacion();
			estudiante = s.estudiante();
			matricula = s.matricula();
			calificacion = s.calificacion();
			hoy = s.hoy();
		}

		Campos docente(Docente d) { docente = d; return this; }
		Campos grupo(Grupo g) { grupo = g; return this; }
		Campos evaluacion(Nota n) { evaluacion = n; return this; }
		Campos estudiante(Estudiante e) { estudiante = e; return this; }
		Campos matricula(EstudianteGrupo m) { matricula = m; return this; }
		Campos calificacion(Calificacion c) { calificacion = c; return this; }
		Campos hoy(LocalDate h) { hoy = h; return this; }
	}
}
