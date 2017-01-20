package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.Talon;

public class Drive implements Runnable {

	private static Talon topleft = new Talon(0);
	private static Talon topright = new Talon(1);
	private static Talon bottomleft = new Talon(2);
	private static Talon bottomright = new Talon(3);
	
	private static Boolean bool = false;
	private static EndCondition ending = null;
	private static Thread driveRunner = null;
	
	private Drive (EndCondition ending) {
		Drive.ending = ending;
	}
	
	/**
	 * This will drive the robot in omnidirectional holonomic drive
	 * 
	 * @param xaxis
	 * 			The x axis of the joystick.
	 * @param yaxis
	 * 			The y axis of the joystick.
	 * @param raxis
	 * 			The rotation of the joystick.
	 * 
	 */	
	public static void mecanumDrive(double xaxis, double yaxis, double raxis){
		
		double r = Math.hypot(xaxis, yaxis);
		double robotAngle = Math.atan2(yaxis, xaxis) - Math.PI / 4;
		double cosu = Math.cos(robotAngle);
		double sinu = Math.sin(robotAngle);
		final double v1 = r * cosu + raxis;
		final double v2 = r * sinu - raxis;
		final double v3 = r * sinu + raxis;
		final double v4 = r * cosu - raxis;
		topleft.set(v1);
		topright.set(v2);
		bottomleft.set(v3);
		bottomright.set(v4);
	}
	/**
	 * This will drive the robot in a direction for the specified time.
	 * @param angleDeg
	 * 			An angle measurement in radians.
	 * @param time
	 * 			A time measurement in milliseconds.
	 */
	public static void driveDirection(double angleDeg, int time){
		
		long timer = System.currentTimeMillis();
		double angleRad = angleDeg*(Math.PI/180);
		double cosu = Math.cos(angleRad);
		double sinu = Math.sin(angleRad);
		final double v1 = cosu;
		final double v2 = sinu;
		final double v3 = sinu;
		final double v4 = cosu;
		topleft.set(v1);
		topright.set(v2);
		bottomleft.set(v3);
		bottomright.set(v4);
		while((System.currentTimeMillis() - timer)< time){
		
		}
		topleft.set(0.0);
		topright.set(0.0);
		bottomleft.set(0.0);
		bottomright.set(0.0);
		
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!ending.done()) {
			
		}
	}
	
	public static void drive(EndCondition ending) {
		synchronized (bool) {
			if (bool) return;
			bool = true;
		}
		driveRunner = new Thread(new Drive(ending), "drive");
		driveRunner.start();
	}
	

}
