package co.edu.uco.ucotestapi.infraestructura.output.adapter;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.UnidadDeTrabajo;

/**
 * Transacción programática (A-31) con tiempo límite (A-32): Hibernate lo aplica a cada sentencia con lo que queda,
 * así la espera por el bloqueo de la matrícula termina en rollback y nunca deja la petición colgada.
 */
@Component
public class UnidadDeTrabajoAdapter implements UnidadDeTrabajo {

	private final TransactionTemplate plantilla;

	public UnidadDeTrabajoAdapter(PlatformTransactionManager transacciones,
			@Value("${registro.transaccion.tiempo-limite}") Duration tiempoLimite) {
		this.plantilla = new TransactionTemplate(Objects.requireNonNull(transacciones, "transacciones"));
		this.plantilla.setTimeout(Math.toIntExact(tiempoLimite.toSeconds()));
	}

	@Override
	public <T> T ejecutar(Supplier<T> bloque) {
		return plantilla.execute(estado -> bloque.get());
	}
}
