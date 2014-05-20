/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: FieldLineNode.java,v 1.19 2011/05/27 15:39:35 pbailey Exp $ 
 * 
 */

package teal.render.jme;

import java.awt.Color;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import org.jdesktop.mtgame.RenderUpdater;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Line.Mode;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.MaterialState.ColorMaterial;
import com.jme.system.DisplaySystem;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

import teal.render.ColorUtil;
import teal.render.HasPosition;
import teal.render.HasRotation;
import teal.render.TAbstractRendered;
import teal.render.TMaterial;
import teal.render.TealMaterial;
import teal.render.scene.TFieldLineNode;
import teal.sim.spatial.FieldLine;
import teal.util.TDebug;

public class FieldLineNode extends Node3D implements TFieldLineNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8474978666463054997L;

	private final static String LINE_1_NAME = "Line 1";
	private final static String LINE_2_NAME = "Line 2";
	private final static String LINES_NAME = "lines";
	private final static String NON_LINES_NAME = "rest";
	
	private int symCount = 1;
	Vector3f symAxis = null;
	
	private transient Line lineGeo1;
	private transient Line lineGeo2;
	
	private boolean coloredVertices = false;
	
//	private FloatBuffer line1Data = null;
//	private FloatBuffer line2Data = null;
//	private FloatBuffer color1Data = null;
//	private FloatBuffer color2Data = null;
	
	public FieldLineNode(){
		this(1, new Vector3f(0,1,0));
	}
	
	public FieldLineNode(TAbstractRendered element) {
		this();
		setFieldLine((FieldLine)element);
	}
	
	public FieldLineNode(int num, Vector3f axis) {
		super();
		symCount = num;
		symAxis = axis;
		
		this.attachChild(new Node(NON_LINES_NAME));
		
		lineGeo1 = new Line(LINE_1_NAME);
		lineGeo1.setMode(Mode.Connected);
		lineGeo2 = new Line(LINE_2_NAME);
		lineGeo2.setMode(Mode.Connected);

		constructClones();

		
		//TODO: markers and override some Node3D methods		
		setVisible(true);
	}
		
	public FieldLineNode(TAbstractRendered element, int num, Vector3f axis) {
		this(num, axis);
		setFieldLine((FieldLine)element);
	}
	
	@Override
	protected void doSetElement(TAbstractRendered element, final boolean inRenderThread){
		Node3D.doSetElement(element, inRenderThread, getChild(NON_LINES_NAME));
	}

	
	public void setFieldLine(FieldLine element) {
		doSetElement(element,false);
		setColor(element.getColor());
		doSetSymmetry(element.getSymmetryCount(),element.getSymmetryAxis(),false);
	}

	private void constructClones() {
        Node lines = (Node)this.getChild(LINES_NAME);
        if(lines == null) {
        	lines = new Node(LINES_NAME);
        	this.attachChild(lines);
        }
        
        lines.detachAllChildren();
		//construct geometry
		float r = 0f;
		if(symCount > 1)
			r = 2f * FastMath.PI / ((float)symCount);
		
		for(int i = 0; i < symCount; i++) {
			Node clone = new Node();
			clone.attachChild(new SharedLine(lineGeo1));
			clone.attachChild(new SharedLine(lineGeo2));
			clone.setLocalRotation(new Quaternion().fromAngleAxis(i*r, symAxis));
			lines.attachChild(clone);
		}
	}	
	
	
	public void checkMarkers(int num) {
		// TODO Auto-generated method stub
		
	}

	
	public double getPickRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public boolean isPickVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void setLineGeometry(int len1, float[] line1, int len2, float[] line2) {
		setLineGeometry(len1,line1,null, len2,line2, null);
	}

	
	public void setLineGeometry(int len1, float[] line1, float[] colors1,
			int len2, float[] line2, float[] colors2) {
		if (len1 > 1) {
			final FloatBuffer line1Data = BufferUtils
					.createFloatBuffer(len1 * 3);
			line1Data.rewind();
			line1Data.put(line1, 0, len1 * 3);

			final FloatBuffer color1Data;
			if (colors1 != null) {
				// if(color1Data == null || color1Data.capacity() != len1*4)
				color1Data = BufferUtils.createFloatBuffer(len1 * 4);
				color1Data.rewind();
				for (int i = 0; i < len1; i++)
					color1Data.put(colors1, 3 * i, 3).put(1f);
			} else {
				color1Data = null;
			}

			final FieldLineNode me = this;
			TealWorldManager.getWorldManager().addRenderUpdater(
					new RenderUpdater() {
						public void update(Object arg) {
							lineGeo1.setVisible(true);
							lineGeo1.reconstruct(line1Data, null, color1Data,
									null);
							me.update();
						};
					}, null);

			// synchronized(lineGeo1) {
			// lineGeo1.reconstruct(line1Data, null, color1Data, null);
			// }
			// lineGeo1.setVisible(true);
		} else {
			lineGeo1.setVisible(false);
		}

		if (len2 > 1) {
			// if(line2Data == null || line2Data.capacity() != len2*3) {
			final FloatBuffer line2Data = BufferUtils
					.createFloatBuffer(len2 * 3);
			// }
			line2Data.rewind();
			line2Data.put(line2, 0, len2 * 3);

			final FloatBuffer color2Data;
			if (colors2 != null) {
				// if(color2Data == null || color2Data.capacity() != len2*4)
				color2Data = BufferUtils.createFloatBuffer(len2 * 4);
				color2Data.rewind();
				for (int i = 0; i < len2; i++)
					color2Data.put(colors2, i * 3, 3).put(1f);
			} else {
				color2Data = null;
			}

			final FieldLineNode me = this;
			TealWorldManager.getWorldManager().addRenderUpdater(
					new RenderUpdater() {
						public void update(Object arg) {
							lineGeo2.setVisible(true);
							lineGeo2.reconstruct(line2Data, null, color2Data,
									null);
							me.update();
						};
					}, null);

			// synchronized(lineGeo2) {
			// lineGeo2.reconstruct(line2Data, null, color2Data, null);
			// }
			// lineGeo2.setVisible(true);
		} else {
			lineGeo2.setVisible(false);
		}

		synchronized (this) {
			if (colors1 == null && colors2 == null
					&& this.coloredVertices == true) {
				this.coloredVertices = false;
				fixColorState();
			} else if ((colors1 != null || colors2 != null)
					&& this.coloredVertices == false) {
				this.coloredVertices = true;
				fixColorState();
			}
		}
	}
	
	// call that only when necessary!
	private void fixColorState() {
		final Spatial lines = this.getChild(LINES_NAME);
    	TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater(){
    		public void update(Object arg) {    			
    			boolean coloredVertices = (Boolean) arg;
    			MaterialState ms = (MaterialState) lines.getRenderState(RenderState.StateType.Material);
    			if(ms == null) {
    				ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
    				ms.setColorMaterial(ColorMaterial.AmbientAndDiffuse);
    				lines.setRenderState(ms);
    			}
    			ms.setEnabled(coloredVertices);
    		};
    	}, this.coloredVertices);
	}
	


	public void setMarkerValues(int idx, Vector3d pos, Vector3d direction) {
		// TODO Auto-generated method stub
		
	}

	
	public void setMarkerVisible(int idx, boolean state) {
		// TODO Auto-generated method stub
		
	}

	
	public void setPickRadius(double r) {
		// TODO Auto-generated method stub
		
	}

	
	public void setPickVisible(boolean state) {
		// TODO Auto-generated method stub
		
	}
	
	protected void doSetSymmetry(int count, Vector3d axis, final boolean runInRendererThread){
		symAxis.set((float)axis.x,(float)axis.y, (float) axis.z);
		int num = count;
		if(num < 0)
			num = 0;
		
		final Node lines = (Node)this.getChild(LINES_NAME);
		int numChildren = lines.getQuantity();
		if(num > numChildren) {
			for(int i = numChildren; i < num; i++) {
				RenderUpdater updater = new RenderUpdater() {
					public void update(Object obj) {
				
						//TODO: check if we can do that outside the renderer thread
						Node clone = new Node();
						clone.attachChild(new SharedLine(lineGeo1));
						clone.attachChild(new SharedLine(lineGeo2));
						lines.attachChild(clone);
						TealWorldManager.getWorldManager().addToUpdateList(lines);
					}				
				};
				
				if(runInRendererThread){
					TealWorldManager.getWorldManager().addRenderUpdater(updater, null);
				} else {
					updater.update(null);
				}				
			}
		}
		
		if(num < numChildren) {
			RenderUpdater updater = new RenderUpdater() {
				public void update(Object obj) {
					while(lines.getQuantity() > ((Integer)obj).intValue()){
						lines.detachChildAt(0);
					}
					TealWorldManager.getWorldManager().addToUpdateList(lines);
				}				
			};
			if(runInRendererThread)
				TealWorldManager.getWorldManager().addRenderUpdater(updater,num);
			else
				updater.update(num);
		}
		
		final float r;
		
		if(num > 1)
			r = 2f * FastMath.PI / ((float)num);
		else
			r=0f;
		
		RenderUpdater updater = new RenderUpdater() {
			public void update(Object obj) {
				int i=0;
				for(Spatial child:lines.getChildren()){
					child.setLocalRotation(new Quaternion().fromAngleAxis(((float)i)*r, (Vector3f)obj));
					i++;
				}
			}				
		};

		if(runInRendererThread){
			TealWorldManager.getWorldManager().addRenderUpdater(updater, new Vector3f(symAxis));
		} else {
			updater.update(new Vector3f(symAxis));
		}
		
//		for(int i=0; i<num; i++) {
//			Spatial child = lines.getChild(i);
//			child.setLocalRotation(new Quaternion().fromAngleAxis(((float)i)*r, symAxis));
//		}
		symCount = num;		
	}

	final public synchronized void setSymmetry(int count, Vector3d axis) {
		doSetSymmetry(count,axis,true);
	}

	
	public Color3f getColor() {
		TMaterial mat = this.getMaterial();
		if(mat == null)				
			return null;
		return ColorUtil.getColor3f(mat.getDiffuse());
	}


	public void setColor(Color3f q) {
		TMaterial mat = this.getMaterial();
		if(mat == null)
			mat = new TealMaterial();
		mat.setDiffuse(q);
		Color3f ambient = new Color3f(q);
		ambient.scale(0.9f);
		mat.setAmbient(ambient);		
	}


	public void setColor(Color q) {
		setColor(new Color3f(q));
	}
	
	//some methods have to be overridden from Node3D
	
	@Override
	public Vector3d getPosition() {
		Vector3f pos = this.getChild(NON_LINES_NAME).getLocalTranslation();
		return new Vector3d(pos.x, pos.y, pos.z);
	}
	
	@Override
	public void setPosition(final Vector3d pos) {
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				getChild(NON_LINES_NAME).setLocalTranslation((float)pos.x, (float)pos.y, (float)pos.z);
				((Node3D)obj).update();
			}			
		}, this);	
	}

	@Override
	public Quat4d getRotation() {
		Quaternion rot = this.getChild(NON_LINES_NAME).getLocalRotation();
		return new Quat4d(rot.x, rot.y, rot.z, rot.w);
	}
	
	@Override
	public void setRotation(final Matrix3d rot) {
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
		    	getChild(NON_LINES_NAME).setLocalRotation(getMatrix3f(rot));	
				((Node3D)obj).update();
			}			
		}, this);	
	}

	@Override
	public void setRotation(final Quat4d orientation) {
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				getChild(NON_LINES_NAME).setLocalRotation(new Quaternion((float)orientation.x,
						(float)orientation.y,(float)orientation.z,(float)orientation.w));
				((Node3D)obj).update();
			}			
		}, this);	
	}

	@Override
	public Vector3d getScale() {
		Vector3f scale = this.getChild(NON_LINES_NAME).getLocalScale();
		return new Vector3d(scale.x, scale.y,scale.z);
	}
	
	@Override
	public void setScale(final double s) {
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				getChild(NON_LINES_NAME).setLocalScale((float)s);
				((Node3D)obj).update();
			}			
		}, this);	
	}

	@Override
	public void setScale(Vector3d s) {
		final Vector3f scale = new Vector3f((float)s.x, (float)s.y, (float)s.z);
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				getChild(NON_LINES_NAME).setLocalScale(scale);
				((Node3D)obj).update();
			}			
		}, this);	
	}
	
	@Override
	public Vector3d getModelOffsetPosition() {
		if(((Node)this.getChild(NON_LINES_NAME)).getQuantity() == 0)
			return new Vector3d();
		Vector3f vec = ((Node)this.getChild(NON_LINES_NAME)).getChild(0).getLocalTranslation();
		return new Vector3d(vec.x, vec.y, vec.z);
	}

	@Override
	public Transform3D getModelOffsetTransform() {
		if(((Node)this.getChild(NON_LINES_NAME)).getQuantity() == 0)
			return new Transform3D();
		return TransformUtil.getTransform3D(((Node)this.getChild(NON_LINES_NAME)).getChild(0));
	}

	@Override
	public void setModelOffsetPosition(Vector3d offset) {
		final Vector3f pos = new Vector3f((float)offset.x, (float)offset.y, (float)offset.z);
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				for(Spatial child:((Node)getChild(NON_LINES_NAME)).getChildren()) {
					child.setLocalTranslation(pos);
				}
				((Node3D)obj).update();
			}			
		}, this);	
	}

	@Override
	public void setModelOffsetTransform(Transform3D t) {
		final Vector3f pos = TransformUtil.getTranslationFromTransform3D(t);
		final Vector3f scale = TransformUtil.getScaleFromTransform3D(t);
		final Quaternion rot = TransformUtil.getRotationFromTransform3D(t);
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				for(Spatial child:((Node)getChild(NON_LINES_NAME)).getChildren()) {
					child.setLocalScale(scale);
					child.setLocalRotation(rot);
					child.setLocalTranslation(pos);
				}
				((Node3D)obj).update();
			}			
		}, this);	
	}

	/*
	private int symCount = 1;
	Vector3f symAxis = null;
	
	private transient Line lineGeo1;
	private transient Line lineGeo2;
	
	private boolean coloredVertices = false;
	
	private FloatBuffer line1Data = null;
	private FloatBuffer line2Data = null;
	private FloatBuffer color1Data = null;
	private FloatBuffer color2Data = null; 
	*/
		
	
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        symCount = capsule.readInt("symCount", 1);
        symAxis = (Vector3f)capsule.readSavable("symAxis", new Vector3f(0,1,0));
        coloredVertices = capsule.readBoolean("coloredVertices", false);

        //TODO: check whether this is needed, MIGHT cause endless recursion
        lineGeo1 = (Line) capsule.readSavable("lineGeo1", null);
        lineGeo2 = (Line) capsule.readSavable("lineGeo2", null);

        
//        line1Data = capsule.readFloatBuffer("line1Data", null);
//        line2Data = capsule.readFloatBuffer("line2Data", null);
//        color1Data = capsule.readFloatBuffer("color1Data", null);
//        color2Data = capsule.readFloatBuffer("color2Data", null);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(symCount, "symCount", 1);
        capsule.write(symAxis, "symAxis", new Vector3f(0,1,0));
        capsule.write(coloredVertices, "coloredVertices", false);
        
        //TODO: check whether this is needed:        
        capsule.write(lineGeo1, "lineGeo1", null);
        capsule.write(lineGeo2, "lineGeo2", null);

        
//        capsule.write(line1Data, "line1Data", null);
//        capsule.write(line2Data, "line2Data", null);
//        capsule.write(color1Data, "color1Data", null);
//        capsule.write(color2Data, "color2Data", null);
    }

}
