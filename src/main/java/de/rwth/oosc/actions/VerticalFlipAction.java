package de.rwth.oosc.actions;

import java.awt.geom.AffineTransform;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;

public class VerticalFlipAction extends AbstractTransformAction {

	private static final long serialVersionUID = 1L;

	public VerticalFlipAction(DrawingEditor editor) {
		super(editor);
	}

	@Override
	protected void flip(Figure figure) {
		AffineTransform tx = new AffineTransform(1,0,0,-1,0, figure.getBounds().getHeight() + 2*figure.getBounds().getY());
		figure.transform(tx);
	}

}
