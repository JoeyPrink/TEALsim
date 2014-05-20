/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: RGBAImage.java,v 1.3 2007/07/16 22:05:19 pbailey Exp $ 
 * 
 */

package teal.visualization.image;


public class RGBAImage {
  
  /* EMSimulations.Image.RGBAImage: Stores a RGBA image
   *
   * = < width, height, red-buffer, green-buffer, blue-buffer, alpha-buffer >
   *
   * An RGBAImage stores a rectangular, color image in RGBA format.
   * The image's width and height are specified upon construction, and
   * four byte arrays are created to store all the red, green, blue, & alpha
   * components separately. The pixel data is stored in left-right,
   * top-down scanline order. Each component value is from 0 to 255, stored
   * in a byte.
   */
  
  /* width, height store the dimensions of the image.
   * size = width*height, the number of pixels */
  public final int width, height, size;
  /* r, g, b, & a store the red, green, blue, and alpha pixel components */
  public byte[] r, g, b, a;
  /* offset[] indexes the left-most pixel of each scanline in r,g,b,a[]
   * The index of pixel (x,y) is therefore offset[y]+x */
  public final int[] offset;
  
  public RGBAImage(int width, int height)
  /* Constructs a new RGBAImage = <width,height,0-buffer,0-buffer,0-buffer,0-buffer> */
  {
    this.width = width;
    this.height = height;
    size = width*height;
    
    r = new byte[size];
    g = new byte[size];
    b = new byte[size];
    a = new byte[size];
    offset = new int[height];
    for (int j = 0; j<height; ++j)
      offset[j] = width*j;
    
    Clear();
  }
  
  public void Clear()
  /* Sets all the pixels values to zero */
  {
    for (int k = 0; k<size; ++k)
    {
      r[k] = 0;   
      g[k] = 0;    
      b[k] = 0;   
      a[k] = 0;
    }
  }
  
  public void Copy(RGBAImage image, int xorigin, int yorigin)
  /* Copies the RGBA values from the sub-window of 'image' starting at
   *   (xorigin, yorigin) to 'this'.
   * Requires: the sub-window fits inside 'image' */
  {
    if (((xorigin+width)>image.width) || ((yorigin+height)>image.height))
      throw new RuntimeException("RGBAImage.Copy: Window too large");
    
    for (int j = 0, k = 0; j<height; ++j) {
      for (int i = 0, l = image.offset[j+yorigin] + xorigin; i<width; ++i, ++k, ++l) {
        r[k] = image.r[l];
        g[k] = image.g[l];
        b[k] = image.b[l];
        a[k] = image.a[l];
      }
    }
  }
  
