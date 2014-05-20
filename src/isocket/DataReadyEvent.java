/* $Id: DataReadyEvent.java,v 1.6 2007/07/16 22:04:42 pbailey Exp $ */

package isocket;

import java.util.*;

public class DataReadyEvent extends EventObject {

    private static final long serialVersionUID = 3256445802514755641L;
    int amount = 0;
    public long time;
    Object data = null;

    DataReadyEvent(Object source) {
        super(source);
        time = System.currentTimeMillis();
    }

    DataReadyEvent(Object source, Object d) {
        super(source);
        time = System.currentTimeMillis();
        data = d;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int count) {
        amount = count;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object d) {
        data = d;
    }
}