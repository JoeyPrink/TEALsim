 /*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Route.java,v 1.17 2010/08/25 22:06:00 stefan Exp $
 * 
 */

 package teal.core;
 
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;

import teal.util.*;

 /**
 * Provides an optimized propertyChangeListener to the class it is added to.
 * Redirects a propertyChange call to the target's set<i>TargetProperty</i> method. 
 * The first call to the setProperty method caches the method as part of the Route object.
 **/
public class Route implements PropertyChangeListener, Serializable {

        /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -8574349728185776203L;
		protected String srcProp;
        protected Object target;
        protected String targetProp;
        protected transient Method mMethod;
        protected transient boolean init = false;
        
        public Route()
        {
        }

        public Route(String attName, TElement targetObj, String tName) {
            srcProp = attName;
            target = targetObj;
            targetProp = tName;
        }
        
 
        
        public boolean equals(Object obj) {
            boolean status = false;
            if (obj instanceof Route)
            {
                Route rec = (Route) obj;
                if ((this.srcProp.compareTo(rec.srcProp) == 0) 
                    && (this.target == rec.target) && (this.targetProp.compareTo(rec.targetProp) == 0))
                {
                    status = true;
                }
            }
            return status;
        }

        public Object getTarget()
        {
            return target;
        }
        public void setTarget(Object obj)
        {
            target = obj;
        }   
        
        public String getTargetProperty()
        {
            return targetProp;
        }
        
        public void setTargetProperty(String str)
        {
            targetProp = str;
        }
        
        public String getSrcProperty()
        {
            return srcProp;
        }
        
        public void setSrcPropery(String str)
        {
            srcProp = str;
        }
        

        
        
        
    /** 
     * The actual dispatch of the propertyChange method. If the method 
     * has not been cached an attempt to resolve the method call will 
     * be performed.
     */
    public void propertyChange(PropertyChangeEvent pce) {
        TDebug.println(1," Route - property: " + pce.getPropertyName());
        if((srcProp.compareTo(pce.getPropertyName()) != 0)){
        		TDebug.println(0,"Route propertyChange: NOT INTERRESTED");
        		return;
        }

        if ((mMethod == null) && (init == false))
        {
            TDebug.println(1," in propertyChange trying find setMethod ");
                getSetMethod(targetProp);
        }
        if (mMethod == null)
        {
            TDebug.println(1,"Error: No method found for " + targetProp);
            return;
        }
//        Object params[] = new Object[1];
//        params[0] = pce.getNewValue();
        try
        {
            mMethod.invoke(target, pce.getNewValue()); 
        }
        catch (InvocationTargetException cnfe) {
            TDebug.println(1, " InvocTargetEx: " + cnfe.getMessage());
        } catch (IllegalAccessException ille) {
            TDebug.println(1, "IllegalAccess: " + ille.getMessage());
        }
    }


	protected void getSetMethod(String name) {
		TDebug.println(3, " In getSetMethod(): " + name);

		// Object param[] = { prop };
		// Class classType[] = { prop.getClass() };

		Method theMethod = null;
		try {
			PropertyDescriptor pd = new PropertyDescriptor(name,
					target.getClass());
			Class<?> paramClass = pd.getPropertyType();
			TDebug.println(1,
					"set: " + name + "   param type = " + paramClass.getName());
			theMethod = pd.getWriteMethod();

			if (theMethod != null) {
				mMethod = theMethod;
				// paramType = paramClass;
			} else {
				TDebug.println(1, "Setter method for " + name + " not found");
				mMethod = null;
				// paramType = null;
			}
			init = true;
		} catch (IntrospectionException ie) {
			TDebug.println(1,
					" Warning: Setter IntrospectionEx: " + ie.getMessage()
							+ "  " + this.getClass().getName());
		}
		/*
		 * catch (InvocationTargetException cnfe) { TDebug.println(0, getID() +
		 * " InvocTargetEx: " + cnfe.getMessage()); } catch
		 * (IllegalAccessException ille) { TDebug.println(0, getID() +
		 * "IllegalAccess: " + ille.getMessage()); }
		 */
	}

	
	// this should not be needed...if method is null after deserialization and init is
	// false, the method will be created on the next call automatically:

	
//	// serializing the method
//	private void writeObject(java.io.ObjectOutputStream s)
//			throws java.io.IOException {
//		s.defaultWriteObject();
//
//		// indicate in stream if mMethod is null
//		if (mMethod == null) {
//			s.writeBoolean(false);
//			return;
//		}
//		s.writeBoolean(true);
//
//		s.writeObject(mMethod.getDeclaringClass());
//		s.writeObject(mMethod.getName());
//		Class<?>[] parameters = mMethod.getParameterTypes();
//		int length = ((parameters == null) ? 0 : parameters.length);
//		s.writeInt(length);
//		for (int i = 0; i < length; ++i)
//			s.writeObject(parameters[i].toString());	
//	}

	
//	private void readObject(java.io.ObjectInputStream s)
//			throws java.io.IOException, ClassNotFoundException,
//			NoSuchMethodException {
//		s.defaultReadObject();
//
//		if (s.readBoolean() == false) // mMethod is null
//			return;
//
//		Class<?> clazz = (Class<?>) s.readObject();
//		String methodName = (String) s.readObject();
//
//		int length = s.readInt();
//		Class<?>[] parameters = new Class<?>[length];
//		for (int i = 0; i < length; ++i){
////			Class<?> nextParam = (Class<?>) s.readObject();
//			String nextParam = (String) s.readObject();
//			
//			//FI XXX ME: terrible workaround...couldn't find out why normal class type 
//			//           serialization and deserialization not work for primitives
//			if(nextParam.equals("boolean"))
//				parameters[i] = boolean.class;
//			else if(nextParam.equals("byte"))
//				parameters[i] = byte.class;
//			else if(nextParam.equals("char"))
//				parameters[i] = char.class;
//			else if(nextParam.equals("short"))
//				parameters[i] = short.class;
//			else if(nextParam.equals("int"))
//				parameters[i] = int.class;
//			else if(nextParam.equals("long"))
//				parameters[i] = long.class;
//			else if(nextParam.equals("float"))
//				parameters[i] = float.class;
//			else if(nextParam.equals("double"))
//				parameters[i] = double.class;
//			else
//				parameters[i] = Class.forName(nextParam);		
//		}
//
//		mMethod = clazz.getDeclaredMethod(methodName, parameters);
//	}

}