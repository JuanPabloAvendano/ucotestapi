package co.edu.uco.ucotestapi.dominio.reglas.impl;

import co.edu.uco.ucotestapi.dominio.reglas.Especificacion;
import co.edu.uco.ucotestapi.dominio.reglas.SolicitudDeRegistro;

/** R-4: la evaluación existe y pertenece al grupo de la ruta (A-29, COH-3). */
public final class EvaluacionDelGrupo implements Especificacion<SolicitudDeRegistro> {

	@Override
	public boolean esSatisfechaPor(SolicitudDeRegistro solicitud) {
		return solicitud.evaluacion().perteneceA(solicitud.grupo().id());
	}
}
