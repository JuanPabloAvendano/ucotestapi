package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteGrupoEntidadJPA;

import jakarta.persistence.LockModeType;

public interface EstudianteGrupoJPARepositorio extends JpaRepository<EstudianteGrupoEntidadJPA, UUID> {

	Optional<EstudianteGrupoEntidadJPA> findByEstudiante_IdAndGrupo_Id(UUID estudianteId, UUID grupoId);

	/**
	 * Bloqueo pesimista solo sobre {@code estudiante_grupo} (A-22, A-62 (8)): la ruta {@code .id} de una asociación
	 * usa la columna de la clave foránea, sin join, así no se bloquean también el grupo ni el estudiante (COH-25).
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select m from EstudianteGrupoEntidadJPA m where m.estudiante.id = :estudianteId and m.grupo.id = :grupoId")
	Optional<EstudianteGrupoEntidadJPA> bloquear(@Param("estudianteId") UUID estudianteId,
			@Param("grupoId") UUID grupoId);
}
