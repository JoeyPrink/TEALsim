/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import teal.ui.UIPanel;

/**
 *
 * @author Viktor Unterberger <viktor.unterberger (at) student.tugraz.at>, @author Florian Schitter <florian.schitter (at) student.tugraz.at>
 */
public class Task extends UIPanel implements ActionListener {
    ArrayList<Requirement> req_list;
    String  hintString;
    int timer;
    int points, width;
    UIPanel taskPanelTop = null;
    UIPanel taskPanelCenterFirst = null; 
    UIPanel taskPanelCenterSecond = null;
    JButton hintButton = null;
    JTextField hintTextField = null;
    UIPanel taskPanel = null;
    JCheckBox taskFinishedCheckBox = null;
    JTextArea taskNameTextArea = null;
    String taskNameString = null;
    JEditorPane taskDescription = null;
    Hint myHint = null;
    
     public Task () {
        this.setLayout(new GridBagLayout()); // do 3 panels to account for requirement panel
        GridBagConstraints c = new GridBagConstraints();
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        taskPanelTop = new UIPanel();
        taskPanelCenterFirst = new UIPanel();

        Border border = taskPanelTop.getBorder();
        Border margin = new LineBorder(Color.BLUE,1);
        taskPanelTop.setBorder(new CompoundBorder(border, margin));
        taskPanelTop.setLayout(new GridLayout(0,2));
        taskNameString = "DEFAULT - Task";
        taskFinishedCheckBox = new JCheckBox(taskNameString);
        taskFinishedCheckBox.setEnabled(true);
        taskFinishedCheckBox.setSelected(false);
        taskFinishedCheckBox.addActionListener(this);
        taskFinishedCheckBox.setActionCommand("task checkbox");
        taskPanelTop.add(taskFinishedCheckBox);
        hintButton = new JButton("Hint");
        hintButton.addActionListener(this);
        hintButton.setActionCommand("hint button");
        this.hintString = new String("Sorry, no hint available");
        hintButton.setSize(2,4);
        hintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tealsim/fragezeichen.png")));
        taskPanelTop.add(hintButton);
        this.add(taskPanelTop, c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        taskDescription = new JTextPane();
        taskDescription.setSize(taskPanelCenterFirst.getWidth(), taskPanelCenterFirst.getHeight());
        taskDescription.setEnabled(true);
        taskDescription.setEditable(false);
        taskPanelCenterFirst.add(taskDescription);
        
        req_list = new ArrayList<Requirement>();


        this.add(taskPanelCenterFirst, c);//, BorderLayout.CENTER);
//        this.setVisible(false);

    }

    public Task (String tName, int width) {
        //this.setLayout(new GridBagLayout());
        this.width = width;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        GridBagConstraints c = new GridBagConstraints();
        
        //this.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        // create frame
        Border borderMain = this.getBorder();
        Border marginMain = new LineBorder(Color.DARK_GRAY,2);
        this.setBorder(new CompoundBorder(borderMain, marginMain));
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        
        // normal layout
        // create 2 panels
        taskPanelTop = new UIPanel();
        taskPanelTop.setLayout(new GridBagLayout());
        this.taskPanelCenterFirst = new UIPanel();
       
        c.fill = GridBagConstraints.VERTICAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.9;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        
        //max chars = 34!!!
        taskNameString = tName;
        
        taskFinishedCheckBox = new JCheckBox(taskNameString);
        taskFinishedCheckBox.setEnabled(true);
        taskFinishedCheckBox.setSelected(false);
        taskFinishedCheckBox.addActionListener(this);
        
        JPanel checkboxContainer = new JPanel();
        checkboxContainer.add(taskFinishedCheckBox);
        taskPanelTop.add(checkboxContainer, c);//, BorderLayout.WEST);
        hintButton = new JButton("Hint");
        hintButton.addActionListener(this);
        this.hintString = new String("Sorry, no hint available");
        hintButton.setSize(2,4);
        hintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tealsim/fragezeichen.png")));
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.1;
        taskPanelTop.add(hintButton, c);//, BorderLayout.EAST);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        this.add(taskPanelTop);
        

        taskDescription = new JTextPane();
        taskDescription.setEnabled(true);
        taskDescription.setEditable(false);
        //this.taskPanelCenterFirst.add(taskDescription);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 1;

        this.add(this.taskDescription);
        
        System.out.println("task init, width: " + this.getPreferredSize().width + " height: " + this.getPreferredSize().height);

        req_list = new ArrayList<Requirement>();
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
        
        if(req.getReqPanel() != null) {
            this.taskPanelCenterSecond = req.getReqPanel();
            this.add(taskPanelCenterSecond);
            
            Dimension taskSize = this.getPreferredSize();
            Dimension reqPanelSize = req.getReqPanel().getPreferredSize();
            this.setPreferredSize(new Dimension(taskSize.width, taskSize.height + reqPanelSize.height));
        }
    }
    
    public void addDescription (String desc) {
        // 60 is the average amount of chars a line can hold within gamification panel
        int noOfLines = desc.length()/60 + 1;
        this.taskDescription.setPreferredSize(new Dimension(this.width-20, noOfLines*20));
        this.setPreferredSize(new Dimension(this.getPreferredSize().width, this.getPreferredSize().height + taskDescription.getPreferredSize().height));
        this.taskDescription.setText(desc);
    }
    
    public void addHint (String hint) {
        this.hintString = hint;
        myHint = new Hint(hint);
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
        
        this.myHint.display();
        
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
