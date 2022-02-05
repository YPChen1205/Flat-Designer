package de.chen.flatdesigner.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kong.unirest.Unirest;

public class PublishDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public PublishDialog(BufferedImage flatImage) {
		super(null, "Upload flat image...", ModalityType.APPLICATION_MODAL);
		setLayout(new BorderLayout());
		
		JLabel lblHint = new JLabel("Would you really like to upload this image?");
		add(lblHint, BorderLayout.NORTH);
		
		JLabel lblImage = new JLabel(new ImageIcon(flatImage));
		add(lblImage, BorderLayout.CENTER);
		
		JPanel pnlButtonOption = new JPanel();
		pnlButtonOption.setLayout(new FlowLayout());
		
		JButton btnYes = new JButton("Yes");
		
		ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
		try {
			ImageIO.write(flatImage, "png", imageOut);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		byte[] imageData = imageOut.toByteArray();
		
		btnYes.addActionListener((e) -> {
			
			Unirest.post("http://localhost:8080/api/images/create").field("image", new ByteArrayInputStream(imageData)).asEmpty();

			JOptionPane.showMessageDialog(this, "Image successfully uploaded.");
			
			this.setVisible(false);
			this.dispose();
		});
		JButton btnNo = new JButton("No");
		btnNo.addActionListener((e) -> {
			this.setVisible(false);
			this.dispose();
		});
		pnlButtonOption.add(btnYes);
		pnlButtonOption.add(btnNo);
		add(pnlButtonOption, BorderLayout.SOUTH);
		
		pack();
		setVisible(true);
	}
}
