package de.rwth.oosc.furniture;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.io.DOMStorableInputOutputFormat;
import org.jhotdraw.draw.io.ImageOutputFormat;
import de.rwth.oosc.DrawFigureFactory;

public class CustomFurniture {

	public static final double ICON_DIMENSION = 15;
	
	public static final String CUSTOM_FURNITURE_PATH = "/de/rwth/oosc/flatdesigner/customfurnitures/";
	
	private String name;
	private String catalog;
	private GroupFigure groupedFigure;
	private Icon icon;
	
	private static DOMStorableInputOutputFormat inoutFormat = new DOMStorableInputOutputFormat(new DrawFigureFactory());
	private static ImageOutputFormat imageOutputFormat = new ImageOutputFormat();
	
	public CustomFurniture(GroupFigure gf) {
		this(gf, null, null);
	}
	
	public CustomFurniture(GroupFigure gf, String name, Icon icon) {
		this(gf, name, "other", icon);
	}
	
	public CustomFurniture(GroupFigure gf, String name, String catalog, Icon icon) {
		this.groupedFigure = gf;
		this.name = name;
		this.catalog = catalog;
		this.icon = icon;
	}
	
	public GroupFigure getGroupedFigure() {
		return groupedFigure;
	}
	
	public Figure getFigure() {
		return groupedFigure.clone();
	}
	
	public Icon getIcon() {
		return icon;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCatalog() {
		return catalog;
	}
	
	public static CustomFurniture loadFurniture(String name) throws IOException, URISyntaxException {
		return loadFurnitureByCatalog("other", name);
	}
	
	public static String getXMLPath(String name) {
		return getXMLPathByCatalog("other", name);
	}
	
	public static String getIconPath(String name) {
		return getIconPathByCatalog("other", name);
	}
	
	private static String getXMLPathByCatalog(String catalog) throws URISyntaxException {
		return new File(CustomFurniture.class.getResource(CUSTOM_FURNITURE_PATH + catalog + "/xml/").toURI()).getAbsolutePath();
	}
	
	private static String getIconPathByCatalog(String catalog) throws URISyntaxException {
		return new File(CustomFurniture.class.getResource(CUSTOM_FURNITURE_PATH + catalog + "/icons/").toURI()).getAbsolutePath();
	}
	
	public static String getXMLPathByCatalog(String catalog, String name) {
		try {
			return getXMLPathByCatalog(catalog) + File.separator + name + ".xml";
		} catch (URISyntaxException e) {
			throw new RuntimeException("Catalog " + catalog + " is not available");
		}
	}
	
	public static String getIconPathByCatalog(String catalog, String name) {
		try {
			return getIconPathByCatalog(catalog) + File.separator + name + ".png";
		} catch (URISyntaxException e) {
			throw new RuntimeException("Catalog " + catalog + " is not available");
		}
	}
	
	public static CustomFurniture loadFurnitureByCatalog(String catalog, String name) throws IOException, URISyntaxException {
		String xmlPath = getXMLPathByCatalog(catalog, name);
		String iconPath = getIconPathByCatalog(catalog, name);
		
		System.out.println(xmlPath);
		System.out.println(iconPath);
		
		File furnitureFile = new File(xmlPath);
		File iconFile = new File(iconPath);
		
		return loadFurniture(catalog, furnitureFile, iconFile);
	}
	
	public static List<CustomFurniture> loadFurnituresByCatalog(String catalog) throws URISyntaxException, IOException {
		System.out.println(getXMLPathByCatalog(catalog));
		File catalogXMLDir = new File(getXMLPathByCatalog(catalog));
		List<CustomFurniture> furnitures = new ArrayList<>();
		
		for (File xmlFile : catalogXMLDir.listFiles()) {
			if (xmlFile.isFile()) {
				String name = xmlFile.getName().substring(0, xmlFile.getName().length() - 4);
				furnitures.add(loadFurnitureByCatalog(catalog, name));
			}
		}
		
		return furnitures;
	}
	
	public static CustomFurniture loadFurniture(String catalog, File furnitureFile, File iconFile) throws IOException, URISyntaxException {
		Drawing drawing = new DefaultDrawing();
		drawing.addInputFormat(inoutFormat);
		
		// load XML
		drawing.getInputFormats().get(0).read(furnitureFile.toURI(), drawing);
		GroupFigure f = (GroupFigure) drawing.getChild(0);
		
		// load image for icon
		BufferedImage image = ImageIO.read(iconFile);
		Icon icon = new ImageIcon(image);
		
		return new CustomFurniture(f, furnitureFile.getName().substring(0, furnitureFile.getName().length() - 4), catalog, icon);
	}
	
	private void saveAsIcon(String name, String category) throws FileNotFoundException, IOException, URISyntaxException {
		Drawing drawing = new DefaultDrawing();
		Figure f = groupedFigure.clone();
		f.setBounds(new Point2D.Double(0,0), new Point2D.Double(ICON_DIMENSION, ICON_DIMENSION));
		drawing.add(f);
		drawing.set(AttributeKeys.CANVAS_HEIGHT, ICON_DIMENSION);
		drawing.set(AttributeKeys.CANVAS_WIDTH, ICON_DIMENSION);
		drawing.addOutputFormat(imageOutputFormat);
		BufferedImage image = imageOutputFormat.toImage(drawing, Arrays.asList(f), 1, true);
		ImageIO.write(image, "PNG", new File(getIconPathByCatalog(category) + File.separator + name + ".png"));
	}
	
	public void saveFigure(String name, String category) throws FileNotFoundException, IOException, URISyntaxException {
		Drawing drawing = new DefaultDrawing();
		drawing.add(groupedFigure);
		drawing.addOutputFormat(inoutFormat);
		drawing.getOutputFormats().get(0).write(new BufferedOutputStream(
				new FileOutputStream(
						new File(getXMLPathByCatalog(category, name)))), drawing);
		saveAsIcon(name, category);
	}
	
	public void saveFigure(String name) throws FileNotFoundException, IOException, URISyntaxException {
		saveFigure(name, "other");
	}
	
	@Override
	public String toString() {
		return getName() + " (Catalogue: " + getCatalog() + ")";
	}
}
