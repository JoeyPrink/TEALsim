/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: RGBImage.java,v 1.4 2008/02/11 16:18:21 pbailey Exp $ 
 * 
 */

package teal.visualization.image;

import java.awt.*;
import java.awt.color.*;
import java.awt.geom.*;
import java.awt.image.*;

import javax.vecmath.*;

import teal.field.*;
import teal.util.*;
import teal.visualization.processing.TColorizer;


public class RGBImage {
  
 /** EMSimulations.Image.RGBImage: Stores a RGB image
   *
   * = < width, height, red-buffer, green-buffer, blue-buffer >
   *
   * An RGBImage stores a rectangular, color image in RGB format.
   * The image's width and height are specified upon construction, and
   * three byte arrays are created to store all the red, green, and blue
   * components separately. The pixel data is stored in left-right,
   * top-down scanline order. Each component value is from 0 to 255, stored
   * in a byte.
   */
  
  /* width, height store the dimensions of the image.
   * size = width*height, the number of pixels */
  public final int width, height, size;
  /* r, g, and b store the red, green, and blue pixel components */
  public byte[] r, g, b;
  /* offset[] indexes the left-most pixel of each scanline in r,g,b[]
   * The index of pixel (x,y) is therefore offset[y]+x */
  public final int[] offset;
  
  public RGBImage(int width, int height)
  /* Constructs a new RGBImage = <width,height,0-buffer,0-buffer,0-buffer> */
  {
    this.width = width;
    this.height = height;
    size = width*height;
    
    r = new byte[size];
    g = new byte[size];
    b = new byte[size];
    offset = new int[height];
    for (int j = 0; j<height; ++j)
      offset[j] = width*j;
    
    clear();
  }
  
  public void clear()
  /* Sets all the pixels values to zero */
  {
    for (int k = 0; k<size; ++k)
    {
      r[k] = 0;
      g[k] = 0;
      b[k] = 0;
    }
  }
  
  public void copy(RGBImage image, int xorigin, int yorigin)
  /* Copies the RGB values from the sub-window of 'image' starting at
   *   (xorigin, yorigin) to 'this'.
   * Requires: the sub-window fits inside 'image' */
  {
    if (((xorigin+width)>image.width) || ((yorigin+height)>image.height))
      throw new RuntimeException("RGBImage.Copy: Window too large");
    
    for (int j = 0, k = 0; j<height; ++j) {
      for (int i = 0, l = image.offset[j+yorigin] + xorigin; i<width; ++i, ++k, ++l) {
        r[k] = image.r[l];
        g[k] = image.g[l];
        b[k] = image.b[l];
      }
    }
  }
  
  public void copy(RGBImage image)
  /* Copies the RGB values from the top-left corner of 'image' to 'this'
   * Requires: 'image' is at least as large as 'this' */
  {
    copy(image, 0, 0);
  }
  
  public byte getR(int x, int y)
  /* Returns: the red component of the pixel at (x, y)
   * Requires: 0<=x<width and 0<=y<height */
  {
    return r[offset[y] + x];
  }
  
  public byte getG(int x, int y)
  /* Returns: the green component of the pixel at (x, y)
   * Requires: 0<=x<width and 0<=y<height */
  {
    return g[offset[y] + x];
  }
  
  public byte getB(int x, int y)
  /* Returns: the blue component of the pixel at (x, y)
   * Requires: 0<=x<width and 0<=y<height */
  {
    return b[offset[y] + x];
  }
  
  public void set(int x, int y, byte R, byte G, byte B)
  /* Sets the pixel at (x, y) to the color (R, G, B).
   * Requires: 0<=x<width and 0<=y<height */
  {
    int k = offset[y] + x;
    r[k] = R;
    g[k] = G;
    b[k] = B;
  }
  /** Copies the pixel data in 'this' to the byte-buffer 'buf' in RGB
   * packed order.
   * Requires: buf.length = 3*width*height 
   */
public void toRGBBytes(byte[] buf) {
	if (buf.length != (size * 3))
		throw new RuntimeException("RGBImage.toRGBBytes: Buffer size mismatch");

	for (int k = 0, l = 0; k < size; ++k, l += 3) {
		buf[l] = r[k];
		buf[l + 1] = g[k];
		buf[l + 2] = b[k];
	}
}
  
