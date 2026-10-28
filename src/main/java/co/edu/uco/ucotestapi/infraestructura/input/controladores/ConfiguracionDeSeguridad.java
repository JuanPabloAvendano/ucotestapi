package co.edu.uco.ucotestapi.infraestructura.input.controladores;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import co.edu.uco.ucotestapi.dominio.mensajes.CodigoMensaje;

/**
 * El backend como resource server de Keycloak (A-17, A-27). La autorización va en la cadena de filtros y no en
 * {@code @PreAuthorize}, para que 401 y 403 no los trague el manejador general del advice (A-62 (2), COH-51).
 */
@Configuration
public class ConfiguracionDeSeguridad {

	private static final Logger LOG = LoggerFactory.getLogger(ConfiguracionDeSeguridad.class);

	static final String ROL_DOCENTE = "DOCENTE";

	@Bean
	SecurityFilterChain cadenaDeSeguridad(HttpSecurity http, AuthenticationEntryPoint sinToken,
			AccessDeniedHandler sinPermiso) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(peticiones -> peticiones
						.requestMatchers("/actuator/health", "/actuator/health/**", "/error").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/grupos/*/notas/*/calificaciones").hasRole(ROL_DOCENTE)
						.requestMatchers("/api/**").hasRole(ROL_DOCENTE)
						.anyRequest().denyAll())
				.oauth2ResourceServer(servidor -> servidor
						.jwt(jwt -> jwt.jwtAuthenticationConverter(convertidorDeRoles()))
						.authenticationEntryPoint(sinToken)
						.accessDeniedHandler(sinPermiso))
				.exceptionHandling(errores -> errores.authenticationEntryPoint(sinToken).accessDeniedHandler(sinPermiso));
		return http.build();
	}

	@Bean
	AuthenticationEntryPoint sinToken(FabricaDeProblemas problemas) {
		return (peticion, respuesta, causa) -> {
			LOG.info("Petición sin token válido a {}: {}", peticion.getRequestURI(), causa.getMessage());
			respuesta.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
			problemas.escribir(respuesta, problemas.crear(HttpStatus.UNAUTHORIZED, CodigoMensaje.M_GEN_401));
		};
	}

	@Bean
	AccessDeniedHandler sinPermiso(FabricaDeProblemas problemas) {
		return (peticion, respuesta, causa) -> {
			LOG.info("Petición sin rol suficiente a {}", peticion.getRequestURI());
			problemas.escribir(respuesta, problemas.crear(HttpStatus.FORBIDDEN, CodigoMensaje.M_GEN_403));
		};
	}

	/** Keycloak pone los roles del realm en {@code realm_access.roles}; Spring solo lee {@code scope} (A-48, COH-27). */
	static JwtAuthenticationConverter convertidorDeRoles() {
		JwtAuthenticationConverter convertidor = new JwtAuthenticationConverter();
		convertidor.setJwtGrantedAuthoritiesConverter(rolesDelRealm());
		return convertidor;
	}

	static Converter<Jwt, Collection<GrantedAuthority>> rolesDelRealm() {
		return token -> {
			Map<String, Object> accesoAlRealm = token.getClaimAsMap("realm_access");
			Object roles = accesoAlRealm == null ? List.of() : accesoAlRealm.getOrDefault("roles", List.of());
			if (!(roles instanceof Collection<?> lista)) {
				return List.of();
			}
			return lista.stream().map(rol -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + rol)).toList();
		};
	}
}
