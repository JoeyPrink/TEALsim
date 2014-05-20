/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: GridNode.java,v 1.15 2010/04/12 20:13:17 stefan Exp $ 
 * 
 */

package teal.render.j3d;


import java.nio.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import teal.math.*;
import teal.render.j3d.geometry.GeomUtil;
import teal.util.*;

/**
 * 
 *
 * @author Phil Bailey
 * @version $Revision: 1.15 $
 * not used?  jwb
 **/
 
/**
* this provides a general Node for representing
* a rectangular grid of information, an indexed 
* geometry array will be used for the geometry.
*/

public class GridNode extends ShapeNode
{

    public static Color3f [] defaultColors;
    private static int numDefaultColors = 32;

    static 
    {
        defaultColors = new Color3f[numDefaultColors];
        double dRad = (Math.PI * 2.0)/ (double) numDefaultColors;
        double rad = 0.;
        int j = 0;
        for(int i= 0;i<numDefaultColors;i++)
        {
            defaultColors[j++] = new Color3f((float) Math.sin(rad),(float) Math.cos(rad),0.5f);
            rad += dRad;
        }
    }  


    protected int type = GeomUtil.POINT;
    protected double scale = 320000.0;
    
    protected Geometry geometry = null;
    protected Appearance apperance = null;
    
    /** the actual vertexes */
    protected float coords[] = null;  
    
    /** the vertex colors */
    protected float colors[] = null;
    protected int numColors;
    
    /** the index only updated upon geometry creation */
    protected int coordIdx [] = null;
    
    /** for each index  the vertex color to use??  */
    protected int colorIdx [] = null;
    
    protected FloatBuffer coordNio = null;
    protected FloatBuffer colorNio = null;
    protected IntBuffer colorIdxNio = null;
   

    
    int width;
    int height;
    
    public GridNode()
    {
        super();
        //numColors = numDefaultColors;
        //colors = defaultColors;
    }

public GridNode( int resX,int resY)
{
    this();
    width = resX;
    height = resY;
   
    //geometry =  makeGeometry(resolution,resolution);
    //mShape.setGeometry((Geometry) geometry,0);
    //mShape.setAppearance(GridNode.makeAppearance(Color.WHITE,1.0f,0.5f,false));
    
} 
public GridNode( int res)
{
    this(res,res);
}

public void setResolution(int x, int y)
{
    width = x;
    height = y;
}

public void setType(int t)
{
    type = t;
}
public int getType()
{
    return type;
}

public void checkGeometry(int w, int h)
{
    //TDebug.println("checkGometry()");
    boolean ok = true;
    if (geometry== null)
    {
            ok = false;
    }
    else if ((w != width) || (h != height))
    {
        // this should cause a remove node error
        if (mShape != null)
        {
            mShape.removeGeometry(0);
            geometry = null;
            ok = false;
        }
    }
    if (!ok)
    {   
        geometry =  makeGeometry(w,h); 
        //width = w;
        //height = h;  
        mShape.setGeometry((Geometry) geometry,0); 
    }   
}

Geometry makeGeometry(int resolution)
{
    return makeGeometry(resolution,resolution);
}

Geometry makeGeometry(int w,int h)
{
    width = w;
    height = h;
    TDebug.println(1,"makeGeometry Type = " +type + " w= " + width + "  h= " + height);
    int numVec = width * height;
    coords = new float[numVec * 3];
    colors = new float[numVec * 3];
       
    coordNio = FloatBuffer.wrap(coords);
    colorNio = FloatBuffer.wrap(colors);
    TDebug.println(1,"coordNio: isDirect = " + coordNio.isDirect() + " hasArray = " + coordNio.hasArray());
  

    

    float dummy [] = {0f,0f,0f};
    float dCol [] = new float[3];
    for( int i =0;i< numVec;i++)
    {       
        coordNio.put(dummy);
        defaultColors[i % numDefaultColors].get(dCol);
        //TDebug.println(i + " Color= " + defaultColors[i % numDefaultColors]);
        colorNio.put(dCol);
        
    }
     IndexedGeometryArray ga = null;
    
    int idxCount = 0;
    int offset = 0;
    int idx = 0;
    int c =0;
    int n = 0;
    int m = 0;
 
    int flags = GeometryArray.COORDINATES| GeometryArray.COLOR_3;
    switch (type)
    {
        case GeomUtil.LINE:
            idxCount  =  width * height * 2;
            TDebug.println(1,"LINE: idxCount = " + idxCount);
            
            coordIdx = new int[idxCount];
            colorIdx = new int[idxCount];
            int strips[] = new int[width + height];
            int j = 0;
            int off = 0;
            int vecOff = 0;
            for(int i = 0; i < height; i++)
            {
                strips[j++] = width;
                //TDebug.print("Strip: " + (j-1) + " len: " + width  + ": \t");
                for(int k = 0; k < width;k++)
                {
                    //TDebug.print(vecOff + ", ");
                    coordIdx[off] = vecOff;
                    colorIdx [off++]= vecOff++;
                   
                }
                //TDebug.println("");
            }
            for(int i = 0; i< width;i++)
            {   vecOff = i;
                strips[j++] = height;
                //TDebug.print("Strip: " + (j-1) + " len: " + height +  ": \t");
                for(int k = 0;k < height;k++)
                {
                    //TDebug.print(vecOff + ", ");
                    coordIdx[off] = vecOff;
                    colorIdx[off++] = vecOff;
                    vecOff += width;
                }
                 //TDebug.println("");
            }
            
           // TDebug.println("Strips complete");
            
           
            ga = new IndexedLineStripArray(numVec,flags, idxCount,strips);
   
            break;
        case GeomUtil.QUAD:    
            idxCount = (width -1) * (height -1)* 4;
            coordIdx = new int[idxCount];
            colorIdx = new int[idxCount];
            for(int r= 0; r < height -1;r++)
            {
                n = offset;
                offset += width;
                m = offset;
                for (c = 0;c < width-1;c++)
                {
                    coordIdx[idx] = n;  
                    colorIdx[idx++] = n++; 
                    coordIdx[idx] = m;          
                    colorIdx[idx++] = m++;
                    
                    coordIdx[idx] = m;
                    colorIdx[idx++] = m;
                    coordIdx[idx] = n;
                    colorIdx[idx++] = n;
                }
            }
            ga = new IndexedQuadArray(numVec, flags, idxCount + 4);
            Appearance app  = new Appearance();
            PolygonAttributes polyAttribs  = new PolygonAttributes( PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE, 0,false );
            app.setPolygonAttributes( polyAttribs );
            mShape.setAppearance(app);
        
            break;
        case GeomUtil.POINT:
        default:
            coordIdx = new int[numVec];
            colorIdx = new int[numVec];
            //System.out.println("LINE: idxCount = " + numVec);
            for(int i= 0; i < numVec;i++)
            {
                coordIdx[i] = i;
                colorIdx[i] = i; 
            }   
            ga = new IndexedPointArray(numVec,flags,numVec);
          break;
    }
    ga.setCapability(GeometryArray.ALLOW_COUNT_READ);
    ga.setCapability(GeometryArray.ALLOW_COORDINATE_READ);   
    ga.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
    ga.setCapability(GeometryArray.ALLOW_COLOR_READ);   
    ga.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
    ga.setCapability(IndexedGeometryArray.ALLOW_COORDINATE_INDEX_READ);   
    ga.setCapability(IndexedGeometryArray.ALLOW_COORDINATE_INDEX_WRITE);  
    ga.setCapability(IndexedGeometryArray.ALLOW_COLOR_INDEX_READ);   
    ga.setCapability(IndexedGeometryArray.ALLOW_COLOR_INDEX_WRITE);             
    
    ga.setCoordinates(0,coords);
    ga.setColors(0,colors);
    ga.setCoordinateIndices(0,coordIdx);
    ga.setColorIndices(0,colorIdx);
 
    return ga;
}


