package teal.render.jme;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Line;
import com.jme.scene.TexCoords;
import com.jme.scene.UserDataManager;
import com.jme.scene.VBOInfo;
import com.jme.scene.state.RenderState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

public class SharedLine extends Line {

	private static final Logger logger = Logger.getLogger(SharedLine.class
            .getName());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Line target;
	
	public SharedLine() {
		super();
		defaultColor = null;
	}
	
	public SharedLine(Line target) {
		this(target.getName(), target);
	}
	
	public SharedLine(String name, Line target) {
		super(name);
        if (target instanceof SharedLine) {
            setTarget(((SharedLine) target).getTarget());
            this.setName(target.getName());
            this.setCullHint(target.getCullHint());
            this.setLightCombineMode(target.getLightCombineMode());
            this.getLocalRotation().set(target.getLocalRotation());
            this.getLocalScale().set(target.getLocalScale());
            this.getLocalTranslation().set(target.getLocalTranslation());
            this.setRenderQueueMode(target.getRenderQueueMode());
            this.setTextureCombineMode(target.getTextureCombineMode());
            this.setZOrder(target.getZOrder());
            this.setDefaultColor(target.getDefaultColor());
            for (RenderState.StateType type : RenderState.StateType.values()) {
                RenderState state = target.getRenderState( type );
                if (state != null) {
                    this.setRenderState(state );
                }
            }
        } else {
            setTarget(target);
        }
        this.localRotation.set(target.getLocalRotation());
        this.localScale.set(target.getLocalScale());
        this.localTranslation.set(target.getLocalTranslation());
	}
	
	
	public void setTarget(Line target) {
		this.target = target;
        UserDataManager.getInstance().bind(this, target);
        for (RenderState.StateType type : RenderState.StateType.values()) {
            RenderState state = this.target.getRenderState( type );
            if (state != null) {
                setRenderState(state);
            }
        }

        setCullHint(target.getLocalCullHint());
        setLightCombineMode(target.getLocalLightCombineMode());
        setRenderQueueMode(target.getLocalRenderQueueMode());
        setTextureCombineMode(target.getLocalTextureCombineMode());
        setZOrder(target.getZOrder());
	}
	
	Line getTarget() {
		return this.target;
	}
	
	@Override
	public void generateIndices() {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
	}
	
    @Override
    public void reconstruct(FloatBuffer vertices, FloatBuffer normals,
            FloatBuffer colors, TexCoords textureCoords) {
    	if(target != null)
    		target.reconstruct(vertices, normals, colors, textureCoords);
    }
    
    @Override
    public void setVBOInfo(VBOInfo info) {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
    }
    
    /**
     * <code>getVBOInfo</code> returns the target line's vbo info.
     */
    @Override
    public VBOInfo getVBOInfo() {
        return target.getVBOInfo();
    }
    
    @Override
    public void setSolidColor(ColorRGBA color) {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
    }
    
    @Override
    public void setRandomColors() {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
    }

    @Override
    public FloatBuffer getVertexBuffer() {
        return target.getVertexBuffer();
    }
    
    @Override
    public void setVertexBuffer(FloatBuffer buff) {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
    }

    @Override
    public int getVertexCount() {
        return target.getVertexCount();
    }
    
    @Override
    public FloatBuffer getNormalBuffer() {
        return target.getNormalBuffer();
    }
    
    @Override
    public void setNormalBuffer(FloatBuffer buff) {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
    }
    
    @Override
    public FloatBuffer getColorBuffer() {
        return target.getColorBuffer();
    }  
    
    @Override
    public void setColorBuffer(FloatBuffer buff) {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
    }
    
    @Override
    public IntBuffer getIndexBuffer() {
        return target.getIndexBuffer();
    }
    
    @Override
    public void setIndexBuffer(IntBuffer indices) {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
    }
    
    @Override
    public int getTriangleCount() {
        return target.getTriangleCount();
    }
    
    @Override
    public void copyTextureCoordinates(int fromIndex, int toIndex, float factor) {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
    }
    
