package mereditor.interfaz.swt.figuras;


import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

import mereditor.modelo.Atributo;
import mereditor.modelo.Entidad;
import mereditor.representacion.PList;


public class EntidadLogicaFigure extends EntidadFigure {

	public static Color classColor = new Color(null,255,255,206);
	private CompartmentFigure attributeFigure;


	//protected PanelEntidad panel;
	
	/*public PanelEntidad getPanel() {
		return panel;
	}

	public void setPanel(PanelEntidad panel) {
		this.panel = panel;
	}*/

	public EntidadLogicaFigure(Entidad entidad) {
		super(entidad);
		//this.setRepresentacion(EstilosFiguras.get(Entidad.class, this.componente.getTipo()));
	}
	
	@Override
	protected void init() {
		this.setBorder(new LineBorder(this.lineColor, this.lineWidth, this.lineStyle));
		this.setBackgroundColor(this.backColor);
		ToolbarLayout layout = new ToolbarLayout();
	    setLayoutManager(layout);	
	    setBorder(new LineBorder(ColorConstants.black,1));
	    setBackgroundColor(classColor);
	    setOpaque(true);
	    
		this.lblName = new Label();
		this.lblName.setFont(this.font);
		this.add(lblName, BorderLayout.TOP);
	    add(this.lblName);
	    attributeFigure = new CompartmentFigure();
	    add(attributeFigure);
		
	}

	@Override
	public void actualizar() {
		
	    this.lblName.setText(this.componente.getNombre());
	    
		
		
	}
	@Override
	public Connection conectarAtributo(Figura<Atributo> figura) {
		this.attributeFigure.add(new Label(figura.lblName.getText()));
		return null;
		
	}
	
	public void ConectarEntidad (EntidadLogicaFigure Entidad){
		PolylineConnection c = new PolylineConnection();
		ChopboxAnchor sourceAnchor = new ChopboxAnchor(this);
		ChopboxAnchor targetAnchor = new ChopboxAnchor(Entidad);
		c.setSourceAnchor(sourceAnchor);
		c.setTargetAnchor(targetAnchor);
		PolygonDecoration decoration = new PolygonDecoration();
		PointList decorationPointList = new PointList();
		decorationPointList.addPoint(0,0);
		decorationPointList.addPoint(-2,2);
		decorationPointList.addPoint(-4,0);
		decorationPointList.addPoint(-2,-2);
		decoration.setTemplate(decorationPointList);
		c.setSourceDecoration(decoration);
	}
	
	public CompartmentFigure getAttributeFigure() {
		return attributeFigure;
	}

	public void setAttributeFigure(CompartmentFigure attributeFigure) {
		this.attributeFigure = attributeFigure;
	}

	@Override
	public void setRepresentacion(PList repr) {
		if (repr != null) {
			if (repr.<PList> get("Posicion") != null) {
				Rectangle rect = new Rectangle(repr.<PList> get("Posicion").<Integer> get("x"),
						repr.<PList> get("Posicion").<Integer> get("y"), repr.<PList> get(
								"Dimension").<Integer> get("ancho"), repr.<PList> get("Dimension")
								.<Integer> get("alto"));

				this.setBounds(rect);
			}

			if (repr.<PList> get("ColorFondo") != null) {
				this.backColor = new Color(null, repr.<PList> get("ColorFondo").<Integer> get("r"),
						repr.<PList> get("ColorFondo").<Integer> get("g"), repr.<PList> get(
								"ColorFondo").<Integer> get("b"));

				this.applyBackgroundColor();
			}

			if (repr.<PList> get("ColorLinea") != null) {
				this.lineColor = new Color(null, repr.<PList> get("ColorLinea").<Integer> get("r"),
						repr.<PList> get("ColorLinea").<Integer> get("g"), repr.<PList> get(
								"ColorLinea").<Integer> get("b"));
			}

			if (repr.<PList> get("AnchoLinea") != null) {
				this.lineWidth = repr.<Integer> get("AnchoLinea");
			}

			if (repr.<Integer> get("EstiloLinea") != null) {
				this.lineStyle = repr.<Integer> get("EstiloLinea");
			}

			this.applyLineStyle();
		}
	}

}
