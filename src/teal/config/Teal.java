/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: Teal.java,v 1.37 2010/03/23 15:20:54 pbailey Exp $
 * 
 */

package teal.config;

import java.awt.*;

import javax.vecmath.*;

/**
 *  
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.37 $
 */

public class Teal {

    public static final int EFIELD = 1;
    public static final int BFIELD = 2;
    public static final int EPOTENTIAL = 4;

    public static String pathToTealWorlds = "www/TealWorlds/";

    public static int iconSize = 20;
    public static int iconBorderSize = 2;
    public static Color iconBackground = Color.white;

    public final Vector3d normalizedReferenceFrame = new Vector3d(0, 1, 0);

    public static final Vector3d DefaultOrigin = new Vector3d();

    public static final int ColorMode_GRAY = 0;
    public static final int ColorMode_COLOR = 1;
    public static final int ColorMode_MAGNITUDE = 2;
    public static final int ColorMode_BRIGHTEN = 3;
    
    public static final Color Background3DColor = new Color(37, 49, 80);
    public static Color appBackground = new Color(242, 242, 235);

    public static final Color ArrowXColor = Color.BLUE;
    public static final Color ArrowYColor = Color.RED;
    public static final Color ArrowZColor = Color.YELLOW;

    public static final Color DefaultBFieldColor = new Color(123, 155, 255);
    public static final Color DefaultBFieldLineColor = new Color(148, 190, 255);
    public static final Color DefaultEFieldColor = new Color(255, 156, 0);
    public static final Color DefaultEFieldLineColor = new Color(255, 156, 0);

//Idraw
    public static Color IDRAW_EPOTENTIAL_COLOR = new Color(128, 128, 0);
    public static Color IDRAW_EFIELD_COLOR = new Color(255, 156, 0); //new Color(66,182,182); //
    public static Color IDRAW_BFIELD_COLOR = Teal.DefaultBFieldColor;
    public static Color IDRAW_PFIELD_COLOR = Color.BLUE;
    
    public static Color fieldValueMainColor = new Color(0, 255, 0);
    public static Color fieldValueSecondColor = new Color(255, 255, 0);

    public static final Color DefaultPFieldColor = Color.cyan;
    public static final Color DefaultEPotentialFieldColor = Color.yellow;

    public static final double lightSpeed = 299792458f; //m.s-1

    public static final double G = 9.81; //m.s-2
    public static final Vector3d G_Vector = new Vector3d(0.,-G,0.);
    public static final double G_Force = 3.48e-2; // GForce in Newtons
    //public static final double G_Constant = 6.673e-11; // Gravitational Constant m3 kg-1 s-2	
    public final static double G_Constant = 1; // Test value;

    public static final double MassOfProton = 1; //kg
    public static final double massOfElectron = 9.1093897e-31f; //kg

    public static final double ElemCharge = 1; //C
    //public static final double ElemCharge = 1.60217733e-19f; //C

    public static final double PermeabilityOfVacuum = 1; //N.A-2
    public static final double PermitivityOfVacuum = 1; // F.m-1
    //public static final double permeabilityOfVacuum=12.566370614e10-7; //N.A-2
    //public static final double permitivityOfVacuum=8.854187817e-12;  // F.m-1

    public static final double fourPiPermVacuum = 4.0 * Math.PI * PermeabilityOfVacuum;
    public static final double PermitivityVacuumOver4Pi = PermitivityOfVacuum/(4.0 * Math.PI);

    public static final double PauliConstant = 0.1;
    public static final int PauliPower = 6;

    public static final double DtWorldMin = 0;
    public static final double DtWorldMax = 20;
    public static final double DtWorldDefault = 1;
    public static final double DefaultWorldDumping = 0.05;

    //Point Charge
    public static final double PointChargeMin = -0.01;
    public static final double PointChargeMax = 0.01;
    public static final double PointChargeDefaultCharge = 0.005;
    public static final double PointChargeMass = 1;
    public static final double PointChargeRadius = 0.1;
    public static final double PointChargeDumping = .0001;
    public static Color PointChargePositiveColor = new Color(245, 111, 0);
    public static Color PointChargeNegativeColor = new Color(0, 128, 255);
    public static final Color PointChargeNeutralColor = Color.lightGray;

    public static final double ElectricDipoleDefaultCharge = 0.1;
    public static final double ElectricDipoleMin = 0;
    public static final double ElectricDipoleMax = 500;
    public static final double ElectricDipoleMass = 1;
    public static final double ElectricDipoleLength = 0.2;
    public static final double ElectricDipoleRadius = 0.05;
    public static final double ElectricDipoleDamping = 0.0003;

//Magnet
    public static Color MagnetColor = new Color(0, 255, 0);
    public static final Vector3d MagnetDefaultMoment = new Vector3d(1, 0, 0);
    public static final double MagnetMass = 1;
    public static final double MagnetMassMin = 0.001;
    public static final double MagnetMassMax = 4;
    public static final double MagnetRadius = 0.15;
    public static final double MagnetLength = 1.0;
    public static final double MagnetDefaultMu = 1.0;

    public static final Color RingOfCurrentColor = Color.orange;
    public static final double RingOfCurrentMinRadius = 0.1;
    public static final double RingOfCurrentMaxRadius = 2.0;
    public static final double RingOfCurrentMass = 1;
    public static final double RingOfCurrentDefaultInductance = 1;
    public static final double RingOfCurrentDefaultCurrent = 1;
    public static final double RingOfCurrentDefaultResistance = 1;
    public static final double RingOfCurrentDefaultTorusRadius = 0.1;
    public static final double RingOfCurrentDefaultRadius = 0.6;
    public static final Vector3d RingOfCurrentDefaultMoment = new Vector3d(0, 1, 0);

    public static final int MaxConductorNumberOfCharges = 50;
    public static final double ConductorChargeMin = -0.003;
    public static final double ConductorChargeMax = 0.003;

    public static final double InfiniteWireMass = 1;
    public static final double InfiniteWireDefaultCurrent = 0;
    public static final double InfiniteWireDefaultRadius = 0.1;
    public static final double InfiniteWireDefaultLength = 2;
  
    public static final double InfiniteLineChargeMass = 1;
    public static final double InfiniteLineChargeDefaultCharge = 0;
    public static final double InfiniteLineChargeDefaultRadius = 0.1;
    public static final double InfiniteLineChargeDefaultLength = 2;
    
    public static final double FieldValueLength = 1.0f;
    public static final double FieldLinePickRadius = 0.05f;

    // AngularFrequency used in teal.physicalObjects.BField and EField
    public static final double DEFAULT_ANGULAR_FREQUENCY = 0;

    // Machine epsilon definitions
    public static final double MachineEpsilon = 2.220446049250313e-16;
    public static final double DoubleZero = 1e-14;
    
    public static final Point3d ZeroOrigin = new Point3d(0.,0.,0.);
    public static final Vector3d UpVector = new Vector3d(0.,1.,0.);

}
