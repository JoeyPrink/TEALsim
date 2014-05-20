/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ProgressEvent.java,v 1.7 2007/08/17 19:38:27 jbelcher Exp $ 
 * 
 */

package teal.util;

import java.util.*;

/** This class defines the behavior of an object that wishes to receive
 * periodic updates on the progress of a lengthy task.
 */

public class ProgressEvent extends EventObject {

    private static final long serialVersionUID = 3545515106021422903L;
    public static final int START = 1;
    public static final int PROGRESS = 2;
    public static final int COMPLETE = 4;
    public static final int INTERRUPT = 8;

    int percent = 0;
    int status;

    public ProgressEvent(Object source, int s, int percent) {
        super(source);
        status = s;
        setPercent(percent);
    }

    public ProgressEvent(Object source, int s) {
        super(source);
        status = s;
        percent = 0;
    }

    public ProgressEvent(Object source) {
        this(source, START, 0);
    }

    /** Set the progress amount to <code>progress</code>, which is a value
     * between 0 and 100. (Out of range values should be silently clipped.)
     *
     * @param val The percentage of the task completed.
     */

    public void setPercent(int val) {
        if (val < 0)
            val = 0;
        else if (val > 100) val = 100;
        percent = val;
    }

    public int getPercent() {
        return percent;
    }

    public void setStatus(int val) {
        status = val;
    }

    public int getStatus() {
        return status;
    }

}
