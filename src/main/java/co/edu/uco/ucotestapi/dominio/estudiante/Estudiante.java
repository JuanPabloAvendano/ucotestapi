package co.edu.uco.ucotestapi.dominio.estudiante;

import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.cuenta.CuentaId;
import co.edu.uco.ucotestapi.dominio.docente.DocenteId;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteGrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;

/** La persona que recibe calificaciones (R-1). */
public final class Estudiante {

	private static final Estudiante NO_EXISTE = new Estudiante(EstudianteId.NINGUNO , CuentaId.NINGUNA , "" , false);
	private final EstudianteId id;
	private final CuentaId cuenta;
	private final String nombre;
	private final boolean activo;
	
	private Estudiante(EstudianteId id, CuentaId cuenta, String nombre, boolean activo) {
		this.id = Objects.requireNonNull(id);
		this.cuenta = Objects.requireNonNull(cuenta);
		this.nombre = Objects.requireNonNull(nombre);
		this.activo = Objects.requireNonNull(activo);
		
	}
	
	public static Estudiante existente(EstudianteId id, CuentaId cuenta, String nombre, boolean activo) {
		return new Estudiante(id,cuenta,nombre,activo);
	}

	
	public static Estudiante noExiste() {
		return NO_EXISTE;
	}
	
	public boolean existe() {
		return this != NO_EXISTE;
	}

	public boolean estaActivo() {
		return activo;
	}

	public EstudianteId id() {
		return id;
	}

	public CuentaId cuenta() {
		return cuenta;
	}

	public String nombre() {
		return nombre;
	}
	
	
	
	
	
	
}
