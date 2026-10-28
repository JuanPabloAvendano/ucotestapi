package co.edu.uco.ucotestapi.dominio.reglas.impl;

import co.edu.uco.ucotestapi.dominio.reglas.Especificacion;
import co.edu.uco.ucotestapi.dominio.reglas.SolicitudDeRegistro;

/** R-5: hoy está dentro de las fechas de registro del parameter catalog (A-8, A-40). */
public final class DentroDeFechasDeRegistro implements Especificacion<SolicitudDeRegistro> {

	@Override
	public boolean esSatisfechaPor(SolicitudDeRegistro solicitud) {
		return solicitud.fechas().contiene(solicitud.hoy());
	}
}
