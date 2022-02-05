package de.rwth.oosc.figures.structure;

import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.Set;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.geom.BezierPath;

import de.rwth.oosc.figures.svg.SVGPathFigure;

public class WallFigure extends SVGPathFigure {
	public static final double WALL_THICKNESS = 10.0f;
	
	public WallFigure() {
		super();
		set(AttributeKeys.STROKE_WIDTH, WALL_THICKNESS);
	}
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * This method divides the WallFigure into lines and returns a set of the lines.
	 * @return all lines to construct the wall
	 */
	public Set<Line2D.Double> getLines() {
		Set<Line2D.Double> lines = new HashSet<>();
		if (getChildCount() == 0) {
			return lines;
		}
		
		BezierPath path = getChild(0).getBezierPath();
		for (int i = 0; i<path.size()-1; i++) {
			BezierPath.Node start = path.get(i);
			BezierPath.Node end = path.get(i+1);
			
			Line2D.Double line = new Line2D.Double(start.x[0], start.y[0], end.x[0], end.y[0]);
			
			lines.add(line);
		}
		
		return lines;
	}
}


