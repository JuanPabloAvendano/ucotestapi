package co.edu.uco.ucotestapi.servicios.input.ports;

import co.edu.uco.ucotestapi.servicios.input.dto.response.GrupoResponse;
import co.edu.uco.ucotestapi.servicios.input.dto.response.PaginaResponse;

/** {@code GET /api/v1/docentes/yo/grupos} (A-24). */
public interface ConsultarGruposDelDocenteInputPort {

	PaginaResponse<GrupoResponse> gruposDelDocente(String cuentaDocente, int pagina, int tamano);
}
