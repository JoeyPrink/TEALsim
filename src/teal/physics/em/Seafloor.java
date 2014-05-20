/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Seafloor.java,v 1.30 2010/01/19 17:43:32 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import java.awt.Color;
import java.util.*;

import javax.media.j3d.Bounds;
import javax.media.j3d.BoundingBox;

import javax.vecmath.*;

//import teal.app.TealSimApp;
import teal.field.Field;
import teal.render.*;
import teal.render.geometry.*;
//import teal.render.j3d.*;
import teal.render.primitives.Arrow;
import teal.render.primitives.Line;
import teal.render.scene.*;
import teal.sim.engine.EngineRendered;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.*;
import teal.util.TDebug;

/**
 * This represents a very specialized object used in the Seafloor applet for modeling seafloor ridge spreading.
 */
public class Seafloor extends EMObject implements GeneratesB {

    private static final long serialVersionUID = 3257854285482898996L;

    // need a pointer to an application to add rendered objects from within Seafloor
    protected SimEM application = null;
    protected boolean generatingBField = true;

    int numStripes; // number of SeafloorStripes
    Vector3d earthField; // direction of Earth's magnetic field
    double earthFieldMag; // magnitude of earth's field
    double strike; // strike of stripes (this is an angle out of 180, i guess)
    Vector3d dip; // magnetization direction of stripes
    Vector3d spreadAxis; // axis along which the floor is spreading

    boolean bEnableCreationVars = false;
    Vector3d creationEarthField;
    double creationStrike;
    double latitude;
    double creationLatitude;

    double scanHeight;
    boolean showScanArrows = true;
    boolean showScanLineFieldArrows = true;
    boolean showScanLineCompArrows = true;
    Line scanLine;
    ArrayList<FieldVector> scanLineFieldArrows = new ArrayList<FieldVector>();
    ArrayList<Arrow> scanLineCompArrows = new ArrayList<Arrow>();

    // first idea for managing Stripes
    // stripes are not completely symmetric about the spreading center, but they need to be modified
    // symmetrically when the user changes a width, etc., on any particular stripe.
    ArrayList<StripeNode> rightStripes = new ArrayList<StripeNode>();
    ArrayList<StripeNode> leftStripes = new ArrayList<StripeNode>();

    // edge lists
    ArrayList<EdgeNode> rightEdges = new ArrayList<EdgeNode>();
    ArrayList<EdgeNode> leftEdges = new ArrayList<EdgeNode>();

    //field line lists
    ArrayList<FieldLine> rightFieldLines = new ArrayList<FieldLine>();
    ArrayList<FieldLine> leftFieldLines = new ArrayList<FieldLine>();

    ArrayList<FieldLine> fieldlines = new ArrayList<FieldLine>();

    // CONSTRUCTORS /////////////////////////
    public Seafloor() {
        // set some defaults
        earthField = new Vector3d();
        earthFieldMag = 1.;
        latitude = 0.;
        strike = 45.;
        creationEarthField = new Vector3d();
        creationLatitude = 0.;
        creationStrike = 0.;
        dip = new Vector3d(1., 0., 0.);
        spreadAxis = new Vector3d(1., 0., 0.);
        scanHeight = 0.1;

        scanLine = new Line(new Vector3d(), new Vector3d());
        scanLine.setColor(new Color(200, 200, 200));

    }

    public SimEM getAppplication() {
        return application;
    }

    public void setApplication(SimEM app) {
        this.application = app;
        this.configureFLines();
        app.addElement(scanLine);
    }

    public int getNumStripes() {
        return numStripes;
    }

    public void setNumStripes(int num) {
        numStripes = num;
    }

    public Vector3d getEarthField() {
        return earthField;
    }

    public void setEarthField(Vector3d global) {
        earthField = global;
    }

    public double getStrike() {
        return strike;
    }

    public void setStrike(double degrees) {
        strike = degrees;
        calcNewGeometry(this.latitude, this.strike, this.creationLatitude, this.creationStrike);
    }

    public Vector3d getDip() {
        return dip;
    }

    public void setStripeDips(Vector3d dipmoment) {
        //this assigns newdip to the stripes, alternately scaling it by -1 starting at the spreading center
        Vector3d neg = new Vector3d(dipmoment);
        neg.scale(-1.);
        for (int i = 0; i < rightStripes.size(); i++) {
            if (i % 2 == 0) {
                ((StripeNode) rightStripes.get(i)).setMoment(dipmoment);
                ((StripeNode) leftStripes.get(i)).setMoment(dipmoment);
                //System.out.println("Stripe " + i + " set to " + dipmoment);
            } else {
                ((StripeNode) rightStripes.get(i)).setMoment(neg);
                ((StripeNode) leftStripes.get(i)).setMoment(neg);
                //System.out.println("Stripe " + i + " set to " + neg);
            }
        }
    }

    public void setDip(Vector3d dipmoment) {
        dip = dipmoment;
        //System.out.println("setDip: " + dipmoment);
        setStripeDips(dipmoment);
    }

    public void setDip(double degrees, double magnitude) {
        // assign dip by angle (in degrees) and magnitude.  zero degrees points up along the y-axis.
        double rads = 2 * Math.PI * (degrees / 360.);
        Vector3d newdip = new Vector3d(Math.sin(rads), Math.cos(rads), 0.);
        newdip.scale(magnitude);

        dip = newdip;
        setStripeDips(newdip);

    }

