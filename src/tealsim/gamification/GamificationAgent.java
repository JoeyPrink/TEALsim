/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import teal.sim.engine.EngineControl;
import teal.ui.control.ControlGroup;

/**
 *
 * @author Florian Schitter <florian.schitter (at) student.tugraz.at>
 */
public final class GamificationAgent extends ControlGroup {
    
    private ArrayList<Task> tasks = new ArrayList<Task>();
    private  EngineControl mSEC;
    private int labelWidth = 200; //zum Positionieren
    private JProgressBar gamificationProgressBar = null;
    private double sumTasks = 0;
    private long startTimeSecond = 0;
    private long timeAllowedSecond = 0;
    private long endTimeSecond = 0;
    private double finished = 0;
    boolean isChecked = false;
            
            
    public GamificationAgent () {
        super();
        mSEC = null;
        this.setVisible(true); // wenn weiter forgeschritten mit 'false' starten?
        setText("Gamification Panel");
        gamificationProgressBar = new JProgressBar( 0, 100);
        gamificationProgressBar.setValue(0);
        gamificationProgressBar.setStringPainted( true );
        add(gamificationProgressBar);//,BorderLayout.PAGE_START );
    }
    
    public void addTask(Task task) {
        task.setPreferredSize(new Dimension(labelWidth, task.getPreferredSize().height));
        add(task);
        tasks.add(task);
//        tasks.get(0).setVisible(true);
        sumTasks++;
    }
    
    public int getSizeOfTaskList() {
        return tasks.size();
    }
    
    public void startTasks()
    {
        startTimeSecond = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        this.setVisible(true);
//        int i = 0;
//        while(tasks.size()>0)
//        {
//            Task current = tasks.get(i);
//            try {
//                current.run();
//            } catch (InterruptedException ex) {
//                Logger.getLogger(GamificationAgent.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            if(current.checkReq()) {
//                tasks.remove(i);
//                i++;
//            }
//        }
//        if(tasks.isEmpty()) {
//            long endTimeSecond = startTimeSecond - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
//            checkTimerBadge(endTimeSecond);
//        }
    }
    
    public void checkTask() {
        
        if(!tasks.isEmpty()) {
            Task current = tasks.get(0);
//            current.setVisible(true);
           
            if(current.checkReq()) {
                tasks.remove(0);
                finished++;
                int progress = (int)((finished/sumTasks)*100);
                gamificationProgressBar.setValue((int)progress);
//                current.setVisible(false);
            }
        }
        else {
            // ALL TASKS COMPLETED - YAAAAAYYYY!!!
            if(!isChecked) {
                long endTimeSecond = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - startTimeSecond;
                checkTimerBadge(endTimeSecond);
            }
        }
    }
    
    public void setTimerBadge(long time_for_task) {
        timeAllowedSecond = time_for_task;
                
    }
    
    public void checkTimerBadge(long endTime) {
        System.out.println(endTime);
        if(endTime < timeAllowedSecond && endTime > 0)
        {
            isChecked = true;
            ImageIcon timerIcon =  new javax.swing.ImageIcon(getClass().getResource("/tealsim/gamification/timer.png"));
            JOptionPane.showMessageDialog(this, "You received \"Timer-Badge\", for beeing in-time","Badge Received",1, timerIcon);
        }     
    }
    
}
