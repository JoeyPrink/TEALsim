/* $Id: IDataSocketC.java,v 1.7 2009/04/24 19:35:47 pbailey Exp $ */


package isocket;

import java.io.*;
import java.util.*;

import teal.util.*;


/** 
Test program for socket connection to sLab, very much in progress. 
I expect to have a collection of classes and at least a DataReadyListener
*/

public class IDataSocketC extends ISocket
{


    public IDataSocketC(String host, int port) {
        super(host,port);
    }


    public void run() {
        int amt = 0;
        int count = 0;

        byte [] dataBuf = new byte[512];
        InputStream is = null;
        //ArrayList data = null;
        int byteCount = 0;
        String temp = null;
        if (workSocket != null) {
            try {
                is = workSocket.getInputStream();

                DataInputStream r = new DataInputStream(is);
                String [] contents = null;
                while (workSocket.isConnected() && cont) {
                    if (r.available() >= 4) {
                        byteCount = r.readInt();
                        if (byteCount > 1024)
                        {
                            flushInput();
                        }
                        else
                        {
                        count = 0;
                        try
                        {
                            count = r.read(dataBuf,0,byteCount);
                       
                            temp = new String(dataBuf,0,byteCount);

                            contents = temp.split(",");
                            ArrayList<Object> data = new ArrayList<Object>();
                            amt = 0;
                            for(int i = 0; i < contents.length; i++) {
                                data.add(contents[i++]);
                                data.add(new Double(contents[i]));
                                amt++;
                            }

                            DataReadyEvent dre = new DataReadyEvent(this,data);
                            dre.setAmount(amt);
                            fireDataReadyEvent(dre);
                           
                        }
                        catch(ArrayIndexOutOfBoundsException ae)
                        {
                            TDebug.println(0,"Array Error: ByteCount = " + byteCount + " numberRead = " + count);
                        }
                        }
                    }
                    else {
                        try {
                            sleep(naptime);
                        }
                        catch(InterruptedException ie) {
                            System.out.println(ie.getMessage());
                        }
                    }
                }
            }
            catch(IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }


    }


}
