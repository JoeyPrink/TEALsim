/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package teal.util;

import java.security.Permission;

/**
 *
 * @author Chrisi
 */
public class TEALSimSecurityManager extends SecurityManager {

        private boolean readAccessGranted = false;
        private boolean readDontAskAgain = false;
        private boolean writeAccessGranted = false;
        private boolean writeDontAskAgain = false;

        /** Creates a new instance of our custom SecurityManager */
        public TEALSimSecurityManager() {
          System.out.println("Creating proper Security Manager");
        }

        @Override
        public void checkPermission(Permission perm) {
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
        }

        /**
         * The Wonderland client should never exit, unless when specifically closed.
         * There are, however, a slew of System.exit() calls throughout the code upon
         * error conditions. Here, we catch when these are called and print out a stack
         * track for debugging purposes.
         */
        @Override
        public void checkExit(int status) {
              System.out.println("Client is either exiting or checking whether it can exit (just FYI).");

              /* Print out the stack trace (don't know how to do it other than manually) */
              StackTraceElement[] els = Thread.currentThread().getStackTrace();
              StringBuilder str = new StringBuilder("Information Stack trace from checkExit():\n");
              for (StackTraceElement el : els) {
                  str.append("    at " + el.getClassName() + "." + el.getMethodName() +
                          "(" + el.getFileName() + ":" + el.getLineNumber() + ")\n");
              }
              System.out.println(str.toString());
              super.checkExit(status);
              
              //we might allow the app to quit that way
              //throw new ExitException(status);
        }  
        
//      private synchronized void askUser(boolean readRequest, String filename) {
//        if (readRequest && readDontAskAgain) 
//            if (!readAccessGranted)
//                throw new SecurityException("User Denied Access to file");
//            else
//                return;
//        
//        if (!readRequest && writeDontAskAgain)
//            if (!writeAccessGranted)
//                throw new SecurityException("User Denied Access to file");
//            else
//                return;
//                        
//        JnlpFileAccessDialog d = new JnlpFileAccessDialog(new JFrame(), true, readRequest, filename);
//        d.setVisible(true);
//        if (readRequest) {
//            readAccessGranted = d.isAccessGranted();
//            readDontAskAgain = d.dontAskAgain();
//        } else {
//            writeAccessGranted = d.isAccessGranted();
//            writeDontAskAgain = d.dontAskAgain();  
//            logger.severe("ANSWER "+writeAccessGranted+" "+writeDontAskAgain);
//        }
//        
//        if (!d.isAccessGranted())
//            throw new SecurityException("User Denied Access to file");        
//    }
        
        
    protected static class ExitException extends SecurityException 
    {
        public final int status;
        public ExitException(int status) 
        {
                super("There is no escape!");
                this.status = status;
        }
    }

}
