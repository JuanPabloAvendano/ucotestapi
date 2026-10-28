package co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades;

import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.Nota;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;
import co.edu.uco.ucotestapi.transversal.Pagina;

/** Evaluaciones, que en el código se llaman {@link Nota} (COH-14). */
public interface EvaluacionRepositorio {

	/** Si no existe, {@link Nota#noExiste()}. */
	Nota porId(NotaId id);

	Pagina<Nota> delGrupo(GrupoId grupo, int pagina, int tamano);
}
