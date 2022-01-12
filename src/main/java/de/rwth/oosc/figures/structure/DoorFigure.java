package de.rwth.oosc.figures.structure;

import java.awt.geom.Point2D.Double;

import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.geom.BezierPath.Node;

import de.rwth.oosc.figures.svg.AbstractCustomSVGFigure;

public class DoorFigure extends AbstractCustomSVGFigure {
	private static final double DOOR_THICKNESS = 0.4*WallFigure.WALL_THICKNESS;

	private static final long serialVersionUID = 1L;
	
	@Override
	public void setBounds(Double anchor, Double lead) {
		double x = Math.min(anchor.x, lead.x);
		double y = Math.min(anchor.y, lead.y);
        double width = Math.max(0.1, Math.abs(lead.x - anchor.x));
        computePath(x, y, width, width);
        invalidate();
	}

	@Override
	protected void computePath(double x, double y, double width, double height) {
		Node start = new Node(x, y+width-DOOR_THICKNESS);	    
	    BezierPath path = new BezierPath();
	    path.add(start);
	    path.lineTo(x,y);
	    path.arcTo(width, height, 0d, false, true, x + width, y + height);
	    path.lineTo(x, y+height);
	    path.lineTo(x, y+height-DOOR_THICKNESS);
	    path.lineTo(x+ width, y+height-DOOR_THICKNESS);
	    figurePath = path.toGeneralPath();
	}
}
