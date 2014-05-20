/* $Id: Meter.java,v 1.6 2007/08/17 19:38:28 jbelcher Exp $ */

package teal.ui.control.meters;

import java.awt.*;
import java.beans.*;
import java.util.*;

import javax.swing.text.*;

import teal.ui.*;
import teal.util.*;


public abstract class Meter extends UIPanel {

	public static final Color PANELCOLOR	= Color.lightGray;
	public static final Color NEEDLECOLOR	= Color.darkGray;
	public static final Color TEXTCOLOR		= Color.white;
	
	public static final String DEFAULTFONTNAME	= "Dialog";
	public static final int DEFAULTFONTSTYLE	= Font.PLAIN;
	public static final int DEFAULTFONTSIZE		= 9;

	// Modes a meter might be in. Currently unused
	public static final int MODENONE	= 0;
	public static final int MODEPEAK	= 1;
	public static final int MODEPEAKHOLD= 2;
	public static final int MODEAVG		= 3;
	public static final int MODERMS		= 4;
	public static final int MODEVU		= 5;



	// Member data
    
    //protected double precision;
    protected double min = -1.0;
    protected double max = 1.0;
	protected double value = 0.0;
    protected double curValue = 0.0;
    
    protected NumberFormatter numFormat = null;
    
	protected Image meterImage = null;
	protected int width;
	protected int height;
	protected int meterMode;
	
    
	protected boolean hasHighlight = true;
    protected boolean showValue = true;
	protected boolean hasLabels = true;

	protected double labelPercent = 1.0;
	protected Vector labels = new Vector();
	protected int numberOfSections = -1;
	protected Font font;
	protected String fontName = "Dialog";
	protected int fontStyle = Font.PLAIN;
	protected int fontSize = 9;
	protected String caption = "";

	protected Color panelColor = PANELCOLOR;
	protected Color highlightBrighterColor;
 	protected Color highlightDarkerColor;
	protected Color needleColor = NEEDLECOLOR;
    protected Color textColor = TEXTCOLOR;
    protected Color captionBackgroundColor = Color.darkGray;
    
    
    public Meter()
    {
        super();
        numFormat = new NumberFormatter();
    }
    
	public Meter(int width, int height, int meterMode,
				 String fontName, int fontStyle, int fontSize,
				 String caption, boolean hasLabels, String labelsString,
				 int value, boolean hasHighlight, int numberOfSections, 
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
		setNumberOfSections(numberOfSections);
		setPanelColor(panelColor);
		setNeedleColor(needleColor);
		setTextColor(textColor);
		setWidth(width);
		setHeight(height);
	}

	protected void sizeToFit() {

		// Resize to the preferred size
		Dimension d = getPreferredSize();
		setSize(d);

		Component p = getParent();
		if (p != null) {
			p.invalidate();
			p.validate();
		}
	}
	
    public void propertyChange(PropertyChangeEvent pce)
    {
    TDebug.println(0,"PropertyChange meter: " + pce.getNewValue());
        setValue(pce.getNewValue());
    }

	protected int round(double d) {

		return (int) Math.round(d);
	}

	// Start of property methods

	public int getWidth() {

		return width;
	}

	public void setWidth(int width) {

		this.width = width;
		meterImage = null;
		sizeToFit();

	}
	
	public int getHeight() {

		return height;
	}

	public void setHeight(int height) {

		this.height = height;
		meterImage = null;
		sizeToFit();
	}
/*
public String getFormat()
{
    return numFormat.getFormat();
}
public void setFormat(String formatStr)
{
    numFormat.setFormat(formatStr);
}

*/	public int getMeterMode() {

		return meterMode;
	}

	public void setMeterMode(int meterMode) {

		this.meterMode = meterMode;
	}
	
	public Font getFont() {

		return font;
	}

	public void setFont(Font font) {

		this.font = font;

		// Size to the text/font
		meterImage = null;
		sizeToFit();
	}
	
	public String getFontName() {

		return fontName;
	}

	public void setFontName(String fontName) {

		this.fontName = fontName;
		
		font = new Font(fontName, fontStyle, fontSize);
		
		// Size to the text/font
		meterImage = null;
		sizeToFit();
	}

