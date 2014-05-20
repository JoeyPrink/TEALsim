/*
 * Created on Oct 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package teal.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import teal.core.HasID;
import teal.framework.TAbstractFramework;
import teal.framework.TFramework;
import teal.framework.TGui;
import teal.render.viewer.TViewer;
import teal.render.viewer.Viewer;
import teal.sim.engine.EngineControl;
import teal.ui.UIPanel;
import teal.ui.swing.PercentLayout;
import teal.ui.swing.plaf.LookAndFeelAddons;
import teal.ui.swing.plaf.aqua.AquaLookAndFeelAddons;
import teal.util.TDebug;

/**
 * Alternative user interface that places controls below the 3D viewer.
 * 
 * @author danziger
 *
 */
public class SouthGUI extends UIPanel implements TGui {
	private static final long serialVersionUID = 3256437010565576760L;

//    protected TFramework fWork;
    protected Dimension mSize;

    protected UIPanel viewPane;
    protected UIPanel controlPane;
    protected JScrollPane scrollPane;

    protected static final Border VIEWPANE_BORDER = BorderFactory.createEmptyBorder(0, 0, 0, 10);
    protected static final Border SIM_BORDER = BorderFactory.createEmptyBorder(10, 10, 10, 10);

    public SouthGUI() {
        super();
        setPreferredSize(new Dimension(525,575));
        try {
            LookAndFeelAddons.setAddon(AquaLookAndFeelAddons.class);
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        
        setID("SouthGUI");
        setLayout(new BorderLayout());
        setBorder(SIM_BORDER);

        viewPane = new UIPanel();
        viewPane.setBorder(VIEWPANE_BORDER);
        viewPane.setLayout(new BorderLayout());
        add(viewPane, BorderLayout.CENTER);

        controlPane = new UIPanel();
        controlPane.setLayout(new PercentLayout(PercentLayout.VERTICAL, 5));
        add(controlPane, BorderLayout.SOUTH);
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
        } else if (te instanceof EngineControl) {
            addComponent((EngineControl) te);
        }else if (te instanceof Component) {
            addComponent((Component) te);
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
        } else if (te instanceof Component) {
            //scrollPane.setVisible(true);
            controlPane.add((Component) te);
        }
    }

    public void removeComponent(Component te) {
        if (te instanceof EngineControl) {
            viewPane.remove(te);
        } else if (te instanceof TViewer) {
            viewPane.remove(te);
        } else if (te instanceof Component) {
            //scrollPane.setVisible(false);
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
    
//    public void setPreferredSize(Dimension size) {
//    		mSize = size;
//    }
//    
//    public Dimension getPreferredSize() {
//    		return mSize;
//    }
}
