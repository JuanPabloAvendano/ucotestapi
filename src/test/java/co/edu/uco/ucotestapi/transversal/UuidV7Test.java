package co.edu.uco.ucotestapi.transversal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class UuidV7Test {

	@Test
	void tieneVersion7VarianteRfcYLaMarcaDeTiempo() {
		UUID id = UuidV7.conMilisegundos(0x0192_F3A1_7B2CL);

		assertThat(id.version()).isEqualTo(7);
		assertThat(id.variant()).isEqualTo(2);
		assertThat(id.getMostSignificantBits() >>> 16).isEqualTo(0x0192_F3A1_7B2CL);
	}

	@Test
	void losIdsDeMilisegundosPosterioresQuedanOrdenados() {
		UUID antes = UuidV7.conMilisegundos(1_000);
		UUID despues = UuidV7.conMilisegundos(1_001);

		assertThat(antes.toString()).isLessThan(despues.toString());
	}
}
