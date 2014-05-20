/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: DLICGenerator.java,v 1.13 2010/07/23 21:38:08 stefan Exp $
 * 
 */

package teal.visualization.dlic;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

import javax.media.j3d.Transform3D;
import javax.vecmath.*;

import teal.config.*;
import teal.core.*;
import teal.field.*;
import teal.field.Field;
import teal.math.RectangularPlane;
import teal.util.*;
import teal.visualization.image.*;
import teal.visualization.processing.Colorizer;
import teal.visualization.processing.TColorizer;

/**
 *
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.13 $ 
 */

public class DLICGenerator extends AbstractElement implements ImageGenerator, ProgressEventListener {

    private static final long serialVersionUID = 3834594313689380920L;
    
    protected int type = teal.field.Field.E_FIELD;
    protected Color mColor = Teal.IDRAW_EFIELD_COLOR;
    protected int colorMode = Teal.ColorMode_COLOR;
    protected TColorizer colorizer;
    protected int width; 
    protected int height;
    protected Image mImage = null;
    protected Vector3dField field = null;

    protected RectangularPlane computePlane = null;

    protected transient Transform3D f2c = null; //new Vec2Transform( new Vec2( 0, 0 ), 1 );

    protected DLIC dlic = null;
    protected ScalarImage input = null;
    protected ScalarImage output = null;
    protected AccumImage canvas = null;
    private boolean prepared = false;
    private boolean isValid = false;
    
    // Used for timing generation
    private long millis = 0;

    protected Vector imageListeners;
    protected Vector progressListeners;

    protected String defaultMethod = null;

    public DLICGenerator() {
        computePlane = null;
        setDefaultMethod("computeFieldImage");
        imageListeners = new Vector();
        progressListeners = new Vector();
        f2c = new Transform3D();
        colorizer = new Colorizer();
    }

    public DLICGenerator(Vector3dField field) {
        this();
        setField(field);
    }

    public DLICGenerator(Vector3dField field, double xt, double yt, double xs, double ys) {
        this(field);
        f2c.set(new Vector3d(xt, yt, 0.));
        f2c.setScale(new Vector3d(xs, ys, 1.));
    }

    public void dispose() {
        if (mImage != null) {
            mImage.flush();
            mImage = null;
        }
        dlic = null;
        canvas = null;
        input = null;
        output = null;
    }
    
    public TColorizer getColorizer(){
    	return colorizer;
    }
    
    public void setColorizer(TColorizer colorizer){
    	this.colorizer = colorizer;
    }

    public boolean setDefaultMethod(String str) {
        if(str == null) {
        	defaultMethod = null;
        	return false;
        }

        boolean status = false;
        
        try {
 
            Method defaultM = this.getClass().getMethod(str, null);
            defaultMethod = str;
            status = true;
        } catch (NoSuchMethodException nse) {
            TDebug.printThrown(0, nse);
        } catch (SecurityException se) {
            TDebug.printThrown(0, se);
        }
        
        return status;
    }

    public void setValid(boolean b) {
        if ((isValid != b) && (!imageListeners.isEmpty())) {
            fireImageEvent(new ImageStatusEvent(this, b ? ImageStatusEvent.VALID : ImageStatusEvent.INVALID));
        }
        isValid = b;

    }

    public boolean getValid() {
        return isValid;
    }

    public Dimension getSize() {
        return new Dimension(width, height);

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int w, int h) {
        width = w;
        height = h;
        reset();
    }
    public void setSize(Dimension size) {
        setSize(size.width,size.height);
    }

    public void setWidth(int w) {
        width = w;
        reset();
    }

    public void setHeight(int h) {
        height = h;
        reset();
    }

    public void reset() {
        if (mImage != null) {
            mImage.flush();
            mImage = null;
        }
        if (canvas != null) canvas = null;
        if (input != null) input = null;
        if (output != null) output = null;
        prepared = false;
    }

    public void generateImage() {
        if (defaultMethod != null) {
            try {
                Object obj = this.getClass().getMethod(defaultMethod, null).invoke(this,  (Object[]) null);

            } catch (Exception e) {
                TDebug.printThrown(0, e, "Trying to invoke defaultMethod");
            }
        } else {
            TDebug.println(0, "No default method set for DLICImageGenerator");
        }
    }

