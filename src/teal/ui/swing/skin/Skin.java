package teal.ui.swing.skin;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * This class is for a skin that can be used to "skin" a component. It's most
 * important feature is to draw an image that can be scaled to fit onto the
 * component. All in all there are nine regions that are treated differently.
 * These regions can be grouped in three cases: 1. The corners are not stretched
 * at all, but take the space as described by the members ulX,ulY,lrX and lrY.
 * They denote the distance from the corner of the image to the rectangle that
 * will be scaled to fit the component. This means that the rectangle
 * (0,0)-(ulX,ulY) will not be scaled at all, the rectangle
 * (ulX,0)-(sizeX-lrX-1,ulY) is scaled only horizontally and (sizeX-lrX,ulY)
 * will not be scaled again. For the upper left corner this means that the
 * Rectangle (0,0) - (ulX,ulY) is painted with the same rectangle from the skin
 * image. 2. The edges of the component. These are the rectangles that fit in
 * between the corners. They will be stretched but only either horizontally or
 * vertically. 3. The center. This is the remaining space of the skin. It will
 * be scaled both horizontally and vertically.
 * 
 * Note that if ulX,ulY,lrX and lrY are set to zero there will be a optimization
 * to improve speed for components that have a fixed size (The skin should be as
 * big as the component in this case).
 * 
 * Each skin file consists of several images that are place next to each other.
 * The constructor for Skin takes the number of images as an argument. When the
 * skin is painted there's an argument for the index of the subimage to be used.
 * The index starts with 0.
 * 
 * @see teal.ui.swing.skin.SkinSimpleButtonIndexModel
 * @see teal.ui.swing.skin.SkinToggleButtonIndexModel
 */
public class Skin extends SkinElement {

	/** the number of subimages in the skin */
	private int nrImages;
	/** the horizontal size of each subimage */
	private int hsize;
	/** the vertical size of each subimage */
	private int vsize;
	/** the distance from the left edge to the scaling region of the skin */
	private int ulX;
	/** the distance from the top edge to the scaling region of the skin */
	private int ulY;
	/** the distance from the right edge to the scaling region of the skin */
	private int lrX;
	/** the distance from the bottom edge to the scaling region of the skin */
	private int lrY;
	/** true if roundedSize==0 => optimization */
	private boolean noBorder = false;

	/**
	 * Creates a new skin from the image file with fileName fileName and the
	 * number of images passed in <code>nrImages</code>. The scaling region
	 * of the image is given by ulX,ulY,lrX,lrY
	 * 
	 * @param fileName
	 *            the filename of the image file
	 * @param nrImages
	 *            the number of subimages in the image file
	 * @param ulX
	 *            the distance from the left edge to the scaling region of the
	 *            skin
	 * @param ulY
	 *            the distance from the top edge to the scaling region of the
	 *            skin
	 * @param lrX
	 *            the distance from the right edge to the scaling region of the
	 *            skin
	 * @param lrY
	 *            the distance from the bottom edge to the scaling region of the
	 *            skin
	 */
	public Skin(String fileName, int nrImages, int ulX, int ulY, int lrX, int lrY) {
		super(fileName, true);
		this.nrImages = nrImages;
		this.ulX = ulX;
		this.ulY = ulY;
		this.lrX = lrX;
		this.lrY = lrY;
		calculateSizes();
	}

	/**
	 * Creates a new skin from the image file with fileName fileName and the
	 * number of images passed in <code>nrImages</code>. The size of the
	 * corners is given by roundedSize.
	 * 
	 * @param fileName
	 *            the filename of the image file
	 * @param nrImages
	 *            the number of subimages in the image file
	 * @param roundedSize
	 *            the distance from the each edge to the scaling region of the
	 *            skin
	 */
	public Skin(String fileName, int nrImages, int roundedSize) {
		this(fileName, nrImages, roundedSize, roundedSize, roundedSize, roundedSize);
		if (roundedSize == 0)
			noBorder = true;
	}

	/**
	 * Use the image with index index to paint the component with size sizeX,
	 * sizeY
	 * 
	 * @param g
	 * @param index
	 *            index of the image in the skin file
	 * @param sizeX
	 *            horizontal size of the component
	 * @param sizeY
	 *            vertical size of the component
	 */
	public void draw(Graphics g, int index, int sizeX, int sizeY) {
		int offset = index * getHsize();
		if (!noBorder) {
			// lo
			g.drawImage(getImage(), 0, 0, ulX, ulY, offset + 0, 0, offset + ulX, ulY, null);
			// mo
			g.drawImage(getImage(), ulX, 0, sizeX - lrX, ulY, offset + ulX, 0, offset + hsize - lrX, ulY, null);
			// ro
			g.drawImage(getImage(), sizeX - lrX, 0, sizeX, ulY, offset + hsize - lrX, 0, offset + hsize, ulY, null);
			// lm
			g.drawImage(getImage(), 0, ulY, ulX, sizeY - lrY, offset + 0, ulY, offset + ulX, vsize - lrY, null);
			// rm
			g.drawImage(getImage(), sizeX - lrX, ulY, sizeX, sizeY - lrY, offset + hsize - lrX, ulY, offset + hsize, vsize - lrY, null);
			// lu
			g.drawImage(getImage(), 0, sizeY - lrY, ulX, sizeY, offset + 0, vsize - lrY, offset + ulX, vsize, null);
			// mu
			g.drawImage(getImage(), ulX, sizeY - lrY, sizeX - lrX, sizeY, offset + ulX, vsize - lrY, offset + hsize - lrX, vsize, null);
			// ru
			g.drawImage(getImage(), sizeX - lrX, sizeY - lrY, sizeX, sizeY, offset + hsize - lrX, vsize - lrY, offset + hsize, vsize, null);
			g.drawImage(getImage(), ulX, ulY, sizeX - lrX, sizeY - lrY, offset + ulX, ulY, offset + hsize - lrX, vsize - lrY, null);
		} else {
			g.drawImage(getImage(), 0, 0, sizeX, sizeY, offset, 0, offset + hsize, vsize, null);
		}
		//		System.out.println("TIME :" + (summedTime/timesCalled) );
	}

