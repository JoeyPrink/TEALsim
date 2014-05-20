/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TextFieldBorder.java,v 1.3 2007/07/16 22:05:14 pbailey Exp $ 
 * 
 */

package teal.ui.swing.borders;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

import teal.ui.swing.skin.*;

/**
 * This is a simple border class used for Text field.
 */
public class TextFieldBorder extends AbstractBorder implements UIResource {

    private static final long serialVersionUID = 3617011957602268722L;

    private static final Insets defaultInsets = new Insets(3, 5, 3, 5);

    private Insets insets;

    static Skin skin;

    public TextFieldBorder() {
        insets = defaultInsets;
    }

    public TextFieldBorder(Insets insets) {
        this.insets = insets;
    }

    /**
     * Gets the border insets for a given component.
     *
     * @param c The component to get its border insets.
     * @return Always returns the same insets as defined in <code>insets</code>.
     */
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    /**
     * lazy initialization of the skin
     */
    public Skin getSkin() {
        if (skin == null) {
            skin = new Skin("textbox.png", 2, 3);
        }

        return skin;
    }

    /**
     * Use the skin to paint the border
     * @see javax.swing.border.Border#paintBorder(Component, Graphics, int, int, int, int)
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        int index = c.isEnabled() ? 0 : 1;
        getSkin().draw(g, index, w, h);
    }
}