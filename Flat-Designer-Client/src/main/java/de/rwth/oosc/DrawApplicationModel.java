/*
 * @(#)DrawApplicationModel.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package de.rwth.oosc;

import static org.jhotdraw.draw.AttributeKeys.PATH_CLOSED;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.ApplicationModel;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.MenuBuilder;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.ActionUtil;
import org.jhotdraw.app.action.edit.DuplicateAction;
import org.jhotdraw.draw.AbstractAttributedCompositeFigure;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.action.BringToFrontAction;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.draw.action.GroupAction;
import org.jhotdraw.draw.action.SendToBackAction;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.event.ToolListener;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.DelegationSelectionTool;
import org.jhotdraw.draw.tool.ImageTool;
import org.jhotdraw.draw.tool.TextAreaCreationTool;
import org.jhotdraw.draw.tool.TextCreationTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.JPopupButton;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.util.ResourceBundleUtil;

import de.rwth.oosc.actions.AbstractTransformAction;
import de.rwth.oosc.actions.HorizontalFlipAction;
import de.rwth.oosc.actions.VerticalFlipAction;
import de.rwth.oosc.components.JFurnitureToolBar;
import de.rwth.oosc.figures.structure.DoorFigure;
import de.rwth.oosc.figures.structure.WallFigure;
import de.rwth.oosc.figures.structure.WindowFigure;
import de.rwth.oosc.figures.svg.SVGBezierFigure;
import de.rwth.oosc.figures.svg.SVGEllipseFigure;
import de.rwth.oosc.figures.svg.SVGGroupFigure;
import de.rwth.oosc.figures.svg.SVGImageFigure;
import de.rwth.oosc.figures.svg.SVGPathFigure;
import de.rwth.oosc.figures.svg.SVGRectFigure;
import de.rwth.oosc.figures.svg.SVGTextAreaFigure;
import de.rwth.oosc.figures.svg.SVGTextFigure;
import de.rwth.oosc.figures.svg.SVGTriangleFigure;
import de.rwth.oosc.furniture.CustomFurniture;
import de.rwth.oosc.furniture.FurnitureModel;
import de.rwth.oosc.furniture.action.AddFurnitureAction;
import de.rwth.oosc.furniture.action.CreateFurnitureCatalogAction;
import de.rwth.oosc.furniture.action.RemoveFurnitureAction;
import de.rwth.oosc.furniture.action.RemoveFurnitureCatalogAction;
import de.rwth.oosc.furniture.action.SVGUngroupAction;
import de.rwth.oosc.tool.FurnitureCreationTool;
import de.rwth.oosc.tool.PathTool;
import de.rwth.oosc.tool.RoomSelectionTool;
import de.rwth.oosc.tool.ToolButtonListener;

/**
 * Provides factory methods for creating views, menu bars and toolbars.
 * <p>
 * See {@link ApplicationModel} on how this class interacts with an application.
 * 
 * @author Werner Randelshofer.
 * @version $Id: DrawApplicationModel.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class DrawApplicationModel extends DefaultApplicationModel {
	private static final long serialVersionUID = 1L;

	private FurnitureModel furnitureModel;

	public static final String CUSTOM_LABELS = "de.rwth.oosc.flatdesigner.Labels";

	/**
	 * Toolbar properties.
	 */
	public static final String TOOLBAR_BUTTONGROUP_PROPKEY = "toolButtonGroup";
	public static final String TOOLBAR_HANDLER_PROPKEY = "toolHandler";

	/**
	 * This editor is shared by all views.
	 */
	private DefaultDrawingEditor sharedEditor;

	/** Creates a new instance. */
	public DrawApplicationModel() {
		initFurnitures();
	}

	public DefaultDrawingEditor getSharedEditor() {
		if (sharedEditor == null) {
			sharedEditor = new DefaultDrawingEditor();
		}
		return sharedEditor;
	}

	@Override
	public void initView(Application a, View p) {
		if (a.isSharingToolsAmongViews()) {
			((DrawView) p).setEditor(getSharedEditor());
		}
	}

	public void initFurnitures() {
		furnitureModel = FurnitureModel.getInstance();
	}
	
	//--------------------------------------------------------------
	private Collection<Action> createSelectionActions(DrawingEditor editor) {
		LinkedList<Action> a = new LinkedList<Action>();
        a.add(new DuplicateAction());

        a.add(null); // separator

        a.add(new GroupAction(editor, new SVGGroupFigure()));
        a.add(new SVGUngroupAction(editor));
        
        a.add(null); // separator

        a.add(new BringToFrontAction(editor));
        a.add(new SendToBackAction(editor));

        return a;
	}
	
	@Override
	protected MenuBuilder createMenuBuilder() {
		return new DesignerMenuBuilder();
	}

	/**
	 * Creates toolbars for the application. This class always returns an empty
	 * list. Subclasses may return other values.
	 */
	@Override
	public List<JToolBar> createToolBars(Application a, View pr) {
		ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
		ResourceBundleUtil customLabels = ResourceBundleUtil.getBundle(CUSTOM_LABELS);
		DrawView p = (DrawView) pr;

		DrawingEditor editor;
		if (p == null) {
			editor = getSharedEditor();
		} else {
			editor = p.getEditor();
		}

		LinkedList<JToolBar> list = new LinkedList<JToolBar>();
		JToolBar tb;
		tb = new JToolBar();
		Collection<Action> selectionActions = createSelectionActions(editor);
		addSelectionToolTo(tb, editor, ButtonFactory.createDrawingActions(editor), selectionActions);

		addDefaultCreationButtonsTo(tb, editor);
		tb.setName(labels.getString("window.drawToolBar.title"));
		list.add(tb);
		// ------------------------------------------------------------
		ButtonGroup buttonGroup = (ButtonGroup) tb.getClientProperty("toolButtonGroup");
		ToolListener listener = (ToolListener) tb.getClientProperty("toolHandler");

		tb = new JFurnitureToolBar(editor, TOOLBAR_BUTTONGROUP_PROPKEY, TOOLBAR_HANDLER_PROPKEY);
		tb.putClientProperty(TOOLBAR_BUTTONGROUP_PROPKEY, buttonGroup);
		tb.putClientProperty(TOOLBAR_HANDLER_PROPKEY, listener);
		addFlatElementButtonsTo(tb, editor);
		tb.setName(customLabels.getString("window.flatElementsToolBar.title"));
		list.add(tb);
		customSelectionAction(editor, selectionActions);

		tb = new JToolBar();
		ButtonFactory.addAttributesButtonsTo(tb, editor);
		tb.setName(labels.getString("window.attributesToolBar.title"));
		list.add(tb);

		tb = new JToolBar();
		ButtonFactory.addAlignmentButtonsTo(tb, editor);
		tb.setName(labels.getString("window.alignmentToolBar.title"));
		list.add(tb);
		return list;
	}

	private void addFlatElementButtonsTo(JToolBar tb, DrawingEditor editor) {
		ResourceBundleUtil customLabels = ResourceBundleUtil.getBundle(CUSTOM_LABELS);

		ButtonFactory.addToolTo(tb, editor, new RoomSelectionTool(), "select.room", customLabels);
		
		ButtonFactory.addToolTo(tb, editor, new CreationTool(new WallFigure()), "edit.createWall", customLabels);
		ButtonFactory.addToolTo(tb, editor, new CreationTool(new WindowFigure()), "edit.createWindow", customLabels);
		ButtonFactory.addToolTo(tb, editor, new CreationTool(new DoorFigure()), "edit.createDoor", customLabels);

		tb.addSeparator();
		furnitureModel.addPropertyChangeListener((JFurnitureToolBar) tb);
		tb.addMouseListener(new CreateFurnitureCatalogAction(furnitureModel, null, null));

		try {
			ButtonGroup group = (ButtonGroup) tb.getClientProperty(TOOLBAR_BUTTONGROUP_PROPKEY);
			furnitureModel.forEachCategory((category, furnitures) -> {
				JPopupButton btnCatalog = new JPopupButton();
				btnCatalog.addMouseListener(new RemoveFurnitureCatalogAction(category, null));
				btnCatalog.setFocusable(false);
				btnCatalog.setToolTipText(category);
				boolean first = true;
				if (furnitures.size() < 1) {
					btnCatalog.setText(category);
				}
				for (CustomFurniture furniture : furnitures) {
					if (first) {
						btnCatalog.setIcon(furniture.getIcon());
						first = false;
					}
					JToggleButton button = new JToggleButton(furniture.getIcon());
					button.setPreferredSize(new Dimension(22, 22));
					button.setToolTipText(furniture.getName());
					Tool furnitureCreationTool = new FurnitureCreationTool(furniture.getFigure());
					button.addItemListener(new ToolButtonListener(furnitureCreationTool, editor, btnCatalog));
					button.setFocusable(false);
					furnitureCreationTool.addToolListener((ToolListener) tb.getClientProperty(TOOLBAR_HANDLER_PROPKEY));
					// --------------popup context menu-------------
					button.addMouseListener(new RemoveFurnitureAction(category, furniture));

					group.add(button);
					btnCatalog.add(button);
				}
				tb.add(btnCatalog);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

	public void customSelectionAction(DrawingEditor editor, Collection<Action> selectionActions) {
		ResourceBundleUtil customLabels = ResourceBundleUtil.getBundle(CUSTOM_LABELS);

		AddFurnitureAction fsAction = new AddFurnitureAction(editor);
		customLabels.configureAction(fsAction, AddFurnitureAction.ID);
		selectionActions.add(fsAction);

		// ---------------
		selectionActions.add(null);

		AbstractTransformAction hFlipAction = new HorizontalFlipAction(editor);
		customLabels.configureAction(hFlipAction, "edit.horizontalFlip");
		hFlipAction.putValue(ActionUtil.SUBMENU_KEY, "Flip");
		selectionActions.add(hFlipAction);

		AbstractTransformAction vFlipAction = new VerticalFlipAction(editor);
		customLabels.configureAction(vFlipAction, "edit.verticalFlip");
		vFlipAction.putValue(ActionUtil.SUBMENU_KEY, "Flip");
		selectionActions.add(vFlipAction);
	}

	private void addSelectionToolTo(JToolBar tb, final DrawingEditor editor, Collection<Action> drawingActions,
			Collection<Action> selectionActions) {
		DelegationSelectionTool selectionTool = new DelegationSelectionTool(drawingActions, selectionActions);
		ButtonFactory.addSelectionToolTo(tb, editor, selectionTool);
		
		tb.addSeparator();
	}

	public void addDefaultCreationButtonsTo(JToolBar tb, final DrawingEditor editor) {
		ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
		
		ButtonFactory.addToolTo(tb, editor, new CreationTool(new SVGRectFigure()), "edit.createRectangle", labels);
		ButtonFactory.addToolTo(tb, editor, new CreationTool(new SVGEllipseFigure()), "edit.createEllipse", labels);
		ButtonFactory.addToolTo(tb, editor, new CreationTool(new SVGTriangleFigure()), "edit.createTriangle", labels);
		Map<AttributeKey<?>, Object> attributes = new HashMap<AttributeKey<?>, Object>();
        attributes.put(AttributeKeys.FILL_COLOR, null);
        attributes.put(PATH_CLOSED, false);
		ButtonFactory.addToolTo(tb, editor, new CreationTool(new SVGPathFigure()), "edit.createLine", labels);

		// ------------------ArcTool---------------------------
//		ButtonFactory.addToolTo(tb, editor, new CreationTool(new FigureRotationProxy(new ArcFigure())), "edit.createArc", customLabels);
		CreationTool ct;
		AbstractAttributedCompositeFigure af;
		
		ButtonFactory.addToolTo(tb, editor, ct = new CreationTool(new SVGPathFigure()), "edit.createArrow", labels);
		af = (AbstractAttributedCompositeFigure) ct.getPrototype();
		af.set(AttributeKeys.END_DECORATION, new ArrowTip(0.35, 12, 11.3));
		
		ButtonFactory.addToolTo(tb, editor, new PathTool(new SVGPathFigure(), new SVGBezierFigure(), attributes), "edit.createScribble", labels);
		ButtonFactory.addToolTo(tb, editor, new PathTool(new SVGPathFigure(), new SVGBezierFigure(true)), "edit.createPolygon", labels);
		
		attributes = new HashMap<AttributeKey<?>, Object>();
        attributes.put(AttributeKeys.FILL_COLOR, Color.black);
        attributes.put(AttributeKeys.STROKE_COLOR, null);
		ButtonFactory.addToolTo(tb, editor, new TextCreationTool(new SVGTextFigure(), attributes), "edit.createText", labels);
		ButtonFactory.addToolTo(tb, editor, new TextAreaCreationTool(new SVGTextAreaFigure(), attributes), "edit.createTextArea",
				labels);
		ButtonFactory.addToolTo(tb, editor, new ImageTool(new SVGImageFigure()), "edit.createImage", labels);
	}

	@Override
	public URIChooser createOpenChooser(Application a, View v) {
		JFileURIChooser c = new JFileURIChooser();
		c.addChoosableFileFilter(new ExtensionFileFilter("Drawing .svg", "svg"));
		return c;
	}

	@Override
	public URIChooser createSaveChooser(Application a, View v) {
		JFileURIChooser c = new JFileURIChooser();
		c.addChoosableFileFilter(new ExtensionFileFilter("Drawing .svg", "svg"));
		return c;
	}

	@Override
	public URIChooser createExportChooser(Application a, @Nullable View v) {
		JFileURIChooser c = (JFileURIChooser) super.createExportChooser(a, v);
		c.addChoosableFileFilter(new ExtensionFileFilter("PNG File .png", "png"));
		return c;
	}
}
