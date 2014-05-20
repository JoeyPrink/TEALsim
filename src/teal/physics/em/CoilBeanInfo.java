/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: CoilBeanInfo.java,v 1.8 2009/04/24 19:35:50 pbailey Exp $ 
 * 
 */

package teal.physics.em;

import java.beans.PropertyDescriptor;
import java.util.*;

import teal.util.TDebug;

public class CoilBeanInfo extends RingOfCurrentBeanInfo
{

	protected static ArrayList<PropertyDescriptor> sProperties =null;
	protected static Class<Coil> baseClass = Coil.class;
	
	static
	{
		sProperties = new ArrayList<PropertyDescriptor>(RingOfCurrentBeanInfo.getPropertyList());
/*
       try
       {
            PropertyDescriptor pd = null;


			pd = new PropertyDescriptor("",baseClass);
			pd.setBound(true);
			sProperties.add(pd);			



		}
        catch(IntrospectionException ie)
        {
           TDebug.println(ie.getMessage());
		}
*/
		TDebug.println(1,baseClass.getName()+"BeanInfo: array complete");    
           
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