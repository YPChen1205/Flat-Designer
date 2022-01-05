package de.rwth.oosc.furniture.action;

import java.awt.event.ActionEvent;
import java.util.Set;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.action.AbstractSelectedAction;

import de.rwth.oosc.dialog.FurnitureSaveDialog;
import de.rwth.oosc.furniture.CustomFurniture;

public class FurnitureSaveAction extends AbstractSelectedAction {

	private static final long serialVersionUID = 1L;

	public static final String ID = "FurnitureSaveAction.ID";

	private String[] catalogues;

	public FurnitureSaveAction(DrawingEditor editor, String[] catalogues) {
		super(editor);

		this.catalogues = catalogues;
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
		CustomFurniture customfuniture = new CustomFurniture(gf);

		FurnitureSaveDialog dialog = new FurnitureSaveDialog(getView().getComponent(), catalogues);

		if (dialog.isApproved()) {
			String name = dialog.getTypedName();
			String catalogue = dialog.getSelectedCatalogue();
			try {
				customfuniture.saveFigure(name, catalogue);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

}
