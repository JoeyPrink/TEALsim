
package teal.ui.control.meters;

import java.awt.*;

import teal.ui.*;
import teal.util.*;

/*
 * This class represents a combination progress meter / busy slider.
 */
public class Thermometer extends UIPanel implements Runnable {

    private static final long serialVersionUID = 3618699682314926391L;

    private int percent = 0, pos = 0, pwidth = 100, pheight = 10, barw = 20;
    //private Thread thread = null;
    private static final Color defaultColor = new Color(99, 99, 206);
    private boolean running = false;

    /**
     * Construct a new <code>Thermometer</code> with a default foreground
     * color (a dark blue).
     */
    public Thermometer() {
        this(defaultColor);
    }

    /**
     * Construct a new <code>Thermometer</code> with the given foreground
     * color.
     * 
     * @param color
     *            The foreground color.
     */
    public Thermometer(Color color) {
        super();
        setForeground(color);
    }

    public synchronized boolean isRunning() {
        return running;
    }

    /**
     * Set a percentage on the meter. The percentage value, which must be
     * between 0 and 100 inclusive, specifies how much of the meter should be
     * filled in with the foreground color.
     * 
     * @param percent
     *            The new percentage value. If the value is out of range, it is
     *            clipped.
     */
    public synchronized void setPercent(int percent) {
        TDebug.println("Tmeter setPercent: " + percent);
        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;
        pos = 0;
        this.percent = percent;
    }

    public void clear() {
        percent = 0;
    }

    /** Set the size of the component. */
    public void setSize(int w, int h) {
        pwidth = 100;
        pheight = h;
        barw = w / 5;
        super.setSize(w, h);
    }

    /**
     * Get the preferred size of the component. The preferred size is always 100
     * pixels for width and the current height.
     */
    public Dimension getPreferredSize() {
        return (new Dimension(pwidth, pheight));
    }

    /**
     * Get the minimum size of the component. Returns the preferred size of the
     * component.
     */
    public Dimension getMinimumSize() {
        return (getPreferredSize());
    }

    /** Paint the component. */
    public void paintComponent(Graphics gc) {
        //TDebug.println(2,"paint() " + percent);
        Insets ins = getInsets();
        //super.paint(gc);
        Dimension d = getSize();
        int w = d.width * percent / 100;
        int p = d.width * pos / 100;
        gc.setColor(getForeground());
        gc.fillRect(p + ins.left, ins.top, w - (ins.left + ins.right), d.height - (ins.top + ins.bottom));
    }

    /* Body of the update thread. */
    public void run() {
        TDebug.println(1, "Tmeter starting thread = " + Thread.currentThread().getName());
        //int i, bw;
        running = true;
        try {
            while (running) {
                TDebug.println(1, "Tmeter run: " + percent);
                //repaint();
                paintComponent(getGraphics());
                TDebug.println(1, "Tmeter run2: " + percent);
                Thread.sleep(100);
            }
        } catch (InterruptedException ex) {
            // done
            running = false;
        }
        TDebug.println(1, "Exiting thread:");
    }

    /*
     * Start the busy slider. The busy slider is a rectangular segment, 20% of
     * the width of the meter that slides back and forth along the length of the
     * meter. A dedicated thread is created to update this slider.
     */
    /*
     * public synchronized void start() { thread = new Thread(new Runnable() {
     * public void run() { _run(); } }); thread.start(); }
     */
    /**
     * Stop the busy slider. Stops the slider and clears the meter. The
     * dedicated update thread is interrupted and released.
     */
    public synchronized void stop() {
        running = false;
    }
}
/* end of source file */
