/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: RigidBar.java,v 1.23 2010/08/10 18:12:33 stefan Exp $ 
 * 
 */

package teal.physics.mech;

import java.awt.*;
import java.beans.*;

import javax.vecmath.*;

import teal.config.*;
import teal.render.j3d.*;
import teal.render.j3d.geometry.Cylinder;
import teal.render.j3d.geometry.Sphere;
import teal.render.scene.*;
import teal.sim.constraint.*;
import teal.sim.properties.HasFromTo;
import teal.sim.properties.HasLength;
import teal.sim.properties.HasRadius;
import teal.physics.physical.*;

public class RigidBar extends PhysicalObject implements HasLength, HasRadius {

    private static final long serialVersionUID = 3688503298856727095L;

    private Boolean state = new Boolean(false);

    protected double length = 1.;
    protected double radius = 0.1;

    double pivot = 0;
    Vector3d axis = new Vector3d(0, 0, 1);
    double force1 = 10, force2 = 10, angle1 = Math.PI / 4, angle2 = -3 * Math.PI / 4, L1 = 1, L2 = -1;

    private Vector3d direction = null;

    public RigidBar(Vector3d pposition, double ppivot, Vector3d aaxis, double llength, double rradius) {
        super();
        mMaterial.setDiffuse(Color.BLUE);
        mMaterial.setTransparancy(0.5f);
        setPosition(pposition);
        setLength(llength);
        setPivot(ppivot);
        setAxis(aaxis);
        setArbitraryDirection(new Vector3d(1, 0, 0));
        setRadius(rradius);
        setConstrained(true);
        setConstraint(new PivotConstraint());
    }

    public void setLength(double llength) {
        llength = Math.abs(llength);
        if (mNode != null) {
            ((Node3D) mNode).setScale(llength / length);
        }
        length = llength;
        setPivot(pivot); // Insures pivot within length.
        barUpdate((ArrayNode) mNode);
    }

    public double getLength() {
        return length;
    }

    public void setAxis(Vector3d aaxis) {
        axis = new Vector3d(aaxis);
    }

    public Vector3d getAxis() {
        return axis;
    }

    public void setPivot(double ppivot) {
        if (Math.abs(ppivot) > length / 2.) ppivot = (ppivot > 0 ? 1 : -1) * length / 2.;
        pivot = ppivot;
        setMass(getMass()); // Updates moment of inertia.
        pivotBallUpdate((ArrayNode) mNode);
        weightArrowUpdate((ArrayNode) mNode);
        force1ArrowUpdate((ArrayNode) mNode);
        force2ArrowUpdate((ArrayNode) mNode);
    }

    public void setPosition(Vector3d pposition) {
        super.setPosition(pposition);
        setMass(getMass()); // Updates moment of inertia.
        if (mNode != null) {
            mNode.setPosition(position);
        }
        weightArrowUpdate((ArrayNode) mNode);
        pivotBallUpdate((ArrayNode) mNode);
    }

    public void setArbitraryDirection(Vector3d ddirection) {
        setDirection(new Vector3d(ddirection));
        direction = new Vector3d(ddirection);
        direction.normalize();
        weightArrowUpdate((ArrayNode) mNode);
        pivotBallUpdate((ArrayNode) mNode);
    }

    public double getPivot() {
        return pivot;
    }

    public void setRadius(double rradius) {
        radius = rradius;
    }

    public double getRadius() {
        return radius;
    }

    // ****************************************************************
    // Rigid Bar Property Accessor Methods
    // ****************************************************************
    public double getForce1Position() {
        return L1;
    }

    public void setForce1Position(double position) {
        L1 = position;
        force1ArrowUpdate((ArrayNode) mNode);
        pivotBallUpdate((ArrayNode) mNode);
    }

    public double getForce2Position() {
        return L2;
    }

    public void setForce2Position(double position) {
        L2 = position;
        force2ArrowUpdate((ArrayNode) mNode);
        pivotBallUpdate((ArrayNode) mNode);
    }

    public double getForce1Magnitude() {
        return force1;
    }

    public void setForce1Magnitude(double force) {
        force1 = force;
        force1ArrowUpdate((ArrayNode) mNode);
        pivotBallUpdate((ArrayNode) mNode);
    }

    public double getForce2Magnitude() {
        return force2;
    }

    public void setForce2Magnitude(double force) {
        force2 = force;
        force2ArrowUpdate((ArrayNode) mNode);
        pivotBallUpdate((ArrayNode) mNode);
    }

    public double getForce1Angle() {
        return angle1;
    }

    public void setForce1Angle(double angle) {
        angle1 = angle;
        force1ArrowUpdate((ArrayNode) mNode);
        pivotBallUpdate((ArrayNode) mNode);
    }

    public double getForce2Angle() {
        return angle2;
    }

