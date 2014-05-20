package teal.render.jme;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.jdesktop.mtgame.RenderUpdater;

import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Sphere;
import com.jme.scene.shape.Torus;
import com.jme.scene.shape.Tube;
import com.jme.scene.state.MaterialState.MaterialFace;
import com.jme.system.DisplaySystem;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.MaxToJme;

import teal.render.TAbstractRendered;
import teal.render.TMaterial;
import teal.render.TAbstractRendered.NodeType;
import teal.render.TRendered;
import teal.render.scene.Model;
import teal.render.scene.TNode3D;
import teal.render.scene.TSceneFactory;
import teal.render.viewer.AbstractViewer3D;
import teal.sim.properties.HasLength;
import teal.sim.properties.HasRadius;
import teal.sim.properties.HasThickness;
import teal.util.TDebug;
import teal.util.URLGenerator;

public class SceneFactoryJME implements TSceneFactory{

	public TNode3D makeNode(TRendered element)
	{
		float r = 1.0f;
		float tr = 0.01f;
		float len = 0.01f;
		NodeType type = element.getNodeType();
		TNode3D node = null;
		if(element.getModel() != null){
			Model model = element.getModel();
			TNode3D node1 = load3DS(model.getPath());

//			loadModel(node, URLGenerator.getResource(model.getPath()));
			((Node3D)node1).doSetElement(element, false);
//			node1.setElement(element);
			
			if(model.getOffset() != null)
				node1.setModelOffsetPosition(model.getOffset());
			if(model.getScale() != null)
				node1.setScale(model.getScale());

//			node1.setElement(element);
			return node1;
		}

		boolean setMaterial = true;

		switch (type) {
		case NONE:
			setMaterial = false;
			break;
		case ARRAY:
			return new ArrayNode(element);
		case ARROW:
			ArrowNode nArrow = new ArrowNode(element);
			return nArrow;
		case ARROW_SOLID:
			node = new SolidArrowNode();
			break;
		case BOX:
			BoxNode nBox= new BoxNode();
			nBox.doSetElement(element,false);
			return nBox;
		case CYLINDER:
			node = new ShapeNode();
			if(element instanceof HasRadius){
				r = (float)((HasRadius)element).getRadius();
			}
			if(element instanceof HasLength){
				len = (float)((HasLength)element).getLength();
			}
//			node.setGeometry(Cylinder.makeGeometry(20,r,len));
			Cylinder cyl = new UprightCylinder("Cylinder",4,20,r,len);
			((Node3D) node).attachChild(cyl); 
			((Node3D) node).doSetElement(element,false);
			break;
		case DIPOLE_ELEC:
			ElectricDipoleNode3D nED = new ElectricDipoleNode3D();
			nED.doSetElement(element,false);
			return nED;
		case DIPOLE_MAG:
			MagDipoleNode3D nMD = new MagDipoleNode3D(element);
			return nMD;
		case FIELD_LINE:
			return new FieldLineNode(element);
		case GRID:
			break;
		case HELIX:
			node = new HelixNode(element);
			break;
		case IMAGE:
			node = new ImageNode();
			setMaterial = false;
			break;
		case INCLINED_PLANE:
			InclinedPlaneNode nIP = new InclinedPlaneNode();
//			nIP.setFillMaterial(element.getMaterial());
			nIP.doSetElement(element,false);
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
			((Node3D) node).doSetElement(element,false);
			
			if(element instanceof HasRadius){
				r = (float)((HasRadius)element).getRadius();
			}
			if(element instanceof HasThickness){
				tr = (float)((HasThickness)element).getThickness();
			}
			if(element instanceof HasLength){
				len = (float)((HasLength)element).getLength();
			}

			Tube geometry = new Tube("pipe",r+(tr/2.0f), r-(tr/2.0f),len);
			Node3D.setFaceMode((Node3D)node, MaterialFace.FrontAndBack);
			((ShapeNode)node).attachChild(geometry);
			break;
		case SPHERE:
			node = new ShapeNode();
			((Node3D) node).doSetElement(element,false);
			((ShapeNode)node).attachChild(new Sphere("Sphere", 15, 15, 1.0f));
			if(element instanceof HasRadius){
//				((ShapeNode)node).setScale(((HasRadius)element).getRadius());
				((Spatial)node).setLocalScale((float)((HasRadius)element).getRadius());
			}
//			((ShapeNode)node).setModelBound(new BoundingSphere());
//			((ShapeNode)node).updateModelBound();
			break;
		case STEM:
			break;
		case TEXT:
			TextLabelNode nTXT = new TextLabelNode();
			break;
		case TORUS:
			node = new ShapeNode();
			((Node3D) node).doSetElement(element,false);
			if(element instanceof HasRadius){
				r = (float)((HasRadius)element).getRadius();
			}
			if(element instanceof HasThickness){
				tr = (float)((HasThickness)element).getThickness();
			}
//			node.setGeometry(Torus.makeGeometry(r,tr));
			((ShapeNode)node).attachChild(new Torus("torus",24,24,r-tr, r+tr)); //FIXXME: tr/2??
			
//			node.setAppearance(Node3D.makeAppearance(element.getMaterial()));
			
			break;
		case WALL:
			WallNode nWall = new WallNode();
			return nWall;
		}
		
		if(setMaterial){
			node.setMaterial(element.getMaterial());
		}
		return node;
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

		return node;
	}
	
