/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: PointChargeBeanInfo.java,v 1.8 2009/04/24 19:35:50 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import java.beans.*;
import java.util.*;

import teal.physics.physical.PhysicalObjectBeanInfo;
import teal.util.TDebug;

public class PointChargeBeanInfo extends PhysicalObjectBeanInfo
{

	protected static ArrayList<PropertyDescriptor> sProperties =null;
	protected static Class<PointCharge> baseClass = PointCharge.class;
	
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
			pd = new PropertyDescriptor("charge",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("fluxMode",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("pauliDistance",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("generatingE",baseClass);
			pd.setBound(true);
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
		return (PropertyDescriptor[]) sProperties.toArray(sPropertyTemplate);
	}

}