package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Tabla {@code nota}: la evaluación de un grupo (COH-14). */
@Entity
@Table(name = "nota")
public class NotaEntidadJPA {

	@Id
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "descripcion", nullable = false, length = 120)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "grupo_id", nullable = false)
	private GrupoEntidadJPA grupo;

	protected NotaEntidadJPA() {
	}

	public NotaEntidadJPA(UUID id, String descripcion, GrupoEntidadJPA grupo) {
		this.id = id;
		this.descripcion = descripcion;
		this.grupo = grupo;
	}

	public UUID getId() {
		return id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public GrupoEntidadJPA getGrupo() {
		return grupo;
	}
}
