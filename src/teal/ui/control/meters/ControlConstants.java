package teal.ui.control.meters;

import java.awt.*;

/**
 * This interface contains a collection of constants generally used 
 * for teal.ui.control components 
 */

public interface ControlConstants {
	
	public static final Color REDCOLOR   = new Color(130, 0, 0);
	public static final Color GREENCOLOR = new Color(0, 130, 0);
	public static final Color BLUECOLOR  = new Color(0, 0, 130);

	//------------ Label style option constants ----------------------
	/**
	 * Label style option - no label
	 */
	public static final byte LABEL_NONE = 0;

	/**
	 * Label style option - with tick marks, high and low value
	 */
	public static final byte LABEL_AXIS = 1;

	/**
	 * Label style option - with tick mark, high/low value, 
	 * current value
	 */
	public static final byte LABEL_VALUE = 2;

	/**
	 * Label style option - with tick mark, high/low value, 
	 * current value, channel name
	 */
	public static final byte LABEL_ALL = 3;

	//-------------- Range style option constants ------------------

	/**
	 * Range style option - auto range
	 */
	public static final byte RANGE_AUTO = 0;

	/**
	 * Range style option - user set range
	 */
	public static final byte RANGE_STATIC = 1;

	//--------------- Color mode option constants --------------------

	/**
	 * color mode option - foreground color don't change
	 */
	public static final byte CLRMOD_STATIC = 0;

	/**
	 * color mode option - foreground color changed according to the state 
	 */

	public static final byte CLRMOD_ALARM = 1;
	
	/**
	 * color mode option - foreground color changed according to the state 
	 */
	
	public static final byte CLRMOD_ZONE = 2;
	
	/**
	 * Modes a meter might be in. Currently unused
	 */
	
	 public static final byte MODENONE     = 0;
	 
	 public static final byte MODEPEAK     = 1;
	 
	 public static final byte MODEPEAKHOLD = 2;
	 
	 public static final byte MODEAVG      = 3;
	 
	 public static final byte MODERMS      = 4;
	 
	 public static final byte MODEVU       = 5;

	//----------------- Alignment option constants ------------------
	/**
	 * alignment option - left alignment
	 */
	public static final byte ALIGN_LEFT = 0;

	/**
	 * alignment option - center alignment
	 */
	public static final byte ALIGN_CENTER = 1;

	/**
	 * alignment option - right alignment
	 */
	public static final byte ALIGN_RIGHT = 2;

	//--------------- orietation option constants --------------------
	/**
	 * orientation option - horizontal direction used in CIndicator
	 */
	public static final byte HORIZONTAL = 0;

	/**
	 * orientation option - vertical direction used in CIndicator
	 */
	public static final byte VERTICAL = 1;

	//------------- fill option constants --------------------------
	/**
	 * fill option - fill from edge
	 */
	public static final byte FILL_FROM_EDGE = 0;

	/**
	 * fill option - fill from center
	 */
	public static final byte FILL_FROM_CENTER = 1;

	//------------- format option constants --------------------------
	/**
	 * format option - display the value as a integer number 
	 * (following printf %d conventions)
	 */
	public static final String FRMT_INTEGER = "%d";

	/**
	 * format option - display the value as float number with 3 decimal places
	 * (following printf %.3f conventions)
	 */
	public static final String FRMT_FLOAT = "%.3f";

	/**
	 * format option - display the value as scientific notation
	 * (following printf %.3e conventions)
	 */
	public static final String FRMT_SCIENTIFIC = "%.3e";

	/**
	 * format option - display the value as float number or scientific notation,
	 * whichever is shorter (following printf %.3g conventions)
	 */
	public static final String FRMT_AUTO = "%.3g";

	/**
	 * format option - display the value as unsigned hexadecimal number.
	 * (following printf %#x conventions)
	 */
	public static final String FRMT_HEX = "%#x";

	/**
	 * format option - display the value as unsigned octal number.
	 * (following printf %#o conventions)
	 */
	public static final String FRMT_OCTAL = "%#o";

} // end of CConstants class
