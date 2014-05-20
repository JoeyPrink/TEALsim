/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TextureLoader.java,v 1.9 2007/07/16 22:04:55 pbailey Exp $ 
 * 
 */

package teal.render.j3d;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;

import teal.util.URLGenerator;

/**
 * This class is used for loading and creating a Texture2D from an Image, 
 * BufferedImage or input specification.
 * It is dependant on the javax.imageio packages included as part of Java 1.4.
 *
 * Normal usage expects to have the constructed loader used for one Texture.
 *
 * Methods are provided to retrieve the Texture object and the associated
 * ImageComponent object or a scaled version of the ImageComponent object.
 *
 * Default format is RGBA. Other legal formats are: RGBA, RGBA4, RGB5_A1, 
 * RGB, RGB4, RGB5, R3_G3_B2, LUM8_ALPHA8, LUM4_ALPHA4, LUMINANCE and ALPHA
 *
 * This based on the utility class provided by Sun Microsystems
 */

public class TextureLoader extends Object {

    /**
     * Optional flag - specifies that mipmaps are generated for all levels 
     **/
    public static final int GENERATE_MIPMAP =  0x01;

    /**
     * Optional flag - specifies that the ImageComponent2D will
     * access the image data by reference
     *
     * @since Java 3D 1.2
     **/
    public static final int BY_REFERENCE = 0x02;
    
    /**
     * Optional flag - specifies that the ImageComponent2D will
     * have a y-orientation of y up, meaning the orgin of the image is the
     * lower left
     *
     * @since Java 3D 1.4
     **/
    public static final int Y_UP = 0x04;
    
    /**
    * Rotation of source image this is independant of the Y_UP setting,
    * and is applied to the specified image, getImage returns the rotated BufferedImage.
    */
    public static final int ROT_CW_90 = 0x08;
    public static final int ROT_CW_180 = 0x10;
    public static final int ROT_CW_270 = 0x20;

    private int textureFormat = Texture.RGBA;
    private int imageComponentFormat = ImageComponent.FORMAT_RGBA;

    private Texture2D tex = null;
    private BufferedImage bufferedImage = null;
    private ImageComponent2D imageComponent = null;

    private int flags;

    private boolean byRef;
    private boolean yUp;


    public TextureLoader(int flags)
    {
        this.flags = flags;
        if ((flags & BY_REFERENCE) != 0) {
	        byRef = true;
	    }
	    if ((flags & Y_UP) != 0) {
	        yUp = true;
	    }
    }
    
    public TextureLoader(  int flags, String format)
    {
        this(flags);
        parseFormat(format);
    }
    /**
     * Contructs a TextureLoader object using the specified BufferedImage 
     * and default format RGBA
     * @param bImage The BufferedImage used for loading the texture 
     */
    public TextureLoader(BufferedImage bImage) {
        this( 0);
        setImage(bImage);
    }

     /**
     * Contructs a TextureLoader object using the specified BufferedImage,
     * option flags and default format RGBA
     * @param bImage The BufferedImage used for loading the texture
     * @param flags The flags specify what options to use in texture loading (generate mipmap etc)
     */
    public TextureLoader(BufferedImage bImage, int flags) {
        this( flags);
        setImage(bImage);
    }

    /**
     * Contructs a TextureLoader object using the specified BufferedImage,
     * format and option flags 
     * @param bImage The BufferedImage used for loading the texture 
     * @param format The format specifies which channels to use
     * @param flags The flags specify what options to use in texture loading (generate mipmap etc)
     */
    public TextureLoader(BufferedImage bImage, int flags, String format) {
	    this(flags);
        parseFormat(format);
	    setImage(bImage);
    }

     /**
     * Contructs a TextureLoader object using the specified file, 
     * option flags and default format RGBA
     * @param fname The file that specifies an Image to load the texture with
     * @param flags The flags specify what options to use in texture loading (generate mipmap etc)
     * @param observer The associated image observer
     */
    public TextureLoader(String fname, int flags)
    throws IOException {
        this(flags);
        setImage(fname);
    }

