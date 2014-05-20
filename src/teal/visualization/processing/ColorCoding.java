
package teal.visualization.processing;

import java.util.*;

import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import teal.visualization.dlic.DLIC;

import teal.field.Vec2dField;
//import teal.math.Vec2dIterator;

import teal.visualization.image.AccumImage;
import teal.visualization.image.RGBImage;
import teal.visualization.image.ScalarImage;
import teal.visualization.image.ImageIO;

import teal.math.Vec2Transform;


public class ColorCoding {
  
  static int width = 640;
  static int height = 480;
  static int streamlen = 80;
 
  
  static double Ggradient(double x)
  {
    x = 4-x;
    if (x<0.0)
      x = 0.0;
    if (x>1.0)
      x = 1.0;
    return x;
  }
  
  static double Rgradient(double x)
  {
    if (x<1)
      x = 1-x;
    else if (x<3)
      x = -2+x;
    else
      x = 6-x;
    if (x<0.0)
      x = 0.0;
    if (x>1.0)
      x = 1.0;
    return x;
  }
  
  static double Bgradient(double x)
  {
    if (x<4)
      x = 2-x;
    else if (x<6)
      x = -4+x;
    else
      x = 7-x;
    if (x<0.0)
      x = 0.0;
    if (x>1.0)
      x = 1.0;
    return x;
  }
  
  static double Wgradient(double x)
  {
    x = 1.0-x/8.0;
    if (x<0.0)
      x = 0.0;
    if (x>1.0)
      x = 1.0;
    return x;
  }
  
  static class NoFLIC implements Vec2dIterator {
    public Vector2d next()
    {
      return null;
    }
  }
  
  
  static class Field implements Vec2dField {
    double x1, y1, x2, y2;
    
    public Field() {
      x1 = width*5/6;
      x2 = width*1/6;
      y1 = height*1./2. ; // height*1/4;
      y2 = height*1./2. ; // height*3/4;
    }
    
    public Vector2d get2d(Tuple2d p, Tuple2d f)
    {
      double factor1 = 1.;
      double factor2 = 1.;
      double R1 = ((p.x-x1)*(p.x-x1) + (p.y-y1)*(p.y-y1));
      double R2 = ((p.x-x2)*(p.x-x2) + (p.y-y2)*(p.y-y2));
      double r1 = Math.pow(R1,-1.5);
      double r2 = Math.pow(R2,-1.5);
      f.x = factor1*(p.x-x1)*r1 + factor2*(p.x-x2)*r2;
      f.y = factor1*(p.y-y1)*r1 + factor2*(p.y-y2)*r2;
      return f;
    }
  }
  
  public static void main(String[] args)
  {
    Random random = new Random(); // 1:45 for (640, 480, 80)
    int seed = random.nextInt();
    //seed = -1180828986 + 1;
    random = new Random(seed);
    System.out.println("Random seed = "+seed);
    
    ScalarImage input = new ScalarImage(width + streamlen, height + streamlen);
    input.setRandom(random);
    input.rescale(2.0, -1.0);
    double[] ikernel = {0.0, 1.0/16, 1.0/8, 1.0/16, 1.0/8, 1.0/4, 1.0/8, 1.0/16, 1.0/8, 1.0/16};
    input.convolve3x3(ikernel);
    input.rescale(1.4, 0.0);
    
    AccumImage output = new AccumImage(width, height);
    
    Vector2dField field = new Field();
    
   // FLIC2 flic = new FLIC2(input, output, field, 
   //   new Vec2Transform(new Vec2((width+streamlen)/2, (height+streamlen)/2), 0.5),
   //   new Vec2Transform(new Vec2(width/2, height/2), 0.5));
    DLIC flic = new DLIC(input, output, field, 
      new Vec2Transform(new Vector2d(streamlen/2, streamlen/2), 1.0),
      new Vec2Transform(new Vector2d(0, 0), 1.0));
    flic.setStreamLen(streamlen);
    //flic.SetIterator(new NoFLIC());
    flic.generateImage();
    
    System.out.println("Colorizing...");
    RGBImage rgbimage = new RGBImage(width, height);
    double[] kernel = {0.0, -0.5, -1.0, 0.0, -1.0, 0.0, 1.0, 0.0, 1.0, 0.5};
    output.convolve3x3(kernel);
    output.rescale(0.5, 0.0);
    double[] kernel2 = {0.0, 0.0, 0.125, 0.0, 0.125, 0.5, 0.125, 0.0, 0.125, 0.0};
    output.convolve3x3(kernel2);
    output.rescale(0.5, 0.5);
    rgbimage.fromScalarImageMagnitude(output, field,
      new Vec2Transform(new Vector2d(0, 0), 1.0), new Colorizer(0.6, 2., .19, true));
    ImageIO.WriteTIFF(rgbimage,"c:\\test1.tif");
   
    System.exit(0);
  }
  
}