    public Vector3d getSpreadAxis() {
        return spreadAxis;
    }

    public void setSpreadAxis(Vector3d axis) {
        axis.normalize();
        spreadAxis = axis;
    }

    public ArrayList<EdgeNode> getRightEdges() {
        return rightEdges;
    }

    public void setRightEdges(ArrayList<EdgeNode> edges) {
        rightEdges = edges;
    }

    // need methods for adding and removing stripes
    public void addStripe(double stripeWidth) {
        // for now let's assume it just adds the stripe to the end of the stripe list
        // needs to add a symmetric stripe to the other side of the spreading center

        StripeNode rStripe = new StripeNode();
        StripeNode lStripe = new StripeNode();

        if (rightStripes.size() == 0) {
            rStripe.setMoment(dip);
            lStripe.setMoment(dip);
        } else if (((StripeNode) rightStripes.get(rightStripes.size() - 1)).getMoment() == dip) {
            Vector3d tmp = new Vector3d(dip);
            tmp.scale(-1.);
            rStripe.setMoment(tmp);
            lStripe.setMoment(tmp);
        } else {
            rStripe.setMoment(dip);
            lStripe.setMoment(dip);
        }

        Vector3d start = new Vector3d();
        Vector3d end = new Vector3d();
        Vector3d negstart = new Vector3d();
        Vector3d negend = new Vector3d();

        if (rightEdges.size() > 0) {
            start.set(((EdgeNode) rightEdges.get(rightEdges.size() - 1)).getPosition());
        }
        negstart = new Vector3d(start);
        negstart.scale(-1.);

        end.scaleAdd(2., spreadAxis, start);
        negend.set(end);
        negend.scale(-1.);

        rStripe.setStartPos(start);
        rStripe.setEndPos(end);
        rStripe.setStripeIndex(rightStripes.size());
        lStripe.setStartPos(negstart);
        lStripe.setEndPos(negend);
        lStripe.setStripeIndex(rightStripes.size());

        rightStripes.add(rStripe);
        leftStripes.add(lStripe);

        application.addElement(rStripe);
        application.addElement(lStripe);

        rStripe.setPickable(false);
        rStripe.setSelectable(false);
        lStripe.setPickable(false);
        lStripe.setSelectable(false);

        EdgeNode rEdge = new EdgeNode();
        EdgeNode lEdge = new EdgeNode();
        rEdge.setEdgePosition(end);
        lEdge.setEdgePosition(negend);
        rEdge.setEdgeIndex(rightEdges.size());
        lEdge.setEdgeIndex(rightEdges.size());
        rEdge.setSideIndex(1);
        lEdge.setSideIndex(-1);

        rightEdges.add(rEdge);
        leftEdges.add(lEdge);

        rEdge.setPickable(true);
        rEdge.setSelectable(true);
        lEdge.setPickable(true);
        lEdge.setSelectable(true);

        application.addElement(rEdge);
        application.addElement(lEdge);

        TDebug.println(0, "Seafloor: EdgeNode add at : " + rEdge.getPosition());

        Collection<FieldLine> theseLines = new ArrayList<FieldLine>();
        //FluxFieldLine fl;
        FieldLine fl;
        //RelativeFLine fl;
        int numlines = 6;
        double flux;
        double fluxScale = rightEdges.size() % 2 == 0 ? 1. : -1.;
        int buildDir = rightEdges.size() % 2 == 1 ? FieldLine.BUILD_POSITIVE : FieldLine.BUILD_NEGATIVE;
        buildDir = FieldLine.BUILD_BOTH;
        //int buildDir = FieldLine.BUILD_POSITIVE;
        /*for (int i = 0; i < numlines; i++) {
         flux = fluxScale*(i/6.)*10;
         //fl = new FluxFieldLine(flux,rEdge,true,true);
         //fl = new FluxFieldLine(flux,new Vector3d(), new Vector3d(),1.);
         double range = getRange()*5;
         double frac = (i*1.0)/numlines;
         fl = new FieldLine(new Vector3d(frac,0,0),Field.B_FIELD);
         //fl = new RelativeFLine(rEdge,(i/6.)*2*Math.PI);
         fl.setType(Field.B_FIELD);
         fl.setColorMode(FieldLine.COLOR_FLAT);
         //fl.setSymmetryCount(200);
         fl.setBuildDir(buildDir);
         ((FieldLine)fl).setMinDistance(2.*((FieldLine)fl).getMinDistance());
         application.addElement(fl);
         theseLines.add(fl);
         System.out.println("built new FluxFieldLine with at edge: " + rightEdges.size() + " with flux: " + flux);
         }*/

        rightFieldLines.addAll(theseLines);

    }

    public void removeStripe(int stripeIndex) {

        // actually, you shouldn't be able to remove by index, because that will screw up the 
        // the dip reversals.
        // either remove the last stripe at the edge, or, if we want to be fancy, "seal" the gap
        // left by removing an arbitrary stripe by merging its neighbors in to one mega stripe.

        // REMEMBER TO REMOVE FIELDLINES ASSOCIATED WITH EDGE NODES TOO

        // i hope this removes all references to the EdgeNode
        application.removeElement(rightEdges.get(rightEdges.size() - 1));
        application.removeElement(leftEdges.get(leftEdges.size() - 1));
        rightEdges.remove(rightEdges.size() - 1);
        leftEdges.remove(leftEdges.size() - 1);

        application.removeElement(rightStripes.get(rightStripes.size() - 1));
        application.removeElement(leftStripes.get(leftStripes.size() - 1));
        rightStripes.remove(rightStripes.size() - 1);
        leftStripes.remove(leftStripes.size() - 1);

    }

