package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Tabla {@code grupo}; un docente por grupo en {@code docente_id} (Q-16) y periodo en días UTC (A-42). */
@Entity
@Table(name = "grupo")
public class GrupoEntidadJPA {

	@Id
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "nombre", nullable = false, length = 120)
	private String nombre;

	@Column(name = "fecha_inicio", nullable = false)
	private LocalDate fechaInicio;

	@Column(name = "fecha_fin", nullable = false)
	private LocalDate fechaFin;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "docente_id", nullable = false)
	private DocenteEntidadJPA docente;

	protected GrupoEntidadJPA() {
	}

	public GrupoEntidadJPA(UUID id, String nombre, LocalDate fechaInicio, LocalDate fechaFin,
			DocenteEntidadJPA docente) {
		this.id = id;
		this.nombre = nombre;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.docente = docente;
	}

	public UUID getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public DocenteEntidadJPA getDocente() {
		return docente;
	}
}