    public void setForce2Angle(double angle) {
        angle2 = angle;
        force2ArrowUpdate((ArrayNode) mNode);
        pivotBallUpdate((ArrayNode) mNode);
    }

    // ****************************************************************
    // External Rotation Application (Used for initial angle setting.)
    // ****************************************************************
    public double getAngle() {
        double angle = getDirection().angle(new Vector3d(1, 0, 0));
        angle *= Math.sin(getDirection().angle(new Vector3d(0, 1, 0))) > 0 ? 1 : -1;
        return angle;
    }

    public void setAngle(double angle) {
        Vector3d old_direction = new Vector3d(direction);
        Vector3d new_direction = new Vector3d(Math.cos(angle), Math.sin(angle), 0);
        setArbitraryDirection(new_direction);

        // Position change ritual, just in update.
        // This needs to be integrated in a more formal way, and the issue falls
        // under how rotational and translational components should interact.
        Vector3d increment = new Vector3d(new_direction);
        increment.sub(old_direction);
        increment.scale(-pivot);
        Vector3d new_position = new Vector3d(position);
        new_position.add(increment);
        PropertyChangeEvent pce = new PropertyChangeEvent(this, "position", position, new_position);
        setPosition(new_position); // Updates graphics internally.
        position_d.set(new_position);
        firePropertyChange(pce);
    }

    protected Vector3d getTorque() {
        // ****************************************************************
        // Dynamic (shadow) direction information retrieval.
        // ****************************************************************
        Vector3d direction_d = new Vector3d(initialDirection);
        Matrix3d rotation_d = new Matrix3d();
        rotation_d.set(orientation_d);
        rotation_d.transform(direction_d);
        direction_d.normalize();
        Vector3d L1_ = new Vector3d(direction_d);
        L1_.scale(L1 - pivot);
        Vector3d L2_ = new Vector3d(direction_d);
        L2_.scale(L2 - pivot);

        // ****************************************************************
        // Effective force components.
        // ****************************************************************
        Vector3d F1_y = new Vector3d();
        F1_y.cross(axis, direction_d);
        if (F1_y.length() > 100 * Teal.MachineEpsilon) F1_y.normalize();
        F1_y.scale(force1 * Math.sin(angle1));

        Vector3d F2_y = new Vector3d();
        F2_y.cross(axis, direction_d);
        if (F2_y.length() > 100 * Teal.MachineEpsilon) F2_y.normalize();
        F2_y.scale(force2 * Math.sin(angle2));

        // ****************************************************************
        // Torques due to the acting forces.
        // ****************************************************************
        Vector3d torqueVector1 = new Vector3d();
        torqueVector1.cross(L1_, F1_y);
        Vector3d torqueVector2 = new Vector3d();
        torqueVector2.cross(L2_, F2_y);

        // ****************************************************************
        // Torque due to the weight of the bar.
        // ****************************************************************
        direction_d.scale(-pivot);
        Vector3d F = new Vector3d(getExternalForces());
        Vector3d torqueVector3 = new Vector3d();
        torqueVector3.cross(direction_d, F);

        //		System.out.println( "Force1: " + F1_y.length() );
        //		System.out.println( "Torque due to force1: " + torqueVector1.dot(axis) );
        //		System.out.println( "Force2: " + F2_y.length() );
        //		System.out.println( "Torque due to force2: " + torqueVector2.dot(axis) );
        //		System.out.println( "Weight: " + F.length() );
        //		System.out.println( "Torque due to weight: " + torqueVector3.dot(axis) );

        // ****************************************************************
        // Total torque.
        // ****************************************************************
        double totalTorque = torqueVector1.dot(axis) + torqueVector2.dot(axis) + torqueVector3.dot(axis);
        Vector3d totalTorqueVector = new Vector3d(axis);
        totalTorqueVector.normalize();
        totalTorqueVector.scale(totalTorque);

        return totalTorqueVector;
    }

    protected TNode3D makeNode() {
        TNode3D node = (TNode3D) new ArrayNode();
        updateNode3D(node);
        return node;
    }

    public void updateNode3D(TNode3D nodeArray) {
        barUpdate(nodeArray);
        pivotBallUpdate(nodeArray);
        weightArrowUpdate(nodeArray);
        force1ArrowUpdate(nodeArray);
        force2ArrowUpdate(nodeArray);

        //		Possible arrow manipulations.
        //		arrow.setAppearance(Node.makeAppearance(Colors.fieldValueMainColor));
        //		arrow.setTransform(Teal.defaultOrigin,fieldValue,fieldValue.length() * Teal.fieldValueLength/lengthRef);
        //		arrow.setPosition(Teal.defaultOrigin);
        //		arrow.setDirection(fieldValue);
        //		arrow.setScale(fieldValue.length() * Teal.fieldValueLength/lengthRef);
    }