    public Vector3d getB(Vector3d pos) {
        Vector3d totalB = new Vector3d();
        // this should iterate through SeafloorStripes and add up each contribution
        Vector3d bStripes = getBStripes(pos);

        totalB.add(bStripes);
        // then we add the global field?
        //
        //

        //TDebug.println(0,"Seafloor getB: " + totalB);
        return totalB;
    }

    public Vector3d getB(Vector3d x, double t) {
        return getB(x);
    }

    public double getBFlux(Vector3d pos) {
        double totalBF = 0.;

        Iterator it = rightStripes.iterator();
        while (it.hasNext()) {
            StripeNode sn = (StripeNode) it.next();
            totalBF += sn.getBFlux(pos);
            //TDebug.println(0,"Stripe dip" + sn.getMoment());
        }

        it = leftStripes.iterator();
        while (it.hasNext()) {
            StripeNode sn = (StripeNode) it.next();
            totalBF += sn.getBFlux(pos);
        }

        return totalBF;
    }

    public boolean isGeneratingB() {
        return generatingBField;
    }

    protected Vector3d getBStripes(Vector3d pos) {
        // Here we return the field just from the stripes
        Vector3d totalB = new Vector3d();

        Iterator it = rightStripes.iterator();
        while (it.hasNext()) {
            StripeNode sn = (StripeNode) it.next();
            totalB.add(sn.getB(pos));
            //TDebug.println(0,"Stripe dip" + sn.getMoment());
        }

        it = leftStripes.iterator();
        while (it.hasNext()) {
            StripeNode sn = (StripeNode) it.next();
            totalB.add(sn.getB(pos));
        }

        return totalB;
    }

    protected class StripeNode extends EngineRendered { // may want to make this a EngineRendered as well

        private static final long serialVersionUID = 3257289140767766581L;

        int stripeIndex; // will calculate its position as the midpoint between startPos and endPos

        double width;
        Vector3d startPos;
        Vector3d endPos;

        Vector3d moment;
        double strike;
        Matrix3d arrowRot;

        // CONSTRUCTORS //////////
        public StripeNode() {
            stripeIndex = 0;
            width = 1.0;
            startPos = new Vector3d();
            endPos = new Vector3d(width, 0., 0.);
            moment = new Vector3d(0., 1., 0.);
            arrowRot = new Matrix3d();

            mNode = makeNode();
        }

        public StripeNode(int myIndex) {
            this();
            stripeIndex = myIndex;
        }

        public double getStrike() {
            return this.strike;
        }

        public void setStrike(double myStrike) {
            this.strike = myStrike;
        }

        public Vector3d getMoment() {
            return this.moment;
        }

        public void setMoment(Vector3d dip) {
            this.moment = dip;
            rotateArrow();
        }

        public int getStripeIndex() {
            return stripeIndex;
        }

        public void setStripeIndex(int myIndex) {
            this.stripeIndex = myIndex;
        }

        public double getWidth() {
            //Vector3d r = new Vector3d(endPos);
            //r.sub(startPos);
            //width = r.length();
            return width;
        }

        public void setWidth(double mywidth) {
            this.width = mywidth;
        }

        public Vector3d getStartPos() {
            return startPos;
        }

        public void setStartPos(Vector3d start) {
            this.startPos = start;
            adjustPosition();
            //TDebug.println(0,"Stripe: " + stripeIndex + " setting startPos to: " + start);

        }

        public Vector3d getEndPos() {
            return endPos;
        }

        public void setEndPos(Vector3d end) {
            this.endPos = end;
            adjustPosition();
            //TDebug.println(0,"Stripe: " + stripeIndex + " setting endPos to: " + end + " startPos: " + startPos);
        }

        public Vector3d getB(Vector3d pos) {
            double Bx, By;
            double offset = 0.0001;

            Bx = (calculatePotential(new Vector3d(pos.x - offset, pos.y, pos.z)) - calculatePotential(new Vector3d(
                pos.x + offset, pos.y, pos.z)))
                / offset;
            By = (calculatePotential(new Vector3d(pos.x, pos.y - offset, pos.z)) - calculatePotential(new Vector3d(
                pos.x, pos.y + offset, pos.z)))
                / offset;

            Vector3d tmp = new Vector3d(Bx, By, 0.);

            return tmp;
            //return new Vector3d(-pos.y,pos.x,0.);
        }

        public double getBFlux(Vector3d pos) {
            double x = pos.x;
            double y = pos.y;

            double px = moment.x;
            double py = moment.y;

            double a = this.startPos.x;
            double b = this.endPos.x;

            //			 I have the start and end points of the slabs defined as radially outwards from the origin, but these 
            // equations assume that the start and end points are defined left-to-right across the rift.  
            // fix: if an endpoint is negative, we assume it belongs to one of the slabs on the left, and we reverse the 
            // start and end points.
            if (b < 0) {
                double temp = a;
                a = b;
                b = temp;
            }

            double P = px
                * Math.atan(((b - a) * y) / (Math.pow((x - (a + b) * 0.5), 2) + y * y - Math.pow(b - a, 2) * 0.25))
                + 0.5
                * py
                * Math.log((Math.pow(((x - (a + b) * 0.5) - (b - a) * 0.5), 2) + y * y)
                    / (Math.pow(((x - (a + b) * 0.5) + (b - a) * 0.5), 2) + y * y));

            return P;
        }

