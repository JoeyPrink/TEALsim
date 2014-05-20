/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: RingOfCurrentBeanInfo.java,v 1.8 2009/04/24 19:35:50 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import java.beans.*;
import java.util.*;

import teal.physics.physical.PhysicalObjectBeanInfo;
import teal.util.TDebug;

public class RingOfCurrentBeanInfo extends PhysicalObjectBeanInfo
{

	protected static ArrayList<PropertyDescriptor> sProperties =null;
	protected static Class<RingOfCurrent> baseClass = RingOfCurrent.class;
	
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
			pd = new PropertyDescriptor("current",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("radius",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("feelsBField",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			
			pd = new PropertyDescriptor("generatingB",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("generatingE",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("inducing",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("inductance",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("integrationMode",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			
			pd = new PropertyDescriptor("resistance",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			
			pd = new PropertyDescriptor("torusRadius",baseClass);
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
		return (PropertyDescriptor[]) sProperties.toArray(sPropertyTemplate);
	}

}