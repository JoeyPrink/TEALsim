/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: UnitField.java,v 1.8 2010/07/21 21:54:28 stefan Exp $
 * 
 */

package teal.field;

import java.io.Serializable;

import javax.vecmath.*;

/** UnitField: normalized vector field
 *
 * UnitField is a wrapper class for Field which transforms the 
 * get methods so that a UnitNormal value is returned from the get method.
 * So that the magnitude at every point in the field is
 * one, but the direction is the same.
 * 
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.8 $ 
 */

public class UnitField implements Vector3dField, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6225374990446606504L;
	
	private Field field;

    /* Constructs a new UnitField that wraps  'field' but with
     * all the retrieved vector values are normalized to unit magnitude. 
     * The field is not converted.
     */
    public UnitField(Field field)

    {
        this.field = field;
    }

    public void setField(Field fld) {
        field = fld;
    }

    /**
     * Returns the original unwrapped field.
     */
    public Field getField() {
        return field;
    }

    /**
     * Returns the original unwrapped field's type.
     */
    public int getType() {
        return field.getType();
    }

    public Vector3d get(double x, double y, double z) {
        return get(new Vector3d(x, y, z));
    }

    public Vector3d get(Vector3d p) {
        Vector3d f = new Vector3d();
        return get(p, f);
    }

    public Vector3d get(Vector3d p, Vector3d f)
    /* Sets 'f' to the value of the field at 'p', scaled to unit magnitude.
     *   'p' is not modified.
     * Returns: resulting 'f' */
    {
        field.get(p, f);

        double len = f.length();
        if (len == 0.0)
            throw new RuntimeException("UnitVector: Zero vector!");
        else f.scale(1.0 / len);
        return f;
    }

    public static void unit(Vector3d vec) {

        double len = vec.length();
        if (len == 0.0)
            throw new RuntimeException("UnitVector: Zero vector!");
        else vec.scale(1.0 / len);
    }

}
