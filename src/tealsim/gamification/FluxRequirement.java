/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import teal.physics.em.RingOfCurrent;
import teal.plot.FluxPlot;

/**
 *
 * @author Georg
 */
public class FluxRequirement extends Requirement {
    
    int ticks;
    double value, range1, range2;
    RingOfCurrent roc;
    
    public FluxRequirement(RingOfCurrent roc, double value) {
        super();
        this.value = value;
        this.range1 = 0.;
        this.range2 = 0.;
        this.roc = roc;
        this.ticks = 0;
    }
    
    public FluxRequirement(RingOfCurrent roc, double range1, double range2) {
        super();
        this.value = 0.;
        this.range1 = range1;
        this.range2 = range2;
        this.roc = roc;
        this.ticks = 0;
    }
    
    @Override
    public boolean isFullFilled() {
        
        ticks += 1;
//        System.out.format("total flux: %f\n", roc.getTotalFlux());
        
        if(value > 0.) {
            if(roc.getTotalFlux() == value && ticks > 125) {
                fullfilled = true;
            }
        }
        else {
            if(roc.getTotalFlux() > range1 && roc.getTotalFlux() < range2 && ticks > 125) {
                fullfilled = true;
            }
        }
        return fullfilled;
    }
    
}
