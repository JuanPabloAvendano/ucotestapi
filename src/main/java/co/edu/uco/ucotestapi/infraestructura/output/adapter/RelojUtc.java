package co.edu.uco.ucotestapi.infraestructura.output.adapter;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.Reloj;

/** El backend trabaja siempre en UTC (A-40). */
@Component
public class RelojUtc implements Reloj {

	private final Clock reloj = Clock.systemUTC();

	@Override
	public LocalDate hoy() {
		return LocalDate.now(reloj);
	}
}
