/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ImageStatusListener.java,v 1.3 2007/07/16 22:05:19 pbailey Exp $ 
 * 
 */

package teal.visualization.image;

public interface ImageStatusListener {

    public void imageStatus(ImageStatusEvent ice);
}