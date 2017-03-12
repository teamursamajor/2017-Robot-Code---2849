package org.usfirst.frc.team2849.robot;

import java.util.List;

public class Autonomous implements Runnable {
	private static Drive drive;
	private static List<AutoMode> mode;
	private static Thread autoRunner = null;
	private static EndCondition ending = null;
	private static Boolean threadOneUse = false;
	private static StartPosition position = null;
	@SuppressWarnings("unused")
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
	} // end run

	public void cross(AutoMode previousMode) {
		if (isKilled())
			return;
		if (previousMode == AutoMode.GEAR) {
			if (position == StartPosition.LEFT) {
				/*
				 * in gear position, straighten out by backing up 1000, turning
				 * to face the field, and drive forward 100
				 */

				gearToStraight();
			} else if (position == StartPosition.RIGHT) {
				/*
				 * in gear position, straighten out by backing up 1000, turning
				 * to face the field, and drive forward 100
				 */
				gearToStraight();

			}

		} else if (previousMode == AutoMode.CROSS) {
			if (position != StartPosition.CENTER) {
				// moves forward straight from the wall on the left or right
				// side, no gear
				drive.driveDirection(0, 1800);
			}
		}

	} // end cross

	// not used
	public void shoot(AutoMode previousMode) {
		if (isKilled())
			return;
	}

	public void gear(AutoMode previousMode, int i) {
		if (isKilled())
			return;

		// if we're doing gear and this is the 1st time (1st selector)
		if (previousMode == AutoMode.GEAR) {
			if (i == 0) {
				if (position == StartPosition.LEFT) {
					/*
					 * drive forward 1750, turn 45 degrees clockwise and drive
					 * forward again so we are close to the peg and on the right
					 * angle. Then we wait so the robot doesn't roll and run
					 * auto align twice, then wait so gear can be pulled.
					 */

					wallToGear("left");

				} else if (position == StartPosition.RIGHT) {

					/*
					 * drive forward 1750, turn 42.5 degrees counterclockwise
					 * and drive forward again so we are close to the peg and on
					 * the right angle. Then we wait so the robot doesn't roll
					 * and run auto align twice, then wait so gear can be
					 * pulled.
					 */

					wallToGear("right");

				} else if (position == StartPosition.CENTER) {
					centerToGear();
				}

				//if we aren't center, theres a second auto, and its not gear, straighten
				if (position != StartPosition.CENTER) {
					if (mode.size() > 1) {
						if (mode.get(1) != AutoMode.GEAR) {
							gearToStraight();
						}
					}
				}
			} else if (i == 1) {
				/*
				 * this is supposed to back the robot up and redo autoalign if
				 * we call gear twice for autonomous
				 */

				gearToGear();
				gearToStraight();
			}

		} else if (previousMode == AutoMode.SHOOT) {
			drive.driveDirection(180, 2000);
		} else if (previousMode == AutoMode.CROSS) {
		} // end of previousmode = gear
	} // end of gear

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

	public static void gearToGear() {
		drive.driveDirection(0, 300);
		Vision.setRunAutoAlign(true);
		Vision.setRunAutoAlign(true);

	}

	public static void gearToStraight() {
		drive.driveDirection(0, 1000);
		drive.turnToAngle(0);
		drive.driveDirection(180, 100);
	}

	public static void wallToGear(String side) {
		// initial drive
		drive.driveDirection(180, 1750);
		// turn, angle depends on side: right or left
		if (side.equals("right")) {
			drive.turnToAngle(45);
		} else if (side.equals("left")) {
			drive.turnToAngle(-42.5);
		}
		// second drive towards peg
		drive.driveDirection(180, 750);

		// wait for the robot to stop rolling
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

		// auto align twice, moves forward slightly after each auto align
		Vision.setRunAutoAlign(true);
		Vision.setRunAutoAlign(true);

		// waits for 5 seconds for the pilot to pick up the gear
		try {
			Thread.sleep(GEAR_LIFT_TIME);
		} catch (Exception e) {
		}

	}

	public static void centerToGear() {

		// initial drive forward from wall for 3.5 seconds
		drive.driveDirection(180, 3500);
		try {
			// waits two seconds so pilot can get gear or wait for readjustment
			Thread.sleep(2000);
		} catch (Exception e) {
		}

		// back of robot moves backwards for 50 ms, should be about 3 in
		/*
		 * this is in case the gear is put in too slanted and needs to get
		 * lifted up a bit by having the robot move back and letting the peg
		 * pull the gear forward
		 */
		drive.driveDirection(0, 50);

		// waits 5 seconds for driver to get peg
		try {
			Thread.sleep(GEAR_LIFT_TIME);
		} catch (InterruptedException e) {
		}
	}
}