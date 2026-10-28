package co.edu.uco.ucotestapi.inicializador;

import java.util.List;

/**
 * La clase simulada de la demo (A-16, Q-3). Los ids de cuenta son los {@code id} fijos de los usuarios del realm
 * {@code ucotest} ({@code infra/keycloak/realm-ucotest.json}), así el {@code sub} del token encuentra a su
 * docente o estudiante (A-21). Si se cambia uno, se cambia en los dos lugares.
 */
public final class ClaseSimulada {

	public record Persona(String usuario, String cuentaId, String nombre) {
	}

	public static final Persona DOCENTE = new Persona("carlos", "5b6f6c1e-2a0d-4b7e-9c1a-0d6c7b8a9e01",
			"Carlos Restrepo");

	public static final List<Persona> ESTUDIANTES = List.of(
			new Persona("ana", "5b6f6c1e-2a0d-4b7e-9c1a-0d6c7b8a9e11", "Ana Gómez"),
			new Persona("andres", "5b6f6c1e-2a0d-4b7e-9c1a-0d6c7b8a9e12", "Andrés Pérez"),
			new Persona("beatriz", "5b6f6c1e-2a0d-4b7e-9c1a-0d6c7b8a9e13", "Beatriz Lopera"),
			new Persona("david", "5b6f6c1e-2a0d-4b7e-9c1a-0d6c7b8a9e14", "David Ríos"),
			new Persona("elena", "5b6f6c1e-2a0d-4b7e-9c1a-0d6c7b8a9e15", "Elena Mejía"));

	public static final String GRUPO = "Arquitectura de Software 01";
	public static final String EVALUACION = "Parcial 1";

	/** Periodo relativo al día de la siembra (A-52): de hace 30 días a dentro de 90. */
	public static final int DIAS_ANTES = 30;
	public static final int DIAS_DESPUES = 90;

	private ClaseSimulada() {
	}
}
