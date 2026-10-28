package co.edu.uco.ucotestapi.servicios.input.ports;

import java.util.UUID;

import co.edu.uco.ucotestapi.servicios.input.dto.response.CalificacionResponse;

/** {@code GET /api/v1/grupos/{g}/notas/{n}/calificaciones/{id}} (Q-15). */
public interface ConsultarCalificacionInputPort {

	CalificacionResponse calificacion(String cuentaDocente, UUID grupoId, UUID notaId, UUID id);
}
