package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteEntidadJPA;

public interface EstudianteJPARepositorio extends JpaRepository<EstudianteEntidadJPA, UUID> {
	
}
