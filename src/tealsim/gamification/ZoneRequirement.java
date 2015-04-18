/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.util.ArrayList;
import javax.vecmath.Vector3d;
import teal.physics.physical.PhysicalObject;
import teal.physics.physical.RectangularBox;

/**
 *
 * @author Florian Schitter <florian.schitter (at) student.tugraz.at>
 */
public class ZoneRequirement extends Requirement {
    
    RectangularBox target_zone;
    ArrayList<PhysicalObject> objects;
    int time;
    
    public ZoneRequirement() {
        super();
        
        target_zone = new RectangularBox();
        objects = new ArrayList<PhysicalObject>();
        time = 70;
    }
    
    public void addObject(PhysicalObject object) {
        objects.add(object);
    }
    
    public void setTargetZone(double width, double length, double height, Vector3d pos) {
        this.target_zone.setPosition(pos);
        this.target_zone.setWidth(width);
        this.target_zone.setLength(length);
        this.target_zone.setHeight(height);
    }
    
    public void setTimeInTicks(int ticks) {
        this.time = ticks;
    }
    
    @Override
    public boolean isFullFilled() {
        this.fulfilled = true;
        
        for (PhysicalObject obj: objects) {
            if(!this.target_zone.contains(obj.getPosition()))
                this.fulfilled = false;
        }
        
        return this.fulfilled;
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
