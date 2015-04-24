/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import teal.ui.UIPanel;

/**
 *
 * @author Viktor Unterberger <viktor.unterberger (at) student.tugraz.at>, Florian Schitter <florian.schitter (at) student.tugraz.at>
 */


public class MultipleChoiceRequirement extends Requirement implements ActionListener {
    private int NUMBER_OF_CHECKBOX = 5; // 5 number of default entries
    String question;
    private ArrayList<JCheckBox> answerN;
    private ArrayList<JTextPane> answerTextN;
    private ArrayList<UIPanel> answerPaneN;
    private JLabel picLabel;
    private boolean [] isRightN;
    private JButton doneButton;
    boolean isComplete;
    GamificationAgent gamificationInstance;
  
    public MultipleChoiceRequirement(GamificationAgent agent, int width) {
        super();
        this.gamificationInstance = agent;
//        this.reqPanel.setLayout(new GridLayout(NUMBER_OF_CHECKBOX,0));
        this.reqPanel = new UIPanel();
        this.reqPanel.setLayout(new BoxLayout(this.reqPanel, BoxLayout.Y_AXIS));
        this.reqPanel.setVisible(true);
        
        picLabel = new JLabel();
        picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.reqPanel.add(picLabel);
       
        answerPaneN = new ArrayList<UIPanel>();
        answerTextN = new ArrayList<JTextPane>();
        answerN = new ArrayList<JCheckBox>();
        isRightN = new boolean[NUMBER_OF_CHECKBOX];
        
        GridBagConstraints c = new GridBagConstraints();
        
        for(int i = 0; i < NUMBER_OF_CHECKBOX; i++) {
            UIPanel answerPane = new UIPanel();
            answerPane.setLayout(new GridBagLayout());
            answerPane.setPreferredSize(new Dimension(width-20, 10));
            
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 0;
            c.ipady = 5;
            c.insets = new Insets(0,15,0,0);
            
            isRightN[i] = false;
            JCheckBox checkbox = new JCheckBox();
            //checkbox.setAlignmentX(Component.CENTER_ALIGNMENT);
            checkbox.setVisible(false);
            answerN.add(checkbox);
            answerPane.add(checkbox);
            
            
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.gridx = 1;
            c.gridy = i;
            c.weightx = 1;
            c.insets = new Insets(0,0,0,0);
            
            JTextPane answerText = new JTextPane();
            answerText.setEnabled(true);
            answerText.setEditable(false);
            answerTextN.add(answerText);
            answerPane.add(answerText);
            
            //answerPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            answerPaneN.add(answerPane);
            this.reqPanel.add(answerPane);
        }
  
        doneButton = new JButton("Submit");
        doneButton.addActionListener(this);
        doneButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.reqPanel.add(doneButton);
        isComplete = true;
    }
    
    public MultipleChoiceRequirement(GamificationAgent agent, int width, int noOfAnswers) {
        super();
        this.gamificationInstance = agent;
//        this.reqPanel.setLayout(new GridLayout(NUMBER_OF_CHECKBOX,0));
        this.reqPanel = new UIPanel();
        this.reqPanel.setLayout(new BoxLayout(this.reqPanel, BoxLayout.Y_AXIS));
        this.reqPanel.setVisible(true);
        
        picLabel = new JLabel();
        picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.reqPanel.add(picLabel);
        
        this.NUMBER_OF_CHECKBOX = noOfAnswers;
        answerPaneN = new ArrayList<UIPanel>();
        answerTextN = new ArrayList<JTextPane>();
        answerN = new ArrayList<JCheckBox>();
        isRightN = new boolean[NUMBER_OF_CHECKBOX];
        
        GridBagConstraints c = new GridBagConstraints();
        
        for(int i = 0; i < NUMBER_OF_CHECKBOX; i++) {
            UIPanel answerPane = new UIPanel();
            answerPane.setLayout(new GridBagLayout());
            answerPane.setPreferredSize(new Dimension(width-20, 0));
            
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 0;
            c.ipady = 5;
            c.insets = new Insets(0,15,0,0);
            
            isRightN[i] = false;
            JCheckBox checkbox = new JCheckBox();
            checkbox.setVisible(false);
            answerN.add(checkbox);
            answerPane.add(checkbox, c);
            
            
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.gridx = 1;
            c.gridy = i;
            c.weightx = 1;
            c.ipady = 0;
            c.insets = new Insets(0,0,0,0);
            
            JTextPane answerText = new JTextPane();
            answerText.setEnabled(true);
            answerText.setEditable(false);
            answerTextN.add(answerText);
            answerPane.add(answerText, c);
            
            //answerPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            answerPaneN.add(answerPane);
            this.reqPanel.add(answerPane);
        }
  
        doneButton = new JButton("Submit");
        doneButton.addActionListener(this);
        doneButton.setAlignmentX(Component.CENTER_ALIGNMENT);
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
                    
                    
                    int noOfLines = answer.length()/50+1;
                    if(noOfLines > 1) {
                        Dimension prefSize = this.reqPanel.getPreferredSize();
                        prefSize.height = prefSize.height + noOfLines*10;
                        this.reqPanel.setPreferredSize(prefSize);
                    }
                   
                    /*prefSize = answerPaneN.get(i).getPreferredSize();
                    prefSize.height = noOfLines*10;
                    answerPaneN.get(i).setPreferredSize(prefSize);*/
                    
                    answerTextN.get(i).setText(answer);
                    empty_found = true;
                    break;
                }
            }
            if(!empty_found)
                System.out.print("Sorry no CheckBox available anymore, increase NUMBER_OF_CHECKBOX");
        } 
    }
    
    public void addImage(String imgPath, int width)
    {
        int pic_height = 220;
        final ImageIcon imageIcon = new ImageIcon(getClass().getResource(imgPath));
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(width/2, pic_height,  java.awt.Image.SCALE_SMOOTH);
        ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
        picLabel.setIcon(scaledImageIcon);
        
        picLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, imageIcon, "", JOptionPane.PLAIN_MESSAGE, null);
            }
        });
        
        Dimension prefSize = this.reqPanel.getPreferredSize();
        prefSize.height = prefSize.height + pic_height;
        this.reqPanel.setPreferredSize(prefSize);
    }
    
    
    
    @Override
    public boolean isFulFilled() {
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
    
    @Override
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
        
        this.gamificationInstance.update();
    }
}