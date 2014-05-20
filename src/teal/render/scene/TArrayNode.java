/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TArrayNode.java,v 1.3 2007/07/16 22:04:57 pbailey Exp $ 
 * 
 */

package teal.render.scene;

import java.util.Iterator;

public interface TArrayNode
{
    
	public Iterator iterator();
    public int getNodeCount();
    public void setVisible(int fromIdx,int toIdx,boolean state);
    public void addNode(TNode3D node);
    public void removeNode(TNode3D node);
    public TNode3D get(int idx);
    public void removeNode(int idx);
    public void removeAll();

}
    
