/* $Id: ColorizerBeanInfo.java,v 1.2 2009/04/24 19:35:59 pbailey Exp $ */

package teal.visualization.processing;

import java.beans.*;
import java.util.Collection;
import java.util.ArrayList;

import teal.core.AbstractElementBeanInfo;
import teal.util.TDebug;

public class ColorizerBeanInfo extends AbstractElementBeanInfo
{

	protected static ArrayList<PropertyDescriptor> sProperties =null;
	protected static Class<Colorizer> baseClass = teal.visualization.processing.Colorizer.class;
	
	static
	{
	   sProperties = new ArrayList<PropertyDescriptor>(AbstractElementBeanInfo.getPropertyList());
       try
       {
            PropertyDescriptor pd = null;

            pd = new PropertyDescriptor("brighten",baseClass);
            System.out.println("In ColorizerBeanInfo: " + pd.toString());
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("hue",baseClass);
			 System.out.println("In ColorizerBeanInfo: " + pd.toString());
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("saturationPoint",baseClass);
			 System.out.println("In ColorizerBeanInfo: " + pd.toString());
			pd.setBound(true);
			sProperties.add(pd);
			pd = new PropertyDescriptor("fallOff",baseClass);
			pd.setBound(true);
			sProperties.add(pd);
			System.out.println("In ColorizerBeanInfo: " + pd.toString());



		}
        catch(IntrospectionException ie)
        {
           TDebug.println(ie.getMessage());
		}
 		TDebug.println(0,baseClass.getName()+"BeanInfo: static complete");    
          
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