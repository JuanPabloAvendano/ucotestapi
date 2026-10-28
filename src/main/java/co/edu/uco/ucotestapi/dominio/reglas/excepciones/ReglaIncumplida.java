package co.edu.uco.ucotestapi.dominio.reglas.excepciones;

import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.mensajes.CodigoMensaje;

/**
 * Rechazo de negocio con el código de la regla que falló (A-44). Es {@code RuntimeException} para que la unidad
 * de trabajo deshaga la transacción sin que el caso de uso conozca Spring (A-31).
 */
public class ReglaIncumplida extends RuntimeException {

	private final CodigoMensaje codigo;

	public ReglaIncumplida(CodigoMensaje codigo) {
		super(Objects.requireNonNull(codigo, "codigo").valor());
		this.codigo = codigo;
	}

	public CodigoMensaje codigo() {
		return codigo;
	}
}
