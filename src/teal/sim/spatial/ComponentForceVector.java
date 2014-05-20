/*
 * Created on Oct 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package teal.sim.spatial;

import javax.vecmath.Vector3d;

import teal.render.j3d.ArrowNode;
import teal.render.j3d.LineNode;
import teal.render.j3d.SolidArrowNode;
import teal.render.scene.TNode3D;
import teal.render.scene.TShapeNode;
import teal.physics.physical.PhysicalObject;
import teal.util.TDebug;

/**
 * Prototype for ComponentForceVector.  Extends ForceVector but measures component forces acting on the object rather than
 * total forces.  This is a prototype and may be somewhat of a hack at first.
 */
public class ComponentForceVector extends SpatialVector {
	
	private int type = 0;
	public static final int TYPE_GRAVITY = 0;
	public static final int TYPE_CONSTRAINT = 1;
	public static final int TYPE_EFIELD = 2;
	public static final int TYPE_BFIELD = 3;
	public static final int TYPE_DAMPING = 4;
	
	private int constraintIndex = -1;
	private boolean isUpdating = true;
	private boolean scaleByMagnitude = true;
	
	protected Vector3d lastValue = new Vector3d();
	
	public ComponentForceVector() {
		super();
		this.type = TYPE_GRAVITY;
	}
	
	public ComponentForceVector(PhysicalObject obj, int type) {
		super(obj);
		this.type = type;
	}
	
	public void setConstraintIndex(int index) {
		constraintIndex = index;
		type = TYPE_CONSTRAINT;
	}
	// There is a general problem here that seems to stem from the fact that nextSpatial() can be called before the 
	// simulation is fully initialized.
	public void nextSpatial() {
		//super.nextSpatial();
		if (isUpdating()) {
			switch (type) {
				case TYPE_GRAVITY:
					Vector3d g = object.getSimEngine().getGravity();
					g.scale(object.getMass());
					if (g != null) value.set(g);
					break;
				case TYPE_CONSTRAINT:
					if (constraintIndex >= 0 && object.getNumConstraints() > constraintIndex) {
						// This is a test.  The Constraint interface has been updated to include a method 
						// called getLastReaction(), which should return the last reaction calculated by that constraint.
						// Since nextSpatial() is called after the doDynamic() phase of world integration, this should 
						// deliver the appropriate value for the current "constraint force".
						Vector3d r = object.getConstraintAtIndex(constraintIndex).getLastReaction();
						if (r != null) value.set(r);
					} else {
						TDebug.println(0, "ERROR in ComponentForceVector: constraintIndex is invalid.");
					}
					break;
				case TYPE_DAMPING:
					Vector3d d = object.getVelocity2(); // This should return velocity_d, which is what I think I want here.
					d.scale(-object.getSimEngine().getDamping());
					if (d != null) value.set(d);
					break;
				case TYPE_EFIELD:
					// Implement later.
					break;
				case TYPE_BFIELD:
					// Implement later.
					break;
				default:
					// Implement later.
					break;
			}
			
			this.setPosition(object.getPosition());
			//value.set(0.,-1.,-0.);
			
			// Here we check against the previous value (lastValue) in an attempt to avoid redundant updates, since we
			// don't want to call setDirection if the direction isn't actually changing.  However, there is one caveat here:
			// any changes to the direction by the User are not reflected in the value of "value", so it won't correct 
			// itself in that case.  The only way to test for this is to check "value" against the current direction of the
			// arrow, which is another operation we don't want to do every step, and defeats the purpose of this 
			// optimization.
			//
			// One way out of this to retool this slightly so that user-generated calls to setDirection force an update
			// of "value", etc..  But for now let's just suggest that ComponentForceVectors should not be selectable
			// (or at least rotatable) while isUpdating() is true.  This is a reasonable suggestion anyways.
			if (value.length() > 0. && !value.equals(lastValue)) {
				//System.out.println("ComponentForceVector setting direction");
				
				this.setDirection(value);
				lastValue.set(value);
			}
			//System.out.println("value = " + value);
			
			registerRenderFlag(GEOMETRY_CHANGE);
			//System.out.println("Object position: " + object.getPosition());
			//System.out.println("Object direction: " + this.getDirection());
			//System.out.println("Object rotation: " + this.getRotation());
		}
	
    }
	
