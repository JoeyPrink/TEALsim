/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import teal.core.TUpdatable;
import teal.sim.TSimElement;
import teal.ui.UIPanel;

/**
 *
 * @author Florian Schitter <florian.schitter (at) student.tugraz.at>
 */
public final class GamificationAgent extends UIPanel implements TUpdatable, TSimElement {
    
    private static final long serialVersionUID = 3761131530902252017L;
    
    private ArrayList<Task> tasks;
    private ArrayList<Task> tasks_backup;
    private final int labelWidth = 200; // to position
    private JProgressBar gamificationProgressBar = null;
    private double sumTasks = 0;
    private long startTimeSecond = 0;
    private long timeAllowedSecond = 0;
    private long endTimeSecond = 0;
    private double finished = 0;
    private UIPanel tasksPane;
    private final int pane_width = 450;
    private final int sub_pane_width = pane_width-10;
    boolean isChecked = false;
            
    public GamificationAgent() {
        super();
        this.setLayout(new FlowLayout());
        //this.setPreferredSize(new Dimension(pane_width, 200));
        this.setVisible(true); // wenn weiter forgeschritten mit 'false' starten?
        this.setSize(pane_width, 0);
        /*GridBagConstraints c = new GridBagConstraints();
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1;
        c.anchor = GridBagConstraints.PAGE_START;*/
        
        UIPanel progressBarPane = new UIPanel();
        progressBarPane.setPreferredSize(new Dimension(sub_pane_width, 40));
        gamificationProgressBar = new JProgressBar();
        gamificationProgressBar.setPreferredSize(new Dimension(sub_pane_width, 20));
        gamificationProgressBar.setValue(0);
        gamificationProgressBar.setStringPainted(true);
        progressBarPane.add(gamificationProgressBar);
        add(progressBarPane);//,BorderLayout.PAGE_START );
        /*
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 2;
        c.anchor = GridBagConstraints.PAGE_START;*/
        
        tasksPane = new UIPanel();
        tasksPane.setLayout(new GridBagLayout());
        Dimension prefSize = tasksPane.getPreferredSize();
        prefSize.width = sub_pane_width;
        prefSize.height = 0;
        System.out.println("tasks pane height: " + prefSize.height);
        tasksPane.setPreferredSize(prefSize);
        add(tasksPane);
        
        
        tasks = new ArrayList<Task>();
        tasks_backup = new ArrayList<Task>();
    }
    
    public void addTask(Task task) {

        task.setDescriptionSize(this.sub_pane_width-20);
        Dimension prefSize = task.getPreferredSize();
        prefSize.width = this.sub_pane_width;
        task.setPreferredSize(prefSize);
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = tasks.size();
        tasksPane.add(task, c);
        
        tasks.add(task);
        tasks_backup.add(task);

        System.out.println("gamifcaition height: " + this.getHeight());
        System.out.println("task height: " + prefSize.height);
        Dimension tasksPrefSize = tasksPane.getPreferredSize();
        System.out.println("tasks pane width: " + tasksPrefSize.width + " height: " + tasksPrefSize.height);
        this.setSize(this.pane_width, this.getHeight() + prefSize.height);
        this.setPreferredSize(new Dimension(this.pane_width, this.getHeight() + prefSize.height));
        tasksPane.setPreferredSize(new Dimension(tasksPrefSize.width, tasksPrefSize.height + prefSize.height));
    }
    
    public int getSizeOfTaskList() {
        return tasks.size();
    }
    
    public int getSubPaneWidth() {
        return this.sub_pane_width;
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
