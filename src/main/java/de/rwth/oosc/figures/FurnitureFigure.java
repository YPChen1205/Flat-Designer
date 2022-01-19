package de.rwth.oosc.figures;

import org.jhotdraw.draw.AbstractCompositeFigure;
import org.jhotdraw.draw.GroupFigure;

public class FurnitureFigure extends AbstractCompositeFigure {

	private static final long serialVersionUID = 1L;
	
	public FurnitureFigure(GroupFigure figure) {
		super();
		add(figure);
	}
}
