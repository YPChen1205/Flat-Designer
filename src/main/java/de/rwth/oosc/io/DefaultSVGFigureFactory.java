/*
 * @(#)DefaultSVGFigureFactory.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package de.rwth.oosc.io;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.geom.BezierPath;

import de.rwth.oosc.figures.FurnitureFigure;
import de.rwth.oosc.figures.structure.DoorFigure;
import de.rwth.oosc.figures.structure.WallFigure;
import de.rwth.oosc.figures.structure.WindowFigure;
import de.rwth.oosc.figures.svg.Gradient;
import de.rwth.oosc.figures.svg.LinearGradient;
import de.rwth.oosc.figures.svg.RadialGradient;
import de.rwth.oosc.figures.svg.SVGBezierFigure;
import de.rwth.oosc.figures.svg.SVGEllipseFigure;
import de.rwth.oosc.figures.svg.SVGGroupFigure;
import de.rwth.oosc.figures.svg.SVGImageFigure;
import de.rwth.oosc.figures.svg.SVGPathFigure;
import de.rwth.oosc.figures.svg.SVGRectFigure;
import de.rwth.oosc.figures.svg.SVGTextAreaFigure;
import de.rwth.oosc.figures.svg.SVGTextFigure;

/**
 * DefaultSVGFigureFactory.
 *
 * @author Werner Randelshofer
 * @version $Id: DefaultSVGFigureFactory.java 785 2013-12-01 19:16:30Z rawcoder $
 */
public class DefaultSVGFigureFactory implements SVGFigureFactory {
    
    /** Creates a new instance. */
    public DefaultSVGFigureFactory() {
    }
    
    @Override
    public Figure createRect(double x, double y, double w, double h, double rx, double ry, Map<AttributeKey<?>, Object> a) {
        SVGRectFigure figure = new SVGRectFigure();
        figure.setBounds(new Point2D.Double(x,y),new Point2D.Double(x+w,y+h));
        figure.setArc(rx, ry);
        figure.setAttributes(a);
        return figure;
    }
    
    @Override
    public Figure createCircle(double cx, double cy, double r, Map<AttributeKey<?>, Object> a) {
        return createEllipse(cx, cy, r, r, a);
    }
    
    @Override
    public Figure createEllipse(double cx, double cy, double rx, double ry, Map<AttributeKey<?>, Object> a) {
        SVGEllipseFigure figure = new SVGEllipseFigure(cx-rx, cy-ry, rx*2d, ry*2d);
        figure.setAttributes(a);
        return figure;
    }
    
    @Override
    public Figure createLine(
            double x1, double y1, double x2, double y2,
            Map<AttributeKey<?>,Object> a) {
        SVGPathFigure figure = new SVGPathFigure();
        figure.removeAllChildren();
        SVGBezierFigure bf = new SVGBezierFigure();
        bf.addNode(new BezierPath.Node(x1, y1));
        bf.addNode(new BezierPath.Node(x2, y2));
        figure.add(bf);
        figure.setAttributes(a);
        return figure;
    }
    
    @Override
    public Figure createPolyline(Point2D.Double[] points, Map<AttributeKey<?>, Object> a) {
        SVGPathFigure figure = new SVGPathFigure();
        figure.removeAllChildren();
        SVGBezierFigure bf = new SVGBezierFigure();
        for (int i=0; i < points.length; i++) {
            bf.addNode(new BezierPath.Node(points[i].x, points[i].y));
        }
        figure.add(bf);
        figure.setAttributes(a);
        return figure;
    }
    
    @Override
    public Figure createPolygon(Point2D.Double[] points, Map<AttributeKey<?>, Object> a) {
        SVGPathFigure figure = new SVGPathFigure();
        figure.removeAllChildren();
        SVGBezierFigure bf = new SVGBezierFigure();
        for (int i=0; i < points.length; i++) {
            bf.addNode(new BezierPath.Node(points[i].x, points[i].y));
        }
        bf.setClosed(true);
        figure.add(bf);
        figure.setAttributes(a);
        return figure;
    }
    @Override
    public Figure createPath(BezierPath[] beziers, Map<AttributeKey<?>, Object> a) {
        SVGPathFigure figure = new SVGPathFigure();
        figure.removeAllChildren();
        for (int i=0; i < beziers.length; i++) {
            SVGBezierFigure bf = new SVGBezierFigure();
            bf.setBezierPath(beziers[i]);
            figure.add(bf);
        }
        figure.setAttributes(a);
        return figure;
    }
    
