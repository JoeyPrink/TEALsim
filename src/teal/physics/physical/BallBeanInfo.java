/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: BallBeanInfo.java,v 1.8 2009/04/24 19:35:51 pbailey Exp $ 
 * 
 */

package teal.physics.physical;

import java.beans.*;
import java.util.*;

import teal.util.TDebug;

public class BallBeanInfo extends PhysicalObjectBeanInfo
{

	protected static List<PropertyDescriptor> sProperties =null;
	protected static Class<Ball> baseClass = Ball.class;
	
	static
	{
       try
       {
            PropertyDescriptor pd = null;
			sProperties = new ArrayList<PropertyDescriptor>(PhysicalObjectBeanInfo.getPropertyList());
/*
			pd = new PropertyDescriptor("boundingArea",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			
*/
			pd = new PropertyDescriptor("elasticity",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("generatingG",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("radius",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
		
			TDebug.println(baseClass.getName()+"BeanInfo: array complete");    
		}
        catch(IntrospectionException ie)
        {
           TDebug.println(ie.getMessage());
		}
           
	} 

	public static Collection<PropertyDescriptor> getPropertyList()
	{
		return sProperties;
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return (PropertyDescriptor[]) sProperties.toArray();
	}

}