/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Scalar.java,v 1.4 2007/08/16 22:09:40 jbelcher Exp $ 
 * 
 */

package teal.sim.function;

import java.beans.*;
import java.lang.reflect.*;

import teal.core.*;
import teal.util.*;

/** 
 * A PropertyRoute class which provides for the specification of an optional
 * method which will be performed on the input value prior to forwarding 
 * the specified value. The method getValue will return the converted value.
 */

public class Scalar extends AbstractElement {

    private static final long serialVersionUID = 3256725061204785201L;
    
    protected Double input;
    protected Double value;
    protected Method method;
    protected double scale;

    public Scalar() {
        super();
        value = new Double(0.);
        input = new Double(0.);
        method = null;
        scale = 1.;
    }

    public void setValue(double val) {
        Double t = new Double(val);

        if (value != t) {

            PropertyChangeEvent pce = PCUtil.makePCEvent(this, "value", value, t);
            value = t;
            firePropertyChange(pce);

        }

    }

    public void setValue(Object obj) {
        if (obj instanceof Number) {
            setValue(((Number) obj).doubleValue());
        } else if (obj instanceof String) {
            try {
                double d = Double.parseDouble((String) obj);
                setValue(d);
            } catch (NumberFormatException ne) {
                TDebug.println(0, "NumberFormatException: '" + obj);
            }
        }

    }

    public Object getValue() {
        return value;
    }

    public void setInput(double val) {
        Double t = new Double(val * scale);

        if (value != t) {
            setValue(val * scale);
        }

    }

    public void setInput(Object obj) {
        if (obj instanceof Number) {
            setValue(((Number) obj).doubleValue() * scale);
        } else if (obj instanceof String) {
            try {
                double d = Double.parseDouble((String) obj);
                setValue(d * scale);
            } catch (NumberFormatException ne) {
                TDebug.println(0, "NumberFormatException: '" + obj);
            }
        }

    }

    public Object getInput() {
        return input;
    }

    public void setScale(double s) {
        TDebug.println(1, getID() + " setScale=" + s);
        scale = s;
    }

    public double getScale() {
        return scale;
    }
}
