/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: DLIC.java,v 1.10 2009/04/24 19:35:58 pbailey Exp $
 * 
 */

package teal.visualization.dlic;

import java.awt.*;
import java.util.*;

import javax.vecmath.*;

import teal.field.*;
import teal.math.*;
import teal.util.*;
import teal.visualization.dlic.field.*;
import teal.visualization.image.*;

/**
 * Provides the engine for generating a DLIC data buffer, Matrix specification and calculation
 * are currently outside of the scope of this class.
 *
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.10 $ 
 */

public class DLIC extends Thread {
    
    /** Bit flags used to define DLIC visualization modes */
    public final static int DLIC_FLAG_E = 0x01;
	public final static int DLIC_FLAG_B = 0x02;
	public final static int DLIC_FLAG_G = 0x04;
	public final static int DLIC_FLAG_P = 0x08;
	public final static int DLIC_FLAG_EP = 0x10;
	public final static int DLIC_FLAG_BP = 0x20;
	public final static int DLIC_FLAG_EF = 0x40;
	public final static int DLIC_FLAG_BF = 0x80;
	
	  /** Action command to trigger an IDraw B field calculation */
    public final static String DLIC_B = "DLIC_B";
    /** Action command to trigger an IDraw E field calculation */
    public final static String DLIC_E = "DLIC_E";
    /** Action command to trigger an IDraw EP calculation */
    public final static String DLIC_BP = "DLIC_BP";
    public final static String DLIC_EP = "DLIC_EP";
    public final static String DLIC_EF = "DLIC_EF";
    public final static String DLIC_BF = "DLIC_BF";
    public final static String DLIC_ECMF = "DLIC_ECMF";
    public final static String DLIC_BCMF = "DLIC_BCMF";

    protected Vector<ProgressEventListener> progressListeners = null;
    public boolean forcedStop = false;

    protected Dimension size;
    private ScalarImage input;
    private AccumImage canvas;
    private Vector3dField field;
    private double cstreamlen, cstepsize, mincoverage, maxcoverage;
    private double cmaxlen;
    private VectorIterator iterator;
    private boolean clear, normalize;
    private Vector<Vector3d> singularities;

    private Matrix4d viewerToWorld;
    private Matrix4d worldToCanvas;

    // these are not in IDRAW at this point  
    private double fstreamlen, fstepsize, fcurlen;
    private Streamline fstream, bstream;
    private int sampleoffset;
    private Vector3d[] samplev;
    private double[] sample;

    /**
     Integrate and Draw algorithm using a given input
     table, which can either be a random noise or some image to be manipulated.
     IDraw parameters can be modified before calling the computation method that
     performs the actual algorithm steps. Some of these parameters are as follows:
     specifying the input table, the vector field and the canvas, independently
     changing the mapping between the vector field and both the input table and
     the canvas, changing the streamline length, sampling step, adjusting the step
     limits of the integration method, the minimum and maximum coverage values of
     each canvas pixel, and choosing an iterator which will provide the order in
     which pixels are picked for contribution. Most of these paramters are set to
     default values which provide safe and reasonable results. These should only
     be changed whenever the outcome is not satisfactory.
     **/
    public DLIC(ScalarImage input, AccumImage canvas, teal.field.Vector3dField field, Matrix4d projection) {
        //imageListeners = new Vector();
        progressListeners = new Vector<ProgressEventListener>();
        setInput(input);
        setOutput(canvas);
        size = new Dimension(canvas.width, canvas.height);
        setField(field);

        viewerToWorld = new Matrix4d();
        worldToCanvas = new Matrix4d();
        setWorldToCanvas(projection);

        setDefaultMinCoverage();
        setDefaultMaxCoverage();
        setDefaultIterator();
        setDefaultClear();
        setDefaultNormalize();
        clearSingularities();

        setDefaultStreamLen();
        setDefaultStepSize();
    }

    public DLIC() {
        size = new Dimension(512, 512);
        viewerToWorld = new Matrix4d();
        worldToCanvas = new Matrix4d();

        iterator = null;
        setInput(null);
        setOutput(null);
        setField(null);
        setDefaultMinCoverage();
        setDefaultMaxCoverage();
        setDefaultClear();
        setDefaultNormalize();
        clearSingularities();
    }

    public void dispose() {
        canvas = null;
        input = null;
        removeAllListeners();
    }

    public void setWorldToCanvas(Matrix4d projection) {
        worldToCanvas.set(projection);
        viewerToWorld.set(worldToCanvas);
        viewerToWorld.invert();
        setDefaultStreamLen();
        setDefaultStepSize();
    }

