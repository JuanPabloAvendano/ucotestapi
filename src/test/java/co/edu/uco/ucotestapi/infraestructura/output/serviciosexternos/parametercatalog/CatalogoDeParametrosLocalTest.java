package co.edu.uco.ucotestapi.infraestructura.output.serviciosexternos.parametercatalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

/** A-52 (desplazamientos resueltos en cada lectura) y A-62 (10) (valores ilegibles fallan al arrancar). */
class CatalogoDeParametrosLocalTest {

	private final AtomicReference<LocalDate> hoy = new AtomicReference<>(LocalDate.of(2026, 10, 25));

	@Test
	void losDesplazamientosSeResuelvenContraElRelojEnCadaLectura() {
		CatalogoDeParametrosLocal catalogo = catalogo("-7d", "+30d", "0", "500");

		assertThat(catalogo.porClave(CatalogoDeParametrosLocal.REGISTRO_INICIO).valor()).isEqualTo("2026-10-18");
		hoy.set(LocalDate.of(2026, 11, 2));
		assertThat(catalogo.porClave(CatalogoDeParametrosLocal.REGISTRO_FIN).valor()).isEqualTo("2026-12-02");
		assertThat(catalogo.porClave(CatalogoDeParametrosLocal.VALOR_MAXIMO).valor()).isEqualTo("500");
	}

	@Test
	void lasFechasIsoSeEntreganTalCual() {
		assertThat(catalogo("2026-10-20", "2026-10-31", "0", "500").porClave(CatalogoDeParametrosLocal.REGISTRO_FIN)
				.valor()).isEqualTo("2026-10-31");
	}

	@Test
	void unValorIlegibleImpideArrancar() {
		assertThatThrownBy(() -> catalogo("ayer", "+30d", "0", "500")).isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> catalogo("-7d", "+30d", "cero", "500")).isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> catalogo("-7d", "+30d", "0", "4.5")).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void unInicioPosteriorAlFinImpideArrancar() {
		assertThatThrownBy(() -> catalogo("2026-11-01", "2026-10-31", "0", "500"))
				.isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> catalogo("-7d", "+30d", "600", "500")).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void unaClaveDesconocidaEsUnErrorDeProgramacion() {
		assertThatThrownBy(() -> catalogo("-7d", "+30d", "0", "500").porClave("otra"))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private CatalogoDeParametrosLocal catalogo(String inicio, String fin, String minimo, String maximo) {
		String contenido = """
				nota.valor.minimo=%s
				nota.valor.maximo=%s
				nota.registro.fecha.inicio=%s
				nota.registro.fecha.fin=%s
				""".formatted(minimo, maximo, inicio, fin);
		return new CatalogoDeParametrosLocal(hoy::get, new ByteArrayResource(contenido.getBytes(StandardCharsets.UTF_8)));
	}
}
