package org.usfirst.frc.team2849.robot;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team2849.robot.Autonomous.AutoMode;

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
		for (int i = 0; i < mode.size(); i++) {
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
		drive.driveDirection(drive.getHeading(), 3000);
		// headless is default true
		// drive.switchHeadless();
		// drive.driveDirection(-180, 500);
	}

	public void shoot(AutoMode previousMode) {

	}

	public void gear(AutoMode previousMode) {
		if (previousMode == AutoMode.GEAR) {
			if (position == StartPosition.LEFT) {
				drive.driveDirection(0, 1000);
				drive.driveDirection(90, 1000);
			} else if (position == StartPosition.RIGHT) {
				drive.driveDirection(0, 1000);
				drive.driveDirection(-90, 1000);
			} else if (position == StartPosition.CENTER) {
				drive.driveDirection(0, 1000);
			}
		} else if (previousMode == AutoMode.SHOOT) {
			drive.driveDirection(180, 2000);
		} else if (previousMode == AutoMode.CROSS) {

		}
	}

	private Autonomous(EndCondition ending, List<AutoMode> mode, StartPosition position, Drive drive) {
		Autonomous.ending = ending;
		Autonomous.mode = mode;
		Autonomous.position = position;
		Autonomous.drive = drive;
	}

	public static void auto(EndCondition ending, List<AutoMode> mode, StartPosition position, Drive drive) {
		synchronized (threadOneUse) {
			if (threadOneUse)
				return;
			threadOneUse = true;
		}
		autoRunner = new Thread(new Autonomous(ending, mode, position, drive), "auto");
		autoRunner.start();
	}


enum AutoMode {
	CROSS, SHOOT, GEAR, NONE;
}
 enum StartPosition {
	LEFT, RIGHT, CENTER
}
}