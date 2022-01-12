package de.rwth.oosc.tool;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.text.View;

import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.tool.DelegationSelectionTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RotationDelegationSelectionTool extends DelegationSelectionTool {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(RotationDelegationSelectionTool.class);
	
	private boolean rotating = false;
	private Cursor rotationCursor;
	
	public RotationDelegationSelectionTool(Collection<Action> drawingActions, Collection<Action> selectionActions) {
		super(drawingActions, selectionActions);
	}
	
	@Override
	public void mouseMoved(MouseEvent evt) {
		super.mouseMoved(evt);
		
		Set<Figure> figures = getView().getSelectedFigures();
		for (Figure f : figures) {
			Rectangle2D.Double selectionBounds = (Rectangle2D.Double) f.getBounds().clone();
			double padding = 0.1 * Math.min(f.getBounds().height, f.getBounds().width);
			selectionBounds.x += padding;
			selectionBounds.y += padding;
			selectionBounds.width -= 2*padding;
			selectionBounds.height -= 2*padding;
			
			if (!selectionBounds.contains(evt.getX(), evt.getY()) && f.getBounds().contains(evt.getX(), evt.getY())) {
				logger.debug("in rotation bounds");
				rotating = true;
			} else if (f.getBounds().contains(evt.getX(), evt.getY())) {
				logger.debug("mouse over figure " + f);
				rotating = false;
			} else {
				rotating = false;
			}
		}
	}

	@Override
	public void updateCursor(DrawingView view, Point p) {
		super.updateCursor(view, p);
		
		if (rotationCursor == null) {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			BufferedImage img;
			try {
				img = ImageIO.read(RotationDelegationSelectionTool.class.getResource("/de/rwth/oosc/flatdesigner/icons/rotate.png"));
				rotationCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (rotating) {
			view.setCursor(rotationCursor);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent evt) {
		// TODO Auto-generated method stub
		super.mousePressed(evt);
	}	
}
