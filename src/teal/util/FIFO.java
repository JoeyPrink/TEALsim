/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: FIFO.java,v 1.5 2007/07/16 22:05:16 pbailey Exp $ 
 * 
 */

package teal.util;

import java.util.*;

/**
 * Provides a simple FIFO que.
 */

public class FIFO extends Vector {

    private static final long serialVersionUID = 3257567287178507826L;

    public FIFO() {
        super();
    }

    public FIFO(int cap, int inc) {
        super(cap, inc);
    }

    public boolean hasNext() {
        return elementCount > 0;
    }

    public Object next() {
        if (elementCount > 0)
            return remove(0);
        else return null;
    }
}