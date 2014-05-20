/* $Id: ExploringElectricField.java,v 1.7 2011/05/27 15:39:41 pbailey Exp $ */
/**
 * @author John Belcher 
 * Revision: 1.0 $
 */

package tealsim.physics.em;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.vecmath.*;

import teal.config.Teal;
import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.render.BoundingSphere;
import teal.render.Rendered;
import teal.render.primitives.Sphere;
import teal.sim.spatial.FieldConvolution;
import teal.sim.spatial.FieldVector;
import teal.physics.physical.Ball;
import teal.physics.physical.Wall;
import teal.physics.em.EField;
import teal.physics.em.PointCharge;
import teal.physics.em.SimEM;
import teal.render.primitives.Line;
import teal.render.scene.Model;
import teal.ui.UIPanel;
import teal.util.TDebug;

/** An application to help the player learn how to deduce the local electric field direction using neighboring values of the 
 * electric potential surrounding the student's current location.
 * @author John Belcher
 * @version 1.0 
 * */


public class ExploringElectricField extends SimEM  {
	/** serialVersionUID */
    private static final long serialVersionUID = 3257008735204554035L;
    /** Scale for electric field vectors. */
	double arrowScale = 0.7;
	/** Radius of the point charges in the scene */
    double pointChargeRadius = 0.1;
    /** Text area for messages to the user while playing the game. */
	JTextArea messages;
	/** Flag for whether to clear text in the message area or not. */
	boolean clearText = false;
	/** Flag to determine whether the player has correctly guessed the direction of the E field.  If false the player must do that 
	 * before he or she can continue on to another location.*/
    boolean exploreNextSquare=false;
    /** UI panel for game controls */
	UIPanel gameControls;
	/** ButtonGroup for the three choices of possible "invisible" charge configurations.*/
	ButtonGroup optionsGroup;
	/** Radio button for the case where the user guesses that the two invisible charges are both positive.*/
	JRadioButton rad1;
	/** Radio button for the case where the user guesses that the two invisible charges are both negative.*/
    JRadioButton rad2;       
	/** Radio button for the case where the user guesses that the two invisible charges are + and -.*/
    JRadioButton rad3;
	/** Button for finding out the potential in surrounding squares.  Player must have correctly guessed E in previous location before this
	 * button will set the potential in the new area. */
    JButton findPotentialButton = null;
    /** Scaling power for the potential.  To reduce the range in potential we plot the potential raised to the PotentialPower power,
     * preserving the sign. */
    double PotentialPower = .2;
    /** The maximum angle between the local E field direction and the direction chosen by the player for agreement between the two. */
    double allowedAngleEdirection = 50.;
    /** The width of the total player field.  The playing field is made up of nsquares * nsquares squares. */
    double widthPlayingField = 5.;
    /** The number of squares on a side of the field divided by 2, rounded to lowest integer. */
    int halfnsquares =12; 
    /** The number of squares on a side of the field.  This is guaranteed to be an odd number by method of construction from halfnsquares. */
	int nsquares = 2*halfnsquares+1;  
	/** The width of a square.  This is computed from the total width of the field and the number of squares on a side. */
    double widthSquare = widthPlayingField/nsquares;  
    /** Penalty for incorrectly choosing the direction of the E field. */
    int wrongGuessPenalty = 4;
    /** The squares making up the playing field. */
    Wall[][] squares;
    /** The electric field vectors at the location of the squares. */
    FieldVector[][] theArrows;
    /** The angles of the various direction choices on the keypad (1,2,3,4,5,6,7,8,9).  The direction of 5 is arbitrary because 
     * this is the center of the keypad. */
    double[] theDirectionAngles = {135.,90.,45.,180.,-1000.,0.,225.,270.,315.};
    /** A flag to tell whether a given square has been previously visited or not. */
    boolean[][] visited;
    /** The score of the game.  */
    int Score=0;
    /** The location of the player in the x plane.  This is an integer running from 0 to nsquares-1, corresponding to squares 
     * from the lower x value of the field to the upper x value of the field. */
    int idX = 0;
    /** The location of the player in the y plane.  This is an integer running from 0 to nsquares-1, corresponding to squares 
     * from the lower y value of the field to the upper y value of the field. */
	int idY = 0;
	/** The scale in the z direction for the potential.  The values of the z coordinate will run from -scaleZdirection*widthPlayingField to
	 * +scaleZdirection*widthPlayingField as the potential runs from minPotential to maxPotential, */
	double scaleZdirection = 0.35;
	/** Once a charge configuration is chosen, this is the maxPotential seen over the player field for that configuration.  */
	double maxPotential;
	/** Once a charge configuration is chosen, this is the minPotential seen over the player field for that configuration.  */
	double minPotential;
	/** A random number used to generate positions and charges of the invisible charges for each game. */
    Random rand;
    /** One of two point charges. */
    PointCharge pcA;
    /** One of two point charges.  */
    PointCharge pcB;
    /** Field convolution for displaying the field once the player has won. */
    protected FieldConvolution mDLIC = null;
    /** The position of player projected into the xy plane, using a native shape (a sphere). */
    Rendered observer;
    /** An imported 3DS object (a lego man icon for the observer).  */
    Rendered iconobserver;
   
	
    public ExploringElectricField() {
        super();
        setShowGizmos(false);
        TDebug.setGlobalLevel(0);

        title = "Exploring Electric Field";
        
        BoundingSphere bs = new BoundingSphere(new Point3d(0, 1.6, 0), 03.5);
        setBoundingArea(bs);
        setDeltaTime(0.005); 
        setBoundingArea(bs);
        setBackgroundColor(new Color(37*4,49*4,255));
        rand = new Random(); 
        theArrows = new FieldVector[nsquares][nsquares];
        
        // We create a two D array of squares.
        squares = new Wall[nsquares][nsquares];
        for (int i = 0; i < nsquares; i++) {
        	for (int j = 0; j < nsquares; j++) {
        		squares[i][j]= new Wall(getPosition(i,j), 
                		new Vector3d(0., widthSquare, 0.), new Vector3d(widthSquare, 0., 0.));
        		squares[i][j].setMoveable(true);
        		addElement(squares[i][j])	;
        	} 	
       }
        // We create a two d array of vector arrows indicating the electric field direction at the center of each square
        for (int i = 0; i < nsquares; i++)  {
        	for (int j = 0; j < nsquares; j++) {
         	theArrows[i][j] = new FieldVector();
         	theArrows[i][j].setColor(Teal.PointChargePositiveColor);
         	theArrows[i][j].setArrowScale(arrowScale);
            theArrows[i][j].setDrawn(false);
            addElement(theArrows[i][j]);
         	} 	
        }
       
        // add four lines to outline the basic playing field to define it better
        Line[][] defineFieldLines;
        defineFieldLines = new Line[2][2];
        for (int i = 0; i < 2; i++) {
        	for (int j = 0; j < 2; j++) {
        		if (i == 0 ) {
        			defineFieldLines[i][j] = new Line(new Vector3d(-widthPlayingField*(0.5-j*1.),-widthPlayingField*(0.5),0.),
        					new Vector3d(-widthPlayingField*(0.5-j*1.),widthPlayingField*(0.5),0.));
        		} else {
        			defineFieldLines[i][j] = new Line(new Vector3d(-widthPlayingField*(0.5),-widthPlayingField*(0.5-j*1.),0.),
        					new Vector3d(widthPlayingField*(0.5),-widthPlayingField*(0.5-j*1.),0.));
        		}
        	defineFieldLines[i][j].setColor(Color.yellow);
        	addElement(defineFieldLines[i][j]);
        	} 	
        }
        
        // add marker balls at the vertices of the playing field square
        double radius = widthPlayingField/24.;
        double offset = widthPlayingField/2.+ radius/Math.sqrt(2.);
        Ball marker = new Ball();
        marker.setRadius(radius);
        marker.setColor(Color.green);
        marker.setPosition(new Vector3d(-offset,-offset,0.));
        marker.setMoveable(false);
        addElement(marker);
        
        marker = new Ball();
        marker.setColor(Color.blue);
        marker.setRadius(radius);
        marker.setPosition(new Vector3d(offset,-offset,0.));
        marker.setMoveable(false);
        addElement(marker);
        
        marker = new Ball();
        marker.setRadius(radius);
        marker.setColor(Color.red);
        marker.setPosition(new Vector3d(-offset,offset,0.));
        marker.setMoveable(false);
        addElement(marker);
        
        marker = new Ball();
        marker.setRadius(radius);
        marker.setColor(Color.yellow);
        marker.setPosition(new Vector3d(offset,offset,0.));
        marker.setMoveable(false);
        addElement(marker);
        
        
        // Additional UI Controls
        GridBagLayout gbl =new GridBagLayout();
        GridBagConstraints con = new GridBagConstraints();
        con.gridwidth = GridBagConstraints.REMAINDER; //end row

        gameControls = new UIPanel();
        gameControls.setLayout(gbl);
        
        // set up keypad for selecting direction of the electric field
        UIPanel buttonGrid = new UIPanel();
        buttonGrid.setLayout(new GridLayout(3,3));
        Button btn =new Button("1");
        btn.setEnabled(true);
        btn.setBackground(Color.red);
        btn.addActionListener(this);
        buttonGrid.add(btn);
        btn =new Button("2");
        btn.setEnabled(true);
        btn.addActionListener(this);
        buttonGrid.add(btn);btn =new Button("3");
        btn.setEnabled(true);
        btn.setBackground(Color.yellow);
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
        btn.setBackground(Color.green);
        btn.setEnabled(true);
        btn.addActionListener(this);
        buttonGrid.add(btn);btn =new Button("8");
        btn.setEnabled(true);
        btn.addActionListener(this);
        buttonGrid.add(btn);btn =new Button("9");
        btn.setBackground(Color.blue);
        btn.setEnabled(true);
        btn.addActionListener(this);
        buttonGrid.add(btn);
        UIPanel options = new UIPanel();
        options.setBorder(BorderFactory.createLineBorder(Color.black));

        // set up keypad for selecting direction for moving avatar
        UIPanel buttonGrid1 = new UIPanel();
        buttonGrid1.setLayout(new GridLayout(3,3));
        Button btn1 =new Button("a");
        btn1.setEnabled(true);
        btn1.setBackground(Color.red);
        btn1.addActionListener(this);
        buttonGrid1.add(btn1);
        btn1 =new Button("b");
        btn1.setEnabled(true);
        btn1.addActionListener(this);
        buttonGrid1.add(btn1);
        btn1 =new Button("c");
        btn1.setEnabled(true);
        btn1.setBackground(Color.yellow);
        btn1.addActionListener(this);
        buttonGrid1.add(btn1);
        btn1 =new Button("d");
        btn1.setEnabled(true);
        btn1.addActionListener(this);
        buttonGrid1.add(btn1);
        btn1 =new Button("e");
        btn1.setEnabled(true);
        btn1.addActionListener(this);
        buttonGrid1.add(btn1);
        btn1 =new Button("f");
        btn1.setEnabled(true);
        btn1.addActionListener(this);
        buttonGrid1.add(btn1);
        btn1 =new Button("g");
        btn1.setBackground(Color.green);
        btn1.setEnabled(true);
        btn1.addActionListener(this);
        buttonGrid1.add(btn1);
        btn1 =new Button("h");
        btn1.setEnabled(true);
        btn1.addActionListener(this);
        buttonGrid1.add(btn1);
        btn1 =new Button("i");
        btn1.setBackground(Color.blue);
        btn1.setEnabled(true);
        btn1.addActionListener(this);
        buttonGrid1.add(btn1);

 // set radio buttons for solution choice        
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
        JButton newBtn = new JButton("New game");
        newBtn.addActionListener(this);
        gbl.setConstraints(buttonGrid, con);
        
 // set up text area for messages to player       
        messages = new JTextArea();
        messages.setColumns(32);
        messages.setRows(4);
        messages.setLineWrap(true);
        messages.setWrapStyleWord(true);
        messages.setVisible(true);
        messages.setBackground(Color.yellow);
        messages.setText("");

        gbl.setConstraints(options,con);
        gameControls.add(options);
        //gbl.setConstraints(scoreBtn, con);
        //gameControls.add(scoreBtn);
        gbl.setConstraints(newBtn, con);
        gameControls.add(newBtn);
        gbl.setConstraints(messages, con);

        gameControls.add(buttonGrid);
        gameControls.add(messages);
        gameControls.add(buttonGrid1);
        addElement(gameControls);
        
        observer = new Sphere();
        observer.setDrawn(true);
        observer.setColor(new Color(0,0,0));
      //  addElement(observer);
        
        iconobserver = new Rendered();
        Model man = new Model("models/man.3DS","models/maps/");
		man.setScale(0.025);
		iconobserver.setModel(man);
        iconobserver.setPosition(new Vector3d(0.,0.,-1.));
        addElement(iconobserver);
       
        pcA = new PointCharge();
        addElement(pcA);
        pcB = new PointCharge();
        addElement(pcB);
        
        // set parameters for mouseScale 
        setMouseMoveScale(0.05,0.05,0.5);
        mSEC.setVisible(false);

        addActions();
      
        
    }
    
