package co.edu.uco.ucotestapi.servicios.output.adapter;

import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudiante;
import co.edu.uco.ucotestapi.servicios.output.dto.AvisoDeRegistroDTO;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.AvisosDeRegistro;

/** R-12: traduce la calificación registrada al DTO del notification gateway (A-58). */
public class AvisosDeRegistroOutputAdapter {

	private final AvisosDeRegistro avisos;

	public AvisosDeRegistroOutputAdapter(AvisosDeRegistro avisos) {
		this.avisos = Objects.requireNonNull(avisos, "avisos");
	}

	public void informar(NotaEstudiante calificacion) {
		avisos.informar(new AvisoDeRegistroDTO(calificacion.id().valor(), calificacion.nota().valor(),
				calificacion.estudiante().valor(), calificacion.calificacion().centesimas()));
	}
}
