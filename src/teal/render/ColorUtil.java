package teal.render;

import java.awt.Color;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;

import com.jme.renderer.ColorRGBA;

public class ColorUtil {
	public static Color3f getColor3f(Color4f col) {
		if(col == null)	return null;
		return new Color3f(col.x, col.y, col.z);
	}

	public static Color4f getColor4f(Color3f col) {
		if(col == null)	return null;
		float [] color = new float [4];	
		col.get(color);
		color[3] = 1.0f;
		return new Color4f(color);
	}
	
	public static Color4f getColor4f(ColorRGBA col) {
		return new Color4f(col.r, col.g, col.b, col.a);
	}

	/**
	 * 
	 * 
	 * @param col
	 * @param transparency transparency value with range [0-1], 0 means opaque
	 * @return
	 */
	public static Color4f getColor4f(Color3f col, float transparency) {
		if(col == null)	return null;
		float [] color = new float [4];	
		col.get(color);
		color[3] = 1.0f-transparency;
		return new Color4f(color);
	}
	
	public static ColorRGBA getColorRGBA(Color4f col) {
		if(col == null)
			return null;
		return new ColorRGBA(col.x, col.y, col.z, col.w);
	}

	/**
	 * Returns a material with diffuse and specular color set to
	 * {@code c} and ambient RGB color set to {@code c*0.9}
	 * 
	 * @param c color
	 * @return material representing {@code c}
	 */
	public static TMaterial getMaterial(Color c) {
		if(c == null)
			return null;
		return getMaterial(new Color4f(c));
	}
	
	public static TMaterial getMaterial(Color4f c) {
		if(c == null)
			return null;
		Color4f ambient = new Color4f(c);
		ambient.scale(0.9f);
		ambient.w = c.w;
		TealMaterial mat = new TealMaterial();
		mat.setAmbient(ambient);
		mat.setDiffuse(c);
		mat.setSpecular(c);
		return mat;		
	}

	public static TMaterial getMaterial(Color3f c) {
		return getMaterial(getColor4f(c));
	}
}
