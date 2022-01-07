package de.rwth.oosc.furniture.action;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import de.rwth.oosc.furniture.CustomFurniture;
import de.rwth.oosc.furniture.FurnitureModel;

public class CreateFurnitureCatalogAction extends AbstractItemAction {

	public CreateFurnitureCatalogAction(FurnitureModel fmodel, String catalog, CustomFurniture cfurniture) {
		super(fmodel, catalog, cfurniture);
	}

	@Override
	protected Map<JMenuItem, Consumer<MouseEvent>> createMenuItems() {
		Map<JMenuItem, Consumer<MouseEvent>> map = new HashMap<>();
		JMenuItem itemCreate = new JMenuItem("Create new catalog");
		map.put(itemCreate, e->{
			String catalog = JOptionPane.showInputDialog("Furniture Catalog name:");
			fmodel.addCatalog(catalog);
		});
		
		
		return map;
	}
	
}
