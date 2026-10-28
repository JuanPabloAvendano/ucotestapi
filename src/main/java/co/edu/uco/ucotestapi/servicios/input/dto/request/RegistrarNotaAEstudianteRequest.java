package co.edu.uco.ucotestapi.servicios.input.dto.request;

import java.util.UUID;

/** Lo que manda el cliente más los ids de la ruta (A-35); {@code centesimas}: 435 = 4.35 (A-19). */
public record RegistrarNotaAEstudianteRequest(UUID grupoId, UUID notaId, UUID estudianteId, int centesimas) {
	
	
}
