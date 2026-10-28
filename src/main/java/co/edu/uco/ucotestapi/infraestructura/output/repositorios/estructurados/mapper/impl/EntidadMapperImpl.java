package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.mapper.impl;

import org.springframework.stereotype.Component;

import co.edu.uco.ucotestapi.dominio.calificacion.Calificacion;
import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudiante;
import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudianteId;
import co.edu.uco.ucotestapi.dominio.cuenta.CuentaId;
import co.edu.uco.ucotestapi.dominio.docente.Docente;
import co.edu.uco.ucotestapi.dominio.docente.DocenteId;
import co.edu.uco.ucotestapi.dominio.estudiante.Estudiante;
import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteGrupo;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteGrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.Grupo;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.Nota;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;
import co.edu.uco.ucotestapi.dominio.grupo.Periodo;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.DocenteEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteGrupoEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.GrupoEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEstudianteEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;

/**
 * Las asociaciones {@code LAZY} solo se leen por su id, que Hibernate entrega sin cargar el proxy, así el mapeo
 * funciona fuera de la sesión (A-62 (6): sin open-in-view).
 */
@Component
public class EntidadMapperImpl implements EntidadMapper {
	
	@Override
	public Docente aDominio(DocenteEntidadJPA entidad) {
		return Docente.existente(new DocenteId(entidad.getId()), new CuentaId(entidad.getCuentaId()),
				entidad.getNombre(), entidad.isActivo());
	}
	@Override
	public Estudiante aDominio(EstudianteEntidadJPA entidad) {
		return Estudiante.existente(new EstudianteId(entidad.getId()),new CuentaId(entidad.getCuentaId()), 
				entidad.getNombre(), entidad.isActivo()); 
	}
	
	@Override
	public Grupo aDominio(GrupoEntidadJPA entidad) {
		return Grupo.existente(new GrupoId(entidad.getId()), entidad.getNombre(),
				new Periodo(entidad.getFechaInicio(), entidad.getFechaFin()),
				new DocenteId(entidad.getDocente().getId()));
	}

	@Override
	public Nota aDominio(NotaEntidadJPA entidad) {
		return Nota.existente(new NotaId(entidad.getId()), new GrupoId(entidad.getGrupo().getId()),
				entidad.getDescripcion());
	}

	@Override
	public EstudianteGrupo aDominio(EstudianteGrupoEntidadJPA entidad) {
		return EstudianteGrupo.existente(new EstudianteGrupoId(entidad.getId()),
				new EstudianteId(entidad.getEstudiante().getId()), new GrupoId(entidad.getGrupo().getId()));
	}

	@Override
	public NotaEstudiante aDominio(NotaEstudianteEntidadJPA entidad) {
		return NotaEstudiante.registrar(new NotaEstudianteId(entidad.getId()), new NotaId(entidad.getNota().getId()),
				new EstudianteId(entidad.getEstudiante().getId()), new Calificacion(entidad.getCentesimas()));
	}

	@Override
	public NotaEstudianteEntidadJPA aEntidad(NotaEstudiante calificacion, NotaEntidadJPA evaluacion,
			EstudianteEntidadJPA estudiante) {
		return new NotaEstudianteEntidadJPA(calificacion.id().valor(), evaluacion, estudiante,
				calificacion.calificacion().centesimas());
	}

	
}
