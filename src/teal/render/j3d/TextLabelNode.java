/*
 * Created on Oct 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package teal.render.j3d;

import java.awt.Color;
import java.awt.Font;

import javax.media.j3d.Appearance;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.OrientedShape3D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

/**
 * Node based on Java3d ObjectOrientedShape3D() for generating camera-aligned 3D text.  Note that there seem to be some issues
 * with the OrientedShape3D not being threadsafe.
 */
public class TextLabelNode extends Node3D {

	Text3D text;
	OrientedShape3D shape;
	float transparency = 0.5f;
	float shininess = 0.1f;
	Color3f color = new Color3f(new Color(255,255,255));
	
	
	public TextLabelNode() {
		text = new Text3D(new Font3D(new Font("Dialog", Font.BOLD, 1),new FontExtrusion()), "This is a Text3D!!");
		shape = new OrientedShape3D();
		initShape(shape);
		shape.addGeometry(text);
		Appearance app = Node3D.makeAppearance(new Color3f(new Color(255,255,255)),.5f,1.f,false);
		TransparencyAttributes ta = new TransparencyAttributes(TransparencyAttributes.NICEST,0.2f); //app.getTransparencyAttributes();
		app.setTransparencyAttributes(ta);
		shape.setAppearance(app);
		
		shape.setAlignmentMode(OrientedShape3D.ROTATE_ABOUT_POINT);
		//shape.setAlignmentAxis(0.f,1.f,0.f);
		
		mContents.addChild(shape);
		
	}
	
	/**
	 * Sets the text of the TextLabelNode.
	 * 
	 * @param newText
	 */
	public void setText(String newText) {
		text.setString(newText);
	}
	
	/**
	 * Not currently implemented.
	 * @param newFont
	 */
	public void setFont(Font newFont) {
//		FontExtrusion extrusion = new FontExtrusion();
//		text.getFont3D().getFontExtrusion(extrusion);
//		Font3D font3d = new Font3D(newFont, extrusion);
//		text.setFont3D(font3d);
	}
	
	/**
	 * Sets the color of the TextLabelNode.
	 * 
	 * @param c
	 */
	public void setColor(Color3f c) {
		color = c;
		Appearance app = Node3D.makeAppearance(c,transparency,shininess,false);
		shape.setAppearance(app);
	}
	
	/**
	 * Sets standard capability bits on passed Shape3D.
	 * @param shape
	 */
	public static void initShape(Shape3D shape){
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		
	}
}
