
/* $Id: IMultiDblSocket.java,v 1.4 2009/04/24 19:35:48 pbailey Exp $ */


package isocket;

import java.io.*;
import java.util.*;


/** 
Test program for socket connection to sLab, very much in progress. 
I expect to have a collection of classes and at least a DataReadyListener
*/

public class IMultiDblSocket extends ISocket
{
    int number = 1;
    

    public IMultiDblSocket(String host, int port, int number)
    {
        super(host,port);
        this.number = number;
    }


 public void run()
 {
    int amt = 0;
    InputStream is = null;
    ArrayList<Object> data = null;
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
                    //System.out.println("NextToken");
                    if (type == StreamTokenizer.TT_NUMBER)
                    {
                    
                        if(amt == 0)
                        {
                            data = new ArrayList<Object>(number);
                        }
                        if (amt < number)
                        {
                            data.add(new Double(st.nval));
                            amt++;
                        }
                        if (amt == number)
                        {
                            amt = 0;
                            DataReadyEvent dre = new DataReadyEvent(this,data);
                            fireDataReadyEvent(dre);
                        }
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


}