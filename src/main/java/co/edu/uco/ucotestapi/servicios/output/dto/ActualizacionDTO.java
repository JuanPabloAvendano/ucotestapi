package co.edu.uco.ucotestapi.servicios.output.dto;

import java.util.UUID;

/** Lo que una pantalla abierta necesita para saber que el estudiante ya tiene nota en la evaluación (A-33). */
public record ActualizacionDTO(UUID evaluacionId, UUID estudianteId) {
}
