package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.Spark;

public class Climber implements Runnable {

	// two cims, opposite directions
	
	private static Thread climberRunner = null;
	
	private static Boolean bool = false;
	
	private static EndCondition ending = null;
	
	private static Spark climber = new Spark(0);
	
	private static boolean forwards = false;
	private static boolean backwards = false;
	
	//TODO please clean up after yourself -Sheldon
	//TODO comment your code so I can actually understand it please
//	private Talon climber = new Talon(9);
	
	private Climber(EndCondition ending) {
		Climber.ending = ending;
	}
	
	@Override
	public void run() {
		// TODO get someone to write code. && clean up commented code!!! -Sheldon
		while (!ending.done()) {
			if(forwards){
				climber.set(1);
				setForwards(false);
			} else if(backwards){
				climber.set(-.5);
				setBackwards(false);
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
	
	public static void setForwards(boolean forwards){
		Climber.forwards=forwards;
	}
	
	public static void setBackwards(boolean backwards){
		Climber.backwards=backwards;
	}

}
