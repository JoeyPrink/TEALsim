/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import teal.ui.UIPanel;

/**
 *
 * @author Viktor Unterberger <viktor.unterberger (at) student.tugraz.at>, Florian Schitter <florian.schitter (at) student.tugraz.at>
 */


public class MultipleChoiceRequirement extends Requirement implements ActionListener {
    private int NUMBER_OF_CHECKBOX = 5; // 5 number of default entries
    String question;
    private ArrayList<JCheckBox> answerN;
    private boolean [] isRightN;
    private JButton doneButton;
    boolean isComplete;
    Task myTask;
  
    public MultipleChoiceRequirement() {
        super();
//        this.reqPanel.setLayout(new GridLayout(NUMBER_OF_CHECKBOX,0));
        this.reqPanel.setLayout(new BoxLayout(this.reqPanel, BoxLayout.Y_AXIS));
        this.reqPanel.setVisible(true);
        BufferedImage image;

            //image = ImageIO.read(new File(MultipleChoiceRequirement.class.getResource("/icons/step.png")));
            JButton picLabel = new JButton(new ImageIcon(getClass().getResource("/images/test.png")));
            this.reqPanel.add(picLabel);
        
        answerN = new ArrayList<JCheckBox>();
        isRightN = new boolean[NUMBER_OF_CHECKBOX];
        
        for(int i = 0; i < NUMBER_OF_CHECKBOX; i++) {
            isRightN[i] = false;
            JCheckBox checkbox = new JCheckBox();
            checkbox.setAlignmentX(Component.CENTER_ALIGNMENT);
            checkbox.setVisible(false);
            answerN.add(checkbox);
            
            this.reqPanel.add(answerN.get(i));
        }  
        doneButton = new JButton("Submit");
        doneButton.addActionListener(this);
        this.reqPanel.add(doneButton);
        isComplete = true;
    }
    
    public MultipleChoiceRequirement(int no_of_answers) {
        super();
//        this.reqPanel.setLayout(new GridLayout(NUMBER_OF_CHECKBOX,0));
        this.reqPanel.setLayout(new BoxLayout(this.reqPanel, BoxLayout.Y_AXIS));
        this.reqPanel.setVisible(true);
        this.NUMBER_OF_CHECKBOX = no_of_answers;
        answerN = new ArrayList<JCheckBox>();
        isRightN = new boolean[NUMBER_OF_CHECKBOX];
        JLabel picLabel = new JLabel(new ImageIcon(getClass().getResource("/images/test.png")));
            this.reqPanel.add(picLabel);
        
        for(int i = 0; i < NUMBER_OF_CHECKBOX; i++) {
            isRightN[i] = false;
            JCheckBox checkbox = new JCheckBox();
            checkbox.setAlignmentX(Component.CENTER_ALIGNMENT);
            checkbox.setVisible(false);
            answerN.add(checkbox);
            
            this.reqPanel.add(answerN.get(i));
        }  
        doneButton = new JButton("Submit");
        doneButton.addActionListener(this);
        this.reqPanel.add(doneButton);
        isComplete = true;
    }
    
    
    
    public void addAnswer(String answer, boolean isRight)
    {
        // JCheckBoxes are created
        boolean empty_found = false;
        if(answer != null)
        {
            for(int i = 0; i < NUMBER_OF_CHECKBOX; i++) {
                if(answerN.get(i).isVisible() == false) {
                    isRightN[i] = isRight;
                    answerN.get(i).setVisible(true);
                    answerN.get(i).setText(answer);
                    empty_found = true;
                    break;
                }
            }
            if(!empty_found)
                System.out.print("Sorry no CheckBox available anymore, increase NUMBER_OF_CHECKBOX");
        } 
    }
    
    
    public void actionPerformed(ActionEvent e) {
        isComplete = true;
        for(int i = 0 ;i < NUMBER_OF_CHECKBOX; i++) {
            if(answerN.get(i).isSelected() != isRightN[i])
            {
                isComplete = false;
                break;
            }
        }
        if(!isComplete) {
            JOptionPane.showMessageDialog(this.reqPanel, "This is not correct. Try again!");
        }
        else {
            JOptionPane.showMessageDialog(this.reqPanel, "This is correct!");
            fulfilled = true;
        }
    } 
    
    @Override
    public boolean isFullFilled() {
        return fulfilled;
    }
    
    public UIPanel getPanel()
    {
        return this.reqPanel;
    }
    
    @Override
    public void setRequirementEnabled(boolean b) {
        this.enabled = b;
        doneButton.setEnabled(b);
        for(int i = 0; i < NUMBER_OF_CHECKBOX; i++) {
            answerN.get(i).setEnabled(b);
        }
    }
    
    @Override
    public void resetRequirement () {
        this.fulfilled = false;
        this.enabled = true;
        
        for (int i = 0; i < NUMBER_OF_CHECKBOX; i++) {
            answerN.get(i).setSelected(false);
            answerN.get(i).setEnabled(true);
        }
        
        doneButton.setEnabled(true);
    }
    
}