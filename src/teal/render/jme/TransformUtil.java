package teal.render.jme;

import javax.media.j3d.Transform3D;
import javax.vecmath.Quat4f;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

public class TransformUtil {
    public static Quaternion getRotationFromTransform3D(Transform3D t) {
		Quat4f j3dRotQuat = new Quat4f();
		t.get(j3dRotQuat);
		return new Quaternion(j3dRotQuat.x, j3dRotQuat.y, j3dRotQuat.z, j3dRotQuat.w);
    }

    public static Vector3f getTranslationFromTransform3D(Transform3D t) {
    	javax.vecmath.Vector3f j3dTransVec = new javax.vecmath.Vector3f();
    	t.get(j3dTransVec);		
    	return new Vector3f(j3dTransVec.x,j3dTransVec.y,j3dTransVec.z);
    }

    public static Vector3f getScaleFromTransform3D(Transform3D t) {
    	javax.vecmath.Vector3d j3dScaleVec = new javax.vecmath.Vector3d();
    	t.getScale(j3dScaleVec);
    	return new Vector3f((float) j3dScaleVec.x, (float) j3dScaleVec.y, (float) j3dScaleVec.z);
    }
    
    
    public static Transform3D getTransform3D(Spatial element) {
    	Transform3D j3dTransform = new Transform3D();
    	Vector3f position = element.getLocalTranslation();
    	Quaternion rotation = element.getLocalRotation();
    	Vector3f scale = element.getLocalScale();
    	j3dTransform.setTranslation(new javax.vecmath.Vector3f(position.x,position.y,position.z));
    	j3dTransform.setRotation(new Quat4f(rotation.x, rotation.y, rotation.z,rotation.w));
    	j3dTransform.setScale(new javax.vecmath.Vector3d(scale.x,scale.y,scale.z));    	

    	return j3dTransform;
    }
}
