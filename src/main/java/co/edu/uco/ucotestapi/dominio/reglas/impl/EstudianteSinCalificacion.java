package co.edu.uco.ucotestapi.dominio.reglas.impl;

import co.edu.uco.ucotestapi.dominio.reglas.CalificacionPrevia;
import co.edu.uco.ucotestapi.dominio.reglas.Especificacion;

/** R-6: el estudiante aún no tiene calificación en la evaluación. */
public final class EstudianteSinCalificacion implements Especificacion<CalificacionPrevia> {

	@Override
	public boolean esSatisfechaPor(CalificacionPrevia previa) {
		return !previa.existe();
	}
}
