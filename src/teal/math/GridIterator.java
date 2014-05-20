/* $Id: GridIterator.java,v 1.5 2010/04/09 16:57:35 pbailey Exp $ */

package teal.math;
 

/**
* provides an interface for classes that iterate over a rectangular
* region. Positions are scanned left-to-right, top-to-bottom, ie.
* 0,0 - x,0 -- y,0 - y,x. the simple implimentations return position 
* information, but it is planned that specific implementations may return
* the data found at the location.
*
*/

public interface GridIterator extends VectorIterator
{
    /** resolution is the total number of positions reported across the region */
    public int getResolutionX();   
    public int getResolutionY();
    public void setResolutionX(int resX);   
    public void setResolutionY(int resY);
    public void setResolution(int resX,int resY);
    /** the data source distance between two points in the X direction */
    public double getDX();
    /** the data source distance between two points in the Y direction */
    public double getDY();
}
