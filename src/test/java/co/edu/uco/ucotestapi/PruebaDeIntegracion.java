package co.edu.uco.ucotestapi;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Base de las pruebas contra Postgres real (A-47, A-62 (9)): sin {@code @Transactional} (absorbería la transacción
 * de la unidad de trabajo), tablas vaciadas y datos sembrados antes de cada prueba, perfil {@code test} sin
 * inicializador. Todas comparten un solo contexto, y con él un solo contenedor.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTracing
@Import(ConfiguracionDePruebas.class)
public abstract class PruebaDeIntegracion {

	@Autowired
	protected MockMvc mvc;

	@Autowired
	protected JdbcTemplate jdbc;

	@Autowired
	protected RelojDePrueba reloj;

	@Autowired
	protected AvisosDePrueba avisos;

	protected DatosDePrueba datos;

	protected DatosDePrueba.ClaseDePrueba clase;

	@BeforeEach
	void prepararBaseDeDatos() {
		jdbc.execute("truncate table nota_estudiante, estudiante_grupo, nota, grupo, estudiante, docente");
		reloj.reiniciar();
		avisos.reiniciar();
		datos = new DatosDePrueba(jdbc);
		clase = datos.sembrar();
	}

	/** Token simulado de un docente: el rol ya convertido, como lo deja el convertidor de A-48. */
	protected static RequestPostProcessor docente(String cuenta) {
		return jwt().jwt(token -> token.subject(cuenta)).authorities(new SimpleGrantedAuthority("ROLE_DOCENTE"));
	}
}
