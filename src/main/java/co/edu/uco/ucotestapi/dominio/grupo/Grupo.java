package co.edu.uco.ucotestapi.dominio.grupo;

import java.time.LocalDate;
import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.docente.DocenteId;

/** Curso dictado en un periodo por un docente (R-2, R-3, R-8; Q-16: un docente por grupo). */
public final class Grupo {

	private static final Grupo NO_EXISTE = new Grupo(GrupoId.NINGUNO, "",
			new Periodo(LocalDate.EPOCH, LocalDate.EPOCH), DocenteId.NINGUNO);

	private final GrupoId id;
	private final String nombre;
	private final Periodo periodo;
	private final DocenteId docenteAsignado;

	private Grupo(GrupoId id, String nombre, Periodo periodo, DocenteId docenteAsignado) {
		this.id = Objects.requireNonNull(id, "id");
		this.nombre = Objects.requireNonNull(nombre, "nombre");
		this.periodo = Objects.requireNonNull(periodo, "periodo");
		this.docenteAsignado = Objects.requireNonNull(docenteAsignado, "docenteAsignado");
	}

	public static Grupo existente(GrupoId id, String nombre, Periodo periodo, DocenteId docenteAsignado) {
		return new Grupo(id, nombre, periodo, docenteAsignado);
	}

	/** Null Object (A-11): no está vigente ni asignado a nadie. */
	public static Grupo noExiste() {
		return NO_EXISTE;
	}

	public boolean existe() {
		return this != NO_EXISTE;
	}

	/** R-3 por fechas y no por un estado que se cambia a mano (A-42). */
	public boolean estaVigenteEn(LocalDate dia) {
		return existe() && periodo.contiene(dia);
	}

	public boolean estaAsignadoA(DocenteId docente) {
		return existe() && docenteAsignado.equals(docente);
	}

	public GrupoId id() {
		return id;
	}

	public String nombre() {
		return nombre;
	}

	public Periodo periodo() {
		return periodo;
	}

	public DocenteId docenteAsignado() {
		return docenteAsignado;
	}
}
