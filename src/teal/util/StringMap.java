/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: StringMap.java,v 1.4 2007/07/16 22:05:17 pbailey Exp $ 
 * 
 */

package teal.util;

import java.util.*;

/**
 * 
 * 
 * Designed to hold fieldName & data/dataType may have many uses.
 *
 * @author	Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version	1.0 - July 1998
 *

 * @see Map
 */
public class StringMap extends HashMap {

    private static final long serialVersionUID = 3257286950267728950L;

    public StringMap() {
        super();
    }

    public StringMap(Map m) {
        this();
        putAll(m);
    }

    public StringMap(StringMap m) {
        super(m);
    }

    public void putAll(StringMap m) {
        super.putAll(m);
    }

    public void putAll(Map m) {
        if ((m != null) && (!m.isEmpty())) {

            Object val = null;
            Object key = null;
            Set set = m.keySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                key = it.next();
                val = m.get(key);
                super.put(key.toString(), val);
            }
        }
    }

    public Object put(Object k, Object v) {
        return put(k.toString(), v);
    }

    public Object put(Object k, int v) {
        return put(k.toString(), v);
    }

    public Object put(Object k, double v) {
        return put(k.toString(), v);
    }

    public Object put(Object k, float v) {
        return put(k.toString(), v);
    }

    public Object put(Object k, boolean v) {
        return put(k.toString(), v);
    }

    public Object put(String k, Object v) {
        return super.put(k, v);
    }

    public Object put(String k, int v) {
        return super.put(k, new Integer(v));
    }

    public Object put(String k, double v) {
        return super.put(k, new Double(v));
    }

    public Object put(String k, float v) {
        return super.put(k, new Float(v));
    }

    public Object put(String k, boolean v) {
        return super.put(k, new Boolean(v));
    }

    public Object put(NamedValue nv) {
        return super.put(nv.getName(), nv.getValue());
    }

    public Object get(String k) {
        return super.get(k);
    }

    public Object get(Object k) {
        return get(k.toString());
    }

    public boolean containsKey(String k) {
        return super.containsKey(k);
    }

    public boolean containsKey(Object k) {
        return containsKey(k.toString());
    }

    public Object remove(String k) {
        return super.remove(k);
    }

    public Object remove(Object k) {
        return remove(k.toString());
    }
}