    @Override
    public CompositeFigure createG(Map<AttributeKey<?>, Object> a) {
        SVGGroupFigure figure = new SVGGroupFigure();
        figure.setAttributes(a);
        return figure;
    }
    
    @Override
    public Figure createImage(double x, double y, double w, double h, 
            byte[] imageData, BufferedImage bufferedImage, Map<AttributeKey<?>, Object> a) {
        SVGImageFigure figure = new SVGImageFigure();
        figure.setBounds(new Point2D.Double(x,y),new Point2D.Double(x+w,y+h));
        figure.setImage(imageData, bufferedImage);
        figure.setAttributes(a);
        return figure;
    }
    @Override
    public Figure createTextArea(double x, double y, double w, double h, StyledDocument doc, Map<AttributeKey<?>, Object> attributes) {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();
        figure.setBounds(new Point2D.Double(x,y),new Point2D.Double(x+w,y+h));
        try {
            figure.setText(doc.getText(0, doc.getLength()));
        } catch (BadLocationException e) {
            InternalError ex = new InternalError(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
        figure.setAttributes(attributes);
        return figure;
    }
    
    @Override
    public Figure createText(Point2D.Double[] coordinates, double[] rotates, StyledDocument text, Map<AttributeKey<?>, Object> a) {
        SVGTextFigure figure = new SVGTextFigure();
        figure.setCoordinates(coordinates);
        figure.setRotates(rotates);
        try {
            figure.setText(text.getText(0, text.getLength()));
        } catch (BadLocationException e) {
            InternalError ex = new InternalError(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
        figure.setAttributes(a);
        return figure;
    }
    
    @Override
    public Gradient createRadialGradient(
            double cx, double cy, double fx, double fy, double r,
            double[] stopOffsets, Color[] stopColors, double[] stopOpacities,
            boolean isRelativeToFigureBounds,
            AffineTransform tx) {
        return new RadialGradient(cx, cy, fx, fy, r,
                stopOffsets, stopColors, stopOpacities,
                isRelativeToFigureBounds,
                tx);
    }
    
    @Override
    public Gradient createLinearGradient(
            double x1, double y1, double x2, double y2,
            double[] stopOffsets, Color[] stopColors, double[] stopOpacities,
            boolean isRelativeToFigureBounds,
            AffineTransform tx) {
        return new LinearGradient(x1, y1, x2, y2,
                stopOffsets, stopColors, stopOpacities,
                isRelativeToFigureBounds,
                tx);
    }

	@Override
	public Figure createWall(BezierPath[] beziers, Map<AttributeKey<?>, Object> attributes) {
		WallFigure figure = new WallFigure();
        figure.removeAllChildren();
        for (int i=0; i < beziers.length; i++) {
            SVGBezierFigure bf = new SVGBezierFigure();
            bf.setBezierPath(beziers[i]);
            figure.add(bf);
        }
        figure.setAttributes(attributes);
        return figure;
	}

	@Override
	public Figure createWindow(double x, double y, double w, double h, double rx, double ry,
			Map<AttributeKey<?>, Object> attributes) {
		WindowFigure figure = new WindowFigure();
        figure.setBounds(new Point2D.Double(x,y),new Point2D.Double(x+w,y+h));
        figure.setArc(rx, ry);
        figure.setAttributes(attributes);
        return figure;
	}

	@Override
	public Figure createDoor(Double bounds, Map<AttributeKey<?>, Object> attributes) {
		DoorFigure door = new DoorFigure();
		door.setBounds(bounds);
		door.setAttributes(attributes);
		return door;
	}

	@Override
	public Figure createFurniture(SVGGroupFigure furnitureFigure, Map<AttributeKey<?>,Object> attributes) {
		FurnitureFigure f = new FurnitureFigure(furnitureFigure);
		f.setAttributes(attributes);
		return f;
	}

}
