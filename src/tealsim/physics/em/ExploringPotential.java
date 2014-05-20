/* $Id: ExploringPotential.java,v 1.21 2010/09/28 21:40:41 pbailey Exp $ */
/**
 * @author John Belcher 
 * Revision: 1.0 $
 */

package tealsim.physics.em;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.util.Random;

import teal.render.BoundingSphere;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.config.Teal;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.physics.em.EField;
import teal.physics.em.PointCharge;
import teal.physics.em.SimEM;
import teal.physics.physical.Ball;
import teal.physics.physical.Wall;
import teal.render.Rendered;
import teal.render.primitives.Line;
import teal.render.primitives.Sphere;
import teal.render.scene.Model;
import teal.sim.control.VisualizationControl;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldVector;
import teal.ui.UIPanel;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;



public class ExploringPotential extends SimEM {

    private static final long serialVersionUID = 3257008735204554035L;
	Ball ball;
	double arrowScale = 0.05;
    double pointChargeRadius = 0.1;
	
	FieldVector theArrow;
	JTextArea messages;
	boolean clearText = false;
	
	Vector3d curPos = null;
	KeyAdapter kListener;
	UIPanel gameControls;
	 ButtonGroup optionsGroup;
	 JRadioButton rad1;

     
     JRadioButton rad2;       
     JRadioButton rad3;
     double potpower = .2;
    double widthtotal = 5.;
    int halfwall =12;  // this is approximately half the number of squares on a side
	int nwall = 2*halfwall+1;  // this the number of squares on a side, it is odd by construction
    double sizewall = widthtotal/nwall;  // this is the length of one square, so if we have 25 squares of length 0.2, the total length will be 5.
    int wrongGuess = 40;
    
    Wall[][] wall;

    boolean[][] visited;
    int chargeCount;
    int numMoves=0;
    int idxX = 0;
	int idxY = 0;
	int max = 0;
	int count = 0;
	double refPowerPotential = 0.;
	double scale = 0.35;
	double PotentialScale = 0.;
	double maxPotential;
	double minPotential;
    Random rand;
    
    PointCharge pcA;
    PointCharge pcB;
    protected FieldConvolution mDLIC = null;


    /** An imported 3DS object (an icon for the observer).  */
    Rendered observer;
    Rendered iconobserver;
    Rendered arrowE;
    
   
	
