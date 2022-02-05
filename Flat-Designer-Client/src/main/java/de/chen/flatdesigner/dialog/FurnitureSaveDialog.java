package de.chen.flatdesigner.dialog;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import de.chen.flatdesigner.components.JPlaceholderComboBox;
import de.chen.flatdesigner.components.JPlaceholderTextField;

public class FurnitureSaveDialog extends JDialog implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	
	private String typedName;
	private String selectedCatalogue;
	
	private String placeholder;
	
	private JTextField txtName;
	private JComboBox<String> cboCatalog;
	
	private JOptionPane optionPane;

	public FurnitureSaveDialog(Component parent, String[] catalogues) {
		super(null, "Save furniture...", ModalityType.APPLICATION_MODAL);		
		
		placeholder = "Please choose a catalogue...";
		
		Vector<String> cboCatalogues = new Vector<>();
		cboCatalogues.addAll(Arrays.asList(catalogues));
		
		cboCatalog = new JPlaceholderComboBox<>(placeholder, cboCatalogues);
		txtName = new JPlaceholderTextField("Type in a furniture name...");
		txtName.setFocusable(false);
		cboCatalog.requestFocus();
		
		txtName.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				txtName.setFocusable(true);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		Object[] components = {"Furniture name:", txtName, "Catalog:", cboCatalog};
		optionPane = new JOptionPane(components, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		
		setContentPane(optionPane);
		
		optionPane.addPropertyChangeListener(this);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
		
		
	}
	
	public String getSelectedCatalogue() {
		return selectedCatalogue;
	}
	
	public String getTypedName() {
		return typedName;
	}

	private void setTypedName(String typedName) {
		this.typedName = typedName;
	}
	
	public void setSelectedCatalogue(String selectedCatalogue) {
		this.selectedCatalogue = selectedCatalogue;
	}
	
	/**
	 * 
	 * @return true if the user set a furniture name and selected a catalogue
	 */
	public boolean isApproved() {
		return optionPane.getValue().equals(JOptionPane.OK_OPTION) &&
				getTypedName() != null &&
				!getTypedName().isBlank() &&
				getSelectedCatalogue() != null;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (optionPane.getValue().equals(JOptionPane.UNINITIALIZED_VALUE)) {
			return;
		}
		
		int optionPaneValue = (int)optionPane.getValue();
		if (isVisible() && evt.getSource() == optionPane &&
				JOptionPane.VALUE_PROPERTY.equals(prop) && 
				optionPaneValue == JOptionPane.OK_OPTION) {
			String name = txtName.getText();
			int selectedIndex = cboCatalog.getSelectedIndex();
			
			if (!name.isBlank() && selectedIndex > -1 && !cboCatalog.getSelectedItem().equals(placeholder)) {
				setTypedName(name);
				setSelectedCatalogue((String) cboCatalog.getSelectedItem());
				
				hideAndClean();
			} else {
				JOptionPane.showMessageDialog(this, "Please input a name AND choose the catalog for your furniture.", "Save error", JOptionPane.ERROR_MESSAGE);
				optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
			}
		} else if (isVisible() && evt.getSource() == optionPane &&
				JOptionPane.VALUE_PROPERTY.equals(prop) && 
				optionPaneValue == JOptionPane.CANCEL_OPTION) {
			hideAndClean();
		}
	}
	
	private void hideAndClean() {
		setVisible(false);
		dispose();
	}
}
