package co.edu.uco.ucotestapi.infraestructura.output.serviciosexternos.messagecatalog;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import co.edu.uco.ucotestapi.dominio.mensajes.CodigoMensaje;
import co.edu.uco.ucotestapi.servicios.output.dto.MensajeDTO;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.CatalogoDeMensajes;

/**
 * Message catalog de S1 sobre {@code mensajes.properties} (A-37); en S3 lo reemplaza el real con Redis. Se carga al
 * arrancar y exige un texto por cada código, para no descubrir un hueco con un usuario delante (A-62 (10)).
 */
@Component
public class CatalogoDeMensajesLocal implements CatalogoDeMensajes {

	private final Map<String, String> textos;

	public CatalogoDeMensajesLocal() {
		Properties propiedades = new Properties();
		try (Reader lector = new InputStreamReader(new ClassPathResource("mensajes.properties").getInputStream(),
				StandardCharsets.UTF_8)) {
			propiedades.load(lector);
		}
		catch (IOException fallo) {
			throw new IllegalStateException("No se pudo leer mensajes.properties", fallo);
		}
		this.textos = propiedades.stringPropertyNames().stream()
				.collect(Collectors.toUnmodifiableMap(codigo -> codigo, propiedades::getProperty));
		List<String> faltantes = Arrays.stream(CodigoMensaje.values()).map(CodigoMensaje::valor)
				.filter(codigo -> textos.getOrDefault(codigo, "").isBlank()).toList();
		if (!faltantes.isEmpty()) {
			throw new IllegalStateException("Faltan textos en mensajes.properties: " + faltantes);
		}
	}

	@Override
	public MensajeDTO porCodigo(String codigo) {
		String texto = textos.get(codigo);
		if (texto == null) {
			throw new IllegalArgumentException("Mensaje desconocido: " + codigo);
		}
		return new MensajeDTO(codigo, texto);
	}
}
