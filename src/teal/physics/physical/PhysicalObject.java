/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PhysicalObject.java,v 1.104 2010/09/01 20:14:03 stefan Exp $ 
 * 
 */

package teal.physics.physical;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.*;

import javax.media.j3d.Transform3D;

import javax.swing.ImageIcon;
import javax.vecmath.*;

import teal.config.Teal;
import teal.core.TUpdatable;
import teal.render.BoundingSphere;
import teal.render.Bounds;
import teal.sim.collision.*;
import teal.sim.constraint.ArcConstraint;
import teal.sim.constraint.Constrainable;
import teal.sim.constraint.Constraint;
import teal.sim.constraint.SpringConstraint;
import teal.sim.engine.*;
import teal.sim.properties.*;

/**
 * PhysicalObject is the base class for all objects with physical properties that can be integrated.
 */
public class PhysicalObject extends EngineRendered implements PhysicalElement, HasMomentOfInertia, Constrainable,
    HasCollisionController, TUpdatable {

    private static final long serialVersionUID = 3257286915991613745L;

    protected HashMap<HasCollisionController,Vector3d> adherenceMap 
    	= new HashMap<HasCollisionController,Vector3d>();

    protected Vector3d velocity;
    protected double mass;
    protected Vector3d position_d;
    protected Vector3d velocity_d;
    /**
     * Position prior to collision.
     */
    protected Vector3d position_c;
    /**
     * Velocity prior to collision.
     */
    protected Vector3d velocity_c;
    protected double mass_d;

    //	static final double maxVel = 10.;

    /*
     * Rotation variables.
     */
    protected double momentOfInertia;
    protected double momentOfInertia_d;

    protected Quat4d orientation_d;
    protected Vector3d angularVelocity;
    protected Vector3d angularVelocity_d;

    protected Quat4d orientation_derivative;

    protected ImageIcon icon;

    protected boolean isIntegrating = true;

    public PhysicalObject() {
        super();
        mass = mass_d = 1;
        setMoveable(true);
        setPickable(true);
        position = new Vector3d();
        position_d = new Vector3d();
        position_c = new Vector3d();
        velocity = new Vector3d();
        velocity_d = new Vector3d();
        velocity_c = new Vector3d();
        setColor(Color.green);
        bounds = null;
        momentOfInertia = 1;
        momentOfInertia_d = 1;
        angularVelocity = new Vector3d();
        angularVelocity_d = new Vector3d();

        setRotable(true);
        orientation_d = new Quat4d(orientation);
        orientation_derivative = new Quat4d();

    }

    public PhysicalObject(double ms, Vector3d pos, Vector3d vel) {
        this();
        mass = mass_d = ms;
        position.set(pos);
        position_d.set(pos);
        position_c.set(pos);
        velocity.set(vel);
        velocity_d.set(vel);
        velocity_c.set(vel);
    }

    public PhysicalObject(double m, Vector3d pos, Vector3d vel, double moi) {
        this(m, pos, vel);
        momentOfInertia = moi;
        momentOfInertia_d = moi;

    }

    protected void createBounds() {
        bounds = new BoundingSphere(new Point3d(), Teal.PointChargeRadius);
    }

    public Bounds getBoundingArea() {
        if (bounds == null) {
            createBounds();
        }
        Transform3D trans = new Transform3D(orientation, position, 1.0);
        Bounds wrk = (Bounds) bounds.clone();
//        wrk.transform(trans);
        return wrk;
    }

    /* (non-Javadoc)
     * @see teal.sim.properties.HasMomentOfInertia#getMomentOfInertia()
     */
    public double getMomentOfInertia() {
        return this.momentOfInertia;
    }

    /* (non-Javadoc)
     * @see teal.sim.properties.HasMomentOfInertia#setMomentOfInertia(double)
     */
    public void setMomentOfInertia(double moi) {
        PropertyChangeEvent pce = new PropertyChangeEvent(this, "momentOfInertia", new Double(momentOfInertia),
            new Double(moi));
        momentOfInertia_d = moi;
        momentOfInertia = moi;
        firePropertyChange(pce);
    }

    public boolean isIntegrating() {
        return isIntegrating;
    }

    public void setIntegrating(boolean b) {
        isIntegrating = b;
    }

    // *****************************************************
    // Rotation-related Accessor Function	
    // *****************************************************

    /**
     * Sets the angular velocity of this object. 
     * 
     * @param aangularVelocity new angular velocity.
     */
    public void setAngularVelocity(Vector3d aangularVelocity) {
        angularVelocity = aangularVelocity;
        angularVelocity_d = aangularVelocity; // Relevant only for external
        // calls.
    }

    public void setRotation(Quat4d rot) {
        setRotation(rot, true);
    }

    public void setRotation(Matrix3d rot) {
        setRotation(rot, true);
    }

    /* (non-Javadoc)
     * @see teal.render.Rendered#setRotation(javax.vecmath.Quat4d, boolean)
     */
    public void setRotation(Quat4d rot, boolean sendPCE) {
        super.setRotation(rot, sendPCE);
        if (theEngine != null) {
            theEngine.requestSpatial();
            theEngine.requestRefresh();
            if (theEngine.getSimState() != EngineControl.RUNNING) {
                orientation_d.set(orientation);
            }
        } else {
            orientation_d.set(orientation);
        }
    }

    public void setRotation(Matrix3d rot, boolean sendPCE) {
        super.setRotation(rot, sendPCE);
        if (theEngine != null) {
            theEngine.requestSpatial();
            theEngine.requestRefresh();
            if (theEngine.getSimState() != EngineControl.RUNNING) {
                orientation_d.set(orientation);
            }
        } else {
            orientation_d.set(orientation);
        }
    }

    public void setDirection(Vector3d newDirection) {
        super.setDirection(newDirection);
        if (theEngine != null) {
            theEngine.requestSpatial();
            theEngine.requestRefresh();
            if (theEngine.getSimState() != EngineControl.RUNNING) {
                orientation_d.set(orientation);
            }
        } else {
            orientation_d.set(orientation);
        }
    }

    /**
     * Returns the orientation of this object as a Quat4d.
     * 
     * @return orientation as Quat4d.
     */
    public Quat4d getOrientation() {
        return orientation;
    }

    /* (non-Javadoc)
     * @see teal.sim.collision.HasCollisionController#getExternalForces()
     */
    public Vector3d getExternalForces() {

        Vector3d weight = new Vector3d(theEngine.getGravity());
        weight.scale(mass_d);
        Vector3d damping = new Vector3d(velocity_d);
        damping.scale(-theEngine.getDamping());
        Vector3d other = theEngine.getForces(this);
        Vector3d action = new Vector3d();
        action.add(weight);
        action.add(damping);
        action.add(other);

        return action;

    }

    ///////////////////////////////////////////////////

    /**
     * We have to appropriately augment the torque in actual derived classes.

     */
    protected Vector3d getTorque() {

        // here is a fix for damping, which now requires that super.getTorque() be added to
        // the specific torque values calculated in instances of PhysicalObject, as is done with
        // getExternalForces().
        //
        // May need to use a different damping coefficient for rotation?
        //

        Vector3d torque = new Vector3d();
        Vector3d damping = new Vector3d(angularVelocity_d);
        damping.scale(-theEngine.getDamping());

        torque.add(damping);

        return torque;
    }

    /**

     * get_dQdT returns dq/dt, where q represents the orientation in quaternion form.
     * This quantity is used in getDependentDerivatives. 
     */
    protected Quat4d get_dQdT() {

        Vector3d w = angularVelocity_d;
        Quat4d dQdT = new Quat4d();
        // It seems that setting the values in the constructor itself is causing a
        // problem, especially when they're all zeros. Though that would be a valid
        // case, the constructor probably is trying to normalize the quaternion,
        // resulting in NaN values. Calling the set method solves the problem.
        dQdT.set(w.x, w.y, w.z, 0);
        dQdT.mul(orientation_d);
        dQdT.scale(0.5);
        return dQdT;

    }

    /*
     // This is the old get_dQdT code. The inertia operator was handled by the
     // directionControl, which is probably unacceptable. The new model might
     // not be doing a better job in the sense that there are no "operators"
     // as such. Instead, the moment of inertia is explicitly used when we are
     // in getDependentDerivatives.

     protected Tuple4d get_dQdT(){
     Matrix3d inertia=directionControl.getInvertedOperatorOfInertia();
     Quat4d quat = directionControl.getQuaternion();
     Vector3d u=new Vector3d(quat.x,quat.y,quat.z);
     Vector3d w=new Vector3d();
     inertia.transform(angularMomentum_d,w);
     Vector4d dQdt=new Vector4d();
     Vector3d tempcross=new Vector3d();
     tempcross.cross(w,u);
     Vector3d temp=new Vector3d();
     temp.set(w);
     temp.scale(quat.w);
     temp.add(tempcross);
     dQdt.w=-w.dot(u);
     dQdt.x=temp.x;
     dQdt.y=temp.y;
     dQdt.z=temp.z;
     dQdt.scale(0.5);
     return dQdt;
     }
     */

    /**
     * this_getNumberDependentValues() is to avoid calling getNumberDependentValues()
     * and ending up calling the one of the most derived subclass.
     * 
     * @deprecated
     */
    public int this_getNumberDependentValues() {
        int size = 0;
        if (isMoveable()) {
            size += 6;
        }
        if (isRotable()) {
            size += 7;
        }
        return size;
    }

    public int getNumberDependentValues() {
        int size = 0;
        if (isMoveable()) {
            size += 6;
        }
        if (isRotable()) {
            size += 7;
        }
        return size;
    }

    /**
     * The Dependent Variables of a Rotating Object are position,velocity and rotation.
     * We return them in an array, broken down into x, y, and z.
     *
     * We pack x, y, z of position, then x, y, z, of velocity.  This
     * order matters in setDependentValues.
     */
    public void getDependentValues(double[] depValues, int offset) {
        int size = this_getNumberDependentValues();
        
        int i = offset;
        if (isMoveable()) {
            depValues[i++] = position_d.x;
            depValues[i++] = position_d.y;
            depValues[i++] = position_d.z;
            depValues[i++] = velocity_d.x;
            depValues[i++] = velocity_d.y;
            depValues[i++] = velocity_d.z;
        }
        if (isRotable()) {
            depValues[i++] = orientation_d.x;
            depValues[i++] = orientation_d.y;
            depValues[i++] = orientation_d.z;
            depValues[i++] = orientation_d.w;
            depValues[i++] = angularVelocity_d.x;
            depValues[i++] = angularVelocity_d.y;
            depValues[i++] = angularVelocity_d.z;

            orientation_derivative.set(orientation);
            orientation_derivative.sub(orientation_d);
            orientation_derivative.scale(1. / theEngine.getDeltaTime());
        }
    }

    /**
     * The dependent variables of a rotating object are position,velocity and rotation
     * We unpack x, y, and z for position, then velocity.  This order
     * must mirror the one we used to pack these values in getDependentValues
     *
     * RungeKutta4.integrate(Integratable) will invoke this method on us
     * when it has determined new values for our dependent variables.
     */

    public void setDependentValues(double[] newValues, int pos) {

        int idx = pos;
        if (isMoveable()) {
            position_d.x = newValues[idx++];
            position_d.y = newValues[idx++];
            position_d.z = newValues[idx++];
            velocity_d.x = newValues[idx++];
            velocity_d.y = newValues[idx++];
            velocity_d.z = newValues[idx++];
            
//      	 CONSTRAINING VELOCITY
            //velocity_d = constrainVelocity(new Vector3d(velocity_d));
        }
        if (isRotable()) {
            orientation_d.x = newValues[idx++];
            orientation_d.y = newValues[idx++];
            orientation_d.z = newValues[idx++];
            orientation_d.w = newValues[idx++];
            angularVelocity_d.x = newValues[idx++];
            angularVelocity_d.y = newValues[idx++];
            angularVelocity_d.z = newValues[idx++];
            
            // Orientation is, by definition, a normalized quaternion. Its evolution
            // is governed by a quaternion derivative (dQ_dT), obtained from angular
            // velocity. However, due to numerical precision, the outcome might not be
            // exactly normalized. Usually, the consequences are minimal, but we
            // encountered a scaling problem at the node level. That is why, we decided
            // to manually normalize orientation_d in the first place.
            orientation_d.normalize();
        }

    }
    
   // private Vector3d constrainVelocity(Vector3d vel) {
   //	return constraint.getReaction2(position_d, vel, new Vector3d(), mass_d);
   // }

    public Vector3d getTotalForces() {
        Vector3d action = getExternalForces();
        // Constraint handling.
        Vector3d total = new Vector3d(action);
        total.add(getReactionDueToAll());
        
        /*
        //      HACKAGE HACKAGE!!!!  DELETE THIS LATER
        Vector3d reaction2 = constraint2.getReaction(position_d, velocity_d, total, mass_d);
        
        
        total.add(reaction2);
        if (is_constrained && constraint != null) {
            Vector3d reaction = constraint.getReaction(position_d, velocity_d, total, mass_d);
            total.add(reaction);
        }
        */
        
        // New constraint implementation:
        // Note that some thought needs to be put in to the order in which constraints are added to the list.
        if (is_constrained && !constraints.isEmpty()) {
        	Iterator<Constraint> i = constraints.iterator();
        	while (i.hasNext()) {
        		Vector3d reaction = ((Constraint)i.next()).getReaction(position_d, velocity_d, total, mass_d);
        		total.add(reaction);
        	}
        }

        
        return total;
    }

    public void getDependentDerivatives(double[] depDerivatives, int offset, double time) {
        int idx = offset;
        if (isMoveable()) {
            Vector3d force = getTotalForces();

            depDerivatives[idx++] = velocity_d.x;
            depDerivatives[idx++] = velocity_d.y;
            depDerivatives[idx++] = velocity_d.z;
            depDerivatives[idx++] = force.x / mass_d;
            depDerivatives[idx++] = force.y / mass_d;
            depDerivatives[idx++] = force.z / mass_d;
        }
        if (isRotable()) {
            Vector3d T = getTorque();
            Quat4d dQdt = get_dQdT();

            depDerivatives[idx++] = dQdt.x + orientation_derivative.x;
            depDerivatives[idx++] = dQdt.y + orientation_derivative.y;
            depDerivatives[idx++] = dQdt.z + orientation_derivative.z;
            depDerivatives[idx++] = dQdt.w + orientation_derivative.w;
            depDerivatives[idx++] = T.x / momentOfInertia_d;
            depDerivatives[idx++] = T.y / momentOfInertia_d;
            depDerivatives[idx++] = T.z / momentOfInertia_d;

        }
    }

    ////////////////////////////////////////////

    /**
     * Returns the independent integration value (otherwise known as time).
     * 
     * @return timestep of simulation.
     */
    public double getIndependentValue() {
        return theEngine.getDeltaTime();
    }

    public double getMass() {
        return this.mass;
    }

    public void setMass(double mass) {
        Double old = new Double(this.mass);
        this.mass = mass;
        mass_d = mass;
        firePropertyChange("mass", old, new Double(mass));

    }

    public void setPosition(Vector3d pos, boolean sendPC) {
        // Very special hack, that should be generalized to all types of constraint.
    	if (is_constrained) {
        	// Ideally: constraint.adjustPosition(pos);
        	Iterator it = constraints.iterator();
        	while (it.hasNext()) {
            //if (constraint instanceof ArcConstraint) {
        		Constraint c = (Constraint)it.next();
	        	if (c instanceof ArcConstraint) {
	            	ArcConstraint ac = (ArcConstraint) c;
	                Vector3d center = ac.getCenter();
	                Vector3d normal = ac.getNormal();
	                double radius = ac.getRadius();
	                Vector3d temp = new Vector3d();
	                temp.set(pos);
	                temp.sub(center);
	                normal.scale(normal.dot(temp));
	                temp.sub(normal);
	                temp.normalize();
	                temp.scale(radius);
	                temp.add(center);
	                pos.set(temp);
	                
	                Vector3d tt = new Vector3d(pos);
	                tt.sub(center);
	                Vector3d n = ac.getNormal();
	                Vector3d v = new Vector3d();
	                v.cross(n,tt);
	                v.normalize();
	                double veldotv = velocity_d.dot(v);
	                v.scale(veldotv);
	                velocity_d.set(v);
	                
	            }
        	}
        }
    	
    	position_d.set(pos);
        position_c.set(pos);
        super.setPosition(pos, sendPC);
        if (theEngine != null) {
            theEngine.requestSpatial();
            theEngine.requestRefresh();
        }
    }

    /* (non-Javadoc)
     * @see teal.sim.properties.HasVelocity#getVelocity()
     */
    public Vector3d getVelocity() {
        return this.velocity;
    }

    public void setVelocity(Vector3d vel) {
        Vector3d old = new Vector3d(velocity);
        velocity.set(vel);
        velocity_d.set(vel);
        firePropertyChange("velocity", old, new Vector3d(vel));
    }

    public void setBoundingArea(Bounds ba) {
        Bounds old = getBoundingArea();
        this.bounds = ba;
        firePropertyChange("boundingArea", old, bounds);
    }

    public void update() {
        if (mass != mass_d) setMass(mass_d);
        if (!position.equals(position_d)) setPosition(position_d, true);
        if (!velocity.equals(velocity_d)) setVelocity(velocity_d);
        if (angularVelocity != angularVelocity_d) setAngularVelocity(angularVelocity_d);
        if (momentOfInertia != momentOfInertia_d) setMomentOfInertia(momentOfInertia_d);
        if (!orientation.equals(orientation_d)) {
            setRotation(orientation_d);
        }
        
        updateCollision();
    }

    public void checkImpulse() {
    }

    public String toString() {
        return ("PhysicalObject:" + id);
    }

    // *****************************************************
    // Constraint-related Section	
    // *****************************************************

    protected boolean is_constrained = false;
    protected Constraint constraint = null;
    
    protected ArrayList<Constraint> constraints = new ArrayList<Constraint>();
    
    // HACKAGE
    protected Constraint constraint2 = null;
    public void setConstraint2(Constraint c) {
    	constraint2 = c;
    }

    public void addConstraint(Constraint c) {
    	constraints.add(c);
    }
    
    public ArrayList<Constraint> getConstraints() {
    	return constraints;
    }
    
    public void clearConstraints() {
    	constraints.clear();
    }
    
    public Constraint getConstraintAtIndex(int index) {
    	return (Constraint)constraints.get(index);
    }
    
    public void removeConstraintAtIndex(int index) {
    	constraints.remove(index);
    }
    
    public int getNumConstraints() {
    	return constraints.size();
    }
    
    public boolean isConstrained() {
        return is_constrained;
    }

    public void setConstrained(boolean x) {
        is_constrained = x;
    }

    public void setConstraint(Constraint c) {
        //constraint = c;
    	constraints.clear();
    	constraints.add(c);
    }

    public Constraint getConstraint() {
        //return constraint;
    	// This needs to be redefined slightly.
    	if (!constraints.isEmpty()) {
    		return (Constraint) constraints.get(0);
    	} else {
    		return null;
    	}
    }

    // *****************************************************
    // Collision-related Section	
    // *****************************************************

    protected boolean is_colliding = false;
    protected CollisionController collisionController = null;

    public boolean isColliding() {
        return is_colliding;
    }

    public void setColliding(boolean x) {
        is_colliding = x;
    }

    public CollisionController getCollisionController() {
        return collisionController;
        //		Incorrect use of replica, because the outsider will need to apply settings on this.
        //		return collisionController.replica();
    }

    public void setCollisionController(CollisionController cg) {
        //		Correct use of replica, we don't want the inside controller to change with the outside element.
        collisionController = cg.replica();
    }

    public Vector3d getPosition1() {
        return new Vector3d(position_c);
    }

    public Vector3d getVelocity1() {
        return new Vector3d(velocity_c);
    }

    public Vector3d getPosition2() {
        return new Vector3d(position_d);
    }

    public Vector3d getVelocity2() {
        return new Vector3d(velocity_d);
    }

    public void updateCollision() {
        position_c = position_d;
        velocity_c = velocity_d;
    }

    /* (non-Javadoc)
     * @see teal.sim.collision.HasCollisionController#applyImpulse(javax.vecmath.Vector3d)
     */
    public void applyImpulse(Vector3d impulse) {
        if (is_constrained && constraint != null) {
            Vector3d reaction = constraint.getReaction(position_d, new Vector3d(), impulse, mass_d);
            impulse.add(reaction);
        }
        velocity_d.scaleAdd(1 / mass, impulse, velocity_d);
    }

    public void applyCorrection(Vector3d correction) {
        if (is_constrained && constraint != null) {
            // The below is incorrect. The correct way to do this is to implement the
            // position correction  methods within the constraints themselves.
            Vector3d ineffective = constraint.getReaction(position_d, new Vector3d(), correction, mass_d);
            correction.add(ineffective);
        }
        position_d.add(correction);
    }

    public void addAdheredObject(HasCollisionController x) {
        if (!adherenceMap.containsKey(x)) {
            Vector3d reaction = new Vector3d();
            adherenceMap.put(x, reaction);
        }
    }

    public void removeAdheredObject(HasCollisionController x) {
        if (adherenceMap.containsKey(x)) {
            adherenceMap.remove(x);
        }
    }

    public boolean isAdheredTo(HasCollisionController x) {
        return adherenceMap.containsKey(x);
    }

    public Vector3d getReactionDueTo(HasCollisionController x) {
        // Assuming the key exists.
        return (Vector3d) adherenceMap.get(x);
    }

    public void setReactionDueTo(HasCollisionController x, Vector3d reaction) {
        // Assuming the key exists.
        ((Vector3d) adherenceMap.get(x)).set(reaction);
    }

    public Vector3d getReactionDueToAllExcept(HasCollisionController x) {
        Iterator it = adherenceMap.entrySet().iterator();
        Vector3d total = new Vector3d();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getKey() != x) {
                total.add((Vector3d) entry.getValue());
            }
        }
        return total;
    }

    public Vector3d getReactionDueToAll() {
        Iterator it = adherenceMap.values().iterator();
        Vector3d total = new Vector3d();
        while (it.hasNext()) {
            total.add((Vector3d) it.next());
        }
        return total;
    }

    public boolean solveReactionStep() {
        if (adherenceMap.size() == 0) return true;
        boolean converged = true;
        Vector3d error = null;
        double tol = 1e-2;

        // Reaction computation on object x.
        HasCollisionController x = this;
        SphereCollisionController xcg = null;
        // The reaction computation below is specific to sphere collisions.
        // Is our controller a sphere collision controller?
        try {
            xcg = (SphereCollisionController) x.getCollisionController();
        } catch (ClassCastException e) {
            return true;
        }
        Vector3d FX = x.getExternalForces();
        double mx = x.getMass();

        Iterator it = adherenceMap.keySet().iterator();
        while (it.hasNext()) {

            // Iterate through each object y in the neighborhood of x.
            HasCollisionController y = (HasCollisionController) it.next();
            CollisionController ycg = y.getCollisionController();
            Vector3d FY = y.getExternalForces();
            double my = y.getMass();

            // Compute line of action unit vector.
            Vector3d u = xcg.touchDirection(ycg);

            // Compute net force on x, except the reaction due to y.
            Vector3d FXnet = new Vector3d(FX);
            Vector3d FRx_except_y = x.getReactionDueToAllExcept(y);
            FXnet.add(FRx_except_y);

            if (ycg instanceof WallCollisionController) {
                // Case: Wall
                // This is a special case of the Sphere case below, once one
                // takes into account that the wall mass, my, is infinite.

                // Are we performing wall-sphere collisions?
                if ((xcg.getMode() & SphereCollisionController.WALL_SPHERE) == 0) continue;

                // Compute the reaction on x due to y.
                FXnet.scale(-1.);
                Vector3d FRx_dueto_y = new Vector3d(FXnet);
                double magnitude = FRx_dueto_y.dot(u);
                if (magnitude < 0.) {
                    FRx_dueto_y.set(u);
                    FRx_dueto_y.scale(magnitude);
                    // Compute error and update the reacton on x due to y.
                    // (Note: the error computation below overwrites the reaction.)
                    error = x.getReactionDueTo(y);
                    error.sub(FRx_dueto_y);
                    converged = (error.length() < tol) && converged;
                    x.setReactionDueTo(y, FRx_dueto_y);
                }
            } else {
                // Case: Sphere

                // Are we performing sphere-sphere collisions on both ends?
                SphereCollisionController ycg_ = null;
                try {
                    ycg_ = (SphereCollisionController) ycg;
                } catch (ClassCastException e) {
                    continue;
                }
                if ((xcg.getMode() & SphereCollisionController.SPHERE_SPHERE) == 0
                    || (ycg_.getMode() & SphereCollisionController.SPHERE_SPHERE) == 0) continue;

                // Compute net force on y, except the reaction due to x.
                Vector3d FYnet = new Vector3d(FY);
                Vector3d FRy_except_x = y.getReactionDueToAllExcept(x);
                FYnet.add(FRy_except_x);
                FXnet.scale(-my / (mx + my));
                FYnet.scale(+mx / (mx + my));
                // Compute the reaction on x due to y.			
                Vector3d FRx_dueto_y = new Vector3d(FXnet);
                FRx_dueto_y.add(FYnet);
                double magnitude = FRx_dueto_y.dot(u);
                if (magnitude < 0.) {
                    FRx_dueto_y.set(u);
                    FRx_dueto_y.scale(magnitude);
                    // Compute error and update the reacton on x due to y.
                    // (Note: the error computation below overwrites the reaction.)
                    error = x.getReactionDueTo(y);
                    error.sub(FRx_dueto_y);
                    converged = (error.length() < tol) && converged;
                    x.setReactionDueTo(y, FRx_dueto_y);
                    // Update the reaction on y due to x.
                    FRx_dueto_y.negate();
                    y.setReactionDueTo(x, FRx_dueto_y);
                }
            }

        }
        return converged;
    }

	public void setVx(double newX) {
		Vector3d old = new Vector3d(this.velocity);
		old.x = newX;
		setVelocity(old);	
	}

	public void setVy(double newY) {
		Vector3d old = new Vector3d(this.velocity);
		old.y = newY;
		setVelocity(old);	
	}

	public void setVz(double newZ) {
		Vector3d old = new Vector3d(this.velocity);
		old.z = newZ;
		setVelocity(old);	
	}

	public double getVx() {
		return this.velocity.x;
	}

	public double getVy() {
		return this.velocity.y;
	}

	public double getVz() {
		return this.velocity.z;
	}

}
