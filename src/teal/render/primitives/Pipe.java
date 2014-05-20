package teal.render.primitives;

import teal.render.Rendered;
import teal.render.scene.SceneFactory;
import teal.sim.properties.HasLength;


public class Pipe extends Torus implements HasLength{
	
	private static final long serialVersionUID = 1L;
	protected double length = 1.0;
	
	public Pipe() {
		super();
		nodeType = NodeType.PIPE;
	}
	
	public Pipe(double radius, double thickness, double length) {
		super(radius, thickness);
		this.length = length;
		nodeType = NodeType.PIPE;

	}
	
	public double getLength(){
		return length;
		
	}
	
	public void setLength(double len){
		length = len;
	}
	
	
}
