package co.edu.uco.ucotestapi.dominio.reglas.impl;

import co.edu.uco.ucotestapi.dominio.reglas.Especificacion;
import co.edu.uco.ucotestapi.dominio.reglas.SolicitudDeRegistro;

/** Parte de R-1: el estudiante está activo. */
public final class EstudianteActivo implements Especificacion<SolicitudDeRegistro>{

	@Override
	public boolean esSatisfechaPor(SolicitudDeRegistro solicitud) {
		return solicitud.estudiante().estaActivo();
	}
	

	// PRA-1 (práctica de estudio de Juan): lógica del estudiante retirada a propósito; reescribirla siguiendo al docente.
}
