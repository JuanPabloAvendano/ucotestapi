package co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos;

import java.time.LocalDate;

/** El día de hoy en UTC (A-40); sin DTO porque {@code LocalDate} ya es un valor (A-62 (4)). */
public interface Reloj {

	LocalDate hoy();
}
