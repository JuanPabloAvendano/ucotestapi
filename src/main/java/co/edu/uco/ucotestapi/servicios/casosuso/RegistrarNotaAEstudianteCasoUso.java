package co.edu.uco.ucotestapi.servicios.casosuso;

import co.edu.uco.ucotestapi.dominio.calificacion.Calificacion;
import co.edu.uco.ucotestapi.dominio.cuenta.CuentaId;
import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;

/**
 * Registra la calificación de un estudiante en una evaluación de su grupo (R-0..R-12). Recibe y devuelve objetos
 * del dominio, sin comando aparte (A-34, A-35).
 *
 * @throws co.edu.uco.ucotestapi.dominio.reglas.excepciones.ReglaIncumplida con el código de la primera regla que falla (A-29)
 */
public interface RegistrarNotaAEstudianteCasoUso {

	// PRA-1 (práctica de estudio de Juan): lógica del estudiante retirada a propósito; reescribirla siguiendo al docente.
	ResultadoDelRegistro ejecutar(EstudianteId estudianteId,CuentaId cuentaDocente, GrupoId grupo, NotaId evaluacion, Calificacion calificacion);
}
