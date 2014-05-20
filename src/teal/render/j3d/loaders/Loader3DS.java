/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Loader3DS.java,v 1.21 2010/06/07 22:00:31 pbailey Exp $ 
 * 
 */

package teal.render.j3d.loaders;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import teal.render.j3d.*;
import teal.render.scene.*;
import teal.util.*;

import com.sun.j3d.loaders.*;

/**
 * 3DS format model loader for Java3D. Based on a Loader by Rycharde Hawkes,
 * Copyright 1998 Hewlett-Packard Company.
 * 
 * This program is significantly different from the original code.
 * 
 * @author Philip Bailey
 * @author Rycharde Hawkes
 * 
 * @version $Revision: 1.21 $
 */

public class Loader3DS  implements teal.render.scene.TLoader
{
	static final int MAX_SURFACES = 33;

	int debugLevel = 0;
	int flags = Loader.LOAD_ALL;

	boolean transformVerts = true;
	TDebug tdb;

	//URLClassLoader urlLoader = null;
	URL baseURL = null;
	String basePath = null;
	String texturePath = null;

	int totalPolygons = 0;
	int totalVertices = 0;

	/** Root of the scene graph for the file. */
	BranchGroup root = null;
	String rootName = "Root";

	/** The Scene */
	SceneBase mScene = null;
	Properties properties = null;

	TreeMap chunkTypes = null;

	/**
	 * Lookup table of instances of SharedObjects held in <objectTable>, indexed
	 * by name.
	 */
	Hashtable instanceTable = null;

	/**
	 * Lookup table of all SharedObjects created, indexed by name.
	 */
	Hashtable objectTable = null;

	/**
	 * A lookup table of Appearance objects representing the materials in the
	 * file, indexed by material name.
	 */
	Hashtable mats = null;

	/** the 3DS Appearance with material currently being constructed. */
	Appearance mAppearance= null;

	/** name of the 3DS material. */
	String matName = null;

	/** the Java3D Material. */
	Material material = null;

	/** 3DS shininess percentage. */
	float shininess = 0.0f;

	/**
	 * The following members are used to construct the current object, and might
	 * better be incorporated into a class.
	 */

	String objectName = null;
	/** The referenced SharedObject. */
	SharedGroup object = null;

	/** name of a referenced SharedObject. */
	String nodeName = null;
	/** name of an instance of a SharedObject. */
	String instanceName = null;

	/** positional component of the objects' transform. */
	Vector3f translation = null;
	/** rotational component of the objects' transform. */
	Matrix4f orientation = null;
	/** scalar component of the objects' transform. */
	Vector3f scale = null;

	/**
	 * The shape component of the object currently being constructed.
	 */
	Shape3D shape = null;

	/** current object's geometry. */
	TriangleArray geometry = null;
	/** number of vertices in the face list. */
	int numVertices = 0;
	/** vertex array. */
	Point3f[] vertices = null;
	/** number of faces in the object. */
	int numFaces = 0;
	/** list of all the faces in the object. */
	Face[] faces = null;
	/** list of vertices constructing each face. */
	Vector[] sharedFaces = null;
	/** list of surfaces making up geometry. */
	Surface[] surfaces = null;
	/** list of geometry's texture coordinates. */
	TexCoord2f[] textureCoords = null;

	/**
	 * Used to indicate whether surfaces were specified and created.
	 */
	boolean surfacesCreated = true;

	public Loader3DS() {
		tdb = new TDebug();
		mats = new Hashtable();
		objectTable = new Hashtable();
		instanceTable = new Hashtable();
		properties = new Properties();

		loadChunkTypes();
	}

	public void setLogDetail(int level) {
		debugLevel = level;
		tdb.setLevel(level);
		if ((level > 2) && (chunkTypes == null))
			loadChunkTypes();
	}
	public int getLogDetail() {
		return tdb.getLevel();
	}

	public int getFlags() {
		return flags;
	}
	public void setFlags(int flgs) {
		flags = flgs;
	}
	public URL getBaseUrl() {
		return baseURL;
	}
	public void setBaseUrl(URL path) {
		baseURL = path;
		basePath = extractBasePath(path);
	}
	public String getBasePath() {
		return basePath;
	}
	public void setBasePath(String path) {
		basePath = path;
	}

	public String getTexturePath() {
		return texturePath;
	}
	public void setTexturePath(String path) {
		texturePath = path;
	}

	protected String extractBasePath(URL url) {
		String path = url.toString();
		return extractBasePath(path);
	}

	protected String extractBasePath(String path) {

		String base = null;
		tdb.debugln(1, "Path: '" + path + "'");
		if ((path == null) || (path.length() == 0)) {
			base = new String("./");
		} else {
			String wrk = path.toLowerCase();
			if (wrk.startsWith("jar:", 0)) {
				int ix = path.lastIndexOf("!");
				if (ix != -1) {
					path = path.substring(ix + 2);
				}
			}
            else if (path.startsWith("file:", 0)) {
                    //path = path.substring(6);
            }
			if ((path == null) || (path.length() == 0)) {
				base = new String("./");
			} else {
				int idx = path.lastIndexOf("/");
				if (idx != -1) {
					base = path.substring(0, idx + 1);
				}
			}
		}
		tdb.debugln(1, "Base: '" + base + "'");
		return base;
	}
    
	@Deprecated
	public BranchGroup getBranchGroup(URL url) {
		com.sun.j3d.loaders.Scene scene = null;
		try {
			scene = load(url);
		} catch (IOException ioe) {
		}
		return scene.getSceneGroup();
	}

@Deprecated
    public BranchGroup getBranchGroup(String path) {

    	com.sun.j3d.loaders.Scene scene = null;

		try {
			scene = load(path);
		} catch (IOException ioe) {
            TDebug.printThrown(ioe);
		}
			return scene.getSceneGroup();
		
	}
@Deprecated
	public BranchGroup getBranchGroup(String path, String texPath) {

		com.sun.j3d.loaders.Scene scene = null;

		try {
			scene = load(path,texPath);
		} catch (IOException ioe) {
            TDebug.printThrown(ioe);
		}
			return scene.getSceneGroup();
		
	}


	public TNode3D getTNode3D(URL url)
	{
	    Node3D node = null;
	    BranchGroup bg = getBranchGroup(url);
	    if (bg != null)
	    {
	        node = new Node3D();
	        node.addContents(bg);
	    }
	    return node;
	}    

    public TNode3D getTNode3D(String path)
    {
        Node3D node = null;
	    BranchGroup bg = getBranchGroup(path);
	    if (bg != null)
	    {
	        node = new Node3D();
	        node.addContents(bg);
	    }
	    return node;
    }
   
    public TNode3D getTNode3D(String path, String texPath)
    {
        Node3D node = null;
	    BranchGroup bg = getBranchGroup(path,texPath);
	    if (bg != null)
	    {
	        node = new Node3D();
	        node.addContents(bg);
	    }
	    return node;
    }
    
	public com.sun.j3d.loaders.Scene load(URL url) throws IOException {
		basePath = extractBasePath(url);
		DataInputStream dataIn = new DataInputStream(url.openStream());
		return load(dataIn);
	}

	public com.sun.j3d.loaders.Scene load(String path) throws IOException {
        tdb.debugln(1, "Load3DS: loading '" + path + "'...");
        basePath = extractBasePath(path);
		URL url = URLGenerator.getResource(path);
		DataInputStream dataIn = new DataInputStream(url.openStream());
		return load(dataIn);
	}

	public com.sun.j3d.loaders.Scene load(String path, String texPath) throws IOException {
		texturePath = texPath;
		return load(path);

	}

	/**
	 * <p>
	 * Given a <i>DataInputStream </i>, load() will construct an appropriate
	 * Java3D scene. It also constructs a table of <i>objects </i>, indexed by
	 * their proper name and a table of <instances>of those objects which are
	 * refered to by their instance name. Currently, only two <properties>are
	 * returned, "polygons" and "vertices", the value of which are a
	 * java.lang.Integer giving the total number of polygons and vertices in the
	 * .3DS file respectively.
	 * </p>
	 * <p>
	 * Limitations of the current version:
	 * <li>Lights are not created.</li>
	 * <li>Interpretation of 3DS's shininess and shininess strength could be
	 * better.</li>
	 * <li>Only handles those texture map image formats supported by
	 * com.sun.j3d.utils.image.TextureLoader.</li>
	 * </p>
	 * <p>
	 * Java3Disms:
	 * <li>3DS wireframe material = PolygonAttributes.POLYGON_LINE</li>
	 * <li>3DS metal-shaded material = PolygonAttributes.POLYGON_POINT</li>
	 * <li>3DS constant-shaded material = PolygonAttributes.POLYGON_FILL</li>
	 * <li>3DS phong-shaded material = PolygonAttributes.POLYGON_PHONG</li>
	 * <li>Hidden objects don't seem to be set by 3DSMax. Load3DS simulates
	 * this by hiding objects whose names begin with '$'.</li>
	 * <li>Note that only 3DS planar mapping generates correct 2D texture
	 * coords.</li>
	 * <li>1 generic 3DSMax unit = 1 Java3D unit. This loader does not handle
	 * other 3DSMax units, e.g. metric.</li>
	 * </p>
	 * <p>
	 * Known bugs:
	 * <li>Normal calculation fails for certain surfaces.</li>
	 * 
	 * @param filename
	 *            .3DS file to load.
	 * @param objects
	 *            table of named objects indexed by object name. name-value
	 *            pairs are a string and a SharedGroup respectively.
	 * @param instances
	 *            table of instanced objects indexed by instance name.
	 *            name-value pairs are a string and a TransformGroup
	 *            respectively.
	 * @param properties
	 *            information about the .3DS file.
	 * @exception IOException
	 *                any failure to process .3DS file.
	 * @see com.sun.j3d.utils.image.TextureLoader
	 */

	public com.sun.j3d.loaders.Scene load(DataInputStream in) throws IOException {

		root = new BranchGroup(); // Root of scene graph for this file.
		root.setCapability(BranchGroup.ALLOW_DETACH);
		root.setCapability(Group.ALLOW_CHILDREN_READ);
		root.setCapability(Group.ALLOW_CHILDREN_WRITE);
		root.setCapability(Group.ALLOW_CHILDREN_READ);
		root.setCapability(Node.ALLOW_BOUNDS_READ);
		root.setBoundsAutoCompute(true);
		root.setUserData(rootName);
		mScene = new SceneBase();
		mScene.setSceneGroup(root);

		try {
			for (;;) {
				processChunk(in);
			}
		} catch (EOFException e) {
		}

		properties.put("polygons", Integer.toString(totalPolygons));
		properties.put("vertices", Integer.toString(totalVertices));
		tdb.debugln(1, "Load3DS totals: Vertices = " + totalVertices
				+ " \tPolygons = " + totalPolygons);
		return mScene;
	}

	void prepareForNewObject() {
		int dbLev = tdb.getLevel();
		if ((dbLev > 0) && (objectName != null)) {
			TDebug.print("Object: " + objectName + " Vertices: " + numVertices
					+ " Faces: " + numFaces);
			TDebug.println("");
		}
		objectName = null;
		object = null;
		shape = null;
		geometry = null;
		numVertices = 0;
		vertices = null;
		sharedFaces = null;
		numFaces = 0;
		faces = null;
		surfaces = null;
		surfacesCreated = false;
		textureCoords = null;
		mAppearance= null;
		matName = null;
		material = null;
		shininess = 0.0f;
		nodeName = null;
		instanceName = null;
		translation = null;
		orientation = null;
		scale = null;
	}

	/**
	 * Returns the root of the scene graph constructed from the given file.
	 * 
	 * @return Root of constructed scene graph.
	 */

	public BranchGroup getRoot() {
		if (mScene != null)
			return mScene.getSceneGroup();
		else
			return null;
	}

	/**
	 * A .3DS file consists of a series of chunks. Each chunk
	 * starts with a tag ID and the chunks' length followed by
	 * optional data. This method handles most of those chunks
	 * processed by this loader. Those chunks that are not
	 * recognised are ignored.
	 **/

