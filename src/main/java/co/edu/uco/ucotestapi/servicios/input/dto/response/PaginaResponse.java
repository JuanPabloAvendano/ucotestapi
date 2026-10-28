package co.edu.uco.ucotestapi.servicios.input.dto.response;

import java.util.List;

/** Forma paginada de Q-13. */
public record PaginaResponse<T>(List<T> contenido, int pagina, int tamano, long total) {
}
