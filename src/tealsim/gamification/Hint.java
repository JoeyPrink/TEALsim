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
    int ticks;
    
    public Hint(String text) {
        this.text = text;
        this.ticks = 300;
    }
    
    public void display() {
        this.ticks--;
        
        if(this.ticks == 0) {
            JOptionPane.showMessageDialog(null, this.text);
            this.ticks = 300;
        }
    }
}
