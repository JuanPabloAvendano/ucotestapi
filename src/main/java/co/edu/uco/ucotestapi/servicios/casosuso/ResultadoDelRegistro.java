package co.edu.uco.ucotestapi.servicios.casosuso;

import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudiante;
import co.edu.uco.ucotestapi.dominio.mensajes.Mensaje;

/** Lo que devuelve el registro: la calificación guardada y el texto de éxito de R-12 (A-49). */
public record ResultadoDelRegistro(NotaEstudiante nota, Mensaje exito) {

	public ResultadoDelRegistro {
		Objects.requireNonNull(nota, "nota");
		Objects.requireNonNull(exito, "exito");
	}
}
