package de.rwth.oosc.util;

import java.awt.Graphics2D;
import java.awt.Image;
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
import de.rwth.oosc.furniture.CustomFurniture;
import de.rwth.oosc.furniture.FurnitureModel;

public class IOUtil {

	public static final String CUSTOM_FURNITURE_PATH = "/de/rwth/oosc/flatdesigner/customfurnitures/";

	private static DOMStorableInputOutputFormat inoutFormat = new DOMStorableInputOutputFormat(new DrawFigureFactory());

	public static FurnitureModel loadDefaultModel() {
		FurnitureModel fmodel = FurnitureModel.getFmodel();

		try {
			fmodel = load(new File(IOUtil.class.getResource(CUSTOM_FURNITURE_PATH).toURI()).getAbsolutePath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return fmodel;
	}

	public static FurnitureModel load(String path) throws URISyntaxException {
		FurnitureModel model = FurnitureModel.getFmodel();
		File dirStructure = new File(path);

		if (!dirStructure.isDirectory()) {
			throw new RuntimeException(path + " is not a directory.");
		}

		for (File catDir : dirStructure.listFiles()) {
			if (catDir.isDirectory()) {
				String category = catDir.getName();
				try {
					model.addFurnitures(category,
							loadFurnituresByCatalogPath(dirStructure.getAbsolutePath() + File.separator + category));
					;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return model;
	}

//	private static String getXMLPathByCatalog(String catalog) throws URISyntaxException {
//		return new File(CustomFurniture.class.getResource(CUSTOM_FURNITURE_PATH + catalog + "/xml/").toURI()).getAbsolutePath();
//	}

	public static List<CustomFurniture> loadFurnituresByCatalogPath(String catalogPath)
			throws URISyntaxException, IOException {
		File xmlDir = new File(getXMLDirPath(catalogPath));
		List<CustomFurniture> furnitures = new ArrayList<>();

		for (File xmlFile : xmlDir.listFiles()) {
			if (xmlFile.isFile()) {
				String name = xmlFile.getName().substring(0, xmlFile.getName().length() - 4);
				File iconFile = new File(getIconPath(catalogPath, name));
				furnitures.add(loadFurniture(xmlFile, iconFile));
			}
		}

		return furnitures;
	}

	private static String getIconDirPath(String catalogPath) {
		return catalogPath + "/icons";
	}

	private static String getXMLDirPath(String catalogPath) {
		return catalogPath + "/xml";
	}

	public static String getXMLPath(String catalogPath, String name) {
		return getXMLDirPath(catalogPath) + File.separator + name + ".xml";
	}

	public static String getIconPath(String catalogPath, String name) throws URISyntaxException {
		return getIconDirPath(catalogPath) + File.separator + name + ".png";
	}

	public static CustomFurniture loadFurniture(File furnitureFile, File iconFile)
			throws IOException, URISyntaxException {
		Drawing drawing = new DefaultDrawing();
		drawing.addInputFormat(inoutFormat);

		// load XML
		drawing.getInputFormats().get(0).read(furnitureFile.toURI(), drawing);
		GroupFigure f = (GroupFigure) drawing.getChild(0);

		// load image for icon
		BufferedImage image = ImageIO.read(iconFile);
		Icon icon = new ImageIcon(image);

		return new CustomFurniture(furnitureFile.getName().substring(0, furnitureFile.getName().length() - 4), f, icon);
	}

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	private static void saveAsIcon(String categoryPath, CustomFurniture furniture, double dimension)
			throws FileNotFoundException, IOException, URISyntaxException {
		BufferedImage image = toBufferedImage(((ImageIcon) furniture.getIcon()).getImage());
		ImageIO.write(image, "PNG", new File(getIconPath(categoryPath, furniture.getName())));
	}

	public static void saveFurnitureDefault(String catalogue, CustomFurniture furniture) {
		try {
			String defaultPath = new File(IOUtil.class.getResource(CUSTOM_FURNITURE_PATH).toURI()).getAbsolutePath();
			saveFurniture(defaultPath + File.separator + catalogue, furniture);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveFurniture(String categoryPath, CustomFurniture furniture)
			throws FileNotFoundException, IOException, URISyntaxException {
		Drawing drawing = new DefaultDrawing();
		drawing.add(furniture.getFigure());
		drawing.addOutputFormat(inoutFormat);
		drawing.getOutputFormats().get(0).write(
				new BufferedOutputStream(new FileOutputStream(new File(getXMLPath(categoryPath, furniture.getName())))),
				drawing);
		saveAsIcon(categoryPath, furniture, CustomFurniture.ICON_DIMENSION);
	}

	public static void deleteFurniture(String catalog, String furnitureName) {
		try {
			String defaultPath = new File(IOUtil.class.getResource(CUSTOM_FURNITURE_PATH).toURI()).getAbsolutePath();
			String xmlPath = getXMLPath(defaultPath + File.separator + catalog, furnitureName);
			String iconPath = getIconPath(defaultPath + File.separator + catalog, furnitureName);
			File xmlFile = new File(xmlPath);
			System.out.println(xmlFile);
			xmlFile.delete();
			File iconFile = new File(iconPath);
			System.out.println(iconPath);
			iconFile.delete();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}
}
