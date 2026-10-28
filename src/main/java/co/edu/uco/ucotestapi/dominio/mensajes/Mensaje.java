package co.edu.uco.ucotestapi.dominio.mensajes;

import java.util.Objects;

/** Texto para el usuario con su código; vive en el dominio para que el input adapter no dependa de un puerto (COH-43). */
public record Mensaje(CodigoMensaje codigo, String texto) {

	public Mensaje {
		Objects.requireNonNull(codigo, "codigo");
		Objects.requireNonNull(texto, "texto");
	}
}
