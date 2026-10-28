package co.edu.uco.ucotestapi.servicios.output.adapter;

import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudiante;
import co.edu.uco.ucotestapi.servicios.output.dto.ActualizacionDTO;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.ActualizacionEnVivo;

/** Traduce la calificación registrada al DTO de la actualización en vivo (A-33, A-58). */
public class ActualizacionEnVivoOutputAdapter {

	private final ActualizacionEnVivo actualizacion;

	public ActualizacionEnVivoOutputAdapter(ActualizacionEnVivo actualizacion) {
		this.actualizacion = Objects.requireNonNull(actualizacion, "actualizacion");
	}

	public void refrescar(NotaEstudiante calificacion) {
		actualizacion.refrescar(new ActualizacionDTO(calificacion.nota().valor(), calificacion.estudiante().valor()));
	}
}
