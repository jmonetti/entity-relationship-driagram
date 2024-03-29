package mereditor.interfaz.swt.figuras;

import mereditor.interfaz.swt.listeners.ArrastreControlador;
import mereditor.modelo.Atributo;
import mereditor.representacion.PList;


import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

import org.eclipse.draw2d.geometry.Point;


public abstract class AtributoFigure extends Figura<Atributo> {

	public AtributoFigure(Atributo componente) {
		super(componente);
		
	}

	@Override
	protected void init() {
		this.lblName = new Label();
		this.lblName.setFont(this.getFont());
		this.lblName.setText(this.getTextoLabel());
		this.lblName.setBounds(this.lblName.getTextBounds().getTranslated(0, -10));

	}

	@Override
	public void setParent(IFigure parent) {
		super.setParent(parent);

		if (this.getParent() != null && !this.componente.esCompuesto())
			this.getParent().add(this.lblName, 0);
	}
	

	/**
	 * Devuelve el texto que se debe mostrar en el diagrama.
	 * 
	 * @return
	 */
	protected String getTextoLabel() {
		String texto = this.componente.getNombre();
		return texto;
	}

	@Override
	public void setRepresentacion(PList repr) {
		super.setRepresentacion(repr);

		if (this.lblName != null && repr.get("Label") != null) {
			PList labelRepr = repr.<PList> get("Label");
			this.lblName.setLocation(new Point(labelRepr.<Integer> get("x"),
					labelRepr.<Integer> get("y")));
		}
	}

	@Override
	public PList getRepresentacion() {
		PList repr = super.getRepresentacion();
		if (this.lblName != null) {
			PList labelRepr = new PList();
			repr.set("Label", labelRepr);
			labelRepr.set("x", this.lblName.getLocation().x);
			labelRepr.set("y", this.lblName.getLocation().y);
		}

		return repr;
	}

	@Override
	public abstract void actualizar();

	public abstract void conectarAtributo(Figura<Atributo> figura);
}