    /**
    * Provides a simple interface for updating the node, the GridIterator 
    * is expected to supply the actual x,y & z values used within the grid.
    */
   public void updateGeometry(GridIterator gItor,boolean setColor)
   {
        //TDebug.println("updateGEometry: x= " + gItor.getResolutionX() + " y = " + gItor.getResolutionY());
        Vector3f val = new Vector3f();
        float col [] = new float[3];
        gItor.reset();
        reset();
        checkGeometry(gItor.getResolutionX(),gItor.getResolutionX());
        coordNio.rewind();
        reset();
        while (gItor.hasNext())
        {
            //TDebug.println("coordNio pos = " + coordNio.position());
            val.set(gItor.nextVec());
            coordNio.put(val.x);
            coordNio.put(val.y);
            coordNio.put(val.z);
            if (setColor)
            {
                defaultColors[( (int) Math.abs((val.z * 100.))) % numDefaultColors].get(col);
                colorNio.put( col  );
            }
        }
        refresh(setColor);
   }
   
   public void assignColor(float val)
   {
    float col[] = new float[3];
        //TDebug.println("vertex[" + colorIdxNio.position() +"]  val= " + val + " colIdx = " + ( (int) (val * scale)) % numColors);
           defaultColors[( (int) (val * 320.0)) % numDefaultColors].get(col);
           
                colorNio.put(col);
   }
    
   public void reset()
   {
    coordNio.rewind();
    colorNio.rewind();
    //TDebug.println("coordNio: pos= " + coordNio.position() + " limit =" + coordNio.limit() + " Cap= " + coordNio.capacity());
    //TDebug.println("colorNio: pos= " + colorNio.position() + " limit =" + colorNio.limit() + " Cap= " + colorNio.capacity());
   }
   public void put(float []data,boolean setColor)
   {
        coordNio.put(data);
        if (setColor)
        {
            assignColor(data[2]);
        }
   }
   public void put(Tuple3f value,boolean setColor)
    {

        coordNio.put(value.x);
        coordNio.put(value.y);
        coordNio.put(value.z);
        if (setColor)
        {
            assignColor(value.z);
        }
    }
   public void put(float a, float b, float c, boolean setColor)
    {
        coordNio.put(a);
        coordNio.put(b);
        coordNio.put(c);
        if (setColor)
        {
            assignColor(c);
        }
    }
    
    public void refresh()
    {
        refresh(false);
    }
    
    public void refresh(boolean setColor)
    {
        ((GeometryArray)geometry).setCoordinates(0,coords);
        if(setColor)
            ((IndexedGeometryArray)geometry).setColors(0,colors);
    }

}