	void processChunk(DataInputStream in) throws IOException {
		TDebug.indentP();
		int tag = readUnsignedShort(in);
		int length = readInt(in);
		if (debugLevel > 3) {

			TDebug.println(TDebug.indent() + "ChunkType = " + getChunkType(tag)
					+ "\tlen:" + length);
		}
		switch (tag) {
			case K3DS_M3DMAGIC : // 3DS file
			case K3DS_MDATA : // Editor chunks
			case K3DS_MAT_ENTRY : // 3DS material
			case K3DS_OBJECT_NODE_TAG : // Object instance
			case K3DS_N_TRI_OBJECT : // Object definition
				processChunk(in);
				break;

			case K3DS_KF_DATA : // Start of the instance tree
				TDebug.println(2, "start of Keyframe data:");
				if (surfacesCreated == false) {
					createUnsmoothedFaces();
					prepareForNewObject();
				}

				processChunk(in);
				break;

			case K3DS_NODE_HDR : // Details of an object instance
				processLink(in);
				break;

			case K3DS_NODE_ID : // Node ID - unused.
				processNodeID(in);
				break;

			case K3DS_POS_TRACK_TAG : // Contains object's initial position
				processPosTrackTag(in);
				break;

			case K3DS_ROT_TRACK_TAG : // Contains object's initial orientation
				processRotTrackTag(in);
				break;

			case K3DS_SCL_TRACK_TAG : // Contains object's initial scale
				processSclTrackTag(in);
				break;

			case K3DS_INSTANCE_NAME : // Name given to object instance
				instanceName = readName(in);
				break;

			case K3DS_AMBIENT_LIGHT : // Ambient light
				processAmbientLight(in);
				break;

			case K3DS_MASTER_SCALE : // Global scaling factor
				processMasterScale(in);
				break;

			case K3DS_MAT_NAME : // Start of a 3DS material
				matName = readName(in);
				mAppearance= new Appearance();
				mAppearance.setCapability(Appearance.ALLOW_MATERIAL_READ);
				mAppearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
				material = new Material();
				material.setCapability(Material.ALLOW_COMPONENT_READ);
				material.setLightingEnable(true);
				mAppearance.setUserData(matName);
				mAppearance.setMaterial(material);
				mats.put(matName, mAppearance);
				tdb.debugln(3, "Processing material '" + matName + "'");

				break;

			case K3DS_MAT_AMBIENT : // Ambient colour of material
				Color3f ambient = readColor(in);
				material.setAmbientColor(ambient);
				tdb.debugln(3, "== Ambient: " + ambient);

				break;

			case K3DS_MAT_DIFFUSE : // Diffuse colour of material
				Color3f diffuse = readColor(in);
				material.setDiffuseColor(diffuse);
				tdb.debugln(3, "== Diffuse: " + diffuse);

				break;

			case K3DS_MAT_SPECULAR : // Specular colour of material
				Color3f specular = readColor(in);
				material.setSpecularColor(specular);
				tdb.debugln(3, "== Specular: " + specular);
				break;

			case K3DS_MAT_SHININESS : // 3DS shininess percentage
				shininess = readPercentage(in);
				tdb.debugln(3, "== Shininess: " + shininess);
				break;

			case K3DS_MAT_SHININESS_STRENGTH : // 3DS shininess strength
				float shininessStrength = readPercentage(in);
				tdb.debugln(3, "== Shininess strength: " + shininessStrength);
				material.setShininess((1.0f - ((shininess + shininessStrength) / 2.0f)) * 128);
				break;

			case K3DS_MAT_TRANSPARENCY : // Transparency percentage
				float transparency = readPercentage(in);
				tdb.debugln(3, "== Transparency: " + transparency);

				if (transparency > 0.1f) {
					TransparencyAttributes ta = new TransparencyAttributes();

					ta.setTransparency(transparency);
					mAppearance.setTransparencyAttributes(ta);

					//
					// If we turn transparency on then we should
					// also turn back face culling off.
					//
					PolygonAttributes pa = mAppearance.getPolygonAttributes();
				

					if (pa == null) {
						pa = new PolygonAttributes();
					}
					pa.setCullFace(PolygonAttributes.CULL_NONE);
					mAppearance.setPolygonAttributes(pa);
				}
				break;

			case K3DS_MAT_SHADING : // Type of rendering
				int style;
				int mode = readUnsignedShort(in);
				PolygonAttributes pa = mAppearance.getPolygonAttributes();
				ColoringAttributes ca = mAppearance.getColoringAttributes();

				if (pa == null) {
					pa = new PolygonAttributes();
				}
				if (ca == null) {
					ca = new ColoringAttributes();
				}

				switch (mode) {
					case 0 : // Wireframe (not used in 3DSMax?)
						style = PolygonAttributes.POLYGON_LINE;
						break;

					case 2 : // Metal - use this for rendering points.
						style = PolygonAttributes.POLYGON_POINT;
						break;

					case 1 : // Constant
						style = PolygonAttributes.POLYGON_FILL;
						ca.setShadeModel(ColoringAttributes.SHADE_FLAT);
						break;

					case 3 : // Phong
					default :
						style = PolygonAttributes.POLYGON_FILL;
						ca.setShadeModel(ColoringAttributes.NICEST);
						break;

				}
				tdb.debugln(3, "== Shading: " + mode + ", style = " + style);

				pa.setPolygonMode(style);
				mAppearance.setPolygonAttributes(pa);
				mAppearance.setColoringAttributes(ca);
				break;

			case K3DS_MAT_WIRE : // Another way of enforcing wireframe
				PolygonAttributes pat = mAppearance.getPolygonAttributes();
				pat.setPolygonMode(PolygonAttributes.POLYGON_LINE);
				tdb.debugln(3, "== Wireframe");

				break;

			case K3DS_MAT_WIRE_SIZE : // Wireframe line width
				float width = readFloat(in);
				LineAttributes la = mAppearance.getLineAttributes();

				if (la == null) {
					la = new LineAttributes();
				}

				la.setLineWidth(width);
				mAppearance.setLineAttributes(la);
				tdb.debugln(3, "== Wire width: " + width);

				break;

			case K3DS_MAT_TWO_SIDE : // Face culling
				PolygonAttributes pat2 = mAppearance.getPolygonAttributes();
				pat2.setCullFace(PolygonAttributes.CULL_NONE);
				tdb.debugln(3, "== Two sided");

				break;

			case K3DS_MAT_TEXMAP : // Image for texture map
				URL url = null;
                String imageName = null;
				float matPercent = readPercentage(in) * 100;
				String matName = readMatName(in);
				tdb.debugln(1, "Texture image name: " + matName);
                if(texturePath != null)
                	imageName = texturePath + matName;
                else
                    imageName = basePath + matName;
				imageName.replaceAll(" ", "%20;");

				tdb.debugln(1, "Loading texture map '" + imageName + "' ("
						+ matPercent + "%)");
				url = URLGenerator.getResource(imageName);
                if(url != null)
                	tdb.debugln(1, " texture map resource = '" + url.toString()
						+ "'");
                else
                    tdb.debugln(1, " texture map resource = null");
          
				//if ( new java.io.File( imageName ).exists() == false )
				//{
				// tdb.debugln(, "** Can't find image '" + imageName + "'" );
				// imageName = null;
				//}
				if (url == null) {
					tdb.debugln(0, "** Can't find image '" + imageName + "'");
					imageName = null;
				} else {
					tdb.debugln(1, "Loading texture map URL: " + url);
					Texture textureMap = null;
					try {
						textureMap = new teal.render.j3d.TextureLoader(url).getTexture();
					} catch (Exception e) {
						TDebug.printThrown(0, e, "TextureLoader error on: "
								+ url);
					}
					if (textureMap == null) {
						tdb.debugln(1, "** TextureMap not found");
					}
                    else
                    {
					tdb.debugln(3, "== Texturing: " + textureMap.getEnable());
					tdb.debugln(3, "== MipMapMode: "
							+ textureMap.getMipMapMode());

					//textureMap.setMinFilter( Texture.BASE_LEVEL_POINT );
					//textureMap.setMagFilter( Texture.BASE_LEVEL_POINT );
					mAppearance.setTexture(textureMap);

					TextureAttributes texA = mAppearance.getTextureAttributes();

					if (texA == null) {
						texA = new TextureAttributes();
					}

					mAppearance.setTextureAttributes(texA);
                    }
				}
				break;

			case K3DS_TEX_VERTS : // 2D Texture coordinates
				processTextureCoordinates(in);
				break;

			case K3DS_NAMED_OBJECT : // Start of 3DS object
				if (surfacesCreated == false) {
					createUnsmoothedFaces();
					prepareForNewObject();
				}

				objectName = readName(in);
				tdb.debugln(1, "Processing named object '" + objectName + "'");

				if (hiddenObject(objectName)) {
					skipChunk(in, length - objectName.length() - 1);
					tdb.debugln(1, "(Skipping hidden object '" + objectName
							+ "')");
					break;
				}

				object = new SharedGroup();
				object.setUserData(objectName);
				object.setCapability(Group.ALLOW_CHILDREN_READ);
				object.setCapability(SharedGroup.ALLOW_LINK_READ);
				object.setCapability(Node.ALLOW_BOUNDS_READ);
				object.setBoundsAutoCompute(true);
				shape = new Shape3D();
				shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
				shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
				shape.setCapability(Node.ALLOW_BOUNDS_READ);
				shape.setBoundsAutoCompute(true);
				processChunk(in);
				tdb.debugln(3, "== Adding shape to transform group");

				object.addChild(shape);
				tdb.debugln(3, "== Adding object to list of shared objects");
				tdb.debugln(3, "Object: " + objectName + " Vertices: "
						+ numVertices + " Faces: " + numFaces);

				objectTable.put(objectName, object);
				break;

			case K3DS_POINT_ARRAY : // Vertex list
				tdb.debugln(3, "Processing POINT_ARRAY");
				processPointArray(in);
				break;

			case K3DS_FACE_ARRAY : // Face list
				tdb.debugln(3, "Processing FACE_ARRAY");
				processFaceArray(in);
				break;

			case K3DS_MSH_MAT_GROUP : // Materials used by object
				tdb.debugln(3, "Processing MSH_MAT_GROUP");
				processMaterial(in);
				break;

			case K3DS_SMOOTH_GROUP : // List of surfaces
				tdb.debugln(3, "Processing SMOOTH_GROUP");
				processSmoothGroup(in);
				prepareForNewObject();
				break;

			case K3DS_MESH_MATRIX : // Object transform
				tdb.debugln(3, "Processing MESH_MATRIX");
				processMeshMatrix(in);
				break;

			case K3DS_POINT_FLAG_ARRAY : // Not much use to us
				tdb.debugln(3, "Skipping FLAG_ARRAY");
				skipChunk(in, length);
				break;
			case K3DS_PIVOT : // Unused
				tdb.debugln(3, "Skipping PIVOT");
				skipChunk(in, length);
				break;

			default :
				tdb.debugln(3, TDebug.indent() + "Skipping TAG: "
						+ getChunkType(tag) + " LEN: " + length);

				skipChunk(in, length);
				break;
		}
		TDebug.indentN();
	}

	//
	// Skip over the current chunk, i.e. we are not interested in it.
	//

	void skipChunk(DataInputStream in, int length) throws IOException {
		int bytesToSkip = length - 6;

		if (bytesToSkip > 0) {
			in.skip(bytesToSkip);
		}
	}

	//
	// K3DS_OBJ_HIDDEN doesn't seem to be set by 3DSMax, so
	// objects that have names beginning with '$' are taken
	// as hidden.
	//

	boolean hiddenObject(String name) {
		return name.charAt(0) == '$';
	}

	//
	// Defines an instance of an object.
	//

	void processLink(DataInputStream in) throws IOException {
		nodeName = readName(in);

		int flags1 = readUnsignedShort(in);
		int flags2 = readUnsignedShort(in);
		int flags3 = readUnsignedShort(in);
		tdb.debugln(4, "== Link for object '" + nodeName + "': 0x"
				+ Integer.toHexString(flags1) + ", 0x"
				+ Integer.toHexString(flags2) + ", 0x"
				+ Integer.toHexString(flags3));

	}

	//
	// Processed out of curiosity!
	//

	void processNodeID(DataInputStream in) throws IOException {
		int id = readUnsignedShort(in);

		tdb.debugln(4, "== NodeID: " + id);

	}

	//
	// Lookup an object by <name>.
	//

	SharedGroup findObject(String name) {
		return (SharedGroup) objectTable.get(name);
	}

	//
	// Take the last position specified in this keyframe list
	// as the initial position of the object.
	//

	void processPosTrackTag(DataInputStream in) throws IOException {
		int dummy, keys, i;

		dummy = readUnsignedShort(in);
		dummy = readUnsignedShort(in);
		dummy = readUnsignedShort(in);
		dummy = readUnsignedShort(in);
		dummy = readUnsignedShort(in);
		keys = readUnsignedShort(in);
		dummy = readUnsignedShort(in);

		for (i = 0; i < keys; i++) {
			dummy = readUnsignedShort(in);
			dummy = readInt(in);

			//
			// Reverse the Y and Z coordinates, negate Z coordinates
			//

			float x = readFloat(in), y = readFloat(in), z = readFloat(in);

			translation = new Vector3f(x, z, -y);
			// translation = new Vector3f( x, y, z );

			tdb.debugln(3, "Key: " + i + "\tPosition: " + translation);

		}
	}

	//
	// Take the last orientation specified in this keyframe list
	// as the initial orientation of the object.
	//