        public double calculatePotential(Vector3d pos) {

            double x = pos.x;
            double y = pos.y;

            double px = moment.x;
            double py = moment.y;

            double a = this.startPos.x;
            double b = this.endPos.x;

            // I have the start and end points of the slabs defined as radially outwards from the origin, but these 
            // equations assume that the start and end points are defined left-to-right across the rift.  
            // fix: if an endpoint is negative, we assume it belongs to one of the slabs on the left, and we reverse the 
            // start and end points.
            if (b < 0) {
                double temp = a;
                a = b;
                b = temp;
            }

            double P = py
                * Math.atan(((b - a) * y) / (Math.pow((x - (a + b) * 0.5), 2) + y * y - Math.pow(b - a, 2) * 0.25))
                - 0.5
                * px
                * Math.log((Math.pow(((x - (a + b) * 0.5) - (b - a) * 0.5), 2) + y * y)
                    / (Math.pow(((x - (a + b) * 0.5) + (b - a) * 0.5), 2) + y * y));

            return P;

        }

        protected void adjustPosition() {
            Vector3d r = new Vector3d(endPos);
            r.sub(startPos);
            double myWidth = r.length();
            r.scale(0.5);

            Vector3d s = new Vector3d(startPos);
            s.add(r);

            super.setPosition(s);
            setWidth(myWidth);
            renderFlags |= SCALE_CHANGE;
            //mNode.setScale(new Vector3d(myWidth,1.,1.));

            //TDebug.println(0,"Setting stripe: " + stripeIndex + " with startPos: " + startPos + " and endPos: " + endPos + " to position: " + s);

            // do i need this?
            if (theEngine != null) {
                theEngine.requestSpatial();
            }
        }

        public void render() {

            if ((renderFlags & ROTATION_CHANGE) == ROTATION_CHANGE) {
                ((ArrowWallNode) mNode).setArrowRotation(arrowRot);
                renderFlags ^= ROTATION_CHANGE;
            }
            if ((renderFlags & SCALE_CHANGE) == SCALE_CHANGE) {
                mNode.setScale(new Vector3d(width, 0.1, 1.));
                renderFlags ^= SCALE_CHANGE;
            }
            super.render();

        }

        protected TNode3D makeNode() {
            //TShapeNode node = (TShapeNode) new SphereNode");
            TNode3D node = new ArrowWallNode(); //(TNode3D) new ArrowWallNode");
            node.setScale(1.0);
            node.setElement(this);

            //node.setColor(mColor);
            Appearance app = ((ArrowWallNode) node).getFillAppearance();
            ColoringAttributes ca = app.getColoringAttributes();
            ca.setColor(new Color3f(1.0f, 0.8f, 0.8f));
            app.setColoringAttributes(ca);
            ((WallNode) node).setFillAppearance(app);
            //node.setScale(new Vector3d(0.25,1.,1.));
            return node;
        }

        protected void rotateArrow() {
            double angle = moment.angle(new Vector3d(0, 1, 0));
            if (moment.x != 0) angle *= moment.x / Math.abs(moment.x);
            //System.out.println("angle =" + angle);
            AxisAngle4d aa = new AxisAngle4d(new Vector3d(0, 0, -1), angle);

            arrowRot.set(aa);
            renderFlags |= ROTATION_CHANGE;

        }

    }

    public class EdgeNode extends EngineRendered {

        private static final long serialVersionUID = 3256723978839013427L;
        int edgeIndex; // this should be its index in whatever ArrayList it's in...
        int sideIndex; // this should be either 0 or 1 to indicate WHICH list it's in...

        protected void createBounds() {
            bounds = new BoundingBox(new Point3d(-0.1, -0.1, -0.1), new Point3d(0.1, 0.1, 0.1));
        }

        public int getEdgeIndex() {
            return edgeIndex;
        }

        public void setEdgeIndex(int myIndex) {
            this.edgeIndex = myIndex;
        }

        public int getSideIndex() {
            return sideIndex;
        }

        public void setSideIndex(int mySideIndex) {
            this.sideIndex = mySideIndex;
        }

