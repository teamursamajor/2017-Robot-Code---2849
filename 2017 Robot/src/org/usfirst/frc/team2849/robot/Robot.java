
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
//	public static XboxController xbox = new XboxController(0);
	private static AHRS ahrs = new AHRS(SPI.Port.kMXP);

	private Vision vision;
	private Drive drive;
	private int povAngle = 0; 
	
	Latch b1 = new Latch();
	Latch xboxLatch = new Latch();
	

//	private PowerDistributionPanel board = new PowerDistributionPanel(0);

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		//create camera feeds
		drive = new Drive(0, 1, 3, 2, ahrs);
		drive.startDrive();
		// TODO please clean up after yourself -Sheldon
		// System.out.println("Test 2");
		Vision.visionInit(drive, ahrs);
		ahrs.resetDisplacement();
		ahrs.zeroYaw();
		//drive = new Drive(0, 1, 3, 2);
		//drive.startDrive();
		
		
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
		if (xboxLatch.buttonPress(joy.getButton(7)))
			drive.startDrive();
		
		if(joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side10)){
			Vision.setRunGetDistance(true);
		}
		else if(!Vision.getIsSwitched() && joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side12)){
			System.out.println("button 12 pressed 1");
			Vision.setSwitchCamera();
		}
		else if(Vision.getIsSwitched() && joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side12)){
			System.out.println("button 12 pressed 2");
			Vision.setSwitchBack();
		}
//		Drive.drive(joy.getXAxis(), joy.getYAxis(), joy.getZAxis(), drive.getHeading());
		

//		Drive.drive(joy.getXAxis(), joy.getYAxis(), -joy.getZAxis(), ahrs.getAngle());
		
//		Shooter.shoot(joy.getButton(1));
		
		// Use slider axis to set Shooter power. Change range of slider from (-1)-(1) to (0)-(1)
//		Shooter.setPower((joy.getAxis(3) - 1) * -0.5d);
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
		
//		drive.angleLock(joy.getAxisGreaterThan(0, 0.1), joy.getAxisGreaterThan(2, 0.1), currentAngle);
//		Shooter.ballIntake(joy.getRawAxis(LogitechFlightStick.AXIS_TILT_X), joy.getRawAxis(LogitechFlightStick.AXIS_TILT_Y) );
//		
//		if(joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side10)){
//			Shooter.clearIntake();
//		}
//		
//		if(joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side8)) {
//			vision.run();
//		}
//		
//		if (joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side7)) {
//			Vision.setPegSide("left");
//			System.out.println("left");
//		} else if (joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side9)) {
//			Vision.setPegSide("middle");
//			System.out.println("middle");
//		} else if (joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side11)) {
//			Vision.setPegSide("right");
//			System.out.println("right");
//		}
		if (joy.getButton(5)) {
			Climber.climb(() -> !joy.getButton(5)); 
		}
		
		if (joy.getButton(2)) {
			povAngle = joy.getPOV(0);
			//TODO This code looks like it wants to be written with trig -Sheldon
			switch (povAngle) {
			case 0:
				Drive.drive(0, -1, 0, 0);
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
				
//		Shooter.shoot(joy.getButton(LogitechFlightStick.BUTTON_Trigger));
		Shooter.startShoot(() -> !joy.getButton(1), ahrs);

		
		Shooter.switchPower(b1.buttonPress(joy.getButton(4)));
		
		Shooter.setPowerSided((joy.getAxis(3) - 1) * -0.5d);

		
		//TODO Why is this code commented out??????? -Sheldon
//		drive.angleLock(joy.getAxisGreaterThan(0, 0.1), joy.getAxisGreaterThan(2, 0.1), currentAngle);
//		Shooter.ballIntake(joy.getRawAxis(LogitechFlightStick.AXIS_TILT_X), joy.getRawAxis(LogitechFlightStick.AXIS_TILT_Y) );
		
		if (joy.getButton(3)) {
			Shooter.ballIntake(1, 1);
		} else {
			Shooter.ballIntake(joy.getXAxis(), joy.getYAxis());
		}
		
		if(joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side8)) {
			vision.run();
		}
		
		if (joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side7)) {
			System.out.println("left");
			Vision.setPegSide("left");
		} else if (joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side9)) {
			System.out.println("middle");
			Vision.setPegSide("middle");
		} else if (joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side11)) {
			System.out.println("right");
			Vision.setPegSide("right");
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
