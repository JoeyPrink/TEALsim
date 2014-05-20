/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ImageIO.java,v 1.3 2007/07/16 22:05:19 pbailey Exp $ 
 * 
 */

package teal.visualization.image;

import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

import teal.util.*;

public class ImageIO {
  
  /* EMSimulations.IO.ImageIO: Writes an RGBImage to a file
   *
   * static class
   *
   * Available file formats are:
   * TIFF: Tagged Image File Format
   * TGA: Truevision (Targa)
   * MonoTGA: gray-scale Truevision (Targa)
   * Raw: Raw RGB pixel data
   * RawX: Raw RGB pixel data, 4-pixel horizontal interleaving
   */

  public static void WriteTIFF(RGBImage image, String filename)
  /* Writes a TIFF file representing 'image' to 'filename' */
  {
    System.out.print("Writing TIFF file: "+filename+"... ");
    try {
      file = new FileOutputStream(filename);

      /* Header */
      if (reversebytes)
        WriteWord(0x4D4D);
      else
        WriteWord(0x4949);
      WriteWord(0x002A);
      WriteDword(0x08);

      /* Image properties */
      WriteWord(0x0D);
      WriteTIFFEntry(0x0FE,4,1,0);
      WriteTIFFEntry(0x100,4,1,image.width);
      WriteTIFFEntry(0x101,4,1,image.height);
      WriteTIFFEntry(0x102,3,3,170);
      WriteTIFFEntry(0x103,3,1,1);
      WriteTIFFEntry(0x106,3,1,2);
      WriteTIFFEntry(0x111,4,1,192);
      WriteTIFFEntry(0x115,3,1,3);
      WriteTIFFEntry(0x116,4,1,image.height);
      WriteTIFFEntry(0x117,4,1,3*image.width*image.height);
      WriteTIFFEntry(0x11A,5,1,176);
      WriteTIFFEntry(0x11B,5,1,184);
      WriteTIFFEntry(0x128,3,1,2);
      WriteDword(0);
      WriteWord(8);
      WriteWord(8);
      WriteWord(8);
      WriteDword(0x000AFC80);
      WriteDword(0x00002710);
      WriteDword(0x000AFC80);
      WriteDword(0x00002710);

      /* Pixel data */
      byte[] buf = new byte[image.width*3];
      for (int j = 0, k = 0; j<image.height; ++j) {
        for (int i = 0, l = 0; i<image.width; ++i, ++k, l += 3) {
          buf[l] = image.r[k];
          buf[l+1] = image.g[k];
          buf[l+2] = image.b[k];
        }
        file.write(buf,0,image.width*3);
      }

      file.close();
      System.out.println("done");
    } catch (Exception e) {
      System.out.println("Error writing TIFF file!");
    }
  }
  
  public static void WriteTIFF(RGBAImage image, String filename)
  /* Writes a TIFF file representing 'image' to 'filename' */
  {
    System.out.print("Writing TIFF file: "+filename+"... ");
    try {
      file = new FileOutputStream(filename);

      /* Header */
      if (reversebytes)
        WriteWord(0x4D4D);
      else
        WriteWord(0x4949);
      WriteWord(0x002A);
      WriteDword(0x08);

      /* Image properties */
      WriteWord(0x0D);
      WriteTIFFEntry(0x0FE,4,1,0);
      WriteTIFFEntry(0x100,4,1,image.width);
      WriteTIFFEntry(0x101,4,1,image.height);
      WriteTIFFEntry(0x102,3,4,182);
      WriteTIFFEntry(0x103,3,1,1);
      WriteTIFFEntry(0x106,3,1,2);
      WriteTIFFEntry(0x111,4,1,206);
      WriteTIFFEntry(0x115,3,1,4);
      WriteTIFFEntry(0x116,4,1,image.height);
      WriteTIFFEntry(0x117,4,1,4*image.width*image.height);
      WriteTIFFEntry(0x11A,5,1,190);
      WriteTIFFEntry(0x11B,5,1,198);
      WriteTIFFEntry(0x128,3,1,2);
      WriteTIFFEntry(0x152,3,1,2);
      WriteDword(0);
      WriteWord(8);
      WriteWord(8);
      WriteWord(8);
      WriteWord(8);
      WriteDword(0x000AFC80);
      WriteDword(0x00002710);
      WriteDword(0x000AFC80);
      WriteDword(0x00002710);

      /* Pixel data */
      byte[] buf = new byte[image.width*4];
      for (int j = 0, k = 0; j<image.height; ++j) {
        for (int i = 0, l = 0; i<image.width; ++i, ++k, l += 4) {
          buf[l] = image.r[k];
          buf[l+1] = image.g[k];
          buf[l+2] = image.b[k];
          buf[l+3] = image.a[k];
        }
        file.write(buf,0,image.width*4);
      }

      file.close();
      System.out.println("done");
    } catch (Exception e) {
      System.out.println("Error writing TIFF file!");
    }
  }
  
