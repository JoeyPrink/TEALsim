/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SortedList.java,v 1.6 2009/04/24 19:35:58 pbailey Exp $ 
 * 
 */

package teal.util;

import java.util.*;

/**
 * A general purpose list that may contain multiple copies of similar items.
 * Normally a Comparator will be specified, or the normal ordering of the objects 
 * will be used,in the case of objects that have the same compair values
 * addition order will be preserved.
 *
 * @author	Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version	$Revision: 1.6 $
 *
 */

@SuppressWarnings("unchecked")
public class SortedList extends ArrayList implements Sorted {

    private static final long serialVersionUID = 3618702985010426424L;
    
    Comparator comparator = null;

    public SortedList() {
        super();
    }

    public SortedList(Collection col) {
       
        Iterator it = col.iterator();
        while (it.hasNext()) {
            add(it.next());
        }
    }

    public SortedList(Comparator comp) {
        super();
        comparator = comp;
    }

    public SortedList(Collection col, Comparator comp) {
        super(col.size());
        comparator = comp;
        Iterator it = col.iterator();
        while (it.hasNext()) {
            add(it.next());
        }
    }

    public SortedList(int size, Comparator comp) {
        super(size);
        comparator = comp;
    }

    public void setComparator(Comparator cmp){
        comparator = cmp;
    }
    
    public Comparator comparator() {
        return comparator;
    }

    public boolean add(Object elm) {
        boolean inserted = false;
        if (isEmpty()) {
            super.add( elm);
            return true;
        } 
        else {
            ListIterator it = listIterator();
            if(comparator == null){
                Comparable cmp = null;
                while (it.hasNext()) {
                    Object obj = it.next();
                    if(obj instanceof Comparable){    
                        cmp = (Comparable) obj;
                        if (cmp.compareTo(elm) > 0) {
                            int idx = it.previousIndex();
                            add(idx, elm);
                            inserted = true;
                            break;
                        }
                    }
                }
            } 
            else {
                Object cur = null;
                while (it.hasNext()) {
                    cur = it.next();
                    if (comparator.compare(cur, elm) > 0) {
                        int idx = it.previousIndex();
                        add(idx, elm);
                        inserted = true;
                        break;
                    }
                }
            }
        }
       if (!inserted)
           inserted = super.add(elm);
        return inserted;
    }

    public Object first() {
        if (isEmpty())
            return null;
        else return get(0);
    }

    public Object last() {
        if (isEmpty())
            return null;
        else return get(size() - 1);
    }

    public Collection head(Object toElement) {
        return null;
    }

    public Collection sub(Object fromElement, Object toElement) {
        return null;
    }

    public Collection tail(Object fromElement) {
        return null;
    }

    public Collection headCompare(Object toElement) {
        return null;
    }

    public Collection subCompare(Object fromElement, Object toElement) {
        return null;
    }

    public Collection tailCompare(Object fromElement) {
        return null;
    }
}