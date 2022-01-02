package de.rwth.oosc.furniture.action;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;

import javax.swing.JOptionPane;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.action.AbstractSelectedAction;

import de.rwth.oosc.DrawApplicationModel;
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
		if(figureSet.size() > 1) {
			gf.addAll(figureSet);
		}else {
			gf = (GroupFigure) figureSet.toArray()[0];
		}
		CustomFurniture customfuniture = new CustomFurniture(gf);
		
		FurnitureSaveDialog dialog = new FurnitureSaveDialog(getView().getComponent() ,catalogues);
		
		if (dialog.isApproved()) {
			String name = dialog.getTypedName();
			String catalogue = dialog.getSelectedCatalogue();
			try {
				customfuniture.saveFigure(name, catalogue);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
