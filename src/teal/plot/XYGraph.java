/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: XYGraph.java,v 1.19 2010/09/01 20:14:04 stefan Exp $ 
 * 
 */

package teal.plot;

import java.io.Serializable;

import javax.vecmath.Vector3d;

import teal.core.TElement;
import teal.sim.engine.HasSimEngine;
import teal.util.TDebug;


public class XYGraph implements PlotItem, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2510431549450320474L;
	private TElement body = null;
    private final Vector3d Xdirection = new Vector3d(1, 0, 0);
    private final Vector3d Ydirection = new Vector3d(0, 1, 0);
    private boolean reset = false;
    private String propertyName = "position";

    public void reset() {
        reset = true;
    }

    public XYGraph(TElement body) {
        this.body = body;
    };

    public void doPlot(Graph graph) {
        Vector3d position = (Vector3d) body.getProperty(propertyName);
        double t = ((HasSimEngine) body).getSimEngine().getTime();
        double[] xrange = graph.getXRange();
        double[] yrange = graph.getYRange();

        double x = position.dot(Xdirection);
        double y = position.dot(Ydirection);

        if (x > yrange[1] || y > yrange[1]) {
            graph.setYRange(yrange[0], Math.max(x, y));
        }
        if (x < yrange[0] || y < yrange[0]) {
            graph.setYRange(Math.min(x, y), yrange[1]);
        }
        if (t > xrange[1]) {
            graph.setXRange(xrange[0], t);
        }
        if (t < xrange[0]) {
            graph.setXRange(t, xrange[1]);
        }

        if (reset) {
            //			graph.setMarksStyle("dots", 0);
            //			graph.setMarksStyle("dots", 1);
            graph.addPoint(0, t, x, false);
            graph.addPoint(1, t, y, false);
            reset = false;
        } else {
            graph.addPoint(0, t, x, true);
            graph.addPoint(1, t, y, true);
            TDebug.println(1, "[ " + t + ", " + x + ", " + y + " ];... ");
        }
    }
}
