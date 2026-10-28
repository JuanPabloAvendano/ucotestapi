package co.edu.uco.ucotestapi.dominio.mensajes;

/**
 * Códigos del message catalog (A-37): uno por regla y los genéricos por estado HTTP. No hay {@code M-NOTA-R10}
 * porque R-10 la cumple el tipo entero (A-38), ni R-11 porque guardar es un paso, no un rechazo (A-28).
 */
public enum CodigoMensaje {

	M_NOTA_R00("M-NOTA-R00"),
	M_NOTA_R01("M-NOTA-R01"),
	M_NOTA_R02("M-NOTA-R02"),
	M_NOTA_R03("M-NOTA-R03"),
	M_NOTA_R04("M-NOTA-R04"),
	M_NOTA_R05("M-NOTA-R05"),
	M_NOTA_R06("M-NOTA-R06"),
	M_NOTA_R07("M-NOTA-R07"),
	M_NOTA_R08("M-NOTA-R08"),
	M_NOTA_R09("M-NOTA-R09"),
	M_NOTA_R12("M-NOTA-R12"),
	M_GEN_400("M-GEN-400"),
	M_GEN_401("M-GEN-401"),
	M_GEN_403("M-GEN-403"),
	M_GEN_404("M-GEN-404"),
	M_GEN_405("M-GEN-405"),
	M_GEN_406("M-GEN-406"),
	M_GEN_415("M-GEN-415"),
	M_GEN_500("M-GEN-500");

	private final String valor;

	CodigoMensaje(String valor) {
		this.valor = valor;
	}

	public String valor() {
		return valor;
	}
}