  /** Copies the pixel data in 'this' to the byte-buffer 'buf' in BGR
   * packed order.
   *Requires: buf.length = 3*width*height 
   */
  public void toBGRBytes(byte[] buf)
  
  {
    if (buf.length!=(size*3))
      throw new RuntimeException("RGBImage.toBGRBytes: Buffer size mismatch");
    
    for (int k = 0, l = 0; k<size; ++k, l += 3) {
      buf[l] = b[k];
      buf[l+1] = g[k];
      buf[l+2] = r[k];
    }
  }
  
  /** Sets 'this' to a colorized representation of 'image' within the
   * sub-window starting at (xorigin, yorigin). The (R,G,B) triad is the
   * color of a scalar value of 1.0, and the resulting RGB values are all
   * clamped to the 0-255 range.
   * Requires: the sub-window fits within 'image' 
   */
   public void fromScalarImage(ScalarImage image, int xorigin, int yorigin, 
    double R, double G, double B)
  
  {
    if (((xorigin+width)>image.width) || ((yorigin+height)>image.height))
      throw new RuntimeException("RGBImage.fromScalarImage: Window too large");
    double v = 0.;
    for (int j = 0, k = 0; j<height; ++j) {
      for (int i = 0, l = image.offset[j+yorigin] + xorigin; i<width; ++i, ++k, ++l) {
       v= image.get(l);
    	r[k] = (byte)clamp(v*R, 0, 255);
        g[k] = (byte)clamp(v*G, 0, 255);
        b[k] = (byte)clamp(v*B, 0, 255);
      }
    }
  }
  /** Sets 'this' to a colorized representation of 'image' within the window
   * starting at the top-left corner. The (R,G,B) triad is the color of a
   * scalar value of 1.0, and the resulting RGB values are all clamped
   * to the 0-255 range.
   * Requires: 'image' is at least as large as 'this' */
  public void fromScalarImage(ScalarImage image, double R, double G, double B)  
  {
    fromScalarImage(image, 0, 0, R, G, B);
  }
  
  public void fromScalarImageMagnitude(ScalarImage image, Matrix4d projection,
		  Vector3dField f,  TColorizer func)
  {
  	Matrix4d imageToField = new Matrix4d();
	
    imageToField.invert(projection);
	
    Vector3d location = new Vector3d();
    Vector3d value = new Vector3d();
    Vector3d color = new Vector3d();
    Point3d pt = new Point3d();
    for (int j = 0, k = 0; j<height; ++j)
      for (int i = 0; i<width; ++i, ++k) {
        pt.x = i + 0.5;
        pt.y = j + 0.5;
        pt.z = 0;
        imageToField.transform(pt);
        location.set(pt);
        value = f.get(location);
        //value.x = v.x;
        //value.y = v.y;
        value.z = image.get(k);
        func.get(value, color);
        r[k] = (byte)clamp(color.x*255, 0, 255);
        g[k] = (byte)clamp(color.y*255, 0, 255);
        b[k] = (byte)clamp(color.z*255, 0, 255);
      }
  } 
  
