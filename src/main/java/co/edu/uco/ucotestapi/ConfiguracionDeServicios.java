package co.edu.uco.ucotestapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.uco.ucotestapi.servicios.casosuso.impl.ConsultarClaseDelDocenteCasoUsoImpl;
import co.edu.uco.ucotestapi.servicios.casosuso.impl.RegistrarNotaAEstudianteCasoUsoImpl;
import co.edu.uco.ucotestapi.servicios.input.adapter.ConsultasDelDocenteInputAdapter;
import co.edu.uco.ucotestapi.servicios.input.adapter.RegistrarNotaAEstudianteInputAdapter;
import co.edu.uco.ucotestapi.servicios.output.adapter.ActualizacionEnVivoOutputAdapter;
import co.edu.uco.ucotestapi.servicios.output.adapter.AvisosDeRegistroOutputAdapter;
import co.edu.uco.ucotestapi.servicios.output.adapter.CatalogoDeMensajesOutputAdapter;
import co.edu.uco.ucotestapi.servicios.output.adapter.CatalogoDeParametrosOutputAdapter;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.GeneradorDeIds;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.UnidadDeTrabajo;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.DocenteRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.EstudianteRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.EvaluacionRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.GrupoRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.MatriculaRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.NotaEstudianteRepositorio;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.ActualizacionEnVivo;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.AvisosDeRegistro;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.CatalogoDeMensajes;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.CatalogoDeParametros;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.Reloj;

/**
 * Cableado de {@code servicios}: los casos de uso y los adapters de servicios no llevan anotaciones de Spring
 * (A-31), así que se crean aquí, en el componente de arranque (Martin, Clean Architecture · cap. 26 «The Main
 * Component»). Las implementaciones de los puertos sí son {@code @Component} en {@code infraestructura}.
 */
@Configuration(proxyBeanMethods = false)
public class ConfiguracionDeServicios {

	@Bean
	CatalogoDeParametrosOutputAdapter catalogoDeParametrosOutputAdapter(CatalogoDeParametros catalogo) {
		return new CatalogoDeParametrosOutputAdapter(catalogo);
	}

	@Bean
	CatalogoDeMensajesOutputAdapter catalogoDeMensajesOutputAdapter(CatalogoDeMensajes catalogo) {
		return new CatalogoDeMensajesOutputAdapter(catalogo);
	}

	@Bean
	AvisosDeRegistroOutputAdapter avisosDeRegistroOutputAdapter(AvisosDeRegistro avisos) {
		return new AvisosDeRegistroOutputAdapter(avisos);
	}

	@Bean
	ActualizacionEnVivoOutputAdapter actualizacionEnVivoOutputAdapter(ActualizacionEnVivo actualizacion) {
		return new ActualizacionEnVivoOutputAdapter(actualizacion);
	}

	@Bean
	RegistrarNotaAEstudianteCasoUsoImpl registrarNotaAEstudianteCasoUso(EstudianteRepositorio estudiantes,DocenteRepositorio docentes,
			GrupoRepositorio grupos, EvaluacionRepositorio evaluaciones,
			MatriculaRepositorio matriculas, NotaEstudianteRepositorio calificaciones, UnidadDeTrabajo unidadDeTrabajo,
			GeneradorDeIds ids, Reloj reloj, CatalogoDeParametrosOutputAdapter parametros,
			CatalogoDeMensajesOutputAdapter mensajes, AvisosDeRegistroOutputAdapter avisos,
			ActualizacionEnVivoOutputAdapter actualizacionEnVivo) {
		// PRA-1 (práctica de estudio de Juan): lógica del estudiante retirada a propósito; reescribirla siguiendo al docente.
		return new RegistrarNotaAEstudianteCasoUsoImpl(estudiantes,docentes, grupos, evaluaciones, matriculas,
				calificaciones, unidadDeTrabajo, ids, reloj, parametros, mensajes, avisos, actualizacionEnVivo);
	}

	@Bean
	ConsultarClaseDelDocenteCasoUsoImpl consultarClaseDelDocenteCasoUso(DocenteRepositorio docentes,
			GrupoRepositorio grupos, EvaluacionRepositorio evaluaciones, MatriculaRepositorio matriculas,
			NotaEstudianteRepositorio calificaciones) {
		return new ConsultarClaseDelDocenteCasoUsoImpl(docentes, grupos, evaluaciones, matriculas, calificaciones);
	}

	@Bean
	RegistrarNotaAEstudianteInputAdapter registrarNotaAEstudianteInputAdapter(
			RegistrarNotaAEstudianteCasoUsoImpl casoUso) {
		return new RegistrarNotaAEstudianteInputAdapter(casoUso);
	}

	@Bean
	ConsultasDelDocenteInputAdapter consultasDelDocenteInputAdapter(ConsultarClaseDelDocenteCasoUsoImpl casoUso) {
		return new ConsultasDelDocenteInputAdapter(casoUso);
	}
}
