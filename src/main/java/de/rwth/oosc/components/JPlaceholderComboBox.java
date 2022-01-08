package de.rwth.oosc.components;

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class JPlaceholderComboBox<E> extends JComboBox<E> {

	private static final long serialVersionUID = 1L;

	private E placeholder;

	public JPlaceholderComboBox(E placeholder) {
		this(placeholder, new DefaultComboBoxModel<>());
	}

	public JPlaceholderComboBox(E placeholder, Vector<E> model) {
		this(placeholder, new DefaultComboBoxModel<>(model));
	}

	public JPlaceholderComboBox(E placeholder, DefaultComboBoxModel<E> model) {
		super(model);
		this.placeholder = placeholder;
		model.insertElementAt(placeholder, 0);
		setSelectedIndex(0);
//		setSelectedItem(model);
		setForeground(Color.GRAY);
		addItemListener((e) -> {
			if (e.getItem() != this.placeholder) {
			
				setForeground(Color.BLACK);
			}
		});

		addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				if (JPlaceholderComboBox.this.placeholder == model.getSelectedItem()) {
					model.removeElementAt(0);
				}
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
	}

	public E getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(E placeholder) {
		this.placeholder = placeholder;
	}
}
