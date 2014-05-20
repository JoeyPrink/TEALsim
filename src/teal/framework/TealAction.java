/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TealAction.java,v 1.6 2009/04/24 19:35:49 pbailey Exp $
 * 
 */

package teal.framework;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/**
 * class to parse Record specification of application requests.
 * Should be improved and may be moved out of the Framework.
 * 
 * @author mesrob
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.6 $ 
 */

public class TealAction extends AbstractAction {

    private static final long serialVersionUID = 3906086737192236082L;
    ArrayList<ActionListener> listeners = null;

    public TealAction() {
        super();
        listeners = new ArrayList<ActionListener>();
    }

    public TealAction(String str) {
        this(str, str);

    }

    public TealAction(String str, String com) {
        super(str);
        listeners = new ArrayList<ActionListener>();
        putValue(Action.ACTION_COMMAND_KEY, com);
    }

    public TealAction(String str, Icon icon) {
        super(str, icon);
        listeners = new ArrayList<ActionListener>();
    }

    public TealAction(String str, ActionListener listen) {
        this(str);
        addActionListener(listen);
    }

    public TealAction(String str, String com, ActionListener listen) {
        this(str);
        addActionListener(listen);
        putValue(Action.ACTION_COMMAND_KEY, com);
    }

    public TealAction(String str, Icon icon, ActionListener listen) {
        this(str, icon);
        addActionListener(listen);
    }

    public String getActionCommand() {
        return getValue(Action.ACTION_COMMAND_KEY).toString();
    }

    public void setActionCommand(String com) {
        putValue(Action.ACTION_COMMAND_KEY, com);
    }

    public String getName() {
        return getValue(AbstractAction.NAME).toString();
    }

    public void setName(String com) {
        putValue(Action.NAME, com);
    }

    public void addActionListener(ActionListener listen) {
        listeners.add(listen);
    }

    public void removeActionListener(ActionListener listen) {
        listeners.remove(listen);
    }

    public void triggerAction() {
        ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
            (String) getValue(Action.ACTION_COMMAND_KEY));
        actionPerformed(ae);
    }

    public void actionPerformed(ActionEvent e) {
        Iterator<ActionListener> it = listeners.iterator();
        while (it.hasNext()) {
            ((ActionListener) it.next()).actionPerformed(e);
        }

    }
}
