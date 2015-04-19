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
import teal.core.TUpdatable;
import teal.sim.TSimElement;
import teal.ui.control.ControlGroup;

/**
 *
 * @author Florian Schitter <florian.schitter (at) student.tugraz.at>
 */
public final class GamificationAgent extends ControlGroup implements TUpdatable, TSimElement {
    
    private static final long serialVersionUID = 3761131530902252017L;
    
    private ArrayList<Task> tasks;
    private ArrayList<Task> tasks_backup;
    private int labelWidth = 200; // to position
    private JProgressBar gamificationProgressBar = null;
    private double sumTasks = 0;
    private long startTimeSecond = 0;
    private long timeAllowedSecond = 0;
    private long endTimeSecond = 0;
    private double finished = 0;
    boolean isChecked = false;
            
    public GamificationAgent() {
        super();
        this.setVisible(true); // wenn weiter forgeschritten mit 'false' starten?
        setText("Gamification Panel");
        gamificationProgressBar = new JProgressBar( 0, 100);
        gamificationProgressBar.setValue(0);
        gamificationProgressBar.setStringPainted( true );
        add(gamificationProgressBar);//,BorderLayout.PAGE_START );
        
        tasks = new ArrayList<Task>();
        tasks_backup = new ArrayList<Task>();
    }
    
    public void addTask(Task task) {
        task.setPreferredSize(new Dimension(labelWidth, task.getPreferredSize().height));
        add(task);
        tasks.add(task);
        tasks_backup.add(task);
//        tasks.get(0).setVisible(true);
//        sumTasks++;
    }
    
    public int getSizeOfTaskList() {
        return tasks.size();
    }
    
    public void startTimer()
    {
        System.out.println("GamificationAgent, startTasks() started");
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
    
    public void checkTasks() {
        
        if(!tasks.isEmpty()) {
            Task current = tasks.get(0);
//            current.setVisible(true);
           
            if(current.checkReq()) {
                tasks.remove(0);
                finished++;
                System.out.println("checkTasks, size of tasks: " + tasks.size());
                int progress = (int)((finished/(tasks_backup.size()))*100);
                gamificationProgressBar.setValue((int)progress);
//                current.setVisible(false);
            }
        } else {
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
    
    public void reset() {
        gamificationProgressBar.setValue(0);
        tasks.removeAll(tasks);
        tasks.addAll(tasks_backup);
        finished = 0;
        for (Task task : tasks) {
            task.resetTask();
        }
    }
    
    @Override
    public void update() {
        checkTasks();
        Thread.yield();
    }
    
}
