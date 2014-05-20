/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: MenuElement.java,v 1.6 2010/07/21 21:55:05 stefan Exp $
 * 
 */

package teal.framework;

import java.io.Serializable;

import javax.swing.Action;

/**
 * 
 * @author mesrob
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.6 $ 
 */

public class MenuElement implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1921841857766699620L;
	
	protected String section;
    protected Action command;

    public MenuElement() {
    }

    public MenuElement(String target, Action cmd) {
        section = target;
        command = cmd;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String target) {
        section = target;
    }

    public Action getAction() {
        return command;
    }

    public void setAction(Action cmd) {
        command = cmd;
    }
}
