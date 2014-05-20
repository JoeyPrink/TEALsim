/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

/**
 *
 * @author Georg
 */
public class Requirement {
 
    boolean fullfilled;
    
    public Requirement () {
        this.fullfilled = false;
    }
    
    public boolean isFullFilled() {
        System.out.println(fullfilled?"fullfilled":"not fullfilled");
        return fullfilled;
    }
    
}
