
package teal.ui.control.meters;

import java.awt.*;

/**
 * ControlIndicator displays a property's value as a moving triangular 
 * indicator. Color mode, Range style and Label style inherited 
 * from ControlAnimatedCanvas class. 
 */

public class ControlIndicator extends ControlAnimatedCanvas {

    private static final long serialVersionUID = 3258129154699506737L;

    static final int TICK_LEN = 8; // length of tick mark

    //--- GUI graphics info. --------------------
    protected int faceX, faceY, faceW, faceH = 0; // bar area
    protected int fillX, fillY, fillW, fillH = 0; // bar filled area 
    protected int axisX, axisY, axisW, axisH = 0; // axis label area
    protected int valX, valY, valW, valH = 0; // value display area
    protected int chaX, chaY, chaW, chaH = 0; // channel name area
    protected int maxLabel, maxFillH, maxFillW = 0;
    protected double fillPercent = 0;
    protected int halfThumb = 2; // for slider use only
    protected FontMetrics fm;
    protected int fontH, fontD = 0;
    protected double maxVal, minVal = 0;
    protected String stringHi, stringLo;
    private Polygon needle = new Polygon();
    private double[] pretty;

    //--- define default value ----------------------
    protected int orientation = VERTICAL;
    protected String fmt = "%.2f";
    protected boolean firstPoint = true;

    /**
     * Null Constructor.
     */
    public ControlIndicator() {
        super();
    }

    /**
     * Constructor with device name and property name.
     * 
     * @param dName    device name
     * @param pName    property name
     */
    public ControlIndicator(String dName, String pName) {
        super(dName, pName);
    }

    /**
     * Constructor.
     * Defines the canvas size and uses default foreground and background color
     * @param w    component width
     * @param h   component height
     */
    public ControlIndicator(int w, int h) {
        super(w, h);
    }

    /**
     * Constructor.
     * Defines the canvas size and sets foreground and background colors.
     * @param w     component width
     * @param h    component height
     * @param fg        foreground color
     * @param bg        background color
     */
    public ControlIndicator(int w, int h, Color fg, Color bg) {
        super(w, h, fg, bg);
    }

    /**
     * Set the orientation
     * @param newOrientation   new orientation, which may be one of:
     *             <OL>
     *             <LI>  CConstants.HORIZONTAL
     *             <LI>  CConstants.VERTICAL
     *             </OL>
     */
    public void setOrientation(int newOrientation) {
        orientation = newOrientation;
    }

    /**
     * Get the orientation
     */
    public int getOrientation() {
        return orientation;
    }

