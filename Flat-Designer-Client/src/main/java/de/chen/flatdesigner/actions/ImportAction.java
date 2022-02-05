package de.chen.flatdesigner.actions;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;

import de.chen.flatdesigner.DrawView;
import de.chen.flatdesigner.figures.svg.SVGImageFigure;

public class ImportAction extends AbstractViewAction {

	private static final long serialVersionUID = 1L;
	
	public static final String ID = "menu.file.import";

	public ImportAction(Application app, @Nullable View view) {
		super(app, view);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new ExtensionFileFilter("Flat image .png", "png"));
		chooser.addChoosableFileFilter(new ExtensionFileFilter("Flat image .jpg", "jpg"));
		chooser.addChoosableFileFilter(new ExtensionFileFilter("Flat image .gif", "gif"));
		
		if (chooser.showOpenDialog(getActiveView().getComponent()) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				BufferedImage image = ImageIO.read(file);
				SVGImageFigure f = new SVGImageFigure(0, 0, image.getWidth(), image.getHeight());
				f.setBufferedImage(image);
				View v = getActiveView();
				if (v instanceof DrawView) {
					Drawing drawing = ((DrawView) v).getEditor().getActiveView().getDrawing();
					drawing.add(f);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
