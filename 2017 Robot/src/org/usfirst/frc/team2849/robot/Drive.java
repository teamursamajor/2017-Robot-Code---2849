package org.usfirst.frc.team2849.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Spark;

public class Drive implements Runnable {

	/**
	 * can't have Sparks and RobotDrive inside another robot drive
	 * you would have to dig in to find out why, but that's the issue
	 * 
	 * - other charlie
	 */
	private static AHRS ahrs = new AHRS(SPI.Port.kMXP);
	
	private static double xaxis = 0.0;
	private static double yaxis = 0.0;
	private static double zaxis = 0.0;
	private static double angle = 0.0;
	
	private Spark frontLeftMotor2;
	private Spark frontRightMotor1;
	private Spark frontRightMotor2;
	private Spark backLeftMotor1;
	private Spark backLeftMotor2;
	private Spark backRightMotor1;
	private Spark backRightMotor2;
	private int numMotors;
	private Spark frontLeftMotor1;	

	/**
	 * Drive constructor for 4-motor drive.
	 * 
	 * @param t1 Port of the front left motor.
	 * @param t2 Port of the back left motor.
	 * @param t3 Port of the front right motor.
	 * @param t4 Port of the back right motor.
	 */
	public Drive(int t1, int t2, int t3, int t4) {
		
		frontLeftMotor1 = new Spark(t1);
		backLeftMotor1 = new Spark(t2);
		frontRightMotor1 = new Spark(t3);
		backRightMotor1 = new Spark(t4);
		frontRightMotor1.setInverted(true);
		backRightMotor1.setInverted(true);
		numMotors = 4;
	}
	
	/**
	 * Drive constructor for 8-motor drive.
	 * 
	 * @param t1 Port of the front left full cim.
	 * @param t2 Port of the front left mini cim.
	 * @param t3 Port of the front right full cim.
	 * @param t4 Port of the front right mini cim.
	 * @param t5 Port of the back left full cim.
	 * @param t6 Port of the back left mini cim.
	 * @param t7 Port of the back right full cim.
	 * @param t8 Port of the back right mini cim.
	 */
	public Drive(int t1, int t3, 
				 int t2, int t4,
				 
				 int t5, int t7, 
				 int t6, int t8) {
		
		frontLeftMotor1 = new Spark(t1);
		frontLeftMotor2 = new Spark(t2);
		frontRightMotor1 = new Spark(t3);
		frontRightMotor2 = new Spark(t4);
		backLeftMotor1 = new Spark(t5);
		backLeftMotor2 = new Spark(t6);
		backRightMotor1 = new Spark(t7);
		backRightMotor2 = new Spark(t8);
		numMotors = 8;
		
	}
	
	
	 /**
	   * Normalize all wheel speeds if the magnitude of any wheel is greater than 1.0.
	   */
	 protected void normalize(double[] wheelSpeeds) {
		    double maxMagnitude = Math.abs(wheelSpeeds[0]);
		    for (int i = 1; i < numMotors; i++) {
		      double temp = Math.abs(wheelSpeeds[i]);
		      if (maxMagnitude < temp) {
		        maxMagnitude = temp;
		      }
		    }
		    if (maxMagnitude > 1.0) {
		      for (int i = 0; i < numMotors; i++) {
		        wheelSpeeds[i] = wheelSpeeds[i] / maxMagnitude;
		      }
		    }
		  }
	 /**
	   * Rotate a vector in Cartesian space.
	   */
	  protected double[] rotateVector(double x, double y, double angle) {
	    double cosA = Math.cos(angle * (Math.PI / 180.0));
	    double sinA = Math.sin(angle * (Math.PI / 180.0));
	    double[] out = new double[2];
	    out[0] = x * cosA - y * sinA;
	    out[1] = x * sinA + y * cosA;
	    return out;
	  }

