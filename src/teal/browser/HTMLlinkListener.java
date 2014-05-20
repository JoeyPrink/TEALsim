/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: HTMLlinkListener.java,v 1.9 2010/07/23 21:38:06 stefan Exp $
 * 
 */

//look into creating a URLStreamHandler to support TEALsim specific content.

package teal.browser;

import java.io.File;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import teal.framework.TAbstractFramework;
import teal.framework.TFramework;

/**
 *  
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.9 $
 */

public class HTMLlinkListener implements HyperlinkListener {

    protected transient TFramework framework;

    public HTMLlinkListener() {
        framework = null;
    }

    public HTMLlinkListener(TFramework fw) {
        framework = fw;
    }

    public TFramework getFramework() {
        return framework;
    }

    public void setFramework(TFramework fw) {
        framework = fw;
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            URL target = e.getURL();
            if (target.getPath().indexOf("TealWorlds/") > 0) {
                if ((framework != null) && (framework instanceof TFramework)) {
                    ((TFramework)framework).load(new File(target.getFile()));
                }
            } else {

                JEditorPane pane = (JEditorPane) e.getSource();
                if (e instanceof HTMLFrameHyperlinkEvent) {
                    HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                    HTMLDocument doc = (HTMLDocument) pane.getDocument();
                    doc.processHTMLFrameHyperlinkEvent(evt);
                } else {
                    try {
                        pane.setPage(e.getURL());
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }
    }
}
