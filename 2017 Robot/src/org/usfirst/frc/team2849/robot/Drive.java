package org.usfirst.frc.team2849.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.hal.HAL;

public class Drive implements Runnable {

	/**
	 * can't have talons and RobotDrive inside another robot drive
	 * you would have to dig in to find out why, but that's the issue
	 * 
	 * - other charlie
	 */
//	private static Talon topleft = new Talon(0);
//	private static Talon topright = new Talon(1);
//	private static Talon bottomleft = new Talon(2);
//	private static Talon bottomright = new Talon(3);
	private static AHRS ahrs = new AHRS(SPI.Port.kMXP);
//	private static RobotDrive drive = new RobotDrive(topleft, topright, bottomleft, bottomright);
	private static double distance;
	private static double angle;

	private static Boolean bool = false;
	private static EndCondition ending = null;
	private static Thread driveRunner = null;
	
	private final double RL_SCALE = .05;
	private final double RL_THRESH = .25;

//	private Drive(double distance, double angle) {
//		Drive.distance = distance;
//		Drive.angle = angle;
//	}
	
	public Drive(SpeedController t1, SpeedController t2, SpeedController t3, SpeedController t4) {
	}
	
	public Drive() {
	}

	/**
	 * This will drive the robot in omnidirectional holonomic drive
	 * 
	 * @param xaxis
	 *            The x axis of the joystick.
	 * @param yaxis
	 *            The y axis of the joystick.
	 * @param raxis
	 *            The rotation of the joystick.
	 * 
	 */
	public static void mecanumDrive(double xaxis, double yaxis, double raxis) {

		double r = Math.hypot(xaxis, yaxis);
		double robotAngle = Math.atan2(yaxis, xaxis) - Math.PI / 4;
		double cosu = Math.cos(robotAngle);
		double sinu = Math.sin(robotAngle);
		final double v1 = r * cosu + raxis;
		final double v2 = r * sinu - raxis;
		final double v3 = r * sinu + raxis;
		final double v4 = r * cosu - raxis;
//		topleft.set(v1);
//		topright.set(v2);
//		bottomleft.set(v3);
//		bottomright.set(v4);
	}

	/**
	 * Drives the robot in a direction without a stop.
	 * 
	 * @param angleDeg
	 *            An angle measurement in radians.
	 */
	public static void driveDirection(double angleDeg) {

		double angleRad = angleDeg * (Math.PI / 180);
		double cosu = Math.cos(angleRad);
		double sinu = Math.sin(angleRad);
		final double v1 = cosu;
		final double v2 = sinu;
		final double v3 = sinu;
		final double v4 = cosu;
//		topleft.set(v1);
//		topright.set(v2);
//		bottomleft.set(v3);
//		bottomright.set(v4);

	}

