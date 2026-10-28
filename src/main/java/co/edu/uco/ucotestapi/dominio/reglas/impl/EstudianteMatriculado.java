package co.edu.uco.ucotestapi.dominio.reglas.impl;

import co.edu.uco.ucotestapi.dominio.reglas.Especificacion;
import co.edu.uco.ucotestapi.dominio.reglas.SolicitudDeRegistro;

/** Parte de R-1: el estudiante está matriculado en el grupo de la ruta. */
public final class EstudianteMatriculado implements Especificacion<SolicitudDeRegistro> {

	@Override
	public boolean esSatisfechaPor(SolicitudDeRegistro solicitud) {
		return solicitud.matricula().matriculaA(solicitud.estudiante().id(), solicitud.grupo().id());
	}
}