    @Override
    public ArrayList<TexCoords> getTextureCoords() {
        return target.getTextureCoords();
    }
    
    @Override
    public TexCoords getTextureCoords(int textureUnit) {
        return target.getTextureCoords(textureUnit);
    }
    
    @Override
    public void clearBuffers() {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
    }
    
    @Override
    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(target, "target", null);
        super.write(e);
    }

    @Override
    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        target = (Line) capsule.readSavable("target", null);
        super.read(e);
    }
    
    @Override
    public Vector3f randomVertex(Vector3f fill) {
        return target.randomVertex(fill);
    }

    @Override
    public void setTextureCoords(TexCoords buff) {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
    }

    @Override
    public void setTextureCoords(TexCoords buff, int position) {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
    }

    @Override
    public void setTangentBuffer(FloatBuffer tangentBuf) {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
    }

    @Override
    public FloatBuffer getTangentBuffer() {
        return target.getTangentBuffer();
    }

    @Override
    public void setBinormalBuffer(FloatBuffer binormalBuf) {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");
    }

    @Override
    public FloatBuffer getBinormalBuffer() {
        return target.getBinormalBuffer();
    }
    
    @Override
    public void updateWorldBound() {
        if (target.getModelBound() != null) {
            worldBound = target.getModelBound().transform(getWorldRotation(),
                    getWorldTranslation(), getWorldScale(), worldBound);
        }
    }

    @Override
    public void setModelBound(BoundingVolume modelBound) {
        target.setModelBound(modelBound);
    }

    
    @Override
    public void updateModelBound() {
        if (target.getModelBound() != null) {
            target.updateModelBound();
            updateWorldBound();
        }
    }
    
    @Override
    public BoundingVolume getModelBound() {
        return target.getModelBound();
    }

    @Override
    public void draw(Renderer r) {
        if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this))
                return;
        }
        target.getWorldTranslation().set(getWorldTranslation());
        target.getWorldRotation().set(getWorldRotation());
        target.getWorldScale().set(getWorldScale());
        target.setDefaultColor(getDefaultColor());
        target.setGlowColor(getGlowColor());
        target.setGlowEnabled(isGlowEnabled());
        target.setGlowScale(getGlowScale());
        target.setRenderQueueMode(getRenderQueueMode());
        target.setLightState(getLightState());
        target.setMode(getMode());
        System.arraycopy(this.states, 0, target.states, 0, states.length);
        
        r.draw(target);
    }
    
    @Override
    public void lockMeshes(Renderer r) {
        target.lockMeshes(r);
    }

    @Override
    public boolean hasDirtyVertices() {
        return target.hasDirtyVertices();
    }

    @Override
    public ColorRGBA getDefaultColor() {
        if (defaultColor == null) {
            return target.getDefaultColor();
        } else {
            return defaultColor;
        }
    }
    

    @Override
    public void appendCircle(float radius, float x, float y, int segments,
            boolean insideOut) {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");    	
    }

    @Override
    public boolean isAntialiased() {
        return target.isAntialiased();
    }

    public void setAntialiased(boolean antialiased) {
        logger.warning("SharedLine does not allow the manipulation"
                + "of the the line data.");    	
    }
    
    public Mode getMode() {
        return target.getMode();
    } 

    public void setMode(Mode mode) {
    	logger.warning("SharedLine does not allow the manipulation"
    			+ "of the the line data.");    	
    }
    
    public float getLineWidth() {
        return target.getLineWidth();
    }

    public void setLineWidth(float lineWidth) {
    	logger.warning("SharedLine does not allow the manipulation"
    			+ "of the the line data.");    	
    }

    public short getStipplePattern() {
        return target.getStipplePattern();
    }

    public void setStipplePattern(short stipplePattern) {
    	logger.warning("SharedLine does not allow the manipulation"
    			+ "of the the line data.");  
    }
    
    public int getStippleFactor() {
    	return target.getStippleFactor();
    }

    public void setStippleFactor(int stippleFactor) {
    	logger.warning("SharedLine does not allow the manipulation"
    			+ "of the the line data.");  
    }

}