    public boolean isImageGenerated() {
        return (isValid && mImage != null);
    }

    public Image getImage() {
        return mImage;
    }

    public void setField(Vector3dField fld) {
        setField(fld, fld.getType());
    }

    public void setField(Vector3dField fld, int type) {
        this.field = fld;
        this.type = type;
        switch (type) {
            case teal.field.Field.B_FIELD:
                mColor = Teal.IDRAW_BFIELD_COLOR;
                break;
            case teal.field.Field.E_FIELD:
                mColor = Teal.IDRAW_EFIELD_COLOR;
                break;
            case teal.field.Field.P_FIELD:
                mColor = Teal.IDRAW_PFIELD_COLOR;
                break;
            case teal.field.Field.EP_FIELD:
                mColor = Teal.IDRAW_EPOTENTIAL_COLOR;
                break;
            default:
                mColor = Teal.IDRAW_EFIELD_COLOR;
                break;
        }
    }

    public void setField(Vector3dField fld, int type, Color color) {
        this.field = fld;
        this.type = type;
        mColor = color;
    }

    public void setTransform(Transform3D trans) {
        this.f2c = trans;
        if (dlic != null) {
            Matrix4d m = new Matrix4d();
            f2c.get(m);
            dlic.setWorldToCanvas(m);
        }
    }

    public RectangularPlane getComputePlane() {
        return computePlane;
    }

    /**
     * Defines the world plane that the image will slice through including the displayed bounds.
     */
    public void setComputePlane(RectangularPlane recPlane) {
        computePlane = recPlane;
        Vector3d scale = computePlane.getScale();
        //System.out.println("ImgPlane Scale: " + scale +"  Width: "+ width + " Height: " + height);
        scale.x /= width;
        scale.y /= height;
        //System.out.println("Scale2: " + scale);
        Transform3D trans = new Transform3D();
        trans.setRotation(computePlane.getAxisAngle());
        trans.setScale(scale);
        trans.setTranslation(computePlane.getVertex0());
        trans.invert();
        //System.out.println("setcomputePlane transform:");
        //System.out.println(trans);
        setTransform(trans);
        reset();
    }

    public Quat4d getRotation() {
        Quat4d quat = null;
        if (computePlane != null) {
            quat = new Quat4d();
            quat.set(computePlane.getAxisAngle());
            //System.out.println(" getting rotation: " + quat);
        }
        return quat;
    }

    public double getImageScale() {
        double sc = 1.;
        if (computePlane != null) {
            Vector3d s = computePlane.getScale();
            sc = s.x;
        } else {
            Transform3D c2f = new Transform3D(f2c);
            c2f.invert();
            Point3d dis = new Point3d(width, height, 0.);
            Point3d zero = new Point3d();
            c2f.transform(dis);
            c2f.transform(zero);
            sc = dis.x - zero.x;
        }
        return sc;

    }

    public int getColorMode() {
        return colorMode;
    }

    public void setColorMode(int mode) {
        colorMode = mode;
    }

    public double getScale() {
        if (computePlane != null) {
            Vector3d s = computePlane.getScale();
            //System.out.println("getting computePlane Scale: " + s.x);
            return s.x;
        }
        Transform3D c2f = new Transform3D(f2c);
        c2f.invert();
        return c2f.getScale();
    }

    public void getScale(Vector3d scale) {
        if (computePlane != null) {
            scale.set(computePlane.getScale());
            //System.out.println("getting computePlane Scale: " + scale);
        }
        Transform3D c2f = new Transform3D(f2c);
        c2f.invert();
        c2f.getScale(scale);
    }

    public Vector3d getCenter() {
        if (computePlane != null) {
            //System.out.println("getting computePlaneCenter: " + computePlane.getCenter());
            return computePlane.getCenter();

        }
        Transform3D c2f = new Transform3D(f2c);
        c2f.invert();

        double x = width / 2.0;
        double y = height / 2.0;
        Point3d vec = new Point3d(x, y, 0.);
        c2f.transform(vec);
        return new Vector3d(vec);

    }

    public void setColor(Color color) {
        mColor = color;
    }

