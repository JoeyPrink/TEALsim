/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TScene.java,v 1.5 2009/06/11 02:21:52 pbailey Exp $ 
 * 
 */

package teal.render.scene;

import java.util.Iterator;


/** 
 * Interface for the SceneGraph This is to be used to contain
 * all elements of the scene, currently the view Transform is 
 * not part of the scene this will enable the scene to be
 * rendered into different viewports.
 */

public interface TScene{

    public Iterator getLights();
    public Iterator getMaterials();
    public Iterator getTNode3Ds();
    public void add(TNode obj); 
    public void remove(TNode obj); 

}
