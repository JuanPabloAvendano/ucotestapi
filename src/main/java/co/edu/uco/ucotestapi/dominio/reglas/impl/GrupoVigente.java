package co.edu.uco.ucotestapi.dominio.reglas.impl;

import co.edu.uco.ucotestapi.dominio.reglas.Especificacion;
import co.edu.uco.ucotestapi.dominio.reglas.SolicitudDeRegistro;

/** R-3: hoy cae dentro del periodo del grupo (A-42). */
public final class GrupoVigente implements Especificacion<SolicitudDeRegistro> {

	@Override
	public boolean esSatisfechaPor(SolicitudDeRegistro solicitud) {
		return solicitud.grupo().estaVigenteEn(solicitud.hoy());
	}
}
