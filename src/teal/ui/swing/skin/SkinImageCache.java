package teal.ui.swing.skin;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * A cache for the skin images. It is used as a singleton.
 *  
 */
public class SkinImageCache {

	private static SkinImageCache instance = new SkinImageCache();
	private HashMap<String, Image> map;
	//private HashMap iconMap;
	private HashMap<String, BufferedImage> bufferedMap;
	static GraphicsConfiguration conf;
	static {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		conf = ge.getDefaultScreenDevice().getDefaultConfiguration();
	}

	protected SkinImageCache() {
		map = new HashMap<String, Image>();
		//iconMap = new HashMap();
		bufferedMap = new HashMap<String, BufferedImage>();
	}

	/**
	 * Loads the image file with fileName <code>fileName</code> as an
	 * automatic image. For images with bitmask transparency or no transparency
	 * the image should be hardware accelerated.
	 * 
	 * @param fileName
	 *            the file name of the image file to load
	 * @return Image
	 */
	public Image getAutomaticImage(String fileName) {
		Image ret = (Image) map.get(fileName);
		if (ret == null) {
			Toolkit tk = Toolkit.getDefaultToolkit();
			SkinLoader image = null;
			try {
				image = new SkinLoader(fileName, tk);
			} catch (Exception e) {
				e.printStackTrace();
			}
			map.put(fileName, image.getImage());
			return image.getImage();
		}
		return ret;
	}

	/**
	 * Loads the image file with fileName <code>fileName</code>.
	 * 
	 * @param fileName
	 *            the file name of the image file to load
	 * @return Image
	 */
	public Image getImage(String fileName) {
		return getAutomaticImage(fileName);
	}

	/**
	 * Loads the image file with fileName <code>fileName</code> as an buffered
	 * image. This is basically not hardware accelerated.
	 * 
	 * @param fileName
	 *            the file name of the image file to load
	 * @return Image
	 */
	public BufferedImage getBufferedImage(String fileName) {
		BufferedImage b = (BufferedImage) bufferedMap.get(fileName);
		if (b != null)
			return b;
		Image img = getImage(fileName);
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		BufferedImage img2 = conf.createCompatibleImage(w, h);
		Graphics g = img2.getGraphics();
		g.drawImage(img, 0, 0, w, h, 0, 0, w, h, null);
		bufferedMap.put(fileName, img2);
		return img2;
	}

	/**
	 * Returns the only instance of the image cache
	 * 
	 * @return SkinImageCache
	 */
	public static SkinImageCache getInstance() {
		return instance;
	}
}