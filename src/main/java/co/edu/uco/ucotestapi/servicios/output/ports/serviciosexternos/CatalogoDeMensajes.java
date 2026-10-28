package co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos;

import co.edu.uco.ucotestapi.servicios.output.dto.MensajeDTO;

/** Message catalog: textos para el usuario por código (A-37, A-58). */
public interface CatalogoDeMensajes {

	MensajeDTO porCodigo(String codigo);
}