    /**
     * Contructs a TextureLoader object using the specified file, 
     * format and option flags 
     * @param fname The file that specifies an Image to load the texture with
     * @param format The format specifies which channels to use
     * @param flags The flags specify what options to use in texture loading (generate mipmap etc)
     * @param observer The associated image observer
     */
    public TextureLoader(String fname, int flags, String format) 
    throws IOException{
        this(flags,format);
        setImage(fname);
    }


    /**
     * Contructs a TextureLoader object using the specified URL 
     * and default format RGBA
     * @param url The URL that specifies an Image to load the texture with
     * @param observer The associated image observer
     */
    public TextureLoader(URL url)
    throws IOException {
        this( 0);
        setImage(url);
    }
    /**
     * Contructs a TextureLoader object using the specified URL, 
     * option flags and default format RGBA
     * @param url The URL that specifies an Image to load the texture with
     * @param flags The flags specify what options to use in texture loading (generate mipmap etc)
     * @param observer The associated image observer
     */
    public TextureLoader(URL url, int flags)
    throws IOException {
        this(flags);
        setImage(url);
    }
    /**
     * Contructs a TextureLoader object using the specified URL, 
     * format and option flags 
     * @param url The url that specifies an Image to load the texture with
     * @param format The format specifies which channels to use
     * @param flags The flags specify what options to use in texture loading (generate mipmap etc)
     * @param observer The associated image observer
     */
    public TextureLoader(URL url, int flags, String format)
    throws IOException {
        this(flags,format);
        setImage(url);
    }

/*
	    if (observer == null) {
	      observer = new java.awt.Container();
	    }

            final Toolkit toolkit = Toolkit.getDefaultToolkit();
	    final Image image[] = new Image[1];
	    final URL Url = url;

	    java.security.AccessController.doPrivileged(
						    new java.security.PrivilegedAction() {
	      public Object run() {
	        image[0] = toolkit.getImage(Url);
	        return null;
	      }
	    }
						    );
	    parseFormat(format);
	    this.flags = flags;
	    bufferedImage = createBufferedImage(image[0], observer);

        if (bufferedImage==null && JAIInstalled() ) {
	    bufferedImage = JAIgetImage( url, observer );
	}

	if (bufferedImage==null)
		System.err.println("Error loading Image "+url.toString() );

	if ((flags & BY_REFERENCE) != 0) {
	    byRef = true;
	}
	if ((flags & Y_UP) != 0) {
	    yUp = true;
	}
    */


    /**
      * 
      */
    private BufferedImage createBufferedImage( String filename)
    throws IOException {
	URL url = URLGenerator.getResource(filename);
	return createBufferedImage(url);
    }
    
    private BufferedImage createBufferedImage(URL url)
    throws IOException {
	BufferedImage bImage = null;
    if (url != null)  
        bImage = ImageIO.read(url);
	return bImage;
    }
    
    public void setImage(String path)
    throws IOException
    {
        BufferedImage img = createBufferedImage(path);
        setImage(img);
    }
    
    public void setImage(URL url)
    throws IOException
    {
        BufferedImage img = createBufferedImage(url);
        setImage(img);
    }
    
    public void setImage(BufferedImage img)
    {
        int rot = flags & (ROT_CW_90 | ROT_CW_180 | ROT_CW_270);
        if (rot == 0)
        {
          bufferedImage = img;  
        }
        else
        {
            
            double theta = 0;
            switch (rot)
            {
              
              case ROT_CW_90:
                theta = Math.PI * 0.5;
                break;
              case ROT_CW_180:
                theta = Math.PI * 1.;
                break;
              case ROT_CW_270:
                theta = Math.PI * 1.5;
                break;
            }
            AffineTransform trans = new AffineTransform();
            trans.rotate(theta, img.getWidth()/0.5, img.getHeight()/0.5);
            AffineTransformOp affineOp = new AffineTransformOp(trans,AffineTransformOp.TYPE_BILINEAR);
            bufferedImage = affineOp.filter(img,null);
        }
       
        tex = null;
        imageComponent = null;
    }
    public BufferedImage getImage()
    {
        return bufferedImage;
    }

