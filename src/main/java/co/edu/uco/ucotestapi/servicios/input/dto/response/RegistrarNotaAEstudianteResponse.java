package co.edu.uco.ucotestapi.servicios.input.dto.response;

import java.util.UUID;

/** Cuerpo del 201 (A-18, A-37): {@code mensaje} es el texto de {@code M-NOTA-R12}. */
public record RegistrarNotaAEstudianteResponse(UUID id, int centesimas, String mensaje) {
}
