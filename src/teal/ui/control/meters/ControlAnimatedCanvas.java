package teal.ui.control.meters;

import java.awt.*;
import java.beans.*;
import java.util.*;

import teal.util.*;
/**
 * CAnimatedCanvas is an abstract class. The derived classes must 
 * implement paintBackground and paintForeground methods.
 */
public abstract class ControlAnimatedCanvas extends ControlCanvas {

	static final int MINSIZE = 5;
	static final int METERRANGEINDEGREES = 180;

	//--- define default value --------------------------
	protected int colorMode  = CLRMOD_STATIC;
	protected int labelStyle = LABEL_ALL;
	protected int rangeStyle = RANGE_STATIC;
	
	protected int frameW = 2; // dafault face frame = 2 pixels
	
	protected double controlHi = 100.0;
	protected double controlLo = 0.0;
	protected double displayHi = 90.0;
	protected double displayLo = 10.0;
	protected double alarmHi = 80.0;
	protected double alarmLo = 20.0;
	protected double warningHi = 70.0;
	protected double warningLo = 30.0;
	protected double rangeHi = displayHi;
	protected double rangeLo = displayLo;
	protected double precision = 1.0;
	
	protected Font font = new Font("Dialog", Font.PLAIN, 12);

	protected Vector colorZones = new Vector();

	//--- boolean flag -------------------------------------------
	protected boolean firstTime = true;
	protected boolean firstCall = true;
	protected boolean hasZones = false;

	protected double currVal = 0;
	protected Color currColor = null;

	/**
	 * Inner class that describes a color zone on the widgets scale
	
	 */

	protected class ColorZone {

		public double valueLo;
		public double valueHi;
		public Color color;
		/**
		 * @param valueLo is the bottom zone value
		 * @param valueHi is the top zone value
		 * @param color is the color for the zone
		 */
		ColorZone(double valueLo, double valueHi, Color color) {
			this.valueLo = valueLo;
			this.valueHi = valueHi;
			this.color = color;
		}
	}

	/**
	 * Null Constructor
	 */
	public ControlAnimatedCanvas() {
		this(0, 0);
	}

	/**
	 * Constructor with device name and property name
	 * 
	 * @param dName    device name
	 * @param pName    property name
	 */
	public ControlAnimatedCanvas(String dName, String pName) {
		this(0, 0);
		setDeviceAndPropertyName(dName, pName);
	}

	/**
	 * Constructor
	 * Defines the canvas size and uses default foreground and 
	 * background colors.
	 * @param width    component width
	 * @param height   component height
	 */
	public ControlAnimatedCanvas(int width, int height) {
		super(width, height);
		this.setSize(width, height);
	}

	/**
	 * Constructor
	 * Defines the canvas size and sets foreground and background colors.
	 * @param width     component width
	 * @param height    component height
	 * @param fg        foreground color
	 * @param bg        background color
	 */
	public ControlAnimatedCanvas(int width, int height, Color fg, Color bg) {
		super(width, height, fg, bg);
		this.setSize(width, height);
	}

	/**
	 * Set canvas size. 
	 * @param newWidth     width of the component
	 * @param newHeight    height of the component
	 */
	public void setSize(int newWidth, int newHeight) {
		int w = Math.max(newWidth, MINSIZE);
		int h = Math.max(newHeight, MINSIZE);
		super.setSize(w, h);
	}

	/**
	 * Set canvas size. 
	 * @param d    dimension of the component
	 */
	public void setSize(Dimension d) {
		this.setSize(d.width, d.height);
	}

	/**
	 * Set the frame width of component face
	 * @param pixels   the pixels of the frame width
	 */
	public void setFrameWidth(int pixels) {
		frameW = pixels;
	}

	/**
	 * Return the frame width of component face
	 */
	public int getFrameWidth() {
		return frameW;
	}

	/**
	 * Set the label style property
	 * @param newLab   the new label style, which may be one of:
	 *             <OL>
	 *             <LI>  CConstants.LABEL_NONE
	 *             <LI>  CConstants.LABEL_AXIS
	 *             <LI>  CConstants.LABEL_VALUE
	 *             <LI>  CConstants.LABEL_ALL
	 *             </OL>
	 */
	public void setLabelStyle(int newLab) {
		if (labelStyle != newLab) {
			labelStyle = newLab;
		}
	}

	/**
	 * Return the label style property
	 */
	public int getLabelStyle() {
		return labelStyle;
	}

