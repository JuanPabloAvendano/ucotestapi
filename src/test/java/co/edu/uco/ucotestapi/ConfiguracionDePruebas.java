package co.edu.uco.ucotestapi;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Única configuración de pruebas (A-62 (9)). Postgres es real (A-47); los dobles son solo de puertos que no son
 * persistencia (A-50): el reloj, los avisos y la clave con que Keycloak firmaría los tokens.
 */
@TestConfiguration(proxyBeanMethods = false)
public class ConfiguracionDePruebas {

	static final String ISSUER = "http://localhost:8080/auth/realms/ucotest";

	/** Clave de esta ejecución: los tokens de {@link TokensDePrueba} se firman con ella y el backend los verifica. */
	static final KeyPair CLAVES = generarClaves();

	@Bean
	@ServiceConnection
	PostgreSQLContainer postgres() {
		return new PostgreSQLContainer(System.getProperty("postgres.imagen", "postgres:18.6-alpine"));
	}

	@Bean
	@Primary
	RelojDePrueba relojDePrueba() {
		return new RelojDePrueba();
	}

	@Bean
	@Primary
	AvisosDePrueba avisosDePrueba() {
		return new AvisosDePrueba();
	}

	/** Reemplaza la descarga de claves de Keycloak; la firma, el emisor y la vigencia se validan igual (A-27). */
	@Bean
	JwtDecoder jwtDecoder() {
		NimbusJwtDecoder decodificador = NimbusJwtDecoder.withPublicKey((RSAPublicKey) CLAVES.getPublic()).build();
		decodificador.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(ISSUER)));
		return decodificador;
	}

	private static KeyPair generarClaves() {
		try {
			KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
			generador.initialize(2048);
			return generador.generateKeyPair();
		}
		catch (NoSuchAlgorithmException fallo) {
			throw new IllegalStateException(fallo);
		}
	}
}
