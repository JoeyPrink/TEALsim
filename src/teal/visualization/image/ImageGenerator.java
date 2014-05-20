/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ImageGenerator.java,v 1.3 2007/07/16 22:05:19 pbailey Exp $ 
 * 
 */

package teal.visualization.image;

import java.awt.*;

import teal.util.*;
//import teal.sim.*;

/**
 * Replaces BackgroundGenerator, should add ImageConsumer support
 * and imageComplete listener support.
 */

public interface ImageGenerator{
    
    public void reset();
    public void generateImage();
    public boolean isImageGenerated();
    public Image getImage();
	public Dimension getSize();
	public void setSize(Dimension size);
	public void setValid(boolean b);
	public boolean getValid();
    public void addImageStatusListener(ImageStatusListener lis);
    public void removeImageStatusListener(ImageStatusListener lis);
    public void addProgressEventListener(ProgressEventListener lis);
    public void removeProgressEventListener(ProgressEventListener lis);
    
}

