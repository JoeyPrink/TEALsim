/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: RelativeFLine.java,v 1.34 2010/07/16 21:41:39 stefan Exp $ 
 * 
 */

package teal.sim.spatial;

import java.util.*;

import javax.media.j3d.Transform3D;
import javax.vecmath.*;

import teal.config.Teal;
import teal.core.*;
import teal.field.Field;
import teal.render.BoundingSphere;
import teal.sim.engine.EngineRendered;


/**
 * Relative field lines use the internal position variable somewhat differently
 * than most of the HasPosition classes, position is an offset from the element associated 
 * with the fieldlines position, if there is no element it will be absolute. Access should 
 * always be through the getter & setter.
 *
 *
 * @author Phil Bailey - Center for Educational Computing Initiatives / MIT
 */

public class RelativeFLine extends FieldLine implements HasReference {

    private static final long serialVersionUID = 3258415031984011319L;

    protected Referenced mElement = null;

    protected Vector3d offset = new Vector3d();

    public RelativeFLine() {
        super();
        setPickable(false);
        mElement = null;

    }

    public RelativeFLine(Referenced obj) {
        super();
        setPickable(false);
        setReference(obj);
    }

    public RelativeFLine(Referenced obj, int fieldType) {
        super(fieldType);
        setPickable(false);
        setReference(obj);
    }

    public RelativeFLine(Referenced obj, Vector3d pos, int fieldType) {
        this(obj, fieldType);
        setPosition(pos);
    }

    public RelativeFLine(Referenced obj, Field fld) {
        super(fld);
        setPickable(false);
        setReference(obj);
    }

    public RelativeFLine(Vector3d pos, double angle) {
        super(pos);
        setPickable(false);
        setOffset(angle);
    }

    public RelativeFLine(Referenced obj, double angle) {
        this(obj);
        setOffset(angle);
    }
    
    // added these to set the radius at which the fieldlines are drawn
    public RelativeFLine(Vector3d pos, double angle, double radius) {
        super(pos);
        setPickable(false);
        setOffset(radius,angle);
    }

    public RelativeFLine(Referenced obj, double angle, double radius) {
        this(obj);
        setOffset(radius,angle);
    }

    public RelativeFLine(Referenced obj, double angle, int fieldType) {
        this(obj, angle);
        ffType = fieldType;
    }

    public RelativeFLine(Referenced obj, double angle, Field fld) {
        this(obj, angle);
        field = fld;
    }

    /**
     * Adds or sets the current referenced PhysicalElement.
     *
     */
    public void addReference(Referenced elm) {
        setReference(elm);
    }

    /**
     * Adds or sets the current referenced PhysicalElement.
     *
     */
    public void setReference(Referenced elm) {
        if (mElement != null) {
            mElement.removeReferent(this);
        }
        mElement = elm;
        mElement.addReferent(this);
    }

    /**
     * removes or clears the current referenced PhysicalElement.
     *
     */
    public void removeReference(Referenced elm) {
        if ((elm != null) && (mElement != null) && (elm == mElement)) {
            mElement.removeReferent(this);
            mElement = null;
        }
    }

    public Referenced getReference() {
        return mElement;
    }


    public Vector3d getPosition() {
        Vector3d pos = new Vector3d();
        if (mElement != null) {
            pos.set(((EngineRendered) mElement).getPosition());
            pos.add(getTransformedOffset());
        }
        return pos;
    }



    public void setPositon(Vector3d pos, boolean sendPC) {
        offset = new Vector3d(pos);

        if (mElement != null) {
            pos.add(((EngineRendered) mElement).getPosition());
        }
        position = pos;
        if (mNode != null) {
            mNode.setPosition(getPosition());
        }
    }

    public Vector3d getOffset() {
        return position;
    }

    public void setOffset(double radians) {
        //double r = 4.0 * Teal.PointChargeRadius;
    	//double r = ((PointCharge)mElement).getRadius();
    	double r = 0;
        if (mElement != null) {
            BoundingSphere bs = new BoundingSphere(((EngineRendered) mElement).getBoundingArea());
            //r = 2.0 * bs.getRadius();
            r = bs.getRadius();
        }
        setOffset(r, radians);
    }

    public void setOffset(double r, double radians) {
        Vector3d sp = new Vector3d();
        sp.x += (r * Math.sin(radians));
        sp.y += (r * Math.cos(radians));
        if (mElement != null) {
            Quat4d q = new Quat4d(((EngineRendered) mElement).getRotation());
            Transform3D t = new Transform3D();
            t.setRotation(q);
            t.transform(sp);
        }
        offset = sp;
        position = sp;
    }

    public void setOffset(Vector3d off) {
        offset = off;
        if (mElement != null) {
            Quat4d q = new Quat4d(((EngineRendered) mElement).getRotation());
            Transform3D t = new Transform3D();
            t.setRotation(q);
            t.transform(off);
        }

        position = off;
    }

    private Vector3d getTransformedOffset() {
        Vector3d baseOffset = new Vector3d(offset);
        if (mElement != null) {
            Quat4d q = new Quat4d(((EngineRendered) mElement).getRotation());
            Transform3D t = new Transform3D();
            t.setRotation(q);
            t.transform(baseOffset);
        }
        return baseOffset;
    }

    
    public static Collection<RelativeFLine> createLines(Referenced element, int number) {
        return createLines(element, teal.field.Field.E_FIELD, number, false);
    }

    public static Collection<RelativeFLine> createLines(Referenced element, int type, int number) {
        return createLines(element, type, number, false);
    }

    public static Collection<RelativeFLine> createLines(Referenced element, int type, int number, boolean pickable) {
        ArrayList<RelativeFLine> list = new ArrayList<RelativeFLine>(number);
        double rad = Math.PI * 2.0 / (double) number;
        double a = -Math.PI;
        for (int i = 0; i < number; i++) {
            RelativeFLine rl = new RelativeFLine(element, a, type);
            rl.setPickable(pickable);
            BoundingSphere bs = new BoundingSphere(((EngineRendered) element).getBoundingArea());
            //r = 2.0 * bs.getRadius();
            double r = bs.getRadius();
            rl.setMinDistance(0.5*r);
            //rl.setBuildDir(FieldLine.BUILD_POSITIVE);
            list.add(rl);
            a += rad;
        }
        return list;
    }
}
