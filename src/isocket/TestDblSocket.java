

package isocket;

import java.util.*;

public class TestDblSocket implements DataReadyListener,DblReadyListener
{
    int total= 0;
    
    public static void main(String [] args)
    {
        
        int port = 43970;
        String host = "localhost";
        if(args.length >= 1)
            host = args[0];
        if (args.length >= 2)
        {
            try{      
                port =  Integer.parseInt(args[1]);
            }
            catch(NumberFormatException e)
            {
                System.out.println(e.getMessage());
                System.exit(0);
            }
        }
        
        System.out.println("Connecting to: " + host + ":" + port);
        TestDblSocket sock = new TestDblSocket(host,port);
        System.out.println("sock: " + sock);
        
        
    }
    
    IMultiDblSocket sock;
    IDblSocket sock2;
    
    TestDblSocket(String h,int p)
    {
        sock = new IMultiDblSocket(h,p,1);
        sock.addDataReadyListener(this);
        sock.start();
        //sock2 = new IDblSocket(h,43980);
        //sock2.addDblReadyListener(this);
        //sock2.start();
        
        while (sock.getContinue())
        {
        }
        sock.setContinue(false);
        System.exit(0);
    }
    
    @SuppressWarnings("unchecked")
	public void dataReady(DataReadyEvent ev)
    {
        Object src = ev.getSource();
        if (src == sock)
        {
            System.out.print("Port1 Data: ");
        }
        /*
        else if (src == sock2)
        {
            System.out.print("Port2 Data: ");
        }
        */
        Object data = ev.getData();
        if (data instanceof Collection)
        {
            int count = 0;
            Iterator it  = ((Collection)data).iterator();
            while(it.hasNext()){           
                System.out.println(total++ + ":\t" + (count++) + "\t" + it.next());
            }
        }
        else
        {
            System.out.println(total++ + ":\t" + data);
        }
    }
    
    public void nextDouble(DblReadyEvent ev)
    {
        Object src = ev.getSource();
        if (src == sock)
        {
            System.out.print("Port1 Dbl: ");
        }
        /*
        else if (src == sock2)
        {
            System.out.print("Port2 Dbl: ");
        }
        */
        System.out. println(total++ + ":\t" + ev.getData());
        
    }

}