    protected void checkParameters() {
        if ((canvas == null) || (canvas.width != size.width) || (canvas.height != size.height)) {
            setOutput(new AccumImage(size.width, size.height));
        }
        if ((input == null) || (input.width < size.width) || (input.height < size.height)) {
            setInput(new ScalarImage(size.width, size.height, new Random()));
        }
    }

    public void setSize(Dimension siz) {
        size = siz;
        checkParameters();
    }

    public void setInput(ScalarImage input) {

        this.input = input;
    }

    public void setOutput(AccumImage canvas) {
        this.canvas = canvas;
        if (canvas != null) {
            setDefaultStreamLen();
            setDefaultIterator();
        }
    }

    public void setField(Vector3dField field) {
        this.field = field;
    }

    public void setDefaultStreamLen() {
        double d = 0;
        if (viewerToWorld != null) {
            Point3d pt = new Point3d();
            Point3d pt1 = new Point3d();
            Point3d pt2 = new Point3d();
            viewerToWorld.transform(pt, pt1);
            pt.x = canvas.width;
            pt.y = canvas.height;
            viewerToWorld.transform(pt, pt2);
            d = Math.max(pt2.x - pt1.x, pt2.y - pt1.y);

        } else {
            d = (canvas.width > canvas.height) ? canvas.width / 8. : canvas.height / 8.;
        }
        TDebug.println(3, "Default streamLen: " + d);
        setStreamLen(d);
    }

    public void setStreamLen(double cstreamlen) {
        this.cstreamlen = Math.abs(cstreamlen);
    }

    public void setDefaultStepSize() {

        double d = 2.;
        if (viewerToWorld != null) {
            Point3d pt = new Point3d();
            Point3d pt1 = new Point3d();
            Point3d pt2 = new Point3d();
            viewerToWorld.transform(pt, pt1);
            pt.x = 2.;
            pt.y = 2.;
            viewerToWorld.transform(pt, pt2);
            d = Math.max(pt2.x - pt1.x, pt2.y - pt1.y);

        }
        TDebug.println(3, "Default stepSize: " + d);
        setStepSize(d);
    }

    public void setStepSize(double cstepsize) {
        this.cstepsize = Math.abs(cstepsize);
    }

    public void setDefaultMinCoverage() {
        setMinCoverage(1.0);
    }

    public void setMinCoverage(double mincoverage) {
        this.mincoverage = mincoverage;
    }

    public void setDefaultMaxCoverage() {
        setMaxCoverage(3.0);
    }

    public void setMaxCoverage(double maxcoverage) {
        this.maxcoverage = maxcoverage;
    }

    public void setDefaultIterator() {
        iterator = new RandomGridIterator_Simplified(canvas.width, canvas.height, null);
    }

    public void setDefaultIterator(Random random) {
        iterator = new RandomGridIterator_Simplified(canvas.width, canvas.height, random);
    }

    public void setIterator(VectorIterator iterator) {
        this.iterator = iterator;
    }