        // POSITION OF EDGES (AND STRIPES) MUST BE CONSTRAINED TO THEIR SIDE!!!
        // ALSO, THEY SHOULD BE CONSTRAINED BETWEEN THE POSITIONS OF THE EDGES IN ON EACH SIDE OF THEM!!!
        protected Vector3d constrainEdgePos(Vector3d unconstrained) {
            //Vector3d constrained = new Vector3d();
            // kill any y component (should maybe generalize this to grab the component along spreadAxis)
            unconstrained.y = 0;
            // ok, this is a quick and dirty constraint implementation that assumes these things are moving along the x axis
            // ... which they are.  if they ever weren't for whatever reason, this would need to be fleshed out a bit.
            // ie. we'd be looking at components along spreadAxis, for example.
            if (sideIndex == 1) {
                // sideIndex == 1 means this is an edge on the "positive" side of the strip (ie. is listed in "rightEdges")
                if (rightEdges.size() == 1) {
                    // make sure x-component is greater than zero
                    if (unconstrained.x < 0.) unconstrained.x = 0.;
                } else if (edgeIndex == 0) {
                    // first edge, constrain between zero and second edge
                    unconstrained.x = Math.min(Math.max(0, unconstrained.x), ((EdgeNode) rightEdges.get(1))
                        .getPosition().x);
                } else if (rightEdges.size() > 1 && edgeIndex != rightEdges.size() - 1) {
                    // all edges except the last one
                    unconstrained.x = Math.min(Math.max(((EdgeNode) rightEdges.get(edgeIndex - 1)).getPosition().x,
                        unconstrained.x), ((EdgeNode) rightEdges.get(edgeIndex + 1)).getPosition().x);
                } else {
                    // should just be the last edge
                    unconstrained.x = Math.max(((EdgeNode) rightEdges.get(edgeIndex - 1)).getPosition().x,
                        unconstrained.x);
                }

            } else {

                // same thing, except since we're on the "negative" side, all the mins and maxes are reversed, etc.
                if (leftEdges.size() == 1) {
                    // make sure x-component is greater than zero
                    if (unconstrained.x > 0.) unconstrained.x = 0.;
                } else if (edgeIndex == 0) {
                    // first edge, constrain between zero and second edge
                    unconstrained.x = Math.max(Math.min(0, unconstrained.x), ((EdgeNode) leftEdges.get(1))
                        .getPosition().x);
                } else if (leftEdges.size() > 1 && edgeIndex != leftEdges.size() - 1) {
                    // all edges except the last one
                    unconstrained.x = Math.max(Math.min(((EdgeNode) leftEdges.get(edgeIndex - 1)).getPosition().x,
                        unconstrained.x), ((EdgeNode) leftEdges.get(edgeIndex + 1)).getPosition().x);
                } else {
                    // should just be the last edge
                    unconstrained.x = Math.min(((EdgeNode) leftEdges.get(edgeIndex - 1)).getPosition().x,
                        unconstrained.x);
                }

            }
            return unconstrained;
        }

        public void setPosition(Vector3d position) {
            //pos.y = 0.;  //ghetto constraint ...
            Vector3d pos = new Vector3d(constrainEdgePos(position));
            super.setPosition(pos);
            Vector3d tmp = new Vector3d(pos);
            tmp.scale(-1.);
            // adjust the StripeNodes associated with this EdgeNode
            // this should also adjust its mirror node, but use a different method to avoid 
            // an infinite loop

            if (sideIndex == 1) {
                ((StripeNode) (rightStripes.get(edgeIndex))).setEndPos(pos);
                ((StripeNode) (leftStripes.get(edgeIndex))).setEndPos(tmp);

                if (rightStripes.size() > edgeIndex + 1) {
                    ((StripeNode) (rightStripes.get(edgeIndex + 1))).setStartPos(pos);
                    ((StripeNode) (leftStripes.get(edgeIndex + 1))).setStartPos(tmp);
                }

                ((EdgeNode) (leftEdges.get(edgeIndex))).setEdgePosition(tmp);
                //TDebug.println(0,"pos: " + pos + " tmp: " + tmp);

                //				//UPDATE FIELDLINES NEW
                //				if (rightFieldLines.get(edgeIndex) != null) {
                //					Iterator it = ((ArrayList)rightFieldLines.get(edgeIndex)).iterator();
                //					int len = rightFieldLines.size();
                //					int j = 0;
                //					while (it.hasNext()) {
                //						FieldLine fl = ((FieldLine)it.next());
                //						double frac = ((j*1.0)/len)*getRange();
                //						fl.setPosition(new Vector3d(frac,0,0));
                //						j++;
                //					}
                //				}

                //				 UPDATE FIELDLINES IN PROGRESS
                /*
                 if (rightFieldLines.get(edgeIndex) != null) {
                 Iterator it = ((ArrayList)rightFieldLines.get(edgeIndex)).iterator();
                 double len;
                 while (it.hasNext()) {
                 FluxFieldLine fl = ((FluxFieldLine)it.next());
                 fl.setObjPos(pos);
                 if (edgeIndex > 0 && edgeIndex < rightEdges.size() - 1) {
                 Vector3d prevpos = new Vector3d(((EdgeNode)rightEdges.get(edgeIndex-1)).getPosition());
                 prevpos.sub(pos);
                 len = prevpos.length();
                 fl.setSearchAxis(prevpos);
                 fl.setObjRadius(len);
                 double nextWidth = ((StripeNode)rightStripes.get(edgeIndex+1)).getWidth();
                 for (int i = 0; i < ((ArrayList)rightFieldLines.get(edgeIndex+1)).size(); i++) {
                 ((FluxFieldLine)((ArrayList)rightFieldLines.get(edgeIndex+1)).get(i)).setObjRadius(nextWidth);
                 }
                 } else if (edgeIndex == rightEdges.size() - 1) {
                 Vector3d prevpos = new Vector3d(((EdgeNode)rightEdges.get(edgeIndex)).getPosition());
                 prevpos.sub(pos);
                 len = prevpos.length();
                 fl.setSearchAxis(prevpos);
                 fl.setObjRadius(len);
                 } else {
                 len = tmp.length();
                 fl.setSearchAxis(new Vector3d(tmp));
                 fl.setObjRadius(len);
                 }
                 }
                 }
                 */
                //////////////////////////////////

            } else {
                ((StripeNode) (leftStripes.get(edgeIndex))).setEndPos(pos);
                ((StripeNode) (rightStripes.get(edgeIndex))).setEndPos(tmp);
                if (leftStripes.size() > edgeIndex + 1) {
                    ((StripeNode) (leftStripes.get(edgeIndex + 1))).setStartPos(pos);
                    ((StripeNode) (rightStripes.get(edgeIndex + 1))).setStartPos(tmp);
                }

                ((EdgeNode) (rightEdges.get(edgeIndex))).setEdgePosition(tmp);

            }

            //printFieldLines();
            //TDebug.println(0,"Stripe " + edgeIndex + " width: " + ((StripeNode)(rightStripes.get(edgeIndex))).getWidth());
            updateScanLine();

            if (theEngine != null) {
                theEngine.requestSpatial();
            }
        }

