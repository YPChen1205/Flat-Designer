package de.rwth.oosc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class SwingGUI {
	private JFrame jf = new JFrame();
	private JTextField txtName = new JTextField();
	private JPanel pnlGender = new JPanel();
	
	private JMenuBar mb = new JMenuBar();
	private JMenu mfile = new JMenu("File");
	private JMenuItem itemUpl = new JMenuItem("Upload");
	private JMenuItem itemSave = new JMenuItem("Save");
	private JRadioButton [] rdbsGender = {new JRadioButton("female", true), new JRadioButton("male",false), new JRadioButton("diverse", false)};
	private ButtonGroup bg = new ButtonGroup();
	
	private JButton btnAddItem = new JButton("Add Item");
	private JButton btnRemoveItem = new JButton("Remove Item");
	private JFileChooser fchooser = new JFileChooser();
	private DefaultListModel<String> model = new DefaultListModel<>();
	private JList<String> itemList = new JList<>(model);
	
	public void file2Str(File file, DefaultListModel<String> model ){
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"))){
			String str = null;
			while((str = br.readLine()) != null) {
				model.addElement(str);
			}
			System.out.println(model);
		}catch(IOException e) {
			System.out.println(e);
		}
	
	}
	public void str2File(DefaultListModel<String> model, File file){
		try(BufferedWriter bw= new BufferedWriter(new FileWriter(file,StandardCharsets.UTF_8))){
			Iterator<String> it = model.elements().asIterator();
			while(it.hasNext()) {
				bw.write(it.next());
				bw.write("\n");
			}
		}catch(IOException e) {
			System.out.println(e);
		}
	}

	public void show() {
		mfile.add(itemUpl);
		mfile.add(itemSave);
		mb.add(mfile);
		
		jf.add(mb, BorderLayout.NORTH);
		
		itemUpl.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int value = fchooser.showOpenDialog(jf);
				if(value == JFileChooser.APPROVE_OPTION) {
					File f = fchooser.getSelectedFile();
					file2Str(f, model);

				}
			}
		});
		
		itemSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			if(fchooser.showSaveDialog(jf)==JFileChooser.APPROVE_OPTION) {
				File f = fchooser.getSelectedFile();
				str2File(model, f);
			}
			}
		});
		
		
		var listBox = new Box(BoxLayout.Y_AXIS);
		listBox.add(new JScrollPane(itemList));
		
		JPanel pnlListLayout = new JPanel();
		jf.add(pnlListLayout,BorderLayout.CENTER);
		listBox.setPreferredSize(new Dimension(200,200));
		pnlListLayout.setLayout(new FlowLayout());
		
		pnlListLayout.add(listBox);
		
		JPanel pnlButtonLayout = new JPanel();
		pnlButtonLayout.setLayout(new BoxLayout(pnlButtonLayout, 1));
		
		
		
		JTextField txtAddedItem = new JTextField();
		pnlButtonLayout.add(txtAddedItem);
		pnlButtonLayout.add(btnAddItem);
		btnAddItem.addActionListener((e)->{
			model.addElement(txtAddedItem.getText());
		});
		
		pnlButtonLayout.add(btnRemoveItem);
		btnRemoveItem.addActionListener((e)->{
			int idx = itemList.getSelectedIndex();
			model.remove(idx);
		});
		pnlListLayout.add(pnlButtonLayout);
		
		for(JRadioButton rdb: rdbsGender) {
			bg.add(rdb);
			pnlGender.add(rdb);
		}
		jf.add(pnlGender, BorderLayout.SOUTH);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		jf.pack();
		jf.setVisible(true);
	}
}
