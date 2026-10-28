package co.edu.uco.ucotestapi.dominio.cuenta;

import java.util.Objects;

/** El {@code sub} del token de Keycloak; solo sirve para traducir el token al docente (A-21, A-35). */
public record CuentaId(String valor) {

	public static final CuentaId NINGUNA = new CuentaId("-");

	public CuentaId {
		Objects.requireNonNull(valor, "valor");
		if (valor.isBlank()) {
			throw new IllegalArgumentException("la cuenta no puede estar vacía");
		}
	}
}
