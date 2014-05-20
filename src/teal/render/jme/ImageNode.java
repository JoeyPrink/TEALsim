/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ImageNode.java,v 1.9 2010/08/18 20:45:43 stefan Exp $ 
 * 
 */

package teal.render.jme;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import javax.vecmath.Quat4d;

import com.jme.image.Image;
import com.jme.image.Texture2D;
import com.jme.image.Image.Format;
import com.jme.image.Texture.WrapMode;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.MaterialState.MaterialFace;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

import teal.math.RectangularPlane;
import teal.render.ColorUtil;
import teal.render.TMaterial;
import teal.util.TDebug;
import teal.visualization.dlic.DLICGenerator;
import teal.visualization.image.ImageGenerator;
import teal.visualization.image.ImageStatusEvent;
import teal.visualization.image.ImageStatusListener;

public class ImageNode extends ShapeNode implements ImageStatusListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4253905302525555093L;
	final private static String PANE_NAME = "image pane";
	
	public ImageNode() {
		super();
		Quad imagePane = new Quad(PANE_NAME, 1f, 1f);
		TMaterial baseMaterial = ColorUtil.getMaterial(Color.GRAY);
//		baseMaterial.setTransparancy(0.5f);
		setFaceMode(imagePane,MaterialFace.FrontAndBack);
//		baseMaterial.setCullMode(TMaterial.CULL_BOTH);
		Node3D.setMaterial(baseMaterial, this);
		this.attachChild(imagePane);
	}
	
	
	public void setImage(BufferedImage img) {
		setImage(img, false);
	}
	public void setImage(BufferedImage img, boolean yUp) {
		
		if (img != null)  {		

			//TODO: improve this
			ByteBuffer imgBuf = BufferUtils.createByteBuffer(3*img.getHeight()*img.getWidth());
			
			for(int line = 0; line < img.getHeight(); ++line)
				for(int col= 0; col < img.getWidth(); ++col){
					int color;
					if(yUp)
						color = img.getRGB(col, line);
					else
						color = img.getRGB(col, img.getHeight()-line-1);							
					imgBuf.put((byte)((color >> 16) & 0xff));
					imgBuf.put((byte)((color >> 8) & 0xff));
					imgBuf.put((byte)((color) & 0xff));
				}
			imgBuf.flip();
			
			Spatial imagePane = this.getChild(PANE_NAME);
			
			Image image = new Image(Format.RGB8,img.getWidth(), img.getHeight(), imgBuf);
			Texture2D texture = new Texture2D();
			texture.setImage(image);
			texture.setWrap(WrapMode.MirrorBorderClamp);
			TextureState ts = (TextureState)imagePane.getRenderState(RenderState.StateType.Texture);				
			if(ts == null) {
				ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
				imagePane.setRenderState(ts);
			}
				
			ts.setTexture(texture);				
			ts.setEnabled(true);
				
		}				
				
	}

	
	public void imageStatus(ImageStatusEvent ise) {
		TDebug.println(1, "imageStatus: " + ise.getStatus());
		int status = ise.getStatus();
		if (status == ImageStatusEvent.INVALID) {
			//if(getVisible())
			setVisible(false);
			setImage(null);
		} else if (status == ImageStatusEvent.COMPLETE) {
			ImageGenerator ig = (ImageGenerator) ise.getSource();
			if (ig instanceof DLICGenerator) {
				RectangularPlane rec = ((DLICGenerator) ig).getComputePlane();
				setImage((BufferedImage) ig.getImage(), rec != null);
				double sc = ((DLICGenerator) ig).getImageScale();
				javax.vecmath.Vector3d pos = ((DLICGenerator) ig).getCenter();
				Quat4d quat = ((DLICGenerator) ig).getRotation();
				setScale(sc);
				setPosition(pos);
				if (quat != null) {
					quat.inverse();
					setRotation(quat);
				}
				TDebug.println(1, "DLIC Image scale = " + sc + " pos= " + pos
						+ " rot = " + quat);
			}
			setVisible(true);
		}
	}
	

}
