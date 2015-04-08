/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import teal.physics.em.RingOfCurrent;

/**
 *
 * @author Florian Schitter <florian.schitter (at) student.tugraz.at>
 */
public class CurrentRequirement extends Requirement {
    int ticks, time;
    double value, range1, range2;
    RingOfCurrent roc;
    
    public CurrentRequirement() {
        super();
        this.value = 0.0;
        this.range1 = 0.0;
        this.range2 = 0.0;
        this.roc = null;
        this.ticks = 0;
        this.time = 75;
    }
    
    public void addRing(RingOfCurrent roc) {
        this.roc = roc;
    }
    
    public void setCurrentValue(double value) {
        this.value = value;
    }
    
    public void setTimeInTicks(int time) {
        this.time = time;
    }
    
    public void setCurrentRange(double range1, double range2) {
        this.range1 = range1;
        this.range2 = range2;
    }
    
    @Override
    public boolean isFullFilled() {
        
        ticks += 1;
//        System.out.format("total flux: %f\n", roc.getTotalFlux());
        double current = roc.getCurrent();
        
        if(value > 0.0) {
            if(current >= (value - 0.02) && current <= (value + 0.02)) {
                if(ticks > time) {
                    fulfilled = true;
                }
            }
        }
        else {
            if(current > range1 && current < range2) {
                if(ticks > time) {
                    fulfilled = true;
                }
            }
            else {
                ticks = 0;
            }
        }
        return fulfilled;
    }
    
    @Override
    public void setRequirementEnabled(boolean b) {
        this.enabled = b;
    }
    
}
