package mereditor.interfaz.swt.figuras;

import mereditor.interfaz.swt.listeners.ArrastreControlador;
import mereditor.modelo.Atributo;
import mereditor.representacion.PList;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

public class AtributoLogicoFigure extends AtributoFigure {
	private Color identificadorBackColor = new Color(null, 0, 0, 0);

	public AtributoLogicoFigure(Atributo componente) {
		super(componente);
	}

	@Override
	protected void init() {
		super.init();
		// Permitir que el label se mueva
		new ArrastreControlador(this.lblName);

	}



	/**
	 * Conecta este atributo con uno de sus atributos hijos
	 * 
	 * @param figura
	 */


	@Override
	public void setRepresentacion(PList repr) {
		/*super.setRepresentacion(repr);

		if (this.lblName != null && repr.get("Label") != null) {
			PList labelRepr = repr.<PList> get("Label");
			this.lblName.setLocation(new Point(labelRepr.<Integer> get("x"),
					labelRepr.<Integer> get("y")));
		}*/
	}

	@Override
	public PList getRepresentacion() {
		/*PList repr = super.getRepresentacion();
		if (this.lblName != null) {
			PList labelRepr = new PList();
			repr.set("Label", labelRepr);
			labelRepr.set("x", this.lblName.getLocation().x);
			labelRepr.set("y", this.lblName.getLocation().y);
		}

		return repr;*/
		return null;
	}

	@Override
	public void actualizar() {
		this.lblName.setText(this.getTextoLabel());
		this.lblName.setBounds(this.lblName.getTextBounds());
		
	}

	@Override
	public void conectarAtributo(Figura<Atributo> figura) {
		// TODO Auto-generated method stub
		
	}
}
