/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PhysicalObjectBeanInfo.java,v 1.10 2009/04/24 19:35:51 pbailey Exp $ 
 * 
 */

package teal.physics.physical;

import java.beans.*;
import java.util.*;

import teal.render.RenderedBeanInfo;
import teal.util.TDebug;

public class PhysicalObjectBeanInfo extends RenderedBeanInfo
{

	protected static ArrayList<PropertyDescriptor> sProperties =null;
	protected static Class<PhysicalObject> baseClass = PhysicalObject.class;
	
	static
	{
       try
       {
            PropertyDescriptor pd = null;
			sProperties = new ArrayList<PropertyDescriptor>(RenderedBeanInfo.getPropertyList());
			//sProperties = new ArrayList();
			pd = new PropertyDescriptor("angularVelocity",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("collisionController",baseClass);
			pd.setBound(true);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("colliding",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("contraint",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("contrained",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("mass",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("momentOfInertia",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("velocity",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("recievingFog",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("rotation",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("rotating",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("selectable",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("selected",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("uRL",baseClass);
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