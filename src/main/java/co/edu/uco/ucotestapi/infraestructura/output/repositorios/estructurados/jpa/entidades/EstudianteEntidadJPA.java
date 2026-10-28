package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Tabla {@code estudiante} (Q-8); {@code cuenta_id} es el {@code sub} de Keycloak (A-21). */
@Entity
@Table(name = "estudiante")
public class EstudianteEntidadJPA {

	@Id
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "cuenta_id", nullable = false, unique = true, length = 64)
	private String cuentaId;

	@Column(name = "nombre", nullable = false, length = 120)
	private String nombre;

	@Column(name = "activo", nullable = false)
	private boolean activo;

	protected EstudianteEntidadJPA() {
	}

	public EstudianteEntidadJPA(UUID id, String cuentaId, String nombre, boolean activo) {
		this.id = id;
		this.cuentaId = cuentaId;
		this.nombre = nombre;
		this.activo = activo;
	}

	public UUID getId() {
		return id;
	}

	public String getCuentaId() {
		return cuentaId;
	}

	public String getNombre() {
		return nombre;
	}

	public boolean isActivo() {
		return activo;
	}
}
