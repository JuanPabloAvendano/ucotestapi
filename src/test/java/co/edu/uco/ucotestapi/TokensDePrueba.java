package co.edu.uco.ucotestapi;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/** Tokens firmados con la forma de los de Keycloak: {@code iss}, {@code sub} y {@code realm_access.roles} (A-48). */
public final class TokensDePrueba {

	private static final NimbusJwtEncoder FIRMADOR = NimbusJwtEncoder
			.withKeyPair((RSAPublicKey) ConfiguracionDePruebas.CLAVES.getPublic(),
					(RSAPrivateKey) ConfiguracionDePruebas.CLAVES.getPrivate())
			.build();

	private TokensDePrueba() {
	}

	public static String de(String cuenta, String... roles) {
		Instant ahora = Instant.now();
		JwtClaimsSet datos = JwtClaimsSet.builder()
				.issuer(ConfiguracionDePruebas.ISSUER)
				.subject(cuenta)
				.issuedAt(ahora)
				.expiresAt(ahora.plusSeconds(300))
				.claim("realm_access", Map.of("roles", List.of(roles)))
				.build();
		return FIRMADOR.encode(JwtEncoderParameters.from(JwsHeader.with(SignatureAlgorithm.RS256).build(), datos))
				.getTokenValue();
	}
}