        public void setEdgePosition(Vector3d pos) {
            // this sets the positions of the edge node without adjusting the Stripes
            super.setPosition(pos);
            if (theEngine != null) {
                theEngine.requestSpatial();
            }
            /*
             Vector3d tmp = new Vector3d(pos);
             tmp.scale(-1.);
             if (sideIndex == 1) {
             ((StripeNode)(rightStripes.get(edgeIndex))).setEndPos(pos);
             if (rightStripes.size() > edgeIndex) {
             ((StripeNode)(rightStripes.get(edgeIndex+1))).setStartPos(pos);
             }
             } else {
             ((StripeNode)(leftStripes.get(edgeIndex))).setEndPos(pos);
             if (leftStripes.size() > edgeIndex) {
             ((StripeNode)(leftStripes.get(edgeIndex+1))).setStartPos(pos);
             }
             }
             */

        }

        protected TNode3D makeNode() {
            //TShapeNode node = (TShapeNode) new SphereNode");
            TNode3D node = (TNode3D) new WallNode();
            node.setScale(1.);
            node.setElement(this);
            //node.setColor(mColor);
            Appearance app = ((WallNode) node).getFillAppearance();
            ColoringAttributes ca = app.getColoringAttributes();
            TransparencyAttributes ta = app.getTransparencyAttributes();
            ta.setTransparency(0.5f);
            ca.setColor(new Color3f(0.75f, 0.75f, 0.75f));
            app.setColoringAttributes(ca);
            ((WallNode) node).setFillAppearance(app);
            node.setScale(new Vector3d(0.25, 1.25, 1.));
            return node;
        }

//        public void printFieldLines() {
//            for (int i = 0; i < rightFieldLines.size(); i++) {
//                ArrayList<FieldLine> a = rightFieldLines.get(i);
//                for (int j = 0; j < a.size(); j++) {
//                    FluxFieldLine fl = ((FluxFieldLine) a.get(j));
//                    System.out.println("fieldline scan start: " + fl.getObjPos() + " dir: " + fl.getSearchAxis()
//                        + " distance: " + fl.getObjRadius());
//                    System.out.println("associated EdgeNode pos: " + ((EdgeNode) rightEdges.get(i)).getPosition());
//                }
//            }
//        }

    }

    /**
     * @return Returns the latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude The latitude to set.
     */
    public void setLatitude(double latitude) {
        this.latitude = Math.min(Math.max(-90., latitude), 90);
        calcNewGeometry(this.latitude, this.strike, this.creationLatitude, this.creationStrike);
    }

    private void calcNewGeometry(double lat, double st) {
        // this should calculate the earth field direction and slab dipole moments based on the selected latitude and strike.
        // the dipole moment is the projection of the earth's field on the spreading cross section.

        double colat = 90. - lat;
        double colatrad = (colat / 360.) * 2 * Math.PI;
        double strad = (st / 360.) * 2 * Math.PI;
        earthField.set(Math.sin(colatrad) * Math.sin(strad), -2 * Math.cos(colatrad), Math.cos(strad)
            * Math.sin(colatrad));
        //earthField.scale(1./Math.pow(3*Math.pow(Math.cos(colatrad),2)-1,0.5));
        Vector3d strikedip = new Vector3d();
        strikedip.set(earthField.x, earthField.y, 0);
        this.setDip(strikedip);

        theEngine.requestSpatial();

    }

    private void calcNewGeometry(double lat, double st, double clat, double cst) {
        // this should calculate the earth field direction and slab dipole moments based on the selected latitude and strike.
        // the dipole moment is the projection of the earth's field on the spreading cross section.
        if (bEnableCreationVars) {
            double colat = 90. - lat;
            double colatrad = (colat / 360.) * 2 * Math.PI;
            double strad = (st / 360.) * 2 * Math.PI;
            earthField.set(Math.sin(colatrad) * Math.sin(strad), -2 * Math.cos(colatrad), Math.cos(strad)
                * Math.sin(colatrad));
            //earthField.scale(1./Math.pow(3*Math.pow(Math.cos(colatrad),2)-1,0.5));

            double ccolat = 90. - clat;
            double ccolatrad = (ccolat / 360.) * 2 * Math.PI;
            double cstrad = (cst / 360.) * 2 * Math.PI;
            creationEarthField.set(Math.sin(ccolatrad) * Math.sin(cstrad), -2 * Math.cos(ccolatrad), Math.cos(cstrad)
                * Math.sin(ccolatrad));

            Vector3d strikedip = new Vector3d();
            strikedip.set(creationEarthField.x, creationEarthField.y, 0);
            this.setDip(strikedip);

            theEngine.requestSpatial();
        } else {
            calcNewGeometry(lat, st);
        }

        renderFlags |= GEOMETRY_CHANGE;
        updateScanLineArrows();

    }

