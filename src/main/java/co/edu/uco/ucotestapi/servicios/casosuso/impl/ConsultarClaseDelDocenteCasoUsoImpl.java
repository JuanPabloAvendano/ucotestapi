package co.edu.uco.ucotestapi.servicios.casosuso.impl;

import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudiante;
import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudianteId;
import co.edu.uco.ucotestapi.dominio.cuenta.CuentaId;
import co.edu.uco.ucotestapi.dominio.docente.Docente;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteDelGrupo;
import co.edu.uco.ucotestapi.dominio.grupo.Grupo;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.Nota;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;
import co.edu.uco.ucotestapi.servicios.casosuso.CalificacionNoEncontrada;
import co.edu.uco.ucotestapi.servicios.casosuso.ConsultaNoPermitida;
import co.edu.uco.ucotestapi.servicios.casosuso.ConsultarClaseDelDocenteCasoUso;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.DocenteRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.EvaluacionRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.GrupoRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.MatriculaRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.NotaEstudianteRepositorio;
import co.edu.uco.ucotestapi.transversal.Pagina;

public class ConsultarClaseDelDocenteCasoUsoImpl implements ConsultarClaseDelDocenteCasoUso {

	private final DocenteRepositorio docentes;
	private final GrupoRepositorio grupos;
	private final EvaluacionRepositorio evaluaciones;
	private final MatriculaRepositorio matriculas;
	private final NotaEstudianteRepositorio calificaciones;

	public ConsultarClaseDelDocenteCasoUsoImpl(DocenteRepositorio docentes, GrupoRepositorio grupos,
			EvaluacionRepositorio evaluaciones, MatriculaRepositorio matriculas,
			NotaEstudianteRepositorio calificaciones) {
		this.docentes = Objects.requireNonNull(docentes, "docentes");
		this.grupos = Objects.requireNonNull(grupos, "grupos");
		this.evaluaciones = Objects.requireNonNull(evaluaciones, "evaluaciones");
		this.matriculas = Objects.requireNonNull(matriculas, "matriculas");
		this.calificaciones = Objects.requireNonNull(calificaciones, "calificaciones");
	}

	@Override
	public Pagina<Grupo> gruposDelDocente(CuentaId cuentaDocente, int pagina, int tamano) {
		Docente docente = docentes.porCuenta(cuentaDocente);
		if (!docente.existe()) {
			throw new ConsultaNoPermitida();
		}
		return grupos.delDocente(docente.id(), pagina, tamano);
	}

	@Override
	public Pagina<Nota> evaluacionesDelGrupo(CuentaId cuentaDocente, GrupoId grupo, int pagina, int tamano) {
		exigirGrupoDelDocente(cuentaDocente, grupo);
		return evaluaciones.delGrupo(grupo, pagina, tamano);
	}

	@Override
	public Pagina<EstudianteDelGrupo> estudiantesDelGrupo(CuentaId cuentaDocente, GrupoId grupo, NotaId evaluacion,
			int pagina, int tamano) {
		exigirGrupoDelDocente(cuentaDocente, grupo);
		return matriculas.estudiantesDe(grupo, evaluacion, pagina, tamano);
	}

	@Override
	public NotaEstudiante calificacion(CuentaId cuentaDocente, GrupoId grupo, NotaId evaluacion,
			NotaEstudianteId id) {
		exigirGrupoDelDocente(cuentaDocente, grupo);
		NotaEstudiante calificacion = calificaciones.porId(id);
		if (!calificacion.esDeLaEvaluacion(evaluacion) || !evaluaciones.porId(evaluacion).perteneceA(grupo)) {
			throw new CalificacionNoEncontrada();
		}
		return calificacion;
	}

	/** Un grupo inexistente tampoco es del docente: responde igual, para no revelar qué grupos existen (Q-13). */
	private void exigirGrupoDelDocente(CuentaId cuentaDocente, GrupoId grupo) {
		Docente docente = docentes.porCuenta(cuentaDocente);
		if (!docente.existe() || !grupos.porId(grupo).estaAsignadoA(docente.id())) {
			throw new ConsultaNoPermitida();
		}
	}
}
