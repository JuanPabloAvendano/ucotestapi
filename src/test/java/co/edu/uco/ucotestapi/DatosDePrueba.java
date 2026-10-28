package co.edu.uco.ucotestapi;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;

import co.edu.uco.ucotestapi.transversal.UuidV7;

/**
 * Inserta los datos de cada prueba directamente en Postgres (A-47). La clase de prueba sigue los pares
 * cumple/viola de §4: cada caso viola solo su regla y las anteriores en el orden de A-29 se cumplen.
 */
public final class DatosDePrueba {

	public record ClaseDePrueba(String cuentaCarlos, String cuentaDiana, String cuentaPedro, UUID carlos,
			UUID arquitectura01, UUID grupoVencido, UUID grupoDePedro, UUID parcial1, UUID parcialVencido,
			UUID tallerDePedro, UUID ana, UUID luisInactivo, UUID martaSinMatricula, UUID matriculaDeAna) {
	}

	private final JdbcTemplate jdbc;

	public DatosDePrueba(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public ClaseDePrueba sembrar() {
		UUID carlos = docente("sub-carlos", "Carlos", true);
		docente("sub-diana", "Diana", false);
		UUID pedro = docente("sub-pedro", "Pedro", true);
		UUID arquitectura01 = grupo("Arquitectura de Software 01", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 12, 15), carlos);
		UUID vencido = grupo("Arquitectura de Software 2025-2", LocalDate.of(2025, 8, 1), LocalDate.of(2025, 12, 15), carlos);
		UUID dePedro = grupo("Redes 01", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 12, 15), pedro);
		UUID parcial1 = evaluacion("Parcial 1", arquitectura01);
		UUID parcialVencido = evaluacion("Parcial 1", vencido);
		UUID taller = evaluacion("Taller 1", dePedro);
		UUID ana = estudiante("sub-ana", "Ana", true);
		UUID luis = estudiante("sub-luis", "Luis", false);
		UUID marta = estudiante("sub-marta", "Marta", true);
		UUID matriculaDeAna = matricula(ana, arquitectura01);
		matricula(luis, arquitectura01);
		matricula(ana, vencido);
		return new ClaseDePrueba("sub-carlos", "sub-diana", "sub-pedro", carlos, arquitectura01, vencido, dePedro,
				parcial1, parcialVencido, taller, ana, luis, marta, matriculaDeAna);
	}

	public UUID docente(String cuenta, String nombre, boolean activo) {
		UUID id = UuidV7.nuevo();
		jdbc.update("insert into docente (id, cuenta_id, nombre, activo) values (?, ?, ?, ?)", id, cuenta, nombre, activo);
		return id;
	}

	public UUID estudiante(String cuenta, String nombre, boolean activo) {
		UUID id = UuidV7.nuevo();
		jdbc.update("insert into estudiante (id, cuenta_id, nombre, activo) values (?, ?, ?, ?)", id, cuenta, nombre,
				activo);
		return id;
	}

	public UUID grupo(String nombre, LocalDate inicio, LocalDate fin, UUID docente) {
		UUID id = UuidV7.nuevo();
		jdbc.update("insert into grupo (id, nombre, fecha_inicio, fecha_fin, docente_id) values (?, ?, ?, ?, ?)", id,
				nombre, inicio, fin, docente);
		return id;
	}

	public UUID evaluacion(String descripcion, UUID grupo) {
		UUID id = UuidV7.nuevo();
		jdbc.update("insert into nota (id, descripcion, grupo_id) values (?, ?, ?)", id, descripcion, grupo);
		return id;
	}

	public UUID matricula(UUID estudiante, UUID grupo) {
		UUID id = UuidV7.nuevo();
		jdbc.update("insert into estudiante_grupo (id, estudiante_id, grupo_id) values (?, ?, ?)", id, estudiante, grupo);
		return id;
	}

	public UUID calificacion(UUID evaluacion, UUID estudiante, int centesimas) {
		UUID id = UuidV7.nuevo();
		jdbc.update("insert into nota_estudiante (id, nota_id, estudiante_id, centesimas) values (?, ?, ?, ?)", id,
				evaluacion, estudiante, centesimas);
		return id;
	}

	public int calificacionesDe(UUID evaluacion, UUID estudiante) {
		return jdbc.queryForObject("select count(*) from nota_estudiante where nota_id = ? and estudiante_id = ?",
				Integer.class, evaluacion, estudiante);
	}
}
