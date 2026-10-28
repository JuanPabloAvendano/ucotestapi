package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.mapper;

import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudiante;
import co.edu.uco.ucotestapi.dominio.docente.Docente;
import co.edu.uco.ucotestapi.dominio.estudiante.Estudiante;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteGrupo;
import co.edu.uco.ucotestapi.dominio.grupo.Grupo;
import co.edu.uco.ucotestapi.dominio.grupo.Nota;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.DocenteEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteGrupoEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.GrupoEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEstudianteEntidadJPA;

/** Traduce entre el dominio y las entidades JPA (Fowler, PoEAA «Data Mapper»); el dominio no conoce JPA (A-30). */
public interface EntidadMapper {

	Docente aDominio(DocenteEntidadJPA entidad);

	Estudiante aDominio(EstudianteEntidadJPA entidad);

	Grupo aDominio(GrupoEntidadJPA entidad);

	Nota aDominio(NotaEntidadJPA entidad);

	EstudianteGrupo aDominio(EstudianteGrupoEntidadJPA entidad);

	NotaEstudiante aDominio(NotaEstudianteEntidadJPA entidad);

	/** Las asociaciones llegan como referencias para no cargar la evaluación ni el estudiante al guardar. */
	NotaEstudianteEntidadJPA aEntidad(NotaEstudiante calificacion, NotaEntidadJPA evaluacion,
			EstudianteEntidadJPA estudiante);
}
