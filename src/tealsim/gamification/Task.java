/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import teal.core.TUpdatable;
import teal.framework.TFramework;
import teal.framework.TealAction;

/**
 *
 * @author Flo
 */
public class Task extends JCheckBox implements ActionListener {
    
    Requirement req;
    String hint;
    int timer;
    int points;
    JButton hintButton = null;
    JTextField hintTextField = null;
    JLabel hintLabel = null;
    GamificationAgent gamificationAgent = null;
    
    public Task () {
        super();
        this.setSelected(false);
        this.addActionListener(this);
        
    }
    
    
    public void run() throws InterruptedException {
    
        System.out.println(this.getText());
//        while(true) {
//            //ladida
//            if(req.isFullFilled()) {
//                break;
//            }
//            System.out.println("test");
////            Thread.sleep(500);
//        }
        
        // check box -> move on to next task (agent)
        this.setSelected(true);
        this.setEnabled(false);   
    }
    
    public void addRequirement (Requirement req) {
        this.req = req;
    }
    
    public void addDescription (String desc) {
        this.setText(desc);
    }
    
    public void addHint (String hint) {
        this.hint = hint;

        hintButton = new JButton("Hint");
        hintButton.setHorizontalAlignment(SwingConstants.RIGHT);
        hintButton.addActionListener(this);
        
        hintTextField = new JTextField(hint);
        hintTextField.setHorizontalAlignment(SwingConstants.LEFT);
        hintTextField.setVisible(false);
        hintTextField.setEditable(false);
        
        hintLabel = new JLabel(hint);
        hintLabel.setHorizontalAlignment(SwingConstants.LEFT);
        hintLabel.setVisible(false);
        
    }
    
    public void addPoints (int points) {
        this.points = points;
    }
    
    public void addTimer (int timer) {  // necessary? at what rate are points lost?
        this.timer = timer;
    }
    
        
    public void actionPerformed(ActionEvent e) {
            hintTextField.setVisible(true);
            gamificationAgent.revalidate();         
    } 
}
    
    
//    public void addActions() {
//        TealAction ta = null;
//        ta = new TealAction("Faraday's Law", this);
//        addAction("Help", ta);
//    }
//
//
//    public void actionPerformed(ActionEvent e) {
//        if (e.getActionCommand().compareToIgnoreCase("Faraday's Law") == 0) {
//        	if(mFramework instanceof TFramework) {
//        		((TFramework) mFramework).openBrowser("help/faradayslaw.html");
//        	}
//       
//    }
//    }
//
//    private void addAction(String help, TealAction ta) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
