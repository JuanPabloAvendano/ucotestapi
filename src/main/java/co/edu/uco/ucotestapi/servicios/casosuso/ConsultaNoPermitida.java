package co.edu.uco.ucotestapi.servicios.casosuso;

/** El docente consulta un grupo que no es suyo (Q-13, Q-15): sale como 403. */
public class ConsultaNoPermitida extends RuntimeException {

	public ConsultaNoPermitida() {
		super("consulta no permitida");
	}
}
