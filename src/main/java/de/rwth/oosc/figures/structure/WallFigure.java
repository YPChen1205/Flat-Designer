package de.rwth.oosc.figures.structure;

import org.jhotdraw.draw.AttributeKeys;

import de.rwth.oosc.figures.svg.SVGPathFigure;

public class WallFigure extends SVGPathFigure {
	public static final double WALL_THICKNESS = 10.0f;
	public WallFigure() {
		super();
		set(AttributeKeys.STROKE_WIDTH, WALL_THICKNESS);
	}
	
	private static final long serialVersionUID = 1L;
}


