package de.rwth.oosc.actions;

import java.awt.event.ActionEvent;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.action.AbstractSelectedAction;

public abstract class AbstractTransformAction extends AbstractSelectedAction{

	public AbstractTransformAction(DrawingEditor editor) {
		super(editor);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		var figures = getView().getSelectedFigures();
		for(Figure figure: figures) {
			figure.willChange();
			transform(figure);
			figure.changed();
		}
	}
	
	protected abstract void transform(Figure figure);

}
