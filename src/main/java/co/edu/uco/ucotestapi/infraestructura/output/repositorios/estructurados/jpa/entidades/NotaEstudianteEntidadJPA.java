package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades;

import java.util.UUID;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

/**
 * Tabla {@code nota_estudiante}: la calificación registrada. {@code uk_nota_estudiante} es el respaldo del bloqueo
 * (A-32). Implementa {@link Persistable} porque el id llega asignado (UUID v7) y, sin eso, Spring Data haría un
 * {@code merge} con un {@code SELECT} previo en cada guardado.
 */
@Entity
@Table(name = "nota_estudiante",
		uniqueConstraints = @UniqueConstraint(name = "uk_nota_estudiante", columnNames = { "nota_id", "estudiante_id" }))
public class NotaEstudianteEntidadJPA implements Persistable<UUID> {

	@Id
	@Column(name = "id", nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "nota_id", nullable = false)
	private NotaEntidadJPA nota;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "estudiante_id", nullable = false)
	private EstudianteEntidadJPA estudiante;

	@Column(name = "centesimas", nullable = false)
	private int centesimas;

	@Transient
	private boolean nueva = true;

	protected NotaEstudianteEntidadJPA() {
	}

	public NotaEstudianteEntidadJPA(UUID id, NotaEntidadJPA nota, EstudianteEntidadJPA estudiante, int centesimas) {
		this.id = id;
		this.nota = nota;
		this.estudiante = estudiante;
		this.centesimas = centesimas;
	}

	@PostLoad
	@PostPersist
	void marcarComoGuardada() {
		nueva = false;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return nueva;
	}

	public NotaEntidadJPA getNota() {
		return nota;
	}

	public EstudianteEntidadJPA getEstudiante() {
		return estudiante;
	}

	public int getCentesimas() {
		return centesimas;
	}
}
