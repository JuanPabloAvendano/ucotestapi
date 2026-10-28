package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEntidadJPA;

public interface NotaJPARepositorio extends JpaRepository<NotaEntidadJPA, UUID> {

	Page<NotaEntidadJPA> findByGrupo_Id(UUID grupoId, Pageable pagina);
}
