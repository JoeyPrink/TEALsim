/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import teal.physics.physical.PhysicalObject;
import teal.physics.physical.RectangularBox;

/**
 *
 * @author Florian Schitter <florian.schitter (at) student.tugraz.at>
 */
public class ZoneRequirement extends Requirement {
    
    HashMap<RectangularBox, ArrayList<PhysicalObject>> objects = new HashMap<RectangularBox, ArrayList<PhysicalObject>>();
    
    ZoneRequirement(HashMap<RectangularBox, ArrayList<PhysicalObject>> objects) {
        super();
        
        this.objects = objects;
    }
    
    @Override
    public boolean isFullFilled() {
        // loop through all RectangularBox
        //  loop through ArrayList<PhysicalObject>
        //      if all PhysicalObjects are within their respective RectangularBox return true
        //      else return false
        
        // call contains() method on each RectangularBox for all PhysicalObjects
        

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