    public void initialize(){
    	super.initialize();
    	newGame();
    	resetCamera();  
    }
	
/** Programmed response to user actions. 
 * @param e is the action event performed by the user that we respond to.*/
    public void actionPerformed(ActionEvent e) {
        String actionCmd = e.getActionCommand();
        TDebug.println(1, " Action comamnd: " + actionCmd);
        
        // treat the case where user has pressed the button for setting the potential of squares surrounding current position.  
        if (actionCmd.compareToIgnoreCase("set potential") == 0) {
            TDebug.println(0, "square array number:  idX  " + idX + " idY "+ idY);  
            sendPlayerMessage(" ");
            if (idX == 0 || idX == (nsquares-1)|| idY == 0 || idY == (nsquares-1)){
            	sendPlayerMessage("You have moved to a square on the border.  Move to an interior square");
            } else {
            if (exploreNextSquare == true) {
            	moveObserver();
                moveSquares();  
        		Score++;
                sendPlayerMessage("What is the direction of the E field at your new position? Use keypad above to indicate that direction\n" +
                		"Your current score is "+Score);
                exploreNextSquare = false;
            } else sendPlayerMessage("Must find E at previous square before you can move to another square.  Use keypad above.");
        }
        }
        
        // treat the case where the action comes from a keypad press
        if (actionCmd.length() == 1){
        	int keypadcode = (int)actionCmd.charAt(0)-49;
    		// the keypad for choosing the direction of the electric field
        	if ( keypadcode < 9){
				if ( keypadcode  == 4) {
					sendPlayerMessage("Not a valid choice\nChoose again");
				}else {
					boolean directionOK = checkEdirectionGuess(keypadcode);
					if (directionOK == true){
						sendPlayerMessage("Correct direction for E.  Move to another square to explore potential.\nUse the keypad below to move." +
								"\nYour current score is "+Score+".") ;
						exploreNextSquare = true;	
						theArrows[idX][idY].setDrawn(true);
					} else {
						Score++;
						sendPlayerMessage("Incorrect direction for E\nOne point penalty\nTry again using keypad above\n" +
								"Your current score is "+Score);
						exploreNextSquare = false;
					}
				}	
			// this is the keypad for moving the observer 	
        	} else {
        		if (exploreNextSquare == true) moveObserverFromKeypad(actionCmd.charAt(0)); 
        		else sendPlayerMessage("You cannot move until you have correctly identified the" +
        		  " E field direction at your present position.  Use keypad above.");
        	}
        }
 
        if (actionCmd.compareToIgnoreCase("Exploring Electric Field") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/exploringelectricfield.html");
        	}
        }  
        else if (actionCmd.compareToIgnoreCase("Execution & View") == 0) 
        {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/executionView.html");
        	}
        
        } 

        else if (actionCmd.compareToIgnoreCase("New Game") == 0) 
        {
        	newGame();
        
        } 
        
        else if(e.getSource() == rad1){
        	if(pcA.getCharge() > 0 &&  pcB.getCharge() > 0 ){
        		showResults();
        		sendPlayerMessage("Correct choice! \nYou won with "+ Score + " points");   		
        	}
        	else{
        	Score += wrongGuessPenalty;
        	sendPlayerMessage("Incorrect choice \n" + wrongGuessPenalty + " point penalty \nYour score is " + Score + " points");
        	}
        }
        
        else if(e.getSource() == rad2){
        	if(pcA.getCharge() < 0 &&  pcB.getCharge() < 0 ) {
        		showResults();
        		sendPlayerMessage("Correct choice! \nYou won with "+ Score + " points");
        		
        	}
        	else{
        	Score += wrongGuessPenalty;
        	sendPlayerMessage("Incorrect choice \n" + wrongGuessPenalty + " point penalty \nYour score is " + Score + " points");
        	}
        }
        
        else if(e.getSource() == rad3){
        	if(pcA.getCharge()*pcB.getCharge() < 0 ){
        		showResults();
        		sendPlayerMessage("Correct choice! \nYou won with "+ Score + " points");
        		
        	}
        	else{
        	Score += wrongGuessPenalty;
        	sendPlayerMessage("Incorrect choice \n" + wrongGuessPenalty + " point penalty \nYour score is " + Score + " points");
        	}
        }

        
        else {
            super.actionPerformed(e);
        }
        theEngine.requestRefresh();

    }
