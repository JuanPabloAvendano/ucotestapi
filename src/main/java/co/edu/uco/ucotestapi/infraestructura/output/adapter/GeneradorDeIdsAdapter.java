package co.edu.uco.ucotestapi.infraestructura.output.adapter;

import org.springframework.stereotype.Component;

import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudianteId;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.GeneradorDeIds;
import co.edu.uco.ucotestapi.transversal.UuidV7;

@Component
public class GeneradorDeIdsAdapter implements GeneradorDeIds {

	@Override
	public NotaEstudianteId nuevo() {
		return new NotaEstudianteId(UuidV7.nuevo());
	}
}
