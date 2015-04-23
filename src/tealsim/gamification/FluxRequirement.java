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
 * @author Florian Schitter <florian.schitter (at) student.tugraz.at>
 */
public class FluxRequirement extends Requirement {
    
    private int ticks, time;
    private double value, range1, range2, tolerance;
    private RingOfCurrent roc;
    
    public FluxRequirement() {
        super();
        this.value = 0.;
        this.range1 = 0.;
        this.range2 = 0.;
        this.ticks = 0;
        this.time = 75;
        this.tolerance = 0.02;
    }
    
    public void addRing(RingOfCurrent roc) {
        this.roc = roc;
    }
    
    public void setFluxValue(double value) {
        this.value = value;
    }
    
    public void setFluxValue(double value, double tolerance) {
        this.value = value;
        this.tolerance = tolerance;
    }
    
    public void setFluxRange(double range1, double range2) {
        this.range1 = range1;
        this.range2 = range2;
    }
    
    public void setTimeInTicks(int time) {
        this.time = time;
    }
    
    @Override
    public boolean isFulFilled() {
        
        ticks += 1;
//        System.out.format("total flux: %f\n", roc.getTotalFlux());
        double flux = roc.getTotalFlux();
        
        if(value > 0.) {
            if(flux >= (value - tolerance) && flux <= (value + tolerance)) {
                if(ticks > time) {
                    fulfilled = true;
                }
            }
            else {
                ticks = 0;
            }
        }
        else {
            if(flux > range1 && flux < range2) {
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
    
    @Override
    public void resetRequirement () {
        this.fulfilled = false;
        this.enabled = true;
        this.ticks = 0;
    }
    
}
