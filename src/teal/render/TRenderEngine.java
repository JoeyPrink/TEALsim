/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TRenderEngine.java,v 1.5 2010/06/07 22:00:31 pbailey Exp $ 
 * 
 */

package teal.render;

import java.util.Collection;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.core.TElement;
import teal.sim.simulation.TSimulation;

/** Interface for the
 */

public interface TRenderEngine extends TElement{

	public void render();
    public void render(boolean repaint);
    public void renderComplete();
    public void addRenderListener(TRenderListener rl);
    public void removeRenderListener(TRenderListener rl);
    public void addDrawable(TAbstractRendered draw);
    public void removeDrawable(TAbstractRendered draw);
    public void addDrawableBulk(Collection<TAbstractRendered> drawn);
    public void removeDrawableBulk(Collection<TAbstractRendered> drawn);
    public void setLookAt(Point3d from, Point3d to, Vector3d angle);
    public void setSimulation(TSimulation sim);
}
