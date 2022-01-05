package de.rwth.oosc.figures.structure;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.LineFigure;

public class WallFigure extends LineFigure {
	public static final double WALL_THICKNESS = 10.0f;
	public WallFigure() {
		super();
		set(AttributeKeys.STROKE_WIDTH, WALL_THICKNESS);
	}
	
	private static final long serialVersionUID = 1L;
}


