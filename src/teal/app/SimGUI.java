/*
 * TEALsim - TEAL Project, CECI/MIT
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SimGUI.java,v 1.23 2010/07/12 14:58:26 stefan Exp $
 * 
 */

package teal.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import teal.core.HasID;
import teal.framework.TAbstractFramework;
import teal.framework.TFramework;
import teal.framework.TGui;
import teal.render.viewer.TViewer;
import teal.render.viewer.Viewer;
import teal.sim.engine.EngineControl;
import teal.ui.UIPanel;
import teal.ui.swing.JTaskPane;
import teal.ui.swing.PercentLayout;
import teal.ui.swing.plaf.LookAndFeelAddons;
import teal.ui.swing.plaf.aqua.AquaLookAndFeelAddons;
import teal.util.TDebug;

/**
 * Provides the default implementation of TGUI for the TEAL 3D simulations,
 * used in both SimPlayer and TealSimApp. Layout includes a viewer location
 * and scrolled panel for controls.
 *
 * @see teal.framework.TGui
 *
 * @author Andrew McKinney
 * @version $Revision: 1.23 $ 
 */

public class SimGUI extends UIPanel implements TGui {

    private static final long serialVersionUID = 3256437010565576760L;

//    protected TFramework fWork;

    protected UIPanel viewPane;
    protected JTaskPane controlPane;
    protected JScrollPane scrollPane;

    protected static final Border VIEWPANE_BORDER = BorderFactory.createEmptyBorder(0, 0, 0, 10);
    protected static final Border SIM_BORDER = BorderFactory.createEmptyBorder(10, 10, 10, 10);

    public SimGUI() {
        super();
        
        try {
            LookAndFeelAddons.setAddon(AquaLookAndFeelAddons.class);
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        
        setPreferredSize(new Dimension(1024,768));
        
        setID("SimGUI");
        setLayout(new BorderLayout());
        setBorder(SIM_BORDER);

        viewPane = new UIPanel();
        viewPane.setBorder(VIEWPANE_BORDER);
        viewPane.setLayout(new BorderLayout());
        add(viewPane, BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        controlPane = new JTaskPane();
        controlPane.setLayout(new PercentLayout(PercentLayout.VERTICAL, 5));
        tabs.addTab("Controls", controlPane);

        scrollPane = new JScrollPane(controlPane);
        scrollPane.setBorder(null);
        scrollPane.setVisible(false);
        add(scrollPane, BorderLayout.EAST);
    }

//    public TFramework getFramework() {
//        return fWork;
//    }

//    public void setFramework(TFramework framework) {
//        fWork = framework;
//    }

    public void addTElement(HasID te) {
        TDebug.println(1, "SimGUI addTElement: " + te);
        if (te instanceof Viewer) {
            addComponent((Viewer) te);
        } else if (te instanceof Component) {
            addComponent((Component) te);
        } else if (te instanceof EngineControl) {
            addComponent((EngineControl) te);
        }
    }

    public void removeTElement(HasID te) {
        if (te instanceof Viewer) {
            viewPane.remove((Viewer) te);
        } else if (te instanceof Component) {
            TDebug.println(1, "gui removing TElement" + te.toString());
            controlPane.remove((Component) te);
        } else if (te instanceof EngineControl) {
            viewPane.remove((Component) te);
        }
    }

    public void addComponent(Component te) {
        TDebug.println(1, "SimGUI addComponent: " + te);
        if (te instanceof Viewer) {
            viewPane.add((Viewer) te, BorderLayout.CENTER);
        } else if (te instanceof EngineControl) {
            viewPane.add((Component) te, BorderLayout.SOUTH);
        	te.setVisible( true ) ;
        } else if (te instanceof Component) {
            scrollPane.setVisible(true);
            controlPane.add((Component) te);
        }
    }

    public void removeComponent(Component te) {
        if (te instanceof EngineControl) {
            viewPane.remove(te);
        } else if (te instanceof TViewer) {
            viewPane.remove(te);
        } else if (te instanceof Component) {
            scrollPane.setVisible(false);
            controlPane.remove(te);
        }
    }

    public void removeAll() {
    }

    public JPanel getPanel() {
        return this;
    }

    public void refresh() {
        Graphics g = getGraphics();
        paintAll(g);
    }
}
