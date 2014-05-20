 /* $Id: Circuits.java,v 1.7 2010/08/10 18:12:33 stefan Exp $ */

package tealsim.physics.circuits;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.vecmath.*;

import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.plot.*;
import teal.render.BoundingSphere;
import teal.render.viewer.TViewer;
import teal.sim.control.VisualizationControl;
import teal.sim.engine.EngineControl;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.physics.em.Circuit;
import teal.ui.swing.*;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;


public class Circuits extends SimWorld {

	private static final long serialVersionUID = 3257004367155705400L;
    
    // Circuit-related global parameters.
	Circuit circuit = null;
	Circuit.Battery battery1 = null, battery2 = null, battery3 = null;
	Circuit.Resistor resistor1 = null, resistor2 = null, resistor3 = null;
	Circuit.Capacitor capacitor1 = null, capacitor2 = null, capacitor3 = null;;
	double E1, E2, E3, R1, R2, R3, C1, C2, C3, L, I0, V1, V2, V3;
	final double almost = 0.999999999999999; 
	double a, mu0, epsilon0, stepSize, energyQuantum, t1, t2;
	int Nsamples, Nparticles, placementType ; 
	private FieldConvolution mDLIC;

	// Potential plot objects.
	Graph graph = null;
	CircuitPotentialPlot plot = null;
	boolean plotPotential = true;
	
