/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: UserFieldGenerator.java,v 1.6 2007/07/17 15:46:53 pbailey Exp $
 * 
 */

package teal.physics;

import javax.swing.JFormattedTextField;
import javax.swing.event.*;
import javax.vecmath.Vector3d;

import teal.field.GenericFieldGenerator;
import teal.math.Function;
import teal.sim.SimObj;

/**
 *
 * UserFieldGenerator implements GenericFieldGenerator to produce a field based on a user-supplied function, parsed
 * from JFormattedTextField.  Currently only used in the MappingFields simulation.
 * 
 * @author mesrob
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.6 $ 
 */
public class UserFieldGenerator extends SimObj implements GenericFieldGenerator, DocumentListener {

    private static final long serialVersionUID = 3258407348355020851L;
    
    JFormattedTextField X = null;
    JFormattedTextField Y = null;
    Function Xf = null;
    Function Yf = null;

    /**
     * Constructor for UserFieldGenerator.
     */
    public UserFieldGenerator(JFormattedTextField XX, JFormattedTextField YY) {
        super();
        X = XX;
        Y = YY;
        Xf = new Function(X.getText());
        Yf = new Function(Y.getText());
        X.getDocument().addDocumentListener(this);
        Y.getDocument().addDocumentListener(this);
    }

    public void changedUpdate(DocumentEvent e) {
        if (e.getDocument() == X.getDocument()) {
            Xf = new Function(X.getText());
        }
        if (e.getDocument() == Y.getDocument()) {
            Yf = new Function(Y.getText());
        }
    }

    public void insertUpdate(DocumentEvent e) {
        if (e.getDocument() == X.getDocument()) {
            Xf = new Function(X.getText());
        }
        if (e.getDocument() == Y.getDocument()) {
            Yf = new Function(Y.getText());
        }
    }

    public void removeUpdate(DocumentEvent e) {
        if (e.getDocument() == X.getDocument()) {
            Xf = new Function(X.getText());
        }
        if (e.getDocument() == Y.getDocument()) {
            Yf = new Function(Y.getText());
        }
    }

    
    public Vector3d getVectorField(Vector3d position) {
    	double x = 0.;
        double y = 0.;
        if (Xf != null) x = Xf.evaluateAt(position.x, position.y);
        if (Yf != null) y = Yf.evaluateAt(position.x, position.y);
      return new Vector3d(x, y, 0);
    }
    
    public double getFirstScalarField(Vector3d position) {
    	return 0.;
    }
    
    public double getSecondScalarField(Vector3d position) {
    	return 0.;
    }
    
    
    
//    /**
//     * @see teal.sim.physical.em.GeneratesE#getE(Vector3d, double)
//     */
//    public Vector3d getE(Vector3d position, double time) {
//        double x = 0.;
//        double y = 0.;
//        if (Xf != null) x = Xf.evaluateAt(position.x, position.y);
//        if (Yf != null) y = Yf.evaluateAt(position.x, position.y);
//        return new Vector3d(x, y, 0);
//    }
//
//    /**
//     * @see teal.sim.physical.em.GeneratesE#getE(Vector3d)
//     */
//    public Vector3d getE(Vector3d position) {
//        return getE(position, 0.);
//    }
//
//    /**
//     * @see teal.sim.physical.em.GeneratesE#getEFlux(Vector3d)
//     */
//    public double getEFlux(Vector3d position) {
//        return 0.;
//    }
//
//    public double getEPotential(Vector3d position) {
//        return 0.;
//    }
//
//    /**
//     * @see teal.sim.physical.em.GeneratesE#isGeneratingE()
//     */
//    public boolean isGeneratingE() {
//        return true;
//    }

}
