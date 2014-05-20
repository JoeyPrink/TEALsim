/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Node3D.java,v 1.33 2010/12/06 12:10:11 stefan Exp $ 
 * 
 */
package teal.render.jme;

import java.io.IOException;

import javax.media.j3d.Bounds;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import org.jdesktop.mtgame.RenderUpdater;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.MaterialState.MaterialFace;
import com.jme.system.DisplaySystem;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

import teal.config.Teal;
import teal.render.ColorUtil;
import teal.render.HasPosition;
import teal.render.HasRotation;
import teal.render.TAbstractRendered;
import teal.render.TMaterial;
import teal.render.TealMaterial;
import teal.render.scene.TNode3D;
import teal.util.TDebug;

/**
 * This is the base class for all jMonkeyEngine Scene Graph objects used
 * in TEALSim. It is basically a jme node which additionally implements
 * the  {@link TNode3D} interface. <br />
 * 
 * Note that this class effects their children in terms of their scale,
 * translation and rotation in the "offset"-methods. So if children are
 * attached, their transform should not be touched.
 * 
 * @author Stefan
 */
public class Node3D extends Node implements TNode3D {

//	private static final String USER_DATA_FIELD_NAME = "TElement";

    /**
	 * 
	 */
	private static final long serialVersionUID = -5733519961911757134L;


	public final static Vector3f refDirection = new Vector3f(0,1,0);

	public final static int stemSegments = 20;
    public final static float stemRadius = 0.02f;
    public final static float stemHeight = 1f;
	public final static float stemOffset = 0.5f;
	
    public final static float coneRadius = 0.1f;
    public final static float coneHeight = 0.15f;

    protected boolean selected = false;
    
    public Node3D() {
    	super();
    }
    
    public Node3D(TAbstractRendered element) {
    	doSetElement(element,false);
    }
    
	public void detach() {
		removeFromParent();		
	}

	public teal.render.Bounds getBoundingArea() {
    	final BoundingVolume bounds = getWorldBound();
    	if(bounds == null)
    		return null;
		Vector3f center = bounds.getCenter();

		teal.render.Bounds return_value = null;

		//casting bounds to j3d type bounds
		switch(bounds.getType()) {
		case AABB: //bounding box
    		return_value = new teal.render.BoundingBox();
    		Vector3f extend = new Vector3f();
    		((BoundingBox)bounds).getExtent(extend);
    		((teal.render.BoundingBox)return_value).setLower(new Point3d(center.x-extend.x, 
    				center.y-extend.y, center.z-extend.z));
    		((teal.render.BoundingBox)return_value).setUpper(new Point3d(center.x+extend.x, 
    				center.y+extend.y, center.z+extend.z));
    		break;
		case Sphere:
    		javax.vecmath.Point3d vecMathCenter = new Point3d(center.x,
    				center.y,center.z);
    		float radius = ((BoundingSphere) bounds).radius;
    		return_value = new teal.render.BoundingSphere(vecMathCenter,radius);
    		break;
		case OBB:
			// this is casted to an enclosing j3d bounding sphere. A BoundingPolytope
			// would probably be a better idea!
			OrientedBoundingBox obb = (OrientedBoundingBox) bounds;
			obb.computeCorners();
			BoundingSphere sphere = new BoundingSphere();
			sphere.averagePoints(obb.vectorStore);
			Vector3f sphereCenter = sphere.getCenter();
			Point3d vmCenter = new Point3d(sphereCenter.x, sphereCenter.y, sphereCenter.z);
			return_value = new teal.render.BoundingSphere(vmCenter,sphere.getRadius());
		default:
			TDebug.println(1,"Bounding volume not supported!");
		}

    	return return_value;
	}

	public TAbstractRendered getElement() {
//		return (TAbstractRendered)this.getUserData(USER_DATA_FIELD_NAME);
		return null;
	}

	public TMaterial getMaterial() {
		return Node3D.getMaterial(this);
	}

	public Vector3d getScale() {
		Vector3f scale = this.getLocalScale();
		return new Vector3d(scale.x, scale.y,scale.z);
	}

	public boolean isSelectable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setDirection(Vector3d newDirection) {
		Vector3f newDir = new Vector3f((float)newDirection.x, (float)newDirection.y, (float)newDirection.z);
		setDirection(newDir);
	}
	
