/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ScalarImage.java,v 1.4 2008/02/11 16:22:02 pbailey Exp $ 
 * 
 */

package teal.visualization.image;

import java.awt.*;
import java.awt.color.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;

import javax.vecmath.*;

import teal.field.*;
import teal.util.*;

public class ScalarImage {
  
    /** EMSimulations.Image.ScalarImage: Stores a monochrome image of floats
   *
   * = < width, height, float-buffer >
   *
   * A ScalarImage stores a rectangular array of scalar values.
   * The image's width and height are specified upon construction, and
   * a float-buffer is created of size width*height, storing the data
   * in left-right, top-down scanline order. Though the scalar data is
   * stored internally as floats to save space, they are accessed as
   * doubles for compatibility.
   *
   * The ScalarImage can also be viewed as a continuous field over a
   * rectangle, with the value at integer coordinates equal to the scalar
   * values, and the values at fractional coordinates interpolated between
   * them. Since the values are interpolated, the effective size of the
   * field becomes (width-1) by (height-1).
   */
  
  /* width, height store the dimensions of the image.
   * size = width*height, the number of scalar values */
  public static final int TYPE_BYTE_RANDOM = -1; 
  
  public final int width, height, size;
  /* f[] stores all the scalar values in scanline order */
  protected float[] f;
  /* offset[] indexes the left-most pixel of each scanline in f[]
   * The index of pixel (x,y) is therefore offset[y]+x */
  public final int[] offset;
  
  public ScalarImage(int width, int height)
  /* Constructs a new ScalarImage = < width, height, zero-buffer > */
  {
    this.width = width;
    this.height = height;
    size = width*height;
    
    f = new float[size];
    offset = new int[height];
    for (int j = 0; j<height; ++j)
      offset[j] = width*j;
    
    for (int k = 0; k<size; ++k)
      f[k] = 0.0f;
  }
   public ScalarImage(int width, int height, Random random)
  /* Constructs a new ScalarImage = < width, height, zero-buffer > */
  {
    this.width = width;
    this.height = height;
    size = width*height;
    
    f = new float[size];
    offset = new int[height];
    for (int j = 0; j<height; ++j)
      offset[j] = width*j;
      
    for (int k = 0; k<size; ++k)
      f[k] = random.nextFloat();

  }
   
   /**
    * Copy constructor, may be used to convert AccumImage.
    * @param image
    */
   public ScalarImage(ScalarImage image)
   {
     this.width = image.width;
     this.height = image.height;
     this.size = this.width*this.height;
     
     f = new float[size];
     offset = new int[height];
     for (int j = 0; j<height; ++j)
       offset[j] = width*j;
       
     for (int k = 0; k<size; ++k)
       f[k] = image.getF(k);;

   }
  public float[] getData()
  {
    return f;
  }
  public void setZero()
  /* Sets all the scalar values to zero */
  {
    for (int k = 0; k<size; ++k)
      f[k] = 0.0f;
  }
  
  public void clear()
  /* Clears all the scalar values to zero.
   * This is different from SetZero() in the derived classes AccumImage, etc */
  {
    for (int k = 0; k<size; ++k)
      f[k] = 0.0f;
  }
  
  public void copy(ScalarImage image, int xorigin, int yorigin)
  /* Copies the scalar values from the sub-window of 'image' starting at
   *   (xorigin, yorigin) to 'this'.
   * Requires: the sub-window fits inside 'image' */
  {
    if (((xorigin+width)>image.width) || ((yorigin+height)>image.height))
      throw new RuntimeException("ScalarImage.Copy: Window too large");
    
    for (int j = 0, k = 0; j<height; ++j)
      for (int i = 0, l = image.offset[j+yorigin] + xorigin; i<width; ++i, ++k, ++l)
        f[k] = image.getF(l);
  }
  
