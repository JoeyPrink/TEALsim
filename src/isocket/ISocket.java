/* $Id: ISocket.java,v 1.12 2008/02/19 21:24:15 pbailey Exp $ */


package isocket;

import java.io.*;
import java.net.*;
import java.util.*;

import teal.util.*;

/** 
Test program for socket connection to sLab, very much in progress. 
I expect to have a collection of classes and at least a DataReadyListener
*/

public class ISocket extends Thread
{

      // Thread theThread = null;
        protected int timeout = 5000; // Read timeout in milliseconds
        protected long naptime = 10L;
        protected int port = 2400;
        protected boolean cont = false;
        protected String host = null;
        protected Socket workSocket = null;
        protected Vector drListeners = null;
        
        
        public ISocket(String host,int port)
        {
            this.host = host;
            this.port = port;
            drListeners = new Vector();
            try
            {
            workSocket = new Socket(host,port);
            workSocket.setKeepAlive(true);
            workSocket.setTcpNoDelay(true);
            workSocket.setSoTimeout(timeout);
            }
            catch(UnknownHostException uhe)
            {
                System.out.println(uhe.getMessage());
            }
            catch(IOException ioe)
            {
                System.out.println(ioe.getMessage());
            }
           flushInput();
            setContinue(true);
        }
        
              public boolean getContinue()
        {
            return cont;
        }
   
        public void setContinue(boolean cont)
        {
            this.cont = cont;
        }
 
 public void setPollWait(long waitTime)
 {
    naptime = waitTime;
 }
 public long getPollWait()
 {
    return naptime;
 }
 
 protected int flushInput()
 {
    int amt = 0;
    InputStream is =null;
    if (workSocket != null)
    {
        try
        {
            is = workSocket.getInputStream();
            if(workSocket.isConnected())
            {
        
                amt = is.available();
                if (amt > 0)
                {
            
                    byte[] data = new byte[amt];
                    is.read(data,0,amt);
                }
            }
        }
        catch(IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }
    }
            return amt;
    }
 
 public void run()
 {
    int amt = 0;
    InputStream is =null;
    if (workSocket != null)
    {
        try
        {
            is = workSocket.getInputStream();
            while (workSocket.isConnected() && cont)
            {
        
                amt = is.available();
                if (amt > 0)
                {
            
                    byte[] data = new byte[amt];
                    is.read(data,0,amt);
                    DataReadyEvent dre = new DataReadyEvent(this,data);
                    fireDataReadyEvent(dre);
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
 public boolean isConnected()
 {
    boolean state = false;
    if ((workSocket != null) && (workSocket.isConnected()))
    {
        state = true;
    }
    return state;
 }
 
 public void shutdown()
 throws IOException 
 {
        if(cont) cont = false;
        if (workSocket != null)
        {
            if (workSocket.isConnected())
            {
                workSocket.shutdownInput();
                workSocket.shutdownOutput();
                workSocket.close();
            }
        }
 }
 
    protected void finalize()
    throws Throwable
    {
        shutdown();
        TDebug.println(1,"finalize() called for ISocket");
        super.finalize();
    }
            
 

public void write(char c)
{
    OutputStream os = null;
    if ((workSocket != null) && workSocket.isConnected())
    {
        try
        {
            os = workSocket.getOutputStream();
            if (os != null)
            {
                PrintWriter out = new PrintWriter(os,true);
                out.print(c);
            }
        }
        catch(IOException ioe)
        {
            TDebug.println(0, "Error: " + ioe.getMessage());
        }
    }
}

public int getConnectedPort()
{
    int tport = -1;
    if ( workSocket != null)
    {
        if (workSocket.isConnected())
        {
            tport = workSocket.getPort();
        }
    }
    return tport;
}
        

public void write(String str)
{
    OutputStream os = null;
    if ((workSocket != null) && workSocket.isConnected())
    {
        try
        {
        os = workSocket.getOutputStream();
        if (os != null)
        {
            PrintWriter out = new PrintWriter(os,true);
            out.print(str);
        }
        }
        catch(IOException ioe)
        {
            TDebug.println(0, "Error: " + ioe.getMessage());
        }
    }
}
public synchronized void addDataReadyListener(DataReadyListener drl)
{
    if ( ! drListeners.contains(drl))
        drListeners.add(drl);
    
}
public synchronized void removeDataReadyListener(DataReadyListener drl)
{
    drListeners.remove(drl);
}

protected synchronized void fireDataReadyEvent(DataReadyEvent dre)
{
    Iterator it = drListeners.iterator();
    while ( it.hasNext())
    {
        ((DataReadyListener)it.next()).dataReady(dre);
    }
}



}
