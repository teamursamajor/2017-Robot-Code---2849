package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.Talon;

public class Shooter {
	private static final double SHOOTERSPEED = 1.0;//change this to change how fast shooter goes
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
	//buttons, one to turn on turns off automatically,
	//another to kill early,

	public static void shooterInit(){
		intakeWheel = new Talon(2);
		shooterWheel1 = new Talon(5);
		shooterWheel2 = new Talon(6);
		intakeInit();
	}
	public static void intakeInit(){
		intakeWheel.set(1.0);//definitely lower
	}
	public static void stopIntake(){
		intakeWheel.set(0.0);
	}
	public static void killShooter(){//call this from controller to stop firing early
		shooterWheel1.set(0.0);
		shooterWheel2.set(0.0);
	}
	public static void startShooter(){
		shooterWheel1.set(SHOOTERSPEED);
		shooterWheel2.set(-SHOOTERSPEED);
	}
	
	/**
	 * Runs the shooter wheels as long as the trigger is being pressed
	 * @param axis
	 * 			Axis of the trigger that runs the motors
	 */
	
	static boolean currentlyshooting =false;
		
	public static void shoot(double axis){// call this from controller to begin firing
		
		/*if(axis > 0){
			shooterWheel1.set(1.0);
			shooterWheel2.set(-1.0);
		}else{
			shooterWheel1.set(0);
			shooterWheel2.set(0);
		}*/
		
		if(axis==0){
			newcase=0;//lever not down, end shooting
		}
		else{
			if (currentlyshooting){
				newcase=1;//lever is down, continue shooting
			}
			else{
				newcase=2;//lever just put down, start shooting
			}
		}
		
		switch(newcase){
			case 0:
				killShooter();
				//closeHopper();
				currentlyshooting=false;
				break;
			case 1:
				if (!keepShooting()){
					//aka magic hopper sensors say no more balls
					Robot.joy.rumbleFor(1000);
				}
				break;
			case 2:
				startShooter();
				//openHopper();
				currentlyshooting=true;
				break;
				
		}
	}
	/**
	 * Runs the shooter wheels as long as the button is being pressed
	 * @param button
	 * 			Value of the button that runs the motors
	 */
	static int newcase;
	public static void shoot(boolean button){// call this from controller to begin firing
			/*
			if(button&keepShooting()){
				shooterWheel1.set(1.0);
				shooterWheel2.set(-1.0);
			}else{
				shooterWheel1.set(0);
				shooterWheel2.set(0);
			}*/
		if(button){
			newcase=0;//lever not down, end shooting
		}
		else{
			if (currentlyshooting){
				newcase=1;//lever is down, continue shooting
			}
			else{
				newcase=2;//lever just put down, start shooting
			}
		}
		
		switch(newcase){
			case 0:
				killShooter();
				//closeHopper();
				currentlyshooting=false;
				break;
			case 1:
				if (!keepShooting()){
					//aka magic hopper sensors say no more balls
					Robot.joy.rumbleFor(1000);
				}
				break;
			case 2:
				startShooter();
				//openHopper();
				currentlyshooting=true;
				break;
				
		}
	}
	private static boolean keepShooting(){
		
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