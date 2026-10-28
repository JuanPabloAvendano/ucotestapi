package co.edu.uco.ucotestapi.dominio.docente;

import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.cuenta.CuentaId;

/** Quien dicta un grupo y registra calificaciones (R-0, R-7). */
public final class Docente {

	private static final Docente NO_EXISTE = new Docente(DocenteId.NINGUNO, CuentaId.NINGUNA, "", false);

	private final DocenteId id;
	private final CuentaId cuenta;
	private final String nombre;
	private final boolean activo;

	private Docente(DocenteId id, CuentaId cuenta, String nombre, boolean activo) {
		this.id = Objects.requireNonNull(id, "id");
		this.cuenta = Objects.requireNonNull(cuenta, "cuenta");
		this.nombre = Objects.requireNonNull(nombre, "nombre");
		this.activo = activo;
	}

	public static Docente existente(DocenteId id, CuentaId cuenta, String nombre, boolean activo) {
		return new Docente(id, cuenta, nombre, activo);
	}

	/** Null Object (A-11): el token no tiene cuenta de docente; no está activo ni asignado a nada. */
	public static Docente noExiste() {
		return NO_EXISTE;
	}

	public boolean existe() {
		return this != NO_EXISTE;
	}

	public boolean estaActivo() {
		return activo;
	}

	public DocenteId id() {
		return id;
	}

	public CuentaId cuenta() {
		return cuenta;
	}

	public String nombre() {
		return nombre;
	}
}
