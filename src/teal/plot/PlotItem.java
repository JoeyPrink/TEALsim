/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PlotItem.java,v 1.7 2007/07/16 22:04:48 pbailey Exp $ 
 * 
 */

package teal.plot;


/**
 * Interface for PlotItems used by Graph.
 */
public interface PlotItem
{
    public void doPlot(Graph graph);
}