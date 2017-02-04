package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.Spark;

public class Climber implements Runnable {

	// two cims, opposite directions
	
	private static Thread climberRunner = null;
	
	private static Boolean bool = false;
	
	private static EndCondition ending = null;
	
	private static Spark climber1 = new Spark(8);
	private static Spark climber2 = new Spark(9);
	
	//TODO please clean up after yourself -Sheldon
//	private Talon climber = new Talon(9);
	
	private Climber(EndCondition ending) {
		Climber.ending = ending;
		climber2.setInverted(true);
	}
	
	@Override
	public void run() {
		// TODO get someone to write code. && clean up commented code!!! -Sheldon
		while (!ending.done()) {
			climber1.set(1);
			climber2.set(1);
		}
		climber1.set(0);
		climber2.set(0);
		
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
