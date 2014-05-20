/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ArrayNode.java,v 1.8 2011/05/27 15:39:35 pbailey Exp $ 
 * 
 */

package teal.render.jme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

import teal.render.TAbstractRendered;
import teal.render.scene.TArrayNode;
import teal.render.scene.TNode3D;

public class ArrayNode extends Node3D implements TArrayNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5296548889133323863L;
//	Vector<TNode3D> nodes = null;
	
	int arrayMembers = 0;

	public ArrayNode() {
	}
	
	public ArrayNode(TAbstractRendered element) {
		this();
		doSetElement(element,false);
	}
	
	
	public synchronized void addNode(TNode3D node) {
//		nodes.add(node);
		super.attachChildAt((Node3D)node,arrayMembers);
		arrayMembers++;
	}

	public synchronized TNode3D get(int idx) {
/*		TNode3D node = null;
		try {
			node = nodes.get(idx);
		} catch (ArrayIndexOutOfBoundsException e) {
			TDebug.println(2,"ArrayNode: Index out of Bounds!");
		}
		*/
		if(idx >= arrayMembers)
			return null;
		
		return (TNode3D)super.getChild(idx);
	}

	public synchronized int getNodeCount() {
		return arrayMembers;
//		return nodes.size();
	}

	/**
	 * returns an iterator pointing to the subnodes.
	 */
	public synchronized Iterator<TNode3D> iterator() {
		return new ArrayNodeIterator(super.getChildren().iterator(),arrayMembers);
//		return nodes.iterator();
	}
	
	public synchronized void removeAll() {
		for(int i=0; i < arrayMembers; i++) {
			assert(getChild(i) instanceof TNode3D);
			detachChildAt(0);
		}
//		for(Spatial child:this.getChildren()){
//			if(child instanceof TNode3D) {
//				this.detachChild(child);
//			}
//		}
		arrayMembers = 0;
//		this.detachAllChildren();
//		nodes.clear();
	}

	public synchronized void removeNode(TNode3D node) {
		if(super.getChildIndex((Node3D)node) >= arrayMembers)
			return;
		super.detachChild((Node3D) node);
		arrayMembers--;
//		nodes.remove(node);
	}

	public synchronized void removeNode(int idx) {
		if(idx < arrayMembers) {	
			assert(super.getChild(idx) instanceof TNode3D);
			super.detachChildAt(idx);
			arrayMembers--;
		}
/*		TNode3D sn = null;
        try
        {
            sn = nodes.get(idx);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
			TDebug.println(2,"ArrayNode: Cannot remove. Index out of Bounds!");
        }
        if (sn != null)
        {
            nodes.remove(idx);
            sn.detach();
        }	*/	
	}

	public synchronized void setVisible(int fromIdx, int toIdx, boolean state) {
        for(int i = fromIdx;(i < arrayMembers) && (i<= toIdx); i++) {
			assert(super.getChild(i) instanceof TNode3D);
        	super.getChild(i).setVisible(state);
//            nodes.get(i).setVisible(state);
        }

	}
	
	
	/*
	// Those methods need to be overwritten because the first
	// arrayMembers elements are reserved for the array
	@Override
    public int attachChildAt(Spatial child, int index) {
		return super.attachChildAt(child, index+arrayMembers);
	}
	
    @Override
    public Spatial detachChildAt(int index) {
    	return super.detachChildAt(index + arrayMembers);
    }

    @Override
    public Spatial getChild(int i) {
    	return super.getChild(i+arrayMembers);
    }

    @Override
    public void swapChildren(int index1, int index2) {
    	super.swapChildren(index1+arrayMembers, index2+arrayMembers);
    }

    @Override
    public int attachChild(Spatial child) {
    	return super.attachChild(child) - arrayMembers;
    }
    
    @Override
    public int getQuantity() {
    	return super.getQuantity()-arrayMembers;
    }
    
    @Override
    public int getTriangleCount() {
        int count = 0;
        if(children != null) {
            for(int i = arrayMembers; i < children.size(); i++) {
                count += children.get(i).getTriangleCount();
            }
        }
        
        return count;
    }
    
    @Override
    public int getVertexCount() {
        int count = 0;
        if(children != null) {
            for(int i = arrayMembers; i < children.size(); i++) {
               count += children.get(i).getVertexCount();
            }
        }
        
        return count;
    }

    @Override
    public int detachChild(Spatial child) {
    	int index = super.detachChild(child);
    	if(index < 0)
    		return index;
    	return index - arrayMembers;
    }

    @Override
    public int detachChildNamed(String childName) {
    	int index = super.detachChildNamed(childName);
    	if(index < 0)
    		return index;
    	return index - arrayMembers;
    }

    @Override
    public void detachAllChildren() {
        if(children != null) {
            for ( int i = children.size() - 1; i >= arrayMembers; i-- ) {
                detachChildAt( i );
            }
        }
    }
    
    @Override    
    public int getChildIndex(Spatial sp) {
    	int index = super.getChildIndex(sp);
    	if(index < 0)
    		return index;
    	return index - arrayMembers;
    }
    
    @Override
    public boolean hasChild(Spatial spat) {
    	return super.hasChild(spat) && (super.getChildIndex(spat) >= arrayMembers);
    }

    @Override
	public List<Spatial> getChildren() {
        if(children != null) {
        	List<Spatial> retList = new ArrayList<Spatial>();
            for(int i = arrayMembers; i < children.size(); i++) {
            	retList.add(children.get(i));
            }
            return retList;
        } 
        return null;
	}
    
    */
    
    
    
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        arrayMembers = capsule.readInt("arrayMembers", 0);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(arrayMembers, "arrayMembers", 0);
    }
    
	class ArrayNodeIterator<E> implements Iterator<E> {

		Iterator<E> from;
		int maxCount;
		int pos = 0;
		
		private ArrayNodeIterator(Iterator<E> from, int count) {
			this.from = from;
			this.maxCount = count;
		}

		
		public boolean hasNext() {			
			if(pos > maxCount)
				return false;
			return from.hasNext();
		}

		
		public E next() {	
			pos++;
			return from.next();
		}

	
		public void remove() {
			maxCount--;
			from.remove();
		}
		
	}


}
