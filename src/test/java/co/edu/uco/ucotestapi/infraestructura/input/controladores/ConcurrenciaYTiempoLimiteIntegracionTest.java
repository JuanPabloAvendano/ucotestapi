package co.edu.uco.ucotestapi.infraestructura.input.controladores;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.sql.Connection;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import com.jayway.jsonpath.JsonPath;

import co.edu.uco.ucotestapi.PruebaDeIntegracion;

/**
 * C-18 y C-19 (A-22, A-32, Q-14): otra conexión JDBC retiene la matrícula de Ana con {@code SELECT ... FOR UPDATE}
 * mientras llegan las peticiones, para que la espera por el bloqueo sea segura y no cuestión de suerte.
 */
class ConcurrenciaYTiempoLimiteIntegracionTest extends PruebaDeIntegracion {

	@Autowired
	DataSource dataSource;

	private final ExecutorService hilos = Executors.newFixedThreadPool(2);

	@Test
	void c18DosPeticionesALaVezDanUn201YUn422R06YUnaSolaCalificacion() throws Exception {
		List<CompletableFuture<MockHttpServletResponse>> respuestas;
		try (Connection retenedora = retenerMatriculaDeAna()) {
			respuestas = List.of(registrarEnOtroHilo(), registrarEnOtroHilo());
			esperarPeticionesBloqueadas(2);
			retenedora.commit();
		}

		List<Integer> estados = respuestas.stream().map(CompletableFuture::join).map(MockHttpServletResponse::getStatus)
				.sorted().toList();
		assertThat(estados).containsExactly(201, 422);
		MockHttpServletResponse rechazada = respuestas.stream().map(CompletableFuture::join)
				.filter(r -> r.getStatus() == 422).findFirst().orElseThrow();
		assertThat((String) JsonPath.read(rechazada.getContentAsString(), "$.codigo")).isEqualTo("M-NOTA-R06");
		assertThat(datos.calificacionesDe(clase.parcial1(), clase.ana())).isEqualTo(1);
	}

	@Test
	void c19SiLaTransaccionSuperaElTiempoLimiteHaceRollbackYResponde500() throws Exception {
		MockHttpServletResponse respuesta;
		Instant inicio = Instant.now();
		try (Connection retenedora = retenerMatriculaDeAna()) {
			respuesta = registrarEnOtroHilo().get(10, TimeUnit.SECONDS);
			retenedora.rollback();
		}

		assertThat(respuesta.getStatus()).isEqualTo(500);
		assertThat((String) JsonPath.read(respuesta.getContentAsString(), "$.codigo")).isEqualTo("M-GEN-500");
		assertThat(Duration.between(inicio, Instant.now())).as("la acota el tiempo límite de 1 s del perfil test (Q-9)")
				.isLessThan(Duration.ofSeconds(5));
		assertThat(datos.calificacionesDe(clase.parcial1(), clase.ana())).isZero();
	}

	private Connection retenerMatriculaDeAna() throws Exception {
		Connection conexion = dataSource.getConnection();
		conexion.setAutoCommit(false);
		try (Statement sentencia = conexion.createStatement()) {
			sentencia.execute("select id from estudiante_grupo where id = '%s' for update".formatted(clase.matriculaDeAna()));
		}
		return conexion;
	}

	private CompletableFuture<MockHttpServletResponse> registrarEnOtroHilo() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return mvc.perform(post("/api/v1/grupos/{g}/notas/{n}/calificaciones", clase.arquitectura01(), clase.parcial1())
						.with(docente(clase.cuentaCarlos()))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"estudianteId":"%s","centesimas":435}""".formatted(clase.ana())))
						.andReturn().getResponse();
			}
			catch (Exception fallo) {
				throw new IllegalStateException(fallo);
			}
		}, hilos);
	}

	/** Espera a que Postgres muestre las peticiones detenidas en el bloqueo, antes de que venza su segundo. */
	private void esperarPeticionesBloqueadas(int cuantas) throws InterruptedException {
		Instant limite = Instant.now().plusMillis(900);
		while (Instant.now().isBefore(limite)) {
			Integer esperando = jdbc.queryForObject(
					"select count(*) from pg_stat_activity where wait_event_type = 'Lock' and datname = current_database()",
					Integer.class);
			if (esperando != null && esperando >= cuantas) {
				return;
			}
			Thread.sleep(20);
		}
		throw new AssertionError("Las peticiones no llegaron a esperar el bloqueo a tiempo");
	}
}