    public ExploringPotential() {
        super();

        TDebug.setGlobalLevel(0);

        title = "Exploring Potential";
        
		///// Set properties on the SimEngine /////
		// Bounding area represents the characteristic size of the space.
		// setDeltaTime() sets the time step of the simulation.
		// setDamping() sets the damping on the system.
        
        BoundingSphere bs = new BoundingSphere(new Point3d(0, 1.6, 0), 03.5);
        setBoundingArea(bs);
        setDeltaTime(0.005); 
        setBoundingArea(bs);
        setBackgroundColor(new Color(37*4,49*4,255));
              
        mDLIC = new FieldConvolution();
        RectangularPlane rec =  new RectangularPlane(new Vector3d(-widthtotal/2.,-widthtotal/2., 0.),
				new Vector3d(-widthtotal/2.,widthtotal/2.,0.), new Vector3d(widthtotal/2.,widthtotal/2.,0.));
        mDLIC.setSize(new Dimension(512,512));
        mDLIC.setComputePlane(rec);
        mSEC.rebuildPanel(0);
        //addElement(mDLIC);
        rand = new Random();
        
        // We create a two D array of walls.
		// Wall constructor.  	
        
        wall = new Wall[nwall][nwall];
        visited = new boolean[nwall][nwall];
        for (int i = 0; i < nwall; i++) 
        {
        	for (int j = 0; j < nwall; j++) 
        	{
        		Vector3d posnow = new Vector3d(getPosition(i,j));
        		wall[i][j]= new Wall(getPosition(i,j), 
                		new Vector3d(0., sizewall, 0.), new Vector3d(sizewall, 0., 0.));
        		addElement(wall[i][j])	;
        		//System.out.println(" i " + i +  " j " + j + " wall.pos " + posnow );
        	} 	
       }
        
 
        
        
        max = nwall -1;
        theArrow = new FieldVector();
		theArrow.setColor(Teal.PointChargePositiveColor);
		theArrow.setArrowScale(.75);
		theArrow.setDrawn(false);
        addElement(theArrow);
        
        // add four lines to outline the base square
        
        Line one = new Line(new Vector3d(-widthtotal/2.,-widthtotal/2., 0.), new Vector3d(-widthtotal/2.,widthtotal/2., 0.));
        one.setColor(Color.white);
        addElement(one);
        Line two = new Line(new Vector3d(-widthtotal/2.,-widthtotal/2., 0.), new Vector3d(widthtotal/2.,-widthtotal/2., 0.));
        two.setColor(Color.white);
        addElement(two);
        Line three = new Line(new Vector3d(widthtotal/2.,widthtotal/2., 0.), new Vector3d(-widthtotal/2.,widthtotal/2., 0.));
        three.setColor(Color.white);
        addElement(three);
        Line four = new Line(new Vector3d(widthtotal/2.,widthtotal/2., 0.), new Vector3d(widthtotal/2.,-widthtotal/2., 0.));
        four.setColor(Color.white);
        addElement(four);
        
        double radius = widthtotal/24.;
        double offset = widthtotal/2.+ radius/Math.sqrt(2.);
        Ball marker;
        marker = new Ball();
        marker.setRadius(radius);
        marker.setColor(Color.green);
        marker.setPosition(new Vector3d(-offset,-offset,0.));
        marker.setMoveable(false);
        TDebug.println(1,"POS: " + marker.getPosition());
        addElement(marker);
        
        marker = new Ball();
        marker.setColor(Color.blue);
        marker.setRadius(radius);
        marker.setPosition(new Vector3d(offset,-offset,0.));
        marker.setMoveable(false);
        TDebug.println(1,"POS: " + marker.getPosition());
        addElement(marker);
        marker = new Ball();
        marker.setRadius(radius);
        marker.setColor(Color.red);
        marker.setPosition(new Vector3d(-offset,offset,0.));
        marker.setMoveable(false);
        TDebug.println(1,"POS: " + marker.getPosition());
        addElement(marker);
        marker = new Ball();
        marker.setRadius(radius);
        marker.setColor(Color.yellow);
        marker.setPosition(new Vector3d(offset,offset,0.));
        marker.setMoveable(false);
        TDebug.println(1,"POS: " + marker.getPosition());
        addElement(marker);
        
        
        // Additional UI Controls
        GridBagLayout gbl =new GridBagLayout();
        GridBagConstraints con = new GridBagConstraints();
        con.gridwidth = GridBagConstraints.REMAINDER; //end row

        gameControls = new UIPanel();
        gameControls.setLayout(gbl);
        UIPanel buttonGrid = new UIPanel();
        buttonGrid.setLayout(new GridLayout(3,3));
        Button btn =new Button("1");
        btn.setEnabled(true);
        //btn.setOpaque(true);
        btn.setBackground(Color.red);
        //btn.setForeground(Color.red);
        btn.addActionListener(this);
        buttonGrid.add(btn);
        btn =new Button("2");
        btn.setEnabled(true);
        
        btn.addActionListener(this);
        buttonGrid.add(btn);btn =new Button("3");
        btn.setEnabled(true);
        //btn.setOpaque(true);
        btn.setBackground(Color.yellow);
        //btn.setForeground(Color.yellow);
        btn.addActionListener(this);
        buttonGrid.add(btn);btn =new Button("4");
        btn.setEnabled(true);
        btn.addActionListener(this);
        buttonGrid.add(btn);btn =new Button("5");
        btn.setEnabled(true);
        btn.addActionListener(this);
        buttonGrid.add(btn);btn =new Button("6");
        btn.setEnabled(true);
        btn.addActionListener(this);
        buttonGrid.add(btn);btn =new Button("7");
        //btn.setOpaque(true);
        //btn.setForeground(Color.green);
        btn.setBackground(Color.green);
        btn.setEnabled(true);
        btn.addActionListener(this);
        buttonGrid.add(btn);btn =new Button("8");
        btn.setEnabled(true);
        btn.addActionListener(this);
        buttonGrid.add(btn);btn =new Button("9");
       // btn.setOpaque(true);
        btn.setBackground(Color.blue);
        //btn.setForeground(Color.blue);
        btn.setEnabled(true);
        btn.addActionListener(this);
        buttonGrid.add(btn);
        UIPanel options = new UIPanel();
        options.setBorder(BorderFactory.createLineBorder(Color.black));

        options.setLayout(new GridLayout(6,1));
        optionsGroup = new ButtonGroup();
        rad1 = new JRadioButton("Both Positive");        
        rad2 = new JRadioButton("Both Negative");       
        rad3 = new JRadioButton("One Positive One Negative");

        
        rad1.addActionListener(this);
        rad2.addActionListener(this);
        rad3.addActionListener(this);
		optionsGroup.add(rad1);
		optionsGroup.add(rad2);
		optionsGroup.add(rad3);
		options.add(rad1);
		options.add(rad2);
		options.add(rad3);
        
        JButton scoreBtn = new JButton("Get Score");
        scoreBtn.addActionListener(this);
        JButton newBtn = new JButton("New game");
        newBtn.addActionListener(this);
        gbl.setConstraints(buttonGrid, con);
        
        messages = new JTextArea();
        messages.setColumns(32);
        messages.setRows(4);
        messages.setLineWrap(true);
        messages.setWrapStyleWord(true);
        messages.setVisible(true);
        messages.setText("");

        gameControls.add(buttonGrid);
        gbl.setConstraints(options,con);
        gameControls.add(options);
        //gbl.setConstraints(scoreBtn, con);
        //gameControls.add(scoreBtn);
        gbl.setConstraints(newBtn, con);
        gameControls.add(newBtn);
        gbl.setConstraints(messages, con);
        gameControls.add(messages);
        addElement(gameControls);


        VisualizationControl vizPanel = new VisualizationControl();     
        vizPanel.setFieldConvolution(mDLIC); 
		vizPanel.setConvolutionModes(DLIC.DLIC_FLAG_EP);
   //     addElement(vizPanel);
		
	    double scale3DS = 0.025; // this is an overall scale factor for .3DS objects
        
        Model iconobserverModel = new Model("models/man1.3DS","models/maps/");
        iconobserverModel.setScale(scale3DS);

        
        iconobserver = new Rendered();
        iconobserver.setModel(iconobserverModel);
        iconobserver.setPosition(new Vector3d(0.,0.,-1.));
        addElement(iconobserver);
        
       	theArrow.setPosition(getPosition(halfwall,halfwall));
        theArrow.setDrawn(true);
        
        arrowE = new Rendered();
        Model arrowModel = new Model("models/arrowE.3DS","models/maps/");
        arrowModel.setScale(scale3DS); 	
        arrowE.setModel(arrowModel);
        arrowE.setPosition(new Vector3d(0.,0.,0.));
    	arrowE.setDirection(theArrow.getValue());
   // 	arrowE.setDirection(new Vector3d(1.,0.,0.));
  //      addElement(arrowE);
    	
    	observer = new Sphere(0.08);
        observer.setDrawn(false);
        observer.setColor(new Color(0,0,0));
        addElement(observer);
        
        pcA = new PointCharge();
        addElement(pcA);
        pcB = new PointCharge();
        addElement(pcB);
        
        // set paramters for mouseScale 
       setMouseMoveScale(0.05,0.05,0.5);
        
       
        //mSEC.init(); 
        mSEC.setVisible(false);
        resetCamera();
        // addAction for pulldown menus on TEALsim windows     
        addActions();
        //reset();
        
    }
    public void initialize(){
    	 newGame();
    	 resetCamera();
    }

   
    void addActions() {
        TealAction ta = new TealAction("Execution & View", this);
        addAction("Help", ta);
        TealAction tb = new TealAction("Exploring Potential", this);
        addAction("Help", tb);
    }