  public void copy(ScalarImage image)
  /* Copies the scalar values from the top-left corner of 'image' to 'this'
   * Requires: 'image' is at least as large as 'this' */
  {
    copy(image, 0, 0);
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
  /** Specialized version to re turn value within the range of a byte color component 0.0 to 255.0. */
  private double clamp(double x)
  {
    if (x<0.)
      return 0.;
    else if (x>255.)
      return 255.;
    else
      return x;
  }
  public void stretchBilinear(ScalarImage image)
  /* Rescales the scalar data in 'image' to 'this' using bilinear interpolation. */
  {
    double xscale = (image.width-1.0)/(width-1.0);
    double yscale = (image.height-1.0)/(height-1.0);
    for (int j = height-1, k = size-1; j>=0; --j)
      for (int i = width-1; i>=0; --i, --k)
        f[k] = (float)image.getBilinear(i*xscale, j*yscale);
  }
  
  public boolean inBounds(int k)
  /* Returns: true if 0<=x<width and 0<=y<height, false otherwise */
  {
    return (k>=0) && (k<size);
  }
  
  public boolean inBounds(int x, int y)
  /* Returns: true if 0<=x<width and 0<=y<height, false otherwise */
  {
    return (x>=0) && (y>=0) && (x<width) && (y<height);
  }
  
  public boolean inBounds(double x, double y)
  /* Returns: true if 0.0<=x<=(width-1.0) and 0.0<=y<=(height-1.0),
   *   false otherwise, the domain check for when 'this' is viewed as
   *   a continuous field. */
  {
    return (x>=0.0) && (y>=0.0) && (x<=(width-1.0)) && (y<=(height-1.0));
  }
  
  public boolean inBounds(Vector2d v)
  /* Returns: true if 'v' is within the domain of 'this' when viewed as
   *   a continuous field. */
  {
    return inBounds(v.x, v.y);
  }
  
  public double get(int k)
  /* Returns: the scalar value at (k)
   * Requires: k >= 0 && k < size */
  {
    if (inBounds(k))
      return (double)f[k];
    else
      throw new RuntimeException("ScalarImage.get: RuntimeException at ("+k+")");
  }
  /**
   * returns the value as float, no bounds check.
   * @param k
   * @return
   */
  public float getF(int k){
	  return f[k];
  }
  
  public double get(int x, int y)
  /* Returns: the scalar value at (x, y)
   * Requires: 0<=x<width and 0<=y<height */
  {
    if (inBounds(x,y))
      return (double)f[offset[y] + x];
    else
      throw new RuntimeException("ScalarImage.get: RuntimeException at ("+x+","+y+")");
  }
  
  public void set(int x, double s)
  /* Sets the scalar value at (x) to 's'. If it is outside the domain
   *   of 'this', it has no effect. */
  {
    if (inBounds(x))
      f[x] = new Float(s).floatValue();
    else
      throw new RuntimeException("ScalarImage.Set: RuntimeException at (" + x + ")");
  }
  
  public void set(int x, int y, double s)
  /* Sets the scalar value at (x, y) to 's'. If it is outside the domain
   *   of 'this', it has no effect. */
  {
    if (inBounds(x,y))
      f[offset[y] + x] = new Float(s).floatValue();
    else
      throw new RuntimeException("ScalarImage.Set: RuntimeException at ("+x+","+y+")");
  }

   public void accumulate(int x, int y, double s)
  /* Adds 's' to the scalar value at (x, y). If it is outside the domain
   *   of 'this', it has no effect. */
  {
    if (inBounds(x,y))
      f[offset[y] + x] += (float)s;
  }
  
  public double getBilinear(double x, double y)
  /* Returns: the bilinearly-interpolated value of the continuous field
   *   at (x, y).
   * Requires: (x, y) is inside the domain of the field */
  {
    if (!inBounds(x, y))
      throw new RuntimeException("ScalarImage.getBilinear: RuntimeException at ("+x+","+y+")");
   
    int xi, yi;
    double xf, yf;
    if (x==(double)(width-1)) {
      xi = width-2;
      xf = 1.0;
    } else {
      double xpf = Math.floor(x);
      xi = (int)xpf;
      xf = x - xpf;
    }
    if (y==(double)(height-1)) {
      yi = height-2;
      yf = 1.0;
    } else {
      double ypf = Math.floor(y);
      yi = (int)ypf;
      yf = y - ypf;
    }
    
    double b1 = get(xi, yi);
    double b2 = get(xi+1, yi);
    double b3 = get(xi, yi+1);
    double b4 = get(xi+1, yi+1);
    
    double bb1 = b1 + xf*(b2 - b1);
    double bb2 = b3 + xf*(b4 - b3);
    
    return bb1 + yf*(bb2 - bb1);
  }
  
  public double getBilinear(Vector2d v)
  /* Returns: the bilinearly-interpolated value of the continuous field
   *   at 'v'.
   * Requires: 'v' is inside the domain of the field */
  {
    return getBilinear(v.x, v.y);
  }
  
  public void accumulateBilinear(double x, double y, double s)
  /* Bilinearly accumulates 's' to the four integer grid points surrounding
   *   the continuous coordinate (x, y). */
  {
    double xpf = Math.floor(x);
    int xi = (int)xpf;
    double xf = x - xpf;
    
    double ypf = Math.floor(y);
    int yi = (int)ypf;
    double yf = y - ypf;
    
    double b;
    b = (1.0-xf)*(1.0-yf);
    accumulate(xi, yi, s*b);
    b = xf*(1.0-yf);
    accumulate(xi+1, yi, s*b);
    b = (1.0-xf)*yf;
    accumulate(xi, yi+1, s*b);
    b = xf*yf;
    accumulate(xi+1, yi+1, s*b);
  }
  
  public void accumulateBilinear(Vector2d v, double s)
  /* Bilinearly accumulates 's' to the four integer grid points surrounding
   *   the continuous coordinate 'v'. */
  {
    accumulateBilinear(v.x, v.y, s);
  }
  
  public void rescale(double scale)
  /* Rescales all the scalar values in 'this' by the rule:
   *   f' = f*scale + add */
  {
    for (int k = 0; k<size; ++k)
      f[k] = (float)(f[k]*scale);
  }
  
  public void rescale(double scale, double add)
  /* Rescales all the scalar values in 'this' by the rule:
   *   f' = f*scale + add */
  {
    for (int k = 0; k<size; ++k)
      f[k] = (float)(f[k]*scale + add);
  }
  
  public void power(double exp)
  /* Transforms all the scalar values in 'this' by the rule:
   *   f' = f^exp */
  {
    for (int k = 0; k<size; ++k)
      f[k] = (float)Math.pow(f[k], exp);
  }
  
  public void convolve3x3(double[] kernel)
  /* Performs a convolution on 'this' with a 3x3 kernel and scalar offset. */
  {
    /* kernel is of type double[10], and the convolution computed is of the form:
       out(i,j) = c0 + c1*out(i-1,j-1) + c2*out( i ,j-1) + c3*out(i+1,j-1) +
                       c4*out(i-1, j ) + c5*out( i , j ) + c6*out(i+1, j ) +
                       c7*out(i-1,j+1) + c8*out( i ,j+1) + c9*out(i+1,j+1)
       where ci = kernel[i] */
    float[] out = new float[size];
    for (int k = 0; k<size; ++k)
      out[k] = (float)kernel[0];
    
    for (int j = height-1, k = width+1, l = 0; j>0; --j, ++k, ++l)
      for (int i = width-1; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[1]*f[l];
    for (int j = height-1, k = width, l = 0; j>0; --j)
      for (int i = width; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[2]*f[l];
    for (int j = height-1, k = width, l = 1; j>0; --j, ++k, ++l)
      for (int i = width-1; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[3]*f[l];
    for (int j = height, k = 1, l = 0; j>0; --j, ++k, ++l)
      for (int i = width-1; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[4]*f[l];
    for (int j = height, k = 0, l = 0; j>0; --j)
      for (int i = width; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[5]*f[l];
    for (int j = height, k = 0, l = 1; j>0; --j, ++k, ++l)
      for (int i = width-1; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[6]*f[l];
    for (int j = height-1, k = 1, l = width; j>0; --j, ++k, ++l)
      for (int i = width-1; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[7]*f[l];
    for (int j = height-1, k = 0, l = width; j>0; --j)
      for (int i = width; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[8]*f[l];
    for (int j = height-1, k = 0, l = width+1; j>0; --j, ++k, ++l)
      for (int i = width-1; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[9]*f[l];

    for (int k = 0; k<size; ++k)
      f[k] = out[k];
    out = null;
  }
  
  public boolean hasSingularity()
  /* Returns: true if any scalar value in 'this' is invalid */
  {
    for (int k = 0; k<size; ++k)
      if (Float.isInfinite(f[k]) || Float.isNaN(f[k]))
        return true;
    return false;
  }
  
  public void setRandom(Random random)
  /* Sets all the scalars in 'this' to random values uniformly distributed
   *   over (0.0, 1.0) using the pseudo-random generator 'random'. */
  {
    //setZero();
    for (int k = 0; k<size; ++k)
      f[k] = random.nextFloat();
  }
  
  public void setRandom()
  /* Sets all the scalars in 'this' to random values uniformly distributed
   *   over (0.0, 1.0). */
  {
    setRandom(new Random());
  }
  
  public void vec2FieldMagnitude(Field field, AffineTransform ftoi)
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
    Vector3d v = new Vector3d();
    Point2D.Double p = new Point2D.Double();
    for (int j = 0, k = 0; j<height; ++j)
      for (int i = 0; i<width; ++i, ++k) {
        p.x = i;
        p.y = j;
		itof.transform(p,p);
        v = field.get(p.x,p.y,0.0);
        f[k] = (float)Math.sqrt(v.x*v.x + v.y*v.y);
      }
  }
  
  public void vec2FieldZero(Field field, AffineTransform ftoi)
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
    Vector3d  v = new Vector3d();
    Point2D.Double p = new Point2D.Double();
    for (int j = 0, k = 0; j<height; ++j)
      for (int i = 0; i<width; ++i, ++k) {
        p.x = i;
        p.y = j;
		itof.transform(p,p);
        v = field.get(p.x,p.y,0.0);
        if ((v.x==0.0) && (v.y==0.0))
          f[k] = 1.0f;
        else
          f[k] = 0.0f;
      }
  }
  
  public void modulate(ScalarImage image)
  {
    for (int k = 0; k<size; ++k)
      f[k] *= image.f[k];
  }
  
  public void add(ScalarImage image)
  {
    for (int k = 0; k<size; ++k)
      f[k] += image.f[k];
  }
  
  public void clamp(double min, double max)
  {
    for (int k = 0; k<size; ++k) {
      if (f[k]<min)
        f[k] = (float)min;
      else if (f[k]>max)
        f[k] = (float)max;
    }
  }
  
   public void normalize()
   {
    normalize(1.0);
   }
  
	public void analyze(boolean doit) {

		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		for (int k = 0; k < size; k++) {
			//    	System.out.println(f[k]);
			/*
			 * if (f[k] == Float.NaN) { System.out.println("NaN at k= "+k);
			 * f[k] = 0f; }
			 */
			if (Float.isNaN(f[k])) {
				System.out.println("NaN at k= " + k);
				f[k] = 0f;
				continue;
			}
			if (Float.isInfinite(f[k])) {
				if (f[k] < 0) {
					System.out.println("-Infinity at k= " + k);
					f[k] = 0f; //-1000f;
				} else {
					System.out.println("+Infinity at k= " + k);
					f[k] = 0f; //+1000f;
				}
				continue;
			}
			//System.out.print(k +"="+f[k]+ ", ");
			min = Math.min(min, f[k]);
			max = Math.max(max, f[k]);
		}
		
		
		int N = 256;
		float [] histogram = new float[N];
		
		for(int k=0; k<size; k++) {
			int level = (int) ( 0.999f*(float)N*(f[k]-min)/(max-min) );
			try {
				histogram[level]++;
			} catch( ArrayIndexOutOfBoundsException e) {
				System.out.println("ArrayIndexOutOfBoundsException in ScalarImage.analyze(double) [A].");
			}
		}
		
		float [] cumulative = new float[N];
		if( histogram[0] > 0 ) {
			if( doit ) {
				cumulative[0] = histogram[0];
			} else {
				cumulative[0] = 1; //histogram[0];
			}
		}
		for(int i=1; i<N; i++) {
			if( histogram[i] > 0 ) {
				if( doit ) {
					cumulative[i] = cumulative[i-1] + histogram[i];
				} else {
					cumulative[i] = cumulative[i-1] + 1; //histogram[i];
				}
			} else {
				cumulative[i] = cumulative[i-1];
			}
		}

/*		for(int k=0; k<size; k++) {
			int level = (int) ( 0.999f*(float)N*(f[k]-min)/(max-min) );
			try {
				f[k]=(cumulative[level]-cumulative[0])/(cumulative[N-1]-cumulative[0]);
			} catch( ArrayIndexOutOfBoundsException e) {
				System.out.println("ArrayIndexOutOfBoundsException in ScalarImage.analyze(double) [B].");
			}
		}
*/
		for(int k=0; k<size; k++) {
			float x, x1, x2, f1, f2;
			try {
				x=Math.abs((f[k]-min)/(max-min));
				x1=(float)Math.floor((float)(N-1)*x*0.999f)/(float)(N-1);
				x2=(float)Math.ceil((float)(N-1)*x*0.999f)/(float)(N-1);
				f1=(cumulative[(int)Math.floor((float)(N-1)*x*0.999f)]-cumulative[0])/(cumulative[N-1]-cumulative[0]);
				f2=(cumulative[(int)Math.ceil((float)(N-1)*x*0.999f)]-cumulative[0])/(cumulative[N-1]-cumulative[0]);
				f[k]=(f2-f1)*(x-x1)/(x2-x1)+f1;
			} catch( ArrayIndexOutOfBoundsException e) {
				System.out.println("ArrayIndexOutOfBoundsException in ScalarImage.analyze(double) [C]. " + e.getMessage());
			}
		}

		return;
		
		
		
/*
		double offset = 0. - min;
		if ((max - min) != 0f) {
			scale /= (max - min);
			offset *= scale;
			rescale(scale, offset);
		}
		System.out.println("Normalizing using: min= " + min + " max= " + max
				+ ", through scaleAdd(" + scale + ", " + offset + ").");
		min = 1000f;
		max = -1000f;
		for (int k = 0; k < size; k++) {
			if (Float.isNaN(f[k])) {
				System.out.println("NaN at k= " + k + " after normalization.");
				f[k] = 0f;
				continue;
			}
			min = Math.min(min, f[k]);
			max = Math.max(max, f[k]);
		}
		System.out.println("After normalization: min= " + min + " max= " + max);
*/
	}
	
	public Tuple4f getDynamics(){
		Tuple4f dynamics = new Vector4f();
		long count = 0;
		double total = 0;
		
		
		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		
		
		for (int k = 0; k < size; k++) {
			//Do not include NAN or isInfinite in dynamics
			if( !( (Float.isNaN(f[k])) || (Float.isInfinite(f[k])))){
			
			//System.out.print(k +"="+f[k]+ ", ");
			min = Math.min(min, f[k]);
			max = Math.max(max, f[k]);
			total += (double)f[k];
			count++;
			}
		}
		dynamics.w = min;
		dynamics.x =max;
		dynamics.y = (float) (total/(double)count);
		float range = max-min;
		int N = 256;
		System.out.println("min: " + min + " Max: " + max + " Range: " + range);
		int [] histogram = new int[N];
		int level = 0;
		float quant = N * 0.999f;
		for(int k=0; k<size; k++) {
			if( !( (Float.isNaN(f[k])) || (Float.isInfinite(f[k])))){
				level = (int) ( quant * ( (f[k]-min)/range ));
				try {
					histogram[level]++;
				} catch( ArrayIndexOutOfBoundsException e) {
					System.out.println("ArrayIndexOutOfBoundsException in ScalarImage.analyze(double) [A].");
				}
			}
		}
		for(int j=0; j <N;j++){
			System.out.println("Level: " + j +" count: " + histogram[j] +  " value: "
					+ (float)((j/quant * (max-min)) + min));	
		}
		int target = (int) (count * 0.9);
		int cum = 0;
		for(int i=0; i<N;i++){
			cum += histogram[i];
			System.out.println("target: " + target +  " cum: " + cum + " Level: " + i + " value: "
					+ ((i/quant * (max-min)) + min));
			if(cum >= target){
				System.out.println("target: " + target + " Level: " + i + " value: "
						+ ((i * (max-min)) + min));
				dynamics.z = (float)( (i/quant *(max-min)) + min);
				break;
			}
		}
		return dynamics; 
		
	}

	public void normalize(double range) {
		double scale = range;
		float min = 1000f;
		float max = -1000f;
		for (int k = 0; k < size; k++) {
			//    	System.out.println(f[k]);
			/*
			 * if (f[k] == Float.NaN) { System.out.println("NaN at k= "+k);
			 * f[k] = 0f; }
			 */
			if (Float.isNaN(f[k])) {
				System.out.println("NaN at k= " + k);
				f[k] = 0f;
				continue;
			}
			if (Float.isInfinite(f[k])) {
				if (f[k] < 0) {
					System.out.println("-Infinity at k= " + k);
					f[k] = 0f; //-1000f;
				} else {
					System.out.println("+Infinity at k= " + k);
					f[k] = 0f; //+1000f;
				}
				continue;
			}
			//System.out.print(k +"="+f[k]+ ", ");
			min = Math.min(min, f[k]);
			max = Math.max(max, f[k]);
		}
		//System.out.println();
		double offset = 0. - min;
		if ((max - min) != 0f) {
			scale /= (max - min);
			offset *= scale;
			rescale(scale, offset);
		}
		System.out.println("Normalizing using: min= " + min + " max= " + max
				+ ", through scaleAdd(" + scale + ", " + offset + ").");
		min = 1000f;
		max = -1000f;
		for (int k = 0; k < size; k++) {
/*			if (f[k] == Float.NaN) {
				System.out.println("NaN at k= " + k);
				f[k] = 0f;
			}
*/
			if (Float.isNaN(f[k])) {
				System.out.println("NaN at k= " + k + " after normalization.");
				f[k] = 0f;
				continue;
			}
			//System.out.print(k +"="+f[k]+ ", ");
			min = Math.min(min, f[k]);
			max = Math.max(max, f[k]);
		}
		System.out.println("After normalization: min= " + min + " max= " + max);
	}
	
  
  /*
  
     public void fromScalarImage(ScalarImage image, int xorigin, int yorigin, 
    double R, double G, double B)
  
  {
    if (((xorigin+width)>image.width) || ((yorigin+height)>image.height))
      throw new RuntimeException("RGBImage.fromScalarImage: Window too large");
    
    for (int j = 0, k = 0; j<height; ++j) {
      for (int i = 0, l = image.offset[j+yorigin] + xorigin; i<width; ++i, ++k, ++l) {
        r[k] = (byte)clamp(image.f[l]*R, 0, 255);
        g[k] = (byte)clamp(image.f[l]*G, 0, 255);
        b[k] = (byte)clamp(image.f[l]*B, 0, 255);
      }
    }
  }
  public BufferedImage convertImage(ScalarImage img, Color color) {
  	RGBImage rgbimage = new RGBImage( img.width, img.height );
  	rgbimage.fromScalarImageNew( img, 0, 0, color.getRed(), color.getGreen(), color.getBlue() );
  	return convertImage( rgbimage );
  }
*/

    public BufferedImage  getBufferedImage(int type, Color c) {
        
		BufferedImage image = null;
        float[] colComp = new float[3];
        c.getRGBColorComponents(colComp);  
        double red = (double) colComp[0];
        double green = (double) colComp[1];
        double blue = (double) colComp[2];
        //System.out.println("blue, green, red = "+ blue +", " + green + ", " + red);
                
        double x = 0.0;
        double x2;
        switch (type)
        {           
            case ScalarImage.TYPE_BYTE_RANDOM:
				{
					int numCol = 256;
					byte[] bBuf = new byte[numCol * 3];
					blue *= 255 * 4.;
					green *= 255 * 4.;
					red *= 255 * 4.;
					double delta = 1.0 / (double) (numCol + 1);
					int j = 0;
					for (int i = 0; i < numCol; i++) {
						if( i%5==0 )	x = 0.7*Math.random()+0.3*x;
						x2 = x * x;
						bBuf[j++] = (byte) clamp((510 - red) * x2 + (red - 255)
								* x);
						bBuf[j++] = (byte) clamp((510 - green) * x2
								+ (green - 255) * x);
						bBuf[j++] = (byte) clamp((510 - blue) * x2
								+ (blue - 255) * x);
						//x += delta;
					}
					IndexColorModel cm = new IndexColorModel(8, numCol, bBuf,
							0, false);
					//image = new
					// BufferedImage(width,height,BufferedImage.TYPE_BYTE_INDEXED,cm);
					byte[] idxBuffer = new byte[size];
					for (int i = 0; i < size; i++) {
						idxBuffer[i] = (byte) (clamp(f[i] * 255.));
					}
					DataBufferByte dataBuffer = new DataBufferByte(idxBuffer,
							size);
					int idxOffset[] = {0};
					int idxBits[] = {8};
					try {
						ComponentSampleModel idxSampleModel = new ComponentSampleModel(
								DataBuffer.TYPE_BYTE, width, height, 1, width,
								idxOffset);
						WritableRaster rasterIdx = java.awt.image.Raster
								.createWritableRaster(idxSampleModel,
										dataBuffer, new Point(0, 0));
						image = new BufferedImage(cm, rasterIdx, false, null);
					} catch (Exception e) {
						System.out
								.println("Exception caught while acquiring image:");
						System.out.println(e.getMessage());
					}
				}
				break;
            case BufferedImage.TYPE_BYTE_INDEXED:
				{
					int numCol = 256;
					byte[] bBuf = new byte[numCol * 3];
					blue *= 255 * 4.;
					green *= 255 * 4.;
					red *= 255 * 4.;
					double delta = 1.0 / (double) (numCol + 1);
					int j = 0;
					for (int i = 0; i < numCol; i++) {
						x2 = x * x;
						bBuf[j++] = (byte) clamp((510 - red) * x2 + (red - 255)
								* x);
						bBuf[j++] = (byte) clamp((510 - green) * x2
								+ (green - 255) * x);
						bBuf[j++] = (byte) clamp((510 - blue) * x2
								+ (blue - 255) * x);
						x += delta;
					}
					IndexColorModel cm = new IndexColorModel(8, numCol, bBuf,
							0, false);
					//image = new
					// BufferedImage(width,height,BufferedImage.TYPE_BYTE_INDEXED,cm);
					byte[] idxBuffer = new byte[size];
					for (int i = 0; i < size; i++) {
						idxBuffer[i] = (byte) (clamp(f[i] * 255.));
					}
					DataBufferByte dataBuffer = new DataBufferByte(idxBuffer,
							size);
					int idxOffset[] = {0};
					int idxBits[] = {8};
					try {
						ComponentSampleModel idxSampleModel = new ComponentSampleModel(
								DataBuffer.TYPE_BYTE, width, height, 1, width,
								idxOffset);
						WritableRaster rasterIdx = java.awt.image.Raster
								.createWritableRaster(idxSampleModel,
										dataBuffer, new Point(0, 0));
						image = new BufferedImage(cm, rasterIdx, false, null);
					} catch (Exception e) {
						System.out
								.println("Exception caught while acquiring image:");
						System.out.println(e.getMessage());
					}
				}
				break;
            case BufferedImage.TYPE_BYTE_GRAY:
                break;
            case BufferedImage.TYPE_3BYTE_BGR:
            default:
               
                
                
                byte[] byteBuffer = new byte[size * 3 ];
                blue *= 255 * 4.;
                green *= 255 * 4.;
                red *= 255 * 4.;
                
                int j = 0;
                for(int i = 0; i < size;i++)
                {                   
                    x = f[i];
                    x2 = x * x;
                    /*
                    byteBuffer[j++] = (byte)clamp( ( 2 * 255 - 4 * red ) * x * x + ( 4 * red - 255 ) * x);
                    byteBuffer[j++] = (byte)clamp( ( 2 * 255 - 4 * green ) * x * x + ( 4 * green - 255 ) * x);
                    byteBuffer[j++] = (byte)clamp( ( 2 * 255 - 4 * blue ) * x * x + ( 4 * blue - 255 ) * x); 
                    */
                    byteBuffer[j++] = (byte)clamp( ( 510 - red ) * x2 + ( red - 255 ) * x);
                    byteBuffer[j++] = (byte)clamp( ( 510 - green ) * x2 + ( green - 255 ) * x);
                    byteBuffer[j++] = (byte)clamp( ( 510 - blue ) * x2 + ( blue - 255 ) * x);                   
                                      
                }
 				 DataBufferByte dataBuffer = new DataBufferByte( byteBuffer, size * 3 );       
		        int componentOffset[] = { 0, 1, 2 };
		        int componentBits[] = { 8, 8, 8 };
		        try {
			        WritableRaster raster = java.awt.image.Raster.createWritableRaster(
			            new PixelInterleavedSampleModel( DataBuffer.TYPE_BYTE,
			            width, height, 3, width * 3, componentOffset ),
			            dataBuffer,
			            new Point( 0, 0) );
			        image = new BufferedImage(
			            new ComponentColorModel( ColorSpace.getInstance( ColorSpace.CS_LINEAR_RGB ),
			            componentBits, false, false, ColorModel.OPAQUE, DataBuffer.TYPE_BYTE ),
			            raster,
			            false,
		                null );
			
		        } catch ( Exception e ) {
			        System.out.println( "Exception caught while acquiring image:" );
			        System.out.println( e.getMessage() );
		        }
		        break;
	    }
		return image;
	}
  
}
