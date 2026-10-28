package co.edu.uco.ucotestapi.servicios.input.ports;

import java.util.UUID;

import co.edu.uco.ucotestapi.servicios.input.dto.response.EvaluacionResponse;
import co.edu.uco.ucotestapi.servicios.input.dto.response.PaginaResponse;

/** {@code GET /api/v1/grupos/{grupoId}/notas} (A-24). */
public interface ConsultarEvaluacionesDelGrupoInputPort {

	PaginaResponse<EvaluacionResponse> evaluacionesDelGrupo(String cuentaDocente, UUID grupoId, int pagina,
			int tamano);
}