  public static RGBImage ReadTIFF(String filename)
  /* Reads 'filename' as a TIFF file and creates an RGBImage object that
   * represents its contents. Note that this is NOT a full TIFF reader
   * implementation, and only works with uncompressed 24-bit images. */
  {
    System.out.print("Reading TIFF file: "+filename+"... ");
    try {
      ifile = new FileInputStream(filename);
      
      // Parse header
      int v = ReadDword();
      if (v!=0x002A4949)
        throw new Exception();
      v = ReadDword();
      ifile.skip(v-8+2);
      
      int width = 0;
      int height = 0;
      int imageofs = 0;
      v = ReadWord();
      while (v!=0) {
        if (v==0x100) {
          ifile.skip(6);
          width = ReadWord();
          ifile.skip(2);
        } else if (v==0x101) {
          ifile.skip(6);
          height = ReadWord();
          ifile.skip(2);
        } else if (v==0x111) {
          ifile.skip(2);
          v = ReadDword();
          if (v>1)
            imageofs = -ReadDword();
          else
            imageofs = ReadDword();
        } else
          ifile.skip(10);
        v = ReadWord();
      }
      
      if ((width==0) || (height==0) || (imageofs==0))
        throw new Exception();
      
      ifile.close();
      if (imageofs<0) {
        ifile = new FileInputStream(filename);
        ifile.skip(-imageofs);
        imageofs = ReadDword();
        ifile.close();
      }
      
      ifile = new FileInputStream(filename);
      ifile.skip(imageofs);
      
      // Read pixel data
      RGBImage image = new RGBImage(width, height);
      byte[] buf = new byte[width*3];
      for (int j = 0, k = 0; j<height; ++j) {
        ifile.read(buf,0,width*3);
        for (int i = 0, l = 0; i<width; ++i, ++k, l += 3) {
          image.r[k] = buf[l];
          image.g[k] = buf[l+1];
          image.b[k] = buf[l+2];
        }          
      }

      ifile.close();
      System.out.println("done");
      return image;
    } catch (Exception e) {
      System.out.println("Error reading TIFF file!");
      return null;
    }
  }    

  public static void WriteRawRGB(RGBImage image, String filename)
  /* Writes a Raw file representing 'image' to 'filename'
   * The pixels are written in RGB, left-right, top-down order */
  {
    System.out.print("Writing Raw file: "+filename+"... ");
    try {
      file = new FileOutputStream(filename);
      byte[] b = new byte[image.width*image.height*3];
      image.toRGBBytes(b);
      file.write(b,0,image.width*image.height*3);

      file.close();
      System.out.println("done");
    } catch (Exception e) {
      System.out.println("Error writing file!");
    }
  }

  public static void WriteRawX(RGBImage image, String filename)
  /* Writes a RawX file representing 'image' to 'filename'
   * The pixels are written in RGB, left-right, top-down, 4-interleaved order */
  {
    System.out.print("Writing RawX file: "+filename+"... ");
    try {
      file = new FileOutputStream(filename);

      byte[] b = new byte[image.width*image.height*3];
      image.toRGBBytes(b);
      int size = (image.width/4)*image.height;
      byte[] buf = new byte[size*3];

      for (int p = 0; p<4; ++p) {
        for (int k = 0, l = p; k<size; ++k, l += 4) {
          buf[k*3] = b[l*3];
          buf[k*3+1] = b[l*3+1];
          buf[k*3+2] = b[l*3+2];
        }

        file.write(buf,0,size*3);
      }

      file.close();
      System.out.println("done");
    } catch (Exception e) {
      System.out.println("Error writing file!");
    }
  }

  public static void WriteMonoTGA(RGBImage image, String filename)
  /* Writes a MonoTGA file representing 'image' to 'filename'
   * The RGB components are mixed together in (30%, 58%, 12%) proportions */
  {
    System.out.print("Writing TGA file: "+filename+"... ");
    try {
      file = new FileOutputStream(filename);
      /* Header */
      WriteByte(0);
      WriteByte(0);
      WriteByte(3);
      WriteByte(0);
      WriteByte(0);
      WriteByte(0);
      WriteByte(0);
      WriteByte(0);
      WriteWord(0);
      WriteWord(0);
      WriteWord(image.width);
      WriteWord(image.height);
      WriteByte(8);
      WriteByte(0x20);

      /* Pixel data */
      int R = 30;
      int G = 58;
      int B = 12;
      byte[] buf = new byte[image.width];
      for (int j = 0, k = 0; j<image.height; ++j) {
        for (int i = 0; i<image.width; ++i, ++k)
          buf[i] = (byte)((R*(int)image.r[k] + G*(int)image.g[k] + B*(int)image.b[k])/100);
        file.write(buf,0,image.width);
      }

      file.close();
      System.out.println("done");
    } catch (Exception e) {
      System.out.println("Error writing TGA file!");
    }
  }

