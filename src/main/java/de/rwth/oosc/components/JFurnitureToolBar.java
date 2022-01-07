package de.rwth.oosc.components;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.event.ToolListener;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.gui.JPopupButton;

import de.rwth.oosc.furniture.CustomFurniture;
import de.rwth.oosc.furniture.FurnitureModel;
import de.rwth.oosc.furniture.action.RemoveFurnitureAction;
import de.rwth.oosc.tool.ToolButtonListener;

public class JFurnitureToolBar extends JToolBar implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private DrawingEditor editor;
	private String buttongroupKey;
	private String handlerKey;

	public JFurnitureToolBar(DrawingEditor editor, String buttongroupKey, String handlerKey) {
		super();
		this.editor = editor;
		this.buttongroupKey = buttongroupKey;
		this.handlerKey = handlerKey;
	}

	private void removePopupButtons() {
		synchronized (getTreeLock()) {
			for (Component c : getComponents()) {
				if (c instanceof JPopupButton) {
					remove(c);
				}
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		FurnitureModel furnitureModel = (FurnitureModel) evt.getNewValue();

		removePopupButtons();

		ButtonGroup group = (ButtonGroup) getClientProperty(buttongroupKey);

		furnitureModel.forEachCategory((category, furnitures) -> {
			JPopupButton btnCatalog = new JPopupButton();
			btnCatalog.setFocusable(false);
			btnCatalog.setToolTipText(category);
			boolean first = true;
			if (furnitures.size() < 1) {
				btnCatalog.setText(category);
			}
			for (CustomFurniture furniture : furnitures) {
				if (first) {
					btnCatalog.setIcon(furniture.getIcon());
					first = false;
				}
				JToggleButton button = new JToggleButton(furniture.getIcon());
				button.setPreferredSize(new Dimension(22, 22));
				button.setToolTipText(furniture.getName());
				Tool furnitureCreationTool = new CreationTool(furniture.getFigure());
				button.addItemListener(new ToolButtonListener(furnitureCreationTool, editor, btnCatalog));
				button.setFocusable(false);
				furnitureCreationTool.addToolListener((ToolListener) getClientProperty(handlerKey));
				button.addMouseListener(new RemoveFurnitureAction(furnitureModel, category, furniture));
				group.add(button);
				btnCatalog.add(button);
			}
			add(btnCatalog);
		});
	}

}
