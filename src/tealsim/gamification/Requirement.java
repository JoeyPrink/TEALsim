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
public abstract class Requirement {
 
    boolean fullfilled;
    
    public Requirement () {
        this.fullfilled = false;
    }
    
    public abstract boolean isFullFilled();
    
}