	public void forceUpdate() {
		switch (type) {
		case TYPE_GRAVITY:
			Vector3d g = object.getSimEngine().getGravity();
			g.scale(object.getMass());
			if (g != null) value.set(g);
			break;
		case TYPE_CONSTRAINT:
			if (constraintIndex >= 0 && object.getNumConstraints() > constraintIndex) {
				// This is a test.  The Constraint interface has been updated to include a method 
				// called getLastReaction(), which should return the last reaction calculated by that constraint.
				// Since nextSpatial() is called after the doDynamic() phase of world integration, this should 
				// deliver the appropriate value for the current "constraint force".
				Vector3d r = object.getConstraintAtIndex(constraintIndex).getLastReaction();
				if (r != null) value.set(r);
			} else {
				TDebug.println(0, "ERROR in ComponentForceVector: constraintIndex is invalid.");
			}
			break;
		case TYPE_DAMPING:
			Vector3d d = object.getVelocity2(); // This should return velocity_d, which is what I think I want here.
			d.scale(-object.getSimEngine().getDamping());
			if (d != null) value.set(d);
			break;
		case TYPE_EFIELD:
			// Implement later.
			break;
		case TYPE_BFIELD:
			// Implement later.
			break;
		default:
			// Implement later.
			break;
		}
	
		this.setPosition(object.getPosition());
		this.setDirection(value);
		lastValue.set(value);

		//System.out.println("value = " + value);
	
		registerRenderFlag(GEOMETRY_CHANGE);
	}
	
	
//	protected TNode3D makeNode() {
//        SolidArrowNode node = new SolidArrowNode( );
//        ((SolidArrowNode) node).setGeometry(teal.render.j3d.geometry.Cylinder.makeGeometry(20, 0.05, 1, 0.5)
//                .getIndexedGeometryArray(true));
//        //ArrowNode node = new ArrowNode();
//        node.setPickable(false);
//        node.setVisible(true);
//        node.setColor(getColor());
//        updateNode3D(node);
//        return node;
//    }
	
//	@Override
//	protected void updateNode3D(LineNode node) {
//        if ((node == null) || (theEngine == null)) {
//            return;
//        }
//        Vector3d vector = new Vector3d(value);
//        
//        if (isScaleByMagnitude()) {
//            //node.setScale(new Vector3d(1.,value.length()*arrowScale,1.));
//            node.setScale(value.length()*arrowScale);
//        } else {
//            //node.setScale(new Vector3d(1.,arrowScale,1.));
//            node.setScale(arrowScale);
//        }
//        
//        //node.setPosition(object.getPosition());
//        //if (vector.length() > 0) {
//        //    node.setDirection(new Vector3d(vector));
//        //}
//        //System.out.println("Node position: " + node.getPosition());
//        //System.out.println("Node rotation: " + node.getRotation());
//        //System.out.println("ComponentForceVector::UpdateNode3D()");
//    }
	/**
	 * @return Returns the isUpdating.
	 */
	public boolean isUpdating() {
		return isUpdating;
	}
	/**
	 * @param isUpdating The isUpdating to set.
	 */
	public void setUpdating(boolean isUpdating) {
		this.isUpdating = isUpdating;
		if (isUpdating) forceUpdate();
	}
	
	/**
	 * @return Returns the scaleByMagnitude.
	 */
	public boolean isScaleByMagnitude() {
		return scaleByMagnitude;
	}
	/**
	 * @param scaleByMagnitude The scaleByMagnitude to set.
	 */
	public void setScaleByMagnitude(boolean scaleByMagnitude) {
		this.scaleByMagnitude = scaleByMagnitude;
	}
}
