package mereditor.interfaz.swt.figuras;

import java.util.List;

import org.eclipse.draw2d.Connection;

import mereditor.modelo.Atributo;
import mereditor.modelo.Entidad;

public class EntidadFigure extends Figura<Entidad> {

	public EntidadFigure(Entidad componente) {
		super(componente);
		
	}
	protected void init() {
		super.init();

		this.actualizar();
	}

	@Override
	public void actualizar() {
		
	}
	public Connection conectarAtributo(Figura<Atributo> figura) {
		return null;
	}

	public Connection conectarEntidad(String id, Connection conexionEntidad) {
		return conexionEntidad;
	
	}
	
	public void conectarIdentificador(List<Connection> conexiones) {
	}
	
	
}
