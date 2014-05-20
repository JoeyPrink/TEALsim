package teal.render.j3d;

import java.net.URL;

import javax.vecmath.Vector3d;

import teal.physics.em.RingOfCurrent;
import teal.render.j3d.geometry.Cylinder;
import teal.render.j3d.geometry.Pipe;
import teal.render.j3d.geometry.Torus;
import teal.render.j3d.loaders.Loader3DS;
import  teal.render.scene.*;
import teal.render.viewer.AbstractViewer3D;

import teal.render.TAbstractRendered;
import teal.render.TRendered;
import teal.render.TAbstractRendered.NodeType;
import teal.render.TealMaterial;
import teal.sim.properties.HasLength;
import teal.sim.properties.HasRadius;
import teal.sim.properties.HasThickness;
import teal.sim.spatial.FieldLine;
import teal.util.URLGenerator;

public class SceneFactoryJ3D implements TSceneFactory{
	
	public TNode3D makeNode(TRendered element)
	{
		double r = 1.0;
		double tr = 0.01;
		double len = 0.01;
		NodeType type = element.getNodeType();
		ShapeNode node = null;
		if(element.getModel() != null){
			Model model = element.getModel();
			TNode3D node1 = new Loader3DS().getTNode3D(URLGenerator
					.getResource(model.getPath()));
			if(model.getOffset() != null)
				node1.setModelOffsetPosition(model.getOffset());
			if(model.getScale() != null)
				node1.setScale(model.getScale());
			node1.setElement(element);
			return node1;
		}
		else{
		boolean setMaterial = true;
		switch (type) {
		case NONE:
			setMaterial = false;
			break;
		case ARRAY:

			ArrayNode nArray = new ArrayNode(element);
			return nArray;

		case ARROW:
			ArrowNode nArrow = new ArrowNode(element);
			return nArrow;
		case ARROW_SOLID:
			node = new SolidArrowNode();
			setMaterial = false;
			break;
		case BOX:
			BoxNode nBox= new BoxNode();
			nBox.setElement(element);
			break;
		case CYLINDER:
			node = new ShapeNode();
			if(element instanceof HasRadius){
				r = ((HasRadius)element).getRadius();
			}
			if(element instanceof HasLength){
				len = ((HasLength)element).getLength();
			}
			node.setGeometry(Cylinder.makeGeometry(20,r,len));
			node.setElement(element);
			node.setVisible(true);
			break;
		case DIPOLE_ELEC:
			ElectricDipoleNode3D nED = new ElectricDipoleNode3D(element);
			setMaterial = false;
			return nED;
		case DIPOLE_MAG:
			MagDipoleNode3D nMD = new MagDipoleNode3D(element);
			setMaterial = false;
			return nMD;

		case FIELD_LINE:
			return new FieldLineNode(element);
			//break;
//		case FIELD_LINE:
//			Vector3d symAxis = ((FieldLine) element).getSymmetryAxis();
//			int symCount = ((FieldLine) element).getSymmetryCount();
//			FieldLineNode fLine = new FieldLineNode(element,symCount,symAxis);
//			return fLine;
		case GRID:
			break;
		case HELIX:
			node = new HelixNode(element);
			break;
		case IMAGE:
			setMaterial = false;
			node = new ImageNode();
			break;
		case INCLINED_PLANE:
			InclinedPlaneNode nIP = new InclinedPlaneNode();
			nIP.setFillAppearance(Node3D.makeAppearance(element.getMaterial()));
			setMaterial = false;
			break;
		case LINE:
			node = new LineNode(element);
			setMaterial = false;
			break;
		case LINE_ARRAY:
			break;
		case MULTI_SHAPE:
			break;
		case POINT:
			break;
		case PIPE:
			node = new ShapeNode();
			node.setElement(element);
			if(element instanceof HasRadius){
				r = ((HasRadius)element).getRadius();
			}
			if(element instanceof HasThickness){
				tr = ((HasThickness)element).getThickness();
			}
			if(element instanceof HasLength){
				len = ((HasLength)element).getLength();
			}
			((ShapeNode)node).setGeometry(Pipe.makeGeometry(48, r,tr,len));
			break;
		case SPHERE:
			node = new SphereNode();
			node.setElement(element);
			if(element instanceof HasRadius){
				((SphereNode)node).setScale(((HasRadius)element).getRadius());
			}
			break;
		case STEM:
			break;
		case TEXT:
			TextLabelNode nTXT = new TextLabelNode();
			break;
		case TORUS:
			node = new ShapeNode();
			node.setElement(element);
			if(element instanceof HasRadius){
				r = ((HasRadius)element).getRadius();
			}
			if(element instanceof HasThickness){
				tr = ((HasThickness)element).getThickness();
			}
			node.setGeometry(Torus.makeGeometry(r,tr));
			break;
		case WALL:
			WallNode nWall = new WallNode();
			return nWall;
		}
		if(setMaterial){
			node.setAppearance(Node3D.makeAppearance(element.getMaterial()));
		}
		return node;
		}
	}
	public TNode3D makeNode(TRendered element, NodeType type){
		return null;
	}
	public TNode3D makeNode(NodeType type){
		TNode3D node = null;
		switch (type) {
		case ARROW_SOLID:
			node = new SolidArrowNode();
			break;			
		}
		
		/*
		switch (type) {
		case NONE:
			break;
		case ARRAY:
			break;
		case ARROW:
			node = new ArrowNode();
			break;
		case ARROW_SOLID:
			break;
		case BOX:
			node = new BoxNode();
			break;
		case CYLINDER:
			node = new ShapeNode();
			break;
		case DIPOLE_ELEC:
			node = new ElectricDipoleNode3D();
			break;
		case DIPOLE_MAG:
			node = new MagDipoleNode3D();
			break;
		case GRID:
			break;
		case HELIX:
			node = new HelixNode();
			break;
		case IMAGE:
			node = new ImageNode();
			break;
		case INCLINED_PLANE:
			node = new InclinedPlaneNode();
			break;
		case LINE:
			break;
		case LINE_ARRAY:
			break;
		case MULTI_SHAPE:
			break;
		case POINT:
			break;
		case PIPE:
			node = (TShapeNode) new ShapeNode();
			node.setElement(this);
			node.setGeometry(Pipe.makeGeometry(20, radius, torusRadius, torusRadius));
			node.setAppearance(Node3D.makeAppearance(mColor,0.7f,0.f,false));
			node.setColor(teal.render.TealMaterial.getColor3f(mMaterial.getDiffuse()));
			node.setShininess(mMaterial.getShininess());
			break;
		case SPHERE:
			node = new SphereNode();
			break;
		case STEM:
			break;
		case TEXT:
			node = new TextLabelNode();
			break;
		case TORUS:
			break;
		case WALL:
			node = new WallNode();
			break;

		}
		*/
		return node;
	}
	public void refreshNode(TRendered element,int renderFlags){
	}
	
	public TNode3D loadModel(URL path){
		return null;
	}
	
	public TNode3D load3DS(String path){
		TNode3D node = new Loader3DS().getTNode3D(URLGenerator
				.getResource(path));
		return node;
	}
	
	public TNode3D load3DS(String path, String texturePath){
		TNode3D node = new Loader3DS().getTNode3D(path,texturePath);
		return node;
	}
	
	
	public AbstractViewer3D makeViewer() {
		return new ViewerJ3D();
	}
	

}
