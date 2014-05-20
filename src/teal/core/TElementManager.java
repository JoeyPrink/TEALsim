/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TElementManager.java,v 1.12 2010/07/16 16:55:41 stefan Exp $
 * 
 */

package teal.core;

import java.util.Collection;


/**
 * Interface for SimWorld that defines required functions for adding simulation objects to a simulation (SimWorld).
 */
public interface TElementManager {
    
    
    public void addTElement(HasID elm);
    public void addTElement(HasID elm, boolean addToList);
    public void removeTElement(HasID elm);

    public void addElement(Object element) throws IllegalArgumentException;
    public void addElement(Object element, boolean addToList) throws IllegalArgumentException;
    public void addElements(Collection<?> elements) throws IllegalArgumentException;

    public void removeElement(Object element);
    public void removeElements(Collection<?> elements);
    
    // Adding this method for command line/XML argument support
    public HasID getTElementByID(String id);	
    }
