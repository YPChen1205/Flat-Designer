package de.rwth.oosc.structure;


import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;

import org.jhotdraw.draw.RectangleFigure;

public class WindowFigure extends RectangleFigure {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void setBounds(Double anchor, Double lead) {
		super.setBounds(anchor, lead);
		rectangle.height = WallFigure.WALL_THICKNESS;
	}
	
	@Override
	protected void drawStroke(Graphics2D g) {
		// TODO Auto-generated method stub
		Rectangle2D.Double myWindow = getBounds();
		double x = myWindow.getX();
		double y = myWindow.getY();
		double w = myWindow.getWidth();
		double h = WallFigure.WALL_THICKNESS;
		g.drawLine((int)x, (int)(y+h/2), (int)(x+w), (int)(y+h/2));
		//g.setStroke(new BasicStroke());
		setBounds(new Rectangle2D.Double(x,y,w,h));
		super.drawStroke(g);
		
	}
}
