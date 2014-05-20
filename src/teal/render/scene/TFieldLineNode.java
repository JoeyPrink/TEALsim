package teal.render.scene;

import javax.vecmath.Vector3d;

import teal.render.HasColor;

public interface TFieldLineNode extends HasColor {
	  public void setPickRadius(double r);
	  public double getPickRadius();
	  public void setPickVisible(boolean state);
	  public boolean isPickVisible();
	  public void setMarkerVisible(int idx, boolean state);
	  public void setMarkerValues(int idx, Vector3d pos, Vector3d direction);
	  public void setLineGeometry(int len1, float[] line1, int len2, float[] line2  );
	  public void setLineGeometry(int len1, float[] line1, float[] colors1,int len2, float[] line2,float[] colors2  );
	  public void checkMarkers(int num);
	  public void setSymmetry(int count, Vector3d axis);
}