    /**
     * Returns the associated ImageComponent2D object
     *   
     * @return The associated ImageComponent2D object
     */  
    public ImageComponent2D getImageComponent2D() {

	if (imageComponent == null) 
            imageComponent = new ImageComponent2D(imageComponentFormat, 
						  bufferedImage, byRef, yUp);
        return imageComponent;
    }

    /**
     * Returns the scaled ImageComponent2D object
     *   
     * @param xScale The X scaling factor
     * @param yScale The Y scaling factor
     *
     * @return The scaled ImageComponent2D object
     */  
    public ImageComponent2D getScaledImage2D(float xScale, float yScale) {

	if (xScale == 1.0f && yScale == 1.0f)
	    return getImageComponent2D();
	else
	    return(new ImageComponent2D(imageComponentFormat, 
					getScaledImage(bufferedImage,
						       xScale, yScale),
					byRef, yUp));
    }

    /**
     * Returns the scaled ImageComponent2D object
     *   
     * @param width The desired width
     * @param height The desired height
     *
     * @return The scaled ImageComponent2D object
     */
    public ImageComponent2D getScaledImage2D(int width, int height) {

	if (bufferedImage.getWidth() == width && 
	    	bufferedImage.getHeight() == height) 
	    return getImageComponent2D();
        else 
	    return(new ImageComponent2D(imageComponentFormat, 
					getScaledImage(bufferedImage,
						       width, height),
					byRef, yUp));
    }

    /**
     * Returns the associated Texture object
     * or null if the image failed to load
     *   
     * @return The associated Texture object
     */
    public Texture getTexture() {

	ImageComponent2D[] scaledImageComponents = null;
	BufferedImage[] scaledBufferedImages = null;
    if (tex == null) {
	  if (bufferedImage==null) return null;

      int width = getClosestPowerOf2(bufferedImage.getWidth());
      int height = getClosestPowerOf2(bufferedImage.getHeight());

	  if ((flags & GENERATE_MIPMAP) != 0) {
      
	    BufferedImage origImage = bufferedImage;
	    int newW = width;
	    int newH = height;
	    int level = Math.max(computeLog(width), computeLog(height)) + 1;
	    scaledImageComponents = new ImageComponent2D[level];
	    scaledBufferedImages = new BufferedImage[level];
            tex = new Texture2D(Texture2D.MULTI_LEVEL_MIPMAP, textureFormat,
                width, height);

            for (int i = 0; i < level; i++) {
                scaledBufferedImages[i] = getScaledImage(origImage, newW, newH);
                scaledImageComponents[i] =  new ImageComponent2D(
			imageComponentFormat, scaledBufferedImages[i],
			byRef, yUp);
	
                tex.setImage(i, scaledImageComponents[i]);
                if (newW > 1) newW >>= 1;
                if (newH > 1) newH >>= 1;
	        origImage = scaledBufferedImages[i];
            }

          } else {
	        scaledImageComponents = new ImageComponent2D[1];
	        scaledBufferedImages = new BufferedImage[1];

            // Create texture from image
            scaledBufferedImages[0] = getScaledImage(bufferedImage, width, height);
            scaledImageComponents[0] = new ImageComponent2D(
			imageComponentFormat, scaledBufferedImages[0],
			byRef, yUp);

            tex = new Texture2D(Texture2D.BASE_LEVEL, textureFormat, width, height);

            tex.setImage(0, scaledImageComponents[0]);
          }
          tex.setMinFilter(Texture2D.BASE_LEVEL_LINEAR);
          tex.setMagFilter(Texture2D.BASE_LEVEL_LINEAR);
        }

	return tex;
    }


