package de.chen.flatdesigner;

import javax.swing.Action;
import javax.swing.JMenu;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultMenuBuilder;
import org.jhotdraw.app.View;
import org.jhotdraw.util.ResourceBundleUtil;

import de.chen.flatdesigner.actions.ExportAction;
import de.chen.flatdesigner.actions.ImportAction;
import de.chen.flatdesigner.actions.PublishAction;

public class DesignerMenuBuilder extends DefaultMenuBuilder {

	@Override
	public void addExportFileItems(JMenu m, Application app, @Nullable View v) {
		super.addExportFileItems(m, app, v);
		ResourceBundleUtil customLabels = ResourceBundleUtil.getBundle(DrawApplicationModel.CUSTOM_LABELS);
		
		m.addSeparator();
		
		Action importAction = new ImportAction(app, v);
		customLabels.configureAction(importAction, ImportAction.ID);
		add(m, importAction);
		
		Action exportAction = new ExportAction(app, v);
		customLabels.configureAction(exportAction, ExportAction.ID);
		add(m, exportAction);
		
		Action uploadAction = new PublishAction(app, v);
		customLabels.configureAction(uploadAction, PublishAction.ID);
		add(m, uploadAction);
	}
}
