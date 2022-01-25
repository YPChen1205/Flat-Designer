package de.rwth.oosc.furniture;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.io.ImageOutputFormat;

import de.rwth.oosc.figures.FurnitureFigure;
import de.rwth.oosc.figures.svg.SVGGroupFigure;

public class CustomFurniture {

	public static final double ICON_DIMENSION = 15;
	
	private String name;
	private FurnitureFigure drawFigure;
	private Icon icon;
	
	public CustomFurniture(String name, FurnitureFigure f, Icon icon) {
		this.drawFigure = f;
		this.name = name;
		this.icon = icon;
	}
	
	public CustomFurniture(SVGGroupFigure gf) {
		this("", gf);
	}
	
	public CustomFurniture(String name, SVGGroupFigure gf) {
		this(name, gf, null);
		
		Figure f = gf.clone();
		ImageOutputFormat imageOutputFormat = new ImageOutputFormat();
		Drawing drawing = new DefaultDrawing();
		f.setBounds(new Point2D.Double(0,0), new Point2D.Double(ICON_DIMENSION, ICON_DIMENSION));
		drawing.add(f);
		drawing.set(AttributeKeys.CANVAS_HEIGHT, ICON_DIMENSION);
		drawing.set(AttributeKeys.CANVAS_WIDTH, ICON_DIMENSION);
		drawing.addOutputFormat(imageOutputFormat);
		BufferedImage image = imageOutputFormat.toImage(drawing, Arrays.asList(f), 1, true);
		setIcon(new ImageIcon(image));
	}
	
	public CustomFurniture(String name, SVGGroupFigure gf, Icon icon) {
		this(name, new FurnitureFigure(gf), icon);
	}
	
	public Figure getFigure() {
		return drawFigure.clone();
	}
	
	private void setIcon(Icon icon) {
		this.icon = icon;
	}
	
	public Icon getIcon() {
		return icon;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
