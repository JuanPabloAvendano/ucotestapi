package co.edu.uco.ucotestapi.infraestructura.output.serviciosexternos.notificationgateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import co.edu.uco.ucotestapi.servicios.output.dto.AvisoDeRegistroDTO;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.AvisosDeRegistro;

/** Implementación de S1 de R-12: solo registra; RabbitMQ, websocket y webhook llegan en S4 (A-62 (4)). */
@Component
public class AvisosDeRegistroEnLog implements AvisosDeRegistro {

	private static final Logger LOG = LoggerFactory.getLogger(AvisosDeRegistroEnLog.class);

	@Override
	public void informar(AvisoDeRegistroDTO aviso) {
		LOG.info("Aviso de registro (S1, solo log): calificación {} evaluación {} estudiante {} centésimas {}",
				aviso.calificacionId(), aviso.evaluacionId(), aviso.estudianteId(), aviso.centesimas());
	}
}