   // initialize appropriate format for ImageComponent and Texture
    private void parseFormat(String format) {
        if (format.equals("RGBA")) {
            imageComponentFormat = ImageComponent.FORMAT_RGBA;
            textureFormat = Texture.RGBA;

        } else if (format.equals("RGBA4")) {                                  
            imageComponentFormat = ImageComponent.FORMAT_RGBA4;
            textureFormat = Texture.RGBA;

        } else if (format.equals("RGB5_A1")) {                                 
            imageComponentFormat = ImageComponent.FORMAT_RGB5_A1;
            textureFormat = Texture.RGBA;

        } else if (format.equals("RGB")) { 
            imageComponentFormat = ImageComponent.FORMAT_RGB;
            textureFormat = Texture.RGB;

        } else if (format.equals("RGB4")) {
            imageComponentFormat = ImageComponent.FORMAT_RGB4;
            textureFormat = Texture.RGB;

        } else if (format.equals("RGB5")) {                                  
            imageComponentFormat = ImageComponent.FORMAT_RGB5;
            textureFormat = Texture.RGB;

        } else if (format.equals("R3_G3_B2")) {                              
            imageComponentFormat = ImageComponent.FORMAT_R3_G3_B2;
            textureFormat = Texture.RGB;
 
        } else if (format.equals("LUM8_ALPHA8")) {
            imageComponentFormat = ImageComponent.FORMAT_LUM8_ALPHA8;
            textureFormat = Texture.LUMINANCE_ALPHA;

        } else if (format.equals("LUM4_ALPHA4")) {
            imageComponentFormat = ImageComponent.FORMAT_LUM4_ALPHA4;
            textureFormat = Texture.LUMINANCE_ALPHA;
       
        } else if (format.equals("LUMINANCE")) {
            imageComponentFormat = ImageComponent.FORMAT_CHANNEL8;
            textureFormat = Texture.LUMINANCE;
 
        } else if (format.equals("ALPHA")) {
            imageComponentFormat = ImageComponent.FORMAT_CHANNEL8;
            textureFormat = Texture.ALPHA;
        }
    }

   /**
   * return a scaled BufferedImage of given width and height.
   */
    private BufferedImage getScaledImage(BufferedImage origImage, int width, 
					int height){

        int origW = origImage.getWidth();
        int origH = origImage.getHeight();
        float xScale = (float)width/(float)origW;
        float yScale = (float)height/(float)origH;

	return (getScaledImage(origImage, xScale, yScale));
    }

    /**
    * return a BufferedImage scaled by the given x and y values.
    */
    private BufferedImage getScaledImage(BufferedImage origImage, float xScale,
                                        float yScale){
        BufferedImage wrkImage = null;
        // If the image is already the requested size, no need to scale
        if (xScale == 1.0f && yScale == 1.0f)
        {
            return origImage;
        }
        else {
        
            if (origImage.getColorModel() instanceof IndexColorModel)
            {
                IndexColorModel icm = (IndexColorModel) origImage.getColorModel();
                wrkImage = icm.convertToIntDiscrete(origImage.getRaster(),true);
            }
            else
            {
                wrkImage = origImage;
            }

	    AffineTransform at = AffineTransform.getScaleInstance(xScale,
	                                                           yScale);
        /*
	    WritableRaster wr =  java.awt.image.Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, scaleW, scaleH , scaleW * 4, 4, bandOffset, null);;
	    BufferedImage scaledImage = new BufferedImage(colorModel, wr, false, null);
	    
	    java.awt.Graphics2D g2 = scaledImage.createGraphics();
 	    g2.transform(at);
	    g2.drawImage(origImage, 0, 0, null);

            return scaledImage;
        */
        
        AffineTransformOp afOp = new AffineTransformOp(at,AffineTransformOp.TYPE_BILINEAR);
        return afOp.filter(wrkImage,null);
        }
    }


    private int computeLog(int value) {
        int i = 0;

        if (value == 0) return -1;
        for (;;) {
            if (value == 1) 
                return i;
            value >>= 1;
	    i++;
        }
    }

    private int getClosestPowerOf2(int value) {

	if (value < 1)
	    return value;
	
	int powerValue = 1;
	for (;;) {
	    powerValue *= 2;
	    if (value < powerValue) {
		// Found max bound of power, determine which is closest
		int minBound = powerValue/2;
		if ((powerValue - value) >
		    (value - minBound))
		    return minBound;
		else
		    return powerValue;
	    }
	}
    }
}