    public void setDefaultNormalize() {
        setNormalize(true);
    }

    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }

    public void setDefaultClear() {
        setClear(true);
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }

    public void clearSingularities() {
        singularities = new Vector<Vector3d>();
    }

    public void addSingularity(Vector3d v) {
        singularities.add(v);
    }

    public void generateImage() {
        checkParameters();
        run();
    }

    public Vector3d getPlaneNormal() {
        // Forward and backward affine transforms.
        Matrix4d T = worldToCanvas;
        Matrix4d T_ = new Matrix4d(worldToCanvas);
        T_.invert();

        // Original and normal, back and forth.
        Vector3d o_ = new Vector3d(0., 0., 0.);
        Vector3d n_ = new Vector3d(0., 0., 1.);
        Vector3d o = new Vector3d();
        Vector3d n = new Vector3d();
        T_.transform(o_, o);
        T_.transform(n_, n);
        n.sub(o);
        n.normalize();

        return n;
    }

    public void run() {

        int percentCoverage = 0;
        int lastPrintCoverage = -1;
        int minPercentReport = 5;
        int pointnum = 0;

        //fireImageStatusEvent( new ImageStatusEvent(this, ImageStatusEvent.START ) );
        fireProgressEvent(new ProgressEvent(this, ProgressEvent.START));

        if (iterator == null) setDefaultIterator();
        if (clear == true) canvas.clear();

        double scale = viewerToWorld.getScale();
        TDebug.println(2, "viewerToWorld: scale" + scale);
        Point3d tst0 = new Point3d();
        Point3d tst1 = new Point3d();
        Point3d tst2 = new Point3d();
        viewerToWorld.transform(tst0, tst1);
        TDebug.println(2, "Starting run\nviewerToWorld: " + tst0 + " -> " + tst1);
        tst0.x = size.width;
        tst0.y = size.height;
        viewerToWorld.transform(tst0, tst2);
        TDebug.println(2, "viewerToWorld: " + tst0 + " -> " + tst2);
        scale = Math.max(tst2.x - tst1.x, tst2.y - tst1.y);
        forcedStop = scale < 1e-11;

        fstreamlen = Math.abs(scale);
        //fstreamlen = scale * cstreamlen;
        //fstepsize = scale * cstepsize;
        //fstreamlen = 16.0;
        fstepsize = fstreamlen / (double) size.width;
        //TDebug.println(1,"range divided by width = " + fstreamlen/(double)size.width);
        //fstreamlen = cstreamlen;
        //fstepsize = cstepsize;
        TDebug.println(1, "StreamLen: " + fstreamlen + "  stepSize: " + fstepsize);

        fstream = new Streamline(field, fstepsize, fstepsize / 10., fstepsize * 10., scale * 1e-4);
        bstream = new Streamline(field, fstepsize, fstepsize / 10., fstepsize * 10., scale * 1e-4);
        fstream.setPlaneNormal(getPlaneNormal());
        bstream.setPlaneNormal(getPlaneNormal());

        fstream.setLength(fstreamlen / 2.);
        //TDebug.println(1,"DLIC StreamLength: " +  fstreamlen / 2.);
        bstream.setLength(-fstreamlen / 2.);
        Enumeration<Vector3d> enm = singularities.elements();

        while (enm.hasMoreElements()) {
            Vector3d v = (Vector3d) enm.nextElement();
            fstream.addSingularity(v);
            bstream.addSingularity(v);
        }

        canvas.minalpha = mincoverage;
        canvas.maxalpha = maxcoverage;

        Point3d point = new Point3d();

        while (percentCoverage < 95 && !forcedStop) {

            point.set(iterator.nextVec());
            int i = (int) point.x;
            int j = (int) point.y;

            if (canvas.getAlpha(i, j) < mincoverage) {

                pointnum++;
                blossomC(point, input.get(i, j));
                percentCoverage = (100 * canvas.coverage) / canvas.size;
                if ((percentCoverage - lastPrintCoverage) >= minPercentReport) {
                    lastPrintCoverage = percentCoverage;

                    fireProgressEvent(new ProgressEvent(this, ProgressEvent.PROGRESS, percentCoverage ));
                    TDebug.println(1, "DLIC progress: " + percentCoverage  + " %");
                    yield();

                }
            }
            yield();
        }
        percentCoverage = 0;
        lastPrintCoverage = -1;
        double alpha;
        for (int i = 0; i < canvas.width && !forcedStop; i++) {
            for (int j = 0; j < canvas.height && !forcedStop; j++) {

                alpha = canvas.getAlpha(i, j);
                if (alpha < mincoverage) {
                    point.set(i, j, 0.);
                    blossom(point, (alpha < 1e-15) ? input.get(i, j) : canvas.get(i, j));
                    percentCoverage = (100 * canvas.coverage) / canvas.size;
                    if ((percentCoverage - lastPrintCoverage) >= minPercentReport) {
                        lastPrintCoverage = percentCoverage;
                        fireProgressEvent(new ProgressEvent(this, ProgressEvent.PROGRESS, percentCoverage / 2 + 50));

                    }
                }
                yield();
            }
        }

        if (normalize == true) canvas.normalize();
        if (!forcedStop) {
            //fireImageStatusEvent( new ImageStatusEvent(this, ImageStatusEvent.COMPLETE ) );
            fireProgressEvent(new ProgressEvent(this, ProgressEvent.COMPLETE, 100));
        } else {
            //fireImageStatusEvent( new ImageStatusEvent( this,ImageStatusEvent.END_FORCED ) );
            fireProgressEvent(new ProgressEvent(this, ProgressEvent.INTERRUPT, percentCoverage));
        }
    }

    /**
     *  Conditional dccumulation of line points. Traces 2 streamlines from a point specified in canvas 
     * coordinates and copies the line into the canvas.
     **/
    public void blossomC(Point3d cPoint, double shade) {
        yield();
        if (!canvas.inBounds(cPoint.x, cPoint.y)) return;

        Vector3d tmp = null;
        Point3d streamPoint = new Point3d();
        Point3d streamCenter = new Point3d();
        Point3d dPoint = new Point3d();
        viewerToWorld.transform(cPoint, streamCenter);
        fstream.setStart(streamCenter);
        bstream.setStart(streamCenter);
        //TDebug.println(2,"Canvas: " + cPoint +"  \tWorld: " + streamCenter);
        Vector3d oldPoint;
        try {
            oldPoint = null;
            //while( fstream.hasNext() )
            while ((tmp = fstream.nextVec()) != null) {

                streamPoint.set(tmp);
                worldToCanvas.transform(streamPoint, dPoint);
                if (oldPoint == null) oldPoint = new Vector3d(dPoint);
                if (!canvas.inBounds(dPoint.x, dPoint.y)
                    || canvas.getAlpha((int) dPoint.x, (int) dPoint.y) > maxcoverage) break;
                canvas.drawLineC(dPoint.x, dPoint.y, oldPoint.x, oldPoint.y, shade);
                oldPoint.set(dPoint);
            }

            oldPoint = null;
            //while(bstream.hasNext() )
            while ((tmp = bstream.nextVec()) != null) {

                streamPoint.set(tmp);
                worldToCanvas.transform(streamPoint, dPoint);
                if (oldPoint == null) oldPoint = new Vector3d(dPoint);
                if (!canvas.inBounds(dPoint.x, dPoint.y)
                    || canvas.getAlpha((int) dPoint.x, (int) dPoint.y) > maxcoverage) break;
                canvas.drawLineC(dPoint.x, dPoint.y, oldPoint.x, oldPoint.y, shade);
                oldPoint.set(dPoint);
            }

        } catch (Exception e) {
            System.out.println("Exception caught, in the following state: ");
            System.out.println("streamCenter = (" + streamCenter.x + "," + streamCenter.y + ")");
            System.out.println("streamPoint = (" + streamPoint.x + "," + streamPoint.y + ")");
            System.out.println(e.getMessage());
        }
    }

    /**
     Unconditional dccumulation of line points.
     **/
    public void blossom(Point3d cPoint, double shade) {
        yield();
        if (!canvas.inBounds(cPoint.x, cPoint.y)) return;
        Vector3d tmp = null;
        Point3d streamPoint = new Point3d();
        Point3d streamCenter = new Point3d();
        Point3d dPoint = new Point3d();
        viewerToWorld.transform(cPoint, streamCenter);
        fstream.setStart(streamCenter);
        bstream.setStart(streamCenter);
        Point3d oldPoint;
        try {
            oldPoint = null;
            //while(fstream.hasNext()  )
            while ((tmp = fstream.nextVec()) != null) {

                //if( (tmp = fstream.nextVec()) == null) break;
                streamPoint.set(tmp);
                //canvasPoint = f2c.v( streamPoint );
                worldToCanvas.transform(streamPoint, dPoint);
                if (oldPoint == null) oldPoint = new Point3d(dPoint);
                if (!canvas.inBounds(dPoint.x, dPoint.y)
                    || canvas.getAlpha((int) dPoint.x, (int) dPoint.y) > maxcoverage) break;
                canvas.drawLine(dPoint.x, dPoint.y, oldPoint.x, oldPoint.y, shade);
                oldPoint.set(dPoint);
            }
            oldPoint = null;

            //while( bstream.hasNext() )
            while ((tmp = bstream.nextVec()) != null) {

                //if(( tmp = bstream.nextVec()) == null) break;
                streamPoint.set(tmp);
                //canvasPoint = f2c.v( streamPoint );
                worldToCanvas.transform(streamPoint, dPoint);
                if (oldPoint == null) oldPoint = new Point3d(dPoint);
                if (!canvas.inBounds(dPoint.x, dPoint.y)
                    || canvas.getAlpha((int) dPoint.x, (int) dPoint.y) > maxcoverage) break;
                canvas.drawLine(dPoint.x, dPoint.y, oldPoint.x, oldPoint.y, shade);
                oldPoint.set(dPoint);
            }
        } catch (Exception e) {
            System.out.println("Exception caught, in the following state: ");
            System.out.println("streamCenter = (" + streamCenter.x + "," + streamCenter.y + ")");
            System.out.println("streamPoint = (" + streamPoint.x + "," + streamPoint.y + ")");
            System.out.println(e.getMessage());
        }
    }

    /**    
     * This version only plots single points. I used it for a long time, but then I found
     * out that joining points by lines, even if these points are made very close such that
     * drawing lines may seem unsignificant, provides higher contrast and faster overall
     *  execution of the algorithm, due to faster coverage of the canvas.
     */
    public void blossomPt(Point3d canvasPoint, double shade) {
        if (!canvas.inBounds(canvasPoint.x, canvasPoint.y)) return;
        Point3d streamPoint = new Point3d();
        Point3d streamCenter = new Point3d();
        Vector3d tmp = new Vector3d();
        viewerToWorld.transform(canvasPoint, streamCenter);
        fstream.setStart(streamCenter);
        bstream.setStart(streamCenter);
        try {
            while ((tmp = fstream.nextVec()) != null) {
                streamPoint.set(tmp);
                worldToCanvas.transform(streamPoint, canvasPoint);
                if (!canvas.inBounds(canvasPoint.x, canvasPoint.y)
                    || canvas.getAlpha((int) canvasPoint.x, (int) canvasPoint.y) > maxcoverage) break;
                canvas.accumulateBilinearC(canvasPoint.x, canvasPoint.y, shade, 1.);
            }
            while ((tmp = bstream.nextVec()) != null) {
                streamPoint.set(tmp);
                worldToCanvas.transform(streamPoint, canvasPoint);
                if (!canvas.inBounds(canvasPoint.x, canvasPoint.y)
                    || canvas.getAlpha((int) canvasPoint.x, (int) canvasPoint.y) > maxcoverage) break;
                canvas.accumulateBilinearC(canvasPoint.x, canvasPoint.y, shade, 1.);
            }

        } catch (Exception e) {
            TDebug.println(0,"Exception caught, in the following state: ");
            TDebug.println(0,"streamCenter = (" + streamCenter.x + "," + streamCenter.y + ")");
            TDebug.println(0,"streamPoint = (" + streamPoint.x + "," + streamPoint.y + ")");
            TDebug.println(0,e.getMessage());
        }

    }

    /*
     public double fillShade( Vector2d canvasPoint )
     {
     if( !canvas.inBounds( canvasPoint ) ) return 0.;
     Vector2d streamPoint = new Vector2d();
     Vector2d streamCenter = c2f.v( canvasPoint );
     fstream.Start( streamCenter );
     bstream.Start( streamCenter );
     streamCenter = canvasPoint;
     double x = 0.;
     double a = 0.;
     try {
     while( ( streamPoint = fstream.Next() ) != null )
     {
     canvasPoint = f2c.v( streamPoint );
     if( !canvas.inBounds( canvasPoint ) ) break;
     x += canvas.get( (int) canvasPoint.x, (int) canvasPoint.y );
     a += canvas.getAlpha( (int) canvasPoint.x, (int) canvasPoint.y );
     }
     while( ( streamPoint = bstream.Next() ) != null )
     {
     canvasPoint = f2c.v( streamPoint );
     if( !canvas.inBounds( canvasPoint ) ) break;
     x += canvas.get( (int) canvasPoint.x, (int) canvasPoint.y );
     a += canvas.getAlpha( (int) canvasPoint.x, (int) canvasPoint.y );
     }
     } catch ( Exception e ) {
     System.out.println( "Exception caught, in the following state: " );
     System.out.println( "streamCenter = (" + streamCenter.x + "," + streamCenter.y + ")" );
     System.out.println( "streamPoint = (" + streamPoint.x + "," + streamPoint.y + ")" );
     System.out.println( e.getMessage() );
     }
     return a > mincoverage ? x / a : Math.random();
     }
     */

    /**

     IDraw implements a set of methods which allow the implementation of an ImageStatus event
     mechanism. Internally, events are fired using the fireIDrawEvent method which notifies
     each of the registered listeners, implementing the so-called ImageStatusListener interface,
     of the occurence of an IDrawEvent. Externally, potential listeners can be added or
     removed to the specific IDraw instance.
     
     Refer also to:
     @see teal.visualization.image.ImageStatusEvent
     @see teal.visualization.image.ImageStatusListener

     */

    public void removeAllListeners() {
        //imageListeners.clear();
        progressListeners.clear();
    }

    /*
     public void removeImageStatusListener( ImageStatusListener listener ) {
     imageListeners.remove( listener );
     }
     public void addImageStatusListener( ImageStatusListener listener ) {
     imageListeners.add( listener );
     }
     protected void fireImageStatusEvent( ImageStatusEvent e ) {
     Iterator it = imageListeners.iterator();
     while( it.hasNext() ) {
     ((ImageStatusListener) it.next()).imageStatus( e );
     }
     }
     */

    //////////
    public void addProgressEventListener(ProgressEventListener listener) {
        progressListeners.add(listener);
    }

    public void removeProgressEventListener(ProgressEventListener listener) {
        progressListeners.remove(listener);
    }

    protected void fireProgressEvent(ProgressEvent e) {
        Iterator<ProgressEventListener> it = progressListeners.iterator();

        while (it.hasNext()) {
            ((ProgressEventListener) it.next()).setProgress(e);

        }
        //yield();
    }

}
