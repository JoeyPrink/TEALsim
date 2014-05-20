/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ImageNode.java,v 1.34 2007/07/16 22:04:54 pbailey Exp $ 
 * 
 */

package teal.render.j3d;

import java.awt.*;
import java.awt.image.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import teal.math.RectangularPlane;
import teal.util.*;
import teal.visualization.dlic.*;
import teal.visualization.image.*;
/**
 * provides SceneGraphNode for the management of a single imageMapped display
 * plane.
 * 
 * screen that the LIC is put on  jwb
 * @author Phil Bailey
 * @version $Revision: 1.34 $
 *  
 */
public class ImageNode extends ShapeNode implements ImageStatusListener {
	protected static int sTexMap[] = {0};
	protected static QuadArray sQa;
	static {
		sQa = new QuadArray(4, GeometryArray.COORDINATES
				| GeometryArray.TEXTURE_COORDINATE_2, 1, sTexMap);
		sQa.setCoordinate(0, new Point3d(-0.5, -0.5, 0));
		sQa.setCoordinate(1, new Point3d(0.5, -0.5, 0));
		sQa.setCoordinate(2, new Point3d(0.5, 0.5, 0));
		sQa.setCoordinate(3, new Point3d(-0.5, 0.5, 0));
		sQa.setTextureCoordinate(0, 0, new TexCoord2f(0, 0));
		sQa.setTextureCoordinate(0, 1, new TexCoord2f(1, 0));
		sQa.setTextureCoordinate(0, 2, new TexCoord2f(1, 1));
		sQa.setTextureCoordinate(0, 3, new TexCoord2f(0, 1));
	}
	Appearance app;
	ImageComponent2D imageComp;
	Texture tex;
	public ImageNode() {
		super();
		mShape.setGeometry(sQa);
		app = Node3D.makeAppearance(new Color3f(Color.GRAY), 0.0f, 0.5f, false);
		app.setCapability(Appearance.ALLOW_TEXTURE_READ);
		app.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
		//PolygonAttributes polyAttribs = new PolygonAttributes(
		// PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE, 0 );
		//app.setPolygonAttributes( polyAttribs );
		mShape.setAppearance(app);
	}
	public void setImage(BufferedImage img) {
		setImage(img, false);
	}
	public void setImage(BufferedImage img, boolean yUp) {
		try {
			if (app == null) {
				app = Node3D.makeAppearance(new Color3f(Color.GRAY), 0.0f, 0.5f, false);
				app.setCapability(Appearance.ALLOW_TEXTURE_READ);
				app.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
				//PolygonAttributes polyAttribs = new PolygonAttributes(
				// PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE,
				// 0 );
				//app.setPolygonAttributes( polyAttribs );
				mShape.setAppearance(app);
			}
			if (img == null) {
				app.setTexture(null);
			} else {
				
                int flags = 0;
				if (yUp)
					flags = TextureLoader.Y_UP;
				
				TextureLoader textureLoader = new TextureLoader(img,flags);
				/*
				ImageComponent2D tmpImg = textureLoader.getImageComponent2D();
				tmpImg.setCapability(ImageComponent.ALLOW_IMAGE_READ);
				tmpImg.setCapability(ImageComponent.ALLOW_IMAGE_WRITE);
				if ((tex == null) || (tex.getWidth() != tmpImg.getWidth())
						|| (tex.getHeight() != tmpImg.getHeight())) {
					tex = makeTexture(tmpImg.getWidth(), tmpImg.getHeight());
				}
                
				tex.setImage(0, tmpImg);
                */
                
                tex = textureLoader.getTexture();
                tex.setCapability(Texture.ALLOW_IMAGE_READ);
		        tex.setCapability(Texture.ALLOW_IMAGE_WRITE);
		        tex.setCapability(Texture.ALLOW_SIZE_READ);
				app.setTexture(tex);
                /*
				if (imageComp != null) {
					imageComp.getImage().flush();
					TDebug.println(2, "Flushed image");
				}
				imageComp = tmpImg;
                */
				}
		} catch (Exception e) {
			TDebug.printThrown(e);
		}
	}
	public synchronized void flushTexture() {
		synchronized (mShape) {
			Appearance app = mShape.getAppearance();
			if (app != null) {
				Texture tex = app.getTexture();
				flushTexture(tex);
				app.setTexture(null);
			}
		}
	}
	public void flushTexture(Texture tex) {
		if (tex != null) {
			TDebug.println(2, "TextureFlushing");
			ImageComponent imgc[] = tex.getImages();
			for (int n = 0; n < imgc.length; n++) {
				TDebug.println(2, "tex img: " + n);
				if (imgc[n] != null) {
					if (imgc[n] instanceof ImageComponent2D) {
						TDebug.println(2, "tex img flush: " + n);
						try {
							BufferedImage img = ((ImageComponent2D) imgc[n])
									.getImage();
							TDebug.println(2, "tex img flush: " + n);
							img.flush();
							TDebug.println(2, "tex img flush: " + n);
						} catch (Exception e) {
							TDebug.println(e.getMessage());
						}
					} else if (imgc[n] instanceof ImageComponent3D) {
						BufferedImage images[] = ((ImageComponent3D) imgc[n])
								.getImage();
						for (int i = 0; i < images.length; i++) {
							TDebug.println(2, "tex images: " + i);
							images[i].flush();
						}
					}
				}
			}
			TDebug.println(2, "TextureFlushed");
		} else {
			TDebug.println(2, "TextureFlushed null");
		}
	}
	public synchronized void dispose() {
		detach();
		flushTexture();
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
				Vector3d pos = ((DLICGenerator) ig).getCenter();
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