/** Adds actions. */
    void addActions() {
        TealAction ta = new TealAction("Execution & View", this);
        addAction("Help", ta);
        TealAction tb = new TealAction("Exploring Electric Field", this);
        addAction("Help", tb);
        findPotentialButton = new JButton(new TealAction("Show Potential Around Current Position", "set potential", this));
        findPotentialButton.setBounds(40, 650, 195, 24);
        addElement(findPotentialButton);
    }
    
/** Checks to see if the local electric field direction and the user's guess for that direction agree within 50 degrees. 
 * @param choice is an integer from 0 to 8 which indicates the nine different choices the user can make on the keypad for the E field direction. */    
    public boolean checkEdirectionGuess( int choice ) {
    	boolean checkdir = false;
    	double angleEdirection;
    	Vector3d direction;
    	// set the direction to the number on the keypad chosen in xy coordinates, using the angles for those numbers
    	direction = new Vector3d(Math.cos(theDirectionAngles[choice]*Math.PI/180.),Math.sin(theDirectionAngles[choice]*Math.PI/180.),0.);
    	double lengththeArrows = theArrows[idX][idY].getValue().length();
    	double dotproduct = direction.dot((theArrows[idX][idY].getValue()));
    	double acosEdirection = direction.dot((theArrows[idX][idY].getValue()))/lengththeArrows;
    	angleEdirection = (180./Math.PI)*Math.acos(acosEdirection);
    	System.out.println("direction "+ direction+"  choice "+choice+" lengththeArrows "+lengththeArrows+" angleEdirection "+angleEdirection);
    	System.out.println("directionE "+ theArrows[idX][idY].getValue()+" acosEdirection "+acosEdirection +"  dotproduct "+dotproduct);
    	if ( angleEdirection <= allowedAngleEdirection ) checkdir = true;
    	return checkdir;
    }
    	
 /** Gets the spatial position of the square given its indices.  
  * @param x is the integer describing the x postion of the square
  * @param y is the integer describing the y position of the square.  */ 
    Vector3d getPosition(int x, int y){
    	return new Vector3d(widthSquare*(x)-nsquares*widthSquare/2.+ widthSquare/2.,
    			widthSquare*(y)-nsquares*widthSquare/2+widthSquare/2.,0.); 
    }
 /** Once a configuration of charges is selected, finds the maximum and minimum potential at the center of the squares.  */   
    public void getPotentialMaxMin(){
    	double potential = 0.;
    	minPotential = 100000000.;
    	maxPotential = -100000000.;
     	for (int i = 0; i < nsquares; i++) {
        	for (int j = 0; j < nsquares; j++) {         		
//        		potential = powerPotential(theEngine.getEField().getPotential(getPosition(i,j)));
        		potential = powerPotential(theEngine.getElementByType(EField.class).getPotential(getPosition(i,j)));
        		if ( potential > maxPotential) maxPotential = potential;
        		if ( potential < minPotential) minPotential = potential;
        	} 	
        }	
    }
    
 /** Gets the scaled potential at this point.  The scaled potential varies from minPotential to maxPotential over the z distance 
  * -scaleZdirection*widthPlayingField to +scaleZdirection*widthPlayingField.  
  * @param x integer indicating the x location of the point.
  * @param y integer indicating the y location of the point.
  * @return scaled potential at this point.  
  */
    double getScaledPotential(int x, int y){
//    	double potential = powerPotential(theEngine.getEField().getPotential(getPosition(x,y)));
    	double potential = powerPotential(theEngine.getElementByType(EField.class).getPotential(getPosition(x,y)));
		double result = -1.*scaleZdirection*nsquares*widthSquare + 2.*scaleZdirection*nsquares*widthSquare*(potential-minPotential)/(maxPotential-minPotential);
		TDebug.println(1,"X: " + x + " Y: " + y + " Potential: " + potential + " Result: " + result);
		return result;    	
    }
 /** Outputs current score to the player.  */
    String getScore(){
    	StringBuffer buf = new StringBuffer();
    	buf.append("\nYour current score is " + Score);
    	messages.setText(buf.toString());
    	messages.setVisible(true);
    	return buf.toString();
    }
   
    public void moveObserver(){
    	Vector3d pos = null;
    	pos = new Vector3d(getPosition(idX,idY));
    	observer.setPosition(pos);
        iconobserver.setPosition(pos);
        if(theEngine != null)
        	theEngine.requestRefresh();    	
    }
    
   public void moveObserverFromKeypad(char code){
    	boolean moved = false;
    	if(clearText){
    		messages.setText("");
    		clearText = false;
    	}
    		
    		TDebug.println(0,"Code: " + code);
    		switch (code){
    		case 'a':
    			if(idX >0 && idY < (nsquares-1)){
    				idX--;
    				idY++;
    				moved = true;
    			}
    			break;
    		case 'b':
    			if(idY < (nsquares-1)){
    				idY++;
    				moved = true;
    			}
    			break;
    		case 'c':
    			if(idX < (nsquares-1) && idY < (nsquares-1)){
    				idX++;
    				idY++;
    				moved = true;
    			}
    			break;
    		
    		case 'd':
    			if(idX > 0){
    				idX--;
    				moved = true;
    			}
    			break;
    			
    		// case e is the center of the keypad, no motion
    			
    		case 'f':
    			if(idX <(nsquares-1)){
    				idX++;
    				moved = true;
    			}
    			break;	
    		
    		case 'g':
    			if(idX >0 && idY > 0){
    				idX--;
    				idY--;
    				moved = true;
    			}
    			break;
    		case 'h':
    			if(idY > 0){
    				idY--;
    				moved = true;
    			}
    			break;
    		case 'i':
    			if(idX < (nsquares-1) && idY > 0){
    				idX++;
    				idY--;
    				moved = true;
    			}
    			break;
    		}
    		if(moved){
    			moveObserver();			
    		}
    	}

