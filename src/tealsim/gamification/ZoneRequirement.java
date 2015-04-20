/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.util.ArrayList;
import java.util.Collection;
import javax.vecmath.Vector3d;
import teal.physics.physical.PhysicalObject;
import teal.physics.physical.RectangularBox;
import teal.physics.physical.Wall;

/**
 *
 * @author Florian Schitter <florian.schitter (at) student.tugraz.at>
 */
public class ZoneRequirement extends Requirement {
    
    protected RectangularBox target_zone;
    protected ArrayList<PhysicalObject> objects;
    protected int time;
    
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
        this.target_zone.setOrientation(new Vector3d(0., 1., 0.));
        this.target_zone.setNormal(new Vector3d(0., 0., 1.));
    }
    
    public void setTimeInTicks(int ticks) {
        this.time = ticks;
    }
    
    public Collection<Wall> getTargetZoneWalls() {
        
        Collection<Wall> walls = this.target_zone.getWalls();
        for(Wall wall: walls) {
            wall.setColliding(false);
        }
        
        return walls;
    }
    
    @Override
    public boolean isFullFilled() {
        boolean contains_all = true;
        
        for (PhysicalObject obj: objects) {
            if(!this.target_zone.contains(obj.getPosition()))
                contains_all = false;
        }
        
        if(contains_all) {
            this.fulfilled = true;
        } else {
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
