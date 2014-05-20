
package isocket;

public class TestSocket implements DataReadyListener
{
    int total= 0;
    
    public static void main(String [] args)
    {
        
        int port = 43970;
        String host = "ludi.mit.edu";
        TestSocket sock = new TestSocket(host,port);
        System.out.println("sock: " + sock);
        
        
    }
    
    TestSocket(String h,int p)
    {
        ISocket sock = new ISocket(h,p);
        sock.addDataReadyListener(this);
        sock.start();
        while (sock.getContinue())
        {
        }
        sock.setContinue(false);
        System.exit(0);
    }
    
    
    public void dataReady(DataReadyEvent ev)
    {
        Object data = ev.getData();
        if (data instanceof byte[])
        {
            byte[] bytes = (byte[]) data;
            for(int i =0; i < bytes.length;i++)
            {
                System.out.print(new Character((char) bytes[i]));
            }
            System.out.println();
        }
        else
        {
            System.out.println(data);
        }
    }

}