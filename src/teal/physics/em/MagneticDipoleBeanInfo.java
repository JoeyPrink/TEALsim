/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: MagneticDipoleBeanInfo.java,v 1.8 2009/04/24 19:35:50 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import java.beans.*;
import java.util.*;

import teal.util.TDebug;

public class MagneticDipoleBeanInfo extends DipoleBeanInfo
{

	protected static ArrayList<PropertyDescriptor> sProperties =null;
	protected static Class<MagneticDipole> baseClass = MagneticDipole.class;
	
	static
	{
       try
       {
            PropertyDescriptor pd = null;
			sProperties = new ArrayList<PropertyDescriptor>(DipoleBeanInfo.getPropertyList());
/*
			pd = new PropertyDescriptor("boundingArea",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			
*/
			//pd = new PropertyDescriptor("dipoleMoment",baseClass);
			//pd.setBound(true);
			//sProperties.add(pd);
			pd = new PropertyDescriptor("mu",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("avoidSingularity",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("avoidSingularityScale",baseClass);
			sProperties.add(pd);
			pd = new PropertyDescriptor("feelsBField",baseClass);
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