	protected void setDirection(final Vector3f newDirection) {
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				setLocalRotation(getDirectionTransform(refDirection, newDirection));		
				((Node3D)obj).update();
			}			
		}, this);
	}
	
	protected void doSetElement(TAbstractRendered element, final boolean inRenderThread){
		doSetElement(element,inRenderThread,this);
	}

		

	protected static void doSetElement(TAbstractRendered element, final boolean inRenderThread, Spatial node){
    	final Vector3d pos = (element instanceof HasPosition)?((HasPosition) element).getPosition() : null;
    	final Quat4d rot = (element instanceof HasRotation) ? ((HasRotation) element).getRotation(): null;

        if(element.getMaterial() != null){
        	Node3D.setMaterial(element.getMaterial(), node);
        }		

        if(pos == null && rot == null)
    		return;
    	
    	RenderUpdater updater = new RenderUpdater() {
			public void update(Object obj) {	
				if(pos != null)
					((Spatial) obj).setLocalTranslation((float)pos.x, (float)pos.y, (float)pos.z);
				if(rot != null)
					((Spatial) obj).setLocalRotation(new Quaternion((float)rot.x,(float)rot.y,(float)rot.z,(float)rot.w));
				TealWorldManager.getWorldManager().addToUpdateList((Spatial)obj);
			}			
		};

		if(inRenderThread)
	    	TealWorldManager.getWorldManager().addRenderUpdater(updater,node);
		else
			updater.update(node);
	}
	
	// do not override this..override doSetElement instead
	final public void setElement(TAbstractRendered element) {
//		this.setUserData(USER_DATA_FIELD_NAME, element);
        if (element != null) {
        	doSetElement(element,true);
        }
	}

	public void setMaterial(TMaterial material) {
		Node3D.setMaterial(material, this);
		
	}

	/**
	 * This should be abstract and implemented in the subclasses!! Is just implemented
	 * here for convenience. If any children set there translation by themselves, this
	 * will override these settings!!!
	 */
	public void setModelOffsetPosition(Vector3d offset) {
		final Vector3f pos = new Vector3f((float)offset.x, (float)offset.y, (float)offset.z);
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				for(Spatial child:getChildren()) {
					child.setLocalTranslation(pos);
				}
				((Node3D)obj).update();
			}			
		}, this);
	}

	/**
	 * This should be abstract and implemented in the subclasses!! Is just implemented
	 * here for convenience. If any children set there translation, scale or rotation
	 * by themselves, this will override these settings!!!
	 */
	public void setModelOffsetTransform(Transform3D t) {
		final Vector3f pos = TransformUtil.getTranslationFromTransform3D(t);
		final Vector3f scale = TransformUtil.getScaleFromTransform3D(t);
		final Quaternion rot = TransformUtil.getRotationFromTransform3D(t);

		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				for(Spatial child:getChildren()) {
					child.setLocalScale(scale);
					child.setLocalRotation(rot);
					child.setLocalTranslation(pos);
				}
				((Node3D)obj).update();
			}			
		}, this);
	}

	public void setScale(final double s) {
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				setLocalScale((float)s);
				((Node3D)obj).update();
			}			
		}, this);
	}

	public void setScale(Vector3d s) {
		final Vector3f scale = new Vector3f((float)s.x, (float)s.y, (float)s.z);
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				setLocalScale(scale);
				((Node3D)obj).update();
			}			
		}, this);
	}

	public void setSelectable(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void setSelected(boolean b) {
		selected = b;
	}
	
	protected void update(){
    	TealWorldManager.getWorldManager().addToUpdateList(this);		
	}

	public int update(int renderFlags) {
    	int status = renderFlags;
    	if((status & TAbstractRendered.POSITION_CHANGE) == TAbstractRendered.POSITION_CHANGE){
    	status ^= TAbstractRendered.POSITION_CHANGE;
    	}
    	if((status & TAbstractRendered.ROTATION_CHANGE) == TAbstractRendered.ROTATION_CHANGE){
    		status ^= TAbstractRendered.ROTATION_CHANGE;
    	}
    	if((status & TAbstractRendered.SCALE_CHANGE) == TAbstractRendered.SCALE_CHANGE){
    		status ^= TAbstractRendered.SCALE_CHANGE;
    	}
    	if((status & TAbstractRendered.VISIBILITY_CHANGE) == TAbstractRendered.VISIBILITY_CHANGE){
    		status ^= TAbstractRendered.VISIBILITY_CHANGE;
    	}
    	return status;
	}

	public Vector3d getPosition() {
		Vector3f pos = this.getLocalTranslation();
		return new Vector3d(pos.x, pos.y, pos.z);
	}

	public void setPosition(final Vector3d pos) {
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				setLocalTranslation((float)pos.x, (float)pos.y, (float)pos.z);
				((Node3D)obj).update();
			}			
		}, this);
	}

	public Quat4d getRotation() {
		Quaternion rot = this.getLocalRotation();
		return new Quat4d(rot.x, rot.y, rot.z, rot.w);
	}

	public boolean isRotable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRotating() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setRotable(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void setRotating(boolean state) {
		// TODO Auto-generated method stub
		
	}
	
	protected static Matrix3f getMatrix3f(Matrix3d rot) {
    	Matrix3f rotation = new Matrix3f();
    	rotation.m00 = (float)rot.m00;
    	rotation.m01 = (float)rot.m01;
    	rotation.m02 = (float)rot.m02;
    	rotation.m10 = (float)rot.m10;
    	rotation.m11 = (float)rot.m11;
    	rotation.m12 = (float)rot.m12;
    	rotation.m20 = (float)rot.m20;
    	rotation.m21 = (float)rot.m21;
    	rotation.m22 = (float)rot.m22;
    	return rotation;
	}
	
	protected static Vector3f getVector3f(Vector3d value){
		return new Vector3f((float) value.x,(float) value.y,(float) value.z);
	}

	public void setRotation(final Matrix3d rot) {
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
		    	setLocalRotation(getMatrix3f(rot));	
				((Node3D)obj).update();
			}			
		}, this);
	}

	public void setRotation(final Quat4d orientation) {
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				setLocalRotation(new Quaternion((float)orientation.x,
						(float)orientation.y,(float)orientation.z,(float)orientation.w));
				((Node3D)obj).update();
			}			
		}, this);
	}

	public Transform3D getTransform() {
		return TransformUtil.getTransform3D(this);
	}

	public void setTransform(Transform3D trans) {
		final Quaternion rot = TransformUtil.getRotationFromTransform3D(trans);
		final Vector3f scale = TransformUtil.getScaleFromTransform3D(trans);
		final Vector3f transform = TransformUtil.getTranslationFromTransform3D(trans);
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				setLocalRotation(rot);
				setLocalScale(scale);
				setLocalTranslation(transform);
				((Node3D)obj).update();
			}			
		}, this);	
	}

	public boolean getPickable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getPicked() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPickable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPicked() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setPickable(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void setPicked(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public Vector3d getModelOffsetPosition() {
		if(this.getQuantity() == 0)
			return new Vector3d();
		Vector3f vec = super.getChild(0).getLocalTranslation();
		return new Vector3d(vec.x, vec.y, vec.z);
	}

	public Transform3D getModelOffsetTransform() {
		if(this.getQuantity() == 0)
			return new Transform3D();
		return TransformUtil.getTransform3D(super.getChild(0));
	}
	
    protected Quaternion getDirectionTransform(Vector3f refDirection, Vector3f newDirection) {
        TDebug.println(3, "setDirection: " + newDirection);
        if (newDirection.length() == 0) return new Quaternion();

        Quaternion axisAngle = new Quaternion();
        
//        Vector3d direction = new Vector3d(newDirection);
//        direction.normalize();
        Vector3f axis = refDirection.cross(newDirection.normalize());
//        axis.cross(refDirection, newDirection.normalize());
        float angle = refDirection.angleBetween(newDirection.normalize());
//        Transform3D trans = getTransform3D();
//        AxisAngle4d axisAngle = null;
//        double angle = refDirection.angle(direction);
        if (axis.length() != 0) {
//            axis.normalizeLocal();
            axisAngle.fromAngleAxis(angle, axis);
        } else {
            if (angle > FastMath.PI / 2f) {
                Vector3f u = new Vector3f();
                Vector3f v = new Vector3f();
                do {
                    u.set(FastMath.nextRandomFloat(),  FastMath.nextRandomFloat(), FastMath.nextRandomFloat());
                    v.set(refDirection);
                    v.normalizeLocal();
//                    v.scale(u.dot(v));
                    v.mult(u.dot(v));
                    u.subtract(v);
                } while (u.length() < Teal.DoubleZero);
//                u.normalize();
//                axisAngle = new AxisAngle4d(u, angle);
                axisAngle.fromAngleAxis(angle, u);
            } else axisAngle.fromAngleAxis(0, refDirection);
        }
        return axisAngle;
    }
    
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        selected = capsule.readBoolean("selected", false);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(selected, "selected", false);
    }


	protected static void setMaterial(final TMaterial material, Spatial shape) {
					
		RenderUpdater ru = new RenderUpdater() {
			public void update(Object arg0) {
				Spatial spatial = (Spatial)arg0;
	
				MaterialState ms = (MaterialState) spatial.getRenderState(RenderState.StateType.Material);
				if (ms == null) {
					ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
					spatial.setRenderState(ms);
				}
				Color4f ambient = material.getAmbient();
				Color4f diffuse = material.getDiffuse();
				Color4f emissive = material.getEmissive();
				Color4f specular = material.getSpecular();
				if(ambient != null) ms.setAmbient(ColorUtil.getColorRGBA(ambient));
				if(diffuse != null) ms.setDiffuse(ColorUtil.getColorRGBA(diffuse));
				if(emissive != null) ms.setEmissive(ColorUtil.getColorRGBA(emissive));
				if(specular != null) ms.setSpecular(ColorUtil.getColorRGBA(specular));
					
				CullState cs = (CullState) spatial.getRenderState(RenderState.StateType.Cull);
				if(material.getCullMode() != TMaterial.CULL_NONE) {
					if(cs == null) {
						cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
						spatial.setRenderState(cs);
					}
					switch (material.getCullMode()) {
					case TMaterial.CULL_BACK:
						cs.setCullFace(CullState.Face.Back);
						break;
					case TMaterial.CULL_FRONT:
						cs.setCullFace(CullState.Face.Front);
						break;
					case TMaterial.CULL_BOTH:
						cs.setCullFace(CullState.Face.FrontAndBack);
						break;
					default:
					}					
					cs.setEnabled(true);
				} else {
					if(cs != null)
						cs.setEnabled(false);
				}
				
				ms.setShininess(material.getShininess()*128f);		
				if(material.getTransparancy() != 0f) {					
					Node3D.enableTransparency(spatial);
				} else {
					Node3D.disableTransparency(spatial);
				}
					
				
				//setting face mode
				WireframeState ws = (WireframeState) spatial.getRenderState(RenderState.StateType.Wireframe);
				if(ws == null) {
					ws = DisplaySystem.getDisplaySystem().getRenderer().createWireframeState();
					spatial.setRenderState(ws);
				}
				ws.setEnabled(material.getFaceMode() != TMaterial.FACE_FILL);
				
				spatial.updateRenderState();
				TealWorldManager.getWorldManager().addToUpdateList(spatial);
			}
		};
		
		if(DisplaySystem.getDisplaySystem().getRenderer() == null)
			TealWorldManager.getWorldManager().addRenderUpdater(ru, shape);
		else
			ru.update(shape);
	}

	protected static TMaterial getMaterial(Spatial shape) {
		MaterialState ms = (MaterialState) shape.getRenderState(RenderState.StateType.Material);
		if(ms == null)
			return null;
		
		TealMaterial mat = new TealMaterial();
		mat.setAmbient(ColorUtil.getColor4f(ms.getAmbient()));
		mat.setDiffuse(ColorUtil.getColor4f(ms.getDiffuse()));
		mat.setEmissive(ColorUtil.getColor4f(ms.getEmissive()));
		mat.setSpecular(ColorUtil.getColor4f(ms.getSpecular()));
		mat.setShininess(ms.getShininess()/128f);
		
		CullState cs = (CullState) shape.getRenderState(RenderState.StateType.Cull);
		if(cs != null && cs.isEnabled()) {
			switch (cs.getCullFace()) {
			case Front:
				mat.setCullMode(TMaterial.CULL_FRONT);
				break;
			case Back:
				mat.setCullMode(TMaterial.CULL_BACK);
				break;
			case FrontAndBack:
				mat.setCullMode(TMaterial.CULL_BOTH);
				break;
			default:
			}			
		}
		return mat;    	
	}
    
	protected static void disableTransparency(Spatial shape) {
		BlendState bs = (BlendState) shape.getRenderState(RenderState.StateType.Blend);
		if(bs != null) {
			bs.setBlendEnabled(false);
			shape.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
		}
		ZBufferState zb = (ZBufferState) shape.getRenderState(RenderState.StateType.ZBuffer);
		if(zb != null) {
			zb.setEnabled(false);
		}
	}

	protected static void enableTransparency(Spatial shape) {
		BlendState bs = (BlendState) shape.getRenderState(RenderState.StateType.Blend);
		if(bs == null) {
			bs = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
			shape.setRenderState(bs);
		}
		bs.setSourceFunctionAlpha(BlendState.SourceFunction.SourceAlpha);
		bs.setDestinationFunctionAlpha(BlendState.DestinationFunction.OneMinusSourceAlpha);		
		bs.setBlendEquationAlpha(BlendState.BlendEquation.Subtract);
	//		bs.setTestEnabled(false);
	//		bs.setTestFunction(TestFunction.GreaterThan);
		bs.setBlendEnabled(true);
		shape.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
			
//		ZBufferState zb = (ZBufferState) shape.getRenderState(RenderState.StateType.ZBuffer);
//		if(zb == null) {
//			zb = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
//			shape.setRenderState(zb);
//		}
//		zb.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
//		zb.setEnabled(true);
//		zb.setWritable(false);
			
					
	}
	
	protected static void setFaceMode(final Spatial shape, final MaterialState.MaterialFace mode){
		TealWorldManager.getWorldManager().addRenderUpdater(new RenderUpdater() {
			public void update(Object obj) {
				MaterialState ms = (MaterialState)shape.getRenderState(RenderState.StateType.Material);
				if(ms == null) {
					ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
					shape.setRenderState(ms);
				}
				ms.setMaterialFace(mode);
				ms.setEnabled(true);
			}
		}, null);		
	}

}
