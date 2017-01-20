package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.Talon;

public class Shooter implements Runnable {

	private static Thread shooterRunner = null;
	
	private Talon shooter = new Talon(4);
	
	private static EndCondition ending = null;
	
	private static Boolean bool = false;
	
	private Shooter(EndCondition ending) {
		this.ending = ending;
	}
	
	@Override
	public void run() {
		
		//TODO shoot code? yes, chute code.
		
		synchronized (bool) {
			bool = false;
		}
	}
	
	public static void shooter(EndCondition ending) {
		synchronized (bool) {
			if (bool) return;
			bool = true;
		}
		shooterRunner = new Thread(new Shooter(ending), "shooter");
		shooterRunner.start();
	}
}