    public void prepareIDraw() {
        if (prepared) return;

        input = new ScalarImage(width, height);
        input.setRandom();
        canvas = new AccumImage(width, height);
        Matrix4d mat = new Matrix4d();
        f2c.get(mat);
        if (dlic != null) dlic.dispose();
        dlic = new DLIC(input, canvas, field, mat);
        dlic.setMinCoverage(1.);
        dlic.setMaxCoverage(4.);
        dlic.setDefaultStreamLen();
        dlic.setDefaultStepSize();
        prepared = true;
    }

    private void generateDLIC(){
    	if (field == null) return;

        fireProgressEvent(new ProgressEvent(this, ProgressEvent.START, 0));
        if (!prepared) prepareIDraw();
        // WHAT IS THIS
        prepared = false;
       
        if (!progressListeners.isEmpty()) {
            dlic.addProgressEventListener(this);
        }
        dlic.generateImage();
        TDebug.println(1, "DLIC time = " + (System.currentTimeMillis() - millis));
        if (!progressListeners.isEmpty()) {
            dlic.removeProgressEventListener(this);
        }
        if (dlic.forcedStop == true) return;
        
        output = new ScalarImage(width,height);
        
//        RGBImage test1 = new RGBImage(output.width,output.height);
//        test1.fromScalarImage(output,128.,128.,128.);
//        ImageIO.WriteTIFF(test1, "test2_raw.tif");

        double kernel[] = { 0, .0425, .0825, .0425, .0825, 0.5, .0825, .0425, .0825, .0425 };
        double brightness = 0.0;
        double contrast = 0.5;

        for (int i = 0; i < width*height; i++){
         
                double x = canvas.get(i);
                double y = (x + contrast * (x - 0.5)) * (1 + brightness);
                y = y > 1. ? 1. - Double.MIN_VALUE : y;
                y = y < 0. ? 0. : y;
                output.set(i, y);
            }
//        RGBImage test2 = new RGBImage(output.width,output.height);
//        test2.fromScalarImage(output,128.,128.,128.);
//        ImageIO.WriteTIFF(test2, "test2_contrast.tif");

        /*	
         Remove the comments to obtain higher quality in the expense of additional
         computing time, due to the application of the method twice, by using the
         output of the first application as the input to the second one.
         */
        /*	 
         input.copy( canvas );
         dlic.generateImage();
         
         for( int i = 0; i < width ; i++ )
         for( int j = 0; j < height ; j++ ) {
         double x = canvas.get( i, j );
         double y = x + contrast * ( x - 0.5 );
         canvas.set( i, j, y);
         }
         
         */
        // output.convolve3x3(kernel);
        
//        RGBImage test3 = new RGBImage(output.width,output.height);
//        test3.fromScalarImage(output,128.,128.,128.);
//        ImageIO.WriteTIFF(test3, "test2_convolve.tif");
        
        
        
    }
    public void computeFieldImage() {
        TDebug.println(2, "DlicGenerator - starting computeFieldImage");
        millis = System.currentTimeMillis();
        generateDLIC();
        
        if (mImage != null) {
            mImage.flush();
            mImage = null;
        }
        //output.normalize();
//        RGBImage test4 = new RGBImage(output.width,output.height);
//        test4.fromScalarImage(output,128.,128.,128.);
//        ImageIO.WriteTIFF(test4, "test2_normalize.tif");
        
        
        RGBImage rgbImage = null;
        switch(colorMode){
        case Teal.ColorMode_GRAY:
        	mImage = output.getBufferedImage(BufferedImage.TYPE_BYTE_INDEXED, Color.GRAY);
        	break;
        case Teal.ColorMode_COLOR:
        	mImage = output.getBufferedImage(BufferedImage.TYPE_BYTE_INDEXED, mColor);
        	break;
        case Teal.ColorMode_MAGNITUDE:
        case Teal.ColorMode_BRIGHTEN:
        	long nowMS = System.currentTimeMillis();
        	if(false){
        	ScalarImage magImage = generateMagnitudeImage(1.0);
        	
        		Tuple4f dynamics = magImage.getDynamics();
        	
        		TDebug.println(1,"Min: " + dynamics.w + " Max: " + dynamics.x 
        			+ " Average: " + dynamics.y + " Mean: " + dynamics.z);
        		TDebug.println(1,"Calc mag dynamics time: " + (System.currentTimeMillis() - nowMS));
        		((Colorizer)colorizer).setSaturationPoint((double)dynamics.z);
        		((Colorizer)colorizer).setFallOff(dynamics.y/3.);
        	
//        		RGBImage test5 = new RGBImage(output.width,output.height);
//        		test5.fromScalarImage(magImage,255,255.,255.);
//        		ImageIO.WriteTIFF(test5, "test2_MAG.tif");
//           
//        		magImage.normalize();
//        		test5 = new RGBImage(output.width,output.height);
//        		test5.fromScalarImage(magImage,255,255.,255.);
//        		ImageIO.WriteTIFF(test5, "test2_MAG_NORMAL.tif");
           
           
        		//float[] hsv = Color.RGBtoHSB(mColor.getRed(), mColor.getGreen(),mColor.getBlue(), null);
        		//
 
        	}
        	Matrix4d m = new Matrix4d();
            f2c.get(m);
            TDebug.println(1,"saturation: " + ((Colorizer)colorizer).getSaturationPoint() 
            		+ " fallOff: " + ((Colorizer)colorizer).getFallOff());
            ((Colorizer)colorizer).setBrighten(colorMode == Teal.ColorMode_BRIGHTEN);
            rgbImage = new RGBImage(output.width,output.height);
            rgbImage.fromScalarImageMagnitude(output, m, field, colorizer);
            		
            mImage = rgbImage.getBufferedImage();
        	break;
        
        
        default:
        	break;
        }
        
        //ImageIO.WriteTIFF(rgbImage, "test2_Final.tif");
        
        //mImage = canvas.getBufferedImage(BufferedImage.TYPE_3BYTE_BGR,mColor);
        //TDebug.println(0, "DLIC time = " + (System.currentTimeMillis() - millis));
        isValid = true;
        TDebug.println(1, "computeFieldImage time = " + (System.currentTimeMillis() - millis));
        fireImageEvent(new ImageStatusEvent(this, ImageStatusEvent.COMPLETE));
        fireProgressEvent(new ProgressEvent(this, ProgressEvent.COMPLETE, 100));
        
        TDebug.println(1, "end computeFieldImage");
    }

    
    private ScalarImage generateMagImage(){
    	ScalarImage img = new ScalarImage(width, height);
        
    	Matrix4d imageToField = new Matrix4d();
    	f2c.get(imageToField);
        imageToField.invert();
       
        Vector3d location = new Vector3d();
        Vector3d value = new Vector3d();
        Vector3d color = new Vector3d();
        Point3d pt = new Point3d();
        float f = 0.f;
        for (int j = 0, k = 0; j<height; ++j){
          for (int i = 0; i<width; ++i, ++k) {
            pt.x = i + 0.5;
            pt.y = j + 0.5;
            pt.z = 0;
            imageToField.transform(pt);
            location.set(pt);
            field.get(location,value);
            double r = Math.sqrt(value.x*value.x + value.y*value.y);
            //r = Math.pow(r,1.5);
            if (r!=0.0)
              r = 1.0/Math.sqrt(r);
            //if (r == 0.) r = MaxZeroLevel;  // this sets the color level you want for zero field strength
            r /= 2000.;
            r = r*26.0;
            
            //f = (float) field.get(location).length();
            img.set(i, j, (float) r);
          }
        }
        return img;
    }
    
