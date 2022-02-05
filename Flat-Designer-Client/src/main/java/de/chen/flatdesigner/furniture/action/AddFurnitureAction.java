package de.chen.flatdesigner.furniture.action;

import java.awt.event.ActionEvent;
import java.util.Set;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.action.AbstractSelectedAction;

import de.chen.flatdesigner.dialog.FurnitureSaveDialog;
import de.chen.flatdesigner.figures.svg.SVGGroupFigure;
import de.chen.flatdesigner.furniture.CustomFurniture;
import de.chen.flatdesigner.furniture.FurnitureModel;

public class AddFurnitureAction extends AbstractSelectedAction {

	private static final long serialVersionUID = 1L;

	public static final String ID = "FurnitureSaveAction.ID";

	public AddFurnitureAction(DrawingEditor editor) {
		super(editor);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SVGGroupFigure gf = new SVGGroupFigure();
		Set<Figure> figureSet = getEditor().getActiveView().getSelectedFigures();
		Object[] figureArray = figureSet.toArray();
		if (figureSet.size() == 1 && figureArray[0] instanceof GroupFigure) {
			gf = (SVGGroupFigure) figureArray[0];
		} else {
			gf.addAll(figureSet);
		}
		

		FurnitureSaveDialog dialog = new FurnitureSaveDialog(getView().getComponent(), FurnitureModel.getInstance().getCatalogues().toArray(new String[0]));
		
		if (dialog.isApproved()) {
			String name = dialog.getTypedName();
			String catalogue = dialog.getSelectedCatalogue();
			try {
				CustomFurniture furniture = new CustomFurniture(name, gf);
				FurnitureModel.getInstance().addFurniture(catalogue, furniture);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

}
