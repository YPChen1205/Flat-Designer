package de.rwth.oosc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.rwth.oosc.util.IOUtil;

public class SwingGUI {
	
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JFrame jf = new JFrame("Training");
	private JPanel pnlGender = new JPanel();
	
	private JMenuBar mb = new JMenuBar();
	private JMenu mfile = new JMenu("File");
	private JMenuItem itemUpl = new JMenuItem("Open ");
	private JMenuItem itemSave = new JMenuItem("Save");
	
	private JPanel pnlListLayout = new JPanel();
	
	private JPanel pnlButtonLayout = new JPanel();
	private JTextField txtAddedItem = new JTextField();
	private JButton btnAddItem = new JButton("Add Item");
	private JButton btnRemoveItem = new JButton("Remove Item");
	
	private DefaultListModel<String> model;
	private JList<String> itemList;
	
	public SwingGUI(DefaultListModel<String> model) {
		this.model = model;
		itemList = new JList<>(this.model);
	}
	
	private void addComponents() {
		// Menu
		mfile.add(itemUpl);
		mfile.add(itemSave);
		mb.add(mfile);
		
		// Jlist
		JPanel pnlList = new JPanel();
		pnlList.setLayout(new BorderLayout());
		pnlList.add(new JScrollPane(itemList));

		pnlListLayout.setLayout(new BorderLayout());
		
		pnlListLayout.add(pnlList, BorderLayout.CENTER);
		
		jf.add(pnlListLayout, BorderLayout.CENTER);
		
		pnlButtonLayout.setLayout(new BoxLayout(pnlButtonLayout, BoxLayout.PAGE_AXIS));
		txtAddedItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtAddedItem.getPreferredSize().height));
		pnlButtonLayout.add(txtAddedItem);
		pnlButtonLayout.add(btnAddItem);
		
		pnlButtonLayout.add(btnRemoveItem);
		
	}
	
	private void setupActions() {
		itemUpl.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Text file (.txt)","txt");
			chooser.setFileFilter(filter);
			int value = chooser.showOpenDialog(jf);
			if(value == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				new Thread(() -> {
					IOUtil.file2Str(f, model);
				}).start();
			}
		});
	
		itemSave.addActionListener( e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Text file (.txt)","txt");
			chooser.setFileFilter(filter);
			if(chooser.showSaveDialog(jf)==JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				new Thread(() -> {
					IOUtil.str2File(model, f);
				}).start();
			}
		});
		
		
		btnAddItem.addActionListener((e)->{
			model.addElement(txtAddedItem.getText());
		});
		
		
		btnRemoveItem.addActionListener((e)->{
			int idx = itemList.getSelectedIndex();
			model.remove(idx);
		});
	}
 
	public void show() {
		jf.setLayout(new BorderLayout());
		
		addComponents();
		setupActions();
		
		jf.add(mb, BorderLayout.NORTH);
		jf.add(pnlButtonLayout, BorderLayout.EAST);
		jf.add(pnlGender, BorderLayout.SOUTH);
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setLocationRelativeTo(null); // set location to the center of the screen
		jf.setVisible(true);
	}
}
