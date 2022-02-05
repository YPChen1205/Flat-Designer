package de.chen.flatdesigner.figures;

import java.awt.geom.Point2D.Double;
import java.util.Collection;
import java.util.LinkedList;

import org.jhotdraw.draw.LineFigure;
import org.jhotdraw.draw.handle.BezierOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.geom.BezierPath;

import de.chen.flatdesigner.figures.handle.ArcNodeHandle;

@Deprecated
public class ArcFigure extends LineFigure {

	private static final long serialVersionUID = 1L;
	
	
	public ArcFigure() {
		super();
	}
	
	@Override
	public void setBounds(Double anchor, Double lead) {
		// clear all points before
		BezierPath newPath = new BezierPath();
		newPath.add(anchor);
		double deltaX = lead.x - anchor.x;
		double deltaY = lead.y - anchor.y;
		
		double length = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
		double r = length / 2;
		
		newPath.arcTo(r, r, Math.atan2(deltaY, deltaX),  false, true, lead.x, lead.y);
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
             default:
        }
        return handles;
    }

}
