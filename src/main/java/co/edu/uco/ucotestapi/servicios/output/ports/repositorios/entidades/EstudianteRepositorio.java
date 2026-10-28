package co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades;

import co.edu.uco.ucotestapi.dominio.estudiante.Estudiante;
import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;

public interface EstudianteRepositorio {
	
	Estudiante porId(EstudianteId id);
}