	/**
	 * This will drive the robot in omnidirectional holonomic drive.
	 * Do NOT call this method from Robot.java! Call drive.drive() instead!
	 * 
	 * @param xaxis
	 * 			The x axis of the joystick.
	 * @param yaxis
	 * 			The y axis of the joystick.
	 * @param raxis
	 * 			The rotation of the joystick.
	 * @param gyroAngle
	 * 			The input of the gyro.
	 * 
	 */
	private void mecanumDrive(double xaxis, double yaxis, double raxis, double gyroAngle) {

		    double xIn = xaxis;
		    double yIn = yaxis;
		    // Negate y for the joystick.
		    yIn = -yIn;
		    raxis = -raxis;
		    // Compenstate for gyro angle.
		    double[] rotated = rotateVector(xIn, yIn, gyroAngle);
		    xIn = rotated[0];
		    yIn = rotated[1];

		    if(numMotors == 4){
			    double[] wheelSpeeds = new double[numMotors];
			    wheelSpeeds[0] = xIn + yIn + raxis;		    
			    wheelSpeeds[1] = -xIn + yIn - raxis;		    
			    wheelSpeeds[2] = -xIn + yIn + raxis;	    
			    wheelSpeeds[3] = xIn + yIn - raxis;
			    
			    normalize(wheelSpeeds);
			    frontLeftMotor1.set(wheelSpeeds[0]);			    
			    frontRightMotor1.set(wheelSpeeds[1]);		    
			    backLeftMotor1.set(wheelSpeeds[2]);		    
			    backRightMotor1.set(wheelSpeeds[3]);
			    
		    }else{
		    	double[] wheelSpeeds = new double[numMotors];
			    wheelSpeeds[0] = xIn + yIn + raxis;	
			    wheelSpeeds[1] = xIn + yIn + raxis;	
			    wheelSpeeds[2] = -xIn + yIn - raxis;
			    wheelSpeeds[3] = -xIn + yIn - raxis;
			    wheelSpeeds[4] = -xIn + yIn + raxis;
			    wheelSpeeds[5] = -xIn + yIn + raxis;
			    wheelSpeeds[6] = xIn + yIn - raxis;
			    wheelSpeeds[7] = xIn + yIn - raxis;
			    
			    normalize(wheelSpeeds);
			    frontLeftMotor1.set(wheelSpeeds[0]);
			    frontLeftMotor2.set(wheelSpeeds[1]);
			    frontRightMotor1.set(wheelSpeeds[2]);
			    frontRightMotor2.set(wheelSpeeds[3]);
			    backLeftMotor1.set(wheelSpeeds[4]);
			    backLeftMotor2.set(wheelSpeeds[5]);
			    backRightMotor1.set(wheelSpeeds[6]);
			    backRightMotor2.set(wheelSpeeds[7]);
		    }

		  
		  }
//		double r = Math.hypot(xaxis, yaxis);
//		double robotAngle = Math.atan2(yaxis, xaxis) - Math.PI / 4;
//		double cosu = Math.cos(robotAngle);
//		double sinu = Math.sin(robotAngle);
//		final double v1 = r * cosu + raxis;
//		final double v2 = r * sinu - raxis;
//		final double v3 = r * sinu + raxis;
//		final double v4 = r * cosu - raxis;
//		topleft.set(v1);
//		topright.set(v2);
//		bottomleft.set(v3);
//		bottomright.set(v4);
	

	/**
	 * Drives the robot in a direction without a stop.
	 * 
	 * @param angleDeg
	 * 			An angle measurement in degrees.
	 */
	public void driveDirection(double angleDeg) {

		mecanumDrive(1.0, 0, 0, -angleDeg);

	}

	/**
	 * This will drive the robot in a direction for the specified time.
	 * 
	 * @param angleDeg
	 * 			An angle measurement in degrees.
	 * @param time
	 * 			A time measurement in milliseconds.
	 */
	public static void driveDirection(double angleDeg, int time) {
		double timer = System.currentTimeMillis();
		
		while (System.currentTimeMillis() - timer < time) {
			drive(-.5, 0, 0, -angleDeg);
		}
		
		drive(0, 0, 0, 0);
		
//		topleft.set(0.0);
//		topright.set(0.0);
//		bottomleft.set(0.0);
//		bottomright.set(0.0);

	}
	/**
	 * This will turn the robot at a steady rate clockwise until it is pointing at the specified angle.
	 * 
	 * @param angleDeg 
	 * 			An angle measurement in degrees,
	 */
	public void driveAngle(double angleDeg){
		
		mecanumDrive(0, 0, .5, 0);
		if(getHeading()==angleDeg){
			mecanumDrive(0, 0, 0, 0);
		}

	}
	/**
	 * This will drive the robot the specified distance at the specified angle.
	 * 
	 * @param distance
	 * 			A distance in meters.
	 * @param angleDeg
	 * 			An angle measurement in degrees.
	 */
	public void mechDriveDistance(double distance, double angleDeg) { // in meters

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

	/**
	 * Runs automatically after calling startDrive(). Will continue running in the drive thread while the robot is on.
	 * Place all calls to drive code inside the while loop.
	 */
	public void run() {
		while (true) {
			mecanumDrive(Drive.xaxis, Drive.yaxis, Drive.zaxis, Drive.angle);
		}
	}
	
	/**
	 * Starts the drive thread. Call this after initializing a Drive object and before any other Drive methods.
	 */
	public void startDrive() {
		new Thread(this, "driveThread").start();
	}
	
	/** 
	 * Call THIS method to drive. Must call drive.startDrive() first to initialize the thread.
	 * 
	 * @param xaxis The x-axis of the joystick
	 * @param yaxis The y-axis of the joystick
	 * @param zaxis The rotational axis of the joystick
	 * @param angle The angle read from the gyro. Pass 0 for robot-centric driving
	 * 
	 */
	public static void drive(double xaxis, double yaxis, double zaxis, double angle) {
		Drive.xaxis = xaxis;
		Drive.yaxis = yaxis;
		Drive.zaxis = zaxis;
		Drive.angle = angle;
	}
	
	public double getHeading(){
		double angle = ahrs.getAngle();
		
		if (angle > 0){
		
			angle %= 360;
			
		} else if(angle < 0){
			
			angle =-(Math.abs(angle) % 360 ) + 360;
		
		}
		return angle;
	}
	
	public void angleLock(double xaxis, double zaxis, double currentAngle){
		
		if(xaxis > 0 && zaxis == 0){
			
			driveAngle(currentAngle);
			
		}
		
	}
}
