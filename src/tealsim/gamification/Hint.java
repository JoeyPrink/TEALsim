/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import javax.swing.JOptionPane;

/**
 *
 * @author Florian Schitter <florian.schitter (at) student.tugraz.at>
 */
public class Hint extends JOptionPane {
    String text;
    
    public Hint(String text) {
        this.text = text;
    }
    
    public void display() {
        JOptionPane.showMessageDialog(null, this.text);
    }
}
