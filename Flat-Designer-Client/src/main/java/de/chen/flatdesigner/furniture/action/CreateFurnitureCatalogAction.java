package de.chen.flatdesigner.furniture.action;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import de.chen.flatdesigner.furniture.CustomFurniture;
import de.chen.flatdesigner.furniture.FurnitureModel;

public class CreateFurnitureCatalogAction extends AbstractItemAction {

	public CreateFurnitureCatalogAction(FurnitureModel fmodel, String catalog, CustomFurniture cfurniture) {
		super(catalog, cfurniture);
	}

	@Override
	protected Map<JMenuItem, Consumer<MouseEvent>> createMenuItems() {
		Map<JMenuItem, Consumer<MouseEvent>> map = new HashMap<>();
		JMenuItem itemCreate = new JMenuItem("Create new catalog");
		map.put(itemCreate, e->{
			String catalog = JOptionPane.showInputDialog("Furniture Catalog name:");
			FurnitureModel.getInstance().addCatalog(catalog);
		});
		
		
		return map;
	}
	
}
