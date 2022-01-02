/*
 * @(#)DrawApplicationModel.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package de.rwth.oosc;

import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.BezierTool;
import org.jhotdraw.draw.tool.TextCreationTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.draw.tool.TextAreaCreationTool;
import org.jhotdraw.draw.tool.ImageTool;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.tool.ConnectionTool;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.event.ToolListener;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.util.*;

import de.rwth.oosc.furniture.CustomFurniture;
import de.rwth.oosc.furniture.action.FurnitureSaveAction;
import de.rwth.oosc.structure.DoorFigure;
import de.rwth.oosc.structure.WallFigure;
import de.rwth.oosc.structure.WindowFigure;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URISyntaxException;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.JPopupButton;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import static org.jhotdraw.draw.AttributeKeys.*;

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
    
    private List<String> catalogues = new ArrayList<>();
    private List<CustomFurniture> furnitures = new ArrayList<>();
    
    public static final String CUSTOM_LABELS = "de.rwth.oosc.flatdesigner.Labels";
    
    /**
     * Toolbar properties.
     */
    public static final String TOOLBAR_BUTTONGROUP_PROPKEY = "toolButtonGroup";
    public static final String TOOLBAR_HANDLER_PROPKEY = "toolHandler";
    
    private static class ToolButtonListener implements ItemListener {

        private Tool tool;
        private DrawingEditor editor;
        private JPopupButton parent;

        public ToolButtonListener(Tool t, DrawingEditor editor, JPopupButton parent) {
            this.tool = t;
            this.editor = editor;
            this.parent = parent;
        }

        @Override
        public void itemStateChanged(ItemEvent evt) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
            	parent.getPopupMenu().setVisible(false);
            	parent.setIcon(((JToggleButton)evt.getItem()).getIcon());
                editor.setTool(tool);
            }
        }
    }

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
    public void initView(Application a,View p) {
        if (a.isSharingToolsAmongViews()) {
            ((DrawView) p).setEditor(getSharedEditor());
        }
    }
    
    public void initFurnitures() {
    	try {
			File path = new File(DrawApplicationModel.class.getResource(CustomFurniture.CUSTOM_FURNITURE_PATH).toURI());
			for (File f : path.listFiles()) {
				if(f.isDirectory()) {
					String catalog = f.getName(); 
					List<CustomFurniture> furnitures = CustomFurniture.loadFurnituresByCatalog(catalog);
					catalogues.add(catalog);
					this.furnitures.addAll(furnitures);
				}
			}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * Creates toolbars for the application.
     * This class always returns an empty list. Subclasses may return other
     * values.
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
        addCreationButtonsTo(tb, editor);
        tb.setName(labels.getString("window.drawToolBar.title"));
        list.add(tb);
        //------------------------------------------------------------
        ButtonGroup buttonGroup = (ButtonGroup) tb.getClientProperty("toolButtonGroup");
        ToolListener listener = (ToolListener) tb.getClientProperty("toolHandler");
        tb = new JToolBar();
        tb.putClientProperty(TOOLBAR_BUTTONGROUP_PROPKEY, buttonGroup);
        tb.putClientProperty(TOOLBAR_HANDLER_PROPKEY, listener);
        addFlatElementButtonsTo(tb, editor);
        tb.setName(customLabels.getString("window.flatElementsToolBar.title"));
        list.add(tb);
        
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
    	
    	ButtonFactory.addToolTo(tb, editor, new CreationTool(new WallFigure()), "edit.createWall", customLabels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new WindowFigure()), "edit.createWindow", customLabels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new DoorFigure()), "edit.createDoor", customLabels);
        
        tb.addSeparator();
        
        
        try {
			ButtonGroup group = (ButtonGroup) tb.getClientProperty(TOOLBAR_BUTTONGROUP_PROPKEY);
			System.out.println(catalogues);
			System.out.println(furnitures);
			for (String catalog : catalogues) {
				JPopupButton btnCatalog = new JPopupButton();
				btnCatalog.setFocusable(false);
				btnCatalog.setToolTipText(catalog);
				boolean first = true;
				for (int i = 0; i < furnitures.size(); i++) {
					CustomFurniture furniture = furnitures.get(i);
					if (!furniture.getCatalog().equals(catalog)) {
						continue;
					}
					if (first) {
						btnCatalog.setIcon(furniture.getIcon());
						first = false;
					}
					JToggleButton button = new JToggleButton(furniture.getIcon());
					button.setPreferredSize(new Dimension(22,22));
					button.setToolTipText(furniture.getName());
					Tool furnitureCreationTool = new CreationTool(furniture.getFigure());
					button.addItemListener(new ToolButtonListener(furnitureCreationTool, editor, btnCatalog));
			        button.setFocusable(false);
			        furnitureCreationTool.addToolListener((ToolListener) tb.getClientProperty(TOOLBAR_HANDLER_PROPKEY));
			        group.add(button);
			        btnCatalog.add(button);
				}
				tb.add(btnCatalog);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void addCreationButtonsTo(JToolBar tb, DrawingEditor editor) {
        addDefaultCreationButtonsTo(tb, editor,
                ButtonFactory.createDrawingActions(editor),
                ButtonFactory.createSelectionActions(editor));
    }

    public void addDefaultCreationButtonsTo(JToolBar tb, final DrawingEditor editor,
            Collection<Action> drawingActions, Collection<Action> selectionActions) {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        ResourceBundleUtil customLabels = ResourceBundleUtil.getBundle(CUSTOM_LABELS);
        
        FurnitureSaveAction fsAction = new FurnitureSaveAction(editor, catalogues.toArray(new String[0]));
        
        customLabels.configureAction(fsAction, FurnitureSaveAction.ID);
        selectionActions.add(fsAction);
        
        
        ButtonFactory.addSelectionToolTo(tb, editor, drawingActions, selectionActions);
        tb.addSeparator();

        AbstractAttributedFigure af;
        CreationTool ct;
        ConnectionTool cnt;
        ConnectionFigure lc;

        ButtonFactory.addToolTo(tb, editor, new CreationTool(new RectangleFigure()), "edit.createRectangle", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new RoundRectangleFigure()), "edit.createRoundRectangle", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new EllipseFigure()), "edit.createEllipse", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new DiamondFigure()), "edit.createDiamond", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new TriangleFigure()), "edit.createTriangle", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new LineFigure()), "edit.createLine", labels);
        
        ButtonFactory.addToolTo(tb, editor, ct = new CreationTool(new LineFigure()), "edit.createArrow", labels);
        af = (AbstractAttributedFigure) ct.getPrototype(); 
        af.set(END_DECORATION, new ArrowTip(0.35, 12, 11.3));
        ButtonFactory.addToolTo(tb, editor, new ConnectionTool(new LineConnectionFigure()), "edit.createLineConnection", labels);
        ButtonFactory.addToolTo(tb, editor, cnt = new ConnectionTool(new LineConnectionFigure()), "edit.createElbowConnection", labels);
        lc = cnt.getPrototype();
        lc.setLiner(new ElbowLiner());
        ButtonFactory.addToolTo(tb, editor, cnt = new ConnectionTool(new LineConnectionFigure()), "edit.createCurvedConnection", labels);
        lc = cnt.getPrototype();
        lc.setLiner(new CurvedLiner());
        ButtonFactory.addToolTo(tb, editor, new BezierTool(new BezierFigure()), "edit.createScribble", labels);
        ButtonFactory.addToolTo(tb, editor, new BezierTool(new BezierFigure(true)), "edit.createPolygon", labels);
        ButtonFactory.addToolTo(tb, editor, new TextCreationTool(new TextFigure()), "edit.createText", labels);
        ButtonFactory.addToolTo(tb, editor, new TextAreaCreationTool(new TextAreaFigure()), "edit.createTextArea", labels);
        ButtonFactory.addToolTo(tb, editor, new ImageTool(new ImageFigure()), "edit.createImage", labels);
    }

    @Override
    public URIChooser createOpenChooser(Application a, View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("Drawing .xml","xml"));
        return c;
    }

    @Override
    public URIChooser createSaveChooser(Application a, View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("Drawing .xml","xml"));
        return c;
    }


}
