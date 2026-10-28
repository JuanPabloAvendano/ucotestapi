package co.edu.uco.ucotestapi.dominio.reglas.impl;

import co.edu.uco.ucotestapi.dominio.reglas.Especificacion;
import co.edu.uco.ucotestapi.dominio.reglas.SolicitudDeRegistro;

/** R-8: el grupo de la ruta existe. */
public final class GrupoExiste implements Especificacion<SolicitudDeRegistro> {

	@Override
	public boolean esSatisfechaPor(SolicitudDeRegistro solicitud) {
		return solicitud.grupo().existe();
	}
}
