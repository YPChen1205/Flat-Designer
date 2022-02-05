package de.chen.flatdesigner.figures;

import java.util.Collection;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.RotateHandle;

@Deprecated
public class FigureRotationProxy extends FigureProxy {

	private static final long serialVersionUID = 1L;

	public FigureRotationProxy(Figure figure) {
		super(figure);
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public Collection<Handle> createHandles(int detailLevel) {
		var handles = super.createHandles(detailLevel);
		
		switch(detailLevel)	{
		case 0:
			handles.add(new RotateHandle(figure));
			break;
		}
		return handles;
	}
	
	@Override
	public Figure clone() {
		return new FigureRotationProxy(figure.clone());
	}

}
