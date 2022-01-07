package de.rwth.oosc.furniture.action;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JMenuItem;

import de.rwth.oosc.furniture.CustomFurniture;
import de.rwth.oosc.furniture.FurnitureModel;

public class RemoveFurnitureAction extends AbstractItemAction {

	public RemoveFurnitureAction(FurnitureModel fmodel, String catalog, CustomFurniture cfurniture) {
		super(fmodel, catalog, cfurniture);
	}

	@Override
	protected Map<JMenuItem, Consumer<MouseEvent>> createMenuItems() {
		Map<JMenuItem, Consumer<MouseEvent>> map  = new HashMap<>();
		JMenuItem itemRm = new JMenuItem("Remove "+ cfurniture.getName());
		map.put(itemRm, (e)->{
			fmodel.removeFurniture(catalog, cfurniture);
		});
		return map;
		
	}

}