    protected void initGraphics() {
        //--- init the graphics parameters -----------
        chaX = 0;
        chaY = 0;
        chaW = 0;
        chaH = 0;
        valX = 0;
        valY = 0;
        valW = 0;
        valH = 0;
        axisX = 0;
        axisY = 0;
        axisW = 0;
        axisH = 0;
        Dimension rec = getSize();

        //--- calculate the first rangeHi/rangeLo when RANGE_AUTO ----
        if ((firstPoint) && (rangeStyle == RANGE_AUTO)) {
            maxVal = currVal;
            minVal = currVal;
            rangeHi = maxVal + Math.abs(0.1 * maxVal);
            rangeLo = minVal - Math.abs(0.1 * minVal);
            firstPoint = false;
        }

        //--- get pretty axis value --------------------------------
        pretty = ControlUtility.makePrettyAxis(rangeLo, rangeHi);
        rangeLo = pretty[0];
        rangeHi = pretty[1];

        //--- get info needed for label ----------------------------
        if (labelStyle != LABEL_NONE) {
            fm = getFontMetrics(font);
            fontH = fm.getHeight();
            fontD = fm.getDescent();
            stringLo = ControlUtility.format(rangeLo, fmt);
            stringHi = ControlUtility.format(rangeHi, fmt);
            maxLabel = Math.max(fm.stringWidth(stringLo), fm.stringWidth(stringHi));
        }

        //--- calculate the x, y, w and h of the various area -------
        switch (labelStyle) {
            case LABEL_ALL:
                chaX = 0;
                chaY = 0;
                chaW = rec.width;
                chaH = fontH;
            case LABEL_VALUE:
                if (orientation == HORIZONTAL) {
                    valW = rec.width - 2 * (maxLabel + frameW + halfThumb);
                    valH = fontH + fontD;
                    valX = maxLabel + frameW + halfThumb;
                    valY = rec.height - valH;
                } else { // (orientation == VERTICAL)
                    valW = rec.width;
                    valH = fontH + fontD;
                    valX = 0;
                    valY = rec.height - valH;
                }
            case LABEL_AXIS:
                if (orientation == HORIZONTAL) {
                    axisW = rec.width - 2 * (frameW + halfThumb);
                    axisH = fontH + fontD + TICK_LEN;
                    axisX = halfThumb;
                    axisY = rec.height - axisH - frameW;
                } else { // (orientation == VERTICAL)
                    axisX = 0; //frameW;
                    axisY = chaH + halfThumb;
                    axisW = maxLabel + frameW + TICK_LEN;
                    axisH = rec.height - valH - chaH - 2 * (frameW + halfThumb);
                }
            default: //case LABEL_NONE:
                if (orientation == HORIZONTAL) {
                    faceX = 0;
                    faceY = chaH;
                    faceW = rec.width;
                    faceH = rec.height - chaH - axisH;
                } else {
                    faceX = axisW;
                    faceY = chaH;
                    faceW = rec.width - axisW;
                    faceH = rec.height - valH - chaH;
                }
        }
    }

    protected void paintBackground(Graphics g) {
        initGraphics();
        g.setColor(getBackground());
        g.fillRect(0, 0, getSize().width, getSize().height);

        switch (labelStyle) {
            case LABEL_ALL:
                drawName(g);
            case LABEL_VALUE:
                drawValue(g);
            case LABEL_AXIS:
                drawAxis(g);
            case LABEL_NONE:
                drawFace(g);
            default:
                drawFace(g);
        }
    }

    protected void paintForeground(Graphics g) {
        int topY, bottonY, topX, bottonX, tipX, tipY;

        //--- draw value when needed  ----------------------
        if ((labelStyle == LABEL_VALUE) || (labelStyle == LABEL_ALL)) {
            drawValue(g);
        }

        //--- Erase old fill rect.-------------------------
        g.setColor(getBackground());
        if (orientation == VERTICAL)
            g.fill3DRect(fillX, fillY, fillW, maxFillH, false);
        else g.fill3DRect(fillX, fillY, maxFillW, fillH, false);

        if (rangeStyle == RANGE_AUTO) do_auto();

        //--- recalculate fill% (1.0 <= fill% >= 0.0) ----- 
        fillPercent = (currVal - rangeLo) / (rangeHi - rangeLo);
        fillPercent = Math.min(fillPercent, 1.0);
        fillPercent = Math.max(fillPercent, 0.0);

        if (orientation == VERTICAL) {
            fillH = (int) (maxFillH * fillPercent);
            tipX = fillX;
            tipY = fillY + maxFillH - fillH;
            topX = fillX + fillW - 1;
            bottonX = topX;
            topY = tipY - fillW / 3;
            topY = Math.max(topY, fillY);
            bottonY = tipY + fillW / 3;
            bottonY = Math.min(bottonY, fillY + maxFillH);
        } else {
            fillW = (int) (maxFillW * fillPercent);
            tipX = fillX + fillW;
            tipY = fillY + fillH;
            topX = tipX + fillH / 3;
            topX = Math.min(topX, fillX + maxFillW);
            bottonX = tipX - fillH / 3;
            bottonX = Math.max(bottonX, fillX);
            topY = fillY;
            bottonY = topY;
        }

        //--- Draw new pointer --------------------------
        needle = new Polygon();
        needle.addPoint(tipX, tipY);
        needle.addPoint(topX, topY);
        needle.addPoint(bottonX, bottonY);
        //System.out.println(tipX+"-"+tipY+"-"+topY+"-"+bottonY); 
        g.setColor(currColor);
        g.fillPolygon(needle);
    }

