/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TGroup.java,v 1.5 2009/04/24 19:35:54 pbailey Exp $ 
 * 
 */

package teal.render.scene;

import java.util.Collection;

/** Interface for the
 */

public interface TGroup {

 
	public TNode getChild(int i); 
	public void addChild(TNode child);
    public void removeChild(TNode child);
    /** The Collection returned is a copy. */
    public Collection<TNode> getChildren();
    public void removeChildren();
    public int getNumberOfChildren();

}
