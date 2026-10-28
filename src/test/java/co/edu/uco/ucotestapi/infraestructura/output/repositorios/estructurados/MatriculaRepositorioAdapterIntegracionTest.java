package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import co.edu.uco.ucotestapi.PruebaDeIntegracion;
import co.edu.uco.ucotestapi.SentenciasCapturadas;
import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteGrupo;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;

/** A-22 y A-62 (7)(8): el bloqueo toma solo la fila de {@code estudiante_grupo}, sin joins (COH-25). */
class MatriculaRepositorioAdapterIntegracionTest extends PruebaDeIntegracion {

	@Autowired
	MatriculaRepositorioAdapter matriculas;

	@Autowired
	PlatformTransactionManager transacciones;

	@Test
	void bloquearEmiteUnSelectForUpdateSoloSobreLaMatriculaSinJoins() {
		SentenciasCapturadas.limpiar();

		EstudianteGrupo bloqueada = new TransactionTemplate(transacciones).execute(
				estado -> matriculas.bloquear(new EstudianteId(clase.ana()), new GrupoId(clase.arquitectura01())));

		assertThat(bloqueada.existe()).isTrue();
		assertThat(bloqueada.id().valor()).isEqualTo(clase.matriculaDeAna());
		assertThat(SentenciasCapturadas.todas()).filteredOn(sql -> sql.contains(" for "))
				.singleElement()
				.satisfies(sql -> assertThat(sql).contains("estudiante_grupo").containsPattern("for (no key )?update")
						.doesNotContain(" join "));
	}

	@Test
	void sinMatriculaDevuelveElNullObject() {
		EstudianteGrupo bloqueada = new TransactionTemplate(transacciones).execute(
				estado -> matriculas.bloquear(new EstudianteId(UUID.randomUUID()), new GrupoId(clase.arquitectura01())));

		assertThat(bloqueada.existe()).isFalse();
	}

	@Test
	void laConsultaDeEstudiantesMarcaQuienYaTieneNota() {
		datos.calificacion(clase.parcial1(), clase.ana(), 400);

		var pagina = matriculas.estudiantesDe(new GrupoId(clase.arquitectura01()),
				new co.edu.uco.ucotestapi.dominio.grupo.NotaId(clase.parcial1()), 0, 20);

		assertThat(pagina.total()).isEqualTo(2);
		assertThat(pagina.contenido()).extracting(fila -> fila.nombre() + ":" + fila.tieneNota())
				.containsExactly("Ana:true", "Luis:false");
	}
}
