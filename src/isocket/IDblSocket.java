/* $Id: IDblSocket.java,v 1.4 2007/07/16 22:04:42 pbailey Exp $ */


package isocket;

import java.io.*;
import java.util.*;


/** 
Test program for socket connection to sLab, very much in progress. 
I expect to have a collection of classes and at least a DataReadyListener
*/

public class IDblSocket extends ISocket
{

    Vector doubleListeners = null;

    public IDblSocket(String host, int port)
    {
        super(host,port);
        doubleListeners = new Vector();
    }


 public void run()
 {
    InputStream is =null;
    if (workSocket != null)
    {
        try
        {
            is = workSocket.getInputStream();
            
            Reader r = new BufferedReader(new InputStreamReader(is));
            StreamTokenizer st = new StreamTokenizer(r);
            st.parseNumbers();
            int type =0;
            while (workSocket.isConnected() && cont)
            {
                if (r.ready())
                {
                    type = st.nextToken();
                    if (type == StreamTokenizer.TT_NUMBER)
                    {
                        DblReadyEvent dre = new DblReadyEvent(this,new Double(st.nval));
                        fireDblReadyEvent(dre);
                    }
                }
            
           
                else
                {
                    try
                    {
                        sleep(naptime);
                    }
                    catch(InterruptedException ie)
            {
                System.out.println(ie.getMessage());
            }
                }
            }
        }
        catch(IOException ioe)
            {
                System.out.println(ioe.getMessage());
            }
    }
            
        
 }
 
public synchronized void addDblReadyListener(DblReadyListener drl)
{
    if ( ! doubleListeners.contains(drl))
        doubleListeners.add(drl);
    
}
public synchronized void removeDblReadyListener(DblReadyListener drl)
{
    doubleListeners.remove(drl);
}

protected synchronized void fireDblReadyEvent(DblReadyEvent dre)
{
    Iterator it = doubleListeners.iterator();
    while ( it.hasNext())
    {
        ((DblReadyListener)it.next()).nextDouble(dre);
    }
}

}