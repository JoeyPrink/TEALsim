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
    
    double value, range1, range2;
    RingOfCurrent roc;
    FluxPlot plot;
    
    public FluxRequirement(RingOfCurrent roc, double value) {
        super();
        this.value = value;
        this.range1 = 0.;
        this.range2 = 0.;
        this.roc = roc;
    }
    
    public FluxRequirement(FluxPlot plot, double range1, double range2) {
        super();
        this.value = 0.;
        this.range1 = range1;
        this.range2 = range2;
        this.plot = plot;
    }
    
    @Override
    public boolean isFullFilled() {
        
//        System.out.format("total flux: %f\n", roc.getTotalFlux());
        
        if(this.value > 0.) {
            if(plot.getTotalFlux() == this.value) {
                this.fullfilled = true;
            }
        }
        else {
            if(plot.getTotalFlux() > this.range1 && plot.getTotalFlux() < this.range2) {
                this.fullfilled = true;
            }
        }
        return this.fullfilled;
    }
    
}
