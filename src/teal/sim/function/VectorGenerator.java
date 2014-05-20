/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: VectorGenerator.java,v 1.4 2007/07/16 22:05:02 pbailey Exp $ 
 * 
 */

package teal.sim.function;

import java.beans.PropertyChangeEvent;

import javax.vecmath.Vector3d;

import teal.core.AbstractElement;
import teal.core.PCUtil;
import teal.sim.TSimElement;
import teal.sim.properties.Stepping;

/** 
 * A simple vector generator, this outputs a sequence of Vector3ds, defined by mode.
 * It is over-kill for this to implement Integratable, but we need to think about
 * that whole interface.
 */

public class VectorGenerator extends AbstractElement implements TSimElement, Stepping
{

    private static final long serialVersionUID = 3258695411744323121L;
    
    protected double speed;
	protected Vector3d value;
	protected Vector3d value_d;
	protected Vector3d startPoint; // start point of linear modes, or center point of circular modes
	protected Vector3d endPoint; // end point of linear modes, or radial point circular modes
	protected double scale;
	
	protected int mode;
	
	public static final int MODE_LINE_LINEAR = 0;  //linear interpolation between points
	public static final int MODE_LINE_LINEAR_LEFT = 1;
	public static final int MODE_LINE_LINEAR_RIGHT = 2;
	public static final int MODE_LINE_SINUSOID = 3; // sinusoidal interpolation between points
	public static final int MODE_CIRCULAR = 4; // circular motion
	
	protected boolean loop = true;  //"loop" the animation?
	
	protected double time = 0.0;
	
	protected double acc = 0.0;
	protected double vel = 0.0;
	protected double x = 0.0;
	
	protected double deltaR;
	protected double rad;
	protected double radPerSec;
	
	protected boolean isStepping = true;
	
	public VectorGenerator(Vector3d start, Vector3d end, int moveMode, boolean loopMode)
	{
		super();
		value = new Vector3d();
		startPoint = start;
		endPoint = end;
		mode = moveMode;
		loop = loopMode;
		
		scale = 1.;
		speed = 1.;
	}
	
	public double getTime(){
		return time;
	}
	
	public void setTime(double newTime) {
		time = newTime;
	}
	
	public int getMode(){
		
		return mode;
	}
	
	public void setMode(int themode){
		this.setTime(0.0);
		acc = 0.0;
		vel = 0.0;
		x = 0.0;
		mode = themode;
	}
	
	public boolean isStepping()
	{
		return isStepping;
	}
	
	public void setStepping(boolean b)
	{
		isStepping = b;
	}
	public void nextStep(double dt)
	{
		if(!isStepping)
			return;
		if (mode == MODE_CIRCULAR){
			setValue(new Vector3d(20*Math.cos(time*0.02),20*Math.sin(time*0.02),0));
		}
		else if (mode == MODE_LINE_SINUSOID) {
			Vector3d end = new Vector3d(endPoint);
			Vector3d start = new Vector3d(startPoint);
			Vector3d interp = new Vector3d();
			start.scale(-1.);
			end.add(start);
			end.scale(0.5);
			end.add(startPoint);
			
			interp.interpolate(end,endPoint,Math.pow(Math.sin(time*speed),1));
			
			setValue(interp);
		}
		else if (mode == MODE_LINE_LINEAR_LEFT) {
			if (time < 133.*dt) {
				acc = 0.0; //0.06;
				vel = 0.3;
				x = x + vel*dt;
				
			}
			else if (time >= 133.*dt){
				acc = 0.0;
				vel = 0.0;
				x = x + vel*dt;
			}
			Vector3d stepright = new Vector3d(startPoint);
			stepright.add(new Vector3d(-20.,0.,0.));
			stepright.add(new Vector3d(x ,0,0));
			
			setValue(stepright);
		}
		else if (mode == MODE_LINE_LINEAR_RIGHT) {
			
			if (time < 30.*dt) {
				acc = 0.0;
				vel = vel + acc*dt;
				x = x + vel*dt;
				
			}
			else if (time >= 30.*dt){
				acc = 0.0;
				vel = 0.3;
				x = x + vel*dt;
			}
			Vector3d stepright = new Vector3d();
			stepright.add(new Vector3d(x ,0,0));
			
			setValue(stepright);
		}
		time += dt;
	}
	public void reset()
	{
		time = 0.0;
		acc = 0.0;
		vel = 0.0;
		x = 0.0;
	}
	public void setValue(Vector3d val)
	{
		if (value != val)
		{			
			PropertyChangeEvent pce = PCUtil.makePCEvent(this,"value",value,val);
			value = val;
			firePropertyChange(pce);			
		}
	}
	public void setValue(Object obj)
	{
	}
	public Vector3d getValue()
	{
		return value;
	}
	public void setSpeed(double val)
	{
		if (speed != val)
		{
			PropertyChangeEvent pce = PCUtil.makePCEvent(this,"speed",speed,val);
			speed= val;
			radsPerSec();
			firePropertyChange(pce);				
		}
	
	}

	public double getSpeed()
	{
		return speed;
	}
	
	public void setScale(double s)
	{
		scale = s;
	}
	public double getScale()
	{
		return scale;
	}
	
	private void radsPerSec()
	{
		radPerSec = 2.0 * Math.PI * speed;
	}
}
