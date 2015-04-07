/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import teal.ui.UIPanel;

/**
 *
 * @author Georg
 */
public abstract class Requirement {
 
    boolean fullfilled;
    boolean enabled;
    UIPanel reqPanel;
    
    public Requirement () {
        this.fullfilled = false;
        this.reqPanel = new UIPanel();
    }
    
    public abstract boolean isFullFilled();
    
    public abstract void setRequirementEnabled(boolean b);
    
}
