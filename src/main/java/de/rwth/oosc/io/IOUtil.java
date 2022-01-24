package de.rwth.oosc.io;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;

import de.rwth.oosc.figures.FurnitureFigure;
import de.rwth.oosc.figures.svg.SVGGroupFigure;
import de.rwth.oosc.furniture.CustomFurniture;

public class IOUtil {

	public static final String CUSTOM_FURNITURE_PATH = "/de/rwth/oosc/flatdesigner/customfurnitures/";

	private static SVGInputFormat inFormat = new SVGInputFormat();
	private static SVGOutputFormat outFormat = new SVGOutputFormat();
	
	public static Map<String, Set<CustomFurniture>> loadDefaultModel() {
		
		Map<String, Set<CustomFurniture>> fMap = new HashMap<String, Set<CustomFurniture>>();

		try {
			fMap = load(new File(IOUtil.class.getResource(CUSTOM_FURNITURE_PATH).toURI()).getAbsolutePath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return fMap;
	}

	public static Map<String, Set<CustomFurniture>> load(String path) throws URISyntaxException {
		Map<String, Set<CustomFurniture>> fMap = new HashMap<String, Set<CustomFurniture>>();
		File dirStructure = new File(path);

		if (!dirStructure.isDirectory()) {
			throw new RuntimeException(path + " is not a directory.");
		}

		for (File catDir : dirStructure.listFiles()) {
			if (catDir.isDirectory()) {
				String category = catDir.getName();
				try {
					fMap.put(category,
							loadFurnituresByCatalogPath(dirStructure.getAbsolutePath() + File.separator + category));
					;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return fMap;
	}

//	private static String getXMLPathByCatalog(String catalog) throws URISyntaxException {
//		return new File(CustomFurniture.class.getResource(CUSTOM_FURNITURE_PATH + catalog + "/xml/").toURI()).getAbsolutePath();
//	}

	public static Set<CustomFurniture> loadFurnituresByCatalogPath(String catalogPath)
			throws URISyntaxException, IOException {
		File xmlDir = new File(getXMLDirPath(catalogPath));
		Set<CustomFurniture> furnitures = new HashSet<>();

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
		return catalogPath + File.separator + "icons";
	}

	private static String getXMLDirPath(String catalogPath) {
		return catalogPath + File.separator + "xml";
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
		drawing.addInputFormat(inFormat);

		// load XML
		inFormat.read(furnitureFile.toURI(), drawing);
		Figure figure = drawing.getChild(0).clone();
		
		// load image for icon
		BufferedImage image = ImageIO.read(iconFile);
		Icon icon = new ImageIcon(image);
		
		FurnitureFigure f;
		if (figure instanceof FurnitureFigure) {
			f = (FurnitureFigure) figure;
		} else {
			f = new FurnitureFigure((SVGGroupFigure) figure);
		}

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
		image.flush();
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
		drawing.addOutputFormat(outFormat);
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(getXMLPath(categoryPath, furniture.getName()))));
		outFormat.write(
				outputStream,
				drawing);
		outputStream.close();
		saveAsIcon(categoryPath, furniture, CustomFurniture.ICON_DIMENSION);
	}

	public static void deleteFurniture(String catalog, String furnitureName) {
		try {
			String defaultPath = new File(IOUtil.class.getResource(CUSTOM_FURNITURE_PATH).toURI()).getAbsolutePath();
			String xmlPath = getXMLPath(defaultPath + File.separator + catalog, furnitureName);
			String iconPath = getIconPath(defaultPath + File.separator + catalog, furnitureName);
			File xmlFile = new File(xmlPath);
			xmlFile.delete();
			File iconFile = new File(iconPath);
			iconFile.delete();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

	public static void deleteFurnitureCatalog(String catalog) {
		try {
			String defaultPath = new File(IOUtil.class.getResource(CUSTOM_FURNITURE_PATH).toURI()).getAbsolutePath();
			String catalogPath = defaultPath + File.separator + catalog;
			File catalogFile = new File(catalogPath);
			if (catalogFile.isDirectory()) {
				FileUtils.deleteDirectory(catalogFile);
			}
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void createCatalog(String catalog) {
		try {
			String defaultPath = new File(IOUtil.class.getResource(CUSTOM_FURNITURE_PATH).toURI()).getAbsolutePath();
			String catalogPath = defaultPath + File.separator + catalog;
			File newCatalog = new File(catalogPath);
			newCatalog.mkdir();
			new File(catalogPath + File.separator + "xml").mkdir();
			new File(catalogPath + File.separator + "icons").mkdir();			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	}
}
