
package teal.ui.control.meters;

import java.awt.*;

/**
 * This class implements a semi-circular meter object enclosed in a 
 * rectangle. It inherits color mode, range style and label style from
 * super class CCanvas.
 */
public class ControlMeter extends ControlAnimatedCanvas {

    private static final long serialVersionUID = 3257291344086251571L;

    //--- GUI graphics info. --------------------
    private int faceX, faceY, faceW, faceH = 0; // meter face area
    private int centerX, centerY, radius = 0; // meter face data
    private int axisY, axisH = 0; // axis label area
    private int valX, valY, valW, valH = 0; // value display area
    private int sX, sY, sW, sH = 0;
    private int chaX, chaY, chaW, chaH = 0; // channel name area
    private double maxVal, minVal = 0;

    private FontMetrics fm;
    private int fontH, fontD = 0;
    private String stringHi, stringLo;
    private String fmt = "%.2f";
    //private String fmt = %.3g;

    //---- Needle data. ----------------------------
    private Polygon needle = null;
    private int[] xneedle = new int[4];
    private int[] yneedle = new int[4];
    private int spread = 12; // fattest point of needle
    private int edgeRad, needleRad = 0;
    private int dialRad = 0; // radius of center ball
    private int dialDia = 0; // should be 2*dialRad
    private double NeedlePos = 0;
    private double[] pretty;

    private boolean firstPoint = true;
    private boolean change = false;

    /**
     * Null Constructor.
     */
    public ControlMeter() {
        super();
    }

    /**
     * Constructor with device name and property name
     * 
     * @param dName    device name
     * @param pName    property name
     */
    public ControlMeter(String dName, String pName) {
        super(dName, pName);
    }

    /**
     * Constructor
     * Defines the canvas size and use default foreground and background color
     * @param w    component width
     * @param h   component height
     */
    public ControlMeter(int w, int h) {
        super(w, h);
    }

    /**
     * Constructor
     * Defines the canvas size and sets foreground and background colors.
     * @param w     component width
     * @param h    component height
     * @param fg        foreground color
     * @param bg        background color
     */
    public ControlMeter(int w, int h, Color fg, Color bg) {
        super(w, h, fg, bg);
    }

