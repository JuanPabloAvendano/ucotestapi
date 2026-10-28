package co.edu.uco.ucotestapi.servicios.casosuso.impl;

import java.time.LocalDate;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.edu.uco.ucotestapi.dominio.calificacion.Calificacion;
import co.edu.uco.ucotestapi.dominio.calificacion.FechasDeRegistro;
import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudiante;
import co.edu.uco.ucotestapi.dominio.calificacion.RangoCalificacion;
import co.edu.uco.ucotestapi.dominio.cuenta.CuentaId;
import co.edu.uco.ucotestapi.dominio.estudiante.Estudiante;
import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;
import co.edu.uco.ucotestapi.dominio.mensajes.CodigoMensaje;
import co.edu.uco.ucotestapi.dominio.mensajes.Mensaje;
import co.edu.uco.ucotestapi.dominio.reglas.CalificacionPrevia;
import co.edu.uco.ucotestapi.dominio.reglas.ReglasDelRegistro;
import co.edu.uco.ucotestapi.dominio.reglas.SolicitudDeRegistro;
import co.edu.uco.ucotestapi.servicios.casosuso.RegistrarNotaAEstudianteCasoUso;
import co.edu.uco.ucotestapi.servicios.casosuso.ResultadoDelRegistro;
import co.edu.uco.ucotestapi.servicios.output.adapter.ActualizacionEnVivoOutputAdapter;
import co.edu.uco.ucotestapi.servicios.output.adapter.AvisosDeRegistroOutputAdapter;
import co.edu.uco.ucotestapi.servicios.output.adapter.CatalogoDeMensajesOutputAdapter;
import co.edu.uco.ucotestapi.servicios.output.adapter.CatalogoDeParametrosOutputAdapter;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.GeneradorDeIds;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.UnidadDeTrabajo;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.DocenteRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.EstudianteRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.EvaluacionRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.GrupoRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.MatriculaRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.NotaEstudianteRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.AvisoNoEnviado;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.Reloj;

/**
 * Flujo de §5: lecturas lentas y catálogos fuera de la transacción; dentro, solo bloquear → R-6 → guardar (A-31);
 * los avisos después del commit, sin cambiar el 201 (A-39, A-53).
 */
public class RegistrarNotaAEstudianteCasoUsoImpl implements RegistrarNotaAEstudianteCasoUso {

	private static final Logger LOG = LoggerFactory.getLogger(RegistrarNotaAEstudianteCasoUsoImpl.class);
	private final EstudianteRepositorio estudiantes;
	private final DocenteRepositorio docentes;
	private final GrupoRepositorio grupos;
	private final EvaluacionRepositorio evaluaciones;
	private final MatriculaRepositorio matriculas;
	private final NotaEstudianteRepositorio calificaciones;
	private final UnidadDeTrabajo unidadDeTrabajo;
	private final GeneradorDeIds ids;
	private final Reloj reloj;
	private final CatalogoDeParametrosOutputAdapter parametros;
	private final CatalogoDeMensajesOutputAdapter mensajes;
	private final AvisosDeRegistroOutputAdapter avisos;
	private final ActualizacionEnVivoOutputAdapter actualizacionEnVivo;
	private final ReglasDelRegistro reglas = new ReglasDelRegistro();

	public RegistrarNotaAEstudianteCasoUsoImpl(EstudianteRepositorio estudiantes, DocenteRepositorio docentes, GrupoRepositorio grupos,
			EvaluacionRepositorio evaluaciones, MatriculaRepositorio matriculas,
			NotaEstudianteRepositorio calificaciones, UnidadDeTrabajo unidadDeTrabajo, GeneradorDeIds ids, Reloj reloj,
			CatalogoDeParametrosOutputAdapter parametros, CatalogoDeMensajesOutputAdapter mensajes,
			AvisosDeRegistroOutputAdapter avisos, ActualizacionEnVivoOutputAdapter actualizacionEnVivo) {
		this.estudiantes = Objects.requireNonNull(estudiantes, "estudiantes");
		this.docentes = Objects.requireNonNull(docentes, "docentes");
		this.grupos = Objects.requireNonNull(grupos, "grupos");
		this.evaluaciones = Objects.requireNonNull(evaluaciones, "evaluaciones");
		this.matriculas = Objects.requireNonNull(matriculas, "matriculas");
		this.calificaciones = Objects.requireNonNull(calificaciones, "calificaciones");
		this.unidadDeTrabajo = Objects.requireNonNull(unidadDeTrabajo, "unidadDeTrabajo");
		this.ids = Objects.requireNonNull(ids, "ids");
		this.reloj = Objects.requireNonNull(reloj, "reloj");
		this.parametros = Objects.requireNonNull(parametros, "parametros");
		this.mensajes = Objects.requireNonNull(mensajes, "mensajes");
		this.avisos = Objects.requireNonNull(avisos, "avisos");
		this.actualizacionEnVivo = Objects.requireNonNull(actualizacionEnVivo, "actualizacionEnVivo");
	}

	@Override
	public ResultadoDelRegistro ejecutar(EstudianteId estudianteId, CuentaId cuentaDocente, GrupoId grupo, NotaId evaluacion,
			Calificacion calificacion) {
		RangoCalificacion rango = parametros.rango();
		FechasDeRegistro fechas = parametros.fechasDeRegistro();
		// El texto de éxito se lee antes del commit: si el catálogo falla después, la calificación ya guardada
		// respondería 500 (A-39, COH-16).
		Mensaje exito = mensajes.porCodigo(CodigoMensaje.M_NOTA_R12);
		LocalDate hoy = reloj.hoy();

		reglas.exigirAntesDelBloqueo(new SolicitudDeRegistro(docentes.porCuenta(cuentaDocente), grupos.porId(grupo),
				evaluaciones.porId(evaluacion),estudiantes.porId(estudianteId),matriculas.de(estudianteId, grupo),
				calificacion, rango, fechas, hoy));

		NotaEstudiante guardada = unidadDeTrabajo.ejecutar(() -> {
			reglas.exigirMatriculaBloqueada(matriculas.bloquear(estudianteId, grupo));
			reglas.exigirSinCalificacionPrevia(new CalificacionPrevia(calificaciones.existe(evaluacion, estudianteId)));
			return calificaciones.guardar(NotaEstudiante.registrar(ids.nuevo(), evaluacion,estudianteId, calificacion));
		});

		avisarSinCambiarLaRespuesta(guardada);
		return new ResultadoDelRegistro(guardada, exito);
	}

	/** Commit hecho = 201 siempre (A-39): cada aviso se intenta por separado y su fallo solo se registra (A-53). */
	private void avisarSinCambiarLaRespuesta(NotaEstudiante guardada) {
		intentar("R-12 informar éxito", guardada, () -> avisos.informar(guardada));
		intentar("actualización en vivo", guardada, () -> actualizacionEnVivo.refrescar(guardada));
	}

	private static void intentar(String aviso, NotaEstudiante guardada, Runnable envio) {
		try {
			envio.run();
		}
		catch (AvisoNoEnviado fallo) {
			LOG.warn("Aviso no enviado ({}) para la calificación {}: {}", aviso, guardada.id().valor(),
					fallo.getMessage());
		}
		catch (RuntimeException fallo) {
			// Otra excepción es un defecto, no un canal caído: va como error con traza (A-62 (11), COH-44).
			LOG.error("Fallo inesperado al avisar ({}) la calificación {}", aviso, guardada.id().valor(), fallo);
		}
	}
}
