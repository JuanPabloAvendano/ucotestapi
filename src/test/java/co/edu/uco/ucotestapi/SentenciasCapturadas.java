package co.edu.uco.ucotestapi;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * Guarda el SQL que Hibernate envía, para probar la forma del bloqueo (A-62 (8)). Se registra en
 * {@code application-test.properties} con {@code hibernate.session_factory.statement_inspector}.
 */
public class SentenciasCapturadas implements StatementInspector {

	private static final List<String> SENTENCIAS = new CopyOnWriteArrayList<>();

	@Override
	public String inspect(String sql) {
		SENTENCIAS.add(sql.toLowerCase());
		return sql;
	}

	public static void limpiar() {
		SENTENCIAS.clear();
	}

	public static List<String> todas() {
		return List.copyOf(SENTENCIAS);
	}
}
