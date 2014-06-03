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
    int ticks;
    double value, range1, range2;
    RingOfCurrent roc;
    
    public CurrentRequirement() {
        super();
        this.value = 0.;
        this.range1 = 0.;
        this.range2 = 0.;
        this.roc = null;
        this.ticks = 0;
    }
    
    public void addRing(RingOfCurrent roc) {
        this.roc = roc;
    }
    
    public void setCurrentValue(double value) {
        this.value = value;
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
        
        if(value > 0.) {
            if(current >= (value - 0.02) && current <= (value + 0.02)) {
                if(ticks > 75) {
                    fullfilled = true;
                }
            }
            else {
                ticks = 0;
            }
        }
        else {
            if(current > range1 && current < range2) {
                if(ticks > 100) {
                    fullfilled = true;
                }
            }
            else {
                ticks = 0;
            }
        }
        return fullfilled;
    }
    
}
