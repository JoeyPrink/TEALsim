
/* $Id: IDataSocket.java,v 1.4 2009/04/24 19:35:47 pbailey Exp $ */


package isocket;

import java.io.*;
import java.util.*;


/** 
Test program for socket connection to sLab, very much in progress. 
I expect to have a collection of classes and at least a DataReadyListener
*/

public class IDataSocket extends ISocket
{
    

    public IDataSocket(String host, int port)
    {
        super(host,port);
    }


 public void run()
 {
    int amt = 0;
    int count = 0;
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
            boolean inPacket = false;
            Double value = null;
            while (workSocket.isConnected() && cont)
            {
                if (r.ready())

                {
                    type = st.nextToken();
                    //System.out.println("NextToken");
                    if (!inPacket)
                    {
                        if (type == StreamTokenizer.TT_NUMBER)
                        {
                            data = new ArrayList<Object>();
                            inPacket = true;
                            value = new Double(st.nval);
                            amt = 2 * value.intValue();
                            //data.add(new Integer(amt));
                            count = 0;
                        }
                    }
                    else
                    {  // Process the data packet 
                    
                        if (type == StreamTokenizer.TT_NUMBER)
                        {  
                            data.add(new Double(st.nval));
                            count++;
                        }
                        else if (type == StreamTokenizer.TT_WORD)
                        {  
                            data.add(st.sval);
                            count++;
                        }
                     
                        if (amt == count)
                        {
                            amt = 0;
                            DataReadyEvent dre = new DataReadyEvent(this,data);
                            dre.setAmount(amt);
                            fireDataReadyEvent(dre);
                            amt = 0;
                            inPacket = false;
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