  public void RfromScalarImage(ScalarImage image, double R)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBImage.RfromScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k)
      r[k] = (byte)clamp(image.f[k]*R, 0, 255);
  }
  
  public void RtoScalarImage(ScalarImage image)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBImage.RtoScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k) {
      int i = (int)r[k];
      if (i<0)
        i += 256;
      image.f[k] = i;
    }
  }
  
  public void GfromScalarImage(ScalarImage image, double G)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBImage.GfromScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k)
      g[k] = (byte)clamp(image.f[k]*G, 0, 255);
  }
  
  public void GtoScalarImage(ScalarImage image)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBImage.GtoScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k) {
      int i = (int)g[k];
      if (i<0)
        i += 256;
      image.f[k] = i;
    }
  }
  
  public void BfromScalarImage(ScalarImage image, double B)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBImage.BfromScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k)
      b[k] = (byte)clamp(image.f[k]*B, 0, 255);
  }
  
  public void BtoScalarImage(ScalarImage image)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBImage.BtoScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k) {
      int i = (int)b[k];
      if (i<0)
        i += 256;
      image.f[k] = i;
    }
  }
  
  private double clamp(double x, double min, double max)
  {
    if (x<min)
      return min;
    else if (x>max)
      return max;
    else
      return x;
  }
  /** Specialized clamp for double to color space value range 0.0 to 255.0 */
  private double clamp(double x)
  {
    if (x < 0.)
      return 0.;
    else if (x>255.)
      return 255.;
    else
      return x;
  }

  public void fromScalarImageNew(ScalarImage image, int xorigin, int yorigin, 
    double R, double G, double B)
  /* Sets 'this' to a colorized representation of 'image' within the
   * sub-window starting at (xorigin, yorigin). The (R,G,B) triad is the
   * color of a scalar value of 1.0, and the resulting RGB values are all
   * clamped to the 0-255 range.
   * Requires: the sub-window fits within 'image' */
  {
    if (((xorigin+width)>image.width) || ((yorigin+height)>image.height))
      throw new RuntimeException("RGBImage.fromScalarImage: Window too large");
    double x;
    for (int j = 0, k = 0; j<height; ++j) {
      for (int i = 0, l = image.offset[j+yorigin] + xorigin; i<width; ++i, ++k, ++l) {
        x = image.get(l);
        r[k] = (byte)clamp( ( 2 * 255 - 4 * R ) * x * x + ( 4 * R - 255 ) * x, 0, 255);
        g[k] = (byte)clamp( ( 2 * 255 - 4 * G ) * x * x + ( 4 * G - 255 ) * x, 0, 255);
        b[k] = (byte)clamp( ( 2 * 255 - 4 * B ) * x * x + ( 4 * B - 255 ) * x, 0, 255);
      }
    }
  }
  
  public void fromScalarImageNew(ScalarImage image, double R, double G, double B)
  /* Sets 'this' to a colorized representation of 'image' within the window
   * starting at the top-left corner. The (R,G,B) triad is the color of a
   * scalar value of 1.0, and the resulting RGB values are all clamped
   * to the 0-255 range.
   * Requires: 'image' is at least as large as 'this' */
  {
    fromScalarImageNew(image, 0, 0, R, G, B);
  }



  
public BufferedImage getBufferedImage() {
	BufferedImage image = null;
	
	WritableRaster raster;
	byte[] byteBuffer = new byte[width * height * 3];
	toRGBBytes(byteBuffer);
	int ComponentOffset[] = { 0, 1, 2 };
	int ComponentBits[] = { 8, 8, 8 };
	try {
		raster =
			Raster.createWritableRaster(
				new PixelInterleavedSampleModel(
					DataBuffer.TYPE_BYTE,
					width,
					height,
					3,
					width * 3,
					ComponentOffset),
				new DataBufferByte(byteBuffer, width * height * 3),
				new Point(0, 0));
		image =
			new BufferedImage(
				new ComponentColorModel(
					ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB),
					ComponentBits,
					false,
					false,
					ColorModel.OPAQUE,
					DataBuffer.TYPE_BYTE),
				raster,
				false,
				null);

	} catch (Exception e) {
		System.out.println("Exception caught while acquiring image:");
		System.out.println(e.getMessage());
	}
	return image;
}

}
