/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: WallBeanInfo.java,v 1.8 2009/04/24 19:35:51 pbailey Exp $ 
 * 
 */

package teal.physics.physical;

import java.beans.*;
import java.util.*;

import teal.render.RenderedBeanInfo;
import teal.util.TDebug;

public class WallBeanInfo extends RenderedBeanInfo
{

	protected static ArrayList<PropertyDescriptor> sProperties =null;
	protected static Class<Wall> baseClass = Wall.class;
	
	static
	{
       try
       {
            PropertyDescriptor pd = null;
			sProperties = new ArrayList<PropertyDescriptor>(RenderedBeanInfo.getPropertyList());
			
			pd = new PropertyDescriptor("collisionController",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("colliding",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("edge1",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("edge2",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("elasticity",baseClass);
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
		return (PropertyDescriptor[]) sProperties.toArray(sPropertyTemplate);
	}

}