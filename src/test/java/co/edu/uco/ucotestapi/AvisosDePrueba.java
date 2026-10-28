package co.edu.uco.ucotestapi;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import co.edu.uco.ucotestapi.servicios.output.dto.AvisoDeRegistroDTO;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.AvisoNoEnviado;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.AvisosDeRegistro;

/** Stub del notification gateway (A-50): registra los avisos y puede fallar con el siguiente (C-16). */
public class AvisosDePrueba implements AvisosDeRegistro {

	private final List<AvisoDeRegistroDTO> enviados = new CopyOnWriteArrayList<>();
	private final AtomicBoolean fallarConElSiguiente = new AtomicBoolean();

	@Override
	public void informar(AvisoDeRegistroDTO aviso) {
		if (fallarConElSiguiente.getAndSet(false)) {
			throw new AvisoNoEnviado("notification gateway de prueba caído");
		}
		enviados.add(aviso);
	}

	public void fallarConElSiguiente() {
		fallarConElSiguiente.set(true);
	}

	public List<AvisoDeRegistroDTO> enviados() {
		return List.copyOf(enviados);
	}

	public void reiniciar() {
		enviados.clear();
		fallarConElSiguiente.set(false);
	}
}
