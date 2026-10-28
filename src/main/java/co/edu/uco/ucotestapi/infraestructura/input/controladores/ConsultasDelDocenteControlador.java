package co.edu.uco.ucotestapi.infraestructura.input.controladores;

import java.util.Objects;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.ucotestapi.servicios.input.dto.response.CalificacionResponse;
import co.edu.uco.ucotestapi.servicios.input.dto.response.EstudianteDelGrupoResponse;
import co.edu.uco.ucotestapi.servicios.input.dto.response.EvaluacionResponse;
import co.edu.uco.ucotestapi.servicios.input.dto.response.GrupoResponse;
import co.edu.uco.ucotestapi.servicios.input.dto.response.PaginaResponse;
import co.edu.uco.ucotestapi.servicios.input.ports.ConsultarCalificacionInputPort;
import co.edu.uco.ucotestapi.servicios.input.ports.ConsultarEstudiantesDelGrupoInputPort;
import co.edu.uco.ucotestapi.servicios.input.ports.ConsultarEvaluacionesDelGrupoInputPort;
import co.edu.uco.ucotestapi.servicios.input.ports.ConsultarGruposDelDocenteInputPort;

/** Las consultas de lectura que el formulario necesita (A-24, Q-13) y el destino del {@code Location} (Q-15). */
@RestController
public class ConsultasDelDocenteControlador {

	private final ConsultarGruposDelDocenteInputPort grupos;
	private final ConsultarEvaluacionesDelGrupoInputPort evaluaciones;
	private final ConsultarEstudiantesDelGrupoInputPort estudiantes;
	private final ConsultarCalificacionInputPort calificacion;

	public ConsultasDelDocenteControlador(ConsultarGruposDelDocenteInputPort grupos,
			ConsultarEvaluacionesDelGrupoInputPort evaluaciones, ConsultarEstudiantesDelGrupoInputPort estudiantes,
			ConsultarCalificacionInputPort calificacion) {
		this.grupos = Objects.requireNonNull(grupos, "grupos");
		this.evaluaciones = Objects.requireNonNull(evaluaciones, "evaluaciones");
		this.estudiantes = Objects.requireNonNull(estudiantes, "estudiantes");
		this.calificacion = Objects.requireNonNull(calificacion, "calificacion");
	}

	@GetMapping("/api/v1/docentes/yo/grupos")
	public PaginaResponse<GrupoResponse> grupos(@AuthenticationPrincipal Jwt token,
			@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamano) {
		return grupos.gruposDelDocente(token.getSubject(), pagina, tamano);
	}

	@GetMapping("/api/v1/grupos/{grupoId}/notas")
	public PaginaResponse<EvaluacionResponse> evaluaciones(@AuthenticationPrincipal Jwt token,
			@PathVariable UUID grupoId, @RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "20") int tamano) {
		return evaluaciones.evaluacionesDelGrupo(token.getSubject(), grupoId, pagina, tamano);
	}

	@GetMapping("/api/v1/grupos/{grupoId}/estudiantes")
	public PaginaResponse<EstudianteDelGrupoResponse> estudiantes(@AuthenticationPrincipal Jwt token,
			@PathVariable UUID grupoId, @RequestParam UUID notaId, @RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "20") int tamano) {
		return estudiantes.estudiantesDelGrupo(token.getSubject(), grupoId, notaId, pagina, tamano);
	}

	@GetMapping("/api/v1/grupos/{grupoId}/notas/{notaId}/calificaciones/{id}")
	public CalificacionResponse calificacion(@AuthenticationPrincipal Jwt token, @PathVariable UUID grupoId,
			@PathVariable UUID notaId, @PathVariable UUID id) {
		return calificacion.calificacion(token.getSubject(), grupoId, notaId, id);
	}
}
