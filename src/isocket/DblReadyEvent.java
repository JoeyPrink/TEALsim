/* $Id: DblReadyEvent.java,v 1.3 2007/07/16 22:04:42 pbailey Exp $ */

package isocket;

import java.util.*;

public class DblReadyEvent extends EventObject {

    private static final long serialVersionUID = 3258133544172861233L;
    Double data = null;

    DblReadyEvent(Object source, double d) {
        this(source, new Double(d));
    }

    DblReadyEvent(Object source, Double d) {
        super(source);
        data = d;
    }

    public Double getData() {
        return data;
    }

    public void setData(Double d) {
        data = d;
    }
}