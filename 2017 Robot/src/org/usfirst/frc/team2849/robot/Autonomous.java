package org.usfirst.frc.team2849.robot;

import java.util.List;

public class Autonomous implements Runnable {

	private static Drive drive;
	private static List<AutoMode> mode;
	private static Thread autoRunner = null;
	private static EndCondition ending = null;
	private static Boolean threadOneUse = false;
	private static StartPosition position = null;
	private static String team = null;

	@Override
	public void run() {
		AutoMode previousMode;
		AutoMode currentMode;
		if (mode == null || mode.size() == 0) {
			return;
		}
		previousMode = mode.get(0);
		for (int i = 0; i < mode.size(); i++) {
			if (isKilled())
				break;
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
		if (isKilled())
			return;
		if(previousMode == AutoMode.GEAR){
		if (position == StartPosition.LEFT || position == StartPosition.RIGHT) {
			drive.driveDirection(0, 1000);
			drive.turnToAngle(0);
			drive.driveDirection(180);
		} else {
			if (team.equals("blue")) {
				drive.driveDirection(0, 1900);
				drive.turnAngle(-90);
				drive.driveDirection(180, 2000);
				drive.driveDirection(90);
				drive.driveDirection(180);
			} else {
				drive.driveDirection(0, 1900);
				drive.turnAngle(90);
				drive.driveDirection(180, 2000);
				drive.driveDirection(-90);
				drive.driveDirection(180);
			}
		}
		}
		// headless is default true
		// drive.switchHeadless();
		// drive.driveDirection(-180, 500);
	}

	public void shoot(AutoMode previousMode) {
		if (isKilled())
			return;
	}

	public void gear(AutoMode previousMode) {
		if (isKilled())
			return;
		if (previousMode == AutoMode.GEAR) {
			if (position == StartPosition.LEFT) {
				drive.driveDirection(180, 2000);
				drive.turnAngle(50);
				drive.driveDirection(180, 750);
			} else if (position == StartPosition.RIGHT) {
				drive.driveDirection(180, 2000);
				drive.turnAngle(-40);
				drive.driveDirection(180, 600);
				try {
					Thread.sleep(1000);
				} catch (Exception e) {

				}
				Vision.setRunAutoAlign(true);
				// drive.driveDirection(90, 1000);
			} else if (position == StartPosition.CENTER) {
				drive.driveDirection(180, 1900);
				drive.turnAngle(3);
				try {
					Thread.sleep(5000);
				} catch (Exception e) {

				}
				Vision.setRunAutoAlign(true);
			}
		} else if (previousMode == AutoMode.SHOOT) {
			drive.driveDirection(180, 2000);
		} else if (previousMode == AutoMode.CROSS) {

		}
	}

	private Autonomous(EndCondition ending, List<AutoMode> mode, StartPosition position, String team, Drive drive) {
		Autonomous.ending = ending;
		Autonomous.mode = mode;
		Autonomous.position = position;
		Autonomous.drive = drive;
		Autonomous.team = team;
	}

	public static void auto(EndCondition ending, List<AutoMode> mode, StartPosition position, String team,
			Drive drive) {
		synchronized (threadOneUse) {
			if (threadOneUse)
				return;
			threadOneUse = true;
		}
		autoRunner = new Thread(new Autonomous(ending, mode, position, team, drive), "auto");
		autoRunner.start();
	}

	enum AutoMode {
		CROSS, SHOOT, GEAR, NONE;
	}

	enum StartPosition {
		LEFT, RIGHT, CENTER
	}

	public static boolean isKilled() {
		return ending.done();
	}
}