	public int getFontStyle() {

		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {

		this.fontStyle = fontStyle;

		font = new Font(fontName, fontStyle, fontSize);

		// Size to the text/font
		meterImage = null;
		sizeToFit();
	}
	
	public int getFontSize() {

		return fontSize;
	}

	public void setFontSize(int fontSize) {

		this.fontSize = fontSize;

		font = new Font(fontName, fontStyle, fontSize);

		// Size to the text/font
		meterImage = null;
		sizeToFit();
	}
	
	public String getCaption() {

		return caption;
	}

	public void setCaption(String caption) {

		this.caption = new String(caption);

		// Size to the text/font
		meterImage = null;
		sizeToFit();
	}

	public boolean getHasLabels() {

		return hasLabels;
	}

	public void setHasLabels(boolean hasLabels) {

		this.hasLabels = hasLabels;
		meterImage = null;
		repaint();
	}
	
	public double getLabelPercent() {

		return labelPercent;
	}

	public void setLabelPercent(double percent) {

        if( percent <= 0)
            return;
            
		this.labelPercent = percent;

		meterImage = null;
		repaint();
	}
    
    public double getLabelDistance()
    {   
        return (labelPercent * width / 2.0 );
    }

    
    

	// Return a CSV string containing the label strings
	public String getLabelsString() {
		String retString = "";
		
		for (int index=0; index < labels.size(); index++) {
			retString += (String) labels.elementAt(index);
			if (index != labels.size() - 1)
				retString += ",";
		}
		return retString;
	}
	
	public void setLabelsString(String s) {

		// Clear any existing labels
		labels.removeAllElements();

		// Prepare to take the string apart
		StringTokenizer st = new StringTokenizer(s, ",");
		
		// Add each part of string as a separate label
		while(st.hasMoreTokens()) 
			labels.addElement(st.nextToken());

		meterImage = null;
		repaint();
	}

	public double getValue() {

		return value;
	}
    
	public void setMinimum(double m)
	{	min = m;
		         ;	
	}
	public double getMinimum()
	{		
		return min;
	}
	public void setMaximum(double m)
	{
			max = m;
	}
	public double getMaximum()
	{
		return max;
	}
    
    
	protected double checkBounds(double i)
	{
		double val = i;
		if(val < min)
			val = min;
		else if	(val > max)
			val = max;
		return val;
	}
	
    
    
   
	public void setValue(Object obj)
	{
        //TDebug.println(0,"setValue" + obj);
		if (obj instanceof Number)
		{
			setValue(((Number)obj).doubleValue());
		}
		else if ( obj instanceof String)
		{
				try
				{
					double d = Double.parseDouble((String)obj);
					setValue(d);
				}
				catch(NumberFormatException ne)
				{
					TDebug.println(0,"NumberFormatException: '" + obj);
				}
		}
				
	}

	public void setValue(double newValue) {

       value = checkBounds(newValue);
    curValue = newValue;
		repaint();
	}
	
	public boolean getHighlight() {

		return hasHighlight;
	}

	public void setHighlight(boolean hasHighlight) {

		this.hasHighlight = hasHighlight;
		meterImage = null;
		repaint();
	}
	
	public int getNumberOfSections() {

		return numberOfSections;
	}

	public void setNumberOfSections(int numberOfSections) {

		this.numberOfSections = numberOfSections;

		// Calculate meter granularity
		meterImage = null;
		repaint();
	}

	/**
	 * Gets the panel color
	 *
	 * @return Color - The panel's color
	 */
	public Color getPanelColor() {

		return panelColor;
	}

	/**
	 * Sets the panel color
	 *
	 * @param panelColor
	 */
	public void setPanelColor(Color panelColor) {

		this.panelColor = panelColor;
		meterImage = null;
		repaint();
	}


	public Color getNeedleColor() {

		return needleColor;
	}

	public void setNeedleColor(Color needleColor) {

		this.needleColor = needleColor;
		repaint();
	}

	public Color getTextColor() {

		return textColor;
	}

	public void setTextColor(Color textColor) {

		this.textColor = textColor;
		meterImage = null;
		repaint();
	}
	public Color getCaptionBackgroundColor() {

		return captionBackgroundColor;
	}

	public void setCaptionBackgroundColor(Color color) {

		captionBackgroundColor = color;
		meterImage = null;
		repaint();
	}


}

