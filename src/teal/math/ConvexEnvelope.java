package teal.math;
import java.util.Vector;

import javax.vecmath.Vector3d;

public class ConvexEnvelope {
	
	public static Vector get(Vector points){
		if (points.size()<3){
			return points;
		}
		else {
			Vector retour=new Vector();
			retour.add(new Vector3d((Vector3d)points.get(0)));
			if (det(points.get(0),points.get(1),points.get(2))>0){
				
				retour.add(new Vector3d((Vector3d)points.get(1)));
				retour.add(new Vector3d((Vector3d)points.get(2)));
			}
			else {
				retour.add(new Vector3d((Vector3d)points.get(2)));
				retour.add(new Vector3d((Vector3d)points.get(1)));
			}
			Vector3d temp=new Vector3d((Vector3d)retour.get(0));
			retour.add(temp);
			for(int i=3;i<(points.size()-1);i++){
				int firstNeg=-1;
				int firstPos=-1;
				for(int j=0;j<(retour.size()-1);j++){
					if (det(retour.get(j),retour.get(j+1),points.get(i))<0){
						if (firstNeg==-1){
							firstNeg=j+1;
						}
					}
					if (det(points.get(i),retour.get(j),retour.get(j+1))>=0){
						if ((firstNeg!=-1)&&(firstPos==-1)){
							firstPos=j;
						}
					}
				}
				if (firstPos==-1){
					firstPos=(retour.size()-1);
				}
				if (firstNeg!=-1){
					Vector newRetour=new Vector();
					for(int k=0;k<retour.size();k++){
						if (k<firstNeg){
							newRetour.add(retour.get(k));
						}
						if (k==firstNeg){
							newRetour.add(new Vector3d((Vector3d)points.get(i)));
						}
						if ((k>=firstPos)&&(firstPos!=-1)){
							newRetour.add(retour.get(k));
						}
					}
					retour=newRetour;
				}
			}
			return retour;
		}
	}

	protected static double det(Object x0,Object x1,Object x2){
		Vector3d i=new Vector3d();
		Vector3d j=new Vector3d();
		i.sub((Vector3d)x1,(Vector3d)x0);
		j.sub((Vector3d)x2,(Vector3d)x0);
		return i.x*j.y-i.y*j.x;
	}
}


