package co.edu.uco.ucotestapi.servicios.input.ports;

import co.edu.uco.ucotestapi.servicios.input.dto.request.RegistrarNotaAEstudianteRequest;
import co.edu.uco.ucotestapi.servicios.input.dto.response.RegistrarNotaAEstudianteResponse;

/** Lo que el controlador puede pedir para registrar una calificación (A-34). El {@code sub} viaja aparte (A-35). */
public interface RegistrarNotaAEstudianteInputPort {

	RegistrarNotaAEstudianteResponse registrar(RegistrarNotaAEstudianteRequest request,String cuentaDocente);
}
