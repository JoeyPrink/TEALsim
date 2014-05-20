package teal.render.scene;

import java.net.URL;

import teal.render.TAbstractRendered;
import teal.render.TRendered;
import teal.render.TAbstractRendered.NodeType;
import teal.render.viewer.AbstractViewer3D;

public interface TSceneFactory {
	
	public TNode3D makeNode(TRendered element);
	public TNode3D makeNode(TRendered element, NodeType type);
	public TNode3D makeNode(NodeType type);
	public void refreshNode(TRendered element,int renderFlags);
	public TNode3D loadModel(URL path);
	public TNode3D load3DS(String path);
	public TNode3D load3DS(String path, String mapPath);
	public AbstractViewer3D makeViewer();
	

}
