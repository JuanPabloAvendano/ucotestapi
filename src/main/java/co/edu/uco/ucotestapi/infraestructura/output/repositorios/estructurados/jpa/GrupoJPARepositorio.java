package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.GrupoEntidadJPA;

public interface GrupoJPARepositorio extends JpaRepository<GrupoEntidadJPA, UUID> {

	Page<GrupoEntidadJPA> findByDocente_Id(UUID docenteId, Pageable pagina);
}
