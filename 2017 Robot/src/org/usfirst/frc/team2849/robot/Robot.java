
package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.Talon;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	Talon t1 = new Talon(0); // front left
	Talon t2 = new Talon(1); // rear left
	Talon t3 = new Talon(2); // front right
	Talon t4 = new Talon(3); // rear right
	RobotDrive drive = new RobotDrive(t1, t2, t3, t4);
	public static LogitechFlightStick joy = new LogitechFlightStick(0);
	private int frontrightstate = 0;
	private int frontleftstate = 0;
	private int backrightstate = 0;
	private int backleftstate = 0;
	private int buttonstate = 0;
	private int povAngle = -1;
	
//	private PowerDistributionPanel board = new PowerDistributionPanel(0);
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		drive.setInvertedMotor(MotorType.kFrontLeft, true);
		drive.setInvertedMotor(MotorType.kRearLeft, true);

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

	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {

	}
	
	public void teleopInit() {
		drive.stopMotor();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {	
				
		if (joy.getButton(LogitechFlightStick.BUTTON_Trigger)) {
			povAngle = joy.getPOV(0);
			switch (povAngle) {
			case 0:
				drive.mecanumDrive_Cartesian(0, -.5, 0, 0);
				break;
			case 45:
				drive.mecanumDrive_Cartesian(.25, -.25, 0, 0);
				break;
			case 90:
				drive.mecanumDrive_Cartesian(.5, 0, 0, 0);
				break;
			case 135:
				drive.mecanumDrive_Cartesian(.25, .25, 0, 0);
				break;
			case 180:
				drive.mecanumDrive_Cartesian(0, .5, 0, 0);
				break;
			case 225:
				drive.mecanumDrive_Cartesian(-.25, .25, 0, 0);
				break;
			case 270:
				drive.mecanumDrive_Cartesian(-.5, 0, 0, 0);
				break;
			case 315:
				drive.mecanumDrive_Cartesian(-.25, -.25, 0, 0);
				break;
			default:
				drive.stopMotor();
				break;
			}
		} else {
			if (joy.getXAxis() == 0 && joy.getYAxis() == 0 && joy.getZAxis() == 0) {
				drive.stopMotor();
			} else {
				drive.mecanumDrive_Cartesian(joy.getXAxis(),
						joy.getYAxis(),
						joy.getZAxis(), 0);
			}
		}
		
//		System.out.print("Talon 1: " + board.getCurrent(15));
//		System.out.print(" Talon 2: " + board.getCurrent(14));
//		System.out.print(" Talon 3: " + board.getCurrent(13));
//		System.out.println(" Talon 4: " + board.getCurrent(12));		
//		switch (frontrightstate) {
//		case 0:
//			t3.set(0);
//			break;
//		case 1:
//			t3.set(1);
//			break;
//		case 2:
//			t3.set(-1);
//			break;
//		}
//		
//		switch (frontleftstate) {
//		case 0:
//			t1.set(0);
//			break;
//		case 1:
//			t1.set(1);
//			break;
//		case 2:
//			t1.set(-1);
//			break;
//		}
//		
//		switch (backrightstate) {
//		case 0:
//			t4.set(0);
//			break;
//		case 1:
//			t4.set(1);
//			break;
//		case 2:
//			t4.set(-1);
//			break;
//		}
//		
//		switch (backleftstate) {
//		case 0:
//			t2.set(0);
//			break;
//		case 1:
//			t2.set(1);
//			break;
//		case 2:
//			t2.set(-1);
//			break;
//		}
		
//		switch (buttonstate) {
//		case 0:
//			if (joy.getButton(5)) {
//				frontleftstate++;
//				buttonstate++;
//			}
//			if (joy.getButton(3)) {
//				backleftstate++;
//				buttonstate++;
//			}
//			if (joy.getButton(4)) {
//				backrightstate++;
//				buttonstate++;
//			}
//			if (joy.getButton(6)) {
//				frontrightstate++;
//				buttonstate++;
//			}
//			break;
//		case 1:
//			if (!joy.getButton(5) && !joy.getButton(4) && !joy.getButton(3) && !joy.getButton(6)) {
//				buttonstate = 0;
//			}
//			break;
//		}
		// Drive.mecanumDrive(joy.getAxisGreaterThan(XboxController.AXIS_LEFTSTICK_X,
		// .1), joy.getAxisGreaterThan(XboxController.AXIS_LEFTSTICK_Y, .1),
		// joy.getAxisGreaterThan(XboxController.AXIS_RIGHTSTICK_X, .1));
		// Shooter.shoot(joy.getAxisGreaterThan(XboxController.AXIS_RIGHTTRIGGER,
		// .1));
		// Drive.mecanumDrive(joy.getAxisGreaterThan(XboxController.AXIS_LEFTSTICK_X,
		// .1), joy.getAxisGreaterThan(XboxController.AXIS_LEFTSTICK_Y, .1),
		// joy.getAxisGreaterThan(XboxController.AXIS_RIGHTSTICK_X, .1));

	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

	public void disabledPeriodic() {

	}
}
