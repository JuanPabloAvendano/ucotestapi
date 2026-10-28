package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados;

import java.util.Objects;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudiante;
import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudianteId;
import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;
import co.edu.uco.ucotestapi.dominio.mensajes.CodigoMensaje;
import co.edu.uco.ucotestapi.dominio.reglas.excepciones.ReglaIncumplida;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.EstudianteJPARepositorio;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.NotaEstudianteJPARepositorio;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.NotaJPARepositorio;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.NotaEstudianteRepositorio;

@Repository
public class NotaEstudianteRepositorioAdapter implements NotaEstudianteRepositorio {

	static final String INDICE_UNICO = "uk_nota_estudiante";

	private final NotaEstudianteJPARepositorio jpa;
	private final NotaJPARepositorio evaluaciones;
	private final EstudianteJPARepositorio estudiantes;
	private final EntidadMapper mapper;

	public NotaEstudianteRepositorioAdapter(NotaEstudianteJPARepositorio jpa, NotaJPARepositorio evaluaciones,
			EstudianteJPARepositorio estudiantes, EntidadMapper mapper) {
		this.jpa = Objects.requireNonNull(jpa, "jpa");
		this.evaluaciones = Objects.requireNonNull(evaluaciones, "evaluaciones");
		this.estudiantes = Objects.requireNonNull(estudiantes, "estudiantes");
		this.mapper = Objects.requireNonNull(mapper, "mapper");
	}

	@Override
	public boolean existe(NotaId evaluacion, EstudianteId estudiante) {
		return jpa.existsByNota_IdAndEstudiante_Id(evaluacion.valor(), estudiante.valor());
	}

	/**
	 * {@code saveAndFlush} hace que el índice único hable dentro del bloque de la unidad de trabajo y no en el
	 * commit, donde ya no se podría traducir a 422 (A-62 (7), COH-19). Solo se traduce ese índice: cualquier otro
	 * fallo es técnico y sale como 500 (C-15).
	 */
	@Override
	public NotaEstudiante guardar(NotaEstudiante calificacion) {
		try {
			jpa.saveAndFlush(mapper.aEntidad(calificacion, evaluaciones.getReferenceById(calificacion.nota().valor()),
					estudiantes.getReferenceById(calificacion.estudiante().valor())));
			return calificacion;
		}
		catch (DataIntegrityViolationException fallo) {
			if (fallo.getCause() instanceof ConstraintViolationException violacion
					&& INDICE_UNICO.equalsIgnoreCase(violacion.getConstraintName())) {
				throw new ReglaIncumplida(CodigoMensaje.M_NOTA_R06);
			}
			throw fallo;
		}
	}

	@Override
	public NotaEstudiante porId(NotaEstudianteId id) {
		return jpa.findById(id.valor()).map(mapper::aDominio).orElse(NotaEstudiante.noExiste());
	}
}
