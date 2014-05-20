/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: UIUtilities.java,v 1.5 2007/07/16 22:05:11 pbailey Exp $ 
 * 
 */

package teal.ui;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class UIUtilities {

    public final static Border PANEL_BORDER = BorderFactory.createEmptyBorder(3, 3, 3, 3);
    public final static Border SIM_BORDER = BorderFactory.createEmptyBorder(4, 10, 10, 10);
    public final static Border EMPTY_BORDER = BorderFactory.createEmptyBorder();

    public static void centerOnScreen(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = window.getSize();
        window.setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
    }

}
