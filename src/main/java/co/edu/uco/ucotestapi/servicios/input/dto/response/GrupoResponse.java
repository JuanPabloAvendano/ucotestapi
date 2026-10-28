package co.edu.uco.ucotestapi.servicios.input.dto.response;

import java.time.LocalDate;
import java.util.UUID;

/** Grupo del docente (Q-13); {@code inicio} y {@code fin} son días UTC (A-40, COH-31). */
public record GrupoResponse(UUID id, String nombre, LocalDate inicio, LocalDate fin) {
}
