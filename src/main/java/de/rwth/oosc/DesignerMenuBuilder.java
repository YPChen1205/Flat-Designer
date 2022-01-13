package de.rwth.oosc;

import javax.swing.Action;
import javax.swing.JMenu;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultMenuBuilder;
import org.jhotdraw.app.View;
import org.jhotdraw.util.ResourceBundleUtil;

import de.rwth.oosc.actions.ExportAction;
import de.rwth.oosc.actions.ImportAction;

public class DesignerMenuBuilder extends DefaultMenuBuilder {

	@Override
	public void addExportFileItems(JMenu m, Application app, @Nullable View v) {
		// TODO Auto-generated method stub
		super.addExportFileItems(m, app, v);
		ResourceBundleUtil customLabels = ResourceBundleUtil.getBundle(DrawApplicationModel.CUSTOM_LABELS);
		
		m.addSeparator();
		
		Action importAction = new ImportAction(app, v);
		customLabels.configureAction(importAction, ImportAction.ID);
		add(m, importAction);
		
		Action exportAction = new ExportAction(app, v);
		customLabels.configureAction(exportAction, ExportAction.ID);
		add(m, exportAction);
	}
}
