/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SpatialField.java,v 1.32 2010/08/10 18:12:33 stefan Exp $ 
 * 
 */

package teal.sim.spatial;

import javax.vecmath.Color3f;

import teal.config.Teal;
import teal.field.Field;
import teal.sim.engine.*;
import teal.physics.em.BField;
import teal.physics.em.EField;
import teal.physics.em.PField;

/**
 * Spatial provides a base class for field representations.
 *
 *
 * @author Phil Bailey - Center for Educational Computing Initiatives / MIT
 */

public abstract class SpatialField extends Spatial{

    protected Field field;
    protected int ffType = Field.E_FIELD;

    public SpatialField() {
        super();
        setColor(Teal.DefaultEFieldColor);
        ffType = Field.E_FIELD;
        field = null;

    }

    /* (non-Javadoc)
     * @see teal.sim.engine.HasEngine#setModel(teal.sim.engine.TEngine)
     */
    public void setSimEngine(TSimEngine world) {
        super.setSimEngine(world);
        field = assignField();

    }

    /**
     * Returns the type of field represented by this SpatialField (EField, BField, etc.).
     * 
     * @return field type (E_FIELD, B_FIELD, etc.).
     */
    public int getType() {
        return ffType;
    }

    /**
     * Sets the type of field represented by this SpatialField.
     * 
     * @param type field type.
     */
    public void setType(int type) {
        ffType = type;
        field = assignField();
        assignDefaultColor();
        if(field != null)
        	needsSpatial();

    }

    /**
     * Sets the Field associated with this SpatialField.
     * 
     * @param fld Field.
     */
    public void setField(Field fld) {
        field = fld;
        ffType = field.getType();
        assignDefaultColor();
        needsSpatial();
 
    }

    /**
     * Gets the Field associated with this SpatialField.
     * 
     * @return Field.
     */
    public Field getField() {
        if (field == null) field = assignField();
        return field;
    }

    /**
     * Assigns default field colors to this object, taken from the teal.config package.
     */
    protected void assignDefaultColor() {
        if (ffType == Field.E_FIELD) {
            setColor(Teal.DefaultEFieldColor);
        } else if (ffType == Field.B_FIELD) {
            setColor(Teal.DefaultBFieldColor);
        } else if (ffType == Field.P_FIELD) {
            setColor(Teal.DefaultPFieldColor);
        }
        //else if (ffType == Field.EP_FIELD)
        //{
        //	mColor = Teal.DefaultEPotentialFieldColor;
        //}
    }

    /**
     * Sets the Field of this Spatial according to the assigned field type.
     * 
     * @return reference to Field.
     */
    protected Field assignField() {
        Field field = null;
        if (theEngine != null) {
        	switch(ffType) {
        	case Field.E_FIELD:
//              field = ((EMEngine)theEngine).getEField();
                field = theEngine.getElementByType(EField.class);
                break;
        	case Field.B_FIELD:
//              field = ((EMEngine)theEngine).getBField();
                field = theEngine.getElementByType(BField.class);
                break;
        	case Field.P_FIELD:
//              field = ((EMEngine)theEngine).getPField();
                field = theEngine.getElementByType(PField.class);        		
        	}

        	//else if (ffType == Field.EP_FIELD)
            //{
            //	field = theEngine.getEPotentialField();
            //}
            needsSpatial();
        }
        return field;
    }

}
