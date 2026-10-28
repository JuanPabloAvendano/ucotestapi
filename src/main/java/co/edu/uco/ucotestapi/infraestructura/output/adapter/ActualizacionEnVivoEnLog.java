package co.edu.uco.ucotestapi.infraestructura.output.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import co.edu.uco.ucotestapi.servicios.output.dto.ActualizacionDTO;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.ActualizacionEnVivo;

/** Implementación de S1: solo registra; el websocket real llega en S4 (A-33, A-62 (4)). */
@Component
public class ActualizacionEnVivoEnLog implements ActualizacionEnVivo {

	private static final Logger LOG = LoggerFactory.getLogger(ActualizacionEnVivoEnLog.class);

	@Override
	public void refrescar(ActualizacionDTO actualizacion) {
		LOG.info("Actualización en vivo (S1, solo log): evaluación {} estudiante {}", actualizacion.evaluacionId(),
				actualizacion.estudianteId());
	}
}
