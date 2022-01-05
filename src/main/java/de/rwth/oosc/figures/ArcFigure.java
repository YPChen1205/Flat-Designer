package de.rwth.oosc.figures;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.jhotdraw.draw.BezierFigure;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.LineFigure;
import org.jhotdraw.draw.handle.BezierControlPointHandle;
import org.jhotdraw.draw.handle.BezierNodeHandle;
import org.jhotdraw.draw.handle.BezierOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.geom.BezierPath.Node;

import de.rwth.oosc.figures.handle.ArcNodeHandle;

public class ArcFigure extends LineFigure {

	private static final long serialVersionUID = 1L;
	
	
	public ArcFigure() {
		super();
	}
	
	@Override
	public void setBounds(Double anchor, Double lead) {
		// clear all points before
		BezierPath newPath = new BezierPath();
		System.out.println(newPath);
		newPath.add(anchor);
		System.out.println(newPath);
		double deltaX = lead.x - anchor.x;
		double deltaY = lead.y - anchor.y;
		
		double length = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
		double r = length / 2;
		
		newPath.arcTo(r, r, Math.atan2(deltaY, deltaX),  false, true, lead.x, lead.y);
		System.out.println(newPath);
		if (newPath.size() > 2) {
			newPath.remove(1);
		}
		setBezierPath(newPath);
		
		invalidate();
	}
	
	public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel) {
            case -1 : // Mouse hover handles
                handles.add(new BezierOutlineHandle(this, true));
                break;
            case 0 :
                handles.add(new BezierOutlineHandle(this));
                for (int i=0, n = path.size(); i < n; i++) {
                	ArcNodeHandle handle = new ArcNodeHandle(this, i);
                    handles.add(handle);
                    path.get(i).setMask(i + 2);
                    handles.addAll(handle.createSecondaryHandles());
                }
                break;
        }
        return handles;
    }

}