    public double getRange() {
        // this will return the x component of the outermost edge
        return ((EdgeNode) (rightEdges.get(rightEdges.size() - 1))).getPosition().x;
    }

    /**
     * @return Returns the creationStrike.
     */
    public double getCreationStrike() {
        return creationStrike;
    }

    /**
     * @param creationStrike The creationStrike to set.
     */
    public void setCreationStrike(double creationStrike) {
        this.creationStrike = creationStrike;
        calcNewGeometry(this.latitude, this.strike, this.creationLatitude, this.creationStrike);
    }

    /**
     * @return Returns the creationLatitude.
     */
    public double getCreationLatitude() {
        return creationLatitude;
    }

    /**
     * @param creationLatitude The creationLatitude to set.
     */
    public void setCreationLatitude(double creationLatitude) {
        this.creationLatitude = creationLatitude;
        calcNewGeometry(this.latitude, this.strike, this.creationLatitude, this.creationStrike);
    }

    /**
     * @return Returns the creationEarthField.
     */
    public Vector3d getCreationEarthField() {
        return creationEarthField;
    }

    /**
     * @param creationEarthField The creationEarthField to set.
     */
    public void setCreationEarthField(Vector3d creationEarthField) {
        this.creationEarthField = creationEarthField;
    }

    /**
     * @return Returns the bEnableCreationVars.
     */
    public boolean isBEnableCreationVars() {
        return bEnableCreationVars;
    }

    /**
     * @param enableCreationVars The bEnableCreationVars to set.
     */
    public void setBEnableCreationVars(boolean enableCreationVars) {
        bEnableCreationVars = enableCreationVars;
        calcNewGeometry(this.latitude, this.strike, this.creationLatitude, this.creationStrike);
    }

    /**
     * @return Returns the scanHeight.
     */
    public double getScanHeight() {
        return scanHeight;
    }

    /**
     * @param scanHeight The scanHeight to set.
     */
    public void setScanHeight(double scanHeight) {
        this.scanHeight = scanHeight;
        updateScanLine();
        theEngine.requestSpatial();
    }

    public void render() {

        //		if ((renderFlags & ROTATION_CHANGE) == ROTATION_CHANGE) {
        //			((ArrowWallNode)mNode).setArrowRotation(arrowRot);
        //			renderFlags ^= ROTATION_CHANGE;
        //		}if ((renderFlags & SCALE_CHANGE) == SCALE_CHANGE) {
        //			mNode.setScale(new Vector3d(width,1.,1.));
        //			renderFlags ^= SCALE_CHANGE;
        //		} 
        if ((renderFlags & GEOMETRY_CHANGE) == GEOMETRY_CHANGE) {
            Vector3d dir = new Vector3d(0, 5, 0);
            Vector3d field = new Vector3d(earthField);
            field.normalize();
            dir.add(field);
            //dir.normalize();

            //((ArrowNode)mNode).setFromTo(new Vector3d(0,5.,0), dir);
            mNode.setPosition(new Vector3d(0, 5, 0));
            mNode.setDirection(field);
        }
        super.render();

    }

    protected TNode3D makeNode() {
        //TShapeNode node = (TShapeNode) new SphereNode");
        //TNode3D node = (TNode3D) new SolidArrowNode");
        TNode3D node = new SolidArrowNode();
        ((SolidArrowNode) node).setGeometry(teal.render.geometry.Cylinder.makeGeometry(20, 0.05, 1, 0.5)
            .getIndexedGeometryArray(true));
        node.setScale(4.);
        node.setElement(this);
        ((TShapeNode) node).setColor(new Color3f(new Color(0, 0, 255)));
        ((TShapeNode) node).setTransparency(0.2f);

        //Appearance app = ((SolidArrowNode)node).getAppearance();
        //ColoringAttributes ca = new ColoringAttributes();
        //ca.setColor(new Color3f(0f,0.f,0.8f));
        //app.setColoringAttributes(ca);
        //((ArrowNode)node).setAppearance(app);
        //((WallNode)node).setFillAppearance(app);
        //node.setScale(new Vector3d(0.25,1.,1.));
        return node;
    }

    private void configureFLines() {
        FieldLine fl;
        int numlines = 40;
        double range = 10;//getRange()*5;
        double flux;
        double fluxScale = rightEdges.size() % 2 == 0 ? 1. : -1.;
        int buildDir = rightEdges.size() % 2 == 1 ? FieldLine.BUILD_POSITIVE : FieldLine.BUILD_NEGATIVE;
        buildDir = FieldLine.BUILD_BOTH;
        //int buildDir = FieldLine.BUILD_POSITIVE;
        for (int i = 0; i < numlines; i++) {
            flux = fluxScale * (i / 6.) * 10;
            //fl = new FluxFieldLine(flux,rEdge,true,true);
            //fl = new FluxFieldLine(flux,new Vector3d(), new Vector3d(),1.);

            double frac = (i * 1.0) / numlines;
            fl = new FieldLine(new Vector3d(frac * range, 0.1, 0), Field.B_FIELD);
            //fl = new RelativeFLine(rEdge,(i/6.)*2*Math.PI);
            fl.setType(Field.B_FIELD);
            fl.setColorMode(FieldLine.COLOR_FLAT);
            //fl.setSymmetryCount(200);
            fl.setBuildDir(buildDir);
            ((FieldLine) fl).setMinDistance(2. * ((FieldLine) fl).getMinDistance());
            //application.addElement(fl);
            fieldlines.add(fl);
        }

        range = -range;
        for (int i = 0; i < numlines; i++) {
            flux = fluxScale * (i / 6.) * 10;
            //fl = new FluxFieldLine(flux,rEdge,true,true);
            //fl = new FluxFieldLine(flux,new Vector3d(), new Vector3d(),1.);

            double frac = (i * 1.0) / numlines;
            fl = new FieldLine(new Vector3d(frac * range, 0.1, 0), Field.B_FIELD);
            //fl = new RelativeFLine(rEdge,(i/6.)*2*Math.PI);
            fl.setType(Field.B_FIELD);
            fl.setColorMode(FieldLine.COLOR_FLAT);
            //fl.setSymmetryCount(200);
            fl.setBuildDir(buildDir);
            ((FieldLine) fl).setMinDistance(2. * ((FieldLine) fl).getMinDistance());
            //application.addElement(fl);
            fieldlines.add(fl);
        }
        //theseLines.add(fl);
        //System.out.println("built new FluxFieldLine with at edge: " + rightEdges.size() + " with flux: " + flux);
    }

