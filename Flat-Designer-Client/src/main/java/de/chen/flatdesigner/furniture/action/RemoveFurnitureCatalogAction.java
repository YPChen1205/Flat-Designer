package de.chen.flatdesigner.furniture.action;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JMenuItem;

import de.chen.flatdesigner.furniture.CustomFurniture;
import de.chen.flatdesigner.furniture.FurnitureModel;

public class RemoveFurnitureCatalogAction extends AbstractItemAction {

	public RemoveFurnitureCatalogAction(String catalog, CustomFurniture cfurniture) {
		super(catalog, cfurniture);
	}

	@Override
	protected Map<JMenuItem, Consumer<MouseEvent>> createMenuItems() {
		Map<JMenuItem, Consumer<MouseEvent>> map = new HashMap<>();
		JMenuItem itemRemove = new JMenuItem("Remove " + catalog);
		map.put(itemRemove, e->{
			FurnitureModel.getInstance().removeFurnitureCatalog(catalog);
		});
		
		
		return map;
	}
	
}
