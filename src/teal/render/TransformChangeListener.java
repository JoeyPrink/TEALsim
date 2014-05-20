/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TransformChangeListener.java,v 1.5 2007/07/16 22:04:51 pbailey Exp $ 
 * 
 */

package teal.render;

import javax.media.j3d.Transform3D;

public interface TransformChangeListener{

     
     //public static final int TRANSLATE = 1;
     //public static final int ROTATE = 2;
     //public static final int ZOOM = 4;

    void transformChanged(int type, Transform3D trans);
}
