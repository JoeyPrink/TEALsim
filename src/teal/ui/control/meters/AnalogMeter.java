/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: AnalogMeter.java,v 1.5 2007/12/24 23:02:24 jbelcher Exp $ 
 * 
 */

package teal.ui.control.meters;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;

import teal.util.*;

public class AnalogMeter extends Meter {

    private static final long serialVersionUID = 3760845653622011440L;
    
    // Various constants used to control the meter's appearance
	private static final int METERRANGEINDEGREES = 120;
	private static final double DEFAULTLABELPERCENT = 1.0;
	private static final int BORDERWIDTH = 3;
	private static final int CAPTIONPERCENT = 30;
	private static final int CORNERDIAMETER = 15;
	private static final int WELLMAJDIAMETERPERCENT = 15;
	private static final int WELLMINDIAMETERPERCENT = 5;
	private static final int COLORSCALEPERCENT = 90;
    
    
    // Private class data
	private double minValueAngle;
	private double maxValueAngle;
	private Vector colorZones = new Vector();
    private JTextField vText;
	
	/**
	 * Inner class that describes a color span on the meter's scale
	 *
	 */
	private class MeterColorZone {

		/**
		 * @param startAngle is the angle where the color specified
		 * here begins on the scale.
		 * @param spanAngle is the angle over which the color continues
		 * @param color is the color for the angle
		 */
		MeterColorZone(double startAngle, double spanAngle, Color color) {
        //TDebug.println(0,"ColorZone start: " + startAngle + "\t span: " + spanAngle);
			// Save incoming
			this.startAngle = startAngle;
			this.spanAngle  = spanAngle;
			this.color = color;
		}

		// Class data
		public double startAngle;
		public double spanAngle;
		public Color color;
	}

	/**
	 * Analog Meter Class Constructor with all agruments
	 *
	 * @param width is the width in pixels of the meter
	 * @param height is the height in pixels of the meter
	 * @param meterMode is not currently used
	 * @param fontName is the name of the font for labelling
	 * @param fontStyle is the name of the font style for labelling
	 * @param fontSize is the size of the font for labelling
	 * @param caption is the caption to label the meter with
	 * @param hasLabels is true if the meter has labels and it
	 * is desired they are displayed.
	 * @param labelsString is the string of comma separated label
	 * strings used to label the meter. There can be any number specified
	 * and the analog meter will spread them evenly across the scale.
	 * @param labelPercent is the percentage relative to the meter's
	 *  height where the labels will be drawn. Labels are drawn radially.
	 * @param value is the value the meter should initially display
	 * @param hasHighlight is true if highlighting should be
	 * used for the meter's display. Currently unused here.
	 * @param panelColor is the color of the panel surrounding the
	 * meter.
	 * @param needleColor is the color of the meter's needle
	 * @param textColor is the color used for the labelling text
	 */
	public AnalogMeter(int width, int height, int meterMode,
					String fontName, int fontStyle, int fontSize,
					String caption, boolean hasLabels, String labelsString,
					int labelPercent,
					int value, boolean hasHighlight,  
					Color panelColor, Color needleColor, Color textColor) {

    this();
			  // Process and save incoming
			  setMeterMode(meterMode);
			  setFontName(fontName);
			  setFontStyle(fontStyle);
			  setFontSize(fontSize);
			  setCaption(caption);
			  setHasLabels(hasLabels);
			  setLabelsString(labelsString);
			  setValue(value);
			  setHighlight(hasHighlight);
			  setNumberOfSections(0);
			  setPanelColor(panelColor);
			  setNeedleColor(needleColor);
			  setTextColor(textColor);
			  setWidth(width);
			  setHeight(height);

		// Install a default meter color range. Full range of scale is
		// green.
		//setColorRange(Color.green, 0, 100);
	}

