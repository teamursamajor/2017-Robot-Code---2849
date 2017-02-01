package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.Talon;

public class Climber implements Runnable {

	// two cims, opposite directions
	
	private static Thread climberRunner = null;
	
	private static Boolean bool = false;
	
	private static EndCondition ending = null;
	
	private Talon climber = new Talon(9);
	
	private Climber(EndCondition ending) {
		Climber.ending = ending;
	}
	
	@Override
	public void run() {
		// TODO get someone to write code.
		while (!ending.done()) {
			climber.set(1);
		}
		climber.set(0);
		
		synchronized (bool) {
			bool = false;
		}
	}
	
	public static void climb(EndCondition ending) {
		synchronized (bool) {
			if (bool) return;
			bool = true;
		}
		climberRunner = new Thread(new Climber(ending), "climber");
		climberRunner.start();
	}
	
	
	
	

}
