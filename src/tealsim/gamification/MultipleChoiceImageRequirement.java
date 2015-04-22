/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.awt.Component;
import java.awt.Dimension;
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
import teal.ui.UIPanel;

/**
 *
 * Florian Schitter <florian.schitter (at) student.tugraz.at>
 */


public class MultipleChoiceImageRequirement extends Requirement implements ActionListener {
    private int NUMBER_OF_CHECKBOX = 4; // 5 number of default entries
    String question;
    private ArrayList<JCheckBox> answerN;
    private ArrayList<JLabel> answerPicN;
    private boolean [] isRightN;
    private JButton doneButton;
    boolean isComplete;
     
    public MultipleChoiceImageRequirement() {
        super();
//        this.reqPanel.setLayout(new GridLayout(NUMBER_OF_CHECKBOX,0));
        this.reqPanel = new UIPanel();
        this.reqPanel.setLayout(new GridBagLayout());
        this.reqPanel.setVisible(true);
        answerN = new ArrayList<JCheckBox>();
        answerPicN = new ArrayList<JLabel>();
        isRightN = new boolean[NUMBER_OF_CHECKBOX];

        GridBagConstraints c = new GridBagConstraints();
        
        // watch out! GridBagLayout has 3 columns to allow for centering the submit
        // button
        // add first image layout properties to gridbaglayout
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.insets = new Insets(0,0,0,0);
        
        UIPanel answerPane = new UIPanel();
        answerPane.setLayout(new BoxLayout(answerPane, BoxLayout.Y_AXIS));
        
        JLabel picLabel = new JLabel();
        picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        answerPicN.add(picLabel);
        answerPane.add(picLabel);

        JCheckBox checkbox = new JCheckBox();
        checkbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkbox.setVisible(false);
        answerN.add(checkbox);
        answerPane.add(checkbox);
        
        this.reqPanel.add(answerPane, c);
        
        
        // add second image layout properties to gridbaglayout
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.5;
        c.insets = new Insets(0,0,0,0);
        
        answerPane = new UIPanel();
        answerPane.setLayout(new BoxLayout(answerPane, BoxLayout.Y_AXIS));
        
        picLabel = new JLabel();
        picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        answerPicN.add(picLabel);
        answerPane.add(picLabel);

        checkbox = new JCheckBox();
        checkbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkbox.setVisible(false);
        answerN.add(checkbox);
        answerPane.add(checkbox);
        
        this.reqPanel.add(answerPane, c);
        
        
        // add third image layout properties to gridbaglayout
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.5;
        c.ipadx = 0;
        c.insets = new Insets(10,0,0,0);
        
        answerPane = new UIPanel();
        answerPane.setLayout(new BoxLayout(answerPane, BoxLayout.Y_AXIS));
        
        picLabel = new JLabel();
        picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        answerPicN.add(picLabel);
        answerPane.add(picLabel);

        checkbox = new JCheckBox();
        checkbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkbox.setVisible(false);
        answerN.add(checkbox);
        answerPane.add(checkbox);
        
        this.reqPanel.add(answerPane, c);
        
        
        // add fourth image layout properties to gridbaglayout
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 0.5;
        c.ipadx = 0;
        c.insets = new Insets(10,0,0,0);
        
        answerPane = new UIPanel();
        answerPane.setLayout(new BoxLayout(answerPane, BoxLayout.Y_AXIS));
        
        picLabel = new JLabel();
        picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        answerPicN.add(picLabel);
        answerPane.add(picLabel);

        checkbox = new JCheckBox();
        checkbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkbox.setVisible(false);
        answerN.add(checkbox);
        answerPane.add(checkbox);
        
        this.reqPanel.add(answerPane, c);
        
        
        // add submit button to gridbaglayout
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(20,0,0,0);
        c.anchor = GridBagConstraints.LINE_END;
        c.weightx = 0;
        
        doneButton = new JButton("Submit");
        doneButton.addActionListener(this);
        //doneLabel.add(doneButton);
        //doneButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.reqPanel.add(doneButton, c);
        isComplete = true;
    }
    
    
    
    public void addImage(String imgPath, boolean isRight)
    {
        // JCheckBoxes are created
        boolean empty_found = false;
        if(imgPath != null)
        {
            for(int i = 0; i < NUMBER_OF_CHECKBOX; i++) {
                if(answerN.get(i).isVisible() == false) {
                    isRightN[i] = isRight;
                    answerN.get(i).setVisible(true);
                    final ImageIcon imageIcon = new ImageIcon(getClass().getResource(imgPath));
                    Image image = imageIcon.getImage();
                    Image scaledImage = image.getScaledInstance(420/4, 110,  java.awt.Image.SCALE_SMOOTH);
                    ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
                    answerPicN.get(i).setIcon(scaledImageIcon);

                    answerPicN.get(i).addMouseListener(new MouseAdapter()
                    {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            JOptionPane.showMessageDialog(null, imageIcon, "", JOptionPane.PLAIN_MESSAGE, null);
                        }
                    });
                    
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