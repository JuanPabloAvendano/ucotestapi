package co.edu.uco.ucotestapi.dominio.calificacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import co.edu.uco.ucotestapi.dominio.grupo.Periodo;

class RangoYFechasTest {

	@Test
	void elRangoIncluyeSusExtremosEnCentesimas() {
		RangoCalificacion rango = new RangoCalificacion(0, 500);

		assertThat(rango.contiene(new Calificacion(0))).isTrue();
		assertThat(rango.contiene(new Calificacion(500))).isTrue();
		assertThat(rango.contiene(new Calificacion(435))).isTrue();
		assertThat(rango.contiene(new Calificacion(501))).isFalse();
		assertThat(rango.contiene(new Calificacion(-1))).isFalse();
	}

	@Test
	void unRangoInvertidoNoSePuedeCrear() {
		assertThatThrownBy(() -> new RangoCalificacion(500, 0)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void lasFechasDeRegistroIncluyenAmbosExtremos() {
		FechasDeRegistro fechas = new FechasDeRegistro(LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 31));

		assertThat(fechas.contiene(LocalDate.of(2026, 10, 20))).isTrue();
		assertThat(fechas.contiene(LocalDate.of(2026, 10, 31))).isTrue();
		assertThat(fechas.contiene(LocalDate.of(2026, 11, 1))).isFalse();
		assertThat(fechas.contiene(LocalDate.of(2026, 10, 19))).isFalse();
	}

	@Test
	void unPeriodoConInicioPosteriorAlFinNoSePuedeCrear() {
		assertThatThrownBy(() -> new Periodo(LocalDate.of(2026, 12, 15), LocalDate.of(2026, 8, 1)))
				.isInstanceOf(IllegalArgumentException.class);
	}
}