	public Circuits() {
		super();
		title = "Circuits";
		TDebug.setGlobalLevel(0);
//		 Building the world.
//		EMEngine engine = new EMEngine();
		BoundingSphere bs = new BoundingSphere(new Point3d(), 16);
		setBoundingArea(bs);
		setDamping(0.);
		setGravity(new Vector3d(0., 0., 0.));
		addPropertyChangeListener(this);
//		setEngine(engine);

		//		mViewer.setNavigationMode(TViewer.ORBIT | TViewer.VP_ZOOM);
		setNavigationMode(TViewer.ORBIT_ALL);
		


		// Circuit initialization.
		// ***********************************************************************
		circuit = new Circuit();

		// Various circuit scenarios.
		// ***********************************************************************
		// Case: ERC
		a				= 1.;			// Inductance
		mu0				= 10.;			L	= 0.; // Circuit.mu0*Math.PI*a*a;
		epsilon0		= 1.;			I0	= 0.;
		stepSize		= 0.2;
		Nsamples		= 64;
		energyQuantum	= 0.01;
		Nparticles		= 7;
		placementType	= Circuit.NONEDGE;
		// Batteries
		t1 = -Math.PI/2.-Math.PI/10.;
		t2 = -Math.PI/2.+Math.PI/10.;
		E1 = 1.;
		battery1 = circuit.new Battery(t1, t2, E1);
		// Resistors
		t1 = 0.;
		t2 = Math.PI/10.;
		R1 = 20.;
		resistor1 = circuit.new Resistor(t1, t2, R1);
		// Capacitors
		t1 = Math.PI-Math.PI/10.;
		t2 = Math.PI;
		C1 = 10.;
		V1 = 0.;
		circuit.setCharge(C1*V1);
		capacitor1 = circuit.new Capacitor(t1, t2, C1);
		// ***********************************************************************/
		/*/ Case: ERL
		a				= 1.;			// Inductance
		mu0				= 1.;			L	=  Circuit.mu0*Math.PI*a*a;
		epsilon0		= 1.;			I0	= 0.;
		stepSize		= 0.02;
		Nsamples		= 128;
		energyQuantum	= 0.05;
		Nparticles		= 7;
		placementType	= Circuit.EDGE;
		// Batteries
		t1 = -Math.PI/2.-Math.PI/10.;
		t2 = -Math.PI/2.+Math.PI/10.;
		E1 = 1.;
		battery1 = circuit.new Battery(t1, t2, E1);
		// Resistors
		t1 = 0.;
		t2 = Math.PI/10.;
		R1 = 1.;
		resistor1 = circuit.new Resistor(t1, t2, R1);
		// Capacitors
		// ***********************************************************************/
		/*/ Case: LC
		a				= 1.;			// Inductance
		mu0				= 1.;			L	= Circuit.mu0*Math.PI*a*a;
		epsilon0		= 1.;			I0	= 1.; //0.;
		stepSize		= 0.02;
		Nsamples		= 32;
		energyQuantum	= 0.01;
		Nparticles		= 10;
		placementType	= Circuit.EDGE;
		// Batteries
		// Resistors
		t1 = Math.PI/2.-Math.PI/10.;
		t2 = Math.PI/2.+Math.PI/10.;
		R1 = 0.1;
		//resistor1 = circuit.new Resistor(t1, t2, R1);
		// Capacitors
		t1 = Math.PI-Math.PI/10.;
		t2 = Math.PI+Math.PI/10.;
		C1 = 1.;
		V1 = 0.; // Math.sqrt(L);
		circuit.setCharge(C1*V1);
		capacitor1 = circuit.new Capacitor(t1, t2, C1);
		capacitor1.setGeometryFactor(0.6);
		// ***********************************************************************/
		/*/ Case: RC
		a				= 1.;			// Inductance
		mu0				= 10.;			L	= 0.; // Circuit.mu0*Math.PI*a*a;
		epsilon0		= 1.;			I0	= 0.;
		stepSize		= 0.2;
		Nsamples		= 64;
		energyQuantum	= 0.01;
		Nparticles		= 7;
		placementType	= Circuit.NONEDGE;
		// Batteries
		// Resistors
		t1 = Math.PI/2.-Math.PI/10.;
		t2 = Math.PI/2.+Math.PI/10.;
		R1 = 20.;
		resistor1 = circuit.new Resistor(t1, t2, R1);
		// Capacitors
		t1 = -Math.PI/2.-Math.PI/10.;
		t2 = -Math.PI/2.+Math.PI/10.;
		C1 = 10.;
		V1 = 1.;
		circuit.setCharge(C1*V1);
		capacitor1 = circuit.new Capacitor(t1, t2, C1);
		// ***********************************************************************/

		// Circuit assembly.
		theEngine.setDeltaTime(stepSize);
		Circuit.mu0 = mu0;
		Circuit.epsilon0 = epsilon0;
		circuit.setDirection(new Vector3d(0.,0.,1.));
		circuit.setRadius(a);
		circuit.setInductance(L);
		circuit.setCurrent(I0);
		circuit.setNsamples(Nsamples);
		circuit.placeComponent(battery1);
		circuit.placeComponent(battery2);
		circuit.placeComponent(battery3);
		circuit.placeComponent(resistor1);
		circuit.placeComponent(resistor2);
		circuit.placeComponent(resistor3);
		circuit.placeComponent(capacitor1);
		circuit.placeComponent(capacitor2);
		circuit.placeComponent(capacitor3);
		
		circuit.setEnergyQuantum(energyQuantum);
		circuit.setNparticles(Nparticles);
		circuit.setPlacementType(placementType);
		circuit.needsSpatial();
		addElement(circuit);

		// Potential plot.
		if( plotPotential ) {
			graph = new Graph();
			theGUI.addTElement(graph);
			graph.setBounds(20,500,160,250);
	        graph.setXRange(0, circuit.getNsamples()-1);
	        graph.setYRange(-1, 1);
			graph.setWrap(false);
			graph.setClearOnWrap(false);
	        graph.setXLabel("Discrete Angle");
	        graph.setYLabel("Potential"); 
			plot = new CircuitPotentialPlot();
			plot.setCircuit(circuit);
			graph.addPlotItem(plot);
			//addElement(graph);
		}
		
		// Add battery button
		TealAction ta = new TealAction("Add Battery", "ADD_BATTERY", this);
		JButton bAddBattery = new JButton(ta);
		//bAddBattery.setBounds(20,890,160,24);
		
		// Remove battery button
		ta = new TealAction("Remove Battery", "REMOVE_BATTERY", this);
		JButton bRemoveBattery = new JButton(ta);
		//bRemoveBattery.setBounds(20,860,460,24);
		
//		// Initialize button.
//		ta = new TealAction("Initialize", "INIT", this);
//		JButton bInitialize = new JButton(ta);
//		//bInitialize.setBounds(20,740,460,24);
		
		// Grass seeds button.
		//ta = new TealAction("Grass Seeds", "DLIC_E", this);
		//JButton bGrassSeeds = new JButton(ta);
		//bGrassSeeds.setBounds(20,770,460,24);

		// Iron filings button.
		//ta = new TealAction("Iron Filings", "DLIC_B", this);
		//JButton bIronFilings = new JButton(ta);
		//bIronFilings.setBounds(20,800,460,24);

		// Electrostatic potential button.
		//ta = new TealAction("Electrostatic Potential", "DLIC_EPOTENTIAL", this);
		//JButton bElectrostaticPotential = new JButton(ta);
		//bElectrostaticPotential.setBounds(20,830,460,24);

		JTaskPane taskPane = new JTaskPane();

		JTaskPaneGroup controls = new JTaskPaneGroup();
		controls.setText("Controls");
		//controls.add(bInitialize);
		controls.add(bAddBattery);
		controls.add(bRemoveBattery);
		taskPane.add(controls);
		
		mDLIC = new FieldConvolution();
		mDLIC.setSize(new Dimension(512,512));
		mDLIC.setComputePlane(new RectangularPlane(bs));
		 VisualizationControl vis = new VisualizationControl();
	     vis.setFieldConvolution(mDLIC);
	     vis.setConvolutionModes(DLIC.DLIC_FLAG_B|DLIC.DLIC_FLAG_BP | DLIC.DLIC_FLAG_E | DLIC.DLIC_FLAG_EP);
	     
		addElement(vis);
		
		JTaskPaneGroup graphPanel = new JTaskPaneGroup();
		graphPanel.setText("Graphs");
		graphPanel.add(graph);
		taskPane.add(graphPanel);
		
		JScrollPane scroll = new JScrollPane(taskPane);
		scroll.setBorder(null);
		scroll.setPreferredSize(new Dimension(400,800));
		addElement(scroll);

		// Launch
		addActions();
		reset();
		resetCamera();
		mSEC.init();
		//mSEC.start();
	}