	/**
	 * Set the color mode property
	 * @param newColorMode   the new color mode, which may be one of:
	 *             <OL>
	 *             <LI>  CConstants.CLRMOD_STATIC
	 *             <LI>  CConstants.CLRMOD_ALARM
	 *             </OL>
	 */
	public void setColorMode(int newColorMode) {
		if (colorMode != newColorMode) {
			colorMode = newColorMode;
		}
	}

	/**
	 * Return the color mode property
	 */
	public int getColorMode() {
		return colorMode;
	}

	/**
	 * Set the range style property
	 * @param newRangeStyle  the new range style, which may be one of:
	 *             <OL>
	 *             <LI>  CConstants.RANGE_AUTO
	 *             <LI>  CConstants.RANGE_STATIC
	 *             </OL>
	 */
	public void setRangeStyle(int newRangeStyle) {
		if (rangeStyle != newRangeStyle) {
			rangeStyle = newRangeStyle;
		}
	}

	/**
	 *  Return the range style property
	 */
	public int getRangeStyle() {
		return rangeStyle;
	}

	/**
	 * Set the display limits. The range style property
	 * will be forced to RANGE_STATIC.
	 * 
	 * @param lo   the display low
	 * @param hi   the display high
	 *
	 * hi must be larger than lo otherwise nothing is changed.
	 */
	public void setDisplayRange(double lo, double hi) {
		if (hi > lo) {
			displayHi = hi;
			displayLo = lo;
			rangeStyle = RANGE_STATIC;
			rangeHi = displayHi;
			rangeLo = displayLo;
		}
	}

	/**
	 * Set the display high. The range style property
	 * will be forced to RANGE_STATIC.
	 * 
	 * @param hi   the display high
	 *
	 * hi must be larger than displayLo otherwise nothing is changed.
	 */
	public void setDisplayHi(double hi) {
		if (hi > displayLo) {
			displayHi = hi;
			rangeHi = displayHi;
			rangeStyle = RANGE_STATIC;
		}
	}

	/**
	 * Set the display low. The range style property
	 * will be forced to RANGE_STATIC.
	 * 
	 * @param lo   the display low
	 *
	 * lo must be smaller than displayHi otherwise nothing is changed.
	 */
	public void setDisplayLo(double lo) {
		if (lo < displayHi) {
			displayLo = lo;
			rangeLo = displayLo;
			rangeStyle = RANGE_STATIC;
		}
	}

	/**
	 * Return display high
	 */
	public double getDisplayHi() {
		return displayHi;
	}

	/**
	 * Return display low
	 */
	public double getDisplayLo() {
		return displayLo;
	}

	public void setAlarmRange(double lo, double hi) {
		if (hi > lo) {
			alarmHi = hi;
			alarmLo = lo;
			colorMode = CLRMOD_ALARM;
		}
	}

	public void setWarningRange(double lo, double hi) {
		if (hi > lo) {
			warningHi = hi;
			warningLo = lo;
			colorMode = CLRMOD_ALARM;
		}
	}

	public void setColorZone(double zoneLo, double zoneHi, Color color) {
		hasZones = true;
		colorZones.addElement(new ColorZone(zoneLo, zoneHi, color));
	}

	//--------------------------------------------------------------
	// child class needs to overwrite these two methods 
	//--------------------------------------------------------------
	protected void paintBackground(Graphics g) {
	}
	
	protected void paintForeground(Graphics g) {
	}

	/**
	 * Draw offscreen graphics and repaint it onto the screen.
	 * Calls derived class' paintForeground(g) and paintBackground(g).
	 */
	public final void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paintBackground(g2);
		paintForeground(g2);
	}

	public void propertyChange(PropertyChangeEvent pce) {
		//TDebug.println(0, "PropertyChange meter: " + pce.getNewValue());
		setValue(pce.getNewValue());
	}

	public void setValue(Object obj) {
		//TDebug.println(0,"setValue" + obj);
		if (obj instanceof Number) {
			setValue(((Number) obj).doubleValue());
		} else if (obj instanceof String) {
			try {
				double d = Double.parseDouble((String) obj);
				setValue(d);
			} catch (NumberFormatException ne) {
				TDebug.println(0, "NumberFormatException: '" + obj);
			}
		}
	}

	public void setValue(double value) {
		currVal = value;
		if (colorMode == CLRMOD_STATIC) {
			currColor = NEEDLECOLOR;
		} else {
			if ((currVal > alarmLo) && (currVal < alarmHi))
				currColor = alarmColor;
			else if ((currVal < warningHi) && (currVal > warningLo)) {
				currColor = warningColor;
			} else
				currColor = NEEDLECOLOR;
		}
		repaint();
	}		
	
} // end ControlAnimatedCanvas
