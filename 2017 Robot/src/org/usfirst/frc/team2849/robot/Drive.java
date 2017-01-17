package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.Talon;

public class Drive {

	private static Talon topleft = new Talon(0);
	private static Talon topright = new Talon(1);
	private static Talon bottomleft = new Talon(2);
	private static Talon bottomright = new Talon(3);
	
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
		final double v1 = r * Math.cos(robotAngle) + raxis;
		final double v2 = r * Math.sin(robotAngle) - raxis;
		final double v3 = r * Math.sin(robotAngle) + raxis;
		final double v4 = r * Math.cos(robotAngle) - raxis;
		
		topleft.set(v1);
		topright.set(v2);
		bottomleft.set(v3);
		bottomright.set(v4);
	
		if (xaxis != 0 || yaxis != 0 || raxis!= 0){
			Shooter.intakeValue(1);
		}
		else{
			Shooter.intakeValue(0);
		}
	}
	

}
