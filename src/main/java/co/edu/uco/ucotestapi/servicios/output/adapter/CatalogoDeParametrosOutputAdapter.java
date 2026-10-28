package co.edu.uco.ucotestapi.servicios.output.adapter;

import java.time.LocalDate;
import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.calificacion.FechasDeRegistro;
import co.edu.uco.ucotestapi.dominio.calificacion.RangoCalificacion;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.CatalogoDeParametros;

/** Traduce los parámetros del catálogo a objetos de valor del dominio (A-58). Las claves son las de §3. */
public class CatalogoDeParametrosOutputAdapter {

	static final String VALOR_MINIMO = "nota.valor.minimo";
	static final String VALOR_MAXIMO = "nota.valor.maximo";
	static final String REGISTRO_INICIO = "nota.registro.fecha.inicio";
	static final String REGISTRO_FIN = "nota.registro.fecha.fin";

	private final CatalogoDeParametros catalogo;

	public CatalogoDeParametrosOutputAdapter(CatalogoDeParametros catalogo) {
		this.catalogo = Objects.requireNonNull(catalogo, "catalogo");
	}

	/** En centésimas, la misma unidad de todo el backend (A-36). */
	public RangoCalificacion rango() {
		return new RangoCalificacion(entero(VALOR_MINIMO), entero(VALOR_MAXIMO));
	}

	public FechasDeRegistro fechasDeRegistro() {
		return new FechasDeRegistro(fecha(REGISTRO_INICIO), fecha(REGISTRO_FIN));
	}

	private int entero(String clave) {
		return Integer.parseInt(catalogo.porClave(clave).valor());
	}

	private LocalDate fecha(String clave) {
		return LocalDate.parse(catalogo.porClave(clave).valor());
	}
}
