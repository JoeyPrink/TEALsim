/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TGui.java,v 1.18 2010/07/12 14:58:27 stefan Exp $
 * 
 */

package teal.framework;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;

import teal.core.HasID;

/**
 * 
 * @author mesrob
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.18 $ 
 */
public interface TGui {

//    public TFramework getFramework();

//    public void setFramework(TFramework framework);
    
    public void setPreferredSize(Dimension size);
    
    public Dimension getPreferredSize();

    public void addTElement(HasID te);

    public void addComponent(Component te);

    public void removeTElement(HasID te);

    public void removeComponent(Component te);

    public void removeAll();

    public JPanel getPanel();

    public void refresh();

    public void repaint();

}
