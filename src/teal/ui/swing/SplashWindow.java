/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SplashWindow.java,v 1.2 2007/07/16 22:05:14 pbailey Exp $
 * 
 */

package teal.ui.swing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * A General purpose Splash Window for display while during
 * application startup.
 *
 * The window contains an image and can display text messages to mark
 * progress of application startup.
 * 
 * @author mesrob
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.2 $ 
 */

public class SplashWindow extends javax.swing.JWindow {

    private static final long serialVersionUID = 3834313916716495923L;
    private static SplashWindow splashWindow = null;
    SplashPanel panel = null;

    /** Creates new SplashWindow
     * @param imageResourceName is the name of the image file to display on
     * the splash screen in the form of a Java Resource
     */
    public SplashWindow(final String imageResourceName, int X, int Y) {
        initComponents();

        URL imageURL = this.getClass().getClassLoader().getResource(imageResourceName);
        Image image = Toolkit.getDefaultToolkit().createImage(imageURL);

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(image, 1);
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
        }

        panel = new SplashPanel(image, X, Y);

        this.getContentPane().add("Center", panel);
        pack();

        Dimension windowDim = getPreferredSize();

        setLocation(screenDim.width / 2 - windowDim.width / 2, screenDim.height / 2 - windowDim.height / 2);
    }

    /** Creates and show a SplashWindow
     * @param imageResourceName is the name of the image file to display on
     * the splash screen in the form of a Java Resource
     */
    public static void showSplashscreen(final String imageResourceName, int X, int Y) {
        if (splashWindow == null) splashWindow = new SplashWindow(imageResourceName, X, Y);

        splashWindow.setVisible(true);
    }

    /**
     * Destroy any locally held resource and hide the window
     */
    public static void destroySplashscreen() {
        splashWindow.setVisible(false);
        splashWindow.dispose();
        splashWindow = null;
    }

    /**
     * Show this message on the splash screen
     */
    public static void showMessage(final String message) {
        if (splashWindow != null) splashWindow.actualShowMessage(message);
    }

    protected void actualShowMessage(final String message) {
        panel.showMessage(message);
    }

    private void initComponents() {
        addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
    }

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {
        System.exit(0);
    }

    class SplashPanel extends javax.swing.JPanel {

        private static final long serialVersionUID = 3258413949803575093L;

        private BufferedImage image;
        private BufferedImage messageImage = null;
        private String message = "";

        private int messageX = 40;
        private int messageY = 55;
        private int messageH = 20;
        private int messageW = 200;
        private int ascent;

        public SplashPanel(final Image im, int X, int Y) {
            messageX = X;
            messageY = Y;
            int width = im.getWidth(null);
            int height = im.getHeight(null);

            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            image.getGraphics().drawImage(im, 0, 0, null);

            Dimension d = new Dimension(width, height);
            setPreferredSize(d);
            setMinimumSize(d);

            ascent = getFontMetrics(getFont()).getMaxAscent();
            messageH = getFontMetrics(getFont()).getMaxDescent() + ascent;
            messageW = width - messageX;

            messageImage = image.getSubimage(messageX, messageY - ascent, messageW, messageH);
        }

        public void showMessage(final String message) {
            this.message = message;
            Graphics g = getGraphics();
            g.drawImage(messageImage, messageX, messageY - ascent, null);
            g.drawString(message, messageX, messageY);
        }

        public void paint(Graphics g) {
            g.drawImage(image, 0, 0, null);
            g.drawString(message, messageX, messageY);
        }

    }
}