/** Move a square to the proper z direction based on the scaled potential.
 * @param x the integer indicating the x position.
 * @param y the integer indicating the y position.
 */
   public void moveSquare(int x, int y){
		Vector3d loc =squares[x][y].getPosition();
		loc.z = getScaledPotential(x,y);
		squares[x][y].setPosition(loc);

   }
/** Move the squares in the immediate neighborhood of the current observer position to their proper z position based on the scaled
 * potential. */   
   public void moveSquares(){
   	Vector3d loc = null;
   	int xint =0;
   	int yint = 0;
       for (int i = 1; i < 4; i++) {
       	for (int j = 1; j < 4; j++) {
       		xint = idX+i-2;
       		yint = idY+j-2;
       		loc =squares[xint][yint].getPosition();
       		loc.z = getScaledPotential(xint,yint);
       		squares[xint][yint].setPosition(loc);
       	}
       }
   }
   
/** Sets up a new game.  */
   public void newGame(){
 //  	optionsGroup.clearSelection();
   	 messages.setText("");
    clearText = false;
   	idX = halfnsquares;
    idY = halfnsquares;
    // reset square positions
   	 for (int i = 0; i < nsquares; i++) {
        for (int j = 0; j < nsquares; j++) {         		
        	squares[i][j].setPosition(getPosition(i,j));
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
   	// first choose the random quadrant of the first charge
	int ranquadA = rand.nextInt(4);
	Vector3d zeroquad = null;
	zeroquad = quadrantLocation(ranquadA);
   	// now pick its random position in this quadrant
	int randi = rand.nextInt((halfnsquares - 4)/2);
	int randj = rand.nextInt((halfnsquares - 4)/2);
 	Vector3d pcApos = new Vector3d();
	pcApos.add(zeroquad,new Vector3d((2*randi+1)*.5*widthSquare + 2*widthSquare, (2*randj+1)*.5*widthSquare + 2*widthSquare,0.));
    // second determine the quadrant of the second charge
	int ranquadB = rand.nextInt(4);
   	// make sure this is not the quadrant of the first charge
	if (ranquadB == ranquadA) ranquadB=ranquadA+1;
	if (ranquadB > 3 ) ranquadB = 1;
  	zeroquad = quadrantLocation(ranquadB);
   	// now pick its random position in this quadrant
	randi = rand.nextInt((halfnsquares - 4)/2);
	randj = rand.nextInt((halfnsquares - 4)/2);
 	Vector3d pcBpos = new Vector3d();
	pcBpos.add(zeroquad,new Vector3d((2*randi+1)*.5*widthSquare + 2*widthSquare, (2*randj+1)*.5*widthSquare + 2*widthSquare,0.));
	pcA.setPosition(pcApos);
	pcA.setDrawn(false);
 	pcB.setPosition(pcBpos);
	pcB.setDrawn(false);
	// for this configuration of charges, get the minimum and maximum potential
   		
     // now set all of the electric field directions at the center of the squares for this charge configuration		
	 for (int i = 0; i < nsquares; i++)  {
        for (int j = 0; j < nsquares; j++) {
        	Vector3d posnow = new Vector3d(getPosition(i,j));
        	theArrows[i][j].setPosition(posnow);
            theArrows[i][j].setDrawn(false);
        	} 	
       }
 // set up initial position of observer at center of grid, more or less
    getPotentialMaxMin();

    moveSquares();
    moveObserver();
    Score = 0;
    exploreNextSquare = false;
    sendPlayerMessage("What is the direction of the E field at your current position?\nUse keypad above to indicate that direction.");
    reset();
       
   }
   
/** Raises the real potential to the power PotentialPower, preserving the sign.  This is done so that the range of variation in potential
 * values is not as large as if we were to use the potential itself.  PotentialPower is usually 1/3 or less.  
 * 
 * @param potential
 * @return
 */
   public double powerPotential(double potential){
      	double signPot = Math.signum(potential);
   	double pot = signPot*Math.pow(Math.abs(potential),PotentialPower);
   	return pot;
   }
   
   /** Sets the left hand lower position of the quadrants (0,1,2,3).  This is part of the process of choosing the location of the
    * invisible charges.  
    * @param rand The integer indicating which of the four quadrants we are considering.
    * @return The location of the left hand corner of this quadrant.  
    */
       public Vector3d quadrantLocation(int rand) {
       	Vector3d result = null;
       	if (rand == 0) result = new Vector3d(-nsquares*widthSquare/2.+widthSquare/2.,-nsquares*widthSquare/2.+widthSquare/2.,0.);
       	if (rand == 1) result = new Vector3d(0.,-nsquares*widthSquare/2+widthSquare/2.,0.);
       	if (rand == 2) result = new Vector3d(0.,0.,0.);
       	if (rand == 3) result = new Vector3d(-nsquares*widthSquare/2+widthSquare/2.,0.,0.);
       	return result;
       }
       	
 /** Resets camera view to its original value. */
   public void resetCamera() {
       setLookAt(new Point3d(0.0, -0.25, .44), 
       		new Point3d(0., 0.0, 0.), new Vector3d(0., 1., 0.));
   }
    
    
    public void scoreMessage(){
    	StringBuffer buf = new StringBuffer();
    	buf.append("\nYou have a total of " + Score + " points.");
    	TDebug.println(0,buf.toString());
    	messages.setText(buf.toString()); 	
    	clearText = true;
    }
    
    public void sendPlayerMessage(String str){
    	StringBuffer buf = new StringBuffer(str);
    	TDebug.println(0,buf.toString());
    	messages.setText(buf.toString());
    	clearText = true;
    }
    
/** When the game is over, show the location of the charges and move all of the squares to their proper position in the z direction. Also 
 * make visible all the electric field directions.  */
    public void showResults(){
    	pcA.setDrawn(true);
    	pcB.setDrawn(true);
    	for (int i = 0; i < nsquares; i++) {
        	for (int j = 0; j < nsquares; j++) {         		
        		moveSquare(i,j);
        		theArrows[i][j].setDrawn(true);
        	} 	
        }
    //    mDLIC.generatePotentialImage();
    	if(theEngine != null)
    		theEngine.requestRefresh(); 
    }



}

   
    			
	
    	
    

   

