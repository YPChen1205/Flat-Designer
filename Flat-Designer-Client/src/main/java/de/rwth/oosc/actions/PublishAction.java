package de.rwth.oosc.actions;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.io.ImageOutputFormat;

import de.rwth.oosc.DrawView;
import de.rwth.oosc.dialog.PublishDialog;

public class PublishAction extends AbstractViewAction {

	private static final long serialVersionUID = 1L;
	
	public static final String ID = "menu.file.publish";
	
	public PublishAction(Application app, @Nullable View view) {
		super(app, view);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		View v = getActiveView();
		if (v instanceof DrawView) {
			Drawing drawing = ((DrawView) v).getEditor().getActiveView().getDrawing();
			ImageOutputFormat out = new ImageOutputFormat();
			BufferedImage image = out.toImage(drawing, drawing.getChildren(), 1d, true);
			new PublishDialog(image);
		}
	}

}