  public static void WriteTGA(RGBImage image, String filename)
  /* Writes a TGA file represneting 'image' to 'filename' */
  {
    System.out.print("Writing TGA file: "+filename+"... ");
    try {
      file = new FileOutputStream(filename);
      /* Header */
      WriteByte(0);
      WriteByte(0);
      WriteByte(2);
      WriteByte(0);
      WriteByte(0);
      WriteByte(0);
      WriteByte(0);
      WriteByte(0);
      WriteWord(0);
      WriteWord(0);
      WriteWord(image.width);
      WriteWord(image.height);
      WriteByte(24);
      WriteByte(0x20);

      /* Pixel data */
      byte[] buf = new byte[image.width*3];
      for (int j = 0, k = 0; j<image.height; ++j) {
        for (int i = 0, l = 0; i<image.width; ++i, ++k, l += 3) {
          buf[l] = image.b[k];
          buf[l+1] = image.g[k];
          buf[l+2] = image.r[k];
        }
        file.write(buf,0,image.width*3);
      }

      file.close();
      System.out.println("done");
    } catch (Exception e) {
      System.out.println("Error writing TGA file!");
    }
  }

  public static void WriteTGA(RGBAImage image, String filename)
  /* Writes a TGA file represneting 'image' to 'filename' */
  {
    System.out.print("Writing TGA file: "+filename+"... ");
    try {
      file = new FileOutputStream(filename);
      /* Header */
      WriteByte(0);
      WriteByte(0);
      WriteByte(2);
      WriteByte(0);
      WriteByte(0);
      WriteByte(0);
      WriteByte(0);
      WriteByte(0);
      WriteWord(0);
      WriteWord(0);
      WriteWord(image.width);
      WriteWord(image.height);
      WriteByte(32);
      WriteByte(0x28);

      /* Pixel data */
      byte[] buf = new byte[image.width*4];
      for (int j = 0, k = 0; j<image.height; ++j) {
        for (int i = 0, l = 0; i<image.width; ++i, ++k, l += 4) {
          buf[l] = image.b[k];
          buf[l+1] = image.g[k];
          buf[l+2] = image.r[k];
          buf[l+3] = image.a[k];
          /*
          int alpha = i(image.a[k]);
          buf[l] = (byte)((i(image.b[k])*alpha)>>8);
          buf[l+1] = (byte)((i(image.g[k])*alpha)>>8);
          buf[l+2] = (byte)((i(image.r[k])*alpha)>>8);
          buf[l+3] = image.a[k];
          */
        }
        file.write(buf,0,image.width*4);
      }
      
      WriteWord(495);
      for (int i = 2; i<494; ++i)
          WriteByte(0);
      WriteByte(3);
      
      WriteDword(18 + 4*image.width*image.height);
      WriteDword(0);
      byte[] signature = {
        0x54,0x52,0x55,0x45,0x56,0x49,0x53,0x49,0x4F,0x4E,0x2D,0x58,0x46,0x49,0x4C,0x45,0x2E,0x00
      };
      for (int i = 0; i<signature.length; ++i)
          WriteByte(signature[i]);
      

      file.close();
      System.out.println("done");
    } catch (Exception e) {
      System.out.println("Error writing TGA file!");
    }
  }

  static private FileOutputStream file = null;
  static private FileInputStream ifile = null;
  static private byte[] bytes = new byte[4];
  static private boolean reversebytes = false;
  
  private static int i(byte b)
  {
    if (b>=0)
      return (int)b;
    else
      return 256+(int)b;
  }
  
  private static void WriteByte(int x) throws IOException
  {
    bytes[0] = (byte)x;
    file.write(bytes,0,1);
  }
  
  private static byte ReadByte() throws IOException
  {
    ifile.read(bytes,0,1);
    return bytes[0];
  }

  private static void WriteWord(int x) throws IOException
  {
    if (reversebytes) {
      bytes[0] = (byte)((x & 0xFF00)>>8);
      bytes[1] = (byte)(x & 0x00FF);
    } else {
      bytes[0] = (byte)(x & 0x00FF);
      bytes[1] = (byte)((x & 0xFF00)>>8);
    }
    file.write(bytes,0,2);
  }
  
