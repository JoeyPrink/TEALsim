/*
 * $Id: HasGUI.java,v 1.2 2007/07/16 22:05:11 pbailey Exp $
 */
package teal.ui;

import teal.framework.TGui;

public interface HasGUI {
    
    public TGui getGui();
    public void setGui(TGui g);
}
