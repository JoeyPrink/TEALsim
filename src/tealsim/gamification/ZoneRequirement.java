/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

/**
 *
 * @author Florian Schitter <florian.schitter (at) student.tugraz.at>
 */
public class ZoneRequirement extends Requirement {
    
    ZoneRequirement() {
        super();
        
    }
    
    @Override
    public boolean isFullFilled() {

        return fulfilled;
    }
    
    @Override
    public void setRequirementEnabled(boolean b) {
        this.enabled = b;
    }
    
    @Override
    public void resetRequirement () {
        this.fulfilled = false;
        this.enabled = true;
    }
    
}
