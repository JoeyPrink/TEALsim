/*
 * $Id: MenuBar.java,v 1.8 2008/06/03 15:38:34 pbailey Exp $
 */

package teal.framework;

import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar extends JMenuBar implements TAbstractMenuBar {

    private static final long serialVersionUID = 4049917169003673653L;
    
    protected TAbstractFramework framework = null;
    protected JMenu helpMenu;
    protected Vector hasModel = new Vector();

    public MenuBar() {
        super();
        JMenu menu = new JMenu("File");
        menu.setActionCommand("File");
        add(menu);
        menu = new JMenu("Help");
        menu.setActionCommand("Help");
        add(menu);
    }

    public void setFramework(TAbstractFramework fw) {
        framework = fw;
    }

    public TAbstractFramework getFramework() {
        return framework;
    }

    public JMenu add(String title) {
        JMenu help = get("help");
        if (help != null) {
            remove(help);
        }
        JMenu menu = new JMenu(title);
        menu.setActionCommand(title);
        add(menu);
        if (help != null) {
            add(help);
        }
        return menu;
    }

    public JMenu get(String title) {
        JMenu menu = null;
        for (int i = 0; i < getMenuCount(); i++) {
            JMenu m = getMenu(i);
            if (title.compareToIgnoreCase(m.getActionCommand()) == 0) {
                menu = m;
                break;
            }
        }
        return menu;
    }

    public void remove(String title) {
        JMenu m = get(title);
        if (m != null) {
            remove(m);
        }
    }

    public void addAction(String target, Action a) {
        JMenu m = get(target);
        if (m == null) {
            m = add(target);
        }
        JMenuItem mi = new JMenuItem();
        mi.setAction(a);
        int i = 0;
        for (i = 0; i < m.getItemCount(); i++) {
        	if (target.equals("Exit")){
        		i--;
        		break;
        	}
        }
        m.add(mi, i);
    }

    public void removeAction(String source, Action a) {
        JMenu m = get(source);
        if (m != null) {
            for (int j = 0; j < m.getItemCount(); j++) {
                if (m.getItem(j).getAction() == a) {
                    m.remove(j);
                    break;
                }
            }
        }
    }
}
