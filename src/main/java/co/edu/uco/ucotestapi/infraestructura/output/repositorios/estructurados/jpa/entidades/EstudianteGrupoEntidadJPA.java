package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Tabla {@code estudiante_grupo}: la matrícula, la fila que se bloquea contra el registro doble (A-22). */
@Entity
@Table(name = "estudiante_grupo",
		uniqueConstraints = @UniqueConstraint(name = "uk_estudiante_grupo", columnNames = { "estudiante_id", "grupo_id" }))
public class EstudianteGrupoEntidadJPA {

	@Id
	@Column(name = "id", nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "estudiante_id", nullable = false)
	private EstudianteEntidadJPA estudiante;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "grupo_id", nullable = false)
	private GrupoEntidadJPA grupo;

	protected EstudianteGrupoEntidadJPA() {
	}

	public EstudianteGrupoEntidadJPA(UUID id, EstudianteEntidadJPA estudiante, GrupoEntidadJPA grupo) {
		this.id = id;
		this.estudiante = estudiante;
		this.grupo = grupo;
	}

	public UUID getId() {
		return id;
	}

	public EstudianteEntidadJPA getEstudiante() {
		return estudiante;
	}

	public GrupoEntidadJPA getGrupo() {
		return grupo;
	}
}
