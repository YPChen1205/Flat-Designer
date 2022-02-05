package de.chen.flatdesigner.furniture.action;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.chen.flatdesigner.furniture.CustomFurniture;

public abstract class AbstractItemAction extends MouseAdapter{
	
	protected CustomFurniture cfurniture;
	protected String catalog;
	
	public AbstractItemAction(String catalog, CustomFurniture cfurniture) {
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
		Map<JMenuItem, Consumer<MouseEvent>> menuItemsMap = createMenuItems();
		
		menuItemsMap.forEach((menuItem, consumer) -> {
			menuItem.addMouseListener(
					new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
							consumer.accept(e);
						}

			});
			rightClickMenu.add(menuItem);
		});

		rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
	}
	
	protected abstract Map<JMenuItem, Consumer<MouseEvent>> createMenuItems();
}
 