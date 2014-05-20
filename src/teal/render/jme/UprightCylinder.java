package teal.render.jme;

import java.nio.FloatBuffer;

import com.jme.scene.shape.Cylinder;

public class UprightCylinder extends Cylinder {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6114867886226215646L;

	public UprightCylinder() {
		super();
	}

	public UprightCylinder(String name, int axisSamples, int radialSamples,
			float radius, float height) {
		super(name, axisSamples, radialSamples, radius, height);
	}

	public UprightCylinder(String name, int axisSamples, int radialSamples,
			float radius, float height, boolean closed) {
		super(name, axisSamples, radialSamples, radius, height, closed);
	}

	public UprightCylinder(String name, int axisSamples, int radialSamples,
			float radius, float height, boolean closed, boolean inverted) {
		super(name, axisSamples, radialSamples, radius, height,
				closed, inverted);	    	
	}

	public UprightCylinder(String name, int axisSamples, int radialSamples,
			float radius, float radius2, float height, boolean closed, boolean inverted) {
		super(name, axisSamples, radialSamples,
				radius, radius2, height, closed, inverted);
	}
	    
	/**
	 * This method overrides the superclass' method. The y and z coordinates
	 * are swapped. That way the cylinder is drawn upright.
	 */
	// TODO: implement that properly with rather generate the geometry than
	//       calling the superclass' method and swapping afterwards
	@Override
	public void updateGeometry(int axisSamples, int radialSamples,
			float radius, float radius2, float height, boolean closed, boolean inverted) {
		super.updateGeometry(axisSamples, radialSamples, radius, radius2, height, closed, inverted);
		//swapping y and z coordinates
		//vertex buffer
		getVertexBuffer().rewind();
		while(getVertexBuffer().remaining() > 0) { //swapping y and z
			getVertexBuffer().get();
			getVertexBuffer().mark();
			float y = getVertexBuffer().get();
			float z = getVertexBuffer().get();
			getVertexBuffer().reset();
			getVertexBuffer().put(-z).put(-y);			
		}
			
/*			//normals buffer
			this.getNormalBuffer().rewind();
			while(getNormalBuffer().remaining() > 0) { //swapping y and z
				getNormalBuffer().get();
				getNormalBuffer().mark();
				float y = getNormalBuffer().get();
				float z = getNormalBuffer().get();
				getNormalBuffer().reset();
				getNormalBuffer().put(z).put(y);			
			}
*/
	}

}
