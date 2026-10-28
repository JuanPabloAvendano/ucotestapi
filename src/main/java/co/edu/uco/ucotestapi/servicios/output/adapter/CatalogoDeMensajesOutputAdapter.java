package co.edu.uco.ucotestapi.servicios.output.adapter;

import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.mensajes.CodigoMensaje;
import co.edu.uco.ucotestapi.dominio.mensajes.Mensaje;
import co.edu.uco.ucotestapi.servicios.output.ports.serviciosexternos.CatalogoDeMensajes;

/** Traduce el {@code MensajeDTO} del catálogo a {@link Mensaje} del dominio (A-58). */
public class CatalogoDeMensajesOutputAdapter {

	private final CatalogoDeMensajes catalogo;

	public CatalogoDeMensajesOutputAdapter(CatalogoDeMensajes catalogo) {
		this.catalogo = Objects.requireNonNull(catalogo, "catalogo");
	}

	public Mensaje porCodigo(CodigoMensaje codigo) {
		return new Mensaje(codigo, catalogo.porCodigo(codigo.valor()).texto());
	}
}
