package co.edu.uco.ucotestapi.servicios.input.adapter;

import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.calificacion.Calificacion;
import co.edu.uco.ucotestapi.dominio.cuenta.CuentaId;
import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;
import co.edu.uco.ucotestapi.servicios.casosuso.RegistrarNotaAEstudianteCasoUso;
import co.edu.uco.ucotestapi.servicios.casosuso.ResultadoDelRegistro;
import co.edu.uco.ucotestapi.servicios.input.dto.request.RegistrarNotaAEstudianteRequest;
import co.edu.uco.ucotestapi.servicios.input.dto.response.RegistrarNotaAEstudianteResponse;
import co.edu.uco.ucotestapi.servicios.input.ports.RegistrarNotaAEstudianteInputPort;

/** DTO → objetos de valor → caso de uso → DTO (A-34, A-35). No carga nada: no tiene puertos de salida (A-2). */
public class RegistrarNotaAEstudianteInputAdapter implements RegistrarNotaAEstudianteInputPort {

	private final RegistrarNotaAEstudianteCasoUso casoUso;

	public RegistrarNotaAEstudianteInputAdapter(RegistrarNotaAEstudianteCasoUso casoUso) {
		this.casoUso = Objects.requireNonNull(casoUso, "casoUso");
	}

	@Override
	public RegistrarNotaAEstudianteResponse registrar(RegistrarNotaAEstudianteRequest request ,String cuentaDocente) {
		ResultadoDelRegistro resultado = casoUso.ejecutar(
				new EstudianteId(request.estudianteId()),
				new CuentaId(cuentaDocente),
				new GrupoId(request.grupoId()),
				new NotaId(request.notaId()), 
				new Calificacion(request.centesimas()));
		// PRA-1 (práctica de estudio de Juan): lógica del estudiante retirada a propósito; reescribirla siguiendo al docente.

		return new RegistrarNotaAEstudianteResponse(resultado.nota().id().valor(),
				resultado.nota().calificacion().centesimas(), resultado.exito().texto());
	}
}
