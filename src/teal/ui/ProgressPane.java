/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ProgressPane.java,v 1.5 2007/07/16 22:05:11 pbailey Exp $ 
 * 
 */

package teal.ui;

import java.awt.GridLayout;

import javax.swing.JProgressBar;

import teal.util.ProgressEvent;
import teal.util.ProgressEventListener;

public class ProgressPane extends UIPanel implements ProgressEventListener {

    private static final long serialVersionUID = 1L;

    protected JProgressBar mProgressBar;

    public ProgressPane() {
        super();
        setLayout(new GridLayout(1, 1));
        mProgressBar = new JProgressBar();
        mProgressBar.setBorderPainted(false);
        mProgressBar.setStringPainted(true);
        add(mProgressBar);
    }

    public void setProgress(ProgressEvent event) {
        switch (event.getStatus()) {
            case ProgressEvent.START:
                revalidate();
                //paintComponents(getGraphics());
                mProgressBar.setValue(0);
                break;
            case ProgressEvent.PROGRESS:
                revalidate();
                mProgressBar.setValue(event.getPercent());
                break;
            case ProgressEvent.COMPLETE:
                revalidate();
                mProgressBar.setValue(0);
                break;
            case ProgressEvent.INTERRUPT:
                break;
            default:
                break;
        }
    }
}
