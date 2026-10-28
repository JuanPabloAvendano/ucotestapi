package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.Nota;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.NotaJPARepositorio;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.EvaluacionRepositorio;
import co.edu.uco.ucotestapi.transversal.Pagina;

@Repository
public class EvaluacionRepositorioAdapter implements EvaluacionRepositorio {

	private final NotaJPARepositorio jpa;
	private final EntidadMapper mapper;

	public EvaluacionRepositorioAdapter(NotaJPARepositorio jpa, EntidadMapper mapper) {
		this.jpa = Objects.requireNonNull(jpa, "jpa");
		this.mapper = Objects.requireNonNull(mapper, "mapper");
	}

	@Override
	public Nota porId(NotaId id) {
		return jpa.findById(id.valor()).map(mapper::aDominio).orElse(Nota.noExiste());
	}

	@Override
	public Pagina<Nota> delGrupo(GrupoId padre, int pagina, int tamano) {
		Page<NotaEntidadJPA> resultado = jpa.findByGrupo_Id(padre.valor(), PageRequest.of(pagina, tamano, Sort.by("descripcion")));
		return new Pagina<>(resultado.map(mapper::aDominio).getContent(), pagina, tamano, resultado.getTotalElements());
	}
}