    private void initGraphics() {
        Dimension rec = getSize();
        chaX = 0;
        chaY = 0;
        chaW = 0;
        chaH = 0;
        valX = 0;
        valY = 0;
        valW = 0;
        valH = 0;
        axisY = 0;
        axisH = 0;

        if ((firstPoint) && (rangeStyle == RANGE_AUTO)) {
            maxVal = currVal;
            minVal = currVal;
            rangeHi = maxVal + Math.abs(0.1 * maxVal);
            rangeLo = minVal - Math.abs(0.1 * minVal);
            firstPoint = false;
        }
        pretty = ControlUtility.makePrettyAxis(rangeLo, rangeHi);
        rangeLo = pretty[0];
        rangeHi = pretty[1];

        if (labelStyle != LABEL_NONE) {
            fm = getFontMetrics(font);
            fontH = fm.getHeight();
            fontD = fm.getDescent();
            stringLo = ControlUtility.format(rangeLo, fmt);
            stringHi = ControlUtility.format(rangeHi, fmt);
        }
        switch (labelStyle) {
            case LABEL_ALL:
                chaX = 0;
                chaY = 0;
                chaW = rec.width;
                chaH = fontH;
            case LABEL_VALUE:
                valW = rec.width;
                valH = fontH - fontD;
                valX = 0;
                valY = rec.height - valH;
            case LABEL_AXIS:
                //axisY is depend on faceH
                axisH = fontH;
            default: //case LABEL_NONE:
                faceH = rec.height - chaH - axisH - valH;
                faceH = Math.min((int) (rec.width / 2), faceH);
                faceW = 2 * faceH;
                faceX = (rec.width - faceW) / 2;
                faceY = chaH;
                axisY = chaH + faceH;
        }
        //--- Set up coordinates for needle and dial based on faceX/Y 
        centerX = faceX + (faceW / 2);
        centerY = faceY + frameW + (int) (0.95 * (faceH - 2 * frameW));

        radius = faceH - 2 * frameW;
        edgeRad = (int) (radius * 0.2);
        needleRad = (int) (radius * 0.72);
        //--- radius of spindle about which needle spins
        dialRad = (int) (radius * 0.07);
        dialDia = (int) (2 * dialRad);

        for (int i = 0; i < xneedle.length; i++) {
            xneedle[i] = centerX;
            yneedle[i] = centerY;
        }
        needle = new Polygon(xneedle, yneedle, 4);
        xneedle = needle.xpoints;
        yneedle = needle.ypoints;
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
                drawAxisLimit(g);
            default: //case LABEL_NONE:
                drawFace(g);
                if (hasZones) drawZones(g);
        }
    }

    protected void paintForeground(Graphics g) {

        if (rangeStyle == RANGE_AUTO) do_auto();
        NeedlePos = (currVal - rangeLo) / (rangeHi - rangeLo);
        NeedlePos = Math.max(NeedlePos, 0.0);
        NeedlePos = Math.min(NeedlePos, 1.0);
        int angle = 180 + (int) (NeedlePos * 180);

        //--- update coordinates for needle ---------------------
        xneedle[1] = centerX + (int) (edgeRad * Math.cos((angle + spread) * Math.PI / 180));
        yneedle[1] = centerY + (int) (edgeRad * Math.sin((angle + spread) * Math.PI / 180));
        xneedle[2] = centerX + (int) (needleRad * Math.cos((angle) * Math.PI / 180));
        yneedle[2] = centerY + (int) (needleRad * Math.sin((angle) * Math.PI / 180));
        xneedle[3] = centerX + (int) (edgeRad * Math.cos((angle - spread) * Math.PI / 180));
        yneedle[3] = centerY + (int) (edgeRad * Math.sin((angle - spread) * Math.PI / 180));

        //--- draw the new needle and fill in the center oval---------
        g.setColor(currColor);
        g.fillPolygon(needle);
        g.fillOval(centerX - dialRad, centerY - dialRad, dialDia, dialDia);
    }

    protected void do_auto() {
        if (currVal > rangeHi) {
            change = true;
            maxVal = currVal;
        }
        if (currVal < rangeLo) {
            change = true;
            minVal = currVal;
        }
        if (change) {
            pretty = ControlUtility.makePrettyAxis(minVal, maxVal);
            rangeLo = pretty[0];
            rangeHi = pretty[1];
            change = false;
            repaint();
        }
    }

    //------------------------------------------------------------
    // Draws meter frame and the marks on the meters face. 
    // The meter is divided into 10 increments in a semi-circle.
    //-------------------------------------------------------------
    private void drawFace(Graphics g) {
        g.setColor(PANELCOLOR);
        g.fill3DRect(faceX, faceY, faceW, faceH, true);
        faceX += frameW;
        faceY += frameW;
        faceW -= 2 * frameW;
        faceH -= 2 * frameW;
        g.fill3DRect(faceX, faceY, faceW, faceH, false);

        g.setColor(NEEDLECOLOR);
        int ix, iy, ox, oy = 0;
        int orad = (int) (radius * 0.85);
        int irad = (int) (radius * 0.75);
        for (int i = 0; i <= pretty[2]; i++) {
            int angle = 180 + (int) (i * (float) 180 / pretty[2]);
            double cos = Math.cos(angle * Math.PI / 180);
            double sin = Math.sin(angle * Math.PI / 180);
            ix = centerX + (int) (irad * cos);
            iy = centerY + (int) (irad * sin);
            ox = centerX + (int) (orad * cos);
            oy = centerY + (int) (orad * sin);
            g.drawLine(ix, iy, ox, oy); // draw the tick marks
        }
    }

    private void drawAxisLimit(Graphics g) {
        g.setColor(TEXTCOLOR);
        g.setFont(font);

        //--- draw High and Low label of axis ------------
        int strLoX = faceX + frameW;
        int stringW = fm.stringWidth(stringHi);
        int strHiX = faceX + faceW - stringW - frameW;
        int allY = axisY + axisH;
        g.drawString(stringLo, strLoX, allY);
        g.drawString(stringHi, strHiX, allY);

    }

    private void drawValue(Graphics g) {
        //--- Erase old fill rect.-------------
        g.setColor(getBackground());
        g.fillRect(sX, valY - fontD, sW, sH);

        String stringCurrVal = ControlUtility.format(currVal, fmt);
        sW = fm.stringWidth(stringCurrVal);
        sX = valX + (int) ((valW - sW) / 2);
        sY = valY + valH - fontD;
        sH = fontH + fontD;
        g.setColor(TEXTCOLOR);
        g.setFont(font);
        g.drawString(stringCurrVal, sX, sY - 3);
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

        g.setColor(TEXTCOLOR);
        g.setFont(font);
        g.drawString(tempName, stringX, stringY);
    }

    private void drawZones(Graphics g) {

        for (int colorZone = 0; colorZone < colorZones.size(); colorZone++) {
            ColorZone mz = (ColorZone) colorZones.elementAt(colorZone);

            double ZonePos = (mz.valueHi - rangeLo) / (rangeHi - rangeLo);
            ZonePos = Math.max(ZonePos, 0.0);
            ZonePos = Math.min(ZonePos, 1.0);
            int startAngle = (int) (ZonePos * 180);

            ZonePos = (mz.valueLo - rangeLo) / (rangeHi - rangeLo);
            ZonePos = Math.max(ZonePos, 0.0);
            ZonePos = Math.min(ZonePos, 1.0);
            int endAngle = (int) (ZonePos * 180);

            int spanAngle = endAngle - startAngle;

            // Set the colorZone color
            g.setColor(mz.color);

            // Fill the arc
            //System.out.println("DrawZone: startAngle = " + startAngle + " spanAngle = " + spanAngle);
            g.fillArc(xneedle[0] - needleRad, yneedle[0] - needleRad, needleRad * 2, needleRad * 2, (int) startAngle,
                (int) spanAngle);
        }
    }

} //end of ControlMeter