  private static int ReadWord() throws IOException
  {
    ifile.read(bytes,0,2);
    if (reversebytes)
      return (i(bytes[0])<<8)+(i(bytes[1]));
    else
      return (i(bytes[1])<<8)+(i(bytes[0]));
  }

  private static void WriteDword(int x) throws IOException
  {
    if (reversebytes) {
      bytes[0] = (byte)((x & 0xFF000000)>>24);
      bytes[1] = (byte)((x & 0x00FF0000)>>16);
      bytes[2] = (byte)((x & 0x0000FF00)>>8);
      bytes[3] = (byte)(x & 0x000000FF);
    } else {
      bytes[0] = (byte)(x & 0x000000FF);
      bytes[1] = (byte)((x & 0x0000FF00)>>8);
      bytes[2] = (byte)((x & 0x00FF0000)>>16);
      bytes[3] = (byte)((x & 0xFF000000)>>24);
    }
    file.write(bytes,0,4);
  }

  private static int ReadDword() throws IOException
  {
    ifile.read(bytes,0,4);
    if (reversebytes)
      return (i(bytes[0])<<24)+(i(bytes[1])<<16)+(i(bytes[2])<<8)+(i(bytes[3]));
    else
      return (i(bytes[3])<<24)+(i(bytes[2])<<16)+(i(bytes[1])<<8)+(i(bytes[0]));
  }

  private static void WriteRGB(byte r, byte g, byte b) throws IOException
  {
    bytes[0] = r;
    bytes[1] = g;
    bytes[2] = b;
    file.write(bytes,0,3);
  }
  
  private static void WriteBGR(byte r, byte g, byte b) throws IOException
  {
    bytes[0] = b;
    bytes[1] = g;
    bytes[2] = r;
    file.write(bytes,0,3);
  }

  private static void WriteTIFFEntry(int tag, int type, int count, int data) throws IOException
  {
    WriteWord(tag);
    WriteWord(type);
    WriteDword(count);
    WriteDword(data);
  }
  
  /*
  * JPEG-encode the image and write to file, close the file.
  * JPEG support removed so JAI is not required
  */
  
  public static void writeJPEG(BufferedImage inImage,int dotsInch, String filePath)
     	throws IOException{
      TDebug.println(0,"Writing: " + filePath);
      File file = new File(filePath);
      writeJPEG(inImage, dotsInch, file);
  }
  

    /*
	* JPEG-encode the image and write to file, close the file.
    * JPEG support removed so JAI is not required
	*/
    
    public static void writeJPEG(BufferedImage inImage,int dotsInch, File file)
       	throws IOException
    {
        javax.imageio.ImageIO.write(inImage,"jpg",file);
    }
    
    
    
    /**
    * return a scaled image of given width and height
    */
    public static BufferedImage getScaledImage(BufferedImage origImage, int width, 
					int height){

        int origW = origImage.getWidth();
        int origH = origImage.getHeight();
        float xScale = (float)width/(float)origW;
        float yScale = (float)height/(float)origH;

	return (getScaledImage(origImage, xScale, yScale));
    }

    // return a scaled image of given x and y scale
    public static BufferedImage getScaledImage(BufferedImage origImage, float xScale,
                                        float yScale){
 
        // If the image is already the requested size, no need to scale
        if (xScale == 1.0f && yScale == 1.0f)
            return origImage;
        else {
        
        int scaleW = (int)(origImage.getWidth() * xScale + 0.5);
        int scaleH = (int)(origImage.getHeight() * yScale + 0.5);
        AffineTransform at = AffineTransform.getScaleInstance(xScale,
                                                               yScale);
        
            /*
        int[] nBits = {8, 8, 8, 8};
        int[] bandOffset = { 0, 1, 2, 3};
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB); 
        ComponentColorModel colorModel = new ComponentColorModel(cs, nBits, true, false, java.awt.Transparency.TRANSLUCENT, 0);

	    WritableRaster wr =  java.awt.image.Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, scaleW, scaleH , scaleW * 4, 4, bandOffset, null);;
	    BufferedImage scaledImage = new BufferedImage(colorModel, wr, false, null);
	    
	    java.awt.Graphics2D g2 = scaledImage.createGraphics();
 	    g2.transform(at);
	    g2.drawImage(origImage, 0, 0, null);

            return scaledImage;
            */
         AffineTransformOp affineOp = new AffineTransformOp(at,AffineTransformOp.TYPE_BILINEAR);   
         return affineOp.filter(origImage,null);   
        }
    }

}

