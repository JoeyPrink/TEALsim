/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ArrayNode.java,v 1.16 2007/07/16 22:04:53 pbailey Exp $ 
 * 
 */

package teal.render.j3d;


import java.util.Iterator;
import java.util.Vector;

import javax.media.j3d.Node;

import teal.render.TAbstractRendered;
import teal.render.scene.TArrayNode;
import teal.render.scene.TNode3D;

/**
 * provides SceneGraphNode for the management of a dynamic group or array of TNodes.
 * used in creating the vector field array for showing the field configuration jwb
 *
 * @author Phil Bailey
 * @version $Revision: 1.16 $
 *
 **/

public class ArrayNode extends Node3D implements TArrayNode
{
	
	Vector nodes = null;

	public ArrayNode(){
		super();
        nodes = new Vector();
	
	}
	
	public ArrayNode(TAbstractRendered element){
		this();
		setElement(element);

	}
    
	public Iterator iterator()
	{
        return nodes.iterator();   
    }
    
    public int getNodeCount()
    {
        return nodes.size();
    }
    
    public void setVisible(int fromIdx,int toIdx,boolean state)
    {
        for(int i = fromIdx;(i < nodes.size()) && (i<= toIdx); i++)
        {        
            ((Node3D)nodes.get(i)).setVisible(state);
        }
    }
    
    
    public void addNode(TNode3D node)
    {
        nodes.add(node);
        mContents.addChild((Node) node);
    }
    
    public void removeNode(TNode3D node)
    {
        int idx = nodes.indexOf(node);
        if( idx > -1)
        {
            removeNode(idx);
        }
    }
    
    public TNode3D get(int idx)
    {
        TNode3D n = null;
        try
        {
            n = (TNode3D) nodes.get(idx);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
        }
        return n; 
    }
    public void removeNode(int idx)
    {
        Node3D sn = null;
        try
        {
            sn = (Node3D) nodes.get(idx);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
        }
        if (sn != null)
        {
            nodes.remove(idx);
            sn.detach();
        }
    }
    
    public void removeAll()
    {
        Iterator it = nodes.iterator();
        while (it.hasNext())
        {
            Node3D sn = (Node3D) it.next();
            sn.detach();
        }
        nodes.clear();    
    }
}
    

