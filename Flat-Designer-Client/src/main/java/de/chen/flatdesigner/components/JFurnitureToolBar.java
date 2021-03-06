package de.chen.flatdesigner.components;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.event.ToolListener;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.gui.JPopupButton;

import de.chen.flatdesigner.furniture.CustomFurniture;
import de.chen.flatdesigner.furniture.FurnitureModel;
import de.chen.flatdesigner.furniture.action.RemoveFurnitureAction;
import de.chen.flatdesigner.tool.FurnitureCreationTool;
import de.chen.flatdesigner.tool.ToolButtonListener;

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
			} else {
				btnCatalog.setText("");
			}
			for (CustomFurniture furniture : furnitures) {
				if (first) {
					btnCatalog.setIcon(furniture.getIcon());
					first = false;
				}
				JToggleButton button = new JToggleButton(furniture.getIcon());
				button.setPreferredSize(new Dimension(22, 22));
				button.setToolTipText(furniture.getName());
				Tool furnitureCreationTool = new FurnitureCreationTool(furniture.getFigure());
				button.addItemListener(new ToolButtonListener(furnitureCreationTool, editor, btnCatalog));
				button.setFocusable(false);
				furnitureCreationTool.addToolListener((ToolListener) getClientProperty(handlerKey));
				button.addMouseListener(new RemoveFurnitureAction(category, furniture));
				group.add(button);
				btnCatalog.add(button);
			}
			add(btnCatalog);
		});
		invalidate();
		repaint();
	}

}