    private ScalarImage generateMagnitudeImage(double norm) {
		ScalarImage img = new ScalarImage(width, height);
		Transform3D c2f = new Transform3D();
		c2f.invert(f2c);
		Vector3d vec = new Vector3d();
		Vector3d value = new Vector3d();
		double r = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				vec.set((double) i, (double) j, 0.);
				c2f.transform(vec);
				field.get(vec, value);
				if (Double.isNaN(value.x) || Double.isInfinite(value.x)
						|| Double.isNaN(value.y) || Double.isInfinite(value.y)) {
					r = 0.0;
				} else {
					r = Math.sqrt(value.x * value.x + value.y * value.y);
					// r = Math.pow(r,1.5);
					if (r != 0.0) {
						r = 1.0 / Math.sqrt(r);
						// if (r == 0.)
						// r = MaxZeroLevel; // this sets the color level you
						// want for zero field strength
						r /= 2000.;
						r = r * 26.0;
					}
				}
				img.set(i, j, (float) Math.abs(r));
			}
		}
		return img;
	}
    private ScalarImage generateMagImage(double norm){
    	ScalarImage img = new ScalarImage(width, height);
        Transform3D c2f = new Transform3D();
        c2f.invert(f2c);
        double f;
        Vector3d vec = new Vector3d();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                vec.set((double) i, (double) j, 0.);
                c2f.transform(vec);
                f = field.get(vec).length();
                img.set(i, j, f / norm);
            }
        }
        return img;
    }

    public void computeMagImage() {

        computeMagImage(field.get(new Vector3d(100., 100., 0.)).length());
    }
    
    public void computeMagImage(double norm) {
        if (field == null) return;

        fireProgressEvent(new ProgressEvent(this, ProgressEvent.START, 0));
        millis = System.currentTimeMillis();
        ScalarImage img = generateMagImage(norm);
        mImage = img.getBufferedImage(BufferedImage.TYPE_BYTE_INDEXED, mColor);

        isValid = true;
        TDebug.println(0, "computeMagImage time = " + (System.currentTimeMillis() - millis));
        fireImageEvent(new ImageStatusEvent(this, ImageStatusEvent.COMPLETE));
        fireProgressEvent(new ProgressEvent(this, ProgressEvent.COMPLETE, 100));

    }

    public void computeFluxImage() {
        if (field == null) return;

        fireProgressEvent(new ProgressEvent(this, ProgressEvent.START, 0));
        millis = System.currentTimeMillis();
        ScalarImage img = new ScalarImage(width, height);

        Transform3D c2f = new Transform3D(f2c);
        c2f.invert();
        double f;
        Point3d pt = new Point3d();
        Vector3d vec = new Vector3d();
        c2f.transform(pt);
        System.out.println(pt);
        for (int i = 1; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pt.set((double) i, (double) j, 0.);
                c2f.transform(pt);
                vec.set(pt);
                f = ((Field) field).getFlux(vec);
                //System.out.print(f + ", ");
                if (Double.isNaN(f) || Double.isInfinite(f)) {
                    TDebug.println(0,"NaN at: " + i + ", " + j);
                    img.set(i, j, 0.);
                } else {

                    double v = Math.log(Math.abs(f));
                    if (f < 0) v = -v;
                    img.set(i, j, v);

                    //img.set( i, j, f);
                }
            }
            //System.out.println();
        }
        //img.clamp(-100.,100.);
        img.normalize();
        mImage = img.getBufferedImage(BufferedImage.TYPE_3BYTE_BGR,mColor);
        //mImage = img.getBufferedImage(BufferedImage.TYPE_BYTE_INDEXED, mColor);
        isValid = true;
        TDebug.println(1, "computeFluxImage time = " + (System.currentTimeMillis() - millis));
        fireImageEvent(new ImageStatusEvent(this, ImageStatusEvent.COMPLETE));
        fireProgressEvent(new ProgressEvent(this, ProgressEvent.COMPLETE, 100));
    }

    public void computePotentialImage() {
        if (field == null) return;

        fireProgressEvent(new ProgressEvent(this, ProgressEvent.START, 0));
        millis = System.currentTimeMillis();
        ScalarImage img = new ScalarImage(width, height);
        Transform3D c2f = new Transform3D(f2c);
        c2f.invert();
        double f;
        Point3d pt = new Point3d();
        Vector3d vec = new Vector3d();
        c2f.transform(pt);
        System.out.println(pt);
        for (int i = 1; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pt.set((double) i, (double) j, 0.);
                c2f.transform(pt);
                vec.set(pt);
                f = ((Field) field).getPotential(vec);
                if (Double.isNaN(f) || Double.isInfinite(f)) {
                    System.out.println("NaN at: " + i + ", " + j);
                    img.set(i, j, 0.);
                } else {
                    img.set(i, j, f);
                }
            }
        }
        img.normalize();

        mImage = img.getBufferedImage(ScalarImage.TYPE_BYTE_RANDOM, Color.GRAY);
        //mImage = img.getBufferedImage(BufferedImage.TYPE_BYTE_INDEXED, mColor);
        isValid = true;
        TDebug.println(1, "computePotentialImage time = " + (System.currentTimeMillis() - millis));
        fireImageEvent(new ImageStatusEvent(this, ImageStatusEvent.COMPLETE));
        fireProgressEvent(new ProgressEvent(this, ProgressEvent.COMPLETE, 100));
    }

    public void computeColorMappedFluxImage() {
        if (field == null) return;

        fireProgressEvent(new ProgressEvent(this, ProgressEvent.START, 0));
        millis = System.currentTimeMillis();
        ScalarImage img = new ScalarImage(width, height);
        Transform3D c2f = new Transform3D(f2c);
        c2f.invert();
        double f;
        Point3d pt = new Point3d();
        Vector3d vec = new Vector3d();
        c2f.transform(pt);
        TDebug.println(3,pt);
        for (int i = 1; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pt.set((double) i, (double) j, 0.);
                c2f.transform(pt);
                vec.set(pt);
                f = ((Field) field).getFlux(vec);
                if (Double.isNaN(f) || Double.isInfinite(f)) {
                	TDebug.println(0,"NaN at: " + i + ", " + j);
                    img.set(i, j, 0.);
                } else {
                    double v = Math.log(Math.abs(f));
                    if (f < 0) v = -v;
                    //img.set(i, j, v);
                    img.set(i, j, f);
                }
            }
            // System.out.println();
        }
        //img.clamp(-16, 8.);
        //img.normalize();

        img.analyze(true);
        img.analyze(true);

        /*		
         for (int i = 1; i < width; i++) {
         for (int j = 0; j < height; j++) {
         f=img.get(i,j);
         double v = Math.log(f);
         img.set(i, j, v);
         }
         // System.out.println();
         }
         img.normalize();
         */

        //mImage = img.getBufferedImage(BufferedImage.TYPE_3BYTE_BGR,mColor);
        mImage = img.getBufferedImage(BufferedImage.TYPE_BYTE_INDEXED, mColor);
        isValid = true;
        TDebug.println(1, "computeColorMappedFluxImage time = " + (System.currentTimeMillis() - millis));
        fireImageEvent(new ImageStatusEvent(this, ImageStatusEvent.COMPLETE));
        fireProgressEvent(new ProgressEvent(this, ProgressEvent.COMPLETE, 100));
    }

    protected void fireImageEvent(ImageStatusEvent ise) {
        Iterator it = imageListeners.iterator();
        while (it.hasNext()) {
            ((ImageStatusListener) it.next()).imageStatus(ise);
        }
    }

    public void addProgressEventListener(ProgressEventListener listener) {
        progressListeners.add(listener);
    }

    public void removeProgressEventListener(ProgressEventListener listener) {
        progressListeners.remove(listener);
    }

    public synchronized void addImageStatusListener(ImageStatusListener lis) {
        imageListeners.add(lis);
    }

    public synchronized void removeImageStatusListener(ImageStatusListener lis) {
        imageListeners.remove(lis);
    }

    public synchronized void setProgress(ProgressEvent event) {
        if (!progressListeners.isEmpty()) {
            ProgressEvent ev = new ProgressEvent(this, event.getStatus(), event.getPercent());
            fireProgressEvent(ev);
        }
    }

    protected void fireProgressEvent(ProgressEvent ev) {
        Iterator it = progressListeners.iterator();
        while (it.hasNext()) {
            ((ProgressEventListener) it.next()).setProgress(ev);
        }
        Thread.yield();
    }
    
    
    private void writeObject(java.io.ObjectOutputStream s)
    throws java.io.IOException{
    	s.defaultWriteObject();

    	if(f2c == null) {
    		//indicate that transform is null;
    		s.writeBoolean(false);
    		return;
    	}
    	s.writeBoolean(true);
    	
    	double [] transform = new double[16];
    	f2c.get(transform);

    	// Write out all elements in the proper order.
    	for (int i=0; i<16; i++)
                s.writeDouble(transform[i]);
    }
    
    private void readObject(java.io.ObjectInputStream s)
    throws java.io.IOException, ClassNotFoundException {
    	// Read in size, and any hidden stuff
    	s.defaultReadObject();

    	//check if transform was null;
    	if(s.readBoolean() == false)
    		return;

    	double [] transform = new double[16];    	
    	
    	// Read in all elements in the proper order.
    	for (int i=0; i<16; i++)
    		transform[i] = s.readDouble();
    	this.f2c = new Transform3D(transform);
    }

}
