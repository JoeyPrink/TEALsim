/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TAbstractRendered.java,v 1.11 2010/06/01 15:24:17 stefan Exp $ 
 * 
 */

package teal.render;

import java.io.Serializable;
import java.net.URL;

public interface TAbstractRendered extends HasBoundingArea, HasPosition,
    IsMoveable, IsPickable, TDrawable, HasColor, Serializable
{
	public enum NodeType {
    	NONE, ARRAY, ARROW, ARROW_SOLID, BOX,  CYLINDER, DIPOLE_ELEC, DIPOLE_MAG, FIELD_LINE, GRID, HELIX,
    	IMAGE, INCLINED_PLANE, LINE, LINE_ARRAY,  MULTI_SHAPE, POINT, PIPE,  SPHERE, STEM, TEXT,
    	 TORUS, WALL
    	
    }
	
    public final static int POSITION_CHANGE = 0x001;
    public final static int ROTATION_CHANGE = 0x002;
	public final static int SCALE_CHANGE = 0x004;
    public final static int GEOMETRY_CHANGE = 0x008;
    public final static int COLOR_CHANGE = 0x010;
	public final static int VISIBILITY_CHANGE = 0x020;
    public void registerRenderFlag(int flag);

    public NodeType getNodeType();
	public boolean isSelected();
	public void setSelected(boolean b);
	
	public boolean isSelectable();
	public void setSelectable(boolean b); 
	public TMaterial getMaterial();
	public void setMaterial(TMaterial material);
	public URL getURL();
	public void setURL(URL modelURL);
	
}
	 
