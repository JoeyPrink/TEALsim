/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: TStatusBar.java,v 1.28 2007/07/16 22:04:47 pbailey Exp $
 * 
 */

package teal.framework;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import teal.ui.UIPanel;
import teal.ui.control.meters.Thermometer;
import teal.util.ProgressEvent;
import teal.util.ProgressEventListener;
import teal.util.TDebug;

/**
 * This class represents a status bar that includes a message area and an
 * optional progress meter (see <code>Thermometer</code>). Status bars are
 * typically placed at the bottom of an application window. Status messages
 * disappear after a fixed number of seconds, unless they are specified as
 * non-expiring; the delay is adjustable.
 * 
 * @author mesrob
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.28 $ 
 */

public class TStatusBar extends UIPanel implements ProgressEventListener, ActionListener {

    private static final long serialVersionUID = 3544669602937451315L;

    private JTextField label = null;

    private Thermometer meter;
    private Thread barThread = null;
    private static final Border inner_border = new LineBorder(Color.gray);

    private Timer timer;

    private GridBagConstraints gbc;

    private boolean busy = false;

    private static final Insets insets = new Insets(0, 2, 0, 0);

    /**
     * Construct a new <code>TStatusBar</code>. Constructs a new status bar
     * without a progress meter.
     */

    public TStatusBar() {
        this(false);
    }

    /**
     * Construct a new <code>TStatusBar</code>.
     * 
     * @param showMeter
     *            A flag specifying whether the status bar should include a
     *            progress meter.
     */

    public TStatusBar(boolean showMeter) {

        //setBorder(outter_border);

        timer = new Timer(10000, this);

        GridBagLayout gb = new GridBagLayout();
        setLayout(gb);

        gbc = new GridBagConstraints();
        gbc.weighty = gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 0;
        gbc.ipadx = 2;

        label = new JTextField();
        label.setFont(new Font("Dialog", Font.BOLD, 12));
        label.setHighlighter(null);
        label.setEditable(false);
        label.setForeground(Color.BLACK);
        label.setOpaque(false);
        label.setBorder(inner_border);
        add(label, gbc);

        gbc.ipadx = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0;
        gbc.insets = insets;

        if (showMeter) addStatusComponent(meter = new Thermometer());

        // super.setOpaque(true);
    }

    /**
     * Remove a component from the status bar. The specified component will be
     * removed from the status bar. The text status field and the progress
     * meter (if present) cannot be removed.
     * 
     * @param c
     *            The component to remove.
     * @exception java.lang.IllegalArgumentException
     *                If an attempt was made to remove the text status field or
     *                tje progress meter.
     */

    public void removeStatusComponent(JComponent c) {
        if ((c == label) || ((meter != null) && (c == meter)))
            throw (new IllegalArgumentException("Cannot remove label or meter."));

        remove(c);
    }

    /**
     * Add a component to the status bar. The new component will be fitted with
     * a beveled border and made transparent to match the other components in
     * the status bar, and will be added at the right end of the status bar.
     * 
     * @param c
     *            The component to add.
     */

    public void addStatusComponent(JComponent c) {
        c.setBorder(BorderFactory.createLineBorder(Color.gray));
        //c.show(false);
        c.setOpaque(false);
        add(c, gbc);
    }

    /**
     * Start or stop the slider. Starts or stops the progress meter's slider.
     * If this status bar was created without a progress meter, this method
     * will have no effect.
     * 
     * @param flag
     *            A flag specifying whether the slider should be started or
     *            stopped.
     */
    /*
     public synchronized void setBusy(boolean flag) {
     if (flag == busy) return;

     if (!labelOnly) {
     if (flag)
     meter.start();
     else
     meter.stop();

     busy = flag;
     }
     }
     */
    /**
     * Determine if the meter slider is currently active.
     * 
     * @return <code>true</code> if the slider is active, and <code>false</code>
     *         otherwise.
     */

    public synchronized boolean isBusy() {
        return (busy);
    }

    /**
     * Set the message font. Sets the font for the text in the status bar.
     * 
     * @param font
     *            The new font.
     */

    public void setFont(Font font) {
        if (label != null) label.setFont(font);
    }

    /**
     * Set the text color. Sets the color of the text displayed in the status
     * bar.
     * 
     * @param color
     *            The text new color.
     */

    public void setTextColor(Color color) {
        label.setForeground(color);
    }

    /**
     * Set the meter color.
     * 
     * @param color
     *            The new forground color for the thermometer.
     */

    public void setMeterColor(Color color) {
        meter.setForeground(color);
    }

    /**
     * Set a percentage on the meter. The <code>percent</code> value, which
     * must be between 0 and 100 inclusive, specifies how much of the meter
     * should be filled in with the foreground color.
     * 
     * Percent:  The percentage, a value between 0 and 100 inclusive. If the
     *            value is out of range, it is clipped.
     */

    public void setProgress(ProgressEvent event) {
        TDebug.println("TStatusBar setProgress(): " + event.getStatus() + "  = " + event.getPercent());
        switch (event.getStatus()) {
            case ProgressEvent.START:
                checkThread();
                meter.setPercent(event.getPercent());
                repaint();
                break;
            case ProgressEvent.PROGRESS:
                if (meter.isRunning()) {
                    meter.setPercent(event.getPercent());
                    repaint();
                }
                break;
            case ProgressEvent.COMPLETE:
                if (meter.isRunning()) {
                    meter.setPercent(100);
                    meter.stop();
                    meter.clear();
                    repaint();
                }
                break;
            case ProgressEvent.INTERRUPT:
                if (meter.isRunning()) {
                    meter.stop();
                    repaint();
                }
                break;
            default:
                break;
        }

    }

    private void checkThread() {
        if ((barThread == null) || (!barThread.isAlive())) initThread();
    }

    private void initThread() {
        //if(theEngine.getSimState() == EngineControl.NOT)
        //    theEngine.init();
        barThread = new Thread(meter, "BarThread");
        TDebug.println(0, "new bar thread: " + barThread.getName());
        barThread.start();
    }

    /**
     * Set the text to be displayed in the status bar. The status bar will be
     * cleared when the delay expires.
     * 
     * @param text
     *            The text to display in the status bar.
     */

    public void setText(String text) {
        setText(text, true);
    }

    /**
     * Set the text to be displayed in the status bar.
     * 
     * @param text
     *            The text to display in the status bar.
     * @param expires
     *            A flag specifying whether the message "expires." If <code>true</code>,
     *            the status bar will be cleared after the message has been in
     *            the status bar for a specified number of seconds. The default
     *            delay is 10 seconds, but can be adjusted via the <code>setDelay()</code>
     *            method.
     * 
     * @see #setDelay
     */

    public synchronized void setText(String text, boolean expires) {
        if (text == null) text = "";
        label.setText(text);

        if (expires) {
            if (timer.isRunning())
                timer.restart();
            else timer.start();
        } else {
            if (timer.isRunning()) timer.stop();
        }
    }

    /**
     * Set the delay on status bar messages.
     * 
     * @param seconds
     *            The number of seconds before a message disappears from the
     *            status bar.
     */

    public synchronized void setDelay(int seconds) {
        timer.setDelay(seconds * 1000);
    }

    /** This method is public as an implementation side-effect. */

    public void actionPerformed(ActionEvent evt) {
        setText(null);
    }

}

/* end of source file */
