package co.edu.uco.ucotestapi.dominio.reglas;

/**
 * Regla de negocio encerrada en un objeto que se puede combinar (A-7; Evans, DDD cap. 9 «Specification»).
 */
public interface Especificacion<T> {

	boolean esSatisfechaPor(T candidato);

	default Especificacion<T> y(Especificacion<T> otra) {
		return candidato -> esSatisfechaPor(candidato) && otra.esSatisfechaPor(candidato);
	}

	default Especificacion<T> o(Especificacion<T> otra) {
		return candidato -> esSatisfechaPor(candidato) || otra.esSatisfechaPor(candidato);
	}

	default Especificacion<T> no() {
		return candidato -> !esSatisfechaPor(candidato);
	}
}
