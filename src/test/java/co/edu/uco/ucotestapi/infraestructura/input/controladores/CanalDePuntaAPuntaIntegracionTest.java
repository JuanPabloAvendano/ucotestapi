package co.edu.uco.ucotestapi.infraestructura.input.controladores;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import co.edu.uco.ucotestapi.PruebaDeIntegracion;
import co.edu.uco.ucotestapi.TokensDePrueba;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * El canal S1 de punta a punta por HTTP real: token firmado con roles de Keycloak → cadena de seguridad →
 * controlador → caso de uso → Postgres → 201; el segundo intento recibe 422 {@code M-NOTA-R06} (§8 «Hecho cuando»).
 */
class CanalDePuntaAPuntaIntegracionTest extends PruebaDeIntegracion {

	@LocalServerPort
	int puerto;

	@Autowired
	JsonMapper json;

	private final HttpClient http = HttpClient.newHttpClient();

	@Test
	void registraPorHttpRealYElSegundoIntentoEsR06() throws Exception {
		String token = TokensDePrueba.de(clase.cuentaCarlos(), "DOCENTE");
		String cuerpo = """
				{"estudianteId":"%s","centesimas":435}""".formatted(clase.ana());

		HttpResponse<String> primera = enviar(token, cuerpo);
		assertThat(primera.statusCode()).isEqualTo(201);
		JsonNode creada = json.readTree(primera.body());
		assertThat(creada.get("centesimas").asInt()).isEqualTo(435);
		assertThat(creada.get("mensaje").asString()).isEqualTo("La calificación se registró con éxito.");
		assertThat(primera.headers().firstValue("Location")).hasValue(
				"/api/v1/grupos/%s/notas/%s/calificaciones/%s".formatted(clase.arquitectura01(), clase.parcial1(),
						creada.get("id").asString()));

		HttpResponse<String> segunda = enviar(token, cuerpo);
		assertThat(segunda.statusCode()).isEqualTo(422);
		assertThat(segunda.headers().firstValue("Content-Type")).hasValue("application/problem+json");
		assertThat(json.readTree(segunda.body()).get("codigo").asString()).isEqualTo("M-NOTA-R06");
		assertThat(datos.calificacionesDe(clase.parcial1(), clase.ana())).isEqualTo(1);
	}

	@Test
	void unTokenSinRolDocenteRecibe403YUnoSinFirmaValida401() throws Exception {
		String cuerpo = """
				{"estudianteId":"%s","centesimas":435}""".formatted(clase.ana());

		assertThat(enviar(TokensDePrueba.de("sub-ana", "ESTUDIANTE"), cuerpo).statusCode()).isEqualTo(403);
		assertThat(enviar("no-es-un-jwt", cuerpo).statusCode()).isEqualTo(401);
	}

	private HttpResponse<String> enviar(String token, String cuerpo) throws Exception {
		HttpRequest peticion = HttpRequest.newBuilder(URI.create("http://localhost:%d/api/v1/grupos/%s/notas/%s/calificaciones"
				.formatted(puerto, clase.arquitectura01(), clase.parcial1())))
				.header("Authorization", "Bearer " + token)
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(cuerpo))
				.build();
		return http.send(peticion, HttpResponse.BodyHandlers.ofString());
	}
}
