/* $Id: TeachSpinLV.java,v 1.19 2010/09/22 15:48:11 pbailey Exp $ */

/**
 * A demonstration integration of SimLab and a network connection to a LabView
 * experiment. This version uses a FIFO que to buffer network TCP messages from
 * the labView application.
 * 
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.19 $
 */

package tealsim.physics.ilab;

import isocket.IDataSocketC;
import isocket.DataReadyListener;
import isocket.DataReadyEvent;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import teal.render.BoundingSphere;
import javax.vecmath.Point3d;

import teal.app.*;
import teal.config.Teal;
import teal.field.Field;
import teal.field.Potential;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.physics.em.BField;
import teal.sim.control.VisualizationControl;
import teal.sim.spatial.FieldDirectionGrid;
import teal.sim.spatial.FieldLine;
import teal.util.*;
import teal.visualization.dlic.DLIC;
import tealsim.physics.em.TeachSpinBase;



public class TeachSpinLV extends TeachSpinBase implements DataReadyListener {

	private static final long serialVersionUID = 3904682678141007927L;
 
	final static int NOT_SET = 0;
	final static int COIL_STATE = 1;
	final static int SWITCH_STATE = 2;
	final static int CONNECTION_STATE = 4;
	final static int OPPOSITE_SW = 8;
	final static int SAME_SW = 16;
	final static int TOP_SW = 32;

	// Connection to LabView

	String lvHost = "ni-ilabs.mit.edu";
	int basePort = 43970;
	
	IDataSocketC dataSocket = null;
	TealAction socketState = null;
	boolean socketsRunning = false;
	FIFO que = null;
	BucketB bb = null;
	boolean cacheMode = false;
	private FieldDirectionGrid fv;
	
	public TeachSpinLV() {
		this("ni-ilabs.mit.edu", 43970, 2, 60, -1, MODEL);
	}

	public TeachSpinLV(String hostname, int port, int numLines, double coilsPerRing, int debugLevel, int lod) {

		super(numLines, coilsPerRing, debugLevel, lod);
		SouthGUI gui = new SouthGUI();
		setGui(gui);
		//theGUI.setPreferredSize(new Dimension(500,600));
		
		que = new FIFO(16, 8);
		TDebug.setGlobalLevel(debugLevel);
		TDebug.println(0, "Force on a Dipole LabView host: " + hostname + ":" + port);
		BoundingSphere bs = new BoundingSphere(new Point3d(0., 0, 0.), 0.250);
		setBoundingArea(bs);
		theScene.setBoundingArea(bs);
		setViewerSize(new Dimension(400,400));
		mDLIC.setComputePlane(new RectangularPlane(bs));
		title = "TEALsim Force on a Dipole-LabVIEW";
		lvHost = hostname;
		basePort = port;
		mSEC.setVisible(false);
		
		fv = new FieldDirectionGrid();
		fv.setType(Field.B_FIELD);
		fv.setDrawn(false);
		
		VisualizationControl vizPanel = new VisualizationControl();
		vizPanel.setFieldConvolution(mDLIC);
		vizPanel.setConvolutionModes(DLIC.DLIC_FLAG_B | DLIC.DLIC_FLAG_BP);
		vizPanel.setFieldLineManager(fmanager);
		vizPanel.setFieldVisGrid(fv);
		vizPanel.setShowFV(false);
		vizPanel.setExpanded(false);
		vizPanel.setColorPerVertex(false);
		//fmanager.setColor(Teal.DefaultEFieldLineColor);
		addElement(vizPanel);
		
		addActions();

		//mSEC.init();
		 if(theEngine != null)
		theEngine.requestRefresh();
		theGUI.refresh();

	}
	public String getHost(){
		return lvHost;
	}
	
	
	public void setHost(String host){
		lvHost = host;
	}
	public int getPort(){
		return basePort;
	}
	public void setPort(int port){
		basePort = port;
	}
	public double getNumCoils(){
		return numCoils;
	}
	public void setNumCoils(double num){
		numCoils = num;
	}

	public void setState(int flags) {

		switch (flags) {
			case 0 :
				TDebug.println(0, "No Switches are set!");
				//mFramework.getStatusBar().setText("No Switches are set!", false);
				break;
			case OPPOSITE_SW :
				setOpposite(true);
				break;
			case SAME_SW :
				setSame(true);
				break;
			case TOP_SW :
				setTopOnly(true);
				break;
			case OPPOSITE_SW | SAME_SW :
				TDebug.println(0, "Switch Error: opposite & same are set!");
				//mFramework.getStatusBar().setText("Switch Error: opposite & same are set!", false);
				break;
			case OPPOSITE_SW | TOP_SW :
				TDebug.println(0, "Switch Error: opposite & topOnly are set!");
				//mFramework.getStatusBar().setText("Switch Error: opposite & topOnly are set!", false);
				break;
			case SAME_SW | TOP_SW :
				TDebug.println(0, "Switch Error: Same & topOnly are set!");
				//mFramework.getStatusBar().setText("Switch Error: Same & topOnly are set!", false);
				break;
			case OPPOSITE_SW | SAME_SW | TOP_SW :
				TDebug.println(0, "Switch Error: All are set!");
				//mFramework.getStatusBar().setText("Switch Error: All are set!", false);
				break;
			default :
				break;
		}
	}
	
