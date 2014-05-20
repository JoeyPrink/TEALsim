/* $Id: MappingFields.java,v 1.16 2010/08/10 18:12:34 stefan Exp $ */

/**
 * A demonstration implementation of the TFramework.
 *
 * @author Philip Bailey - Center for Educational Computing Initiatives / MIT
 * @version $Revision: 1.16 $
 */

package tealsim.physics.em;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.media.j3d.Appearance;
import teal.render.BoundingSphere;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import teal.framework.TFramework;
import teal.framework.TealAction;
import teal.math.RectangularPlane;
import teal.render.j3d.Node3D;
import teal.render.j3d.WallNode;
import teal.render.viewer.SelectEvent;
import teal.render.viewer.SelectListener;
import teal.render.viewer.TViewer;
import teal.sim.control.VisualizationControl;
import teal.physics.em.SimEM;
import teal.physics.em.EField;
import teal.physics.UserFieldGenerator;
import teal.physics.physical.Wall;
import teal.physics.em.GenericEField;
import teal.sim.simulation.SimWorld;
import teal.sim.spatial.FieldConvolution;
import teal.ui.control.ControlGroup;
import teal.ui.control.PropertyCombo;
import teal.ui.control.PropertyDouble;
import teal.util.TDebug;
import teal.visualization.dlic.DLIC;
import teal.visualization.image.ImageIO;
import teal.visualization.image.ImageStatusEvent;
import teal.visualization.image.ImageStatusListener;

public class MappingFields extends SimEM implements ImageStatusListener, SelectListener, DocumentListener {

    private static final long serialVersionUID = 4050483426165534771L;
    // UserFieldGenerator object, for field entry, and associated GUI elements.
    UserFieldGenerator uf;
    JLabel label_Fx, label_Fy;
    JFormattedTextField Fx, Fy;
    PropertyCombo options;
    PropertyCombo winners;

    // Wall object, for region selection, associated GUI elements, and initialization.
    Wall wall = null;
    PropertyDouble scale_slider = null;
    JFormattedTextField centerx, centery;
    JLabel label_centerx, label_centery;
    private final double initial_scale = 8.;
    private final double max_scale = 8.;

    // Selection list, to manage SelectListener responsibilities.
    private ArrayList<Object> selectList = new ArrayList<Object>();

    // IDraw related buttons. File is for potential image saving functionality.
    JButton but0, but1, but2;
    File curDir = null;
    JFileChooser fc = null;

    // Viewer bounds.
    //Rectangle normalViewerBounds = null;

    // Enumeration of first two combo box lines.
    private final int NONE = -2;
    private final int LINE = -1;

    // Enumeration of field examples.
    private final int TWO_POINT_CHARGES = 0;
    private final int POINT_CHARGE_CONSTANT_FIELD = 1;
    private final int DIPOLE_NO_FIELD = 2;
    private final int DIPOLE_CONSTANT_FIELD_1 = 3;
    private final int DIPOLE_CONSTANT_FIELD_2 = 4;
    private final int DIPOLE_FIELD_WITH_GRADIENT = 5;
    private final int TWO_LINE_CURRENTS_1 = 6;
    private final int TWO_LINE_CURRENTS_2 = 7;
    private final int NO_CIRCULATION_MANY_SOURCES = 8;
    private final int NO_SOURCES_LOTS_OF_CIRCULATION = 9;
    private final int RADIATING_DIPOLE = 10;
    private final int SWIRL = 11;
    private final int WEIRD_FIELD = 12;

    // Enumeration of weird field contest winners.
    private final int NICKI_LEHRER = 0;
    private final int DAVID_RUSH = 1;
    private final int MCGRAW_HERDEG = 2;
    private final int GEORGE_ZAIDAN = 3;

    // Flags
    private boolean withinPropertyChange = false;
    
    // DLIC generator
    private FieldConvolution mDLIC;
    

