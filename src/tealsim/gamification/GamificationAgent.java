/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import teal.framework.TealAction;
import teal.sim.engine.EngineControl;
import teal.ui.UIPanel;
import teal.ui.control.ControlGroup;

/**
 *
 * @author Viktor Unterberger, Florian Schitter
 */
public final class GamificationAgent extends ControlGroup {
    
    private ArrayList<Task> tasks = new ArrayList<Task>();
    private  EngineControl mSEC;
    private JLabel hintLabel;
    private JButton hintButton;
    private JTextField hintTextbox;
    private int labelWidth = 200; //zum Positionieren
    private JProgressBar gamiProgressBar = null;
    private int sumTasks = 0;
    private int finished = 0;
    private String motivationMessage = null;
    
       
            
            
    public GamificationAgent (EngineControl msec) {
        super();
        mSEC = msec;
        this.setVisible(true); // wenn weiter forgeschritten mit 'false' starten?
        setText("Tasks");
        gamiProgressBar = new JProgressBar( 0, 100 );
        gamiProgressBar.setValue(0);
        gamiProgressBar.setStringPainted( true );
        add(gamiProgressBar);//,BorderLayout.PAGE_START );
        motivationMessage = "Start your tasks";
    }
    
    public void addTask(Task task) {
        UIPanel Panel = new UIPanel();
        task.setHorizontalAlignment(SwingConstants.LEFT);
        task.setPreferredSize(new Dimension(labelWidth, task.getPreferredSize().height));
        Panel.add(task);
        if(task.hint!=null)
        {
            Panel.add(task.hintButton);
            task.hintTextField.setPreferredSize(new Dimension(100, task.getPreferredSize().height));
            add(Panel); 

            add(task.hintTextField);
        }
        
        //guiElements.add(plate1Panel);
        
//            
////            groundButton = new JButton(new TealAction("Ground", "Ground", this));
////        groundButton.setFont(groundButton.getFont().deriveFont(Font.BOLD));
////        groundButton.setBounds(40, 570, 195, 24);
////        controls.add(groundButton);
//       
//        hintButton = new JButton("Hint");
//        hintButton.setHorizontalAlignment(SwingConstants.RIGHT);
//        hintButton.addActionListener(this);
//        add(hintButton);
//        
//        hintTextbox = new JTextField(task.hint);
//        hintTextbox.setVisible(true);
//        add(hintTextbox);
//        }
        
        
        
        
//        plate2Charge_label.setPreferredSize(new Dimension(labelWidth, plate2Charge_label.getPreferredSize().height));
//        plate2Charge_label.setHorizontalAlignment(SwingConstants.RIGHT);

//        plate2Charge = new JTextField();
//        plate2Charge.setColumns(4);
//        plate2Charge.setHorizontalAlignment(SwingConstants.RIGHT);
//        plate2Charge.setText(String.valueOf(-pc_charge));
//        plate2Charge.addActionListener(this);
   
        
      //  setID(task.getName());
        tasks.add(task);
        sumTasks++;
    }
    
    public void getSizeOfTaskList() {
        tasks.size();
    }
    
    public void startTasks()
    {
        this.setVisible(true);
        int i = 0;
        while(tasks.size()>0)
        {
            Task temp = tasks.get(i);
            tasks.remove(i);
            i++;
            double progress = ((double)i/sumTasks)*100;
            gamiProgressBar.setValue((int)progress);
//            try {
//                temp.run();
//            } catch (InterruptedException ex) {
//                Logger.getLogger(GamificationAgent.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            gamiProgressBar.setValue();
 
        }
    }
    
    public void checkTask() {
        
//        System.out.println("HWUT UP!");
        
        if(!tasks.isEmpty()) {
            Task current = tasks.get(0);
           
            if(current.checkReq()) {
                tasks.remove(0);
                finished++;
                double progress = ((double)finished/sumTasks)*100;
                gamiProgressBar.setValue((int)progress);
            }
        }
        else {
            // ALL TASKS COMPLETED - YAAAAAYYYY!!!
        }
    }
    
    public void addHint(Hint hint) {
        
    }
    
}
