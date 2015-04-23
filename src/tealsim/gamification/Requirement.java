/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import teal.ui.UIPanel;


/**
 *
 * @author Florian Schitter <florian.schitter (at) student.tugraz.at>
 */
public abstract class Requirement {
 
    protected boolean fulfilled;
    protected boolean enabled;
    protected UIPanel reqPanel = null;
    
    public Requirement () {
        this.fulfilled = false;
        this.enabled = true;
    }
    
    public abstract boolean isFulFilled();
    
    public abstract void setRequirementEnabled(boolean b);
    
    public abstract void resetRequirement();
    
    public UIPanel getReqPanel() {
        return this.reqPanel;
    }
    
}