    protected void do_auto() {
        boolean rangeChanged = false;
        if (currVal > rangeHi) {
            rangeChanged = true;
            maxVal = currVal;
        }
        if (currVal < rangeLo) {
            rangeChanged = true;
            minVal = currVal;
        }
        if (rangeChanged) {
            pretty = ControlUtility.makePrettyAxis(minVal, maxVal);
            rangeLo = pretty[0];
            rangeHi = pretty[1];
            repaint();
        }
    }

    protected void drawFace(Graphics g) {
        fillX = faceX + frameW;
        fillY = faceY + frameW;
        fillW = faceW - 2 * frameW;
        fillH = faceH - 2 * frameW;
        maxFillH = faceH - 2 * frameW;
        maxFillW = faceW - 2 * frameW;

        g.setColor(getBackground());
        g.fill3DRect(faceX, faceY, faceW, faceH, true);
        g.fill3DRect(fillX, fillY, fillW, maxFillH, false);
    }

    protected void drawAxis(Graphics g) {
        int strLoX, strLoY, strHiX, strHiY;
        g.setColor(getForeground());
        g.setFont(font);

        //--- draw High and Low label of axis -----
        if (orientation == HORIZONTAL) {
            strLoX = axisX + 2;
            strLoY = axisY + TICK_LEN + fontH;
            strHiX = axisX + axisW - fm.stringWidth(stringHi) + 4;
            strHiY = strLoY;
        } else {
            strHiX = axisX + 2;
            strHiY = axisY + fontH;
            strLoX = strHiX;
            strLoY = axisY + axisH + 2;
        }
        g.drawString(stringLo, strLoX, strLoY);
        g.drawString(stringHi, strHiX, strHiY);

        //--- draw bar ticks ----------------
        if (orientation == HORIZONTAL) {
            int sX, eX;
            int sY = axisY;
            int eY = sY + TICK_LEN;
            for (int i = 0; i <= pretty[2]; i++) {
                double xinc = ((double) axisW) / pretty[2];
                sX = axisX + frameW + (int) ((double) i * xinc);
                eX = sX;
                g.drawLine(sX, sY, eX, eY);
            }
        } else {
            int sX = axisX + 2 + maxLabel;
            int eX = sX + TICK_LEN;
            int sY, eY;
            for (int i = 0; i <= pretty[2]; i++) {
                double yinc = ((double) axisH) / pretty[2];
                sY = axisY + frameW + (int) (i * yinc);
                eY = sY;
                g.drawLine(sX, sY, eX, eY);
            }
        }
    }

    protected void drawValue(Graphics g) {
        //--- Erase old fill rect.-------------------
        g.setColor(getBackground());
        g.fillRect(valX, valY + 1, valW, valH);

        String stringCurrVal = ControlUtility.format(currVal, fmt);
        int stringW = fm.stringWidth(stringCurrVal);
        int stringX = valX + (int) ((valW - stringW) / 2);
        int stringY = valY + valH - fontD;
        g.setColor(getForeground());
        g.drawString(stringCurrVal, stringX, stringY);
    }

    private void drawName(Graphics g) {

        String chaName = deviceName + "." + propertyName;
        String tempName = chaName;
        //--- remove the last letter to fit the limited space ----
        int chanLen = fm.stringWidth(tempName);
        int i = 2;
        while (chaW <= chanLen) {
            tempName = tempName.substring(0, tempName.length() - i);
            i++;
            chanLen = fm.stringWidth(tempName);
        }

        int stringW = fm.stringWidth(tempName);
        int stringX = chaX + (int) ((chaW - stringW) / 2);
        int stringY = chaY + chaH - fontD;

        g.setColor(getForeground());
        g.setFont(font);
        g.drawString(tempName, stringX, stringY);
    }

} //end of ControlIndicator
