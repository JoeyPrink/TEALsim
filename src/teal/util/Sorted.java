/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Sorted.java,v 1.5 2009/04/24 19:35:58 pbailey Exp $ 
 * 
 */

package teal.util;

import java.util.*;

/**
 * A general purpose list that may contain multiple copies of similar items.
 * Normally a Comparator will be specified, or the normal ordering of the objects 
 * will be used,in the case of objects that have the same compair values
 * addition order will be preserved.
 *
 * @author	Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version	$Revision: 1.5 $
 *
 */
@SuppressWarnings("unchecked")
public interface Sorted{

	
	public Comparator comparator();
	public Object first();
	public Object last();

	public Collection head(Object toElement);
	public Collection sub(Object fromElement, Object toElement);
	public Collection tail(Object fromElement);

	public Collection headCompare(Object toElement);
	public Collection subCompare(Object fromElement, Object toElement);
	public Collection tailCompare(Object fromElement);
}
