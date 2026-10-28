package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteDelGrupo;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteGrupo;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.EstudianteGrupoJPARepositorio;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteGrupoEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEstudianteEntidadJPA;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.MatriculaRepositorio;
import co.edu.uco.ucotestapi.transversal.Pagina;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Repository
public class MatriculaRepositorioAdapter implements MatriculaRepositorio {

	private final EstudianteGrupoJPARepositorio jpa;
	private final EntidadMapper mapper;
	private final EntityManager entityManager;

	public MatriculaRepositorioAdapter(EstudianteGrupoJPARepositorio jpa, EntidadMapper mapper,
			EntityManager entityManager) {
		this.jpa = Objects.requireNonNull(jpa, "jpa");
		this.mapper = Objects.requireNonNull(mapper, "mapper");
		this.entityManager = Objects.requireNonNull(entityManager, "entityManager");
	}

	@Override
	public EstudianteGrupo de(EstudianteId estudiante, GrupoId grupo) {
		return jpa.findByEstudiante_IdAndGrupo_Id(estudiante.valor(), grupo.valor()).map(mapper::aDominio)
				.orElse(EstudianteGrupo.noExiste());
	}

	@Override
	public EstudianteGrupo bloquear(EstudianteId estudiante, GrupoId grupo) {
		return jpa.bloquear(estudiante.valor(), grupo.valor()).map(mapper::aDominio).orElse(EstudianteGrupo.noExiste());
	}

	/**
	 * Criteria API (A-11): {@code tieneNota} sale de un {@code exists} sobre {@code nota_estudiante} en la misma
	 * consulta, así la lista no hace una consulta por estudiante.
	 */
	@Override
	@Transactional(readOnly = true)
	public Pagina<EstudianteDelGrupo> estudiantesDe(GrupoId grupo, NotaId evaluacion, int pagina, int tamano) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<Tuple> consulta = cb.createTupleQuery();
		Root<EstudianteGrupoEntidadJPA> matricula = consulta.from(EstudianteGrupoEntidadJPA.class);
		Join<EstudianteGrupoEntidadJPA, EstudianteEntidadJPA> estudiante = matricula.join("estudiante");
		Subquery<Integer> calificada = consulta.subquery(Integer.class);
		Root<NotaEstudianteEntidadJPA> calificacion = calificada.from(NotaEstudianteEntidadJPA.class);
		calificada.select(cb.literal(1)).where(
				cb.equal(calificacion.get("nota").get("id"), evaluacion.valor()),
				cb.equal(calificacion.get("estudiante"), estudiante));
		consulta.multiselect(estudiante.get("id").alias("id"), estudiante.get("nombre").alias("nombre"),
				cb.exists(calificada).alias("tieneNota"))
				.where(cb.equal(matricula.get("grupo").get("id"), grupo.valor()))
				.orderBy(cb.asc(estudiante.get("nombre")));
		List<EstudianteDelGrupo> filas = entityManager.createQuery(consulta)
				.setFirstResult(pagina * tamano)
				.setMaxResults(tamano)
				.getResultList().stream()
				.map(fila -> new EstudianteDelGrupo(new EstudianteId(fila.get("id", UUID.class)),
						fila.get("nombre", String.class), fila.get("tieneNota", Boolean.class)))
				.toList();

		CriteriaQuery<Long> conteo = cb.createQuery(Long.class);
		Root<EstudianteGrupoEntidadJPA> todas = conteo.from(EstudianteGrupoEntidadJPA.class);
		conteo.select(cb.count(todas)).where(cb.equal(todas.get("grupo").get("id"), grupo.valor()));
		return new Pagina<>(filas, pagina, tamano, entityManager.createQuery(conteo).getSingleResult());
	}
}
