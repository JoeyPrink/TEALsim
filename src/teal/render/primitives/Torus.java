package teal.render.primitives;

//import teal.render.Rendered;
//import teal.render.scene.SceneFactory;
import teal.sim.properties.HasThickness;


public class Torus extends Sphere implements HasThickness{
	
	private static final long serialVersionUID = 1L;

	protected double thickness = 0.1;
	
	public Torus() {
		super();
		nodeType = NodeType.TORUS;
	}
	
	public Torus(double radius, double thickness) {
		this();
		this.radius = radius;
		this.thickness = thickness;

	}
	
	public double getThickness(){
		return thickness;
	}
	
	public void setThickness(double thickness){
		this.thickness = thickness;
	}
	
	
	
	
}
