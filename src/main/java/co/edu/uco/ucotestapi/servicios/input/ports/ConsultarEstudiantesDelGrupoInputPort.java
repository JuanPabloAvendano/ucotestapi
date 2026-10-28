package co.edu.uco.ucotestapi.servicios.input.ports;

import java.util.UUID;

import co.edu.uco.ucotestapi.servicios.input.dto.response.EstudianteDelGrupoResponse;
import co.edu.uco.ucotestapi.servicios.input.dto.response.PaginaResponse;

/** {@code GET /api/v1/grupos/{grupoId}/estudiantes?notaId=} (A-24). */
public interface ConsultarEstudiantesDelGrupoInputPort {

	PaginaResponse<EstudianteDelGrupoResponse> estudiantesDelGrupo(String cuentaDocente, UUID grupoId, UUID notaId,
			int pagina, int tamano);
}
