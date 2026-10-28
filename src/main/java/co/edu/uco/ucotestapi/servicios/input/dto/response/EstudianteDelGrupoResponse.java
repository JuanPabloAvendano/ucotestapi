package co.edu.uco.ucotestapi.servicios.input.dto.response;

import java.util.UUID;

public record EstudianteDelGrupoResponse(UUID id, String nombre, boolean tieneNota) {
}
