package co.edu.uco.ucotestapi.dominio.reglas.impl;

import co.edu.uco.ucotestapi.dominio.reglas.Especificacion;
import co.edu.uco.ucotestapi.dominio.reglas.SolicitudDeRegistro;

/** R-0: el token corresponde a un docente registrado. */
public final class DocenteExiste implements Especificacion<SolicitudDeRegistro> {

	@Override
	public boolean esSatisfechaPor(SolicitudDeRegistro solicitud) {
		return solicitud.docente().existe();
	}
}
