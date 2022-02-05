package de.rwth.oosc.figures.structure;


import java.awt.Graphics2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;

import de.rwth.oosc.figures.svg.SVGRectFigure;

public class WindowFigure extends SVGRectFigure {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void setBounds(Double anchor, Double lead) {
		lead.y = anchor.y + WallFigure.WALL_THICKNESS;
		super.setBounds(anchor, lead);
	}
	
	@Override
	protected void drawStroke(Graphics2D g) {
		Rectangle2D.Double myWindow = getBounds();
		double x = myWindow.getX();
		double y = myWindow.getY();
		double w = myWindow.getWidth();
		double h = WallFigure.WALL_THICKNESS;
		g.drawLine((int)x, (int)(y+h/2), (int)(x+w), (int)(y+h/2));
		setBounds(new Rectangle2D.Double(x,y,w,h));
		super.drawStroke(g);
		
	}
}