	public void refreshNode(TRendered element,int renderFlags){
	}
	
	public TNode3D loadModel(URL path){
		return null;
	}

	
	public AbstractViewer3D makeViewer() {
		return new ViewerJME();
	}
	
	public TNode3D load3DS(String path) {
		Node3D node = new Node3D();
		URL modelUrl = getURL(path);
		if(modelUrl == null) {
			TDebug.println(1,"cannot generate URL from path: " + path);
			return node;
		}
		try {
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator(modelUrl));
		} catch (URISyntaxException e) {
			TDebug.println(1,"cannot resolve URL " + modelUrl.toString());
			return node;
		}
		loadModel(node, modelUrl);
		return node;
	}
	
	public TNode3D load3DS(String path, String mapPath) {
		Node3D node = new Node3D();
		URL modelUrl = getURL(path);
		URL mapUrl = getURL(mapPath); //TODO: check if this works
		if(modelUrl == null || mapUrl == null) {
			TDebug.println(1,"cannot generate URL from path: " + path);
			return node;
		}
		try {
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator(mapUrl));
		} catch (URISyntaxException e) {
			TDebug.println(2,"cannot resolve URL " + mapUrl.toString());
			return node;
		}
		
		loadModel(node, modelUrl);
		return node;
	}
	
	
	protected static void loadModel(TNode3D node, final URL modelUrl) {
		
		Node offsetNode = new Node();
		((Node)node).attachChild(offsetNode);
		
		RenderUpdater ru = new RenderUpdater() {
			public void update(Object arg0) {
				Node node1 = (Node)arg0;
				
				ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
				
				try {
					InputStream in = modelUrl.openStream();
					FormatConverter converter = new MaxToJme();	
//					converter.setProperty("mtllib", modelUrl);
					converter.convert(in, byteOutput);
					final byte[] out = byteOutput.toByteArray();
					Node nd = (Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(out));
					nd.setLocalRotation(new Quaternion().fromAngleAxis(3*FastMath.PI/2.0f, new Vector3f(1,0,0)));
					node1.attachChild(nd);
				} catch (IOException e) {
					return;
				}
			
			}
		};
		if(DisplaySystem.getDisplaySystem().getRenderer() == null) 
			TealWorldManager.getWorldManager().addRenderUpdater(ru, offsetNode);
		else
			ru.update(offsetNode);
	}
	
	protected URL getURL(String url){
		return URLGenerator.getResource(url);
	}


}
