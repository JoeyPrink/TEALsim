/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: CreateAtPick.java,v 1.20 2009/04/24 19:35:55 pbailey Exp $ 
 * 
 */

/**
 * A demonstration implementation of a simple behavior for adding elements.
 * It will be moved to a better package once I figure where it makes the most sense.
 *
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.20 $
 */

package teal.sim.behavior;

import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import javax.vecmath.*;

import teal.core.*;
import teal.render.*;
import teal.render.viewer.*;
import teal.util.*;

public class CreateAtPick extends AbstractElement implements MouseListener {

    private static final long serialVersionUID = 3833467301352453688L;

    TViewer viewer;
    Class<?> createClass;
    StringMap properties;

    boolean invalidateImage = true;

    public CreateAtPick() {
        viewer = null;
        createClass = null;
        properties = new StringMap();
    }

    public CreateAtPick(TViewer v) {
        viewer = v;
        createClass = null;
    }

    public Class<?> getCreateClass() {
        return createClass;
    }

    public void setCreateClass(Class<?> cl) {
        createClass = cl;
    }

    public String getCreateClassName() {
        String name = null;
        if (createClass != null) name = createClass.getName();
        return name;
    }

    public void setCreateClassName(String str) {
        try {
            Class<?> cl = Class.forName(str);
            setCreateClass(cl);
        } catch (ClassNotFoundException cnf) {
            TDebug.printThrown(0, cnf, "Setting class in CreateAtPick");
        }
    }

    public TViewer getViewer() {
        return viewer;
    }

    public void setViewer(TViewer v) {
        viewer = v;
    }

    public void clearProperties() {
        properties.clear();
    }

    public void addProperty(String name, Object value) {
        properties.put(name, value);
    }

    public void removeProperty(String name) {
        properties.remove(name);
    }

    public void loadProperties(Map map) {
        properties.putAll(map);
    }

    public void loadProperties(StringMap map) {
        properties.putAll(map);
    }

    /** this is broken during move to 3D only should be simple tpo fix.
     */
    public void mousePressed(MouseEvent e) {
        Object source = e.getSource();
        if (source instanceof TViewer) {
            viewer = (TViewer) source;
        }
        if (viewer != null) {
            TElement obj = null;

            AffineTransform inverse = null;
            Vector3d pos = null;
            Point2D.Double src = new Point2D.Double();
            Point2D.Double dest = new Point2D.Double();
            src.setLocation((double) e.getX(), (double) e.getY());

            // Convert screen coords to world coords
            inverse = viewer.getInvertedAffineTransform();
            inverse.transform(src, dest);
            pos = new Vector3d(dest.getX(), dest.getY(), 0.0);
            Vector3d pos2 = null;
            //pos2 = viewer.project(pos);

            if (createClass != null) {
                try {
                    obj = (TElement) createClass.newInstance();
                    if (!properties.isEmpty()) {
                        Set set = properties.keySet();
                        Iterator it = set.iterator();
                        String str = (String) it.next();
                        Object val = properties.get(str);
                        obj.setProperty(str, val);

                    }
                    if (obj instanceof HasPosition) ((HasPosition) obj).setPosition(pos2);

                    //BasicApp.getInstance().addTElement(obj);

                } catch (InstantiationException ie) {
                    TDebug.printThrown(0, ie, "In CreatAtPick: ");
                } catch (IllegalAccessException ie) {
                    TDebug.printThrown(0, ie, "In CreatAtPick: ");
                }
            }
        }

    }

    public void mouseClicked(MouseEvent me) {
    }

    public void mouseReleased(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
    }

}
