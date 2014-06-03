/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import teal.core.TUpdatable;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.ui.UIPanel;

/**
 *
 * @author Viktors
 */
public class Task extends JPanel implements ActionListener {
    Requirement req;
    String  hintString;
    int timer;
    int points;
    UIPanel taskPanelUp = null;
    UIPanel taskPanelCenterFirst= null; 
    UIPanel taskPanelCenterSecond =null;
    UIPanel taskPanelDown = null;
    JButton hintButton = null;
    JTextField hintTextField = null;
    UIPanel taskPanel =null;
    JCheckBox taskFinishedCheckBox = null;
    JTextArea taskNameTextArea = null;
    String taskNameString = null;
    JLabel taskDescription = null;
    
     public Task () {
        this.setLayout(new GridLayout(3,0)); // Plane für 3 Panels, da Requirement auch eines hat
        
        //erzeuge Rahmen
        Border borderMain = this.getBorder();
        Border marginMain = new LineBorder(Color.DARK_GRAY,2);
        this.setBorder(new CompoundBorder(borderMain, marginMain));
        
        
        //Normales Layout
        // Erzeuge 2 Panels
        taskPanelUp = new UIPanel();
        taskPanelCenterFirst = new UIPanel();
        //        taskPanelCenterSecond = new UIPanel();
        //        taskPanelDown = new UIPanel();
        
        //Fuelle Panels
        //1)
        Border border = taskPanelUp.getBorder();
        Border margin = new LineBorder(Color.BLUE,1);
        taskPanelUp.setBorder(new CompoundBorder(border, margin));
        taskPanelUp.setLayout(new GridLayout(0,2));
        taskNameString = "DEFAULT - Task";
        taskFinishedCheckBox = new JCheckBox(taskNameString);
        taskFinishedCheckBox.setEnabled(false);
        taskPanelUp.add(taskFinishedCheckBox);//, BorderLayout.WEST);
        hintButton = new JButton("Hint");
        hintButton.addActionListener(this);
        this.hintString = new String("Sorry, no hint available");
        hintButton.setSize(2,4);
        hintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tealsim/fragezeichen.png")));
        taskPanelUp.add(hintButton);//, BorderLayout.EAST);
       
        
        //2)
        taskDescription = new JLabel("DEFAULT - No Description");//,4,10);
        //        taskDescription.setColumns(50);
        //        taskDescription.setRows(3);
        taskDescription.setEnabled(false);
        taskPanelCenterFirst.add(taskDescription);

        this.add(taskPanelUp);//, BorderLayout.NORTH);
        this.add(taskPanelCenterFirst);//, BorderLayout.CENTER);

    }
    
     
     


    public Task (String tName) {
        this.setLayout(new GridLayout(3,0)); // Plane für 3 Panels, da Requirement auch eines hat
        
        //erzeuge Rahmen
        Border borderMain = this.getBorder();
        Border marginMain = new LineBorder(Color.DARK_GRAY,2);
        this.setBorder(new CompoundBorder(borderMain, marginMain));
        
        
        //Normales Layout
        // Erzeuge 2 Panels
        taskPanelUp = new UIPanel();
        taskPanelCenterFirst = new UIPanel();
        //        taskPanelCenterSecond = new UIPanel();
        //        taskPanelDown = new UIPanel();
        
        //Fuelle Panels
        //1)
        Border border = taskPanelUp.getBorder();
        Border margin = new LineBorder(Color.BLUE,1);
        taskPanelUp.setBorder(new CompoundBorder(border, margin));
        taskPanelUp.setLayout(new GridLayout(0,2));
        taskNameString = tName;
        taskFinishedCheckBox = new JCheckBox(taskNameString);
        taskFinishedCheckBox.setEnabled(false);
        taskPanelUp.add(taskFinishedCheckBox);//, BorderLayout.WEST);
        hintButton = new JButton("Hint");
        hintButton.addActionListener(this);
        this.hintString = new String("Sorry, no hint available");
        hintButton.setSize(2,4);
        hintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tealsim/fragezeichen.png")));
        taskPanelUp.add(hintButton);//, BorderLayout.EAST);
       
        
        //2)
        taskDescription = new JLabel("DEFAULT - No Description");//,4,10);
        //        taskDescription.setColumns(50);
        //        taskDescription.setRows(3);
        taskDescription.setEnabled(false);
        taskPanelCenterFirst.add(taskDescription);

        this.add(taskPanelUp);//, BorderLayout.NORTH);
        this.add(taskPanelCenterFirst);//, BorderLayout.CENTER);
    }
    
    
    public void run() throws InterruptedException {
    
        System.out.println(this.taskNameTextArea.getText());
        // check box -> move on to next task (agent)
        this.taskFinishedCheckBox.setSelected(true);
        this.taskFinishedCheckBox.setEnabled(false);
    }
    
    public void addRequirement (Requirement req) {
        this.req = req;
        this.taskPanelCenterSecond = req.reqPanel;
        this.add(taskPanelCenterSecond);
        this.revalidate();
    }
    
    public void addDescription (String desc) {
        this.taskDescription.setText(desc);
    }
    
    public void addHint (String hint) {
        this.hintString = hint;
    }
    
    public void addPoints (int points) {
        this.points = points;
    }
    
    public void addTimer (int timer) {  // necessary? at what rate are points lost?
        this.timer = timer;
    }
        
    public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(this, this.hintString);      
    } 
    
    public UIPanel getPanel()
    {
        return taskPanel;
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
