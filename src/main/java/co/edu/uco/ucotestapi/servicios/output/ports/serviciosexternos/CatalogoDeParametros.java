package co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos;

import co.edu.uco.ucotestapi.servicios.output.dto.ParametroDTO;

/** Parameter catalog: valores de negocio configurables por clave (A-8, A-36, A-58). */
public interface CatalogoDeParametros {

	ParametroDTO porClave(String clave);
}