	void processRotTrackTag(DataInputStream in) throws IOException {
		int dummy, keys, i;

		dummy = readUnsignedShort(in);
		dummy = readUnsignedShort(in);
		dummy = readUnsignedShort(in);
		dummy = readUnsignedShort(in);
		dummy = readUnsignedShort(in);
		keys = readUnsignedShort(in);
		dummy = readUnsignedShort(in);

		for (i = 0; i < keys; i++) {
			dummy = readUnsignedShort(in);
			dummy = readInt(in);
			float rot = readFloat(in);
			float x = readFloat(in);
			float y = readFloat(in);
			float z = readFloat(in);

			//
			// Convert the orientation between 3DS and
			// Java3D coordinate systems.
			//

			AxisAngle4f axes = new AxisAngle4f(x, y, z, -rot);
			Matrix4f m = new Matrix4f();
			Matrix4f rm = new Matrix4f();

			m.set(axes);
			rm.rotX((float) -Math.PI / 2);
			orientation = new Matrix4f();
			orientation.mul(rm, m);
			tdb.debugln(3, " Rotation: " + orientation);

		}
	}

	//
	// Take the last scale specified in this keyframe list
	// as the initial scale of the object. Also take this
	// as the queue to finish instancing the object.
	//

	void processSclTrackTag(DataInputStream in) throws IOException {
		int dummy, keys, i;

		dummy = readUnsignedShort(in);
		dummy = readUnsignedShort(in);
		dummy = readUnsignedShort(in);
		dummy = readUnsignedShort(in);
		dummy = readUnsignedShort(in);
		keys = readUnsignedShort(in);
		dummy = readUnsignedShort(in);

		for (i = 0; i < keys; i++) {
			dummy = readUnsignedShort(in);
			dummy = readInt(in);

			//
			// Reverse the Y and Z coordinates
			//

			float x = readFloat(in), y = readFloat(in), z = readFloat(in);

			scale = new Vector3f(x, z, y);
			tdb.debugln(3, " Scale : " + scale);

		}

		if (hiddenObject(nodeName)) {
			return;
		}

		Matrix4f m = new Matrix4f();

		m.set(orientation);
		if (transformVerts) {

			m.setTranslation(translation);
		}
		Transform3D transform = new Transform3D(m);
		TransformGroup instance = new TransformGroup(transform);
		instance.setCapability(Group.ALLOW_CHILDREN_READ);

		instance.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		SharedGroup shared = null;

		if (instanceName == null) // Use the node name.
		{
			tdb.debugln(2, "Instancing '" + nodeName + "'");
			instanceTable.put(nodeName, instance);
		} else {
			tdb.debugln(2, "Instancing '" + instanceName + "' (->'" + nodeName
					+ "')");
			instanceTable.put(instanceName, instance);
		}
		tdb.debugln(3, "\tsetting translation: " + translation);

		tdb.debugln(3, "\tsetting orientation: " + orientation);

		shared = findObject(nodeName);

		if (shared == null) {
			throw new IOException("Can't locate referenced object.");
		}

		Link link = new Link(shared);
		link.setCapability(Link.ALLOW_SHARED_GROUP_READ);
		instance.addChild(link);
		root.addChild(instance);
		instanceName = null;
	}

	//
	// Processed out of curiosity.
	//

	void processMasterScale(DataInputStream in) throws IOException {
		float scale = readFloat(in);
		tdb.debugln(2, "== Master scale: " + scale);
	}

	//
	// Read in the list of vertices.
	//

	void processPointArray(DataInputStream in) throws IOException {
		int i;

		numVertices = readUnsignedShort(in);
		vertices = new Point3f[numVertices];
		sharedFaces = new Vector[numVertices];

		tdb.debugln(3, " vertices: " + numVertices);
		totalVertices += numVertices;

		for (i = 0; i < numVertices; i++) {

			vertices[i] = new Point3f(readFloat(in), readFloat(in),
					readFloat(in));

			/*
			 * // Reverse the Y and Z coordinates, negate Z coordinates
			 * 
			 * float temp = vertices[i].z;
			 * 
			 * vertices[i].z = -vertices[i].y; vertices[i].y = temp;
			 */

			sharedFaces[i] = new Vector(); // Filled in processFaceArray()
		}
	}

	//
	// Read in the list of polygons and allocate a TriangleArray
	// big enough to contain them. Don't fill it in yet though,
	// we want to one surface at a time.
	//

	void processFaceArray(DataInputStream in) throws IOException {
		int i;
		int vertexFormat = GeometryArray.COORDINATES | GeometryArray.NORMALS;

		if (textureCoords != null) {
			tdb.debugln(2, " Object is TEXTURED");

			vertexFormat |= GeometryArray.TEXTURE_COORDINATE_2;
		}

		numFaces = readUnsignedShort(in);
		if (numFaces > 0) {
			faces = new Face[numFaces];
			try {
				geometry = new TriangleArray(numFaces * 3, vertexFormat);

				tdb.debugln(3, " faces: " + numFaces);
				totalPolygons += numFaces;

				for (i = 0; i < numFaces; i++) {
					int a = readUnsignedShort(in);
					int b = readUnsignedShort(in);
					int c = readUnsignedShort(in);
					int flags = readUnsignedShort(in);

					faces[i] = new Face(a, b, c);
				}
				tdb.debugln(3, "== Adding geometry to shape");

				shape.setGeometry(geometry);
			} catch (Exception e) {
				TDebug.printThrown(0, e);
			}
		} else {
			TDebug.println(0, "Number of faces zero!");
		}
	}

	//
	// In 3DS a face is constructed by listing vertices in
	// anti-clockwise order.
	//

	Vector3f calculateFaceNormal(int a, int b, int c) {
		Vector3f vertexA = new Vector3f(vertices[a].x, vertices[a].y,
				vertices[a].z);
		Vector3f vertexB = new Vector3f(vertices[b].x, vertices[b].y,
				vertices[b].z);
		Vector3f vertexC = new Vector3f(vertices[c].x, vertices[c].y,
				vertices[c].z);
		Vector3f normal = new Vector3f();

		vertexB.sub(vertexA);
		vertexC.sub(vertexA);

		normal.cross(vertexB, vertexC);
		normal.normalize();

		return normal;
	}

	//
	// Read in the transformation matrix and inverse transform
	// the vertex coordinates so they are back where they were
	// when 3DS initially created the object. They are
	// transformed back again with the information found in
	// the position, rotation and scale keyframe tracks.
	//

	void processMeshMatrix(DataInputStream in) throws IOException {
		Matrix4f m = new Matrix4f();
		int x, y;

		for (y = 0; y < 4; y++) {
			for (x = 0; x < 3; x++) {
				if (y == 3) {
					m.setElement(x, y, readFloat(in));
				} else {
					m.setElement(x, y, readFloat(in));
				}
			}
		}

		m.setElement(3, 3, 1.0f);

		float transY = m.getElement(1, 3);
		float transZ = m.getElement(2, 3);

		/*
		 * // // Reverse the Y and Z coordinates, negate Z coordinates //
		 * 
		 *  // m.setElement( 1, 3, transZ ); // m.setElement( 2, 3, -transY );
		 */

		Transform3D t = new Transform3D(m);
		tdb.debugln(3, " Transform: " + t);

		t.invert();
		if (transformVerts) {
			for (x = 0; x < numVertices; x++) {
				t.transform(vertices[x]);
			}
		}
	}

	//
	// Associate material with the object.
	//

	void processMaterial(DataInputStream in) throws IOException {
		String name = readName(in);
		Appearance m = (Appearance) mats.get(name);

		if (m == null) {
			tdb.debugln(0, "** Can't find referenced material");
			return;
		}

		tdb.debugln(2, " Attaching material '" + name + "'" + m.getMaterial());

		shape.setAppearance(m);

		int faceCount = readUnsignedShort(in);
		int i;
		tdb.debugln(2, "skipping material faces: " + faceCount);
		for (i = 0; i < faceCount; i++) {
			int dummy = readUnsignedShort(in);
		}
	}

	//
	// Contains a list of smoothed surfaces. Construct each surface
	// at a time, specifying vertex normals and texture coordinates
	// (if present).
	//

	void processSmoothGroup(DataInputStream in) throws IOException {
		double log2 = Math.log(2);
		int i;

		surfaces = new Surface[MAX_SURFACES];

		for (i = 0; i < MAX_SURFACES; i++) {
			surfaces[i] = new Surface();
		}

		for (i = 0; i < numFaces; i++) {
			int group = readInt(in);
			long b = 0x1;
			int index;

			for (index = 0; index < 32; index++) {
				if ((group & b) > 0) {
					break;
				}
			}

			//tdb.debugln(4, "== group " + Long.toHexString( group ) + ", index
			// = " + index );

			faces[i].group = (int) group;
			surfaces[index].add(faces[i]);
		}

		int surface;

		i = 0;

		for (surface = 0; surface < MAX_SURFACES; surface++) {
			tdb.debugln(4, " Constructing surface " + surface);

			Enumeration<Face> iter = surfaces[surface].faces();

			while (iter.hasMoreElements()) {
				Face f = (Face) iter.nextElement();

				Vector3f normalA = calculateVertexNormal(f.a, f,
						surfaces[surface]);
				Vector3f normalB = calculateVertexNormal(f.b, f,
						surfaces[surface]);
				Vector3f normalC = calculateVertexNormal(f.c, f,
						surfaces[surface]);

				//tdb.debugln(3, "Face [" + f.a + "] " + f.a() + ", [" + f.b +
				// "] " +
				// f.b() + ", [" + f.c + "] " + f.c() );
				//tdb.debugln(3, "Norm " + normalA + ", " + normalB + ", " +
				// normalC );

				geometry.setCoordinate(i, f.a());
				geometry.setNormal(i, normalA);
				if (textureCoords != null) {
					geometry.setTextureCoordinate(0,i, textureCoords[f.a]);
				}

				geometry.setCoordinate(++i, f.b());
				geometry.setNormal(i, normalB);
				if (textureCoords != null) {
					geometry.setTextureCoordinate(0,i, textureCoords[f.b]);
				}

				geometry.setCoordinate(++i, f.c());
				geometry.setNormal(i, normalC);
				if (textureCoords != null) {
					geometry.setTextureCoordinate(0,i, textureCoords[f.c]);
				}
				i++;
			}
		}
		tdb.debugln(2, " Constructed surfaces ");
		surfacesCreated = true;
	}

	//
	// Fill the TriangleArray with the raw polygon information,
	// don't calculate vertex normals but do give texture coordinates
	// if present.
	//

	void createUnsmoothedFaces() {
		int i = 0;

		for (int j = 0; j < numFaces; j++) {
			Face f = faces[j];

			//tdb.debugln(, "Face [" + f.a + "], [" + f.b + "], [" + f.c + "]"
			// );

			geometry.setCoordinate(i, f.a());
			geometry.setNormal(i, f.normal);
			if (textureCoords != null) {
				geometry.setTextureCoordinate(0,i, textureCoords[f.a]);
			}
			geometry.setCoordinate(++i, f.b());
			geometry.setNormal(i, f.normal);
			if (textureCoords != null) {

				geometry.setTextureCoordinate(0,i, textureCoords[f.b]);
			}
			geometry.setCoordinate(++i, f.c());
			geometry.setNormal(i, f.normal);
			if (textureCoords != null) {
				geometry.setTextureCoordinate(0,i, textureCoords[f.c]);
			}
			i++;
		}
	}

	//
	// Checks if <vertex> is used by <face>.
	//

	boolean sharesVertex(Face face, int vertex) {
		return face.a == vertex || face.b == vertex || face.c == vertex;
	}

	//
	// Calculates a normalised vertex normal for <vertex> in
	// <thisFace> within <surface>.
	//

	Vector3f calculateVertexNormal(int vertex, Face thisFace, Surface surface) {
		Enumeration<Face> otherFaces = surface.faces();
		Vector3f normal = new Vector3f(thisFace.normal);
		int numNormals = 1;

		while (otherFaces.hasMoreElements()) {
			Face otherFace = (Face) otherFaces.nextElement();

			if (sharesVertex(otherFace, vertex)) {
				normal.add(otherFace.normal);
				numNormals++;
			}
		}

		if (numNormals != 1) {
			normal.x /= numNormals;
			normal.y /= numNormals;
			normal.z /= numNormals;
		}

		normal.normalize();

		return normal;
	}

	//
	// Read in the 2D texture coordinates - note these are
	// only valid if planar mapping was used in 3DS.
	//

	void processTextureCoordinates(DataInputStream in) throws IOException {
		int vertexCount = readUnsignedShort(in);
		int i;

		if (vertexCount != numVertices) {
			tdb.debugln(3, "** Number of texture vertices = #model vertices");
			return;
		}

		tdb.debugln(3, " Texture coordinates: #" + vertexCount);

		textureCoords = new TexCoord2f[vertexCount];

		for (i = 0; i < vertexCount; i++) {
			textureCoords[i] = new TexCoord2f(readFloat(in), readFloat(in));
			//tdb.debugln(3, "== " + textureCoords[i] );
		}
	}

