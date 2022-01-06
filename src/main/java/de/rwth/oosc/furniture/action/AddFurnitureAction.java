package de.rwth.oosc.furniture.action;

import java.awt.event.ActionEvent;
import java.util.Set;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.action.AbstractSelectedAction;

import de.rwth.oosc.dialog.FurnitureSaveDialog;
import de.rwth.oosc.furniture.CustomFurniture;
import de.rwth.oosc.furniture.FurnitureModel;

public class AddFurnitureAction extends AbstractSelectedAction {

	private static final long serialVersionUID = 1L;

	public static final String ID = "FurnitureSaveAction.ID";

	private FurnitureModel furnitureModel;

	public AddFurnitureAction(DrawingEditor editor, FurnitureModel furnitureModel) {
		super(editor);

		this.furnitureModel = furnitureModel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GroupFigure gf = new GroupFigure();
		Set<Figure> figureSet = getEditor().getActiveView().getSelectedFigures();
		Object[] figureArray = figureSet.toArray();
		if (figureSet.size() == 1 && figureArray[0] instanceof GroupFigure) {
			gf = (GroupFigure) figureArray[0];
		} else {
			gf.addAll(figureSet);
		}
		

		FurnitureSaveDialog dialog = new FurnitureSaveDialog(getView().getComponent(), furnitureModel.getCatalogues().toArray(new String[0]));

		if (dialog.isApproved()) {
			String name = dialog.getTypedName();
			String catalogue = dialog.getSelectedCatalogue();
			try {
				CustomFurniture furniture = new CustomFurniture(name, gf);
				furnitureModel.addFurniture(catalogue, furniture);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

}
