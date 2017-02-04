package org.usfirst.frc.team2849.robot;

import java.util.List;

public class Autonomous implements Runnable {
	
	private static Drive drive;
	private static List<AutoMode> mode;
	private static Thread autoRunner = null;
	private static EndCondition ending = null;
	private static Boolean threadOneUse = false;
	private static StartPosition position = null;
	@Override
	public void run() {
		AutoMode previousMode;
		AutoMode currentMode;
		if (mode == null || mode.size() == 0) {
			return;
		}
		previousMode = mode.get(0);
		for (int i = 0; i<mode.size(); i++) {
			currentMode = mode.get(i);
			switch (currentMode) {
			case CROSS:
				cross(previousMode);
				break;
			case SHOOT:
				shoot(previousMode);
				break;
			case GEAR:
				gear(previousMode);
				break;
			case NONE:
			default:
				// do nothing
				break;
			}
			previousMode = currentMode;
		}
		synchronized (threadOneUse) {
			threadOneUse = false;
		}
	}

	public void cross(AutoMode previousMode) {
		while (!ending.done()) {
			//Drive.drive(3, 0);
		}
	}

	public void shoot(AutoMode previousMode) {
		
	}

	public void gear(AutoMode previousMode) {
		if (previousMode == AutoMode.GEAR) {
			if (position == StartPosition.LEFT) {
				Drive.driveDirection(0, 1000);
				Drive.driveDirection(90, 1000);
			} else if (position == StartPosition.RIGHT) {
				Drive.driveDirection(0, 1000);
				Drive.driveDirection(-90, 1000);
			} else if (position == StartPosition.CENTER) {
				Drive.driveDirection(0, 1000);
			}
		} else if (previousMode == AutoMode.SHOOT){
			Drive.driveDirection(180, 2000);
		} else if (previousMode == AutoMode.CROSS) {
			
		}
	}

	private Autonomous(EndCondition ending, List <AutoMode> mode, StartPosition position, Drive drive) {
		Autonomous.ending = ending;
		Autonomous.mode = mode;
		Autonomous.position = position;
		Autonomous.drive = drive;
	}

	public static void auto(EndCondition ending, List <AutoMode> mode, StartPosition position, Drive drive) {
		synchronized (threadOneUse) {
			if (threadOneUse)
				return;
			threadOneUse = true;
		}
		autoRunner = new Thread(new Autonomous(ending, mode, position, drive), "auto");
		autoRunner.start();
	}
}
