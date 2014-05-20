/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SelectManager.java,v 1.8 2009/04/24 19:35:54 pbailey Exp $ 
 * 
 */

package teal.render.viewer;

import java.util.*;

import teal.render.*;

public interface SelectManager
{    public void addSelectListener(SelectListener listener);
    public void removeSelectListener(SelectListener listener);
    
    /**
     * Adds an item to the selectedItems, 
     * it is responsible for setting the selected state of the object.
     * Clear flag if true, de-selects any existant selected objects.
     */
    public void addSelected(TAbstractRendered obj,boolean clear);
    public void removeSelected(TAbstractRendered obj);
    public void clearSelected();
    // This is a generalized method added to handle cases where no pick result is found by the behavior manager.
    // Before, BehaviorManager was defaulting to clearSelected() if there was no pick result, but this is not something
    // we want to do in all cases.
    public void noPickResult();
    public boolean disableVpBehaviorWhileSelecting();
    /**
     * Returns a cloned copy of the currently selected Elements, 
     * any additions or deletions performed on this Collection will
     * not effect the selected item count. Use the managers add & 
     * remove methods to alter the selected item count.
     */
    public Collection<TAbstractRendered> getSelected();
    public int getNumberSelected();
    public boolean isSelectionEmpty();
    //public void notify(int actionType);
    
}