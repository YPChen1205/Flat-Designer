package de.rwth.oosc.actions;

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
import org.jhotdraw.draw.io.ImageOutputFormat;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;

import de.rwth.oosc.DrawView;

public class ExportAction extends AbstractViewAction {

	private static final long serialVersionUID = 1L;

	public static final String ID = "menu.file.export";

	public ExportAction(Application app, @Nullable View view) {
		super(app, view);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new ExtensionFileFilter("Flat image .png", "png"));
		chooser.addChoosableFileFilter(new ExtensionFileFilter("Flat image .jpg", "jpg"));
		chooser.addChoosableFileFilter(new ExtensionFileFilter("Flat image .gif", "gif"));

		if (chooser.showSaveDialog(getActiveView().getComponent()) == JFileChooser.APPROVE_OPTION) {
			ExtensionFileFilter filter = (ExtensionFileFilter) chooser.getFileFilter();
			File file = filter.makeAcceptable(chooser.getSelectedFile());
			try {
				View v = getActiveView();
				if (v instanceof DrawView) {
					Drawing drawing = ((DrawView) v).getEditor().getActiveView().getDrawing();
					String[] parts = file.getPath().split("\\.");
					String extension = parts[parts.length - 1];
					ImageOutputFormat imageOut = new ImageOutputFormat();
					BufferedImage image = imageOut.toImage(drawing, drawing.getChildren(), 1d, true);
					ImageIO.write(image, extension.toUpperCase(), file);
					image.flush();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
