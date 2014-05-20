/*
 * Created on Oct 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package teal.render.jme;

import java.awt.Color;
import java.awt.Font;

import javax.vecmath.Color3f;

import teal.render.ColorUtil;
import teal.render.TMaterial;

import com.jme.scene.BillboardNode;
import com.jme.scene.Node;
import com.jme.scene.Text;

/**
 * Node based on Java3d ObjectOrientedShape3D() for generating camera-aligned 3D text.  Note that there seem to be some issues
 * with the OrientedShape3D not being threadsafe.
 */
public class TextLabelNode extends Node3D {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4227515381083510714L;

	private static final String TEXT_LABEL_NAME = "text";
	private static final String BILLBOARD_NODE_NAME = "alignedNode";
	
//	Text3D text;
//	OrientedShape3D shape;
	float transparency = 0.5f;
	float shininess = 0.1f;
	Color3f color = new Color3f(new Color(255,255,255));
	
	
	public TextLabelNode() {		
		Text text = Text.createDefaultTextLabel(TEXT_LABEL_NAME, "This is a Text3D!!");		
		BillboardNode aligned = new BillboardNode(BILLBOARD_NODE_NAME);
		aligned.attachChild(text);
		this.attachChild(aligned);
		
		TMaterial mat = ColorUtil.getMaterial(color);
		mat.setTransparancy(0.5f);
		mat.setShininess(0.1f);
		Node3D.setMaterial(mat, this);		
		
	}
	
	/**
	 * Sets the text of the TextLabelNode.
	 * 
	 * @param newText
	 */
	public void setText(String newText) {
		Text text = (Text) ((Node)this.getChild(BILLBOARD_NODE_NAME)).getChild(TEXT_LABEL_NAME);
		text.print(newText);
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
		TMaterial mat = this.getMaterial();
		mat.setDiffuse(c);
		mat.setSpecular(c);
		Color3f ambient = new Color3f(c);
		ambient.scale(0.9f);
		mat.setAmbient(ambient);
		setMaterial(mat,this);
	}

}
