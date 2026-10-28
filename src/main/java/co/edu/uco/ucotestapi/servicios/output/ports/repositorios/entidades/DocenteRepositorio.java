package co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades;

import co.edu.uco.ucotestapi.dominio.cuenta.CuentaId;
import co.edu.uco.ucotestapi.dominio.docente.Docente;

/** Docentes por la cuenta del token (A-21); si no hay, {@link Docente#noExiste()}. */
public interface DocenteRepositorio {

	Docente porCuenta(CuentaId cuenta);
}
