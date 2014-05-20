package teal.visualization.processing;

import java.awt.Color;

import teal.core.*;
import javax.vecmath.Vector3d;

//Including JAVA's Color class for the HSV -> RGB conversion method
public class Colorizer extends AbstractElement implements TColorizer {
    /**
	 * 
	 */
	private static final long serialVersionUID = 864319727525630711L;
	/**
	 * 
	 */
	
	
	// break point for coloring
    public double saturationPoint = 0.30;
    public double TargetHue = 0.65;
    public double fallOff = 0.19;
    public boolean bBrighten = true;
    public double MaxZeroLevel = 1000000000.;
    
    public Colorizer()
    {
        this.TargetHue = 0.65;
        this.saturationPoint = 2.0;
        this.fallOff = 0.19;
        this.bBrighten = true;
        this.MaxZeroLevel = 1000000000.;
    }
    
    public Colorizer( double MyTargetHue, double MysaturationPoint, double MyfallOff)
    {
        this.TargetHue = MyTargetHue;
        this.saturationPoint = MysaturationPoint;
        this.fallOff = MyfallOff;
        this.bBrighten = true;
        this.MaxZeroLevel = 1000000000.;
    }
    
    
    public Colorizer( double MyTargetHue, double MysaturationPoint, double MyfallOff, boolean MybBrighten)
    {
        this.TargetHue = MyTargetHue;
        this.saturationPoint = MysaturationPoint;
        this.fallOff = MyfallOff;
        this.bBrighten = MybBrighten;
        this.MaxZeroLevel = 1000000000.;
    }
    
    public Colorizer( double MyTargetHue, double MysaturationPoint, double MyfallOff, boolean MybBrighten, double MaxZeroLevel)
    {
        this.TargetHue = MyTargetHue;
        this.saturationPoint = MysaturationPoint;
        this.fallOff = MyfallOff;
        this.bBrighten = MybBrighten;
        this.MaxZeroLevel = MaxZeroLevel;
    }
    
    public double getSaturationPoint()
    {
    	return saturationPoint;
    	
    }
    
    public void setSaturationPoint(double point)
    {
    	Double old = new Double(saturationPoint);
    	saturationPoint = point;;
    	firePropertyChange("saturation", old, new Double(saturationPoint));
    	//System.out.println("saturation: " + saturationPoint);
    	
    	
    }
    public double getHue()
    {
    	return TargetHue;
    	
    }
    public void setHue(double hue)
    {
    	Double old = new Double(hue);
    	TargetHue = hue;
    	firePropertyChange("hue", old, new Double(TargetHue));
    	//System.out.println("Hue: " + TargetHue);
    	
    }
    public double getFallOff()
    {
    	return fallOff;
    	
    }
    public void setFallOff(double rate)
    {
    	Double old = new Double(fallOff);
    	fallOff = rate;
    	firePropertyChange("fallOff", old, new Double(fallOff));
    	//System.out.println("FallOff: " + fallOff);
    	
    }
    public double getMaxZeroLevel()
    {
    	return MaxZeroLevel;
    	
    }
    public void setMaxZeroLevel(double zeroLevel)
    {
    	MaxZeroLevel = zeroLevel;
    	
    }
    public boolean getBrighten()
    {
    	return bBrighten;
    	
    }
    public void setBrighten(boolean brighten)
    {
    	bBrighten = brighten;
    	
    }
    
    private double IncreaseSat(double fieldMag)
    {
      double S = Math.abs(Math.pow((fieldMag/saturationPoint), 1.0));
      return S;
    }
  
    private double DecreaseVal(double fieldMag)
    {
      double V = Math.pow((saturationPoint / fieldMag),2);
      if ( V < 0.0)
          V = 0.0;
      return V;
    }
  
    private Vector3d ConvertToRGB( float Hue, float Saturation, float Value)
    { 
      Color MyColor = Color.getHSBColor( Hue, Saturation, Value);
      int MyRed = MyColor.getRed();
      int MyGreen = MyColor.getGreen();
      int MyBlue = MyColor.getBlue();
      Vector3d MyVec = new Vector3d((double)MyRed, (double)MyGreen, (double)MyBlue);
      return MyVec;
    }
    
    private double Brighten(double fieldMag, Vector3d p){
    	double totalBright;
    	if (bBrighten == true) 
    	{
    		double brightAdd = ( 1.0 - IncreaseSat(fieldMag));
    		totalBright = (p.z) + brightAdd;
    	}
    	else
    	{
    		double brightAdd = ( 1.0 - Math.sqrt(IncreaseSat(fieldMag)));
    		totalBright = (p.z) + Math.pow(brightAdd,2);
    	}
        
        if (totalBright > 1.0)
            totalBright = 1.0;
            
        return totalBright;
    }
    
    
    public void get(Vector3d p, Vector3d f)
    {
      double r = Math.sqrt(p.x*p.x + p.y*p.y);
      //r = Math.pow(r,1.5);
      if (r!=0.0)
        r = 1.0/Math.sqrt(r);
      if (r == 0.) 
    	  r = MaxZeroLevel;  // this sets the color level you want for zero field strength
      r /= 2000.;
      r = r*26.0;
      double MySaturation = 0.0;
      double MyValue = 1.0;
      
      double AbsR = Math.abs(r);
      
      if ( AbsR < saturationPoint )
      {
          MySaturation = IncreaseSat(AbsR);
          MyValue = 1.0;
      }
      else
      {
          MyValue = DecreaseVal(AbsR);
          MySaturation = 1.0;
      }
      
      Vector3d RGBVec = ConvertToRGB( (float)TargetHue, (float)MySaturation, (float)MyValue);
      
      //System.out.println(MyRed);
      
      // Set the color of the pixel to our values multiplied by the intensity of the DLIC (p.z)
      //f.x = (p.z)*((float)MyRed/255);
      //f.y = (p.z)*((float)MyGreen/255);
      //f.z = (p.z)*((float)MyBlue/255);
      
      // This applies the Brighten function, which flushes out the DLIC grain at high field magnitudes (ie, within AbsR)
      //if (( AbsR < saturationPoint ) && (bBrighten == true))
      if (( AbsR < saturationPoint ))
      {
          f.x = Brighten(AbsR, p)*(RGBVec.x/255.0);
          f.y = Brighten(AbsR, p)*(RGBVec.y/255.0);
          f.z = Brighten(AbsR, p)*(RGBVec.z/255.0);
      }
      else
      {
          f.x = (p.z)*(RGBVec.x/255.0);
          f.y = (p.z)*(RGBVec.y/255.0);
          f.z = (p.z)*(RGBVec.z/255.0);
      }
    }
 
  }

