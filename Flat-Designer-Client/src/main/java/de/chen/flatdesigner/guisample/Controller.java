package de.chen.flatdesigner.guisample;

import javax.swing.DefaultListModel;

public class Controller {
	private static final Controller controller = new Controller();
	
	private SwingGUI gui;
	private DefaultListModel<String> model;

	private Controller() {
		this.model = new DefaultListModel<>();
		this.gui = new SwingGUI(model);
	};
	
	public void init() {
		gui.show();
	}

	public static Controller getController() {
		return controller;
	}
}
