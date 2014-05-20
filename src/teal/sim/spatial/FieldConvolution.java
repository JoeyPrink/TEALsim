/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: FieldConvolution.java,v 1.33 2010/05/26 17:05:19 stefan Exp $ 
 * 
 */

package teal.sim.spatial;

import java.awt.*;

import javax.media.j3d.Transform3D;

import teal.field.*;
import teal.math.RectangularPlane;
import teal.render.scene.*;
import teal.util.*;
import teal.visualization.dlic.*;
import teal.visualization.image.*;
import teal.visualization.processing.TColorizer;

public class FieldConvolution extends Spatial implements ImageGenerator {

    private static final long serialVersionUID = 3256728359773091639L;

    DLICGenerator mGen = null;
    boolean autoGenerate = false;

    public FieldConvolution() {    	
        super();
        mGen = new DLICGenerator();
        mGen.setSize(new Dimension(512,512));
        nodeType = NodeType.IMAGE;
    }

    public FieldConvolution(DLICGenerator gen) {
        super();
        mGen = gen;
    }

    public FieldConvolution(Vector3dField field) {
        super();
        mGen = new DLICGenerator(field);
    }

    public FieldConvolution(Vector3dField field, double xt, double yt, double xs, double ys) {
        super();
        mGen = new DLICGenerator(field, xt, yt, xs, ys);
    }

    protected TNode3D makeNode() {
//        ImageNode imgNode = new ImageNode();
    	TNode3D imgNode = SceneFactory.makeNode(this);
        imgNode.setPickable(false);
        imgNode.setVisible(false);
        mGen.addImageStatusListener((ImageStatusListener)imgNode);
        return imgNode;
    }

    public void setAutoGenerate(boolean state) {
        autoGenerate = state;
    }

    public boolean isAutoGenerate() {
        return autoGenerate;
    }
    
    

    public void setVisible(boolean b) {
        if (mNode != null) mNode.setVisible(b);
    }

    public boolean getVisible() {
        return mNode.isVisible();
    }
    
    public TColorizer getColorizer(){
    	return mGen.getColorizer();
    }
    
    public void setColorizer(TColorizer colorizer){
    	mGen.setColorizer(colorizer);
    }

    public int getColorMode() {
        return mGen.getColorMode();
    }

    public void setColorMode(int mode) {
        mGen.setColorMode(mode);
    }

    public void setComputePlane(RectangularPlane recPlane) {
        mGen.setComputePlane(recPlane);
    }

    public void nextSpatial() {
        //TDebug.println(0,"FieldConvolution: nextSpatial()");
        if (autoGenerate) mGen.generateImage();
        /* else
         if(mNode != null)
         mNode.setVisible(false); 
         */

    }

    public void addProgressEventListener(ProgressEventListener listener) {
        mGen.addProgressEventListener(listener);
    }

    public void removeProgressEventListener(ProgressEventListener listener) {
        mGen.removeProgressEventListener(listener);
    }

    public void addImageStatusListener(ImageStatusListener listener) {
        mGen.addImageStatusListener(listener);
    }

    public void removeImageStatusListener(ImageStatusListener listener) {
        mGen.removeImageStatusListener(listener);
    }

    public void setField(Vector3dField field) {
        mGen.setField(field);
    }

    public void generateImage() {
        mGen.generateImage();
    }
    public void generateFieldImage() {
        mGen.computeFieldImage();
    }
    public void generatePotentialImage() {
        mGen.computePotentialImage();
    }

    public void generateFluxImage() {
        mGen.computeFluxImage();
    }

    public void generateColorMappedFluxImage() {
        mGen.computeColorMappedFluxImage();
    }

    public boolean isImageGenerated() {
        return mGen.isImageGenerated();
    }

    public Image getImage() {
        return mGen.getImage();
    }

    public Dimension getSize() {
        return mGen.getSize();
    }

    public void setSize(Dimension size) {
        mGen.setSize(size);
    }

    /**
     * @deprecated
     */
    /*
     public void setTransform(AffineTransform trans)
     {
     mGen.setTransform(trans);
     }
     */
    /**
     * @deprecated
     */

    public void setTransform(Transform3D trans) {
        mGen.setTransform(trans);
    }

    public boolean getValid() {
        return mGen.getValid();
    }

    public void setValid(boolean b) {
        //TDebug.println("FieldConvolution setValid: "+ b);
        mGen.setValid(b);
        if (!b) {
            if (mNode != null) mNode.setVisible(false);
        }
    }

    public void reset() {
        mGen.reset();
    }

    public void dispose() {
        if (mGen != null) mGen.dispose();
        
        //FIXXME:
        if (mNode != null && mNode instanceof teal.render.j3d.ImageNode) {
            ((teal.render.j3d.ImageNode) mNode).dispose();
        }
    }

}