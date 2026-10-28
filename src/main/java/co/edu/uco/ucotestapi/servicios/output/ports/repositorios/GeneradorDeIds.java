package co.edu.uco.ucotestapi.servicios.output.ports.repositorios;

import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudianteId;

/** Entrega el id UUID v7 de una calificación nueva (A-4, A-51). */
public interface GeneradorDeIds {

	NotaEstudianteId nuevo();
}
