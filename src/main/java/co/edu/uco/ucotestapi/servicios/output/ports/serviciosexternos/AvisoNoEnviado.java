package co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos;

/** Fallo propio de los puertos de avisos; después del commit solo se registra (A-39, A-53). */
public class AvisoNoEnviado extends RuntimeException {

	public AvisoNoEnviado(String mensaje) {
		super(mensaje);
	}

	public AvisoNoEnviado(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}
