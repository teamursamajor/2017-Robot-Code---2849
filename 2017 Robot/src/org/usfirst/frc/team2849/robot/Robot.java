
package org.usfirst.frc.team2849.robot;

import java.util.LinkedList;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	public static LogitechFlightStick joy = new LogitechFlightStick(0);
<<<<<<< HEAD
	private int frontrightstate = 0;
	private int frontleftstate = 0;
	private int backrightstate = 0;
	private int backleftstate = 0;
	private int buttonstate = 0;
	private int povAngle = -1;
	private double displacement = 0.0;
=======
>>>>>>> 300484c247dfc5a24184fbbfc3e7efbc3dc2b32b
	private static AHRS ahrs = new AHRS(SPI.Port.kMXP);
	private Vision vision;
	private double currentAngle = 0.0;
	private Drive drive; 
	

//	private PowerDistributionPanel board = new PowerDistributionPanel(0);

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		//create camera feeds
		Vision vision = new Vision();
		// System.out.println("Test 2");
		//Vision.visionInit();
		ahrs.resetDisplacement();
		ahrs.zeroYaw();
		drive = new Drive(0, 1, 3, 2);
		drive.startDrive();
		
		
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	public void autonomousInit() {
		LinkedList<AutoMode> modes = new LinkedList<AutoMode>();
		modes.add(AutoMode.GEAR);
		Autonomous.auto(() -> !this.isAutonomous(), modes, StartPosition.CENTER, drive);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {

	}

	public void teleopInit() {
		ahrs.reset();
		ahrs.zeroYaw();
		ahrs.resetDisplacement();
	}

	/**
	 * This function is called periodically during operator control.
	 * Only write final code into this method. Place test code into testPeriodic().
	 */
	public void teleopPeriodic() {
		// PLACE NO TEST CODE INTO HERE
		
<<<<<<< HEAD
		Drive.drive(joy.getXAxis(), joy.getYAxis(), -joy.getZAxis(), ahrs.getAngle());
		
		Shooter.shoot(joy.getButton(1));
=======
		Drive.drive(joy.getXAxis(), joy.getYAxis(), joy.getZAxis(), drive.getHeading());
>>>>>>> 300484c247dfc5a24184fbbfc3e7efbc3dc2b32b
		
		Shooter.shoot(joy.getButton(LogitechFlightStick.BUTTON_Trigger));
		
		// Use slider axis to set Shooter power. Change range of slider from (-1)-(1) to (0)-(1)
		Shooter.setPower((joy.getAxis(3) - 1) * -0.5d);
	}
	
	public void testInit() {
		ahrs.zeroYaw();
		ahrs.reset();
	}

	/**
	 * This function is called periodically during test mode
	 * Place all non-final code here instead of teleopPeriodic().
	 */
	public void testPeriodic() {
<<<<<<< HEAD
		if (joy.getButton(LogitechFlightStick.BUTTON_Trigger)) {
			povAngle = joy.getPOV(0);
			switch (povAngle) {
			case 0:
				Drive.drive(0, -.5, 0, 0);
				break;
			case 45:
				Drive.drive(.5, -.5, 0, 0);
				break;
			case 90:
				Drive.drive(.75, 0, 0, 0);
				break;
			case 135:
				Drive.drive(.5, .5, 0, 0);
				break;
			case 180:
				Drive.drive(0, .5, 0, 0);
				break;
			case 225:
				Drive.drive(-.5, .5, 0, 0);
				break;
			case 270:
				Drive.drive(-.75, 0, 0, 0);
				break;
			case 315:
				Drive.drive(-.5, -.5, 0, 0);
				break;
			default:
				Drive.drive(0, 0, 0, 0);
				break;
			}
		} else {
			Drive.drive(joy.getXAxis(), joy.getYAxis(), -joy.getZAxis(), drive.getHeading());
		}
		
		Shooter.ballIntake(joy.getXAxis(), joy.getYAxis());
=======
		drive.angleLock(joy.getAxisGreaterThan(0, 0.1), joy.getAxisGreaterThan(2, 0.1), currentAngle);
		Shooter.ballIntake(joy.getRawAxis(LogitechFlightStick.AXIS_TILT_X), joy.getRawAxis(LogitechFlightStick.AXIS_TILT_Y) );
>>>>>>> 300484c247dfc5a24184fbbfc3e7efbc3dc2b32b
		
		if(joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side10)){
			Shooter.clearIntake();
		}
		
		if(joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side8)) {
			vision.run();
		}
		
		if (joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side7)) {
			Vision.setPegSide("left");
			System.out.println("left");
		} else if (joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side9)) {
			Vision.setPegSide("middle");
			System.out.println("middle");
		} else if (joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side11)) {
			Vision.setPegSide("right");
			System.out.println("right");
		}
	}

	public void disabledPeriodic() {
		SmartDashboard.putBoolean("IMU_Connected", ahrs.isConnected());
		SmartDashboard.putBoolean("IMU_IsCalibrating", ahrs.isCalibrating());
		SmartDashboard.putNumber("IMU_Yaw", ahrs.getYaw());
		SmartDashboard.putNumber("IMU_Pitch", ahrs.getPitch());
		SmartDashboard.putNumber("IMU_Roll", ahrs.getRoll());
	}
}
