package de.chen.flatdesigner.furniture.action;

import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.action.UngroupAction;

import de.chen.flatdesigner.figures.svg.SVGGroupFigure;

public class SVGUngroupAction extends UngroupAction {

	private static final long serialVersionUID = 1L;
	
	private CompositeFigure prototype;

	public SVGUngroupAction(DrawingEditor editor) {
		this(editor, new SVGGroupFigure());
	}
	
	public SVGUngroupAction(DrawingEditor editor, CompositeFigure prototype) {
		super(editor, prototype);
		this.prototype = prototype;
	}

	@Override
	protected boolean canUngroup() {
		boolean canUngroup = super.canUngroup();
		return canUngroup || (getView() != null
                && getView().getSelectionCount() == 1
                && prototype != null
                && prototype.getClass().isInstance(getView().getSelectedFigures().iterator().next()));
	}
}
