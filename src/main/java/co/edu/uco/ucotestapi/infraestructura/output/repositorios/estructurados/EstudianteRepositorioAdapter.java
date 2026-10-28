package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados;

import java.util.Objects;

import org.springframework.stereotype.Repository;


import co.edu.uco.ucotestapi.dominio.estudiante.Estudiante;
import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.EstudianteJPARepositorio;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.EstudianteRepositorio;

@Repository
public class EstudianteRepositorioAdapter implements EstudianteRepositorio{
		

	private final EstudianteJPARepositorio jpa;
	private final EntidadMapper mapper;
	
	public EstudianteRepositorioAdapter(EstudianteJPARepositorio jpa, EntidadMapper mapper) {
		this.jpa = Objects.requireNonNull(jpa, "jpa");
		this.mapper= Objects.requireNonNull(mapper, "mapper");
		
		
	}

	@Override
	public Estudiante porId(EstudianteId estudianteId) {
		
		return jpa.findById(estudianteId.valor()).map(mapper::aDominio).orElse(Estudiante.noExiste());
	}
}
