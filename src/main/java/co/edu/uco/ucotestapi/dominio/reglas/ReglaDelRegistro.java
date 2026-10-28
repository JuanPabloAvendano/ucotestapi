package co.edu.uco.ucotestapi.dominio.reglas;

import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.mensajes.CodigoMensaje;
import co.edu.uco.ucotestapi.dominio.reglas.excepciones.ReglaIncumplida;

/** Une una especificación con el código que responde cuando no se cumple (A-29, A-37). */
public record ReglaDelRegistro<T>(CodigoMensaje codigo, Especificacion<T> especificacion) {

	public ReglaDelRegistro {
		Objects.requireNonNull(codigo, "codigo");
		Objects.requireNonNull(especificacion, "especificacion");
	}

	public void exigir(T candidato) {
		if (!especificacion.esSatisfechaPor(candidato)) {
			throw new ReglaIncumplida(codigo);
		}
	}
}
