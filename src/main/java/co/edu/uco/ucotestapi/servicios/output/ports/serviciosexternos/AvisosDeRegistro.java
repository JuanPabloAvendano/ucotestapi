package co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos;

import co.edu.uco.ucotestapi.servicios.output.dto.AvisoDeRegistroDTO;

/** R-12: avisa al docente y al estudiante por el notification gateway (A-9). */
public interface AvisosDeRegistro {

	/** @throws AvisoNoEnviado si no se pudo enviar */
	void informar(AvisoDeRegistroDTO aviso);
}