	/**
	 * Analog Meter Class Constructor with reasonable defaults
	 */
	public AnalogMeter(int width, int height, String caption, double min, double max, double value) {

		this();
    
		setCaption(caption);
        setMinimum(min);
        setMaximum(max);
        setValue(value);
	    setWidth(width);
	    setHeight(height);
	}

	/**
	 * Analog Meter Class Constructor with zero arguments. Needed for
	 * use as a bean.
	 */
	public AnalogMeter() {
        super();
        vText = new JTextField();
        add(vText);
        vText.setVisible(true);
        vText.setBounds(40,200,100,32);
	    // Calculate min and max angles of needle
	    double halfAngle = METERRANGEINDEGREES / 2.0;
	    minValueAngle = 90 + halfAngle;
	    maxValueAngle = 90 - halfAngle;
        //TDebug.println(0,"minAngle = " + minValueAngle + " maxAngle = " + maxValueAngle);
        setLabelPercent(DEFAULTLABELPERCENT);
        
		
	}


	/**
	 * Draw a text string using polar coordinates
	 *
	 * @param g is the graphics context on which to render
	 * the text.
	 * @param xCenter is the horizontal center from which the
	 * text is positioned.
	 * @param  yCenter is the vertical center from which the
	 * text is positioned.
	 * @param  angle is the angle in degrees at which the text
	 * should be rendered.
	 * @param scale The scale.  
	 * @param  text is the text that gets drawn
	 */
	protected void drawTextPolar(Graphics g, int xCenter, int yCenter,
								 double angle, double scale, String text) {


        //TDebug.println(0,"textPolor: " + angle + " \txC=" + xCenter + " \tyC=" + yCenter + "\t" + scale);
		double rads = (angle * Math.PI) / 180.0;
        
		// Get font specs
		FontMetrics fm = g.getFontMetrics();
		int halfHeight = fm.getAscent() / 2;
		int halfWidth  = fm.stringWidth(text) / 2;
		
		double x = scale * Math.cos(rads);
		double y = scale * Math.sin(rads);
		y -= halfHeight;

		x += xCenter - halfWidth;
		y  = yCenter - y;

		// Draw the text
        //TDebug.println(0,"textPolor: " + angle + " \tx=" + round(x) + " \ty=" + round(y) + "\t" + text);
		g.drawString(text, round(x), round(y));
	}
    
    protected void sizeToFit()
    {
        super.sizeToFit();
    	// Get the size of the container and calculate important
		// values.
		int cwidth  = getSize().width;
		int cheight = getSize().height;
		int xCenter = cwidth / 2;
		int captionHeight  = (cheight * CAPTIONPERCENT) / 100;
		int captionYOffset = cheight - captionHeight;
        if(vText != null)
        vText.setBounds(cwidth/4,captionYOffset + BORDERWIDTH,cwidth/2,32);
    }