    TShapeNode B = null;

    private void barUpdate(TNode3D node) {
        if (!(node instanceof TArrayNode)) return;
        if (B == null) {
            B = (TShapeNode) new ShapeNode();
            B.setGeometry(Cylinder.makeGeometry(16, radius, 1));
            B.setMaterial(mMaterial);
            //B.setColor(TealMaterial.getColor3f(mMaterial.getDiffuse()));
            //B.setShininess(mMaterial.getShininess());
            //B.setTransparency(mMaterial.getTransparancy());
            // Appearance:
            //Appearance appearance = Node3D.makeAppearance(mColor,0.f,0.5f,false);
            //			appearance.setTransparencyAttributes(
            //				new TransparencyAttributes(TransparencyAttributes.FASTEST, 0.f) );
            //	B.setAppearance(appearance);
            B.setPickable(false);
            B.setVisible(true);
            ((TArrayNode) node).addNode(B);
        }
        // ****************************************************************
        // Bar 
        // ****************************************************************
        Vector3d scale = new Vector3d(1, length, 1);
        B.setScale(scale);
    }

    TShapeNode P = null;
    TShapeNode T = null;

    private void pivotBallUpdate(TNode3D node) {
        if (!(node instanceof TArrayNode)) return;
        if (P == null) {
            P = (TShapeNode) new ShapeNode();
            P.setGeometry(Sphere.makeGeometry(16, radius * 2));
            P.setColor(new Color3f(Color.GRAY));
            P.setShininess(0f);
            P.setTransparency(0.5f);
            //Appearance app = Node3D.makeAppearance(Color.GRAY,0.f,0.5f,false);
            //P.setAppearance(app);
            P.setPickable(false);
            P.setVisible(true);
            ((TArrayNode) node).addNode(P);
        }
        // ****************************************************************
        // Pivot Ball
        // ****************************************************************
        Vector3d piv_pos = new Vector3d(0, pivot, 0);
        P.setPosition(piv_pos);

        if (T == null) {
            T = (TShapeNode) new ArrowNode();
            T.setPickable(false);
            T.setColor(new Color3f(Color.GRAY));
            T.setVisible(true);
            ((TArrayNode) node).addNode(T);
        }
        // Torque-to-length scale, in m/(m x N)
        double torque_sc = 0.1;
        // ****************************************************************
        // Torque Arrow
        // ****************************************************************
        double torque = torque_sc * axis.dot(getTorque());
        Vector3d axisVector = new Vector3d(axis);
        axisVector.scale(torque);
        axisVector.add(new Vector3d(0, pivot, 0));
        ((HasFromTo) T).setFromTo(new Vector3d(0, pivot, 0), axisVector);

        // ****************************************************************
        // Pivot Force Arrow (separate method, for clarity).
        // ****************************************************************
        pivotForceArrowUpdate(node);
    }

    ArrowNode F = null;

    private void weightArrowUpdate(TNode3D node) {
        if (!(node instanceof TArrayNode)) return;
        if (F == null) {
            F = new ArrowNode();
            F.setPickable(false);
            F.setColor(new Color3f(Color.BLUE.brighter()));
            ((TArrayNode) node).addNode(F);
        }
        // Force-to-length scale, in m/N.
        double force_sc = 0.1;
        // ****************************************************************
        // Weight Arrow
        // ****************************************************************
        if (Math.abs(pivot) > 100 * Teal.MachineEpsilon) {
            Vector3d tail = new Vector3d(0, 0, 0);
            double force = theEngine.getGravity().length() * mass * force_sc;
            Vector3d tip = new Vector3d(0, -force, 0);
            Matrix3d rotation = new Matrix3d();
            rotation.set(orientation_d);
            rotation.invert();
            rotation.transform(tip);
            F.setFromTo(tail, tip);
            F.setVisible(true);
        } else {
            F.setVisible(false);
        }
    }

    TShapeNode F1 = null;

    private void force1ArrowUpdate(TNode3D node) {
        if (!(node instanceof TArrayNode)) return;
        if (F1 == null) {
            F1 = (TShapeNode) new ArrowNode();
            F1.setPickable(false);
            F1.setColor(new Color3f(Color.RED));
            ((TArrayNode) node).addNode(F1);
        }
        // Force-to-length scale, in m/N.
        double force_sc = 0.1;
        // ****************************************************************
        // Force1 Arrow
        // ****************************************************************
        if (Math.abs(L1 - pivot) > 100 * Teal.MachineEpsilon) {
            Vector3d tail1 = new Vector3d(0, L1, 0);
            Vector3d tip1 = new Vector3d(-force1 * force_sc * Math.sin(angle1), force1 * force_sc * Math.cos(angle1), 0);
            tip1.add(tail1);
            ((HasFromTo) F1).setFromTo(tail1, tip1);
            F1.setVisible(true);
        } else {
            F1.setVisible(false);
        }
    }

