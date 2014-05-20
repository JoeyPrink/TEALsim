/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: AccumImage.java,v 1.4 2008/02/11 19:54:36 pbailey Exp $ 
 * 
 */

package teal.visualization.image;

import javax.vecmath.*;


public class AccumImage extends ScalarImage {
  
    /**
     * teal.visualization.image.AccumImage: Stores a monochrome float image with alpha
   * = < width, height, float-buffer, alpha-buffer, minalpha, coverage >.
   *
   * An AccumImage extends ScalarImage to include an alpha component for every
   * scalar value. The alpha values are pre-multiplied into the scalar, so
   * to obtain the scalar value at a pixel, the alpha must be divided out of
   * it. This sort of image is useful for accumulation operations.
   *
   * Each AccumImage also automatically maintains information about the
   * coverage of the image. Every time a pixel surpasses the 'minalpha'
   * requirement, 'coverage' is incremented to indicated the total 
   * count of such pixels.
   */
  
    // a[] stores all the alpha values for the corresponding array f[]
    public float[] a;
    // 'minalpha' defines the minimum alpha required to consider a pixel "covered"
    public double minalpha;
    // 'maxalpha' defines the maximum alpha, after which the pixel is no more modified
    public double maxalpha;
    // 'coverage' indicates the number of pixels whose alpha exceeds 'minalpha'
    public int coverage;
  
    public AccumImage(int width, int height)
    /* Constructs a new AccumImage = 
     *   < width, height, zero-buffer, zero-buffer, 1.0, 0 > */
    {
        super(width, height);
        a = new float[size];
        for (int k = 0; k<size; ++k)  a[k] = 0.0f;
        minalpha = 1.0;
        maxalpha = 3.0; 
        coverage = 0;
    }

    public void setZero()
    /* Sets the samples in 'this' to zero, with an alpha of 1.0 */
    {
        super.setZero();
        for (int k = 0; k<size; ++k) a[k] = 1.0f;
        coverage = ( 1.0 >= minalpha ) ? size : 0;
    }
  
    public void clear()
    /* Clears both the scalar and alpha components in 'this' */
    {
        super.clear();
        for (int k = 0; k<size; ++k) a[k] = 0.0f;
        coverage = 0;
    }
  
    public void setAlpha()
    /* Sets the scalar component of 'this' to the alpha component */
    {
        for (int k = 0; k<size; ++k)
        {
            f[k] = a[k];
            a[k] = 1.0f;
        }
        coverage = ( 1.0 >= minalpha ) ? size : 0;
    }
  
    public void copy(AccumImage image, int xorigin, int yorigin)
    /* Copies the scalar and alpha values from the sub-window of 'image'
    *   starting at (xorigin, yorigin) to 'this'.
    * Requires: the sub-window fits inside 'image' */
    {
        if ( ( xorigin + width ) > image.width || ( yorigin + height ) > image.height )
            throw new RuntimeException( "AccumImage.Copy: Window too large" );
        int k = 0;
        for( int j = 0; j < height; j++ )
        {
            int l = image.offset[ j + yorigin ] + xorigin ;
            for( int i = 0; i < width ; i++ )
            {
                f[k] = image.f[l];
                a[k] = image.a[l];
                k++; l++;
            }
        }
        minalpha = image.minalpha;
        maxalpha = image.maxalpha;
        coverage = image.coverage;
    }
  
    public void copy(AccumImage image)
    /* Copies the scalar and alpha values from the top-left corner of 'image'
     *   to 'this'
     * Requires: 'image' is at least as large as 'this' */
    {
        copy(image, 0, 0);
    }
  
    public void normalize()
    /* Divides out the alpha component in 'this', renormalizing it to 1.0 */
    {
        for( int k = 0; k < size; k++ )
        {
            f[k] = ( a[k]==0.0f ) ? 0.0f : f[k] / a[k];
            a[k] = 1.0f;
        }
        coverage = ( 1.0 >= minalpha ) ? size : 0;
    }
  
    public void normalize(int x, int y)
    /* Divides out the alpha component at coordinate (x, y) in 'this', 
     *  renormalizing it to 1.0
     * Requires: 0<=x<width, 0<=y<height */
    {
        int k = offset[y] + x;
        coverage -= ( a[k] >= minalpha ) ? 1 : 0;
        f[k] = ( a[k]==0.0f ) ? 0.0f : f[k] / a[k];
        a[k] = 1.0f;
        coverage += ( 1.0 >= minalpha ) ? 1 : 0;
    }
  
