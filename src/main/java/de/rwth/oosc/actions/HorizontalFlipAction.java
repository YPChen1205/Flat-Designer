package de.rwth.oosc.actions;

import java.awt.geom.AffineTransform;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;

public class HorizontalFlipAction extends AbstractTransformAction {

	private static final long serialVersionUID = 1L;

	public HorizontalFlipAction(DrawingEditor editor) {
		super(editor);
	}

	@Override
	protected void flip(Figure figure) {
		var originalBonds = figure.getBounds();
		AffineTransform tx = new AffineTransform(-1,0,0,1,originalBonds.getWidth()+2*originalBonds.getX(),0);
		System.out.println(figure.getBounds());
		figure.transform(tx);
		
		System.out.println(figure.getBounds());
	}

}
