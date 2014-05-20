/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: FunctionRoute.java,v 1.10 2010/07/23 21:38:07 stefan Exp $ 
 * 
 */

package teal.math;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

import teal.core.*;
import teal.util.TDebug;

public class FunctionRoute extends Route {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -7215336531157580757L;
    private Function mFunction;
    public FunctionRoute(String attName, TElement targetObj, String tName, Function fun) {
        super(attName, targetObj, tName);
        mFunction = fun;
    }

    public Function getFunction() {
        return mFunction;
    }

    public void setFunction(Function f) {
        mFunction = f;
    }

    /** 
     * The actual dispatch of the propertyChange method. If the method 
     * has not been cached an attempt to resolve the method call will 
     * be performed.
     */
    public void propertyChange(PropertyChangeEvent pce) {
        TDebug.println(0, " Route - property: " + pce.getPropertyName());
        if ((srcProp.compareTo(pce.getPropertyName()) != 0)) {
            TDebug.println(1, "Route propertyChange: NOT INTERRESTED");
            return;
        }
        if ((mMethod == null) && (init == false)) {
            TDebug.println(1, " in propertyChange trying find setMethod ");
            getSetMethod(targetProp);
        }
        if (mMethod == null) {
            TDebug.println(1, "Error: No method found for " + targetProp);
            return;
        }
        Object params[] = new Object[1];
        Object newValue = pce.getNewValue();
        params[0] = pce.getNewValue();
        double d = -1.;
        try {
            if (mFunction != null) {

                if (newValue instanceof Number) {
                    d = mFunction.evaluateAt(((Number) newValue).doubleValue());
                }
                //if(paramType.isAssignableFrom(d.Class()))    
                params[0] = new Double(d);
                mMethod.invoke(target, params);

            } else {
                //if(paramType.isAssignableFrom(pce.getNewValue().getClass()))
                //   params[0] = newValue;

                mMethod.invoke(target, params);
            }

        } catch (InvocationTargetException cnfe) {
            TDebug.println(1, " InvocTargetEx: " + cnfe.getMessage());
        } catch (IllegalAccessException ille) {
            TDebug.println(1, "IllegalAccess: " + ille.getMessage());
        }

    }
    
}