	/**
	 * Read in the definition of the ambient light.
	 */

	void processAmbientLight(DataInputStream in) throws IOException {
		Color3f ambient = readColor(in);

		tdb.debugln(3, "Ambient Light: " + ambient);
	}

	//
	// Read in a colour, either in the 3 * float format or
	// the 3 * byte format.
	//

	Color3f readColor(DataInputStream in) throws IOException {
		int tag = readUnsignedShort(in);
		int length = readInt(in);

		switch (tag) {
			case K3DS_COLOR_F :
				return new Color3f(readFloat(in), readFloat(in), readFloat(in));

			case K3DS_COLOR_24 :
				return new Color3f((float) in.readUnsignedByte() / 255,
						(float) in.readUnsignedByte() / 255, (float) in
								.readUnsignedByte() / 255);

			case K3DS_LIN_COLOR_24 :
				return new Color3f(1.0f * (in.readUnsignedByte() / 255.0f),
						1.0f * (in.readUnsignedByte() / 255.0f), 1.0f * (in
								.readUnsignedByte() / 255.0f));

			default :
				throw new IOException("COLOR_F/COLOR_24 expected: "
						+ getChunkType(tag));
		}
	}

	/**
	 * Read in a float or int percentage and return it as a number between 0.0
	 * and 1.0
	 */

	float readPercentage(DataInputStream in) throws IOException {
		int tag = readUnsignedShort(in);
		int length = readInt(in);

		switch (tag) {
			case K3DS_INT_PERCENTAGE :
				return (float) readUnsignedShort(in) / 100;

			case K3DS_FLOAT_PERCENTAGE :
				return readFloat(in);

			default :
				throw new IOException(
						"INT_PERCENTAGE/FLOAT_PERCENTAGE expected");
		}
	}

	/**
	 * Read in material name.
	 */
	String readMatName(DataInputStream in) throws IOException {
		int tag = readUnsignedShort(in);
		int length = readInt(in);

		return readName(in);
	}

	/**
	 * Read in the string used to specify a name in many different chunks.
	 */

	String readName(DataInputStream in) throws IOException {
		StringBuffer buf = new StringBuffer();
		char c;

		while ((c = (char) in.readUnsignedByte()) != '\0') {
			buf.append(c);
		}
        tdb.debugln(1, "Name: '" + buf + "'");
		return buf.toString();
	}

	//
	// Read in an unsigned short (16 bits).
	//

	int readUnsignedShort(DataInputStream in) throws IOException {
		int num = in.readUnsignedShort();

		return ((num << 8) & 0xFF00) | ((num >> 8) & 0x00FF);
	}

	//
	// Read in a 32 bit integer (unsigned).
	//

	int readInt(DataInputStream in) throws IOException {
		int num = in.readInt();

		return ((num << 24) & 0xFF000000) | ((num << 8) & 0x00FF0000)
				| ((num >> 8) & 0x0000FF00) | ((num >> 24) & 0x000000FF);
	}

	//
	// Read in a 32 bit floating point number.
	//

	float readFloat(DataInputStream in) throws IOException {
		return Float.intBitsToFloat(readInt(in));
	}

	/**
	 * Internal data structure representing a polygon and when constructed
	 * updates a list of those faces sharing any given vertex.
	 */

	class Face {
		int a, b, c;
		Vector3f normal = null;
		int group;

		public Face(int vertexA, int vertexB, int vertexC) {
			a = vertexA;
			b = vertexB;
			c = vertexC;
			normal = calculateFaceNormal(a, b, c);

			sharedFaces[a].addElement(this);
			sharedFaces[b].addElement(this);
			sharedFaces[c].addElement(this);
		}

		public Point3f a() {
			return vertices[a];
		}

		public Point3f b() {
			return vertices[b];
		}

		public Point3f c() {
			return vertices[c];
		}
	};

	//
	// Internal data structure for representing a surface
	// as a list of faces.
	//

	class Surface {
		Vector faces = new Vector();

		public Surface() {
		}

		public void add(Face f) {
			faces.addElement(f);
		}

		public Enumeration<Face> faces() {
			return faces.elements();
		}

		public int numFaces() {
			return faces.size();
		}
	}

	private String getChunkType(int tag) {
		String chtype = null;
		chtype = (String) chunkTypes.get(new Integer(tag));
		if (chtype == null)
			chtype = "UNKNOWN: 0x" + Integer.toHexString(tag);
		return chtype;
	}

	//
	// List of chunks contained in a .3DS file that we
	// are interested in.
	//

	public static final int K3DS_MESH_VERSION = 0x0001;
	public static final int K3DS_PROJECT_FILE = 0xc23d;
	public static final int K3DS_MAPPING_RETILE = 0xc4b0;
	public static final int K3DS_MAPPING_CENTER = 0xc4c0;

	public static final int K3DS_MAPPING_SCALE = 0xc4d0;
	public static final int K3DS_MAPPING_ORIENTATION = 0xc4e1;
	public static final int K3DS_DUMMY_CHUNK = 0xffff;

	public static final int K3DS_NULL_CHUNK = 0x0000;
	public static final int K3DS_M3DMAGIC = 0x4D4D; /* 3DS file */
	public static final int K3DS_SMAGIC = 0x2D2D;
	public static final int K3DS_LMAGIC = 0x2D3D;
	public static final int K3DS_MLIBMAGIC = 0x3DAA; /* MLI file */
	public static final int K3DS_MATMAGIC = 0x3DFF;
	// public static final int K3DS_CMAGIC =0xC23D; /*PRJ file*/
	public static final int K3DS_M3D_VERSION = 0x0002;
	public static final int K3DS_M3D_KFVERSION = 0x0005;
	public static final int K3DS_COLOR_F = 0x0010;
	public static final int K3DS_COLOR_24 = 0x0011;
	public static final int K3DS_LIN_COLOR_24 = 0x0012;
	public static final int K3DS_LIN_COLOR_F = 0x0013;
	public static final int K3DS_INT_PERCENTAGE = 0x0030;
	public static final int K3DS_FLOAT_PERCENTAGE = 0x0031;
	public static final int K3DS_MDATA = 0x3D3D;
	public static final int K3DS_MESH_VERSION_2 = 0x3D3E;
	public static final int K3DS_MASTER_SCALE = 0x0100;
	public static final int K3DS_LO_SHADOW_BIAS = 0x1400;
	public static final int K3DS_HI_SHADOW_BIAS = 0x1410;
	public static final int K3DS_SHADOW_MAP_SIZE = 0x1420;
	public static final int K3DS_SHADOW_SAMPLES = 0x1430;
	public static final int K3DS_SHADOW_RANGE = 0x1440;
	public static final int K3DS_SHADOW_FILTER = 0x1450;
	public static final int K3DS_RAY_BIAS = 0x1460;
	public static final int K3DS_O_CONSTS = 0x1500;
	public static final int K3DS_AMBIENT_LIGHT = 0x2100;
	public static final int K3DS_BIT_MAP = 0x1100;
	public static final int K3DS_SOLID_BGND = 0x1200;
	public static final int K3DS_V_GRADIENT = 0x1300;
	public static final int K3DS_USE_BIT_MAP = 0x1101;
	public static final int K3DS_USE_SOLID_BGND = 0x1201;
	public static final int K3DS_USE_V_GRADIENT = 0x1301;
	public static final int K3DS_FOG = 0x2200;
	public static final int K3DS_FOG_BGND = 0x2210;
	public static final int K3DS_LAYER_FOG = 0x2302;
	public static final int K3DS_DISTANCE_CUE = 0x2300;
	public static final int K3DS_DCUE_BGND = 0x2310;
	public static final int K3DS_USE_FOG = 0x2201;
	public static final int K3DS_USE_LAYER_FOG = 0x2303;
	public static final int K3DS_USE_DISTANCE_CUE = 0x2301;
	public static final int K3DS_MAT_ENTRY = 0xAFFF;
	public static final int K3DS_MAT_NAME = 0xA000;
	public static final int K3DS_MAT_AMBIENT = 0xA010;
	public static final int K3DS_MAT_DIFFUSE = 0xA020;
	public static final int K3DS_MAT_SPECULAR = 0xA030;
	public static final int K3DS_MAT_SHININESS = 0xA040;
	public static final int K3DS_MAT_SHININESS_STRENGTH = 0xA041;
	public static final int K3DS_MAT_TRANSPARENCY = 0xA050;
	public static final int K3DS_MAT_FALLOFF = 0xA052;
	public static final int K3DS_MAT_USE_XPFALL = 0xA240;
	public static final int K3DS_MAT_REFBLUR = 0xA053;
	public static final int K3DS_MAT_SHADING = 0xA100;
	public static final int K3DS_MAT_USE_REFBLUR = 0xA250;
	public static final int K3DS_MAT_SELF_ILLUM = 0xA080;
	public static final int K3DS_MAT_TWO_SIDE = 0xA081;
	public static final int K3DS_MAT_DECAL = 0xA082;
	public static final int K3DS_MAT_ADDITIVE = 0xA083;
	public static final int K3DS_MAT_WIRE = 0xA085;
	public static final int K3DS_MAT_FACEMAP = 0xA088;
	public static final int K3DS_MAT_PHONGSOFT = 0xA08C;
	public static final int K3DS_MAT_WIREABS = 0xA08E;
	public static final int K3DS_MAT_WIRE_SIZE = 0xA087;
	public static final int K3DS_MAT_TEXMAP = 0xA200;
	public static final int K3DS_MAT_SXP_TEXT_DATA = 0xA320;
	public static final int K3DS_MAT_TEXMASK = 0xA33E;
	public static final int K3DS_MAT_SXP_TEXTMASK_DATA = 0xA32A;
	public static final int K3DS_MAT_TEX2MAP = 0xA33A;
	public static final int K3DS_MAT_SXP_TEXT2_DATA = 0xA321;
	public static final int K3DS_MAT_TEX2MASK = 0xA340;
	public static final int K3DS_MAT_SXP_TEXT2MASK_DATA = 0xA32C;
	public static final int K3DS_MAT_OPACMAP = 0xA210;
	public static final int K3DS_MAT_SXP_OPAC_DATA = 0xA322;
	public static final int K3DS_MAT_OPACMASK = 0xA342;
	public static final int K3DS_MAT_SXP_OPACMASK_DATA = 0xA32E;
	public static final int K3DS_MAT_BUMPMAP = 0xA230;
	public static final int K3DS_MAT_SXP_BUMP_DATA = 0xA324;
	public static final int K3DS_MAT_BUMPMASK = 0xA344;
	public static final int K3DS_MAT_SXP_BUMPMASK_DATA = 0xA330;
	public static final int K3DS_MAT_SPECMAP = 0xA204;
	public static final int K3DS_MAT_SXP_SPEC_DATA = 0xA325;
	public static final int K3DS_MAT_SPECMASK = 0xA348;
	public static final int K3DS_MAT_SXP_SPECMASK_DATA = 0xA332;
	public static final int K3DS_MAT_SHINMAP = 0xA33C;
	public static final int K3DS_MAT_SXP_SHIN_DATA = 0xA326;
	public static final int K3DS_MAT_SHINMASK = 0xA346;
	public static final int K3DS_MAT_SXP_SHINMASK_DATA = 0xA334;
	public static final int K3DS_MAT_SELFIMAP = 0xA33D;
	public static final int K3DS_MAT_SXP_SELFI_DATA = 0xA328;
	public static final int K3DS_MAT_SELFIMASK = 0xA34A;
	public static final int K3DS_MAT_SXP_SELFIMASK_DATA = 0xA336;
	public static final int K3DS_MAT_REFLMAP = 0xA220;
	public static final int K3DS_MAT_REFLMASK = 0xA34C;
	public static final int K3DS_MAT_SXP_REFLMASK_DATA = 0xA338;
	public static final int K3DS_MAT_ACUBIC = 0xA310;
	public static final int K3DS_MAT_MAPNAME = 0xA300;
	public static final int K3DS_MAT_MAP_TILING = 0xA351;
	public static final int K3DS_MAT_MAP_TEXBLUR = 0xA353;
	public static final int K3DS_MAT_MAP_USCALE = 0xA354;
	public static final int K3DS_MAT_MAP_VSCALE = 0xA356;
	public static final int K3DS_MAT_MAP_UOFFSET = 0xA358;
	public static final int K3DS_MAT_MAP_VOFFSET = 0xA35A;
	public static final int K3DS_MAT_MAP_ANG = 0xA35C;
	public static final int K3DS_MAT_MAP_COL1 = 0xA360;
	public static final int K3DS_MAT_MAP_COL2 = 0xA362;
	public static final int K3DS_MAT_MAP_RCOL = 0xA364;
	public static final int K3DS_MAT_MAP_GCOL = 0xA366;
	public static final int K3DS_MAT_MAP_BCOL = 0xA368;
	public static final int K3DS_NAMED_OBJECT = 0x4000;
	public static final int K3DS_N_DIRECT_LIGHT = 0x4600;
	public static final int K3DS_DL_OFF = 0x4620;
	public static final int K3DS_DL_OUTER_RANGE = 0x465A;
	public static final int K3DS_DL_INNER_RANGE = 0x4659;
	public static final int K3DS_DL_MULTIPLIER = 0x465B;
	public static final int K3DS_DL_EXCLUDE = 0x4654;
	public static final int K3DS_DL_ATTENUATE = 0x4625;
	public static final int K3DS_DL_SPOTLIGHT = 0x4610;
	public static final int K3DS_DL_SPOT_ROLL = 0x4656;
	public static final int K3DS_DL_SHADOWED = 0x4630;
	public static final int K3DS_DL_LOCAL_SHADOW2 = 0x4641;
	public static final int K3DS_DL_SEE_CONE = 0x4650;
	public static final int K3DS_DL_SPOT_RECTANGULAR = 0x4651;
	public static final int K3DS_DL_SPOT_ASPECT = 0x4657;
	public static final int K3DS_DL_SPOT_PROJECTOR = 0x4653;
	public static final int K3DS_DL_SPOT_OVERSHOOT = 0x4652;
	public static final int K3DS_DL_RAY_BIAS = 0x4658;
	public static final int K3DS_DL_RAYSHAD = 0x4627;
	public static final int K3DS_N_CAMERA = 0x4700;
	public static final int K3DS_CAM_SEE_CONE = 0x4710;
	public static final int K3DS_CAM_RANGES = 0x4720;
	public static final int K3DS_OBJ_HIDDEN = 0x4010;
	public static final int K3DS_OBJ_VIS_LOFTER = 0x4011;
	public static final int K3DS_OBJ_DOESNT_CAST = 0x4012;
	public static final int K3DS_OBJ_DONT_RECVSHADOW = 0x4017;
	public static final int K3DS_OBJ_MATTE = 0x4013;
	public static final int K3DS_OBJ_FAST = 0x4014;
	public static final int K3DS_OBJ_PROCEDURAL = 0x4015;
	public static final int K3DS_OBJ_FROZEN = 0x4016;
	public static final int K3DS_N_TRI_OBJECT = 0x4100;
	public static final int K3DS_POINT_ARRAY = 0x4110;
	public static final int K3DS_POINT_FLAG_ARRAY = 0x4111;
	public static final int K3DS_FACE_ARRAY = 0x4120;
	public static final int K3DS_MSH_MAT_GROUP = 0x4130;
	public static final int K3DS_SMOOTH_GROUP = 0x4150;
	public static final int K3DS_MSH_BOXMAP = 0x4190;
	public static final int K3DS_TEX_VERTS = 0x4140;
	public static final int K3DS_MESH_MATRIX = 0x4160;
	public static final int K3DS_MESH_COLOR = 0x4165;
	public static final int K3DS_MESH_TEXTURE_INFO = 0x4170;
	public static final int K3DS_KF_DATA = 0xB000;
	public static final int K3DS_KF_HDR = 0xB00A;
	public static final int K3DS_KF_SEG = 0xB008;
	public static final int K3DS_KF_CURTIME = 0xB009;
	public static final int K3DS_AMBIENT_NODE_TAG = 0xB001;
	public static final int K3DS_OBJECT_NODE_TAG = 0xB002;
	public static final int K3DS_CAMERA_NODE_TAG = 0xB003;
	public static final int K3DS_TARGET_NODE_TAG = 0xB004;
	public static final int K3DS_LIGHT_NODE_TAG = 0xB005;
	public static final int K3DS_L_TARGET_NODE_TAG = 0xB006;
	public static final int K3DS_SPOTLIGHT_NODE_TAG = 0xB007;
	public static final int K3DS_NODE_ID = 0xB030;
	public static final int K3DS_NODE_HDR = 0xB010;
	public static final int K3DS_PIVOT = 0xB013;
	public static final int K3DS_INSTANCE_NAME = 0xB011;
	public static final int K3DS_MORPH_SMOOTH = 0xB015;
	public static final int K3DS_BOUNDBOX = 0xB014;
	public static final int K3DS_POS_TRACK_TAG = 0xB020;
	public static final int K3DS_COL_TRACK_TAG = 0xB025;
	public static final int K3DS_ROT_TRACK_TAG = 0xB021;
	public static final int K3DS_SCL_TRACK_TAG = 0xB022;
	public static final int K3DS_MORPH_TRACK_TAG = 0xB026;
	public static final int K3DS_FOV_TRACK_TAG = 0xB023;
	public static final int K3DS_ROLL_TRACK_TAG = 0xB024;
	public static final int K3DS_HOT_TRACK_TAG = 0xB027;
	public static final int K3DS_FALL_TRACK_TAG = 0xB028;
	public static final int K3DS_HIDE_TRACK_TAG = 0xB029;
	public static final int K3DS_POLY_2D = 0x5000;
	public static final int K3DS_SHAPE_OK = 0x5010;
	public static final int K3DS_SHAPE_NOT_OK = 0x5011;
	public static final int K3DS_SHAPE_HOOK = 0x5020;
	public static final int K3DS_PATH_3D = 0x6000;
	public static final int K3DS_PATH_MATRIX = 0x6005;
	public static final int K3DS_SHAPE_2D = 0x6010;
	public static final int K3DS_M_SCALE = 0x6020;
	public static final int K3DS_M_TWIST = 0x6030;
	public static final int K3DS_M_TEETER = 0x6040;
	public static final int K3DS_M_FIT = 0x6050;
	public static final int K3DS_M_BEVEL = 0x6060;
	public static final int K3DS_XZ_CURVE = 0x6070;
	public static final int K3DS_YZ_CURVE = 0x6080;
	public static final int K3DS_INTERPCT = 0x6090;
	public static final int K3DS_DEFORM_LIMIT = 0x60A0;
	public static final int K3DS_USE_CONTOUR = 0x6100;
	public static final int K3DS_USE_TWEEN = 0x6110;
	public static final int K3DS_USE_SCALE = 0x6120;
	public static final int K3DS_USE_TWIST = 0x6130;
	public static final int K3DS_USE_TEETER = 0x6140;
	public static final int K3DS_USE_FIT = 0x6150;
	public static final int K3DS_USE_BEVEL = 0x6160;
	public static final int K3DS_DEFAULT_VIEW = 0x3000;
	public static final int K3DS_VIEW_TOP = 0x3010;
	public static final int K3DS_VIEW_BOTTOM = 0x3020;
	public static final int K3DS_VIEW_LEFT = 0x3030;
	public static final int K3DS_VIEW_RIGHT = 0x3040;
	public static final int K3DS_VIEW_FRONT = 0x3050;
	public static final int K3DS_VIEW_BACK = 0x3060;
	public static final int K3DS_VIEW_USER = 0x3070;
	public static final int K3DS_VIEW_CAMERA = 0x3080;
	public static final int K3DS_VIEW_WINDOW = 0x3090;
	public static final int K3DS_VIEWPORT_LAYOUT_OLD = 0x7000;
	public static final int K3DS_VIEWPORT_DATA_OLD = 0x7010;
	public static final int K3DS_VIEWPORT_LAYOUT = 0x7001;
	public static final int K3DS_VIEWPORT_DATA = 0x7011;
	public static final int K3DS_VIEWPORT_DATA_3 = 0x7012;
	public static final int K3DS_VIEWPORT_SIZE = 0x7020;
	public static final int K3DS_NETWORK_VIEW = 0x7030;

