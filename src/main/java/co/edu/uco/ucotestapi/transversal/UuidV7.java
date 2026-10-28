package co.edu.uco.ucotestapi.transversal;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * UUID versión 7 (RFC 9562 §5.7): 48 bits de milisegundos y el resto aleatorio, para que los ids nuevos queden
 * ordenados por tiempo y el índice de la clave primaria no se fragmente (A-4).
 */
public final class UuidV7 {

	private static final SecureRandom ALEATORIO = new SecureRandom();

	private UuidV7() {
	}

	public static UUID nuevo() {
		return conMilisegundos(System.currentTimeMillis());
	}

	static UUID conMilisegundos(long milisegundos) {
		byte[] aleatorios = new byte[10];
		ALEATORIO.nextBytes(aleatorios);
		long alto = (milisegundos & 0xFFFF_FFFF_FFFFL) << 16
				| 0x7000L
				| ((aleatorios[0] & 0x0FL) << 8) | (aleatorios[1] & 0xFFL);
		long bajo = 0x8000_0000_0000_0000L | ((aleatorios[2] & 0x3FL) << 56);
		for (int i = 3; i < 10; i++) {
			bajo |= (aleatorios[i] & 0xFFL) << (8 * (9 - i));
		}
		return new UUID(alto, bajo);
	}
}