	/**
	 * Use the image with index index to paint the component at point x, y with
	 * dimension sizeX, sizeY
	 * 
	 * @param g
	 * @param index
	 *            index of the image in the skin file
	 * @param x
	 *            x coordiante of the point where the skin is painted
	 * @param y
	 *            y coordiante of the point where the skin is painted
	 * @param sizeX
	 *            horizontal size of the component
	 * @param sizeY
	 *            vertical size of the component
	 */
	public void draw(Graphics g, int index, int x, int y, int sizeX, int sizeY) {
		int offset = index * getHsize();
		if (!noBorder) {
			// lo
			g.drawImage(getImage(), x + 0, y + 0, x + ulX, y + ulY, offset + 0, 0, offset + ulX, ulY, null);
			// mo
			g.drawImage(getImage(), x + ulX, y + 0, x + sizeX - lrX, y + ulY, offset + ulX, 0, offset + hsize - lrX, ulY, null);
			// ro
			g.drawImage(getImage(), x + sizeX - lrX, y + 0, x + sizeX, y + ulY, offset + hsize - lrX, 0, offset + hsize, ulY, null);
			// lm
			g.drawImage(getImage(), x + 0, y + ulY, x + ulX, y + sizeY - lrY, offset + 0, ulY, offset + ulX, vsize - lrY, null);
			// rm
			g.drawImage(getImage(), x + sizeX - lrX, y + ulY, x + sizeX, y + sizeY - lrY, offset + hsize - lrX, ulY, offset + hsize, vsize - lrY,
					null);
			// lu
			g.drawImage(getImage(), x + 0, y + sizeY - lrY, x + ulX, y + sizeY, offset + 0, vsize - lrY, offset + ulX, vsize, null);
			// mu
			g.drawImage(getImage(), x + ulX, y + sizeY - lrY, x + sizeX - lrX, y + sizeY, offset + ulX, vsize - lrY, offset + hsize - lrX, vsize,
					null);
			// ru
			g.drawImage(getImage(), x + sizeX - lrX, y + sizeY - lrY, x + sizeX, y + sizeY, offset + hsize - lrX, vsize - lrY, offset + hsize, vsize,
					null);
			g.drawImage(getImage(), x + ulX, y + ulY, x + sizeX - lrX, y + sizeY - lrY, offset + ulX, ulY, offset + hsize - lrX, vsize - lrY, null);
		} else {
			g.drawImage(getImage(), x, y, x + sizeX, y + sizeY, offset, 0, offset + hsize, vsize, null);
		}
	}

	/**
	 * Use the image with index index to paint the component with its natural
	 * size centred in a rectangle with dimension sizeX, sizeY
	 * 
	 * @param g
	 * @param index
	 *            index of the image in the skin file
	 * @param sizeX
	 *            horizontal size of the component
	 * @param sizeY
	 *            vertical size of the component
	 */
	public void drawCentered(Graphics g, int index, int sizeX, int sizeY) {
		int offset = index * getHsize();
		int w = getHsize();
		int h = getVsize();
		int sx = (sizeX - w) / 2;
		int sy = (sizeY - h) / 2;
		g.drawImage(getImage(), sx, sy, sx + w, sy + h, offset, 0, offset + w, h, null);
	}

	/**
	 * Use the image with index index to paint the component with its natural
	 * size centred in a rectangle with dimension sizeX, sizeY
	 * 
	 * @param g
	 * @param index
	 *            index of the image in the skin file
	 * @param x
	 *            x coordiante of the point where the skin is painted
	 * @param y
	 *            y coordiante of the point where the skin is painted
	 * @param sizeX
	 *            horizontal size of the component
	 * @param sizeY
	 *            vertical size of the component
	 */
	public void drawCentered(Graphics g, int index, int x, int y, int sizeX, int sizeY) {
		int offset = index * getHsize();
		int w = getHsize();
		int h = getVsize();
		int sx = (sizeX - w) / 2;
		int sy = (sizeY - h) / 2;
		g.drawImage(getImage(), x + sx, y + sy, x + sx + w, y + sy + h, offset, 0, offset + w, h, null);
	}

	/**
	 * Returns the horizontal size of the skin, this is the width of each
	 * subimage
	 * 
	 * @return int
	 */
	public int getHsize() {
		return hsize;
	}

	/**
	 * Returns the vertical size of the skin, this is the height of each
	 * subimage
	 * 
	 * @return int
	 */
	public int getVsize() {
		return vsize;
	}

	/**
	 * Returns the size of the skin, this is the height of each subimage
	 * 
	 * @return Dimension
	 */
	public Dimension getSize() {
		return new Dimension(hsize, vsize);
	}

	/**
	 * Calculates the size for each subimage
	 */
	protected void calculateSizes() {
		hsize = getImage().getWidth(null) / nrImages;
		vsize = getImage().getHeight(null);
	}
}