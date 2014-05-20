package teal.render.j3d;

import java.awt.GraphicsConfiguration;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsContext3D;

import teal.render.scene.TNode3D;


/**
 * Test class for some hacked immediate-mode rendering.  Ignore.
 * 
 * @author danziger
 *
 */
public class TEALCanvas3D extends javax.media.j3d.Canvas3D {
	private static final long serialVersionUID = 1L;
	ArrayList<ShapeNode3D> drawnObjs = new ArrayList<ShapeNode3D>();
	GraphicsContext3D graphc;
	public TEALCanvas3D(GraphicsConfiguration gcfg) {
		super(gcfg);
		graphc = this.getGraphicsContext3D();
		graphc.addLight(new DirectionalLight());
	}
	
	public void addDrawnShape(ShapeNode3D drawn) {
		drawnObjs.add(drawn);
	}
	
	public void preRender() {
		Iterator it = drawnObjs.iterator();
		while (it.hasNext()) {
			ShapeNode3D s = ((ShapeNode3D)it.next());
			graphc.setAppearance(s.getAppearance());
			graphc.setModelTransform(((TNode3D)s).getTransform());
			graphc.draw(s.getShape3D());
		}
	}
}
