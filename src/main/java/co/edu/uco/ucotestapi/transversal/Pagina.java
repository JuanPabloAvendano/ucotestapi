package co.edu.uco.ucotestapi.transversal;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/** Una página de resultados con la forma de Q-13: {@code {contenido, pagina, tamano, total}}. */
public record Pagina<T>(List<T> contenido, int pagina, int tamano, long total) {

	public Pagina {
		contenido = List.copyOf(Objects.requireNonNull(contenido, "contenido"));
	}

	public <R> Pagina<R> mapear(Function<T, R> conversion) {
		return new Pagina<>(contenido.stream().map(conversion).toList(), pagina, tamano, total);
	}
}
