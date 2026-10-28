package co.edu.uco.ucotestapi.servicios.output.dto;

import java.util.UUID;

/** Lo que el notification gateway necesita para avisar de una calificación registrada (R-12). */
public record AvisoDeRegistroDTO(UUID calificacionId, UUID evaluacionId, UUID estudianteId, int centesimas) {
}
