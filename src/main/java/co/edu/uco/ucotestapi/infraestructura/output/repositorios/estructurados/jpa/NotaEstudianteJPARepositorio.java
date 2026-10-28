package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEstudianteEntidadJPA;

public interface NotaEstudianteJPARepositorio extends JpaRepository<NotaEstudianteEntidadJPA, UUID> {

	boolean existsByNota_IdAndEstudiante_Id(UUID notaId, UUID estudianteId);
}
