package co.edu.uco.ucotestapi;

import java.time.LocalDate;

import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.Reloj;

/** Stub del reloj (A-50): «hoy» es 2026-10-25 salvo que la prueba lo cambie; se reinicia antes de cada prueba. */
public class RelojDePrueba implements Reloj {

	public static final LocalDate HOY = LocalDate.of(2026, 10, 25);

	private volatile LocalDate hoy = HOY;

	@Override
	public LocalDate hoy() {
		return hoy;
	}

	public void fijar(LocalDate dia) {
		hoy = dia;
	}

	public void reiniciar() {
		hoy = HOY;
	}
}
