/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: AbstractViewer3DBeanInfo.java,v 1.7 2009/04/24 19:35:54 pbailey Exp $ 
 * 
 */

package teal.render.viewer;

import java.beans.*;
import java.util.*;

import teal.core.*;
import teal.util.*;

public class AbstractViewer3DBeanInfo extends AbstractElementBeanInfo
{

   protected static List<PropertyDescriptor> sProperties =null;

    static 
    {
        
        Class<?> baseClass = null;
        try
        {
            baseClass = Class.forName("teal.render.viewer.AbstractViewer3D");
        }
        catch(ClassNotFoundException cnf)
        {
            TDebug.println(cnf.getMessage());
        }
        if (baseClass != null)
        {
            try
            {
                PropertyDescriptor pd = null;
                sProperties = new ArrayList<PropertyDescriptor>(AbstractElementBeanInfo.getPropertyList());
                
                // From TViewer
                pd = new PropertyDescriptor("backgroundColor",baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("bounds",baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("boundingArea",baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("maximumSize",baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("minimumSize",baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("preferredSize",baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("pickMode",baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("viewerSize",baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("cursorOnDrag",baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("mouseMoveScale",baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("navigationMode",baseClass);
                pd.setBound(true);
                //sProperties.add(pd);
                //pd = new PropertyDescriptor("picking",baseClass));
                sProperties.add(pd);
                pd = new PropertyDescriptor("refreshOnDrag",baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("vpTranslateScale",baseClass);
                pd.setBound(true);

                // From TViewer3D
                sProperties.add(pd);
                pd = new PropertyDescriptor("cameraChange",baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("cameraDistance",baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("showGizmos",baseClass);
                pd.setBound(true);              
                sProperties.add(pd);
                //pd = new PropertyDescriptor("viewTransform",baseClass); 
                //sProperties.add(pd);
                pd = new PropertyDescriptor("backClipDistance",baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("fieldOfView",baseClass);
                pd.setBound(true);
                sProperties.add(pd);
                pd = new PropertyDescriptor("frontClipDistance",baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("fogBackDistance",baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("fogFrontDistance",baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("fogTransformBackScale",baseClass);
                sProperties.add(pd);
                pd = new PropertyDescriptor("fogTransformFrontScale",baseClass);
                sProperties.add(pd);
                //pd = new PropertyDescriptor("translateEnable",baseClass);
                //sProperties.add(pd);
                //pd = new PropertyDescriptor("rotateEnable",baseClass);
                //sProperties.add(pd);
                //pd = new PropertyDescriptor("zoomEnable",baseClass);
                //sProperties.add(pd);
            
                
                TDebug.println("AbstractViewer3DBeanInfo: array complete");    
            }
            catch(IntrospectionException ie)
            {
               TDebug.println(ie.getMessage());
            }
        }
        
         TDebug.println("AbstractViewer3DBeanInfo static complete");
    } 
    
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        TDebug.println("AbstractViewer3DBeanInfo: getPropertyDescriptors");
        return (PropertyDescriptor[]) sProperties.toArray(sPropertyTemplate);
    }
 
}