package org.usfirst.frc.team2849.robot;

import java.util.List;

public class Autonomous implements Runnable {
	/*
	 * TODO make sure all the parameters are the same between different
	 * methods/in methods bc I changed a lot of things when testing auto align.
	 * Anything changed in probably correct and I logged it through TODOs -20XX
	 */
	private static Drive drive;
	private static List<AutoMode> mode;
	private static Thread autoRunner = null;
	private static EndCondition ending = null;
	private static Boolean threadOneUse = false;
	private static StartPosition position = null;
	private static String team = null;
	private static final int GEAR_LIFT_TIME = 5000;

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
				gear(previousMode, i);
				break;
			case NONE:
				break;
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
		if (previousMode == AutoMode.GEAR) {
			if (position == StartPosition.LEFT) {
				drive.driveDirection(0, 1000);
				drive.turnAngle(-210);
				drive.driveDirection(0, 100);
			} else if (position == StartPosition.RIGHT) {
				drive.driveDirection(0, 1000);
				drive.turnAngle(225.2);
				drive.driveDirection(0, 100);
			} else {
				// TODO these angles and times arent tested
				if (team.equals("blue")) {
					drive.driveDirection(0, 1900);
					drive.turnAngle(-100);
					drive.driveDirection(180, 1700);
					drive.turnAngle(90);
					drive.driveDirection(0, 2000);
				} else {
					drive.driveDirection(0, 1900);
					drive.turnAngle(100);
					drive.driveDirection(180, 1700);
					drive.turnAngle(-90);
					drive.driveDirection(0, 2000);
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

	public void gear(AutoMode previousMode, int i) {
		if (isKilled())
			return;
		// if we're doing gear and this is the 1st automode
		if (previousMode == AutoMode.GEAR && i == 0) {
			if (position == StartPosition.LEFT) {
				drive.driveDirection(180, 1900);
				drive.turnToAngle(45);
				drive.driveDirection(180, 300);
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				Vision.setRunAutoAlign(true);
				Vision.setRunAutoAlign(true);
				try {
					Thread.sleep(GEAR_LIFT_TIME);
				} catch (Exception e) {
				}

				// TODO these numbers were all changed
			} else if (position == StartPosition.RIGHT) {
				// was 1850, made 1800
				drive.driveDirection(180, 1800);
				// was -40
				drive.turnToAngle(-42.5);
				// was 300, changed to 900
				drive.driveDirection(180, 900);
				try {
					// was 1000 is now 100
					Thread.sleep(100);
				} catch (Exception e) {
				}
				Vision.setRunAutoAlign(true);
				Vision.setRunAutoAlign(true);
				try {
					Thread.sleep(GEAR_LIFT_TIME);
				} catch (Exception e) {
				}

			} else if (position == StartPosition.CENTER) {
				//was 1400 made 700
				drive.driveDirection(190, 700);
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				Vision.setRunAutoAlign(true);
				Vision.setRunAutoAlign(true);
			}
			try {
				Thread.sleep(GEAR_LIFT_TIME);
			} catch (InterruptedException e) {
			}

			// if we arent running gear twice, straighten
			// TODO this isnt tested
			if (mode.size() > 1) {
				if (mode.get(1) != AutoMode.GEAR && position != StartPosition.CENTER) {
					drive.driveDirection(0, 1000);
					if (position == StartPosition.RIGHT) {
						drive.turnAngle(222.5);
					} else {
						// TODO is this the right angle?
						drive.turnAngle(-210);
					}
				}
			}
		} else if (previousMode == AutoMode.GEAR && i == 1) {
			// TODO all of this is completely untested
			/*
			 * this is supposed to back the robot up and redo autoalign if we
			 * call gear twice for autonomous
			 */
			drive.driveDirection(0, 1000);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			Vision.setRunAutoAlign(true);
			Vision.setRunAutoAlign(true);
			
			drive.driveDirection(0, 1000);
			if (position == StartPosition.RIGHT) {
				drive.turnAngle(222.5);
			} else {
				// TODO is this the right angle?
				drive.turnAngle(-210);
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