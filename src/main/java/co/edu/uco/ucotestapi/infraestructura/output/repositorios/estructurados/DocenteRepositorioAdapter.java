package co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados;

import java.util.Objects;

import org.springframework.stereotype.Repository;

import co.edu.uco.ucotestapi.dominio.cuenta.CuentaId;
import co.edu.uco.ucotestapi.dominio.docente.Docente;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.jpa.DocenteJPARepositorio;
import co.edu.uco.ucotestapi.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades.DocenteRepositorio;

@Repository
public class DocenteRepositorioAdapter implements DocenteRepositorio {

	private final DocenteJPARepositorio jpa;
	private final EntidadMapper mapper;

	public DocenteRepositorioAdapter(DocenteJPARepositorio jpa, EntidadMapper mapper) {
		this.jpa = Objects.requireNonNull(jpa, "jpa");
		this.mapper = Objects.requireNonNull(mapper, "mapper");
	}

	@Override
	public Docente porCuenta(CuentaId cuenta) {
		return jpa.findByCuentaId(cuenta.valor()).map(mapper::aDominio).orElse(Docente.noExiste());
	}
}
