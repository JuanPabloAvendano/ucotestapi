package co.edu.uco.ucotestapi.dominio.reglas;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EspecificacionTest {

	private final Especificacion<Integer> positivo = numero -> numero > 0;
	private final Especificacion<Integer> par = numero -> numero % 2 == 0;

	@Test
	void seCombinanConYONo() {
		assertThat(positivo.y(par).esSatisfechaPor(4)).isTrue();
		assertThat(positivo.y(par).esSatisfechaPor(3)).isFalse();
		assertThat(positivo.o(par).esSatisfechaPor(-2)).isTrue();
		assertThat(positivo.o(par).esSatisfechaPor(-3)).isFalse();
		assertThat(positivo.no().esSatisfechaPor(-1)).isTrue();
	}
}
