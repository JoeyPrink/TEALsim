/* A point in a plot.

@Author: Edward A. Lee
@Version: $Id: PlotPoint.java,v 1.2 2007/07/16 22:04:50 pbailey Exp $

@Copyright (c) 1997-2002 The Regents of the University of California.
All rights reserved.

Permission is hereby granted, without written agreement and without
license or royalty fees, to use, copy, modify, and distribute this
software and its documentation for any purpose, provided that the
above copyright notice and the following two paragraphs appear in all
copies of this software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
ENHANCEMENTS, OR MODIFICATIONS.

                                                PT_COPYRIGHT_VERSION_2
                                                COPYRIGHTENDKEY
@ProposedRating Yellow (cxh@eecs.berkeley.edu)
@AcceptedRating Yellow (cxh@eecs.berkeley.edu)
*/
package teal.plot.ptolemy;

import java.io.*;

//////////////////////////////////////////////////////////////////////////
//// PlotPoint
/**
A simple structure for storing a plot point.
@author Edward A. Lee
@version $Id: PlotPoint.java,v 1.2 2007/07/16 22:04:50 pbailey Exp $
@since Ptolemy II 0.2
*/
public class PlotPoint implements Serializable {

    ///////////////////////////////////////////////////////////////////
    ////                         public variables                  ////

    private static final long serialVersionUID = 3256438123096322100L;

    /** True if this point is connected to the previous point by a line. */
    public boolean connected = false;

    /** True if the yLowEB and yHighEB fields are valid. */
    public boolean errorBar = false;

    /** Original value of x before wrapping. */
    public double originalx;

    /** X value after wrapping (if any). */
    public double x;

    /** Y value. */
    public double y;

    /** Error bar Y low value. */
    public double yLowEB;

    /** Error bar Y low value. */
    public double yHighEB;
}
