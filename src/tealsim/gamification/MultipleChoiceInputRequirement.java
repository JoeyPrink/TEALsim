/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import teal.ui.UIPanel;

/**
 *
 * Florian Schitter <florian.schitter (at) student.tugraz.at>
 */


public class MultipleChoiceInputRequirement extends Requirement implements ActionListener {
    String question;
    private JEditorPane questionTextPane;
    private JTextField answerTextField;
    private String answer;
    private JButton doneButton;
    private JLabel picLabel;
    boolean isComplete;
     
    public MultipleChoiceInputRequirement(int width) {
         super();
//        this.reqPanel.setLayout(new GridLayout(NUMBER_OF_CHECKBOX,0));
        this.reqPanel = new UIPanel();
        this.reqPanel.setLayout(new BoxLayout(this.reqPanel, BoxLayout.Y_AXIS));
        this.reqPanel.setVisible(true);
        
        picLabel = new JLabel();
        picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.reqPanel.add(picLabel);
        
        // to have some vertical spacing between components
        this.reqPanel.add(Box.createRigidArea(new Dimension(5,15)));
        
        questionTextPane = new JEditorPane();
        questionTextPane.setEditable(false);
        questionTextPane.setEnabled(true);
        questionTextPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.reqPanel.add(questionTextPane);
        
        this.reqPanel.add(Box.createRigidArea(new Dimension(5,10)));
       
        answerTextField = new JTextField();
        answerTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.reqPanel.add(answerTextField);
        
        this.reqPanel.add(Box.createRigidArea(new Dimension(5,5)));
  
        doneButton = new JButton("Submit");
        doneButton.addActionListener(this);
        doneButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.reqPanel.add(doneButton);
        isComplete = true;
    }
    
    public void addAnswer(String answer) {
        this.answer = answer;
    }
    
    public void addQuestion(String question) {
        this.questionTextPane.setText(question);
        this.reqPanel.revalidate();
    }
    
    public void addImage(String imgPath, int width)
    {
        final ImageIcon imageIcon = new ImageIcon(getClass().getResource(imgPath));
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(width/2, 220,  java.awt.Image.SCALE_SMOOTH);
        ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
        picLabel.setIcon(scaledImageIcon);
        
        picLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, imageIcon, "", JOptionPane.PLAIN_MESSAGE, null);
            }
        });
        
        this.reqPanel.revalidate();
    }
    
    
    public void actionPerformed(ActionEvent e) {
        isComplete = false;

        String answerText = answerTextField.getText();
        
        if(answer.equals(answerText)) {
            isComplete = true;
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
        this.answerTextField.setEnabled(b);
        this.questionTextPane.setEnabled(b);
        this.doneButton.setEnabled(b);
    }
    
    @Override
    public void resetRequirement () {
        this.fulfilled = false;
        this.enabled = true;
        this.answerTextField.setEnabled(true);
        this.questionTextPane.setEnabled(true);
        this.doneButton.setEnabled(true);
    }
    
}