package teal.ui.control.meters;

import java.awt.*;

import javax.swing.*;

import teal.ui.*;

/** 
 * It is an abstract class. The subclass must implement the propertyChange
 * and setValue method.
 */
public abstract class ControlCanvas extends UIPanel implements ControlConstants {

	static final int MINSIZE = 5;

	static final Color PANELCOLOR = new Color(63, 68, 60);
	static final Color TEXTCOLOR = Color.lightGray;
	static final Color NEEDLECOLOR = Color.lightGray;

	static final Color DEFAULT_WARNING_COLOR = new Color(250, 200, 0);
	static final Color DEFAULT_ALARM_COLOR = new Color(130, 0, 0);
	//static final Color DEFAULT_FG = TEXTCOLOR;
	//static final Color DEFAULT_BG = PANELCOLOR;

	static final Color DEFAULT_FG = TEXTCOLOR;
	static final Color DEFAULT_BG = PANELCOLOR;

	protected static String BLANK = "";
	protected String deviceName = null;
	protected String propertyName = null;

	//--- define default value --------------------------
	protected Color warningColor = DEFAULT_WARNING_COLOR;
	protected Color alarmColor = DEFAULT_ALARM_COLOR;

	protected int colorMode = CLRMOD_STATIC;
	protected int labelStyle = LABEL_NONE;
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

	protected Font font = new Font("Dialog", Font.PLAIN, 11);

	//--- boolean flag -------------------------------------------
	protected boolean firstTime = true;
	protected boolean firstCall = true;

	protected double currVal = 0;
	protected Color currColor = null;

	/**
	 * Null Constructor
	 */
	public ControlCanvas() {
		this(0, 0);
	}

	/**
	 * Constructor with device name and property name
	 * 
	 * @param dName    device name
	 * @param pName    property name
	 */
	public ControlCanvas(String dName, String pName) {
		setSize(0, 0);
		setDeviceAndPropertyName(dName, pName);
	}

	/**
	 * Constructor
	 * Defines the canvas size and uses default foreground and 
	 * background colors.
	 * @param width    component width
	 * @param height   component height
	 */
	public ControlCanvas(int width, int height) {
		setSize(width, height);
		setForeground(getForeground());
		setBackground(getBackground());
		setToolTipText("name");
		ToolTipManager.sharedInstance().setInitialDelay(300);
		setDoubleBuffered(true);
		setOpaque(false);
	}

	/**
	 * Constructor
	 * Defines the canvas size and sets foreground and background colors.
	 * @param width     component width
	 * @param height    component height
	 * @param fg        foreground color
	 * @param bg        background color
	 */
	public ControlCanvas(int width, int height, Color fg, Color bg) {
		this(width, height);
		if (fg != null)
			setForeground(getForeground());
		if (bg != null)
			setBackground(getBackground());
	}

	public Dimension getPreferredSize() {
		return getSize();
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public String getToolTipText() {
		return (deviceName + "." + propertyName);
	}

	/**
	 * Set limitsContext to get all meta-data at first time
	 */
	synchronized protected void getFirstValue() {
		try {
			if (deviceName == null || propertyName == null)
				return;
			firstCall = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the Device name 
	 * @param newName     the name of device you want to connect
	 */
	synchronized public void setDeviceName(String newName) {
		newName = ControlUtility.makeValidName(newName);
		deviceName = newName;
		getFirstValue();
	}

	/**
	 * Return the device name to which this component is connected.
	 * If device name is not set, return empty string "". 
	 */
	public String getDeviceName() {
		return (deviceName == null) ? BLANK : deviceName;
	}

	/**
	 * Set device property name 
	 * @param newName    the name of device property you want to connect
	 */
	synchronized public void setPropertyName(String newName) {
		newName = ControlUtility.makeValidName(newName);
		propertyName = newName;
		getFirstValue();
	}

	/**
	 * Return the device property name to which this component is connected.
	 * If property name is not set, return empty string "". 
	 */
	public String getPropertyName() {
		return (propertyName == null) ? BLANK : propertyName;
	}

	/**
	  * Set device name and device property name
	  * @param newDeviceName    the device name you want to connect
	  * @param newPropertyName  the device property name you want to connect
	  */
	synchronized public void setDeviceAndPropertyName(String newDeviceName, String newPropertyName) {
		newDeviceName = ControlUtility.makeValidName(newDeviceName);
		newPropertyName = ControlUtility.makeValidName(newPropertyName);
		deviceName = newDeviceName;
		propertyName = newPropertyName;
		getFirstValue();
	}

} // end of ControlCanvas class
