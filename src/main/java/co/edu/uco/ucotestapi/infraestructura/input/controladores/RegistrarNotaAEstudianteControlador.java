package co.edu.uco.ucotestapi.infraestructura.input.controladores;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import co.edu.uco.ucotestapi.servicios.input.dto.request.RegistrarNotaAEstudianteRequest;
import co.edu.uco.ucotestapi.servicios.input.dto.response.RegistrarNotaAEstudianteResponse;
import co.edu.uco.ucotestapi.servicios.input.ports.RegistrarNotaAEstudianteInputPort;

/** {@code POST /api/v1/grupos/{grupoId}/notas/{notaId}/calificaciones} (A-18). Solo habla HTTP. */
@RestController
@RequestMapping("/api/v1/grupos/{grupoId}/notas/{notaId}/calificaciones")
public class RegistrarNotaAEstudianteControlador {

	/**
	 * Cuerpo leído de forma estricta (A-26, A-60): campos obligatorios; {@code null}, texto o decimal en
	 * {@code centesimas} se rechazan con 400 antes de llegar al puerto.
	 */
	// PRA-1 (práctica de estudio de Juan): lógica del estudiante retirada a propósito; reescribirla siguiendo al docente.
	public record Cuerpo(
			@JsonProperty(required = true) Integer centesimas, 
			@JsonProperty(required = true) UUID estudianteId
			
			) {
		

	}

	private final RegistrarNotaAEstudianteInputPort puerto;

	public RegistrarNotaAEstudianteControlador(RegistrarNotaAEstudianteInputPort puerto) {
		this.puerto = Objects.requireNonNull(puerto, "puerto");
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RegistrarNotaAEstudianteResponse> registrar(@PathVariable UUID grupoId,
			@PathVariable UUID notaId, @RequestBody Cuerpo cuerpo, @AuthenticationPrincipal Jwt token) {
		RegistrarNotaAEstudianteResponse respuesta = puerto.registrar(
				new RegistrarNotaAEstudianteRequest(grupoId, notaId,cuerpo.estudianteId(), cuerpo.centesimas() ),
				token.getSubject());
		// Relativa a propósito: detrás del gateway el host interno no sirve al cliente (RFC 9110 §10.2.2).
		URI ubicacion = UriComponentsBuilder
				.fromPath("/api/v1/grupos/{grupoId}/notas/{notaId}/calificaciones/{id}")
				.buildAndExpand(grupoId, notaId, respuesta.id()).toUri();
		return ResponseEntity.created(ubicacion).body(respuesta);
	}
}
