package co.edu.uco.ucotestapi.infraestructura.output.serviciosexternos.parametercatalog;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import co.edu.uco.ucotestapi.servicios.output.dto.ParametroDTO;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.CatalogoDeParametros;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.Reloj;

/**
 * Parameter catalog de S1 sobre {@code parametros.properties} (A-40); en S3 lo reemplaza el catálogo real con Redis
 * sin tocar el caso de uso (A-37). Se carga y valida al arrancar: un valor ilegible o un inicio posterior al fin
 * impide arrancar (A-62 (10)). Los desplazamientos ({@code -7d}, {@code +30d}) se resuelven contra el reloj en
 * cada lectura, para que la demo registre cualquier día (A-52).
 */
@Component
public class CatalogoDeParametrosLocal implements CatalogoDeParametros {

	static final String VALOR_MINIMO = "nota.valor.minimo";
	static final String VALOR_MAXIMO = "nota.valor.maximo";
	static final String REGISTRO_INICIO = "nota.registro.fecha.inicio";
	static final String REGISTRO_FIN = "nota.registro.fecha.fin";

	private static final Pattern DESPLAZAMIENTO = Pattern.compile("([+-]\\d+)d");

	private final Map<String, String> valores;
	private final Reloj reloj;

	@Autowired
	public CatalogoDeParametrosLocal(Reloj reloj) {
		this(reloj, new ClassPathResource("parametros.properties"));
	}

	CatalogoDeParametrosLocal(Reloj reloj, Resource archivo) {
		this.reloj = Objects.requireNonNull(reloj, "reloj");
		this.valores = cargar(archivo);
		validar();
	}

	@Override
	public ParametroDTO porClave(String clave) {
		String valor = valores.get(clave);
		if (valor == null) {
			throw new IllegalArgumentException("Parámetro desconocido: " + clave);
		}
		return new ParametroDTO(clave, esFecha(clave) ? resolverFecha(clave, valor).toString() : valor.trim());
	}

	private void validar() {
		int minimo = entero(VALOR_MINIMO);
		int maximo = entero(VALOR_MAXIMO);
		if (minimo > maximo) {
			throw new IllegalStateException(VALOR_MINIMO + " no puede ser mayor que " + VALOR_MAXIMO);
		}
		LocalDate inicio = resolverFecha(REGISTRO_INICIO, obligatorio(REGISTRO_INICIO));
		LocalDate fin = resolverFecha(REGISTRO_FIN, obligatorio(REGISTRO_FIN));
		if (inicio.isAfter(fin)) {
			throw new IllegalStateException(REGISTRO_INICIO + " no puede ser posterior a " + REGISTRO_FIN);
		}
	}

	private int entero(String clave) {
		try {
			return Integer.parseInt(obligatorio(clave).trim());
		}
		catch (NumberFormatException fallo) {
			throw new IllegalStateException("El parámetro " + clave + " debe ser un entero en centésimas", fallo);
		}
	}

	private LocalDate resolverFecha(String clave, String valor) {
		Matcher desplazamiento = DESPLAZAMIENTO.matcher(valor.trim());
		if (desplazamiento.matches()) {
			return reloj.hoy().plusDays(Long.parseLong(desplazamiento.group(1)));
		}
		try {
			return LocalDate.parse(valor.trim());
		}
		catch (DateTimeParseException fallo) {
			throw new IllegalStateException(
					"El parámetro " + clave + " debe ser una fecha ISO o un desplazamiento como -7d", fallo);
		}
	}

	private String obligatorio(String clave) {
		String valor = valores.get(clave);
		if (valor == null || valor.isBlank()) {
			throw new IllegalStateException("Falta el parámetro " + clave);
		}
		return valor;
	}

	private static boolean esFecha(String clave) {
		return REGISTRO_INICIO.equals(clave) || REGISTRO_FIN.equals(clave);
	}

	private static Map<String, String> cargar(Resource archivo) {
		Properties propiedades = new Properties();
		try (Reader lector = new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8)) {
			propiedades.load(lector);
		}
		catch (IOException fallo) {
			throw new IllegalStateException("No se pudo leer " + archivo.getDescription(), fallo);
		}
		return propiedades.stringPropertyNames().stream()
				.collect(Collectors.toUnmodifiableMap(clave -> clave, propiedades::getProperty));
	}
}
