/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SpatialVisualization.java,v 1.20 2010/04/12 20:13:18 stefan Exp $ 
 * 
 */

package teal.sim.spatial;

import javax.vecmath.*;

import teal.field.*;
import teal.math.*;
import teal.render.j3d.*;
import teal.render.j3d.geometry.GeomUtil;
import teal.render.scene.*;
import teal.util.*;

/**
 * Spatial provides a base class for visualizations.
 *
 *
 * @author Phil Bailey - Center for Educational Computing Initiatives / MIT
 */

public class SpatialVisualization extends Spatial {

    private static final long serialVersionUID = 3760845644914634804L;
    //For testing
    public Field field = null;
    protected int vType = GeomUtil.LINE;
    protected GridIterator vgIterator = null;
    protected int resX = 12;
    protected int resY = 12;

    public SpatialVisualization() {
        super();
        setPickable(false);
    }

    protected TNode3D makeNode() {
        GridNode node = new GridNode(resX, resY);
        node.setPickable(false);
        node.setType(vType);
        node.checkGeometry(resX, resY);
        updateNodeGeometry(node);
        renderFlags |= GEOMETRY_CHANGE;
        return node;
    }

    public void setResolution(int res) {
        if ((resX != res) && (resY != res)) {

            int old = resX;
            resX = res;
            resY = res;
            if (mNode != null) {
                mNode.detach();
                mNode = null;
            }
            if (vgIterator != null) {
                if (vgIterator instanceof RectangularPlane) ((RectangularPlane) vgIterator).setResolution(resX, resY);
            }

            //TDebug.println(1,id + ": setting resolution to: " + res);

            firePropertyChange("resolution", old, resX);
            if (mNode != null) {
                needsSpatial();
            }
        }
    }

    public int getResolution() {
        return resX;
    }

    public void setGridIterator(GridIterator iter) {
        vgIterator = iter;
    }

    public GridIterator getGridIterator() {
        return vgIterator;
    }

    public void nextSpatial() {
        registerRenderFlag(GEOMETRY_CHANGE);
    }

    public int getType() {
        return vType;
    }

    public void setType(int type) {
        vType = type;

    }

    public void render() {
        if (mNode == null) return;
        if (mNeedsSpatial) {
            nextSpatial();
            mNeedsSpatial = false;
        }
        if ((renderFlags & GEOMETRY_CHANGE) == GEOMETRY_CHANGE) {
            ((GridNode) mNode).refresh();
            renderFlags ^= GEOMETRY_CHANGE;
        }
    }

    public void updateNodeGeometry(GridNode node) {
        if ((resX * resY) == 0) return;
        if (theEngine != null && (resX * resY) > 0) {

            if (vgIterator == null) {
                vgIterator = new RectangularPlane(theEngine.getBoundingArea());
                ((RectangularPlane) vgIterator).setResolution(resX, resY);

            }
            vgIterator.reset();
            Vector3d normal = null;
            if (vgIterator instanceof RectangularPlane) {
                normal = ((RectangularPlane) vgIterator).getNormal();
                TDebug.println("normal: " + normal);
            }

            node.checkGeometry(resX, resY);
            node.reset();
            int offset = 0;
            Vector3d fieldValue = null;
            Vector3d pos = new Vector3d();
            Vector3d data = new Vector3d();
            while (vgIterator.hasNext()) {
                pos = vgIterator.nextVec();
                fieldValue = field.get(pos);
                if (normal != null) {
                    data.scale(fieldValue.length(), normal);
                    pos.add(data);
                } else {
                    pos.z = fieldValue.length();
                }

                node.put((float) pos.x, (float) pos.y, (float) pos.z, true);
            }
        }

    }

}
