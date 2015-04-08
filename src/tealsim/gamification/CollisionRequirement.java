/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tealsim.gamification;

import java.util.ArrayList;
import teal.sim.collision.HasCollisionController;

/**
 *
 * @author Florian Schitter <florian.schitter (at) student.tugraz.at>Georg
 */

public class CollisionRequirement extends Requirement {
    
    HasCollisionController object1;
    HasCollisionController object2;
    ArrayList <HasCollisionController> objects;
    
    public CollisionRequirement (HasCollisionController object1, HasCollisionController object2) {
        super();
        this.object1 = object1;
        this.object2 = object2;
        this.objects = null;
        
    }
    
    public CollisionRequirement (HasCollisionController object1, ArrayList<HasCollisionController> objects) {
        super();
        this.object1 = object1;
        this.object2 = null;
        this.objects = objects;
        
    }
    
    @Override
    public boolean isFullFilled() {
        
        if(objects != null) {
            boolean all_colliding = true;
            for(int i = 0; i < objects.size(); i++) {
                if(!objects.get(i).isColliding()) {
                    all_colliding = false;
                }
            }
            
            if(all_colliding == true) {
                fulfilled = true;
            }
        } else {
            if(object1.isAdheredTo(object2)) {
                fulfilled = true;
            }
        }
        return fulfilled;
    }
    
    @Override
    public void setRequirementEnabled(boolean b) {
        this.enabled = b;
    }
}
