package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.Spark;

public class Climber implements Runnable {

	// two cims, opposite directions

	private static Thread climberRunner = null;

	private static Boolean bool = false;
	private static Boolean button4 = false;
	private static EndCondition ending = null;

	private static Spark climber = new Spark(2);

	private static boolean backwards = false;
	private static boolean forwards = false;

	// TODO comment your code so I can actually understand it please

	private Climber(EndCondition ending) {
		Climber.ending = ending;
	}

	@Override
	public void run() {
		while (!ending.done()) {
			if (backwards) {
				climber.set(-.5);
				setBackwards(false);
			} else if (forwards) {
				climber.set(-1);
				setForwards(false);
			} else if (button4) {
				climber.set(.5);
				setButton4(false);
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
			if (bool)
				return;
			bool = true;
		}
		climberRunner = new Thread(new Climber(ending), "climber");
		climberRunner.start();
	}

	public static void setBackwards(boolean backwards) {
		Climber.backwards = backwards;
	}

	public static void setForwards(boolean forwards) {
		Climber.forwards= forwards;
	}
	
	public static void setButton4 (boolean button4) {
		Climber.button4 = button4;
	}

}