    private void updateScanLine() {
        Vector3d leftEdge = new Vector3d(getRange() * -1, getScanHeight(), 0.);
        Vector3d rightEdge = new Vector3d(getRange(), getScanHeight(), 0.);

        scanLine.setPosition(leftEdge);
        scanLine.setDrawTo(rightEdge);
        updateScanLineArrows();
    }

    private void updateScanLineArrows() {
        int arrowRes = 40;
        if (scanLineFieldArrows.size() == 0) {
            // make them

            for (int i = 0; i < arrowRes; i++) {
                FieldVector fv = new FieldVector(new Vector3d(), Field.B_FIELD, true);
                fv.setScaleFactor(0.1);
                scanLineFieldArrows.add(fv);
                application.addElement(fv);

                // component arrows
                Arrow a = new Arrow(new Vector3d(), new Vector3d());
                a.setColor(new Color(255, 150, 50));
                scanLineCompArrows.add(a);
                application.addElement(a);
            }

        } else {
            //update them
            FieldVector fv;
            Arrow a;
            Iterator it = scanLineFieldArrows.iterator();
            int j = 0;
            while (it.hasNext()) {
                fv = ((FieldVector) it.next());
                if (showScanLineFieldArrows == false) {
                    fv.setDrawn(false);
                } else {
                    double xpos = ((double) j / arrowRes) * getRange() * 2;
                    fv.setPosition(-getRange() + xpos, getScanHeight(), 0);
                    fv.setDrawn(true);
                }

                j++;
            }

            it = scanLineCompArrows.iterator();
            j = 0;
            while (it.hasNext()) {
                a = ((Arrow) it.next());
                if (showScanLineCompArrows == false) {
                    a.setDrawn(false);
                } else {
                    double xpos = ((double) j / arrowRes) * getRange() * 2;
                    Vector3d vecpos = new Vector3d(-getRange() + xpos, getScanHeight(), 0);
                    Vector3d e = new Vector3d(earthField);
                    //Vector3d f = new Vector3d(((FieldVector)scanLineFieldArrows.get(j)).getValue());
                    Vector3d f = new Vector3d(((EMEngine)theEngine).getBField().get(new Vector3d(vecpos)));
                    f.scale(0.1);
                    e.normalize();
                    double dot = f.dot(e);

                    e.scale(dot);
                    e.add(new Vector3d(vecpos));
                    a.setPosition(vecpos);
                    a.setDrawTo(e);
                    a.setDrawn(true);
                }
                j++;
            }

        }
        theEngine.requestSpatial();
    }

    public void setFieldlineVisibility(boolean vis) {
        Iterator it = fieldlines.iterator();
        while (it.hasNext()) {
            ((FieldLine) it.next()).setDrawn(vis);
        }
        theEngine.requestSpatial();
    }

    /**
     * @return Returns the showScanArrows.
     */
    public boolean isShowScanArrows() {
        return showScanArrows;
    }

    /**
     * @param showScanArrows The showScanArrows to set.
     */
    public void setShowScanArrows(boolean showScanArrows) {
        this.showScanArrows = showScanArrows;
        updateScanLineArrows();
    }

    /**
     * @return Returns the showScanLineCompArrows.
     */
    public boolean isShowScanLineCompArrows() {
        return showScanLineCompArrows;
    }

    /**
     * @param showScanLineCompArrows The showScanLineCompArrows to set.
     */
    public void setShowScanLineCompArrows(boolean showScanLineCompArrows) {
        this.showScanLineCompArrows = showScanLineCompArrows;
        updateScanLineArrows();
    }

    /**
     * @return Returns the showScanLineFieldArrows.
     */
    public boolean isShowScanLineFieldArrows() {
        return showScanLineFieldArrows;
    }

    /**
     * @param showScanLineFieldArrows The showScanLineFieldArrows to set.
     */
    public void setShowScanLineFieldArrows(boolean showScanLineFieldArrows) {
        this.showScanLineFieldArrows = showScanLineFieldArrows;
        updateScanLineArrows();
    }

	/**
	 * @return Returns the fieldlines.
	 */
	public ArrayList<FieldLine> getFieldlines() {
		return fieldlines;
	}

	/**
	 * @param fieldlines The fieldlines to set.
	 */
	public void setFieldlines(ArrayList<FieldLine> fieldlines) {
		this.fieldlines = fieldlines;
	}
}
