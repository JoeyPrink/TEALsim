/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: GenericFieldGenerator.java,v 1.3 2007/07/16 22:04:45 pbailey Exp $
 * 
 */

package teal.field;

import javax.vecmath.*;
/**
 * Provides methods for any generic field generator, which are envisioned as objects that have no physical 
 * representation, but generate "background" fields that add to the composite fields of the world.  They are 
 * generic in the sense that they simply return vectors and scalars, with no field type associations.  A 
 * GenericFieldGenerator can be wrapped in another class (GenericEField, for example) to produce fields of a 
 * specific type.  The separation of field value and field type allows us to create a single object of a given field
 * value, and through wrapping use it as any field type.
 */
public interface GenericFieldGenerator {
		
		// this presumably returns the vector field itself
		/**
		 * Returns the vector quantity associated with this field at the given position.
		 * @param pos position at which the field is being evaluated.
		 * @return the vector value of the field at this position.
		 */
		public Vector3d getVectorField(Vector3d pos);
		// this could be used to represent the "flux" function of the field
		/**
		 * Returns a scalar quantity associated with this field at the given position.
		 * @param pos position at which the field is being evaluated.
		 * @return a scalar value of the field at this position.
		 */
		public double getFirstScalarField(Vector3d pos);
		// this could be used to represent the "potential" function of the field
		/**
		 * Returns a scalar quantity associated with this field at the given position.
		 * @param pos position at which the field is being evaluated.
		 * @return a scalar value of the field at this position.
		 */
		public double getSecondScalarField(Vector3d pos);
}
