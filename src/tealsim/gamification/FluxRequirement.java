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
 * @author Florian Schitter
 */
public class FluxRequirement extends Requirement {
    
    int ticks;
    double value, range1, range2;
    RingOfCurrent roc;
    
    public FluxRequirement() {
        super();
        this.value = 0.;
        this.range1 = 0.;
        this.range2 = 0.;
        this.roc = roc;
        this.ticks = 0;
    }
    
    public void addRing(RingOfCurrent roc) {
        this.roc = roc;
    }
    
    public void addFluxValue(double value) {
        this.value = value;
    }
    
    public void addFluxRange(double range1, double range2) {
        this.range1 = range1;
        this.range2 = range2;
    }
    
    @Override
    public boolean isFullFilled() {
        
        ticks += 1;
//        System.out.format("total flux: %f\n", roc.getTotalFlux());
        double flux = roc.getTotalFlux();
        
        if(value > 0.) {
            if(flux >= (value - 0.02) && flux <= (value + 0.02) && ticks > 75) {
                fullfilled = true;
            }
            else {
                ticks = 0;
            }
        }
        else {
            if(flux > range1 && flux < range2 && ticks > 100) {
                fullfilled = true;
            }
            else {
                ticks = 0;
            }
        }
        return fullfilled;
    }
    
}
