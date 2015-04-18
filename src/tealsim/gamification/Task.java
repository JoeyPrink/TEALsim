/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import teal.ui.UIPanel;

/**
 *
 * @author Viktor Unterberger <viktor.unterberger (at) student.tugraz.at>
 */
public class Task extends JPanel implements ActionListener {
    ArrayList<Requirement> req_list;
    String  hintString;
    int timer;
    int points;
    UIPanel taskPanelUp = null;
    UIPanel taskPanelCenterFirst = null; 
    UIPanel taskPanelCenterSecond = null;
    JButton hintButton = null;
    JTextField hintTextField = null;
    UIPanel taskPanel = null;
    JCheckBox taskFinishedCheckBox = null;
    JTextArea taskNameTextArea = null;
    String taskNameString = null;
    JEditorPane taskDescription = null;
    
     public Task () {
        this.setLayout(new GridLayout(3,0)); // do 3 panels to account for requirement panel
        
        // create frame
        Border borderMain = this.getBorder();
        Border marginMain = new LineBorder(Color.DARK_GRAY,2);
        this.setBorder(new CompoundBorder(borderMain, marginMain));
        
        
        // normal layout
        // create 2 panels
        taskPanelUp = new UIPanel();
        taskPanelCenterFirst = new UIPanel();
        //        taskPanelCenterSecond = new UIPanel();
        //        taskPanelDown = new UIPanel();
        
        // fill panels
        //1)
        Border border = taskPanelUp.getBorder();
        Border margin = new LineBorder(Color.BLUE,1);
        taskPanelUp.setBorder(new CompoundBorder(border, margin));
        taskPanelUp.setLayout(new GridLayout(0,2));
        taskNameString = "DEFAULT - Task";
        taskFinishedCheckBox = new JCheckBox(taskNameString);
        taskFinishedCheckBox.setEnabled(true);
        taskFinishedCheckBox.setSelected(false);
        taskFinishedCheckBox.addActionListener(this);
        taskFinishedCheckBox.setActionCommand("task checkbox");
        taskPanelUp.add(taskFinishedCheckBox);//, BorderLayout.WEST);
        hintButton = new JButton("Hint");
        hintButton.addActionListener(this);
        hintButton.setActionCommand("hint button");
        this.hintString = new String("Sorry, no hint available");
        hintButton.setSize(2,4);
        hintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tealsim/fragezeichen.png")));
        taskPanelUp.add(hintButton);//, BorderLayout.EAST);
       
        
        //2)
        taskDescription = new JEditorPane("DEFAULT - No Description", null);//,4,10);
        //        taskDescription.setColumns(50);
        //        taskDescription.setRows(3);
        taskDescription.setSize(taskPanelCenterFirst.getWidth(), taskPanelCenterFirst.getHeight());
        taskDescription.setEnabled(true);
        taskDescription.setEditable(false);
        taskPanelCenterFirst.add(taskDescription);
        
        req_list = new ArrayList<Requirement>();

        this.add(taskPanelUp);//, BorderLayout.NORTH);
        this.add(taskPanelCenterFirst);//, BorderLayout.CENTER);
//        this.setVisible(false);

    }

    public Task (String tName, int width, int height) {
        this.setLayout(new GridLayout(3,0)); // do 3 panels to account for requirement panel
        //this.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        
        // create frame
        Border borderMain = this.getBorder();
        Border marginMain = new LineBorder(Color.DARK_GRAY,2);
        this.setBorder(new CompoundBorder(borderMain, marginMain));
        
        
        // normal layout
        // create 2 panels
        taskPanelUp = new UIPanel();
        taskPanelCenterFirst = new UIPanel();
        //        taskPanelCenterSecond = new UIPanel();
        //        taskPanelDown = new UIPanel();
        
        // fill panels
        //1)
        Border border = taskPanelUp.getBorder();
        Border margin = new LineBorder(Color.BLACK,1);
        taskPanelUp.setBorder(new CompoundBorder(border, margin));
        taskPanelUp.setLayout(new GridLayout(0,2));
        taskNameString = tName;
        taskFinishedCheckBox = new JCheckBox(taskNameString);
        taskFinishedCheckBox.setEnabled(true);
        taskFinishedCheckBox.setSelected(false);
        taskFinishedCheckBox.addActionListener(this);
        taskPanelUp.add(taskFinishedCheckBox);//, BorderLayout.WEST);
        hintButton = new JButton("Hint");
        hintButton.addActionListener(this);
        this.hintString = new String("Sorry, no hint available");
        hintButton.setSize(2,4);
        hintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tealsim/fragezeichen.png")));
        taskPanelUp.add(hintButton);//, BorderLayout.EAST);
        
        //2)
        taskDescription = new JEditorPane("DEFAULT - No Description", null);//,4,10);
        taskDescription.setPreferredSize(new Dimension(width, height));
        //        taskDescription.setColumns(50);
        //        taskDescription.setRows(3);
        //taskDescription.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        taskDescription.setEnabled(true);
        taskDescription.setEditable(false);
        taskPanelCenterFirst.add(taskDescription);
        
        req_list = new ArrayList<Requirement>();

        this.add(taskPanelUp);//, BorderLayout.NORTH);
        this.add(taskPanelCenterFirst);//, BorderLayout.CENTER);
//        this.setVisible(false);
    }
    
    
    public void run() throws InterruptedException {
    
        System.out.println(this.taskNameTextArea.getText());
        // check box -> move on to next task (agent)
        
    }
    
    public void addName(String name) {
        taskFinishedCheckBox.setName(name);
    }
    
    public void addRequirement (Requirement req) {
        this.req_list.add(req);
        this.taskPanelCenterSecond = req.getReqPanel();
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
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == hintButton) {
            JOptionPane.showMessageDialog(this, this.hintString);
        }
        else if(e.getSource() == taskFinishedCheckBox) {
            taskFinishedCheckBox.setSelected(false);
        }
    } 
    
    public UIPanel getPanel()
    {
        return taskPanel;
    }
    
    public boolean checkReq() {
        boolean all_fulfilled = true;
        
        for (Requirement req : req_list) {
            if(!req.isFullFilled())
                all_fulfilled = false;
        }
        
        if(all_fulfilled) {
            for (Requirement req : req_list) {
                taskFinishedCheckBox.setSelected(true);
                taskFinishedCheckBox.setEnabled(false);
                hintButton.setEnabled(false);
                taskDescription.setEnabled(false);
                req.setRequirementEnabled(false);
            }
            
            return true;
        }
        return false;
    }
    
    public void resetTask() {
        taskFinishedCheckBox.setSelected(false);
        taskFinishedCheckBox.setEnabled(true);
        hintButton.setEnabled(true);
        taskDescription.setEnabled(true);
        for (Requirement req : req_list)
            req.resetRequirement();
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
