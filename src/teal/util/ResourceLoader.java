/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: ResourceLoader.java,v 1.5 2007/07/16 22:05:17 pbailey Exp $ 
 * 
 */

package teal.util;

import java.net.*;


public class ResourceLoader extends URLClassLoader
{
    static URL baseURLs []; 
    
    static
    {
        baseURLs = new URL[1];
        try
        {
            baseURLs[0] =  new URL("file:./");
        }
        catch(MalformedURLException e)
        {
        }
    }
   
    public ResourceLoader()
    {
      super(baseURLs);
    }
 
    public void addResourcePath(URL url)
    {
        addURL(url);
    }
    
}