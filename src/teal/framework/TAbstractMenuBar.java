/*
 * $Id: TAbstractMenuBar.java,v 1.2 2007/07/16 22:04:46 pbailey Exp $
 */

package teal.framework;

import javax.swing.Action;
import javax.swing.JMenu;

public interface TAbstractMenuBar {

    public void setFramework(TAbstractFramework fw);
    public TAbstractFramework getFramework();
    public JMenu add(String title);
    public JMenu get(String title);
    public void remove(String title);
    public void addAction(String target, Action a);
    public void removeAction(String source, Action a);
}