  public void Copy(RGBAImage image)
  /* Copies the RGBA values from the top-left corner of 'image' to 'this'
   * Requires: 'image' is at least as large as 'this' */
  {
    Copy(image, 0, 0);
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
  
  public byte getA(int x, int y)
  /* Returns: the alpha component of the pixel at (x, y)
   * Requires: 0<=x<width and 0<=y<height */
  {
    return a[offset[y] + x];
  }
  
  public void Set(int x, int y, byte R, byte G, byte B, byte A)
  /* Sets the pixel at (x, y) to the color (R, G, B, A).
   * Requires: 0<=x<width and 0<=y<height */
  {
    int k = offset[y] + x;
    r[k] = R;
    g[k] = G;
    b[k] = B;
    a[k] = A;
  }
  
  public void toRGBBytes(byte[] buf)
  /* Copies the pixel data in 'this' to the byte-buffer 'buf' in RGB
   * packed order.
   * Requires: buf.length = 3*width*height */
  {
    if (buf.length!=(size*3))
      throw new RuntimeException("RGBAImage.toRGBBytes: Buffer size mismatch");
    
    for (int k = 0, l = 0; k<size; ++k, l += 3) {
      buf[l] = r[k];
      buf[l+1] = g[k];
      buf[l+2] = b[k];
    }
  }
  
  public void toRGBABytes(byte[] buf)
  /* Copies the pixel data in 'this' to the byte-buffer 'buf' in RGBA
   * packed order.
   * Requires: buf.length = 4*width*height */
  {
    if (buf.length!=(size*4))
      throw new RuntimeException("RGBAImage.toRGBABytes: Buffer size mismatch");
    
    for (int k = 0, l = 0; k<size; ++k, l += 4) {
      buf[l] = r[k];
      buf[l+1] = g[k];
      buf[l+2] = b[k];
      buf[l+3] = a[k];
    }
  }
  
  public void toBGRABytes(byte[] buf)
  /* Copies the pixel data in 'this' to the byte-buffer 'buf' in BGRA
   * packed order.
   *Requires: buf.length = 4*width*height */
  {
    if (buf.length!=(size*4))
      throw new RuntimeException("RGBAImage.toBGRABytes: Buffer size mismatch");
    
    for (int k = 0, l = 0; k<size; ++k, l += 4) {
      buf[l] = b[k];
      buf[l+1] = g[k];
      buf[l+2] = r[k];
      buf[l+3] = a[k];
    }
  }
  
  public void fromScalarImage(ScalarImage image, int xorigin, int yorigin, 
    double R, double G, double B, double A)
  /* Sets 'this' to a colorized representation of 'image' within the
   * sub-window starting at (xorigin, yorigin). The (R,G,BA) triad is the
   * color of a scalar value of 1.0, and the resulting RGBA values are all
   * clamped to the 0-255 range.
   * Requires: the sub-window fits within 'image' */
  {
    if (((xorigin+width)>image.width) || ((yorigin+height)>image.height))
      throw new RuntimeException("RGBAImage.fromScalarImage: Window too large");
    
    for (int j = 0, k = 0; j<height; ++j) {
      for (int i = 0, l = image.offset[j+yorigin] + xorigin; i<width; ++i, ++k, ++l) {
        r[k] = (byte)clamp(image.f[l]*R, 0, 255);
        g[k] = (byte)clamp(image.f[l]*G, 0, 255);
        b[k] = (byte)clamp(image.f[l]*B, 0, 255);
        a[k] = (byte)clamp(image.f[l]*A, 0, 255);
      }
    }
  }
  
  public void fromScalarImage(ScalarImage image, double R, double G, double B, double A)
  /* Sets 'this' to a colorized representation of 'image' within the window
   * starting at the top-left corner. The (R,G,B,A) triad is the color of a
   * scalar value of 1.0, and the resulting RGBA values are all clamped
   * to the 0-255 range.
   * Requires: 'image' is at least as large as 'this' */
  {
    fromScalarImage(image, 0, 0, R, G, B, A);
  }


/** disabled until fixed.
  
  public void fromScalarImageMagnitude(ScalarImage image, Field f, 
    AffineTransform ftoi, Field func)
  {
  	AffineTransform itof = null;
	try
	{
    	itof = ftoi.createInverse();
	}
	catch(NoninvertibleTransformException niv)
	{
		TDebug.println(0,"NoninvertibleTransformException: " + niv); 
	}
    Vector3d value = new Vector3d(), color = new Vector3d();
    Point2D.Double p = new Point2D.Double();
    for (int j = 0, k = 0; j<height; ++j)
      for (int i = 0; i<width; ++i, ++k) {
        p.x = i + 0.5;
        p.y = j + 0.5;
		itof.transform(p,p);
        value = f.get(p.x,p.y,0);
        //value.x = v.x;
        //value.y = v.y;
        value.z = image.f[k];
        func.get(value, color);
        r[k] = (byte)clamp(color.x*255, 0, 255);
        g[k] = (byte)clamp(color.y*255, 0, 255);
        b[k] = (byte)clamp(color.z*255, 0, 255);
        a[k] = (byte)clamp(1.0*255, 0, 255);
      }
  } 
 */
  
  public void RfromScalarImage(ScalarImage image, double R)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBAImage.RfromScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k)
      r[k] = (byte)clamp(image.f[k]*R, 0, 255);
  }
  
  public void RtoScalarImage(ScalarImage image)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBAImage.RtoScalarImage: Window size mismatch!");
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
      throw new RuntimeException("RGBAImage.GfromScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k)
      g[k] = (byte)clamp(image.f[k]*G, 0, 255);
  }
  
  public void GtoScalarImage(ScalarImage image)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBAImage.GtoScalarImage: Window size mismatch!");
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
      throw new RuntimeException("RGBAImage.BfromScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k)
      b[k] = (byte)clamp(image.f[k]*B, 0, 255);
  }
  
  public void BtoScalarImage(ScalarImage image)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBAImage.BtoScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k) {
      int i = (int)b[k];
      if (i<0)
        i += 256;
      image.f[k] = i;
    }
  }
  
  public void AfromScalarImage(ScalarImage image, double A)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBAImage.AfromScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k)
      a[k] = (byte)clamp(image.f[k]*A, 0, 255);
  }
  
  public void AtoScalarImage(ScalarImage image)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBAImage.AtoScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k) {
      int i = (int)a[k];
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

}