	/**
	 * Paint the Analog Meter onto the graphics context. Double
	 * buffering is used to reduce flicker.
	 *
	 * @param g is the graphics context on which to draw
	 */
	public void paint(Graphics g) {

		// Get the size of the container and calculate important
		// values.
		int cwidth  = getSize().width;
		int cheight = getSize().height;
		int xCenter = cwidth / 2;
		int captionHeight  = (cheight * CAPTIONPERCENT) / 100;
		int captionYOffset = cheight - captionHeight;
		int captionXOffset = 0;
		int needleLength = captionYOffset - (2 * BORDERWIDTH);
		
		// Is there an image of the analog meter to work with ?
		if (meterImage == null) {
			// No, create the image for the meter
			meterImage = createImage(cwidth, cheight);
			
			// Get graphics context for the image
			//Convert to Graphics2
			Graphics2D gMeter = (Graphics2D) meterImage.getGraphics();
			// Fill the panel
			int panelXOffset = BORDERWIDTH;
			int panelYOffset = BORDERWIDTH;
			int panelWidth = cwidth  - (2 * BORDERWIDTH);
			int panelHeight= cheight - (2 * BORDERWIDTH);
			gMeter.setColor(panelColor);
            
            gMeter.fillRect(0,0, cwidth, cheight);
            //gMeter.fillRect(panelXOffset, panelYOffset, panelWidth, panelHeight);

			// Draw color scale
			// First draw the filled arcs
			int xMaxOrg = xCenter - needleLength;
			int yMaxOrg = captionYOffset - needleLength;
			int arcMaxWidth = needleLength * 2;

			for (int colorZone=0; colorZone < colorZones.size(); colorZone++) {
				MeterColorZone mz = (MeterColorZone) colorZones.elementAt(colorZone);

				// Set the colorZone color
				gMeter.setColor(mz.color);

				// Fill the arc
				gMeter.fill(new Arc2D.Double((double) xMaxOrg,(double)  yMaxOrg,
                    (double)  arcMaxWidth,(double)  arcMaxWidth,
                     mz.startAngle, mz.spanAngle,Arc2D.PIE));
			}
			// Then clean out meter middle
			int bandLength = (needleLength * COLORSCALEPERCENT) / 100;
			int xMinOrg = xCenter - bandLength;
			int yMinOrg = captionYOffset - bandLength;
			int arcMinWidth = bandLength * 2;

			// Fill the arc with the panel color
			gMeter.setColor(panelColor);
			gMeter.fillArc(xMinOrg, yMinOrg, arcMinWidth, arcMinWidth,
						  (int) maxValueAngle - 1, METERRANGEINDEGREES + 2);

			// Draw the major meter well
			int wellMajDiameter = (WELLMAJDIAMETERPERCENT * cwidth) / 100;
			int halfWellMajDiameter = wellMajDiameter / 2;
			int wellMaxXOrg = xCenter - halfWellMajDiameter;
			int wellMaxYOrg = captionYOffset - halfWellMajDiameter;
			gMeter.setColor(Color.black);
			gMeter.fillOval(wellMaxXOrg, wellMaxYOrg, wellMajDiameter, wellMajDiameter);

			// Draw the minor meter well
			int wellMinDiameter = (WELLMINDIAMETERPERCENT * cwidth) / 100;
			int halfWellMinDiameter = wellMinDiameter / 2;
			int wellMinXOrg = xCenter - halfWellMinDiameter;
			int wellMinYOrg = captionYOffset - halfWellMinDiameter;
			gMeter.setColor(Color.gray);
			gMeter.fillOval(wellMinXOrg, wellMinYOrg, wellMinDiameter, wellMinDiameter);

  

			// Fill caption portion
			gMeter.setColor(captionBackgroundColor);
			gMeter.fillRect(captionXOffset, captionYOffset, 
								 cwidth, captionHeight);	 
			// Draw the caption
			gMeter.setFont(font);
			FontMetrics fm = gMeter.getFontMetrics();
			int labelWidth = fm.stringWidth(caption);
			int charHeight = fm.getAscent() / 2;
			int xText = xCenter - (labelWidth / 2);
			int yText = captionYOffset + (captionHeight / 2) + charHeight;
			gMeter.setColor(textColor);
			gMeter.drawString(caption, xText, yText);

			// Draw the labels
            gMeter.setColor(Color.black);
			int numberOfLabels = labels.size();
            TDebug.println(0,"Number of labels = " + numberOfLabels);
			String label, label1;
            double labelDist = 0.;
			switch(numberOfLabels) {
            
				case 0:
					break;
				case 1:
                    labelDist = getLabelDistance();
					label = (String) labels.elementAt(0);
					drawTextPolar(gMeter, xCenter, captionYOffset,
								  90, labelDist, label);
					break;
				default:
				
                    labelDist = getLabelDistance();
					double deltaAngle = ((double) METERRANGEINDEGREES) / 
										(numberOfLabels - 1);
					for (int l=0; l < numberOfLabels; l++) {
						double angle = minValueAngle - (l * deltaAngle);
						label = (String) labels.elementAt(l);
						drawTextPolar(gMeter, xCenter, captionYOffset,
								  angle, labelDist, label);
					}
			}
			gMeter.setColor(Color.black);
			gMeter.draw3DRect(0, 0, cwidth, cheight,true);
            gMeter.setColor(Color.darkGray);
			gMeter.draw3DRect(1, 1, cwidth - 2, cheight - 2,true);
            gMeter.setColor(Color.gray);
			gMeter.draw3DRect(2, 2, cwidth - 4, cheight - 4,true);
            gMeter.setColor(Color.black);
			gMeter.draw3DRect(captionXOffset, captionYOffset, 
								 cwidth, captionHeight,true);
            gMeter.setColor(Color.darkGray);
			gMeter.draw3DRect(captionXOffset +1 , captionYOffset + 1, 
								 cwidth - 2, captionHeight - 2,true);	
            gMeter.setColor(Color.gray);
			gMeter.draw3DRect(captionXOffset + 2, captionYOffset + 2, 
								 cwidth - 4, captionHeight -4 ,true);		
		}
		// Render the meter into the device context
		g.drawImage(meterImage, 0, 0, null);

		// Setup to draw the needle
		g.setColor(needleColor);
		
		double valueAngle = minValueAngle - (((value - min)/(max - min)) * METERRANGEINDEGREES);
        //TDebug.println(0,"Needle angle = " + valueAngle);
		valueAngle = valueAngle * Math.PI / 180.0;
        //TDebug.println(0,"Drawing line: " + valueAngle);
		int xOffset = xCenter + (int)(needleLength * Math.cos(valueAngle));
		int yOffset = captionYOffset - (int)(needleLength * Math.sin(valueAngle));

		// Draw the needle
		g.drawLine(xCenter, captionYOffset, xOffset, yOffset);
        if (showValue)
        {
            vText.setText(new Double(curValue).toString());
        }
	}

