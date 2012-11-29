package mereditor.interfaz.swt.figuras;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Insets;


public class CompartmentFigure extends Figure {

  public CompartmentFigure() {
    ToolbarLayout layout = new ToolbarLayout();
    layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
    layout.setStretchMinorAxis(false);
    layout.setSpacing(2);
    setLayoutManager(layout);
    setBorder(new CompartmentFigureBorder());
  }
    
  public class CompartmentFigureBorder extends AbstractBorder {
    public Insets getInsets(IFigure figure) {
      return new Insets(1,0,0,0);
    }
    @Override
    public void paint(IFigure figure, Graphics graphics, Insets insets) {
      graphics.drawLine(getPaintRectangle(figure, insets).getTopLeft(),
                        tempRect.getTopRight());
    }
	
  }
}