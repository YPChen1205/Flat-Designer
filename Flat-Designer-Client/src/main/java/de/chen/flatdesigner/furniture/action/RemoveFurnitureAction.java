package de.chen.flatdesigner.furniture.action;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JMenuItem;

import de.chen.flatdesigner.furniture.CustomFurniture;
import de.chen.flatdesigner.furniture.FurnitureModel;

public class RemoveFurnitureAction extends AbstractItemAction {

	public RemoveFurnitureAction(String catalog, CustomFurniture cfurniture) {
		super(catalog, cfurniture);
	}

	@Override
	protected Map<JMenuItem, Consumer<MouseEvent>> createMenuItems() {
		Map<JMenuItem, Consumer<MouseEvent>> map  = new HashMap<>();
		JMenuItem itemRm = new JMenuItem("Remove "+ cfurniture.getName());
		map.put(itemRm, (e)->{
			FurnitureModel.getInstance().removeFurniture(catalog, cfurniture);
		});
		return map;
		
	}

}
