package co.edu.uco.ucotestapi.inicializador;

import java.time.LocalDate;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.DocenteJPARepositorio;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.EstudianteGrupoJPARepositorio;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.EstudianteJPARepositorio;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.GrupoJPARepositorio;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.NotaJPARepositorio;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.DocenteEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteGrupoEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.GrupoEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEntidadJPA;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.Reloj;
import co.edu.uco.ucotestapi.transversal.UuidV7;

/**
 * Siembra la clase simulada al arrancar si la base está vacía (A-16, A-23). No corre en el perfil {@code test}:
 * cada prueba prepara sus propios datos (A-62 (9)).
 */
@Component
@Profile("!test")
public class CargadorDeClaseSimulada implements ApplicationRunner {

	private static final Logger LOG = LoggerFactory.getLogger(CargadorDeClaseSimulada.class);

	private final DocenteJPARepositorio docentes;
	private final EstudianteJPARepositorio estudiantes;
	private final GrupoJPARepositorio grupos;
	private final NotaJPARepositorio evaluaciones;
	private final EstudianteGrupoJPARepositorio matriculas;
	private final TransactionTemplate transaccion;
	private final Reloj reloj;

	public CargadorDeClaseSimulada(DocenteJPARepositorio docentes, EstudianteJPARepositorio estudiantes,
			GrupoJPARepositorio grupos, NotaJPARepositorio evaluaciones, EstudianteGrupoJPARepositorio matriculas,
			TransactionTemplate transaccion, Reloj reloj) {
		this.docentes = Objects.requireNonNull(docentes, "docentes");
		this.estudiantes = Objects.requireNonNull(estudiantes, "estudiantes");
		this.grupos = Objects.requireNonNull(grupos, "grupos");
		this.evaluaciones = Objects.requireNonNull(evaluaciones, "evaluaciones");
		this.matriculas = Objects.requireNonNull(matriculas, "matriculas");
		this.transaccion = Objects.requireNonNull(transaccion, "transaccion");
		this.reloj = Objects.requireNonNull(reloj, "reloj");
	}

	@Override
	public void run(ApplicationArguments argumentos) {
		if (docentes.count() > 0) {
			LOG.info("La clase simulada ya está sembrada; no se toca");
			return;
		}
		transaccion.executeWithoutResult(estado -> sembrar());
	}

	private void sembrar() {
		LocalDate hoy = reloj.hoy();
		DocenteEntidadJPA carlos = docentes.save(new DocenteEntidadJPA(UuidV7.nuevo(),
				ClaseSimulada.DOCENTE.cuentaId(), ClaseSimulada.DOCENTE.nombre(), true));
		GrupoEntidadJPA grupo = grupos.save(new GrupoEntidadJPA(UuidV7.nuevo(), ClaseSimulada.GRUPO,
				hoy.minusDays(ClaseSimulada.DIAS_ANTES), hoy.plusDays(ClaseSimulada.DIAS_DESPUES), carlos));
		evaluaciones.save(new NotaEntidadJPA(UuidV7.nuevo(), ClaseSimulada.EVALUACION, grupo));
		ClaseSimulada.ESTUDIANTES.forEach(persona -> {
			EstudianteEntidadJPA estudiante = estudiantes.save(
					new EstudianteEntidadJPA(UuidV7.nuevo(), persona.cuentaId(), persona.nombre(), true));
			matriculas.save(new EstudianteGrupoEntidadJPA(UuidV7.nuevo(), estudiante, grupo));
		});
		LOG.info("Clase simulada sembrada: {} con {} estudiantes, periodo {} a {}", ClaseSimulada.GRUPO,
				ClaseSimulada.ESTUDIANTES.size(), grupo.getFechaInicio(), grupo.getFechaFin());
	}
}