    public double get(int k)
    /* Returns: the value at (x, y) with the alpha divided out
     * Requires: k >= 0 && k < size */
    {
        if( inBounds(k) )
        {
          
            return ( a[k] == 0.0f ) ? 0.0 : (double) ( f[k] / a[k] );
        }
        else
        {
            System.out.println( "Domain Exception: (" + k + ")" );
            throw new RuntimeException();
        }
    }
    public double get(int x, int y)
    /* Returns: the value at (x, y) with the alpha divided out
     * Requires: 0<=x<width and 0<=y<height */
    {
        if( inBounds(x,y) )
        {
            int k = offset[y] + x;
            return ( a[k] == 0.0f ) ? 0.0 : (double) ( f[k] / a[k] );
        }
        else
        {
            System.out.println( "Domain Exception: (" + x + "," + y + ")" );
            throw new RuntimeException();
        }
    }
    
    /**
     * returns the value as float, no bounds check.
     * @param k
     * @return
     */
    public float getF(int k){
    	return ( a[k] == 0.0f ) ? 0.0f :  f[k] / a[k] ;
    }
  
     public double getAlpha(int x, int y)
    /* Returns: the alpha value at (x, y)
     * Requires: 0<=x<width and 0<=y<height */
    {
        return ( inBounds( x, y ) ) ? a[offset[y] + x] : 0.0;
    }
  
    public void set(int x, int y, double s, double sa)
    /* Sets the scalar and alpha value at (x, y) to (s, sa).
     * If (x, y) is out-of-bounds, this has no effect. */
    {
        if ( inBounds(x,y) )
        {
            int k = offset[y] + x;
            coverage -= ( a[k] >= minalpha ) ? 1 : 0;
            f[k] = (float) s;
            a[k] = (float) sa;
            coverage += ( a[k] >= minalpha ) ? 1 : 0;
        }
    }
    
    public void accumulate(int x, int y, double s)
    /* Adds 's' to the scalar value at (x, y) and increments the corresponding
    *   alpha value. If (x, y) is out-of-bounds, it has no effect. */
    {
        if (inBounds(x,y))
        {
            int k = offset[y] + x;
            coverage -= ( a[k] >= minalpha ) ? 1 : 0;
            f[k] += (float) s;
            a[k] += 1.0f;
            coverage += ( a[k] >= minalpha ) ? 1 : 0;
        }
    }
  
    public void accumulate(int x, int y, double s, double sa)
    /* Adds 's' and 'sa' to the scalar and alpha values at (x, y) if it is
     *   in-bounds. */
    {
        if (inBounds(x,y))
        {
            int k = offset[y] + x;
            coverage -= ( a[k] >= minalpha ) ? 1 : 0;
            f[k] += (float) s;
            a[k] += (float) sa;
            coverage += ( a[k] >= minalpha ) ? 1 : 0;
        }
    }
  
    public void accumulateBilinear(double x, double y, double s)
    /* Bilinearly accumulates 's' to the four integer grid points surrounding
     *   the continuous coordinate (x, y), weighting the alpha accumulation
     *   bilinearly as well. */
    {
        int xi = (int) x;
        int yi = (int) y;
        double xf = x - xi;
        double yf = y - yi;
        double b;
        b = ( 1.0 - xf ) * ( 1.0 - yf );
        accumulate( xi, yi, s * b, b );
        b = xf * ( 1.0 - yf );
        accumulate( xi + 1, yi, s * b, b );
        b = ( 1.0 - xf ) * yf;
        accumulate( xi, yi + 1, s * b, b );
        b = xf * yf;
        accumulate( xi + 1, yi + 1, s * b, b );
    }
  
    public void accumulateBilinear(Vector2d v, double s)
    /* Bilinearly accumulates 's' to the four integer grid points surrounding
     *   the continuous coordinate 'v', weighting the alpha accumulation
     *   bilinearly as well. */
    {
        accumulateBilinear( v.x, v.y, s );
    }
  
    public boolean hasSingularity()
    /* Returns: true if an scalar or alpha value in 'this' is invalid */
    {
        if( super.hasSingularity() ) return true;
        for(int k = 0; k < size; k++ )
            if( Float.isInfinite( a[k] ) || Float.isNaN( a[k] ) )
                return true;
        return false;
    }

/*    ************************************************************
      *  Code after this remark was added by Mesrob Ohannessian. *
      ************************************************************      */