	private void loadChunkTypes() {
		chunkTypes = new TreeMap();
		chunkTypes.put(new Integer(K3DS_MESH_VERSION), "K3DS_MESH_VERSION");
		chunkTypes.put(new Integer(K3DS_PROJECT_FILE), "K3DS_PROJECT_FILE");
		chunkTypes.put(new Integer(K3DS_MAPPING_RETILE), "K3DS_MAPPING_RETILE");
		chunkTypes.put(new Integer(K3DS_MAPPING_CENTER), "K3DS_MAPPING_CENTER");
		chunkTypes.put(new Integer(K3DS_MAPPING_SCALE), "K3DS_MAPPING_SCALE");
		chunkTypes.put(new Integer(K3DS_MAPPING_ORIENTATION),
				"K3DS_MAPPING_ORIENTATION");
		chunkTypes.put(new Integer(K3DS_DUMMY_CHUNK), "K3DS_DUMMY_CHUNK");

		chunkTypes.put(new Integer(K3DS_NULL_CHUNK), "K3DS_NULL_CHUNK");
		chunkTypes.put(new Integer(K3DS_M3DMAGIC), "K3DS_M3DMAGIC");
		chunkTypes.put(new Integer(K3DS_LMAGIC), "K3DS_LMAGIC");
		chunkTypes.put(new Integer(K3DS_MLIBMAGIC), "K3DS_MLIBMAGIC");
		chunkTypes.put(new Integer(K3DS_MATMAGIC), "K3DS_MATMAGIC");
		//chunkTypes.put(new Integer(K3DS_CMAGIC), "K3DS_CMAGIC");
		chunkTypes.put(new Integer(K3DS_M3D_VERSION), "K3DS_M3D_VERSION");
		chunkTypes.put(new Integer(K3DS_M3D_KFVERSION), "K3DS_M3D_KFVERSION");
		chunkTypes.put(new Integer(K3DS_COLOR_F), "K3DS_COLOR_F");
		chunkTypes.put(new Integer(K3DS_COLOR_24), "K3DS_COLOR_24");
		chunkTypes.put(new Integer(K3DS_LIN_COLOR_24), "K3DS_LIN_COLOR_24");
		chunkTypes.put(new Integer(K3DS_LIN_COLOR_F), "K3DS_LIN_COLOR_F");
		chunkTypes.put(new Integer(K3DS_INT_PERCENTAGE), "K3DS_INT_PERCENTAGE");
		chunkTypes.put(new Integer(K3DS_FLOAT_PERCENTAGE),
				"K3DS_FLOAT_PERCENTAGE");
		chunkTypes.put(new Integer(K3DS_MDATA), "K3DS_MDATA");
		chunkTypes.put(new Integer(K3DS_MESH_VERSION_2), "K3DS_MESH_VERSION_2");
		chunkTypes.put(new Integer(K3DS_MASTER_SCALE), "K3DS_MASTER_SCALE");
		chunkTypes.put(new Integer(K3DS_LO_SHADOW_BIAS), "K3DS_LO_SHADOW_BIAS");
		chunkTypes.put(new Integer(K3DS_HI_SHADOW_BIAS), "K3DS_HI_SHADOW_BIAS");
		chunkTypes.put(new Integer(K3DS_SHADOW_MAP_SIZE),
				"K3DS_SHADOW_MAP_SIZE");
		chunkTypes.put(new Integer(K3DS_SHADOW_SAMPLES), "K3DS_SHADOW_SAMPLES");
		chunkTypes.put(new Integer(K3DS_SHADOW_RANGE), "K3DS_SHADOW_RANGE");
		chunkTypes.put(new Integer(K3DS_SHADOW_FILTER), "K3DS_SHADOW_FILTER");
		chunkTypes.put(new Integer(K3DS_RAY_BIAS), "K3DS_RAY_BIAS");
		chunkTypes.put(new Integer(K3DS_O_CONSTS), "K3DS_O_CONSTS");
		chunkTypes.put(new Integer(K3DS_AMBIENT_LIGHT), "K3DS_AMBIENT_LIGHT");
		chunkTypes.put(new Integer(K3DS_BIT_MAP), "K3DS_BIT_MAP");
		chunkTypes.put(new Integer(K3DS_SOLID_BGND), "K3DS_SOLID_BGND");
		chunkTypes.put(new Integer(K3DS_V_GRADIENT), "K3DS_V_GRADIENT");
		chunkTypes.put(new Integer(K3DS_USE_BIT_MAP), "K3DS_USE_BIT_MAP");
		chunkTypes.put(new Integer(K3DS_USE_SOLID_BGND), "K3DS_USE_SOLID_BGND");
		chunkTypes.put(new Integer(K3DS_USE_V_GRADIENT), "K3DS_USE_V_GRADIENT");
		chunkTypes.put(new Integer(K3DS_FOG), "K3DS_FOG");
		chunkTypes.put(new Integer(K3DS_FOG_BGND), "K3DS_FOG_BGND");
		chunkTypes.put(new Integer(K3DS_LAYER_FOG), "K3DS_LAYER_FOG");
		chunkTypes.put(new Integer(K3DS_DISTANCE_CUE), "K3DS_DISTANCE_CUE");
		chunkTypes.put(new Integer(K3DS_DCUE_BGND), "K3DS_DCUE_BGND");
		chunkTypes.put(new Integer(K3DS_USE_FOG), "K3DS_USE_FOG");
		chunkTypes.put(new Integer(K3DS_USE_LAYER_FOG), "K3DS_USE_LAYER_FOG");
		chunkTypes.put(new Integer(K3DS_USE_DISTANCE_CUE),
				"K3DS_USE_DISTANCE_CUE");
		chunkTypes.put(new Integer(K3DS_MAT_ENTRY), "K3DS_MAT_ENTRY");
		chunkTypes.put(new Integer(K3DS_MAT_NAME), "K3DS_MAT_NAME");
		chunkTypes.put(new Integer(K3DS_MAT_AMBIENT), "K3DS_MAT_AMBIENT");
		chunkTypes.put(new Integer(K3DS_MAT_DIFFUSE), "K3DS_MAT_DIFFUSE");
		chunkTypes.put(new Integer(K3DS_MAT_SPECULAR), "K3DS_MAT_SPECULAR");
		chunkTypes.put(new Integer(K3DS_MAT_SHININESS), "K3DS_MAT_SHININESS");
		chunkTypes.put(new Integer(K3DS_MAT_SHININESS_STRENGTH),
				"K3DS_MAT_SHINESS_STRENGTH");
		chunkTypes.put(new Integer(K3DS_MAT_TRANSPARENCY),
				"K3DS_MAT_TRANSPARENCY");
		chunkTypes.put(new Integer(K3DS_MAT_FALLOFF), "K3DS_MAT_FALLOFF");
		chunkTypes.put(new Integer(K3DS_MAT_USE_XPFALL), "K3DS_MAT_USE_XPFALL");
		chunkTypes.put(new Integer(K3DS_MAT_REFBLUR), "K3DS_MAT_REFBLUR");
		chunkTypes.put(new Integer(K3DS_MAT_SHADING), "K3DS_MAT_SHADING");
		chunkTypes.put(new Integer(K3DS_MAT_USE_REFBLUR),
				"K3DS_MAT_USE_REFBLUR");
		chunkTypes.put(new Integer(K3DS_MAT_SELF_ILLUM), "K3DS_MAT_SELF_ILLUM");
		chunkTypes.put(new Integer(K3DS_MAT_TWO_SIDE), "K3DS_MAT_TWO_SIDE");
		chunkTypes.put(new Integer(K3DS_MAT_DECAL), "K3DS_MAT_DECAL");
		chunkTypes.put(new Integer(K3DS_MAT_ADDITIVE), "K3DS_MAT_ADDITIVE");
		chunkTypes.put(new Integer(K3DS_MAT_WIRE), "K3DS_MAT_WIRE");
		chunkTypes.put(new Integer(K3DS_MAT_FACEMAP), "K3DS_MAT_FACEMAP");
		chunkTypes.put(new Integer(K3DS_MAT_PHONGSOFT), "K3DS_MAT_PHONGSOFT");
		chunkTypes.put(new Integer(K3DS_MAT_WIREABS), "K3DS_MAT_WIREABS");
		chunkTypes.put(new Integer(K3DS_MAT_WIRE_SIZE), "K3DS_MAT_WIRE_SIZE");
		chunkTypes.put(new Integer(K3DS_MAT_TEXMAP), "K3DS_MAT_TEXMAP");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_TEXT_DATA),
				"K3DS_MAT_SXP_TEXT_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_TEXMASK), "K3DS_MAT_TEXMASK");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_TEXTMASK_DATA),
				"K3DS_MAT_SXP_TEXTMASK_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_TEX2MAP), "K3DS_MAT_TEX2MAP");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_TEXT2_DATA),
				"K3DS_MAT_SXP_TEXT2_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_TEX2MASK), "K3DS_MAT_TEX2MASK");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_TEXT2MASK_DATA),
				"K3DS_MAT_SXP_TEXT2MASK_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_OPACMAP), "K3DS_MAT_OPACMAP");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_OPAC_DATA),
				"K3DS_MAT_SXP_OPAC_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_OPACMASK), "K3DS_MAT_OPACMASK");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_OPACMASK_DATA),
				"K3DS_MAT_SXP_OPACMASK_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_BUMPMAP), "K3DS_MAT_BUMPMAP");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_BUMP_DATA),
				"K3DS_MAT_SXP_BUMP_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_BUMPMASK), "K3DS_MAT_BUMPMASK");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_BUMPMASK_DATA),
				"K3DS_MAT_SXP_BUMPMASK_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_SPECMAP), "K3DS_MAT_SPECMAP");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_SPEC_DATA),
				"K3DS_MAT_SXP_SPEC_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_SPECMASK), "K3DS_MAT_SPECMASK");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_SPECMASK_DATA),
				"K3DS_MAT_SXP_SPECMASK_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_SHINMAP), "K3DS_MAT_SHINMAP");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_SHIN_DATA),
				"K3DS_MAT_SXP_SHIN_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_SHINMASK), "K3DS_MAT_SHINMASK");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_SHINMASK_DATA),
				"K3DS_MAT_SXP_SHINMASK_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_SELFIMAP), "K3DS_MAT_SELFIMAP");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_SELFI_DATA),
				"K3DS_MAT_SXP_SELFI_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_SELFIMASK), "K3DS_MAT_SELFIMASK");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_SELFIMASK_DATA),
				"K3DS_MAT_SXP_SELFIMASK_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_REFLMAP), "K3DS_MAT_REFLMAP");
		chunkTypes.put(new Integer(K3DS_MAT_REFLMASK), "K3DS_MAT_REFLMASK");
		chunkTypes.put(new Integer(K3DS_MAT_SXP_REFLMASK_DATA),
				"K3DS_MAT_SXP_REFLMASK_DATA");
		chunkTypes.put(new Integer(K3DS_MAT_ACUBIC), "K3DS_MAT_ACUBIC");
		chunkTypes.put(new Integer(K3DS_MAT_MAPNAME), "K3DS_MAT_MAPNAME");
		chunkTypes.put(new Integer(K3DS_MAT_MAP_TILING), "K3DS_MAT_MAP_TILING");
		chunkTypes.put(new Integer(K3DS_MAT_MAP_TEXBLUR),
				"K3DS_MAT_MAP_TEXBLUR");
		chunkTypes.put(new Integer(K3DS_MAT_MAP_USCALE), "K3DS_MAT_MAP_USCALE");
		chunkTypes.put(new Integer(K3DS_MAT_MAP_VSCALE), "K3DS_MAT_MAP_VSCALE");
		chunkTypes.put(new Integer(K3DS_MAT_MAP_UOFFSET),
				"K3DS_MAT_MAP_UOFFSET");
		chunkTypes.put(new Integer(K3DS_MAT_MAP_VOFFSET),
				"K3DS_MAT_MAP_VOFFSET");
		chunkTypes.put(new Integer(K3DS_MAT_MAP_ANG), "K3DS_MAT_MAP_ANG");
		chunkTypes.put(new Integer(K3DS_MAT_MAP_COL1), "K3DS_MAT_MAP_COL1");
		chunkTypes.put(new Integer(K3DS_MAT_MAP_COL2), "K3DS_MAT_MAP_COL2");
		chunkTypes.put(new Integer(K3DS_MAT_MAP_RCOL), "K3DS_MAT_MAP_RCOL");
		chunkTypes.put(new Integer(K3DS_MAT_MAP_GCOL), "K3DS_MAT_MAP_GCOL");
		chunkTypes.put(new Integer(K3DS_MAT_MAP_BCOL), "K3DS_MAT_MAP_BCOL");
		chunkTypes.put(new Integer(K3DS_NAMED_OBJECT), "K3DS_NAMED_OBJECT");
		chunkTypes.put(new Integer(K3DS_N_DIRECT_LIGHT), "K3DS_N_DIRECT_LIGHT");
		chunkTypes.put(new Integer(K3DS_DL_OFF), "K3DS_DL_OFF");
		chunkTypes.put(new Integer(K3DS_DL_OUTER_RANGE), "K3DS_DL_OUTER_RANGE");
		chunkTypes.put(new Integer(K3DS_DL_INNER_RANGE), "K3DS_DL_INNER_RANGE");
		chunkTypes.put(new Integer(K3DS_DL_MULTIPLIER), "K3DS_DL_MULTIPLIER");
		chunkTypes.put(new Integer(K3DS_DL_EXCLUDE), "K3DS_DL_EXCLUDE");
		chunkTypes.put(new Integer(K3DS_DL_ATTENUATE), "K3DS_DL_ATTENUATE");
		chunkTypes.put(new Integer(K3DS_DL_SPOTLIGHT), "K3DS_DL_SPOTLIGHT");
		chunkTypes.put(new Integer(K3DS_DL_SPOT_ROLL), "K3DS_DL_SPOT_ROLL");
		chunkTypes.put(new Integer(K3DS_DL_SHADOWED), "K3DS_DL_SHADOWED");
		chunkTypes.put(new Integer(K3DS_DL_LOCAL_SHADOW2),
				"K3DS_DL_LOCAL_SHADOW2");
		chunkTypes.put(new Integer(K3DS_DL_SEE_CONE), "K3DS_DL_SEE_CONE");
		chunkTypes.put(new Integer(K3DS_DL_SPOT_RECTANGULAR),
				"K3DS_DL_SPOT_RECTANGULAR");
		chunkTypes.put(new Integer(K3DS_DL_SPOT_ASPECT), "K3DS_DL_SPOT_ASPECT");
		chunkTypes.put(new Integer(K3DS_DL_SPOT_PROJECTOR),
				"K3DS_DL_SPOT_PROJECTOR");
		chunkTypes.put(new Integer(K3DS_DL_SPOT_OVERSHOOT),
				"K3DS_DL_SPOT_OVERSHOOT");
		chunkTypes.put(new Integer(K3DS_DL_RAY_BIAS), "K3DS_DL_RAY_BIAS");
		chunkTypes.put(new Integer(K3DS_DL_RAYSHAD), "K3DS_DL_RAYSHAD");
		chunkTypes.put(new Integer(K3DS_N_CAMERA), "K3DS_N_CAMERA");
		chunkTypes.put(new Integer(K3DS_CAM_SEE_CONE), "K3DS_CAM_SEE_CONE");
		chunkTypes.put(new Integer(K3DS_CAM_RANGES), "K3DS_CAM_RANGES");
		chunkTypes.put(new Integer(K3DS_OBJ_HIDDEN), "K3DS_OBJ_HIDDEN");
		chunkTypes.put(new Integer(K3DS_OBJ_VIS_LOFTER), "K3DS_OBJ_VIS_LOFTER");
		chunkTypes.put(new Integer(K3DS_OBJ_DOESNT_CAST),
				"K3DS_OBJ_DOESNT_CAST");
		chunkTypes.put(new Integer(K3DS_OBJ_DONT_RECVSHADOW),
				"K3DS_OBJ_DONT_RECVSHADOW");
		chunkTypes.put(new Integer(K3DS_OBJ_MATTE), "K3DS_OBJ_MATTE");
		chunkTypes.put(new Integer(K3DS_OBJ_FAST), "K3DS_OBJ_FAST");
		chunkTypes.put(new Integer(K3DS_OBJ_PROCEDURAL), "K3DS_OBJ_PROCEDURAL");
		chunkTypes.put(new Integer(K3DS_OBJ_FROZEN), "K3DS_OBJ_FROZEN");
		chunkTypes.put(new Integer(K3DS_N_TRI_OBJECT), "K3DS_N_TRI_OBJECT");
		chunkTypes.put(new Integer(K3DS_POINT_ARRAY), "K3DS_POINT_ARRAY");
		chunkTypes.put(new Integer(K3DS_POINT_FLAG_ARRAY),
				"K3DS_POINT_FLAG_ARRAY");
		chunkTypes.put(new Integer(K3DS_FACE_ARRAY), "K3DS_FACE_ARRAY");
		chunkTypes.put(new Integer(K3DS_MSH_MAT_GROUP), "K3DS_MSH_MAT_GROUP");
		chunkTypes.put(new Integer(K3DS_SMOOTH_GROUP), "K3DS_SMOOTH_GROUP");
		chunkTypes.put(new Integer(K3DS_MSH_BOXMAP), "K3DS_MSH_BOXMAP");
		chunkTypes.put(new Integer(K3DS_TEX_VERTS), "K3DS_TEX_VERTS");
		chunkTypes.put(new Integer(K3DS_MESH_MATRIX), "K3DS_MESH_MATRIX");
		chunkTypes.put(new Integer(K3DS_MESH_COLOR), "K3DS_MESH_COLOR");
		chunkTypes.put(new Integer(K3DS_MESH_TEXTURE_INFO),
				"K3DS_MESH_TEXTURE_INFO");
		chunkTypes.put(new Integer(K3DS_KF_DATA), "K3DS_KF_DATA");
		chunkTypes.put(new Integer(K3DS_KF_HDR), "K3DS_KF_HDR");
		chunkTypes.put(new Integer(K3DS_KF_SEG), "K3DS_KF_SEG");
		chunkTypes.put(new Integer(K3DS_KF_CURTIME), "K3DS_KF_CURTIME");
		chunkTypes.put(new Integer(K3DS_AMBIENT_NODE_TAG),
				"K3DS_AMBIENT_NODE_TAG");
		chunkTypes.put(new Integer(K3DS_OBJECT_NODE_TAG),
				"K3DS_OBJECT_NODE_TAG");
		chunkTypes.put(new Integer(K3DS_CAMERA_NODE_TAG),
				"K3DS_CAMERA_NODE_TAG");
		chunkTypes.put(new Integer(K3DS_TARGET_NODE_TAG),
				"K3DS_TARGET_NODE_TAG");
		chunkTypes.put(new Integer(K3DS_LIGHT_NODE_TAG), "K3DS_LIGHT_NODE_TAG");
		chunkTypes.put(new Integer(K3DS_L_TARGET_NODE_TAG),
				"K3DS_L_TARGET_NODE_TAG");
		chunkTypes.put(new Integer(K3DS_SPOTLIGHT_NODE_TAG),
				"K3DS_SPOTLIGHT_NODE_TAG");
		chunkTypes.put(new Integer(K3DS_NODE_ID), "K3DS_NODE_ID");
		chunkTypes.put(new Integer(K3DS_NODE_HDR), "K3DS_NODE_HDR");
		chunkTypes.put(new Integer(K3DS_PIVOT), "K3DS_PIVOT");
		chunkTypes.put(new Integer(K3DS_INSTANCE_NAME), "K3DS_INSTANCE_NAME");
		chunkTypes.put(new Integer(K3DS_MORPH_SMOOTH), "K3DS_MORPH_SMOOTH");
		chunkTypes.put(new Integer(K3DS_BOUNDBOX), "K3DS_BOUNDBOX");
		chunkTypes.put(new Integer(K3DS_POS_TRACK_TAG), "K3DS_POS_TRACK_TAG");
		chunkTypes.put(new Integer(K3DS_COL_TRACK_TAG), "K3DS_COL_TRACK_TAG");
		chunkTypes.put(new Integer(K3DS_ROT_TRACK_TAG), "K3DS_ROT_TRACK_TAG");
		chunkTypes.put(new Integer(K3DS_SCL_TRACK_TAG), "K3DS_SCL_TRACK_TAG");
		chunkTypes.put(new Integer(K3DS_MORPH_TRACK_TAG),
				"K3DS_MORPH_TRACK_TAG");
		chunkTypes.put(new Integer(K3DS_FOV_TRACK_TAG), "K3DS_FOV_TRACK_TAG");
		chunkTypes.put(new Integer(K3DS_ROLL_TRACK_TAG), "K3DS_ROLL_TRACK_TAG");
		chunkTypes.put(new Integer(K3DS_HOT_TRACK_TAG), "K3DS_HOT_TRACK_TAG");
		chunkTypes.put(new Integer(K3DS_FALL_TRACK_TAG), "K3DS_FALL_TRACK_TAG");
		chunkTypes.put(new Integer(K3DS_HIDE_TRACK_TAG), "K3DS_HIDE_TRACK_TAG");
		chunkTypes.put(new Integer(K3DS_POLY_2D), "K3DS_POLY_2D");
		chunkTypes.put(new Integer(K3DS_SHAPE_OK), "K3DS_SHAPE_OK");
		chunkTypes.put(new Integer(K3DS_SHAPE_NOT_OK), "K3DS_SHAPE_NOT_OK");
		chunkTypes.put(new Integer(K3DS_SHAPE_HOOK), "K3DS_SHAPE_HOOK");
		chunkTypes.put(new Integer(K3DS_PATH_3D), "K3DS_PATH_3D");
		chunkTypes.put(new Integer(K3DS_PATH_MATRIX), "K3DS_PATH_MATRIX");
		chunkTypes.put(new Integer(K3DS_SHAPE_2D), "K3DS_SHAPE_2D");
		chunkTypes.put(new Integer(K3DS_M_SCALE), "K3DS_M_SCALE");
		chunkTypes.put(new Integer(K3DS_M_TWIST), "K3DS_M_TWIST");
		chunkTypes.put(new Integer(K3DS_M_TEETER), "K3DS_M_TEETER");
		chunkTypes.put(new Integer(K3DS_M_FIT), "K3DS_M_FIT");
		chunkTypes.put(new Integer(K3DS_M_BEVEL), "K3DS_M_BEVEL");
		chunkTypes.put(new Integer(K3DS_XZ_CURVE), "K3DS_XZ_CURVE");
		chunkTypes.put(new Integer(K3DS_YZ_CURVE), "K3DS_YZ_CURVE");
		chunkTypes.put(new Integer(K3DS_INTERPCT), "K3DS_INTERPCT");
		chunkTypes.put(new Integer(K3DS_DEFORM_LIMIT), "K3DS_DEFORM_LIMIT");
		chunkTypes.put(new Integer(K3DS_USE_CONTOUR), "K3DS_USE_CONTOUR");
		chunkTypes.put(new Integer(K3DS_USE_TWEEN), "K3DS_USE_TWEEN");
		chunkTypes.put(new Integer(K3DS_USE_SCALE), "K3DS_USE_SCALE");
		chunkTypes.put(new Integer(K3DS_USE_TWIST), "K3DS_USE_TWIST");
		chunkTypes.put(new Integer(K3DS_USE_TEETER), "K3DS_USE_TEETER");
		chunkTypes.put(new Integer(K3DS_USE_FIT), "K3DS_USE_FIT");
		chunkTypes.put(new Integer(K3DS_USE_BEVEL), "K3DS_USE_BEVEL");
		chunkTypes.put(new Integer(K3DS_DEFAULT_VIEW), "K3DS_DEFAULT_VIEW");
		chunkTypes.put(new Integer(K3DS_VIEW_TOP), "K3DS_VIEW_TOP");
		chunkTypes.put(new Integer(K3DS_VIEW_BOTTOM), "K3DS_VIEW_BOTTOM");
		chunkTypes.put(new Integer(K3DS_VIEW_LEFT), "K3DS_VIEW_LEFT");
		chunkTypes.put(new Integer(K3DS_VIEW_RIGHT), "K3DS_VIEW_RIGHT");
		chunkTypes.put(new Integer(K3DS_VIEW_FRONT), "K3DS_VIEW_FRONT");
		chunkTypes.put(new Integer(K3DS_VIEW_BACK), "K3DS_VIEW_BACK");
		chunkTypes.put(new Integer(K3DS_VIEW_USER), "K3DS_VIEW_USER");
		chunkTypes.put(new Integer(K3DS_VIEW_CAMERA), "K3DS_VIEW_CAMERA");
		chunkTypes.put(new Integer(K3DS_VIEW_WINDOW), "K3DS_VIEW_WINDOW");
		chunkTypes.put(new Integer(K3DS_VIEWPORT_LAYOUT_OLD),
				"K3DS_VIEWPORT_LAYOUT_OLD");
		chunkTypes.put(new Integer(K3DS_VIEWPORT_DATA_OLD),
				"K3DS_VIEWPORT_DATA_OLD");
		chunkTypes.put(new Integer(K3DS_VIEWPORT_LAYOUT),
				"K3DS_VIEWPORT_LAYOUT");
		chunkTypes.put(new Integer(K3DS_VIEWPORT_DATA), "K3DS_VIEWPORT_DATA");
		chunkTypes.put(new Integer(K3DS_VIEWPORT_DATA_3),
				"K3DS_VIEWPORT_DATA_3");
		chunkTypes.put(new Integer(K3DS_VIEWPORT_SIZE), "K3DS_VIEWPORT_SIZE");
		chunkTypes.put(new Integer(K3DS_NETWORK_VIEW), "K3DS_NETWORK_VIEW");

	}
	/*
	 *  // .3DS file magic number static final int S3D_M3DMAGIC = 0x4d4d;
	 *  // Tag IDs
	 * 
	 * static final int S3D_MMAGIC = 0x3d3d; static final int S3D_MESH_VERSION =
	 * 0x0001; static final int S3D_M3D_VERSION = 0x0002;
	 * 
	 * 
	 * ////////////// New values static final int S3D_MESH_VERSION_2 = 0x3d3e;
	 * static final int S3D_LIN_COLOR_24 = 0x0012; static final int
	 * S3D_LIN_COLOR_F = 0x0013; static final int S3D_MESH_COLOR_IDX = 0x4165;//
	 * Visible State ? static final int S3D_MESH_TEX_INFO = 0x4170;//Map type &
	 * info static final int S3D_MESH_HIERARCHY = 0x4f00; static final int
	 * S3D_MAT_FALLOFF = 0xa052; //static final int S3D_ = 0x; ////////////////
	 * 
	 * static final int S3D_COLOR_F = 0x0010; static final int S3D_COLOR_24 =
	 * 0x0011; static final int S3D_INT_PERCENTAGE = 0x0030; static final int
	 * S3D_FLOAT_PERCENTAGE = 0x0031;
	 * 
	 * static final int S3D_MASTER_SCALE = 0x0100;
	 * 
	 * static final int S3D_BIT_MAP = 0x1100; static final int S3D_USE_BIT_MAP =
	 * 0x1101; static final int S3D_SOLID_BGND = 0x1200; static final int
	 * S3D_USE_SOLID_BGND = 0x1201; static final int S3D_V_GRADIENT = 0x1300;
	 * static final int S3D_USE_V_GRADIENT = 0x1301;
	 * 
	 * static final int S3D_LO_SHADOW_BIAS = 0x1400; static final int
	 * S3D_HI_SHADOW_BIAS = 0x1410; static final int S3D_SHADOW_MAP_SIZE =
	 * 0x1420; static final int S3D_SHADOW_SAMPLES = 0x1430; static final int
	 * S3D_SHADOW_RANGE = 0x1440;
	 * 
	 * static final int S3D_AMBIENT_LIGHT = 0x2100;
	 * 
	 * static final int S3D_FOG = 0x2200; static final int S3D_USE_FOG = 0x2201;
	 * static final int S3D_FOG_BGND = 0x2210; static final int S3D_DISTANCE_CUE =
	 * 0x2300; static final int S3D_USE_DISTANCE_CUE = 0x2301; static final int
	 * S3D_DCUE_BGND = 0x2310;
	 * 
	 * static final int S3D_DEFAULT_VIEW = 0x3000; static final int S3D_VIEW_TOP =
	 * 0x3010; static final int S3D_VIEW_BOTTOM = 0x3020; static final int
	 * S3D_VIEW_LEFT = 0x3030; static final int S3D_VIEW_RIGHT = 0x3040; static
	 * final int S3D_VIEW_FRONT = 0x3050; static final int S3D_VIEW_BACK =
	 * 0x3060; static final int S3D_VIEW_USER = 0x3070; static final int
	 * S3D_VIEW_CAMERA = 0x3080; static final int S3D_VIEW_WINDOW = 0x3090;
	 * 
	 * static final int S3D_NAMED_OBJECT = 0x4000; static final int
	 * S3D_OBJ_HIDDEN = 0x4010; static final int S3D_OBJ_VIS_LOFTER = 0x4011;
	 * static final int S3D_OBJ_DOESNT_CAST = 0x4012; static final int
	 * S3D_OBJ_MATTE = 0x4013;
	 * 
	 * static final int S3D_N_TRI_OBJECT = 0x4100;
	 * 
	 * static final int S3D_POINT_ARRAY = 0x4110; static final int
	 * S3D_POINT_FLAG_ARRAY = 0x4111; static final int S3D_FACE_ARRAY = 0x4120;
	 * static final int S3D_MSH_MAT_GROUP = 0x4130; static final int
	 * S3D_TEX_VERTS = 0x4140; static final int S3D_SMOOTH_GROUP = 0x4150;
	 * static final int S3D_MESH_MATRIX = 0x4160;
	 * 
	 * static final int S3D_N_DIRECT_LIGHT = 0x4600; static final int
	 * S3D_DL_SPOTLIGHT = 0x4610; static final int S3D_DL_OFF = 0x4620; static
	 * final int S3D_DL_SHADOWED = 0x4630;
	 * 
	 * static final int S3D_N_CAMERA = 0x4700;
	 * 
	 *  // Material file Chunk IDs
	 * 
	 * static final int S3D_MAT_ENTRY = 0xafff; static final int S3D_MAT_NAME =
	 * 0xa000; static final int S3D_MAT_AMBIENT = 0xa010; static final int
	 * S3D_MAT_DIFFUSE = 0xa020; static final int S3D_MAT_SPECULAR = 0xa030;
	 * static final int S3D_MAT_SHININESS = 0xa040; static final int
	 * S3D_MAT_SHININESS_STRENGTH = 0xa041; static final int
	 * S3D_MAT_TRANSPARENCY = 0xa050; static final int S3D_MAT_WIRE = 0xa085;
	 * static final int S3D_MAT_WIRESIZE = 0xa087; static final int
	 * S3D_MAT_SELF_ILLUM = 0xa080; static final int S3D_MAT_TWO_SIDE = 0xa081;
	 * static final int S3D_MAT_DECAL = 0xa082; static final int
	 * S3D_MAT_ADDITIVE = 0xa083;
	 * 
	 * static final int S3D_MAT_SHADING = 0xa100;
	 * 
	 * 
	 * static final int S3D_MAT_TEXMAP = 0xa200; static final int
	 * S3D_MAT_OPACMAP = 0xa210; static final int S3D_MAT_REFLMAP = 0xa220;
	 * static final int S3D_MAT_BUMPMAP = 0xa230;
	 * 
	 * static final int S3D_MAT_MAPNAME = 0xa300;
	 * 
	 *  // Reverse engineered hierarchy information
	 * 
	 * static final int S3D_HIERARCHY = 0xb000; static final int
	 * S3D_HIERARCHY_NODE = 0xb002; static final int S3D_HIERARCHY_LINK =
	 * 0xb010; static final int S3D_INSTANCE_NAME = 0xb011; static final int
	 * S3D_PIVOT = 0xb013; static final int S3D_POS_TRACK_TAG = 0xb020; static
	 * final int S3D_ROT_TRACK_TAG = 0xb021; static final int S3D_SCL_TRACK_TAG =
	 * 0xb022; static final int S3D_NODE_ID = 0xb030; static final int
	 * S3D_OBJECT_LINK_NULL = 0xffff;
	 * 
	 *  // Dummy Chunk ID
	 * 
	 * static final int S3D_DUMMY_CHUNK = 0xffff;
	 * 
	 *  // These chunks are found in the .PRJ file
	 * 
	 * static final int S3D_PROJECT_FILE = 0xc23d; static final int
	 * S3D_MAPPING_RETILE = 0xc4b0; static final int S3D_MAPPING_CENTRE =
	 * 0xc4c0; static final int S3D_MAPPING_SCALE = 0xc4d0; static final int
	 * S3D_MAPPING_ORIENTATION = 0xc4e1;
	 * 
	 * 
	 * private void loadChunkTypes() { chunkTypes = new TreeMap();
	 * 
	 * chunkTypes.put( new Integer(0x4d4d),"S3D_M3DMAGIC");
	 * 
	 *  // Tag IDs
	 * 
	 * chunkTypes.put( new Integer(0x3d3d),"S3D_MMAGIC"); chunkTypes.put( new
	 * Integer(0x0001),"S3D_MESH_VERSION"); chunkTypes.put( new
	 * Integer(0x0002),"S3D_M3D_VERSION");
	 * 
	 * chunkTypes.put( new Integer(0x0010),"S3D_COLOR_F"); chunkTypes.put( new
	 * Integer(0x0011),"S3D_COLOR_24"); chunkTypes.put( new
	 * Integer(0x0030),"S3D_INT_PERCENTAGE"); chunkTypes.put( new
	 * Integer(0x0031),"S3D_FLOAT_PERCENTAGE");
	 * 
	 * chunkTypes.put( new Integer(0x0100),"S3D_MASTER_SCALE");
	 * 
	 * chunkTypes.put( new Integer(0x1100),"S3D_BIT_MAP"); chunkTypes.put( new
	 * Integer(0x1101),"S3D_USE_BIT_MAP"); chunkTypes.put( new
	 * Integer(0x1200),"S3D_SOLID_BGND"); chunkTypes.put( new
	 * Integer(0x1201),"S3D_USE_SOLID_BGND"); chunkTypes.put( new
	 * Integer(0x1300),"S3D_V_GRADIENT"); chunkTypes.put( new
	 * Integer(0x1301),"S3D_USE_V_GRADIENT");
	 * 
	 * chunkTypes.put( new Integer(0x1400),"S3D_LO_SHADOW_BIAS");
	 * chunkTypes.put( new Integer(0x1410),"S3D_HI_SHADOW_BIAS");
	 * chunkTypes.put( new Integer(0x1420),"S3D_SHADOW_MAP_SIZE");
	 * chunkTypes.put( new Integer(0x1430),"S3D_SHADOW_SAMPLES");
	 * chunkTypes.put( new Integer(0x1440),"S3D_SHADOW_RANGE");
	 * 
	 * chunkTypes.put( new Integer(0x2100),"S3D_AMBIENT_LIGHT");
	 * 
	 * chunkTypes.put( new Integer(0x2200),"S3D_FOG"); chunkTypes.put( new
	 * Integer(0x2201),"S3D_USE_FOG"); chunkTypes.put( new
	 * Integer(0x2210),"S3D_FOG_BGND"); chunkTypes.put( new
	 * Integer(0x2300),"S3D_DISTANCE_CUE"); chunkTypes.put( new
	 * Integer(0x2301),"S3D_USE_DISTANCE_CUE"); chunkTypes.put( new
	 * Integer(0x2310),"S3D_DCUE_BGND");
	 * 
	 * chunkTypes.put( new Integer(0x3000),"S3D_DEFAULT_VIEW"); chunkTypes.put(
	 * new Integer(0x3010),"S3D_VIEW_TOP"); chunkTypes.put( new
	 * Integer(0x3020),"S3D_VIEW_BOTTOM"); chunkTypes.put( new
	 * Integer(0x3030),"S3D_VIEW_LEFT"); chunkTypes.put( new
	 * Integer(0x3040),"S3D_VIEW_RIGHT"); chunkTypes.put( new
	 * Integer(0x3050),"S3D_VIEW_FRONT"); chunkTypes.put( new
	 * Integer(0x3060),"S3D_VIEW_BACK"); chunkTypes.put( new
	 * Integer(0x3070),"S3D_VIEW_USER"); chunkTypes.put( new
	 * Integer(0x3080),"S3D_VIEW_CAMERA"); chunkTypes.put( new
	 * Integer(0x3090),"S3D_VIEW_WINDOW");
	 * 
	 * chunkTypes.put( new Integer(0x4000),"S3D_NAMED_OBJECT"); chunkTypes.put(
	 * new Integer(0x4010),"S3D_OBJ_HIDDEN"); chunkTypes.put( new
	 * Integer(0x4011),"S3D_OBJ_VIS_LOFTER"); chunkTypes.put( new
	 * Integer(0x4012),"S3D_OBJ_DOESNT_CAST"); chunkTypes.put( new
	 * Integer(0x4013),"S3D_OBJ_MATTE");
	 * 
	 * chunkTypes.put( new Integer(0x4100),"S3D_N_TRI_OBJECT");
	 * 
	 * chunkTypes.put( new Integer(0x4110),"S3D_POINT_ARRAY"); chunkTypes.put(
	 * new Integer(0x4111),"S3D_POINT_FLAG_ARRAY"); chunkTypes.put( new
	 * Integer(0x4120),"S3D_FACE_ARRAY"); chunkTypes.put( new
	 * Integer(0x4130),"S3D_MSH_MAT_GROUP"); chunkTypes.put( new
	 * Integer(0x4140),"S3D_TEX_VERTS"); chunkTypes.put( new
	 * Integer(0x4150),"S3D_SMOOTH_GROUP"); chunkTypes.put( new
	 * Integer(0x4160),"S3D_MESH_MATRIX");
	 * 
	 * chunkTypes.put( new Integer(0x4600),"S3D_N_DIRECT_LIGHT");
	 * chunkTypes.put( new Integer(0x4610),"S3D_DL_SPOTLIGHT"); chunkTypes.put(
	 * new Integer(0x4620),"S3D_DL_OFF"); chunkTypes.put( new
	 * Integer(0x4630),"S3D_DL_SHADOWED");
	 * 
	 * chunkTypes.put( new Integer(0x4700),"S3D_N_CAMERA");
	 * 
	 *  // Material file Chunk IDs
	 * 
	 * chunkTypes.put( new Integer(0xafff),"S3D_MAT_ENTRY"); chunkTypes.put( new
	 * Integer(0xa000),"S3D_MAT_NAME"); chunkTypes.put( new
	 * Integer(0xa010),"S3D_MAT_AMBIENT"); chunkTypes.put( new
	 * Integer(0xa020),"S3D_MAT_DIFFUSE"); chunkTypes.put( new
	 * Integer(0xa030),"S3D_MAT_SPECULAR"); chunkTypes.put( new
	 * Integer(0xa040),"S3D_MAT_SHININESS"); chunkTypes.put( new
	 * Integer(0xa041),"S3D_MAT_SHININESS_STRENGTH "); chunkTypes.put( new
	 * Integer(0xa050),"S3D_MAT_TRANSPARENCY"); chunkTypes.put( new
	 * Integer(0xa085),"S3D_MAT_WIRE"); chunkTypes.put( new
	 * Integer(0xa087),"S3D_MAT_WIRESIZE"); chunkTypes.put( new
	 * Integer(0xa080),"S3D_MAT_SELF_ILLUM"); chunkTypes.put( new
	 * Integer(0xa081),"S3D_MAT_TWO_SIDE"); chunkTypes.put( new
	 * Integer(0xa082),"S3D_MAT_DECAL"); chunkTypes.put( new
	 * Integer(0xa083),"S3D_MAT_ADDITIVE");
	 * 
	 * chunkTypes.put( new Integer(0xa100),"S3D_MAT_SHADING");
	 * 
	 * 
	 * chunkTypes.put( new Integer(0xa200),"S3D_MAT_TEXMAP"); chunkTypes.put(
	 * new Integer(0xa210),"S3D_MAT_OPACMAP"); chunkTypes.put( new
	 * Integer(0xa220),"S3D_MAT_REFLMAP"); chunkTypes.put( new
	 * Integer(0xa230),"S3D_MAT_BUMPMAP");
	 * 
	 * chunkTypes.put( new Integer(0xa300),"S3D_MAT_MAPNAME");
	 * 
	 *  // Reverse engineered hierarchy information chunkTypes.put( new
	 * Integer(0xb000),"S3D_HIERARCHY"); chunkTypes.put( new
	 * Integer(0xb002),"S3D_HIERARCHY_NODE"); chunkTypes.put( new
	 * Integer(0xb010),"S3D_HIERARCHY_LINK"); chunkTypes.put( new
	 * Integer(0xb011),"S3D_INSTANCE_NAME"); chunkTypes.put( new
	 * Integer(0xb013),"S3D_PIVOT"); chunkTypes.put( new
	 * Integer(0xb020),"S3D_POS_TRACK_TAG"); chunkTypes.put( new
	 * Integer(0xb021),"S3D_ROT_TRACK_TAG"); chunkTypes.put( new
	 * Integer(0xb022),"S3D_SCL_TRACK_TAG"); chunkTypes.put( new
	 * Integer(0xb030),"S3D_NODE_ID"); chunkTypes.put( new
	 * Integer(0xffff),"S3D_OBJECT_LINK_NULL");
	 *  // Dummy Chunk ID chunkTypes.put( new
	 * Integer(0xffff),"S3D_DUMMY_CHUNK");
	 *  // These chunks are found in the .PRJ file chunkTypes.put( new
	 * Integer(0xc23d),"S3D_PROJECT_FILE"); chunkTypes.put( new
	 * Integer(0xc4b0),"S3D_MAPPING_RETILE"); chunkTypes.put( new
	 * Integer(0xc4c0),"S3D_MAPPING_CENTRE"); chunkTypes.put( new
	 * Integer(0xc4d0),"S3D_MAPPING_SCALE"); chunkTypes.put( new
	 * Integer(0xc4e1),"S3D_MAPPING_ORIENTATION");
	 * 
	 * 
	 * ///// New values chunkTypes.put( new
	 * Integer(S3D_MESH_VERSION_2),"S3D_MESH_VERSION_2"); chunkTypes.put( new
	 * Integer(S3D_LIN_COLOR_24),"S3D_LIN_COLOR_24"); chunkTypes.put( new
	 * Integer(S3D_LIN_COLOR_F),"S3D_LIN_COLOR_F"); chunkTypes.put( new
	 * Integer(S3D_MESH_COLOR_IDX),"S3D_MESH_COLOR_IDX"); chunkTypes.put( new
	 * Integer(S3D_MESH_TEX_INFO),"S3D_MESH_TEX_INFO"); chunkTypes.put( new
	 * Integer(S3D_MESH_HIERARCHY),"S3D_MESH_HIERARCHY"); chunkTypes.put( new
	 * Integer(S3D_MAT_FALLOFF),"S3D_MAT_FALLOFF"); }
	 */
	public static void main(String[] args) {
		Loader3DS obj = new Loader3DS();
		Set keys = obj.chunkTypes.keySet();
		Iterator it = keys.iterator();
		PrintStream out = System.out;
		while (it.hasNext()) {
			Integer i = (Integer) it.next();
			out.println("0x" + Integer.toHexString(i.intValue()) + "\t= "
					+ obj.chunkTypes.get(i));
		}

	}
}