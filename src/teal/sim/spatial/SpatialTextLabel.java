/*
 * Created on Oct 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package teal.sim.spatial;

import java.awt.Font;

import javax.vecmath.Vector3d;

import teal.core.HasReference;
import teal.core.Referenced;
import teal.render.HasColor;
import teal.render.j3d.TextLabelNode;
import teal.render.scene.TNode3D;
import teal.sim.engine.TEngineControl;
import teal.sim.engine.EngineRendered;
import teal.physics.physical.PhysicalObject;

/**
 *  SpatialTextLabel represents a text label, rendered either as a 2D billboard or 3D text (currently just 3D text) that
 *  can be attached to another object in the scene, or positioned at a fixed point.  The label will retain its position 
 *  relative to its reference object as described below.
 */
public class SpatialTextLabel extends Spatial implements HasReference {

	private EngineRendered refObj = null;
	private Vector3d refPos = null;
	private String labelText;
	private Font font;
	
	private Vector3d positionOffset = new Vector3d();
	private double refDirectionOffset = 0.;
	private boolean useDirectionOffset = false;
	private Vector3d lastPosition = new Vector3d();
	
	private boolean textChanged = false;
	private boolean fontChanged = false;
	
	private Vector3d scale = new Vector3d(1.,1.,1.);
	private boolean scaleWithObject = true;
	private double baseScale = 1.;
	
	
	public SpatialTextLabel() {
		super();
		labelText = "Default Text";
		refPos = new Vector3d(0.,0.,0.);
	}
	
	public SpatialTextLabel(String txt, EngineRendered ref) {
		super();
		labelText = txt;
		setReference(ref);
	}
	
	public SpatialTextLabel(String txt, Vector3d pos) {
		super();
		labelText = txt;
		refPos = pos;
	}
	
	
	public void setRefObj(EngineRendered obj) {
		setReference(obj);
	}
	
	public EngineRendered getRefObj() {
		return (EngineRendered)getReference();
	}
	
	public void setRefPos(Vector3d pos) {
		this.refPos = pos;
	}
	
	public Vector3d getRefPos() {
		return refPos;
	}
	
	public void setText(String t) {
		labelText = t;
		textChanged = true;
		//renderFlags |= GEOMETRY_CHANGE;
	}
	
	public String getText() {
		return labelText;
	}
	
	public void setFont(Font f) {
		font = f;
		fontChanged = true;
		//renderFlags |= GEOMETRY_CHANGE;
	}
	
	public Font getFont() {
		return font;
	}
	
	public void setBaseScale(double bs) {
		this.baseScale = bs;
		setScale(bs);
	}
	
	public void setScale(Vector3d s) {
		this.scale = s;
		renderFlags |= SCALE_CHANGE;
	}
	
	public void setScale(double s) {
		this.setScale(new Vector3d(s,s,s));
	}
	
	/* (non-Javadoc)
	 * @see teal.sim.properties.IsSpatial#nextSpatial()
	 */
	public void nextSpatial() {
		//System.out.println("Is this mess even being called?");
		Vector3d pos;
		if (refObj != null) {
			pos = new Vector3d(refObj.getPosition());
			pos.add(getPositionOffset());
			//System.out.println("useDirectionOFfset : " + useDirectionOffset);
			if (useDirectionOffset) {
				Vector3d dir = refObj.getDirection();
				//System.out.println("directionOFFSET: " + dir);
				dir.normalize();
				dir.scale(getRefDirectionOffset());
				if (refObj instanceof SpatialVector) {
					double vecScale = ((SpatialVector)refObj).getNode3D().getScale().length();
					dir.scale(vecScale);
					if ((theEngine.getEngineControl().getSimState() == TEngineControl.RUNNING ) && scaleWithObject) setScale(baseScale*vecScale); //*((SpatialVector)refObj).getArrowScale());
					//if (theEngine.getSimControl().getSimState() != EngineControl.NOT  && scaleWithObject) setScale(baseScale*vecScale*((SpatialVector)refObj).getArrowScale());
				}
				pos.add(dir);
			}
			
			if (!pos.equals(lastPosition)) {
				setPosition(pos);
				lastPosition.set(pos);
			}
		} else if (refPos != null) {
			
			if (!refPos.equals(getPosition())) setPosition(refPos);
		
		}
		
		//if (scaleWithObject) this.setScale(refObj.getNode3D().getScale());
	}
	
