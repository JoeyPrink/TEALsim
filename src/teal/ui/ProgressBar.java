/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ProgressBar.java,v 1.7 2007/08/17 19:38:30 jbelcher Exp $ 
 * 
 */

package teal.ui;

import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;

import teal.util.ProgressEvent;
import teal.util.ProgressEventListener;

/**
 * @author Andrew Mckinney
 */
public class ProgressBar extends JProgressBar implements ProgressEventListener {

	private static final long serialVersionUID = 1L;

	/**
     * 
     */
    public ProgressBar() {
        super();
    }

    /**
     * @param orient
     */
    public ProgressBar(int orient) {
        super(orient);
    }

    /**
     * @param min
     * @param max
     */
    public ProgressBar(int min, int max) {
        super(min, max);
    }

    /**
     * @param orient
     * @param min
     * @param max
     */
    public ProgressBar(int orient, int min, int max) {
        super(orient, min, max);
    }

    /**
     * @param newModel
     */
    public ProgressBar(BoundedRangeModel newModel) {
        super(newModel);
    }

    /**
     * Set a percentage on the meter. The <code>percent</code> value, which
     * must be between 0 and 100 inclusive, specifies how much of the meter
     * should be filled in with the foreground color.
     * 
     * @param event
     *            The percentage, a value between 0 and 100 inclusive. If the
     *            value is out of range, it is clipped.
     */

    public void setProgress(ProgressEvent event) {
        switch (event.getStatus()) {
            case ProgressEvent.START:
                setString("Please Wait:");
                setValue(0);
                paintComponent(getGraphics());
                break;
            case ProgressEvent.PROGRESS:
                setValue(event.getPercent());
                paintComponent(getGraphics());
                break;
            case ProgressEvent.COMPLETE:
                setString("Complete:");
                paintComponent(getGraphics());
                setValue(0);
                break;
            case ProgressEvent.INTERRUPT:
                setString("Interrupted:");
                break;
            default:
                break;
        }

    }

}