    TShapeNode F2 = null;

    private void force2ArrowUpdate(TNode3D node) {
        if (!(node instanceof TArrayNode)) return;
        if (F2 == null) {
            F2 = (TShapeNode) new ArrowNode();
            F2.setPickable(false);
            F2.setColor(new Color3f(Color.GREEN));
            ((TArrayNode) node).addNode(F2);
        }
        // Force-to-length scale, in m/N.
        double force_sc = 0.1;
        // ****************************************************************
        // Force2 Arrow
        // ****************************************************************
        if (Math.abs(L2 - pivot) > 100 * Teal.MachineEpsilon) {
            Vector3d tail2 = new Vector3d(0, L2, 0);
            Vector3d tip2 = new Vector3d(-force2 * force_sc * Math.sin(angle2), force2 * force_sc * Math.cos(angle2), 0);
            tip2.add(tail2);
            ((HasFromTo) F2).setFromTo(tail2, tip2);
            F2.setVisible(true);
        } else {
            F2.setVisible(false);
        }
    }

    TShapeNode PF = null;

    private void pivotForceArrowUpdate(TNode3D node) {
        if (!(node instanceof TArrayNode)) return;
        if (PF == null) {
            PF = (TShapeNode) new ArrowNode();
            PF.setPickable(false);
            PF.setColor(new Color3f(Color.YELLOW));
            PF.setVisible(true);
            ((TArrayNode) node).addNode(PF);
        }
        // Force-to-length scale, in m/N.
        double force_sc = 0.1;
        // ****************************************************************
        // Pivot Force Arrow
        // ****************************************************************
        // Weight computation.
        double force = theEngine.getGravity().length() * mass * force_sc;
        Vector3d tip0 = new Vector3d(0, -force, 0);
        Matrix3d rotation = new Matrix3d();
        rotation.set(orientation_d);
        rotation.invert();
        rotation.transform(tip0);
        // Force1 Computation
        Vector3d tip1 = new Vector3d(-force1 * force_sc * Math.sin(angle1), force1 * force_sc * Math.cos(angle1), 0);
        // Force2 Computation
        Vector3d tip2 = new Vector3d(-force2 * force_sc * Math.sin(angle2), force2 * force_sc * Math.cos(angle2), 0);
        // Net force Computation
        Vector3d tip = new Vector3d();
        tip.add(tip0);
        tip.add(tip1);
        tip.add(tip2);
        tip.negate();
        Vector3d tail = new Vector3d(0, pivot, 0);
        tip.add(tail);
        ((HasFromTo) PF).setFromTo(tail, tip);
    }

    /*
     * The following modified mass/moment of inertia accessor functions
     * handle the dependence between the two quantities.
     */

    public void setMass(double mass) {
        setMass(mass, true);
    }

    private void setMass(double mass, boolean flag) {
        super.setMass(mass);
        if (flag == true) {
            double l = length;
            double l2 = l / 2 - Math.abs(pivot);
            double l1 = l - l2;
            double I = (mass / l) * (l1 * l1 * l1 + l2 * l2 * l2) / 3;
            setMomentOfInertia(I, false);
        }
    }

    public void setMomentOfInertia(double I) {
        setMomentOfInertia(I, true);
    }

    private void setMomentOfInertia(double I, boolean flag) {
        super.setMomentOfInertia(I);
        if (flag == true) {
            double l = length;
            double l2 = l / 2 - Math.abs(pivot);
            double l1 = l - l2;
            double mass = 3 * I * l / (l1 * l1 * l1 + l2 * l2 * l2);
            setMass(mass, false);
        }
    }

    /*
     * The following handles the update of position, due to rotation.
     */

    public void update() {
        super.update();
        if (Math.abs(pivot) > 100 * Teal.MachineEpsilon) {
            if (direction == null) direction = new Vector3d(getDirection());
            Vector3d new_direction = new Vector3d(getDirection());
            new_direction.normalize();
            Vector3d increment = new Vector3d(new_direction);
            increment.sub(direction);
            increment.scale(-pivot);
            direction.set(new_direction);

            if (increment.length() > 100 * Teal.MachineEpsilon) {
                Vector3d new_position = new Vector3d(position);
                new_position.add(increment);
                PropertyChangeEvent pce = new PropertyChangeEvent(this, "position", position, new_position);
                setPosition(new_position);
                position_d.set(new_position);
                firePropertyChange(pce);
            }
        }
//		System.out.println(
        //			"*************************************************************\n"+
        //			"L1: " + L1 + " L2: " + L2 + "\n" +
        //			"F1: " + force1 + " F2: " + force2 + "\n" +
        //			"A1: " + angle1 + " A2: " + angle2 + "\n" +
        //			"*************************************************************" );
    }

}
