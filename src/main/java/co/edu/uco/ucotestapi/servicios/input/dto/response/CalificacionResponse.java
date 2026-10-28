package co.edu.uco.ucotestapi.servicios.input.dto.response;

import java.util.UUID;

/** Destino del {@code Location} del 201 (Q-15). */
public record CalificacionResponse(UUID id, UUID estudianteId, int centesimas) {
}
