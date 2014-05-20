/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TToolBar.java,v 1.10 2010/07/23 21:38:07 stefan Exp $
 * 
 */

package teal.framework;

/**
 * 
 * @author mesrob
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.10 $ 
 */
import java.util.*;

import javax.swing.*;

public class TToolBar extends JPanel //implements HasEngine
{
    private static final long serialVersionUID = 3256718502806108466L;
    
    /** Creates new TToolBar */
    //protected TEngine world=null;
    protected Vector hasModel = new Vector();
    protected transient TFramework framework = null;

    protected LinkedHashMap toolbars;

    protected boolean showWorld = true;
    protected boolean showSim = true;
    protected boolean showHelp = true;
    protected boolean showViewer = true;

    //protected JToolBar fileTB=null;
    //protected JToolBar simTB= null;
    //protected JToolBar helpTB= null;
    //protected JToolBar viewerTB=null;

    public TToolBar() {
        super();
        toolbars = new LinkedHashMap();

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        showWorld = true;
        showSim = true;
        showViewer = true;
        showHelp = true;
        //this.add(Box.createGlue());
        setVisible(true);

    }

    /*	
     public TToolBar(TEngine world) {
     this();
     setModel(world);
     }
     */
    public void setFramework(TFramework fw) {
        framework = fw;
    }

    public TFramework getFramework() {
        return framework;
    }

    public void setShowFile(boolean showFile) {
        this.showWorld = showFile;
    }

    public boolean getShowFile() {
        return showWorld;
    }

    public void setShowPhysics(boolean showPhysics) {
        this.showSim = showPhysics;
    }

    public boolean getShowPhysics() {
        return showSim;
    }

    public void setShowHelp(boolean showHelp) {
        this.showHelp = showHelp;
    }

    public boolean getShowHelp() {
        return showHelp;
    }

    public void setShowViewers(boolean showViewers) {
        this.showViewer = showViewers;
    }

    public boolean getShowViewers() {
        return showViewer;
    }

    /*
     public void showMenu(){
     removeAll();
     if(world!=null){
     if (showWorld == true){
     add(get("World"));
     }
     if (showSim == true){
     add(get("Simulations"));
     }
     if(showViewer == true){
     add(get("Viewers"));
     }
     if(showHelp == true){
     add(get("Help"));
     }
     
     }
     this.add(Box.createGlue());
     }
     */
    /*
     public TEngine getModel() {
     return world;
     }
     public void setModel(TEngine world)
     {
     this.world = world;
     for(int i=0;i<hasModel.size();i++){
     if (hasModel.get(i) instanceof HasEngine){
     ((HasEngine)hasModel.get(i)).setModel(world);
     }
     }
     
     }
     */

    public JToolBar add(String name) {
        JToolBar help = (JToolBar) toolbars.get("help");
        if (help != null) {
            remove(help);
        }
        JToolBar tb = new JToolBar(name);
        toolbars.put(name.toLowerCase(), tb);
        add(tb);
        if (help != null) {
            add(help);
        }
        return tb;
    }

    public JToolBar get(String name) {
        JToolBar tb = null;
        tb = (JToolBar) toolbars.get(name.toLowerCase());
        return tb;
    }

    public void remove(String name) {
        JToolBar tb = get(name);
        if (tb != null) {
            toolbars.remove(name.toLowerCase());
            remove(tb);
        }
    }

    public void addAction(Action a) {
    }

    public void removeAction(Action a) {
    }

    public void addAction(String target, Action a) {

        JToolBar m = get(target);
        if (m == null) {
            m = add(target);
        }
        /*
         JButton mi = new JButton();
         mi.setAction(a);
         m.add(mi);
         */
        JButton b = m.add(a);
        b.setActionCommand((String) a.getValue(Action.NAME));
        //b.setToolTipText((String) a.getValue(Action.SHORT_DESCRIPTION));
    }

    public void removeAction(String source, Action a) {

        JToolBar m = get(source);
        if (m != null) {
            for (int j = 0; j < m.getComponentCount(); j++) {
                JButton b = (JButton) m.getComponent(j);
                if (b.getAction() == a)
                ;
                {
                    m.remove(j);
                    break;
                }
            }
        }
    }

}
