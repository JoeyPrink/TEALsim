/*
 * $Id: TAbstractFramework.java,v 1.6 2010/07/09 20:46:00 pbailey Exp $
 */

package teal.framework;

import javax.swing.JFrame;

public interface TAbstractFramework extends TFramework {

    public JFrame getTheWindow();
    public void setTitle(String t);
}
