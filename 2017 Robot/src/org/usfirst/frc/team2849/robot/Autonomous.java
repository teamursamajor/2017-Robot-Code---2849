package org.usfirst.frc.team2849.robot;

public class Autonomous implements Runnable {
	
	private static AutoMode mode;
	private static Thread autoRunner = null;
	private static EndCondition ending = null;
	private static Boolean bool = false;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		switch (mode) {
		case CROSS:
			cross();
			break;
		case SHOOT:
			shoot();
			break;
		case GEAR:
			gear();
			break;
		case NONE:
		default:
			// do nothing
			break;
		}
		synchronized (bool) {
			bool = false;
		}
	}
	
	public void cross() {
		Drive.drive(3, 0);
	}
	
	public void shoot() {
		
	}
	
	public void gear() {
		
	}
	
	private Autonomous(EndCondition ending, AutoMode mode) {
		Autonomous.ending = ending;
		Autonomous.mode = mode;
	}
	public static void auto(EndCondition ending, AutoMode mode) {
		synchronized (bool) {
			if (bool) return;
			bool = true;
		}
		autoRunner = new Thread(new Autonomous(ending, mode), "auto");
		autoRunner.start();
	}
}