	/**
	 * Return the preferred size of this analog meter
	 *
	 * @return Dimension object containing the preferred size of the meter
	 */
	public Dimension getPreferredSize() {
		
		// width and height define meter
		return new Dimension(width, height);
	}

	/**
	 * Reset all of the color zones used for the meter. This allows the
	 * zones to be changed at runtime if needed.
	 */
	public void resetMeterColorZones() {

		colorZones.removeAllElements();
		meterImage = null;
		repaint();
	}

	/**
	 * Set a color for a range of values on the meter's scale.
	 * Creates a new MeterColorZone object to describe the range. NOTE:
	 * this isn't any overlap checking done here so the most recent
	 * range set sticks.
	 *
	 * @param color is the color for the specified range of values
	 * @param minPercentValue is the percentage of full scale value
	 * where this color should begin
	 * @param  maxPercentValue is the percentage of full scale value
	 * where this color should end
	 */
	public void setColorRange(Color color,
							  double minPercentValue,
							  double maxPercentValue) {

		double spanAngle = (maxPercentValue - minPercentValue) * METERRANGEINDEGREES/-100.0;
		double startAngle = minValueAngle - ((minPercentValue *METERRANGEINDEGREES/100.0));

		colorZones.addElement(new MeterColorZone(startAngle, spanAngle, color));
		meterImage = null;
		repaint();
	}
	
	// Simple test program for Analog Meters
	public static void main(String [] args) {
		
		Frame f = new Frame("Test");
		AnalogMeter am = new AnalogMeter();
		f.add(am);

		Dimension d = am.getPreferredSize();
		f.setSize(d);
		f.setVisible(true);
		
		am.setColorRange(Color.green,	 0, 33);
		am.setColorRange(Color.yellow,	33, 66);
		am.setColorRange(Color.red,		66, 100);
		
		for (int index=0; index < 1000; index++) {

			int value = (int)(Math.random() * 100);
			am.setValue(value);
			try {
				Thread.sleep(200);
			}
			catch(Exception ignor) {}
		}
	}

 }

