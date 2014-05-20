/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ClassComparator.java,v 1.6 2009/04/24 19:35:58 pbailey Exp $ 
 * 
 */

package teal.util;

import java.util.*;



/** test for a list of classes and instances which may or may not
be used as part of some condition.
**/

@SuppressWarnings("unchecked")
public class ClassComparator
	implements Comparator
{


/**
<pre><code>
 order imposed by this comparator is:
 	Classes are less than instances.
	A class is greater than another class unless it is
		a superClass of the class.
	Multiple instances maybe added to the set as long as
		they are not equal.
	Order of instances may be grouped by Class in the future.
</code></pre>
**/

	public int compare(Object x,Object y)
	{
		TDebug.println(1,"comparing: "+ x.toString() + " & " +y.toString());
		int status = 0;	
		if (x instanceof Class)
		{
			if (! (y instanceof Class))
			{
				status = -1;
			}
			else // We have two Classes
			{
				Class<?> cx = (Class<?>) x;
				Class<?> cy = (Class<?>) y;
				if(cx.equals(cy)){
					status = 0;
				}
				else if (cx.isAssignableFrom(cy))
				{
					status = -1;
				}
				else
				{
					status = 1;
				}
			}
		}
		// Its an instance
		else
		{
			if (y instanceof Class)
			{
				status = 1;
			}
			else // two instances
			{
				if(x.equals(y))
					status = 0;
				else
					status = 1;	
			}
		}
		TDebug.println(1,"Status = " +status + " comparing: "+ x.toString() + " & " +y.toString());
		return status;
	}
	

}