    public void accumulateC( int x, int y, double s )
    {
        if( inBounds( x, y ) )
        {
            int k = offset[y] + x;
            if( a[k] < maxalpha )
            {
                coverage -= ( a[k] >= minalpha ) ? 1 : 0;
                f[k] += (float) s;
                a[k] += (float) 1;
                coverage += ( a[k] >= minalpha ) ? 1 : 0;
            }
        }
    }
    
    public void accumulateC( int x, int y, double s, double sa )
    {
        if( inBounds( x, y ) )
        {
            int k = offset[y] + x;
            if( a[k] < maxalpha )
            {
                coverage -= ( a[k] >= minalpha ) ? 1 : 0;
                f[k] += (float) s;
                a[k] += (float) sa;
                coverage += ( a[k] >= minalpha ) ? 1 : 0;
            }
        }
    }

    public void accumulateBilinearC( double x, double y, double s)
    {
        int xi = (int) x;
        int yi = (int) y;

        double xf = x - xi;
        double yf = y - yi;
        
        double b;
        b = ( 1.0 - xf ) *( 1.0 - yf );
        accumulateC( xi, yi, s * b, b );
        b = xf * ( 1.0 - yf );
        accumulateC( xi + 1, yi, s * b, b );
        b = ( 1.0 - xf ) * yf;
        accumulateC( xi, yi + 1, s * b, b );
        b = xf * yf;
        accumulateC( xi + 1, yi + 1, s * b, b );
    }
  
    public void accumulateBilinearC( double x, double y, double s, double sa )
    {
        int xi = (int) x;
        int yi = (int) y;

        double xf = x - xi;
        double yf = y - yi;
        
        double b;
        b = ( 1.0 - xf ) *( 1.0 - yf );
        accumulateC( xi, yi, s * b, sa * b );
        b = xf * ( 1.0 - yf );
        accumulateC( xi + 1, yi, s * b, sa * b );
        b = ( 1.0 - xf ) * yf;
        accumulateC( xi, yi + 1, s * b, sa * b );
        b = xf * yf;
        accumulateC( xi + 1, yi + 1, s * b, sa * b );
    }
  
    public void drawLine( double i1, double j1, double i2, double j2, double c )
    {
        if( Math.abs( i1 - i2 ) > Math.abs( j1 - j2 ) )
        {
            if ( i2 > i1 )
                for( double i = i1; i < i2; i += 1. )
                {
                    double j = j1 + ( i - i1 ) /  ( i2 - i1 ) * ( j2 - j1 );
                    accumulateBilinear( i, j, c);
                }
            else
                for( double i = i1; i > i2; i -= 1. )
                {
                    double j = j1 + ( i - i1 ) /  ( i2 - i1 ) * ( j2 - j1 );
                    accumulateBilinear( i, j, c);
                }
        }
        else if( j1 != j2 )
        {
            if( j2 > j1 )
                for( double j = j1; j < j2; j += 1. )
                {
                    double i = i1 + ( j - j1 ) / ( j2 - j1 ) * ( i2 - i1 );
                    accumulateBilinear( i, j, c);
                }
            else
                for( double j = j1; j > j2; j -= 1. )
                {
                    double i = i1 + ( j - j1 ) / ( j2 - j1 ) * ( i2 - i1 );
                    accumulateBilinear( i, j, c);
                }
        }
        else
            accumulateBilinear( i1, j1, c );
    }

    public void drawLineC( double i1, double j1, double i2, double j2, double c)
    {
        if( Math.abs( i1 - i2 ) > Math.abs( j1 - j2 ) )
        {
            if ( i2 > i1 )
                for( double i = i1; i < i2; i += 1. )
                {
                    double j = j1 + ( i - i1 ) /  ( i2 - i1 ) * ( j2 - j1 );
                    accumulateBilinearC( i, j, c );
                }
            else
                for( double i = i1; i > i2; i -= 1. )
                {
                    double j = j1 + ( i - i1 ) /  ( i2 - i1 ) * ( j2 - j1 );
                    accumulateBilinearC( i, j, c );
                }
        }
        else if( j1 != j2 )
        {
            if( j2 > j1 )
                for( double j = j1; j < j2; j += 1. )
                {
                    double i = i1 + ( j - j1 ) / ( j2 - j1 ) * ( i2 - i1 );
                    accumulateBilinearC( i, j, c );
                }
            else
                for( double j = j1; j > j2; j -= 1. )
                {
                    double i = i1 + ( j - j1 ) / ( j2 - j1 ) * ( i2 - i1 );
                    accumulateBilinearC( i, j, c );
                }
         }
        else
            accumulateBilinearC( i1, j1, c );
    }

    
}
