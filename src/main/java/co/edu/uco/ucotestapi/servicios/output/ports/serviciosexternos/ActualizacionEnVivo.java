package co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos;

import co.edu.uco.ucotestapi.servicios.output.dto.ActualizacionDTO;

/** Refresca las pantallas abiertas del docente después del commit (A-33). */
public interface ActualizacionEnVivo {

	/** @throws AvisoNoEnviado si no se pudo enviar */
	void refrescar(ActualizacionDTO actualizacion);
}
