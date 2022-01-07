package de.rwth.oosc.furniture.action;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.rwth.oosc.furniture.CustomFurniture;
import de.rwth.oosc.furniture.FurnitureModel;

public class RemoveFurnitureAction extends MouseAdapter{
	
	private FurnitureModel fmodel; 
	private CustomFurniture cfurniture;
	private String catalog;
	
	public RemoveFurnitureAction(FurnitureModel fmodel, String catalog, CustomFurniture cfurniture) {
		this.fmodel = fmodel;
		this.catalog = catalog;
		this.cfurniture = cfurniture;
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON3)
			popMenu(e);
	}
	
	private void popMenu(MouseEvent  e) {
		JPopupMenu rightClickMenu = new JPopupMenu();
		JMenuItem rmItem = new JMenuItem("Remove " + cfurniture.getName());
		rmItem.addMouseListener(
				new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						fmodel.removeFurniture(catalog, cfurniture);
					}

		});
		rightClickMenu.add(rmItem);
		rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
	}
}
