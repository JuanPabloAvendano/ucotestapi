package co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades;

import co.edu.uco.ucotestapi.dominio.docente.DocenteId;
import co.edu.uco.ucotestapi.dominio.grupo.Grupo;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.transversal.Pagina;

public interface GrupoRepositorio {

	/** Si no existe, {@link Grupo#noExiste()}. */
	Grupo porId(GrupoId id);

	Pagina<Grupo> delDocente(DocenteId docente, int pagina, int tamano);
}