	/**
	 * This will drive the robot in a direction for the specified time.
	 * 
	 * @param angleDeg
	 *            An angle measurement in radians.
	 * @param time
	 *            A time measurement in milliseconds.
	 */
	public static void driveDirection(double angleDeg, int time) {

		long timer = System.currentTimeMillis();
		double angleRad = angleDeg * (Math.PI / 180);
		double cosu = Math.cos(angleRad);
		double sinu = Math.sin(angleRad);
		final double v1 = cosu;
		final double v2 = sinu;
		final double v3 = sinu;
		final double v4 = cosu;
//		topleft.set(v1);
//		topright.set(v2);
//		bottomleft.set(v3);
//		bottomright.set(v4);
		while ((System.currentTimeMillis() - timer) < time) {

		}
//		topleft.set(0.0);
//		topright.set(0.0);
//		bottomleft.set(0.0);
//		bottomright.set(0.0);

	}
	public static void driveAngle(double angleDeg){
//		drive.mecanumDrive_Cartesian(0, 0, .5, 0);
//		if(ahrs.getAngle() == angleDeg){
//			drive.stopMotor();
//		}
	}
	public static void mechDriveDistance(double distance, double angleDeg) { // in meters

		double displacement = 0;
		ahrs.resetDisplacement();

		driveDirection(angleDeg);
		long time = System.currentTimeMillis();
		while (displacement <= distance) {
			displacement += Math.sqrt(Math.pow(ahrs.getRawAccelX() * 9.81, 2) + Math.pow(ahrs.getRawAccelZ() * 9.81, 2))
					* .5 * Math.pow((System.currentTimeMillis() - time) / 1000, 2);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
//		topleft.set(0.0);
//		topright.set(0.0);
//		bottomleft.set(0.0);
//		bottomright.set(0.0);

		// VELOCITY
		// driveDirection(angle);
		// while(displacement <= distance){
		// long time = System.currentTimeMillis();
		// Math.sqrt(Math.pow(ahrs.getVelocityX(), 2) +
		// Math.pow(ahrs.getVelocityZ(),
		// 2))*((System.currentTimeMillis()/1000)-time)
		// }
		// topleft.set(0.0);
		// topright.set(0.0);
		// bottomleft.set(0.0);
		// bottomright.set(0.0);

		// DISPLACEMENT
		// driveDirection(angle);
		// while(Math.sqrt(Math.pow(ahrs.getDisplacementX(), 2) +
		// Math.pow(ahrs.getDisplacementZ(), 2)) < distance){
		//
		// }
		// topleft.set(0.0);
		// topright.set(0.0);
		// bottomleft.set(0.0);
		// bottomright.set(0.0);
	}

	public void run() {
		// TODO Auto-generated method stub
		long time = System.currentTimeMillis();
		double displacement = 0.0;
		while (true) {
			displacement += Math.sqrt(Math.pow(ahrs.getRawAccelX(), 2) + Math.pow(ahrs.getRawAccelZ(), 2))
					* .5 * Math.pow((System.currentTimeMillis() - time) / 1000, 2);
//			System.out.println("X Accel: " + ahrs.getRawAccelX());
//			System.out.println("Z Accel: " + ahrs.getRawAccelY());
//			System.out.println(displacement);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void startDrive() {
		driveRunner = new Thread(new Drive(), "drive");
		driveRunner.start();

	}
	
//	@SuppressWarnings("ParameterName")
//	  public void mecanumDrive_Cartesian(double x, double y, double rotation, double gyroAngle) {
//	    if (!kMecanumCartesian_Reported) {
//	      HAL.report(tResourceType.kResourceType_RobotDrive, getNumMotors(),
//	          tInstances.kRobotDrive_MecanumCartesian);
//	      kMecanumCartesian_Reported = true;
//	    }
//	    @SuppressWarnings("LocalVariableName")
//	    double xIn = x;
//	    @SuppressWarnings("LocalVariableName")
//	    double yIn = y;
//	    // Negate y for the joystick.
//	    yIn = -yIn;
//	    // Compenstate for gyro angle.
//	    double[] rotated = rotateVector(xIn, yIn, gyroAngle);
//	    xIn = rotated[0];
//	    yIn = rotated[1];
//
//	    double[] wheelSpeeds = new double[kMaxNumberOfMotors];
//	    wheelSpeeds[MotorType.kFrontLeft.value] = xIn + yIn + rotation;
//	    wheelSpeeds[MotorType.kFrontRight.value] = -xIn + yIn - rotation;
//	    wheelSpeeds[MotorType.kRearLeft.value] = -xIn + yIn + rotation;
//	    wheelSpeeds[MotorType.kRearRight.value] = xIn + yIn - rotation;
//
//	    normalize(wheelSpeeds);
//	    m_frontLeftMotor.set(wheelSpeeds[MotorType.kFrontLeft.value] * m_maxOutput);
//	    m_frontRightMotor.set(wheelSpeeds[MotorType.kFrontRight.value] * m_maxOutput);
//	    double rearLeftValue = wheelSpeeds[MotorType.kRearLeft.value] * m_maxOutput;
//	    System.out.println(rearLeftValue);
//	    if (Math.abs(rearLeftValue) < RL_THRESH) {
//	    	rearLeftValue += RL_SCALE * Math.signum(rearLeftValue);
//	    }
//	    m_rearLeftMotor.set(rearLeftValue);
//	    m_rearRightMotor.set(wheelSpeeds[MotorType.kRearRight.value] * m_maxOutput);
//
//	    if (m_safetyHelper != null) {
//	      m_safetyHelper.feed();
//	    }
//	  }

}
