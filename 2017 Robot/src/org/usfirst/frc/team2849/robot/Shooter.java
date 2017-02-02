package org.usfirst.frc.team2849.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;

public class Shooter implements Runnable {

	private static Thread shooterRunner = null;
	
	private Talon upperShooter = new Talon(4);
	private Talon lowerShooter = new Talon(5);
	
	private static Spark intake = new Spark(8);
	
	AnalogInput encoder = new AnalogInput(0);
	
	PIDController pid = new PIDController(.1, .1, .1, encoder, upperShooter);
	
	private static EndCondition ending = null;
	
	private static Boolean bool = false;
	
	private static AHRS ahrs = new AHRS(SPI.Port.kMXP);
	
	private Shooter(EndCondition ending) {
		Shooter.ending = ending;
	}
	
	@Override
	public void run() {
		
		//TODO shoot code? yes, chute code.
		upperShooter.set(1);
		lowerShooter.set(-1);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(!ending.done()) {
			
		}
		
		upperShooter.set(0);
		lowerShooter.set(0);
		
		synchronized (bool) {
			bool = false;
		}
	}
	
	public static void shoot(EndCondition ending) {
		synchronized (bool) {
			if (bool) return;
			bool = true;
		}
		shooterRunner = new Thread(new Shooter(ending), "shooter");
		shooterRunner.start();
	}
	
	public static void ballIntake(double xaxis, double yaxis){
		if( Math.abs( joystickAngle(xaxis, yaxis) - (ahrs.getAngle() % 360)) <= 30 ){
	
			intake.set(1.0);
			
		} else {
			
			intake.set(0.0);
			
		}
		
		
		
	}
	
	public static double joystickAngle(double joyX, double joyY){
		//gets joystick's x/y values and does arc tan, then converts to degrees from radians
		return Math.atan((-joyY / joyX) * (180 / Math.PI));
	}
	
	public static void clearIntake(){
		intake.set(-1.0);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		intake.set(0.0);
	}
}