    public void actionPerformed(ActionEvent e) {
   
        String actionCmd = e.getActionCommand();
        TDebug.println(1, " Action comamnd: " + actionCmd);
        if (actionCmd.length() == 1){
        	checkMove(actionCmd.charAt(0));
        }  
        else if (actionCmd.compareToIgnoreCase("Exploring Potential") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/exploringpotential.html");
        	}
        }  
        else if (actionCmd.compareToIgnoreCase("Execution & View") == 0) 
        {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/executionView.html");
        	}
        
        } 
        else if (actionCmd.compareToIgnoreCase("Get Score") == 0) 
        {
        	TDebug.println(0,getScore());
        
        } 
        else if (actionCmd.compareToIgnoreCase("New Game") == 0) 
        {
        	newGame();
        
        } 
        else if(e.getSource() == rad1){
        //	if(pcA.getCharge() > 0 &&  pcB.getCharge() > 0 && (Math.rint(pcA.getCharge()) == Math.rint(pcB.getCharge()))){
        	if(pcA.getCharge() > 0 &&  pcB.getCharge() > 0 ){
        		showResults();
        		resultMessage("Correct choice! \nYou won with "+ numMoves + " points");
        		
        	}
        	else{
        	numMoves += wrongGuess;
        	errorMessage("Incorrect choice \n" + wrongGuess + " point penalty \nYour score is " + numMoves + " points");
        	}
        }
        else if(e.getSource() == rad2){
        	if(pcA.getCharge() < 0 &&  pcB.getCharge() < 0 ) {
        		showResults();
        		resultMessage("Correct choice! \nYou won with "+ numMoves + " points");
        		
        	}
        	else{
        	numMoves += wrongGuess;
        	errorMessage("Incorrect choice \n" + wrongGuess + " point penalty \nYour score is " + numMoves + " points");
        	}
        }
        else if(e.getSource() == rad3){
        	if(pcA.getCharge()*pcB.getCharge() < 0 ){
        		showResults();
        		resultMessage("Correct choice! \nYou won with "+ numMoves + " points");
        		
        	}
        	else{
        	numMoves += wrongGuess;
        	errorMessage("Incorrect choice \n" + wrongGuess + " point penalty \nYour score is " + numMoves + " points");
        	}
        }

        
        else {
            super.actionPerformed(e);
        }
    }
    
    void resultMessage(String str){
    	StringBuffer buf = new StringBuffer(str);
    	//buf.append("\nYou have a total of " + numMoves + " points!");
    	//buf.append("\nCharge A: = " + pcA.getCharge() + "\nCharge B: = " + pcB.getCharge());
    	TDebug.println(0,buf.toString());
    	messages.setText(buf.toString()); 	
    	clearText = true;
    }
    
    void scoreMessage(String str){
    	StringBuffer buf = new StringBuffer(str);
    //	buf.append("\nYou have a total of " + numMoves + " moves!");
    	//buf.append("\nCharge A: = " + pcA.getCharge() + "\nCharge B: = " + pcB.getCharge());
    	TDebug.println(0,buf.toString());
    	messages.setText(buf.toString()); 	
    	clearText = true;
    }
    
    void errorMessage(String str){
    	StringBuffer buf = new StringBuffer(str);
    //	buf.append("\nYou won with " + numMoves + " points!");
    	TDebug.println(0,buf.toString());
    	messages.setText(buf.toString());
    	clearText = true;
    }

    public void reset() {
    	if(theEngine != null){
    		newGame();
    		theEngine.requestRefresh();
    	}
		
    }

    public void resetCamera() {
        setLookAt(new Point3d(0.0, -0.25, .44), 
        		new Point3d(0., 0.0, 0.), new Vector3d(0., 1., 0.));
    }
    
    Vector3d getPosition(int x, int z){
    	return new Vector3d(sizewall*(x)-nwall*sizewall/2.+ sizewall/2.,
    			sizewall*(z)-nwall*sizewall/2+sizewall/2.,0.); 
    }
    
    double getScaledPotential(int x, int y){
//    	double potential = powerPotential(theEngine.getEField().getPotential(getPosition(x,y)));
    	double potential = powerPotential(theEngine.getElementByType(EField.class).getPotential(getPosition(x,y)));
    	//System.out.println(" minPotential " + minPotential + " maxPotential " + maxPotential + " potential " + potential);
		double result = -1.*scale*nwall*sizewall + 2.*scale*nwall*sizewall*(potential-minPotential)/(maxPotential-minPotential);
	//	TDebug.println(0,"X: " + x + " Y: " + y + " Potential: " + potential + " Result: " + result);
		return result;    	
    }
    
    public void getPotentialMaxMin(){
    	double potential = 0.;
    	minPotential = 100000000.;
    	maxPotential = -100000000.;
     	for (int i = 0; i < nwall; i++) 
        {
        	for (int j = 0; j < nwall; j++) 
        	{         		
//        		potential = powerPotential(theEngine.getEField().getPotential(getPosition(i,j)));
        		potential = powerPotential(theEngine.getElementByType(EField.class).getPotential(getPosition(i,j)));
        		if ( potential > maxPotential) maxPotential = potential;
        		if ( potential < minPotential) minPotential = potential;
        	    //System.out.println(" i "	+i+" j "	+j+" maxPotential " + maxPotential + " minPotential " + minPotential);
        	} 	
        }

       //System.out.println(" maxPotential " + maxPotential + " minPotential " + minPotential);
	
    }
    
    public double powerPotential(double potential){
       	double signPot = Math.signum(potential);
    	double pot = signPot*Math.pow(Math.abs(potential),potpower);
    	return pot;
    }
    
    public void moveObserver(){
    	Vector3d pos = getPosition(idxX,idxY);
    	theArrow.setPosition(pos);
    	arrowE.setPosition(pos);
    	Vector3d arrowdirection = null;
    	double potential = getScaledPotential(idxX,idxY); 
    	Vector3d posup = new Vector3d(0.,0.,potential);
    	Vector3d postot = new Vector3d();
    	postot.add(pos,posup);
    	// System.out.println("position" + pos);
    	theEngine.requestRefresh();    	
    	observer.setPosition(pos);
    	arrowdirection = theArrow.getValue();
    	//arrowdirection.normalize();
    	System.out.println("before set arrowdirection idxX " + idxX + "  idxY " + idxY + " arrowdirection " + arrowdirection);
    	arrowE.setDirection(arrowdirection);
    	arrowdirection = arrowE.getDirection();
       	System.out.println("after set arrowdirection idxX "+ idxX + "  idxY " + idxY + " arrowdirection " + arrowdirection);
    	theEngine.requestRefresh();    	
       	arrowdirection = theArrow.getValue();
    	//arrowdirection.normalize();
    	System.out.println("2 before set arrowdirection idxX " + idxX + "  idxY " + idxY + " arrowdirection " + arrowdirection);
    	arrowE.setDirection(arrowdirection);
    	arrowdirection = arrowE.getDirection();
       	System.out.println("2 after set arrowdirection idxX "+ idxX + "  idxY " + idxY + " arrowdirection " + arrowdirection);

        iconobserver.setPosition(postot);
      //  mViewer.setLookAt(new Point3d(postot.x,postot.y,postot.z), 
        	//	new Point3d(0., 0.0, 0.), new Vector3d(0., 1., 0.));
    
    	if(!visited[idxX][idxY]){
    		moveWall(idxX,idxY);
    		visited[idxX][idxY] = true;
    		numMoves++;
    	}
   // 	scoreMessage("Your score is  " + numMoves);
    	if(theEngine != null)
    	theEngine.requestRefresh();    	
    }
    
    void moveWall(int x, int y){
    	
		Vector3d loc =wall[x][y].getPosition();
		loc.z = getScaledPotential(x,y);
		wall[x][y].setPosition(loc);

    }
    
    void checkMove(char code){
    	boolean moved = false;
    	if(clearText){
    		messages.setText("");
    		clearText = false;
    	}
    		
    		TDebug.println(0,"Code: " + code);
    		switch (code){
    		case '1':
    			if(idxX >0 && idxY < max){
    				idxX--;
    				idxY++;
    				moved = true;
    			}
    			break;
    		case '2':
    			if(idxY < max){
    				idxY++;
    				moved = true;
    			}
    			break;
    		case '3':
    			if(idxX < max && idxY < max){
    				idxX++;
    				idxY++;
    				moved = true;
    			}
    			break;
    		
    		case '4':
    			if(idxX > 0){
    				idxX--;
    				moved = true;
    			}
    			break;
    		case '6':
    			if(idxX <max){
    				idxX++;
    				moved = true;
    			}
    			break;
    		
    		
    		case '7':
    			if(idxX >0 && idxY > 0){
    				idxX--;
    				idxY--;
    				moved = true;
    			}
    			break;
    		case '8':
    			if(idxY > 0){
    				idxY--;
    				moved = true;
    			}
    			break;
    		case '9':
    			if(idxX < max && idxY > 0){
    				idxX++;
    				idxY--;
    				moved = true;
    			}
    			break;
    		default:
    			break;
    		}
    		if(moved){
    			moveObserver();
    			scoreMessage("Your score is  " + numMoves);
    			
    		}
    	}
    
    void newGame(){

  //  	optionsGroup.clearSelection();
    	messages.setText("");
    	clearText = false;
    	// reset walls
    	 for (int i = 0; i < nwall; i++) 
         {
         	for (int j = 0; j < nwall; j++) 
         	{         		
         		wall[i][j].setPosition(getPosition(i,j));
         		visited[i][j] = false;
         	} 	
         }
    	// pick charges of invisible charges, either two pluses, two minuses, or one plus one minus, with equal probability
 			pcB.setRadius(pointChargeRadius);
	 		pcA.setRadius(pointChargeRadius);
	    	int rancharge = rand.nextInt(3);
	 		if ( rancharge == 0 ) {
    			pcA.setCharge( 1.);
    			pcB.setCharge(1.);}
	 		if ( rancharge == 1 ) {
    			pcA.setCharge(-1.);
    			pcB.setCharge(-1.);}
	 		if ( rancharge == 2 ) {
    			pcA.setCharge(-1.);
    			pcB.setCharge(1.);}
	 		
    	// set positions of the invisible charges
    	// first determine the quadrant of the first charge
    		int ranquadA = rand.nextInt(4);
    		Vector3d zeroquad = null;
    		zeroquad = zeroset(ranquadA);
    	// now pick its random position in this quadrant
    		int randi = rand.nextInt((halfwall - 4)/2);
    		int randj = rand.nextInt((halfwall - 4)/2);
    	 	Vector3d pcApos = new Vector3d();
    		pcApos.add(zeroquad,new Vector3d((2*randi+1)*.5*sizewall + 2*sizewall, (2*randj+1)*.5*sizewall + 2*sizewall,0.));
        // second determine the quadrant of the second charge
    		int ranquadB = rand.nextInt(4);
    	// make sure this is not the quadrant of the first charge
    		if (ranquadB == ranquadA) ranquadB=ranquadA+1;
    		if (ranquadB > 3 ) ranquadB = 1;
       		zeroquad = zeroset(ranquadB);
    	// now pick its random position in this quadrant
    		randi = rand.nextInt((halfwall - 4)/2);
    		randj = rand.nextInt((halfwall - 4)/2);
    	 	Vector3d pcBpos = new Vector3d();
     		pcBpos.add(zeroquad,new Vector3d((2*randi+1)*.5*sizewall + 2*sizewall, (2*randj+1)*.5*sizewall + 2*sizewall,0.));
     		//System.out.println(" zeroquad "+zeroquad);
    	 	//System.out.println(" pcA "+ pcApos + " pcB "+ pcBpos + " ranquadA "+ ranquadA +" ranquadB "	+ ranquadB);
    		pcA.setPosition(pcApos);
    		pcA.setDrawn(false);
    	 	pcB.setPosition(pcBpos);
    		pcB.setDrawn(false);
    		

        
    	idxX = halfwall;
        idxY = halfwall;
        //refPowerPotential = powerPotential(theEngine.getEField().getPotential(getPosition(idxX,idxY)));
        // find the min and max potential values for this configuration of charges

        getPotentialMaxMin();
        moveObserver();
        numMoves = 0;
    }
    
    public Vector3d zeroset(int rand) {
    	Vector3d result = null;
    	if (rand == 0) result = new Vector3d(-nwall*sizewall/2.+sizewall/2.,-nwall*sizewall/2.+sizewall/2.,0.);
    	if (rand == 1) result = new Vector3d(0.,-nwall*sizewall/2+sizewall/2.,0.);
    	if (rand == 2) result = new Vector3d(0.,0.,0.);
    	if (rand == 3) result = new Vector3d(-nwall*sizewall/2+sizewall/2.,0.,0.);
    	return result;
    }
    	
    void showResults(){
    	pcA.setDrawn(true);
    	pcB.setDrawn(true);
    	
    	for (int i = 0; i < nwall; i++) 
        {
        	for (int j = 0; j < nwall; j++) 
        	{         		
        		moveWall(i,j);
        	} 	
        }
    	theEngine.requestRefresh(); 
    //    mDLIC.generatePotentialImage();
    //    theEngine.requestRefresh(); 
    }

String getScore(){
	StringBuffer buf = new StringBuffer();

	showResults();
//	buf.append("There were " +chargeList.size() + " charges.");
//	buf.append("\nPositive: " + pos +" Negative: " + neg);
	buf.append("\nYou moved  total of " + numMoves + " times.");
	messages.setText(buf.toString());
	messages.setVisible(true);
	return buf.toString();
}

    
   
    			
	
    	
    

    
    
}

