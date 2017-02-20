
package org.usfirst.frc.team2849.robot;

import java.util.LinkedList;

import org.usfirst.frc.team2849.robot.Autonomous.AutoMode;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/* TODO Organize layout and resolve button conflicts
 * Control Scheme **Currently ideas, code does NOT match**
 * Dpad Up:
 * Dpad Down:
 * Dpad Left:
 * Dpad Right:
 * Trigger: Shooter
 * Side: some POV crap (in test) i swear you guys need to comment your code better how will anyone understand this TODO
 * Button 3:
 * Button 4: Switch Power (in test)
 * Button 5: Climber (in test)
 * Button 6: Back Climber (in test)
 * Button 7: Peg Left 
 * Button 8: Clear Intake
 * Button 9: Peg Middle
 * Button 10: some random drive thing TODO wth is it
 * Button 11: Peg Right
 * Button 12: Toggle Headless
 * Slider: Setting shooter power (?)
 * 
 */
/*
 * Shooter, Climber, Back Climber, Peg Left, Peg Middle, Peg Right, 
 * Clear Intake, Shooter Power,
 */
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	public static LogitechFlightStick joy = new LogitechFlightStick(0);

	// public static XboxController xbox = new XboxController(0);

	private static AHRS ahrs = new AHRS(SPI.Port.kMXP);
	
	private Ultrasonic ultra = new Ultrasonic(0);
	
	private Drive drive;
	private int povAngle = 0;
	private double currentAngle = 0.0;

	Latch b1 = new Latch();
	Latch xboxLatch = new Latch();
	Latch shooterLatch = new Latch();

	// Set these for motors
	private final int FRONT_LEFT_DRIVE = 0;
	private final int BACK_LEFT_DRIVE = 9;
	private final int FRONT_RIGHT_DRIVE = 1;
	private final int BACK_RIGHT_DRIVE = 8;

	private AutoSelector autoSelector;
	
	private Solenoid prox = new Solenoid(0);

	// private PowerDistributionPanel board = new PowerDistributionPanel(0);

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		/*
		 * RTFD the correct order is Front Left, Back Left, Front Right, Back
		 * Right It's in the documentation, read it for once
		 * 
		 * Alright just change the finals up above instead of this line
		 */
		drive = new Drive(FRONT_LEFT_DRIVE, BACK_LEFT_DRIVE, FRONT_RIGHT_DRIVE, BACK_RIGHT_DRIVE, ahrs);
		drive.startDrive();
		
		prox.set(true);

		ahrs.resetDisplacement();
		ahrs.zeroYaw();

		// creates camera feeds
		//Vision.visionInit(drive);

		autoSelector = new AutoSelector();
		autoSelector.initialize();
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
		System.out.println(autoSelector.getCameras());
		System.out.println(autoSelector.getAutoMode());
		System.out.println(autoSelector.getStartPosition());
		ahrs.reset();
		ahrs.zeroYaw();
		LinkedList<AutoMode> modes = new LinkedList<AutoMode>();
		modes.add(autoSelector.getAutoMode());
		Autonomous.auto(() -> !this.isAutonomous(), modes, autoSelector.getStartPosition(), drive);
		drive.setHeadingOffset(0);

		if(autoSelector.getCameras()==0){
			Vision.setCameras(1, 0);
		} else {
			Vision.setCameras(0, 1);
		}
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
//		System.out.println(drive.getHeading());
	}

	public void teleopInit() {
		ahrs.reset();
		ahrs.zeroYaw();
		ahrs.resetDisplacement();
		drive.setHeadingOffset(45);
		
		/*if(autoSelector.getCameras()==0){
			Vision.setCameras(1, 0);
		} else {
			Vision.setCameras(0, 1);
		}*/
	}

	/**
	 * This function is called periodically during operator control. Only write
	 * final code into this method. Place test code into testPeriodic().
	 */
	public void teleopPeriodic() {
		// PLACE NO TEST CODE INTO HERE
		System.out.println("Distance: " + ultra.getDistance());
		
//		try {
//			// if (joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side10))
//			// {
//			Drive.drive(joy.getXAxis(), joy.getYAxis(), -joy.getZAxis(), ahrs.getAngle());
//			// }
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//		}
//
//		if (joy.getButton(1)) {
//			Shooter.startShoot(() -> !joy.getButton(1));
//		}
//		
//		if (joy.getButton(5)) {
//			Climber.setForwards(true);
//			Climber.climb(() -> !joy.getButton(5));
//		}
//
//		// run climber to unwind rope
//		if (joy.getButton(6)) {
//			Climber.setBackwards(true);
//			Climber.climb(() -> !joy.getButton(6));
//		}
//
//		/*
//		 * switch camera from gear to shooter when trigger is pressed and then
//		 * switch back to gear when trigger is released
//		 * 
//		 * This should be getButton, not getSingleButtonPress
//		 */
//
//		// if the camera is on shooter cam when shooting is done, switch it back
//		// to front cam
//
//		if (joy.getButton(LogitechFlightStick.BUTTON_Trigger) && !Vision.getIsSwitched()) {
//			System.out.println("switch to shooter camera");
//			Vision.switchCamera(1);
//		} else if (!joy.getButton(LogitechFlightStick.BUTTON_Trigger) && Vision.getIsSwitched()) {
//			System.out.println("switch to front cam");
//			Vision.switchCamera(0);
//		}
//		currentAngle = drive.getHeading();
//		// TODO add a y deadzone for anglelock
//		drive.angleLock(joy.getAxisGreaterThan(0, 0.1), joy.getAxisGreaterThan(2, 0.1), currentAngle);
//
//		if (joy.getButton(3)) {
//			Shooter.ballIntake(1.0);
//		} else{
//			Shooter.ballIntake(0.0);
//		}
//		
//		// else {
//		// Shooter.ballIntake(joy.getXAxis(), joy.getYAxis());
//		// }
//
//		// Shooter.ballIntake(joy.getRawAxis(LogitechFlightStick.AXIS_TILT_X),
//		// joy.getRawAxis(LogitechFlightStick.AXIS_TILT_Y));
//		//
//		// if (joy.getButton(LogitechFlightStick.BUTTON_Side8)) {
//		// Shooter.clearIntake(joy);
//		// }
//
//		// run auto align for the three different gear pegs
//
//		if (joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side7)) {
//			Vision.setPegSide("left");
//			Vision.setRunAutoAlign(true);
//		} else if (joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side9)) {
//			Vision.setPegSide("middle");
//			Vision.setRunAutoAlign(true);
//		} else if (joy.getSingleButtonPress(LogitechFlightStick.BUTTON_Side11)) {
//			Vision.setPegSide("right");
//			Vision.setRunAutoAlign(true);
//		}
	}

	public void testInit() {
		ahrs.zeroYaw();
		ahrs.reset();
		drive.setHeadingOffset(0);
	}

	/**
	 * This function is called periodically during test mode Place all non-final
	 * code here instead of teleopPeriodic().
	 */
	public void testPeriodic() {

		System.out.println(drive.getHeading());
		if (joy.getButton(5)) {
			Climber.setForwards(true);
			Climber.climb(() -> !joy.getButton(5));
		}

		// run climber to unwind rope
		if (joy.getButton(6)) {
			Climber.setBackwards(true);
			Climber.climb(() -> !joy.getButton(6));
		}

		if (joy.getButton(2)) {
			povAngle = joy.getPOV(0);
			if (povAngle == -1) {
				Drive.drive(0, 0, 0, 0);
			} else {
				Drive.drive(Math.sin(povAngle * Math.PI / 180), -Math.cos(povAngle * Math.PI / 180), 0, 0);
			}
		} else {
			Drive.drive(joy.getXAxis(), joy.getYAxis(), -joy.getZAxis(), drive.getHeading());
		}

		if (joy.getSingleButtonPress(12)) {
			drive.switchHeadless();
			System.out.println("Headless: " + drive.getHeadless());
		}

		//
		// // TODO Needed?
		// // Shooter.shoot(joy.getButton(LogitechFlightStick.BUTTON_Trigger));
		//// Shooter.startShoot(() -> !joy.getButton(1), ahrs);
		//

		if (joy.getButton(1)) {
			Shooter.startShoot(() -> !joy.getButton(1));
			// TODO wth is this come on guys
			Shooter.switchPower(b1.buttonPress(joy.getButton(4)));

			Shooter.setPowerSided((joy.getAxis(3) - 1) * -0.5d);

			// // TODO Why is this code commented out??????? -Sheldon
			// // drive.angleLock(joy.getAxisGreaterThan(0, 0.1),
			// // joy.getAxisGreaterThan(2, 0.1), currentAngle);
			// //
			// Shooter.ballIntake(joy.getRawAxis(LogitechFlightStick.AXIS_TILT_X),
			// // joy.getRawAxis(LogitechFlightStick.AXIS_TILT_Y) );
			//
			// if (joy.getButton(3)) {
			// Shooter.ballIntake(1, 1);
			// }
			//// else {
			//// Shooter.ballIntake(joy.getXAxis(), joy.getYAxis());
			//// }
			//

		}

		if (joy.getButton(1)) {
			Shooter.startShoot(() -> !joy.getButton(1));
			Shooter.switchPower(b1.buttonPress(joy.getButton(4)));

			Shooter.setPowerSided((joy.getAxis(3) - 1) * -0.5d);

		}
	}

	public void disabledInit() {
		Vision.closeFile();
	}

	public void disabledPeriodic() {
		SmartDashboard.putBoolean("IMU_Connected", ahrs.isConnected());
		SmartDashboard.putBoolean("IMU_IsCalibrating", ahrs.isCalibrating());
		SmartDashboard.putNumber("IMU_Yaw", ahrs.getYaw());
		SmartDashboard.putNumber("IMU_Pitch", ahrs.getPitch());
		SmartDashboard.putNumber("IMU_Roll", ahrs.getRoll());
	}
}
