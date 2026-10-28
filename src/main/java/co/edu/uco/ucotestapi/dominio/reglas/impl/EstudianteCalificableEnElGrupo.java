package co.edu.uco.ucotestapi.dominio.reglas.impl;

import co.edu.uco.ucotestapi.dominio.reglas.Especificacion;
import co.edu.uco.ucotestapi.dominio.reglas.SolicitudDeRegistro;

/** R-1 compuesta (A-7, A-43): el estudiante existe, está activo y está matriculado en el grupo. */
public final class EstudianteCalificableEnElGrupo implements Especificacion<SolicitudDeRegistro>{
	Especificacion<SolicitudDeRegistro> regla= new EstudianteExiste().y(new EstudianteActivo()).y(new EvaluacionDelGrupo()).y(new EstudianteMatriculado());
	@Override
	public boolean esSatisfechaPor(SolicitudDeRegistro solicitud) {
		
		return regla.esSatisfechaPor(solicitud);
	}
}
