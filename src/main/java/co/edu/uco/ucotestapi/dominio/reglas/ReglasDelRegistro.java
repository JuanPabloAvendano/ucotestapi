package co.edu.uco.ucotestapi.dominio.reglas;

import java.util.List;

import co.edu.uco.ucotestapi.dominio.grupo.EstudianteGrupo;
import co.edu.uco.ucotestapi.dominio.mensajes.CodigoMensaje;
import co.edu.uco.ucotestapi.dominio.reglas.excepciones.ReglaIncumplida;
import co.edu.uco.ucotestapi.dominio.reglas.impl.CalificacionDentroDelRango;
import co.edu.uco.ucotestapi.dominio.reglas.impl.DentroDeFechasDeRegistro;
import co.edu.uco.ucotestapi.dominio.reglas.impl.DocenteActivo;
import co.edu.uco.ucotestapi.dominio.reglas.impl.DocenteAsignadoAlGrupo;
import co.edu.uco.ucotestapi.dominio.reglas.impl.DocenteExiste;
import co.edu.uco.ucotestapi.dominio.reglas.impl.EstudianteCalificableEnElGrupo;
import co.edu.uco.ucotestapi.dominio.reglas.impl.EstudianteSinCalificacion;
import co.edu.uco.ucotestapi.dominio.reglas.impl.EvaluacionDelGrupo;
import co.edu.uco.ucotestapi.dominio.reglas.impl.GrupoExiste;
import co.edu.uco.ucotestapi.dominio.reglas.impl.GrupoVigente;

/**
 * Las reglas del registro en el orden de dependencia de A-29; corta en la primera que falla. R-6 va aparte porque
 * se evalúa dentro de la transacción, después de bloquear la matrícula (A-22, A-31).
 */
public final class ReglasDelRegistro {

	private static final List<ReglaDelRegistro<SolicitudDeRegistro>> ANTES_DEL_BLOQUEO = List.of(
			new ReglaDelRegistro<>(CodigoMensaje.M_NOTA_R09, new CalificacionDentroDelRango()),
			new ReglaDelRegistro<>(CodigoMensaje.M_NOTA_R00, new DocenteExiste()),
			new ReglaDelRegistro<>(CodigoMensaje.M_NOTA_R07, new DocenteActivo()),
			
			new ReglaDelRegistro<>(CodigoMensaje.M_NOTA_R08, new GrupoExiste()),
			new ReglaDelRegistro<>(CodigoMensaje.M_NOTA_R03, new GrupoVigente()),
			new ReglaDelRegistro<>(CodigoMensaje.M_NOTA_R02, new DocenteAsignadoAlGrupo()),
			new ReglaDelRegistro<>(CodigoMensaje.M_NOTA_R04, new EvaluacionDelGrupo()),
			new ReglaDelRegistro<>(CodigoMensaje.M_NOTA_R01, new EstudianteCalificableEnElGrupo()),
			new ReglaDelRegistro<>(CodigoMensaje.M_NOTA_R05, new DentroDeFechasDeRegistro()));
	// PRA-1 (práctica de estudio de Juan): lógica del estudiante retirada a propósito; reescribirla siguiendo al docente.


	private static final ReglaDelRegistro<CalificacionPrevia> SIN_CALIFICACION_PREVIA = new ReglaDelRegistro<>(
			CodigoMensaje.M_NOTA_R06, new EstudianteSinCalificacion());

	public void exigirAntesDelBloqueo(SolicitudDeRegistro solicitud) {
		ANTES_DEL_BLOQUEO.forEach(regla -> regla.exigir(solicitud));
	}

	/**
	 * Sin fila no hay bloqueo, y R-6 y el guardado quedarían sin protección; por eso una matrícula que desapareció
	 * entre la lectura y el bloqueo responde como R-1 (A-62 (7), COH-38).
	 */
	public void exigirMatriculaBloqueada(EstudianteGrupo bloqueada) {
		if (!bloqueada.existe()) {
			throw new ReglaIncumplida(CodigoMensaje.M_NOTA_R01);
		}
	}

	public void exigirSinCalificacionPrevia(CalificacionPrevia previa) {
		SIN_CALIFICACION_PREVIA.exigir(previa);
	}
}
