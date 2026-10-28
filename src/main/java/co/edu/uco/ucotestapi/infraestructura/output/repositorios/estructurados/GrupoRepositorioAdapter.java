package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import co.edu.uco.ucotestapi.dominio.docente.DocenteId;
import co.edu.uco.ucotestapi.dominio.grupo.Grupo;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.GrupoJPARepositorio;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.GrupoEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.GrupoRepositorio;
import co.edu.uco.ucotestapi.transversal.Pagina;

@Repository
public class GrupoRepositorioAdapter implements GrupoRepositorio {

	private final GrupoJPARepositorio jpa;
	private final EntidadMapper mapper;

	public GrupoRepositorioAdapter(GrupoJPARepositorio jpa, EntidadMapper mapper) {
		this.jpa = Objects.requireNonNull(jpa, "jpa");
		this.mapper = Objects.requireNonNull(mapper, "mapper");
	}

	@Override
	public Grupo porId(GrupoId id) {
		return jpa.findById(id.valor()).map(mapper::aDominio).orElse(Grupo.noExiste());
	}

	@Override
	public Pagina<Grupo> delDocente(DocenteId padre, int pagina, int tamano) {
		Page<GrupoEntidadJPA> resultado = jpa.findByDocente_Id(padre.valor(), PageRequest.of(pagina, tamano, Sort.by("nombre")));
		return new Pagina<>(resultado.map(mapper::aDominio).getContent(), pagina, tamano, resultado.getTotalElements());
	}
}