    public MappingFields() {

        super();
        TDebug.setGlobalLevel(0);
        title = "Mapping Fields";

        // *****************************************************************
        // Physical properties of the model (irrelevant in this application).
        // *****************************************************************
       
        setDamping(0.1);
        setGravity(new Vector3d(0., 0., 0.));

        // *****************************************************************
        // World, viewer and DLIC bounds and viewing properties.
        // *****************************************************************
        setNavigationMode(TViewer.ORBIT | TViewer.VP_TRANSLATE | TViewer.VP_ZOOM);
        setRefreshOnDrag(true);
        setShowGizmos(false);

        setBoundingArea(new BoundingSphere(new Point3d(), max_scale));
        //theEngine.requestRefresh();
        mDLIC = new FieldConvolution();
        mDLIC.setSize(new Dimension(512, 512));
        //mDLIC.setSize(new Dimension(3000,3000));
        //mDLIC.setSize(new Dimension(3000, 3000));
        //mDLIC.setColorMode(BufferedImage.TYPE_3BYTE_BGR);
        mDLIC.setComputePlane(new RectangularPlane(new BoundingSphere(new Point3d(0, 0, -initial_scale / 100.),
            initial_scale)));

        mSEC.setVisible(false);

        // *****************************************************************
        // Expression entry: UserFieldGenerator object and associated controls.
        // *****************************************************************
        Fx = new JFormattedTextField("-y");
        Fy = new JFormattedTextField("x");
        uf = new UserFieldGenerator(Fx, Fy);
        addElement(uf);
        GenericEField efield = new GenericEField(uf);
        addElement(efield);
        
        label_Fx = new JLabel("g(x,y) ");
        label_Fy = new JLabel("h(x,y) ");
        Fx.setBounds(75, 510, 395, 26);
        Fy.setBounds(75, 540, 395, 26);
        label_Fx.setBounds(20, 510, 50, 26);
        label_Fy.setBounds(20, 540, 50, 26);
        Font font = Fx.getFont();
        Fx.setFont(font.deriveFont(16f));
        Fy.setFont(font.deriveFont(16f));
        label_Fx.setFont(font.deriveFont(Font.BOLD | Font.ITALIC, 16f));
        label_Fy.setFont(font.deriveFont(Font.BOLD | Font.ITALIC, 16f));
        Fx.getDocument().addDocumentListener(this);
        Fy.getDocument().addDocumentListener(this);
        
        //		addElement(Fx);
        //		addElement(Fy);
        //		addElement(label_Fx);
        //		addElement(label_Fy);

        options = new PropertyCombo();
        options.add("Examples", new Integer(this.NONE));
        options.add("----------------------------------------", new Integer(this.LINE));
        options.add("Two point charges", new Integer(this.TWO_POINT_CHARGES));
        options.add("Point charge in a constant field", new Integer(this.POINT_CHARGE_CONSTANT_FIELD));
        options.add("Dipole in no field", new Integer(this.DIPOLE_NO_FIELD));
        options.add("Dipole in constant field (1)", new Integer(this.DIPOLE_CONSTANT_FIELD_1));
        options.add("Dipole in constant field (2)", new Integer(this.DIPOLE_CONSTANT_FIELD_2));
        options.add("Dipole in a field with gradient", new Integer(this.DIPOLE_FIELD_WITH_GRADIENT));
        options.add("Two line currents (1)", new Integer(this.TWO_LINE_CURRENTS_1));
        options.add("Two line currents (2)", new Integer(this.TWO_LINE_CURRENTS_2));
        options.add("No circluation, many sources", new Integer(this.NO_CIRCULATION_MANY_SOURCES));
        options.add("No sources, lots of circulation", new Integer(this.NO_SOURCES_LOTS_OF_CIRCULATION));
        options.add("Radiating Dipole", new Integer(this.RADIATING_DIPOLE));
        options.add("Swirl", new Integer(this.SWIRL));
        options.add("Weird field", new Integer(this.WEIRD_FIELD));
        //options.setBounds(0, 480, 245, 30);
        options.setFont(font.deriveFont(14f));
        options.addPropertyChangeListener("value", this);
        options.setSelectedIndex(0);
        //addElement(options);

        winners = new PropertyCombo();
        winners.add("Contest Winners", new Integer(this.NONE));
        winners.add("----------------------------------------", new Integer(this.LINE));
        winners.add("Nicki Lehrer, Spring 2004", new Integer(this.NICKI_LEHRER));
        winners.add("David Rush, Spring 2004", new Integer(this.DAVID_RUSH));
        winners.add("Michael McGraw-Herdeg, Fall 2004", new Integer(this.MCGRAW_HERDEG));
        winners.add("George Zaidan, Spring 2005", new Integer(this.GEORGE_ZAIDAN));
        winners.setBounds(240, 480, 245, 30);
        winners.setFont(font.deriveFont(14f));
        winners.addPropertyChangeListener("value", this);
        winners.setSelectedIndex(0);
        //addElement(winners);

        // *****************************************************************
        // Region selection: Wall object and associated controls.
        // *****************************************************************
        wall = new Wall();
        wall.setColliding(false);
        wall.setPosition(new Vector3d(0, 0, 0));
        wall.setEdge1(new Vector3d(2. * initial_scale, 0, 0));
        wall.setEdge2(new Vector3d(0, 2. * initial_scale, 0));
        Appearance fillAppearance = Node3D.makeAppearance(new Color3f(Color.GRAY), 0.5f, 0.5f, false);
        fillAppearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.FASTEST, 1f));
        Appearance frameAppearance = Node3D.makeAppearance(new Color3f(Color.BLUE), 0.f, 0.f, false);
        frameAppearance.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_LINE,
            PolygonAttributes.CULL_NONE, 0f));
        frameAppearance.setLineAttributes(new LineAttributes(4f, LineAttributes.PATTERN_SOLID, false));
        WallNode node = (WallNode) wall.getNode3D();
        node.setFillAppearance(fillAppearance);
        node.setFrameAppearance(frameAppearance);
        node.setScale(2. * initial_scale);
        wall.setSelectable(true);
        wall.setPickable(true);
        wall.addPropertyChangeListener("position", this);
        addElement(wall);

        centerx = new JFormattedTextField("0");
        centery = new JFormattedTextField("0");
        label_centerx = new JLabel("Center x: ");
        label_centery = new JLabel("Center y: ");
        centerx.setBounds(115, 580, 100, 26);
        centery.setBounds(315, 580, 100, 26);
        label_centerx.setBounds(60, 580, 50, 26);
        label_centery.setBounds(260, 580, 50, 26);
        centerx.getDocument().addDocumentListener(this);
        centery.getDocument().addDocumentListener(this);
        centerx.addActionListener(this);
        centery.addActionListener(this);
        addElement(centerx);
        addElement(centery);
        addElement(label_centerx);
        addElement(label_centery);
        scale_slider = new PropertyDouble();
        scale_slider.setPrecision(0.00001);
        scale_slider.setMinimum(0.0);
        scale_slider.setMaximum(max_scale);
        scale_slider.setBounds(20, 610, 415, 45);
        scale_slider.addPropertyChangeListener("value", this);
        scale_slider.setValue(initial_scale);
        scale_slider.setText("Scale");
        scale_slider.setBorder(null);
        //addElement(scale_slider);

        //normalViewerBounds = mViewer.getBounds();

        // *****************************************************************
        // IDraw Buttons.
        // *****************************************************************
        but0 = new JButton(new TealAction("Grass Seeds", "DLIC_E", this));
        but0.setBounds(40, 670, 195, 24);
        //addElement(but0);
        but1 = new JButton(new TealAction("Equipotential Lines", "DLIC_EP", this));
        but1.setBounds(250, 670, 195, 24);
        //addElement(but1);
        but2 = new JButton(new TealAction("Save Image", "SAVE_IMAGE", this));
        but2.setBounds(120, 710, 195, 24);
        but2.setEnabled(false);
        //addElement(but2);

        // *****************************************************************
        // GUI Construction
        // *****************************************************************
        //JTaskPane taskPane = new JTaskPane();

        ControlGroup examples = new ControlGroup();
        examples.setText("Field Examples");
        examples.add(options);
        examples.add(winners);
        addElement(examples);
        //taskPane.add(examples);

        JPanel FxPanel = new JPanel();
        FxPanel.setLayout(new BoxLayout(FxPanel, BoxLayout.X_AXIS));
        FxPanel.add(label_Fx);
        FxPanel.add(Fx);

        JPanel FyPanel = new JPanel();
        FyPanel.setLayout(new BoxLayout(FyPanel, BoxLayout.X_AXIS));
        FyPanel.add(label_Fy);
        FyPanel.add(Fy);

        ControlGroup vectorfield = new ControlGroup();
        vectorfield.setText("Vector Field");
        vectorfield.add(FxPanel);
        vectorfield.add(FyPanel);
        addElement(vectorfield);
        //taskPane.add(vectorfield);

        /*		
         JPanel centerxPanel = new JPanel();
         centerxPanel.setLayout(new BoxLayout(centerxPanel, BoxLayout.X_AXIS));
         centerxPanel.add(label_centerx);
         centerxPanel.add(centerx);

         JPanel centeryPanel = new JPanel();
         centeryPanel.setLayout(new BoxLayout(centeryPanel, BoxLayout.X_AXIS));
         centeryPanel.add(label_centery);
         centeryPanel.add(centery);
         */
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.add(label_centerx);
        centerPanel.add(centerx);
        centerPanel.add(label_centery);
        centerPanel.add(centery);

        ControlGroup zoombox = new ControlGroup();
        zoombox.setText("Zoom Box");
        zoombox.add(centerPanel);
        //		zoombox.add(centerxPanel);
        //		zoombox.add(centeryPanel);
        zoombox.add(scale_slider);
        //taskPane.add(zoombox);
        addElement(zoombox);
        
        VisualizationControl vizPanel = new VisualizationControl();
        vizPanel.setConvolutionModes(DLIC.DLIC_FLAG_E |DLIC.DLIC_FLAG_EP );
        vizPanel.setFieldConvolution(mDLIC);
  
        addElement(vizPanel);

        

        // *****************************************************************
        // Launch.
        // *****************************************************************
        addSelectListener(this);
        addActions();
        mDLIC.addImageStatusListener(this);
        //mSEC.init();
        //reset();
        //layoutNormal();
    }

    // *****************************************************************
    // DocumentListener methods.
    // Use only to listen to real-time manipulations of a text field.
    // *****************************************************************
    public void changedUpdate(DocumentEvent e) {
        if (e.getDocument() == centerx.getDocument()) {
        }
        if (e.getDocument() == centery.getDocument()) {
        }
        if (e.getDocument() == Fx.getDocument()) {
            if (!withinPropertyChange) options.setSelectedIndex(0);
            if (!withinPropertyChange) winners.setSelectedIndex(0);
        }
        if (e.getDocument() == Fy.getDocument()) {
            if (!withinPropertyChange) options.setSelectedIndex(0);
            if (!withinPropertyChange) winners.setSelectedIndex(0);
        }
    }

    public void insertUpdate(DocumentEvent e) {
        if (e.getDocument() == centerx.getDocument()) {
        }
        if (e.getDocument() == centery.getDocument()) {
        }
        if (e.getDocument() == Fx.getDocument()) {
            if (!withinPropertyChange) options.setSelectedIndex(0);
            if (!withinPropertyChange) winners.setSelectedIndex(0);
        }
        if (e.getDocument() == Fy.getDocument()) {
            if (!withinPropertyChange) options.setSelectedIndex(0);
            if (!withinPropertyChange) winners.setSelectedIndex(0);
        }
    }

    public void removeUpdate(DocumentEvent e) {
        if (e.getDocument() == centerx.getDocument()) {
        }
        if (e.getDocument() == centery.getDocument()) {
        }
        if (e.getDocument() == Fx.getDocument()) {
            if (!withinPropertyChange) options.setSelectedIndex(0);
            if (!withinPropertyChange) winners.setSelectedIndex(0);
        }
        if (e.getDocument() == Fy.getDocument()) {
            if (!withinPropertyChange) options.setSelectedIndex(0);
            if (!withinPropertyChange) winners.setSelectedIndex(0);
        }
    }

    void addActions() {
        // *****************************************************************
        // Menu items.
        // *****************************************************************
        TealAction ta = null;
        ta = new TealAction("Mapping Fields", this);
        addAction("Help", ta);
        ta = new TealAction("Save Image","SAVE_IMAGE", this);
        addAction("File", ta);
        //		ta= new TealAction("View Status", this);
        //		addAction("View", ta);
        /*		
         ta = new TealAction("Full Screen", this);
         addAction("View", ta);
         ta = new TealAction("Normal", this);
         addAction("View", ta);
         */
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareToIgnoreCase("reset") == 0) {
            TDebug.println(1, "Reset called");
            reset();
            theEngine.requestRefresh();
        } else if (e.getActionCommand().compareToIgnoreCase("Mapping Fields") == 0) {
        	if(mFramework instanceof TFramework) {
        		((TFramework)mFramework).openBrowser("help/mappingfields.html");
        	}
        }
        /*
         else if (
         e.getActionCommand().compareToIgnoreCase("Full Screen") == 0) {
         appFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
         layoutFull();
         } else if (e.getActionCommand().compareToIgnoreCase("Normal") == 0) {
         appFrame.setExtendedState(JFrame.NORMAL);
         layoutNormal();
         }
         */
        else if (e.getSource() == centerx) {
            Vector3d position = new Vector3d(wall.getPosition());
            try {
                position.x = Double.parseDouble(centerx.getText());
            } catch (NumberFormatException exception) {
                return;
            }
            wall.setPosition(position);
            propertyChange(new PropertyChangeEvent(wall, "position", new Vector3d(wall.getPosition()), new Vector3d(
                position)));
        } else if (e.getSource() == centery) {
            Vector3d position = new Vector3d(wall.getPosition());
            try {
                position.y = Double.parseDouble(centery.getText());
            } catch (NumberFormatException exception) {
                return;
            }
            wall.setPosition(position);
            propertyChange(new PropertyChangeEvent(wall, "position", new Vector3d(wall.getPosition()), new Vector3d(
                position)));
        } else if (e.getActionCommand().compareToIgnoreCase("SAVE_IMAGE") == 0) {
            saveDLICImage();
        } else {
            super.actionPerformed(e);
        }
    }

    public void propertyChange(PropertyChangeEvent pce) {
        withinPropertyChange = true;
        if (pce.getSource() == options) {
            int option = ((Integer) pce.getNewValue()).intValue();
            switch (option) {
                case NONE:
                    break;
                case LINE:
                    if (Fx.getText().startsWith("~") && Fy.getText().startsWith("~")) {
                        Fx.setText("-(y-3.5*sin(-acos(r/7)+t))");
                        Fy.setText("x-3.5*cos(-acos(r/7)+t)");
                        resetDefaultRegion();
                    }
                    break;
                case TWO_POINT_CHARGES:
                    Fx.setText("(x-4)/((x-4)^2+y^2)^1.5+.2*(x+4)/((x+4)^2+y^2)^1.5");
                    Fy.setText("y/((x-4)^2+y^2)^1.5+.2*y/((x+4)^2+y^2)^1.5");
                    resetDefaultRegion();
                    break;
                case POINT_CHARGE_CONSTANT_FIELD:
                    Fx.setText("x/r^3");
                    Fy.setText("y/r^3-.1");
                    resetDefaultRegion();
                    break;
                case DIPOLE_NO_FIELD:
                    Fx.setText("3.*x*y/r^5");
                    Fy.setText("(2*y^2-x^2)/r^5");
                    resetDefaultRegion();
                    break;
                case DIPOLE_CONSTANT_FIELD_1:
                    Fx.setText("3.*x*y/r^5+0.02");
                    Fy.setText("(2*y^2-x^2)/r^5+0.02");
                    resetDefaultRegion();
                    break;
                case DIPOLE_CONSTANT_FIELD_2:
                    Fx.setText("3.*x*y/r^5");
                    Fy.setText("(2*y^2-x^2)/r^5-0.02");
                    resetDefaultRegion();
                    break;
                case DIPOLE_FIELD_WITH_GRADIENT:
                    Fx.setText("3.*x*y/r^5");
                    Fy.setText("(2*y^2-x^2)/r^5-0.003*y");
                    resetDefaultRegion();
                    break;
                case TWO_LINE_CURRENTS_1:
                    Fx.setText("-y/((x-4)^2+y^2)^1.5-.2*y/((x+4)^2+y^2)^1.5");
                    Fy.setText("(x-4)/((x-4)^2+y^2)^1.5+.2*(x+4)/((x+4)^2+y^2)^1.5");
                    resetDefaultRegion();
                    break;
                case TWO_LINE_CURRENTS_2:
                    Fx.setText("-y/((x-4)^2+y^2)^1.5+.2*y/((x+4)^2+y^2)^1.5");
                    Fy.setText("(x-4)/((x-4)^2+y^2)^1.5-.2*(x+4)/((x+4)^2+y^2)^1.5");
                    resetDefaultRegion();
                    break;
                case NO_CIRCULATION_MANY_SOURCES:
                    Fx.setText("sin(x)");
                    Fy.setText("cos(y)");
                    resetDefaultRegion();
                    break;
                case NO_SOURCES_LOTS_OF_CIRCULATION:
                    Fx.setText("sin(y)");
                    Fy.setText("cos(x)");
                    resetDefaultRegion();
                    break;
                case RADIATING_DIPOLE:
                    Fx.setText("3*x*y*cos(r)+x*y*r*sin(r)");
                    Fy.setText("(2*y^2-x^2)*cos(r)-x*x*r*sin(r)");
                    resetDefaultRegion();
                    break;
                case SWIRL:
                    Fx.setText("x-y");
                    Fy.setText("x+y");
                    resetDefaultRegion();
                    break;
                case WEIRD_FIELD:
                    Fx.setText("y*cos(y)");
                    Fy.setText("x*cos(x)");
                    resetDefaultRegion();
                    break;
                default:
                    options.setSelectedIndex(0);
            /* Some other field examples.
             * 
             * Fx = sin(-y*abs(y/sin(t))^0.5)
             * Fy = sin(x*abs(x/cos(t))^0.5)
             * 
             * Fx = sin(x*abs(x/cos(t))^0.5)-sin(y*abs(y/sin(t))^0.5)
             * Fy = sin(x*abs(x/cos(t))^0.5)+sin(y*abs(y/sin(t))^0.5)
             * 
             * Fx = sin(-ln(8/r-1)*sin(t))
             * Fy = sin(ln(8/r-1)*cos(t))
             * 
             * Fx = sin(-ln(1-r/8)/ln(1/1.125)*sin(t))
             * Fy = sin(ln(1-r/8)/ln(1/1.125)*cos(t))
             * 
             * Fx = sin(ln(1-r/8)/ln(1/1.125)*cos(t))-sin(ln(1-r/8)/ln(1/1.125)*sin(t)) 
             * Fy = sin(ln(1-r/8)/ln(1/1.125)*cos(t))+sin(ln(1-r/8)/ln(1/1.125)*sin(t))
             * 
             * Fx = sin(atan(2*sin(10*t))-t)
             * Fy = cos(atan(2*sin(10*t))-t)
             * 
             */
            }
        } else if (pce.getSource() == winners) {
            int winner = ((Integer) pce.getNewValue()).intValue();
            switch (winner) {
                case NONE:
                    break;
                case NICKI_LEHRER:
                    Fx.setText("(ln(sin(x)))^3*(tan(y))");
                    Fy.setText("(ln(cos(y)))^3*(tan(x)) ");
                    setX(1.6);
                    setY(0);
                    setScale(8);
                    autoTranslateZoom();
                    break;
                case DAVID_RUSH:
                    Fx.setText("sin(y^2)");
                    Fy.setText("cos(x^2)");
                    setX(0);
                    setY(0);
                    setScale(8);
                    autoTranslateZoom();
                    break;
                case MCGRAW_HERDEG:
                    Fx.setText("tan(y^2)");
                    Fy.setText("3*y*tan(x^3)");
                    setX(0);
                    setY(0);
                    setScale(8);
                    autoTranslateZoom();
                    break;
                case GEORGE_ZAIDAN:
                    Fx.setText("sin(x*cos(y)-y*cos(x))");
                    Fy.setText("tan(x)");
                    setX(0);
                    setY(0);
                    setScale(8);
                    autoTranslateZoom();
                    break;
                default:
                    winners.setSelectedIndex(0);
            }
        } else if (pce.getSource() == scale_slider) {
            if ((wall != null) && pce.getPropertyName().equalsIgnoreCase("value")) {
                double scale = ((Double) scale_slider.getValue()).doubleValue();
                wall.setEdge1(new Vector3d(scale, 0, 0));
                wall.setEdge2(new Vector3d(0, scale, 0));
                WallNode node = (WallNode) wall.getNode3D();
                node.setScale(2. * scale);
                Vector3d position = new Vector3d(wall.getPosition());
                position.z = -scale / 100.;
                mDLIC.setComputePlane(new RectangularPlane(new BoundingSphere(new Point3d(position), scale)));
            }
        } else if (pce.getSource() == wall) {
            if (pce.getPropertyName().equalsIgnoreCase("position")) {
                double scale = ((Double) scale_slider.getValue()).doubleValue();
                Vector3d position = new Vector3d(wall.getPosition());
                if (position.z != 0.) {
                    position.z = 0.;
                    wall.setPosition(position, false);
                }
                position.z = -scale / 100.;
                mDLIC.setComputePlane(new RectangularPlane(new BoundingSphere(new Point3d(position), scale)));

                int fix = 3;
                double pow = Math.pow(10., fix);
                double x = Math.round(position.x * pow) / pow;
                double y = Math.round(position.y * pow) / pow;
                centerx.setText(String.valueOf(x));
                centery.setText(String.valueOf(y));
                theEngine.requestRefresh();
            }
        }

        super.propertyChange(pce);
        withinPropertyChange = false;
    }

    public void processSelection(SelectEvent se) {
        int status = se.getStatus();
        Object source = se.getSource();
        if (status == SelectEvent.SELECT) {
            selectList.add(source);
        } else if (status == SelectEvent.MULTI_SELECT) {
            selectList.add(source);
        } else if (status == SelectEvent.NOT_SELECTED) {
            selectList.remove(source);
        }
    }

    public void imageStatus(ImageStatusEvent ise) {
        // Currently only accepting imageStatus events from one source
        // the source is mDLIC's internal DLIC class member accssed through
        // the DLICGenerator
        int state = ise.getStatus();
        TDebug.println(2, "state: " + state + " complete? " + (state == ImageStatusEvent.COMPLETE));
        if ((state == ImageStatusEvent.COMPLETE) || (state == ImageStatusEvent.VALID)) {
            // *****************************************************************
            // After an IDraw image is generated, the camera auto-translates and
            // auto-zooms to center and expand the selected region.
            // *****************************************************************
            autoTranslateZoom();
            but2.setEnabled(true);
        } else if (state == ImageStatusEvent.INVALID) {
            but2.setEnabled(false);
        }
    }

    public void autoTranslateZoom() {
        Vector3d position = new Vector3d(wall.getPosition());
        double scale = ((Double) scale_slider.getValue()).doubleValue();
        position.scale(0.05);
        setLookAt(new Point3d(position.x, position.y, scale / 7.5), new Point3d(position.x, position.y, 0.),
            new Vector3d(0., 1., 0.));
    }

    public void resetDefaultRegion() {
        setX(0);
        setY(0);
        setScale(initial_scale);
        autoTranslateZoom();
    }

    public void setX(double x) {
        centerx.setText(String.valueOf(x));
        actionPerformed(new ActionEvent(centerx, 0, "value"));
    }

    public void setY(double y) {
        centery.setText(String.valueOf(y));
        actionPerformed(new ActionEvent(centery, 0, "value"));
    }

    public void setScale(double s) {
        scale_slider.setValue(s, true);
        //		propertyChange(new PropertyChangeEvent(scale_slider, "value",
        //		new Double(-1), scale_slider.getValue()));
    }

    protected void saveDLICImage() {
        if (mDLIC.isImageGenerated()) {
            BufferedImage img = (BufferedImage) mDLIC.getImage();
            if (img != null) {
                if (fc == null) fc = new JFileChooser();
                if (curDir != null) fc.setCurrentDirectory(curDir);
                int status = fc.showSaveDialog((Component)getGui());
                if (status == JFileChooser.APPROVE_OPTION) {
                    File file = null;
                    curDir = fc.getCurrentDirectory();
                    try {
                        file = fc.getSelectedFile();
                        ImageIO.writeJPEG(img, 300, file);
                    } catch (IOException fnf) {
                        TDebug.printThrown(fnf, " Trying to save file: " + file);
                    }

                }
            }
        }
    }

    public void reset() {
        resetRegion();
        resetCamera();
    }

    public void resetRegion() {
        wall.setPosition(new Vector3d());
        wall.setEdge1(new Vector3d(2. * initial_scale, 0, 0));
        wall.setEdge2(new Vector3d(0, 2. * initial_scale, 0));
        WallNode node = (WallNode) wall.getNode3D();
        node.setScale(2. * initial_scale);
        Vector3d position = wall.getPosition();
        position.z = -initial_scale / 100.;
        scale_slider.setValue(initial_scale, false);
        mDLIC.setComputePlane(new RectangularPlane(new BoundingSphere(new Point3d(position), initial_scale)));

        //				RectangularPlane r = new RectangularPlane(new Vector3d(-initial_scale, -0.5*initial_scale*(4./3.), -initial_scale/100.),
        //						new Vector3d(initial_scale,0.5*(4./3.)*initial_scale,-initial_scale/100.),new Vector3d(initial_scale,-0.5*(4./3.)*initial_scale,-initial_scale/100.));
        //				mDLIC.setComputePlane(r);
    }

    public void resetCamera() {
        Vector3d initial_position = new Vector3d();
        initial_position.scale(0.05);
        setLookAt(new Point3d(initial_position.x, initial_position.y, initial_scale / 7.5), new Point3d(
            initial_position.x, initial_position.y, 0.), new Vector3d(0., 1., 0.));
    }

    

}
