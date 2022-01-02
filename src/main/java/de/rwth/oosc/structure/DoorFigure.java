package de.rwth.oosc.structure;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.RectangleFigure;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.geom.BezierPath.Node;
import org.jhotdraw.geom.Geom;

public class DoorFigure extends RectangleFigure {
	private static final double DOOR_THICKNESS = 0.4*WallFigure.WALL_THICKNESS;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DoorFigure() {
	}
	
	/**
	 * make the fill method from the super class unavailable
	 */
	@Override
	protected void drawFill(Graphics2D g) {
		Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
        double grow = AttributeKeys.getPerpendicularFillGrowth(this);
        Geom.grow(r, grow, grow);
        Rectangle2D.Double filledRec = new Rectangle2D.Double(r.getX(), r.getY()+r.getWidth()-WallFigure.WALL_THICKNESS, r.getWidth(),WallFigure.WALL_THICKNESS);
        g.fill(filledRec);
	}

	@Override
	public void setBounds(Double anchor, Double lead) {
		// TODO Auto-generated method stub
		super.setBounds(anchor, lead);
		//ensure square bounds
		rectangle.height = rectangle.width;
	}

	@Override
	protected void drawStroke(Graphics2D g) {
		//draw a square as the bounds, don't show it
		Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
	    double grow = AttributeKeys.getPerpendicularDrawGrowth(this);
	    Geom.grow(r, grow, grow);
//	    r.height = r.getWidth();
	    
	    // nodes
	    Node start = new Node(r.getX(), r.getY()+r.getWidth()-DOOR_THICKNESS);	    
	    BezierPath path = new BezierPath();
	    path.add(start);
	    path.lineTo(r.getX(),r.getY());
	    path.arcTo(r.getWidth(), r.getHeight(), 0d, false, true, r.getX() + r.getWidth(), r.getY() + r.getHeight());
	    path.lineTo(r.getX(), r.getY()+r.getHeight());
	    path.lineTo(r.getX(), r.getY()+r.getHeight()-DOOR_THICKNESS);
	    path.lineTo(r.getX()+ r.getWidth(), r.getY()+r.getHeight()-DOOR_THICKNESS);
	    g.draw(path);
	}
}
