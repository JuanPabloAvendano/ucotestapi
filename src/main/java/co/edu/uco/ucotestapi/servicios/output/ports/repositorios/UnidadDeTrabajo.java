package co.edu.uco.ucotestapi.servicios.output.ports.repositorios;

import java.util.function.Supplier;

/**
 * Ejecuta un bloque dentro de una transacción de base de datos sin que el caso de uso importe Spring (A-31;
 * Fowler, PoEAA «Unit of Work»). Una {@code RuntimeException} del bloque deshace todo y sale tal cual.
 */
public interface UnidadDeTrabajo {

	<T> T ejecutar(Supplier<T> bloque);
}