	void addActions() {
		TealAction ta = new TealAction("Circuits", this);
		addAction("Help", ta);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		System.out.println("Action: " + command);
		if (e.getActionCommand().compareToIgnoreCase("Circuits") == 0) {
			mFramework.openBrowser("resources/help/circuits.html");
//		} else if (e.getActionCommand().compareToIgnoreCase("INIT") == 0) {
//			System.out.println("Initializing particle system...");
//			//mSEC.threadSuspend();
//			circuit.initializeParticleSystem();
//			//mSEC.threadResume();
		} else if (e.getActionCommand().compareToIgnoreCase("REMOVE_BATTERY") == 0) {
			System.out.println("Removing Battery");
			mSEC.stop();
			while(mSEC.getSimState()!= EngineControl.PAUSED);
			circuit.removeComponent(battery1);
			plot.doPlot(graph);
			//mSEC.start();
		} else if (e.getActionCommand().compareToIgnoreCase("ADD_BATTERY") == 0) {
			System.out.println("Add Battery");
			mSEC.stop();
			while(mSEC.getSimState()!= EngineControl.PAUSED);
			circuit.placeComponent(battery1);
			plot.doPlot(graph);
			//mSEC.start();
		} else {
			super.actionPerformed(e);
		}
	}

	public void propertyChange(PropertyChangeEvent pce) {
		if( pce.getSource() == theEngine && pce.getPropertyName().equalsIgnoreCase("simState")) {
			if( mSEC.getSimState() == EngineControl.PAUSED) {
				System.out.println("Paused - Time: " + theEngine.getTime());
				circuit.inform();
			}
		}
		super.propertyChange(pce);
	}

	public void reset() {
		circuit.setCurrent(I0);
		// It must be that C1*V1 = C2*V2 = C3*V3
		if(capacitor1!=null) circuit.setCharge(C1*V1);
		if(capacitor2!=null) circuit.setCharge(C2*V2);
		if(capacitor3!=null) circuit.setCharge(C3*V3);
		circuit.resetParticleSystem();
		// circuit.initializeParticleSystem();
		
		double alpha = 0.99;
		double N =(alpha*alpha)*0.5 * L * I0 * I0 / energyQuantum;
		double A = Math.PI * a * a;
		double gridwidth = Math.sqrt(A/N);
		circuit.uniformlyPlace(gridwidth, alpha);
		if(plotPotential) plot.doPlot(graph);
		//resetCamera();
	}

	public void resetCamera() {
		Point3d from = new Point3d(0., 0., 7.);
		Point3d to = new Point3d(0., 0., 0.);
		Vector3d up = new Vector3d(0., 1., 0.);
		from.scale(0.05);
		to.scale(0.05);
		setLookAt(from, to, up);
	}
	
	
}
