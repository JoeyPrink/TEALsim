/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: SeafloorBeanInfo.java,v 1.8 2009/04/24 19:35:51 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import java.beans.*;
import java.util.*;

import teal.physics.physical.PhysicalObjectBeanInfo;
import teal.util.TDebug;

public class SeafloorBeanInfo extends PhysicalObjectBeanInfo
{

	protected static ArrayList<PropertyDescriptor> sProperties =null;
	protected static Class<Seafloor> baseClass = Seafloor.class;
	
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
			pd = new PropertyDescriptor("application",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("dip",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("generatingB",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("globalField",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("numStripes",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("spreadAxis",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("strike",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("stripsDip",baseClass);
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