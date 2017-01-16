
package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	Talon t1 = new Talon(0);
	Talon t2 = new Talon(1);
	WingmanExtreme joy = new WingmanExtreme(0);
	RobotDrive drive = new RobotDrive(t1, t2);
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {

    }
    
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomousInit() {
    	
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	
     //   drive.mecanumDrive_Cartesian(joy1.getRawAxis(XboxController.AXIS_LEFTSTICK_X), joy1.getRawAxis(XboxController.AXIS_LEFTSTICK_Y), joy1.getRawAxis(XboxController.AXIS_RIGHTSTICK_X), 0);
        
        Drive.mechanumDrive(joy.getAxisGreaterThan(XboxController.AXIS_LEFTSTICK_X, .1), joy.getAxisGreaterThan(XboxController.AXIS_LEFTSTICK_Y, .1), joy.getAxisGreaterThan(XboxController.AXIS_RIGHTSTICK_X, .1));
        Shooter.shoot(joy.getAxisGreaterThan(XboxController.AXIS_RIGHTTRIGGER, .1));
        
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}
