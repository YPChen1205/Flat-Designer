package de.rwth.oosc.tool;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.tool.CreationTool;

public class FurnitureCreationTool extends CreationTool {

	private static final long serialVersionUID = 1L;

	public FurnitureCreationTool(Figure prototype) {
		super(prototype);
	}
	
	@Override
	protected Figure createFigure() {
		return prototype.clone();
	}

}
