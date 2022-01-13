/*
 * @(#)DrawFigureFactory.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package de.rwth.oosc;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.DiamondFigure;
import org.jhotdraw.draw.QuadTreeDrawing;
import org.jhotdraw.draw.connector.ChopBezierConnector;
import org.jhotdraw.draw.connector.ChopDiamondConnector;
import org.jhotdraw.draw.connector.ChopEllipseConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.ChopRoundRectangleConnector;
import org.jhotdraw.draw.connector.ChopTriangleConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.xml.DefaultDOMFactory;

import de.rwth.oosc.figures.svg.SVGBezierFigure;
import de.rwth.oosc.figures.svg.SVGEllipseFigure;
import de.rwth.oosc.figures.svg.SVGGroupFigure;
import de.rwth.oosc.figures.svg.SVGImageFigure;
import de.rwth.oosc.figures.svg.SVGPathFigure;
import de.rwth.oosc.figures.svg.SVGRectFigure;
import de.rwth.oosc.figures.svg.SVGTextAreaFigure;
import de.rwth.oosc.figures.svg.SVGTextFigure;
import de.rwth.oosc.figures.svg.SVGTriangleFigure;
/**
 * DrawFigureFactory.
 *
 * @author  Werner Randelshofer
 * @version $Id: DrawFigureFactory.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class DrawFigureFactory extends DefaultDOMFactory {
    private static final Object[][] classTagArray = {
        { DefaultDrawing.class, "drawing" },
        { QuadTreeDrawing.class, "drawing" },
        { DiamondFigure.class, "diamond" },
        { SVGTriangleFigure.class, "triangle" },
        { SVGBezierFigure.class, "bezier" },
        { SVGRectFigure.class, "r" },
        { SVGPathFigure.class, "l" },
        { SVGEllipseFigure.class, "e" },
        { SVGTextFigure.class, "t" },
        { SVGTextAreaFigure.class, "ta" },
        { SVGImageFigure.class, "image" },
        { SVGGroupFigure.class, "g" },
        
        { ArrowTip.class, "arrowTip" },
        { ChopRectangleConnector.class, "rConnector" },
        { ChopEllipseConnector.class, "ellipseConnector" },
        { ChopRoundRectangleConnector.class, "rrConnector" },
        { ChopTriangleConnector.class, "triangleConnector" },
        { ChopDiamondConnector.class, "diamondConnector" },
        { ChopBezierConnector.class, "bezierConnector" },
        
        { ElbowLiner.class, "elbowLiner" },
        { CurvedLiner.class, "curvedLiner" },
    };
    private static final Object[][] enumTagArray = {
        { AttributeKeys.StrokePlacement.class, "strokePlacement" },
        { AttributeKeys.StrokeType.class, "strokeType" },
        { AttributeKeys.Underfill.class, "underfill" },
        { AttributeKeys.Orientation.class, "orientation" },
    };
    
    /** Creates a new instance. */
    public DrawFigureFactory() {
        for (Object[] o : classTagArray) {
            addStorableClass((String) o[1], (Class<?>) o[0]);
        }
        for (Object[] o : enumTagArray) {
            addEnumClass((String) o[1], (Class<?>) o[0]);
        }
    }
}
