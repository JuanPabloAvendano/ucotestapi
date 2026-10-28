package co.edu.uco.ucotestapi.servicios.input.adapter;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudiante;
import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudianteId;
import co.edu.uco.ucotestapi.dominio.cuenta.CuentaId;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;
import co.edu.uco.ucotestapi.servicios.casosuso.ConsultarClaseDelDocenteCasoUso;
import co.edu.uco.ucotestapi.servicios.input.dto.response.CalificacionResponse;
import co.edu.uco.ucotestapi.servicios.input.dto.response.EstudianteDelGrupoResponse;
import co.edu.uco.ucotestapi.servicios.input.dto.response.EvaluacionResponse;
import co.edu.uco.ucotestapi.servicios.input.dto.response.GrupoResponse;
import co.edu.uco.ucotestapi.servicios.input.dto.response.PaginaResponse;
import co.edu.uco.ucotestapi.servicios.input.ports.ConsultarCalificacionInputPort;
import co.edu.uco.ucotestapi.servicios.input.ports.ConsultarEstudiantesDelGrupoInputPort;
import co.edu.uco.ucotestapi.servicios.input.ports.ConsultarEvaluacionesDelGrupoInputPort;
import co.edu.uco.ucotestapi.servicios.input.ports.ConsultarGruposDelDocenteInputPort;
import co.edu.uco.ucotestapi.transversal.Pagina;

/** Un solo adapter para los cuatro puertos de consulta: son mapeos sin lógica (A-24). */
public class ConsultasDelDocenteInputAdapter implements ConsultarGruposDelDocenteInputPort,
		ConsultarEvaluacionesDelGrupoInputPort, ConsultarEstudiantesDelGrupoInputPort, ConsultarCalificacionInputPort {

	private static final int TAMANO_MAXIMO = 100;

	private final ConsultarClaseDelDocenteCasoUso casoUso;

	public ConsultasDelDocenteInputAdapter(ConsultarClaseDelDocenteCasoUso casoUso) {
		this.casoUso = Objects.requireNonNull(casoUso, "casoUso");
	}

	@Override
	public PaginaResponse<GrupoResponse> gruposDelDocente(String cuentaDocente, int pagina, int tamano) {
		return respuesta(casoUso.gruposDelDocente(new CuentaId(cuentaDocente), paginaValida(pagina),
				tamanoValido(tamano)), grupo -> new GrupoResponse(grupo.id().valor(), grupo.nombre(),
						grupo.periodo().inicio(), grupo.periodo().fin()));
	}

	@Override
	public PaginaResponse<EvaluacionResponse> evaluacionesDelGrupo(String cuentaDocente, UUID grupoId, int pagina,
			int tamano) {
		return respuesta(casoUso.evaluacionesDelGrupo(new CuentaId(cuentaDocente), new GrupoId(grupoId),
				paginaValida(pagina), tamanoValido(tamano)),
				nota -> new EvaluacionResponse(nota.id().valor(), nota.descripcion()));
	}

	@Override
	public PaginaResponse<EstudianteDelGrupoResponse> estudiantesDelGrupo(String cuentaDocente, UUID grupoId,
			UUID notaId, int pagina, int tamano) {
		return respuesta(casoUso.estudiantesDelGrupo(new CuentaId(cuentaDocente), new GrupoId(grupoId),
				new NotaId(notaId), paginaValida(pagina), tamanoValido(tamano)),
				fila -> new EstudianteDelGrupoResponse(fila.id().valor(), fila.nombre(), fila.tieneNota()));
	}

	@Override
	public CalificacionResponse calificacion(String cuentaDocente, UUID grupoId, UUID notaId, UUID id) {
		NotaEstudiante calificacion = casoUso.calificacion(new CuentaId(cuentaDocente), new GrupoId(grupoId),
				new NotaId(notaId), new NotaEstudianteId(id));
		return new CalificacionResponse(calificacion.id().valor(), calificacion.estudiante().valor(),
				calificacion.calificacion().centesimas());
	}

	private static <T, R> PaginaResponse<R> respuesta(Pagina<T> pagina, Function<T, R> conversion) {
		Pagina<R> convertida = pagina.mapear(conversion);
		return new PaginaResponse<>(convertida.contenido(), convertida.pagina(), convertida.tamano(),
				convertida.total());
	}

	private static int paginaValida(int pagina) {
		return Math.max(0, pagina);
	}

	private static int tamanoValido(int tamano) {
		return Math.clamp(tamano, 1, TAMANO_MAXIMO);
	}
}