	public void forceUpdate() {
		Vector3d pos;
		if (refObj != null) {
			pos = new Vector3d(refObj.getPosition());
			pos.add(getPositionOffset());
			//System.out.println("useDirectionOFfset : " + useDirectionOffset);
			if (useDirectionOffset) {
				Vector3d dir = refObj.getDirection();
				//System.out.println("directionOFFSET: " + dir);
				dir.normalize();
				dir.scale(getRefDirectionOffset());
				if (refObj instanceof SpatialVector) {
					double vecScale = ((SpatialVector)refObj).getNode3D().getScale().length();
					dir.scale(vecScale);
					if (theEngine.getEngineControl().getSimState() != TEngineControl.NOT) {
						//System.out.println("INIT:  Updating label scale!");
						//setScale(vecScale*((SpatialVector)refObj).getArrowScale());
						//setScale(baseScale*vecScale*((SpatialVector)refObj).getArrowScale());
						setScale(baseScale);
					}
				}
				pos.add(dir);
			}
			
			
				setPosition(pos);
				lastPosition.set(pos);
			
		} else if (refPos != null) {
			
			if (!refPos.equals(getPosition())) setPosition(refPos);
		
		}
	}
	
	public void render() {
		if (textChanged) {
			((TextLabelNode)mNode).setText(labelText);
			textChanged = false;
		}
		if (fontChanged) {
			((TextLabelNode)mNode).setFont(font);
			fontChanged = false;
		}
		
		if ((renderFlags & SCALE_CHANGE) == SCALE_CHANGE) {
			mNode.setScale(scale);
			renderFlags ^= SCALE_CHANGE;
		}
		if ((renderFlags & COLOR_CHANGE) == COLOR_CHANGE) {
			((TextLabelNode)mNode).setColor(getColor());
		}
		
		super.render();
	}
	
	public TNode3D makeNode() {
		TextLabelNode node = new TextLabelNode();
		node.setText(labelText);
		node.setFont(font);
		//forceUpdate();
		return node;
	}
	
	/* (non-Javadoc)
	 * @see teal.core.HasReference#addReference(teal.core.Referenced)
	 */
	public void addReference(Referenced elm) {
		setReference(elm);
	}
	/* (non-Javadoc)
	 * @see teal.core.HasReference#getReference()
	 */
	public Referenced getReference() {
		return refObj;
	}
	/* (non-Javadoc)
	 * @see teal.core.HasReference#removeReference(teal.core.Referenced)
	 */
	public void removeReference(Referenced elm) {
		if ((elm != null) && (refObj != null) && (elm == refObj)) {
			refObj.removeReferent(this);
			refObj = null;
        }
	}
	/* (non-Javadoc)
	 * @see teal.core.HasReference#setReference(teal.core.Referenced)
	 */
	public void setReference(Referenced elm) {
		if (refObj != null) {
			refObj.removeReferent(this);
        }
		refObj = (EngineRendered)elm;
        if (elm != null) {
        	refObj.addReferent(this);
        }
	}

	/**
	 * @return Returns the positionOffset, which is the offset from the reference object's position that the label will
	 * appear.
	 */
	public Vector3d getPositionOffset() {
		return positionOffset;
	}
	/**
	 * @param offset The positionOffset to set, which is the offset from the reference object's position that the
	 * label will appear.
	 */
	public void setPositionOffset(Vector3d offset) {
		this.positionOffset = offset;
	}
	/**
	 * @return Returns the refDirectionOffset.  
	 */
	public double getRefDirectionOffset() {
		return refDirectionOffset;
	}
	/**
	 * @param refDirectionOffset The refDirectionOffset to set.
	 */
	public void setRefDirectionOffset(double refDirectionOffset) {
		this.refDirectionOffset = refDirectionOffset;
	}
	/**
	 * @return Returns the useDirectionOffset.
	 */
	public boolean getUseDirectionOffset() {
		return useDirectionOffset;
	}
	/**
	 * @param useDirectionOffset The useDirectionOffset to set.
	 */
	public void setUseDirectionOffset(boolean useDirectionOffset) {
		this.useDirectionOffset = useDirectionOffset;
	}
}
