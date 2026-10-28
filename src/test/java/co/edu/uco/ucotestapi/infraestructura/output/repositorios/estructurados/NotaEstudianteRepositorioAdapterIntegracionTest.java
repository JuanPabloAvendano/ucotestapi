package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import co.edu.uco.ucotestapi.PruebaDeIntegracion;
import co.edu.uco.ucotestapi.dominio.calificacion.Calificacion;
import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudiante;
import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudianteId;
import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;
import co.edu.uco.ucotestapi.dominio.mensajes.CodigoMensaje;
import co.edu.uco.ucotestapi.dominio.reglas.excepciones.ReglaIncumplida;
import co.edu.uco.ucotestapi.transversal.UuidV7;

/** A-32 y A-62 (7): si algo se salta el bloqueo, el índice único responde como R-6 y no como error técnico. */
class NotaEstudianteRepositorioAdapterIntegracionTest extends PruebaDeIntegracion {

	@Autowired
	NotaEstudianteRepositorioAdapter calificaciones;

	@Autowired
	PlatformTransactionManager transacciones;

	@Test
	void elIndiceUnicoSeTraduceAReglaIncumplidaR06DentroDeLaTransaccion() {
		datos.calificacion(clase.parcial1(), clase.ana(), 400);
		NotaEstudiante duplicada = NotaEstudiante.registrar(new NotaEstudianteId(UuidV7.nuevo()),
				new NotaId(clase.parcial1()), new EstudianteId(clase.ana()), new Calificacion(435));

		assertThatThrownBy(() -> new TransactionTemplate(transacciones).execute(estado -> calificaciones.guardar(duplicada)))
				.isInstanceOfSatisfying(ReglaIncumplida.class,
						regla -> assertThat(regla.codigo()).isEqualTo(CodigoMensaje.M_NOTA_R06));
		assertThat(datos.calificacionesDe(clase.parcial1(), clase.ana())).isEqualTo(1);
	}

	@Test
	void guardaYSeLeePorId() {
		NotaEstudiante nueva = NotaEstudiante.registrar(new NotaEstudianteId(UuidV7.nuevo()),
				new NotaId(clase.parcial1()), new EstudianteId(clase.ana()), new Calificacion(435));

		new TransactionTemplate(transacciones).execute(estado -> calificaciones.guardar(nueva));

		NotaEstudiante leida = calificaciones.porId(nueva.id());
		assertThat(leida.existe()).isTrue();
		assertThat(leida.calificacion().centesimas()).isEqualTo(435);
		assertThat(calificaciones.existe(new NotaId(clase.parcial1()), new EstudianteId(clase.ana()))).isTrue();
	}
}
