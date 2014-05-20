package teal.ui.swing.skin;

import java.awt.*;
import java.awt.image.*;
import java.net.URL;

public class SkinUtils implements ImageObserver {

	static Image img = null;
	static GraphicsConfiguration conf;
	static {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		conf = ge.getDefaultScreenDevice().getDefaultConfiguration();
	}

	SkinUtils(URL url) {
		img = Toolkit.getDefaultToolkit().createImage(url);
		//System.out.println("Image: width = " + img.getWidth(null) + " height
		// = " + img.getHeight(null));
		BufferedImage img2 = conf.createCompatibleImage(1, 1);
		Graphics g = img2.getGraphics();
		g.drawImage(img, img.getWidth(null), img.getHeight(null), null);
	}
	private boolean imageLoaded = false;

	public Image getImage() {
		//if imageLoaded == true
		//		Image tempImage = createImage(1, 1);
		//		Graphics tempGraphics = tempImage.getGraphics();
		//		tempGraphics.drawImage(img, 0, 0, this);
		//System.out.println("Image: width = " + img.getWidth(null) + " height
		// = " + img.getHeight(null));
		return img;
	}

	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		if ((infoflags & ImageObserver.ALLBITS) != 0) {
			//System.out.println("Image: width = " + img.getWidth(null) + "
			// height = " + img.getHeight(null));
			imageLoaded = true;
			return false;
		} else
			return true;
	}
}