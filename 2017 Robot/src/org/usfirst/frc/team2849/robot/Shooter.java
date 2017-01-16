package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.Talon;

public class Shooter {
	//Hopper
	/* 2. open shoot after delay
	 * 
	 */
	//Shooter
	/* 1. start motor
	 * 3. stop motor after shooting is complete
	 */
	private static Talon intakeWheel;
	private static Talon shooterWheel1;
	private static Talon shooterWheel2;
	//buttons, one to turn on turns off automatically, another to kill early,

	public static void shooterInit(){
		intakeWheel = new Talon(2);
		shooterWheel1 = new Talon(5);
		shooterWheel2 = new Talon(6);
	}
	
	public static boolean killShooter(){//call this from controller to stop firing early
		return true;
	}
	
	/**
	 * Runs the shooter wheels as long as the trigger is being pressed
	 * @param axis
	 * 			Axis of the trigger that runs the motors
	 */
	public static void shoot(double axis){// call this from controller to begin firing
		
		if(axis > 0){
			shooterWheel1.set(1.0);
			shooterWheel2.set(-1.0);
		}else{
			shooterWheel1.set(0);
			shooterWheel2.set(0);
		}
		
		//int newcase;
		
//		switch(0){
//			case 1:
//				break;
//			case 2:
//				break;
//				
//		}
	}
	/**
	 * Runs the shooter wheels as long as the button is being pressed
	 * @param button
	 * 			Value of the button that runs the motors
	 */
	public static void shoot(boolean button){// call this from controller to begin firing
			
			if(button){
				shooterWheel1.set(1.0);
				shooterWheel2.set(-1.0);
			}else{
				shooterWheel1.set(0);
				shooterWheel2.set(0);
			}
	}
	private static boolean keepshooting(){
		
		return true;//this will use any sensors we can use to see if
		//there are still balls to be fired
	}
//	private static void startshootmotors(){
//		for (Talon motor: shootermotors){
//			motor.set(.5);//-1 to 1
//		}
//	}
//	private static void stopshootmotors(){
//		for (Talon motor: shootermotors){
//			motor.set(0);
//		}
//	}
}