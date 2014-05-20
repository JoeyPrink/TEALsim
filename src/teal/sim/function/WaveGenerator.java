/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: WaveGenerator.java,v 1.4 2007/07/16 22:05:02 pbailey Exp $ 
 * 
 */

package teal.sim.function;

import java.beans.*;

import teal.core.*;
import teal.sim.*;
import teal.sim.properties.*;
import teal.util.*;

/** 
 * A simple waveform generator, this will be a sine wave.
 * It is over-kill for this to implement Integratable, but we need to think about
 * that whole interface.
 */

public class WaveGenerator extends AbstractElement implements TSimElement, Stepping {

    private static final long serialVersionUID = 3258415044953059383L;
    protected double hz;
    protected double value;
    protected double value_d;
    protected double scale;

    protected double deltaR;
    protected double rad;
    protected double radPerSec;

    protected boolean isStepping = true;

    public WaveGenerator() {
        super();
        value = 0.;
        scale = 1.;
        hz = 1.;
        radsPerSec();
        //rad  = -Math.PI;
        rad = 0.;
    }

    public boolean isStepping() {
        return isStepping;
    }

    public void setStepping(boolean b) {
        isStepping = b;
    }

    public void nextStep(double dt) {
        if (!isStepping) return;
        rad += radPerSec * dt;
        //if(rad > Math.PI)
        //	rad =- 2.0 * Math.PI;
        setValue(Math.sin(rad) * scale);
    }

    public void reset() {
        rad = -Math.PI;
    }

    public void setValue(double val) {
        if (value != val) {
            PropertyChangeEvent pce = PCUtil.makePCEvent(this, "value", value, val);
            value = val;
            // TDebug.println("Wave: Calling routes.FPC");
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

    public double getValue() {
        return value;
    }

    public void setHz(double val) {
        if (hz != val) {

            PropertyChangeEvent pce = PCUtil.makePCEvent(this, "hz", hz, val);
            hz = val;
            radsPerSec();
            firePropertyChange(pce);
        }

    }

    public double getHz() {
        return hz;
    }

    public void setScale(double s) {
        scale = s;
    }

    public double getScale() {
        return scale;
    }

    private void radsPerSec() {
        radPerSec = 2.0 * Math.PI * hz;
    }
}
