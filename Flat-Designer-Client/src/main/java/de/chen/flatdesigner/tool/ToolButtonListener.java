package de.chen.flatdesigner.tool;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JToggleButton;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.gui.JPopupButton;

public class ToolButtonListener implements ItemListener {

	private Tool tool;
	private DrawingEditor editor;
	private JPopupButton parent;

	public ToolButtonListener(Tool t, DrawingEditor editor, JPopupButton parent) {
		this.tool = t;
		this.editor = editor;
		this.parent = parent;
	}

	@Override
	public void itemStateChanged(ItemEvent evt) {
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			parent.getPopupMenu().setVisible(false);
			parent.setIcon(((JToggleButton) evt.getItem()).getIcon());
			editor.setTool(tool);
		}
	}
}