	public void startSocketforWL() {
		System.out.println("heere");
		startSockets();
		System.out.println("heere");
	}
	
	public void stopSocketforWL() {
		try {
			stopSockets();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private synchronized boolean startSockets() {
		boolean status = false;
		dataSocket = new IDataSocketC(lvHost, basePort);
		
		System.out.println("heere");
		if (dataSocket.isConnected()) {
			
		dataSocket.addDataReadyListener(this);
		dataSocket.start();
		bb = new BucketB(cacheMode);
		bb.start();
		status = true;
		//TDebug.println(1, "dataSocket Priority: " + dataSocket.getPriority());

		//dataSocket.setPriority(10);
		//TDebug.println("dataSocket Priority: " +dataSocket.getPriority());
		//if(dataSocket.isConnected())
		//   TDebug.println(0, "Data socket started: " +
		// dataSocket.getConnectedPort());

			socketState.setName("Disconnect");
			socketState.setActionCommand("Disconnect");
			socketsRunning = true;
			TDebug.println(0, "Sockets started");
			System.out.println("socket started");
			//mFramework.getStatusBar().setText("Connection started", true);
		} else {
			dataSocket = null;
			TDebug.println(-1, "Error connecting to " + lvHost + ":" + basePort);
			//mFramework.getStatusBar().setText("Error connecting to " + lvHost, false);
		}
		return status;
	}

	private synchronized boolean stopSockets() throws Throwable 
	{
		boolean status = false;
		if(bb != null)
            bb.setContinue(false);
		if (dataSocket != null) {
			dataSocket.removeDataReadyListener(this);
			if (dataSocket.isConnected()) {
				dataSocket.write('z');
				dataSocket.shutdown();
			}
			dataSocket = null;
		}
		if (bb != null) {
			bb = null;
		}
		if(que != null)
			que.clear();
		
		//socketState.putValue("Name","Connect");
		socketState.setName("Connect");
		socketState.setActionCommand("Connect");
		socketsRunning = false;
		TDebug.println(0, "Connection shutdown");
		//mFramework.getStatusBar().setText("Connection shutdown", true);
		status = true;
		return status;
	}

	public void reset() {
		try {
			stopSockets();
		} catch (Throwable th) {
			TDebug.printThrown(-1, th, "Error on stopSockets()");
		}
		m1.setPosition(magPos);
		mFramework.displayBounds();
		mDLIC.setVisible(false);
		 if(theEngine != null)
		theEngine.requestRefresh();
	}

	public void dataReady(DataReadyEvent dbe) {
		int dataType = NOT_SET;
		String tag = null;
		Double val;
		//ISocket source = (ISocket) dbe.getSource();
		//TDebug.print(-1, "Port: " + source.getConnectedPort() + "\t");
		TDebug.println(1, "DBE created at: " + dbe.time);
		ArrayList<?> data = (ArrayList<?>) dbe.getData();
		if (data == null)
			return;
		int count = data.size();
		double py = 0.;
		double tmpCurrent = 0.;
		long time = 0L;
		int swState = 0;
		boolean connected = false;
		for (int i = 0; i < count; i++) {
			tag = (String) data.get(i++);
			val = (Double) data.get(i);
			TDebug.println(1, "\t'" + tag + "' = " + val);

			if (tag.compareToIgnoreCase("current") == 0) {
				dataType = COIL_STATE;
				tmpCurrent = -val.doubleValue();
			} else if (tag.compareToIgnoreCase("posy") == 0) {
				py = val.doubleValue() / 1000.;
			} else if (tag.compareToIgnoreCase("time") == 0) {
				time = val.longValue();
			} else if (tag.compareToIgnoreCase("same") == 0) {
				dataType = SWITCH_STATE;
				if (val.doubleValue() > 2)
					swState |= SAME_SW;
			} else if (tag.compareToIgnoreCase("opposite") == 0) {
				if (val.doubleValue() > 2)
					swState |= OPPOSITE_SW;
			} else if (tag.compareToIgnoreCase("top") == 0) {
				if (val.doubleValue() > 2)
					swState |= TOP_SW;
			} else if (tag.compareToIgnoreCase("status") == 0) {
				dataType = CONNECTION_STATE;
				connected = (val.doubleValue() > 2);
			}
		}
		if (dataType == COIL_STATE) {
			DataRec rec = new DataRec(time, py, tmpCurrent);
			que.add(rec);
			TDebug.println(1, "Que size = " + que.size());
		} else if (dataType == SWITCH_STATE) {
			setState(swState);
		} else if (dataType == CONNECTION_STATE) {
			if (!connected) {
				try {
					stopSockets();
				} 
				catch (Throwable t) {
				}
			}
		}
		 if(theEngine != null)
		theEngine.requestRefresh();
		//mViewer.render();
	}

	void addActions() {
		socketState = new TealAction("Connect", this);
		addAction("Actions", socketState);
		TealAction a = new TealAction("Magnetic Field", this);
		addAction("Actions",a);
		a = new TealAction("Magnetic Potential", this);
		addAction("Actions",a);
		a = new TealAction("Force on a Dipole", this);
		addAction("Help",a);
		a= new TealAction("About", this);
		addAction("Help",a);
			 
		//super.addActions();
	}

	public void actionPerformed(ActionEvent e) {
//		TDebug.println(3, "ActionComamand: " + e.getActionCommand());
//		TDebug.println(3, "Action: " + e.paramString());
//		TDebug.println(3, "Source: " + e.getSource());
//		TDebug.println(3, "socketState: " + socketState);
		
		// Need to overload this action to deal with the data stream
		if (e.getActionCommand().compareToIgnoreCase("Magnetic Field") == 0) {
			try {				
				stopSockets();  
//				mDLIC.setField(theEngine.getBField());
				mDLIC.setField(theEngine.getElementByType(BField.class));
				mDLIC.generateFieldImage();
			} catch (Throwable th) {

			}
			//processData = false;
			super.actionPerformed(e);
		}else if (e.getActionCommand().compareToIgnoreCase("Magnetic Potential") == 0) {
			try {				
				stopSockets();
//				mDLIC.setField(new Potential(theEngine.getBField()));
				mDLIC.setField(new Potential(theEngine.getElementByType(BField.class)));
				mDLIC.generateFieldImage();
			} 
			catch (Throwable th) {
			}
			//processData = false;
			super.actionPerformed(e);
		}else if (e.getActionCommand().compareToIgnoreCase("Connect") == 0) {
			
				try {
					startSockets();
				} 
				catch (Throwable ioe) {
				}
			}
		else if (e.getActionCommand().compareToIgnoreCase("Disconnect") == 0) {
				try {
						stopSockets();  
				} catch (Throwable ioe) {
					TDebug.println("Error Stopping sockets: " + ioe.getMessage());
				}
			} 
		else if (e.getActionCommand().compareToIgnoreCase("Force on a Dipole") == 0) 
        {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/ForceOnDipoleLabVIEW.html");
        	}
        } 
		else if (e.getActionCommand().compareToIgnoreCase("About") == 0) 
        {
        	if(mFramework != null) {
        		mFramework.displayMessage("TeachSpinLV Version 1.10", false);
        	}
        } 
		
		else {
			super.actionPerformed(e);
		}
	}

	public synchronized void dispose() {
		try {
			stopSockets();
		} catch (Throwable t) {
			TDebug.printThrown(-1, t);
		}
		super.dispose();
	}

	class DataRec {
		long time;
		double posy;
		double value;

		DataRec(long t, double py, double val) {
			time = t;
			posy = py;
			value = val;
		}
	}

	class BucketB extends Thread {
		long waitTime = 0L;
		long cycleTime = 10L;
		long lastTime = Long.MAX_VALUE;
		long frameStart = Long.MAX_VALUE;
		long curTime = 0L;
		boolean doContinue = true;
		DataRec curRec = null;
        boolean cacheMode = false;
		
		BucketB(boolean pruneCache)
		{
			super();
			cacheMode = pruneCache;
		}

        public void setContinue(boolean b)
        {
            doContinue = b;
        }

		public void run()
		{
			while (doContinue)
			{
				try
				{
					if (que.hasNext())
					{
						curTime = System.currentTimeMillis();
						if (!cacheMode)
						{
							curRec = (DataRec) que.next();
							waitTime = curRec.time - (lastTime + 10L);
							if (waitTime > 0)
							{
								sleep(waitTime);
							}
						}
						else
						{
							int count = 0;
							while (que.hasNext())
							{
								curRec = (DataRec) que.next();
								count++;
								waitTime = curRec.time - (lastTime + 10L);
								waitTime -= (curTime - frameStart);
								TDebug.println(1, "WaitTime: "+ waitTime);
								if (waitTime >= 0)
								{
									TDebug.println(1," Count = " + count);
									if(count <= 1)
										sleep(waitTime);
									break;
								}
							}
						}
						// Send a new info block
						frameStart =  curTime;
						m1.setY(curRec.posy);
						setCurrent(curRec.value);
						lastTime = curRec.time;
						TDebug.println(1, "Frame at: " + curTime);
					}
					else
					{
						sleep(cycleTime);
					}
				} catch (InterruptedException ie)
				{
					TDebug.println("run iterrupted");
				}	
			}
		}
	}

}
