/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Graph.java,v 1.9 2009/04/24 19:35:52 pbailey Exp $ 
 * 
 */

package teal.plot;

import java.util.*;

import teal.core.TUpdatable;
import teal.sim.TSimElement;
import tealsim.gamification.GamificationAgent;

public class Graph extends teal.plot.ptolemy.Plot implements TUpdatable, TSimElement {

    private static final long serialVersionUID = 3761131530906252082L;

    protected Collection<PlotItem> plotItems;
    protected GamificationAgent gamification;

    public Graph() {
        super();
        plotItems = new ArrayList<PlotItem> ();

    }

    public synchronized void addPlotItem(PlotItem pi) {
        if (!plotItems.contains(pi)) {
            plotItems.add(pi);
        }
    }
    
    public synchronized void addGamification(GamificationAgent game) {
        gamification = game;
    }

    public synchronized void removePlotItem(PlotItem pi) {

        if (plotItems.contains(pi)) {
            plotItems.remove(pi);
        }
    }

    public void update() {
        Iterator it = plotItems.iterator();
        while (it.hasNext()) {
            PlotItem pi = (PlotItem) it.next();
            pi.doPlot(this);
        }
        if(gamification != null) {
            gamification.checkTask();
        }
        
        repaint();
        Thread.yield();      
    }
}
