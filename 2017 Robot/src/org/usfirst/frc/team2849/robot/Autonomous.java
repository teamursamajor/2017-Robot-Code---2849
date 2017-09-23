package org.usfirst.frc.team2849.robot;

import java.util.List;

// TODO driveDirection: 0 moves gear towards DS, 180 moves gear towards field
// TODO turnToAngle: 0 moves gear towards field, 180 moves gear towards DS
public class Autonomous implements Runnable {

	private static Drive drive;
	private static List<AutoMode> mode;
	private static Thread autoRunner = null;
	private static EndCondition ending = null;
	private static Boolean threadOneUse = false;
	private static StartPosition position = StartPosition.LEFT;
	private static String team = null;
	private static final int GEAR_LIFT_TIME = 5000;
	private static boolean killAuto = false;

	@Override
	public void run() {
		AutoMode previousMode;
		AutoMode currentMode;
		if (killAuto) {
			return;
		}
		// if there is no auto methods in the mode array, return
		if (mode == null || mode.size() == 0) {
			return;
		}
		previousMode = mode.get(0);
		// if there are still auto modes left in the array, continue to do them
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

	// for crossing the line at the beginning of auto
	public void cross(AutoMode previousMode) {
		// if auto ends stop autoing
		if (isKilled()) {
			return;
		}

		// TODO DELETE THIS
		drive.turnToAngle(270);

		if (previousMode == AutoMode.GEAR) {
		} else if (previousMode == AutoMode.CROSS) {
			if (position != StartPosition.CENTER) {
				// moves forward straight from the wall on the left or right
				// side, no gear
				drive.driveDirection(180, 1900);
			}
		} else {
		}

	} // end cross

	public void shoot(AutoMode previousMode) {
		if (isKilled()) {
			return;
		}
		if (previousMode == AutoMode.GEAR) {
			gearToBoiler(position, team);
			dumpBalls();
		} else if (previousMode == AutoMode.SHOOT) {
			turnByWall();
			wallToBoiler(position, team);
			dumpBalls();
		} else {

		}
	}

	public void gear(AutoMode previousMode, int i) {
		if (isKilled()) {
			return;
		}

		if (previousMode == AutoMode.GEAR) {
			// if we're doing gear and this is the 1st time (1st selector)
			if (i == 0) {
				if (position == StartPosition.LEFT) {
					leftToGear();
					// Vision.setRunAutoAlign(true);
				} else if (position == StartPosition.RIGHT) {
					rightToGear();
					// Vision.setRunAutoAlign(true);
				} else if (position == StartPosition.CENTER) {
					centerToGear();
				}

				// if not center & theres a second auto thats not gear,
				// straighten - this isn't happening anymore
				if (position != StartPosition.CENTER) {
					if (mode.size() > 1) {
						if (mode.get(1) != AutoMode.GEAR) {
							// gearToStraight();
						}
					}
				}
			} else if (i == 1) {
				/*
				 * this is supposed to back the robot up and redo autoalign if
				 * we call gear twice for autonomous, then straightens out
				 */

				// gearToGear();
				// gearToStraight();
			} else {
				System.out.println("error in selecting auto: empty function selected");
			}
		}
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
		// creates a new auto thread and makes sure it doesn't get run more than
		// once
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
		return ending.done() || !Robot.getIsAutonomous();
	}

	/**
	 * Moves the robot from the gear peg and orients it to make it straight,
	 * then moves forward
	 */
	public static void gearToAngle(double angle) {
		drive.driveDirection(0, 950);
		drive.turnToAngle(angle);
	}

	/**
	 * Moves the robot from the wall on the right to the gear peg by moving
	 * forward, turning, moving forward again, waiting 3 seconds, then moving
	 * back slightly
	 * 
	 */
	public static void rightToGear() {
		// 8.35 seconds, 1600 to 1500
		drive.driveDirection(180, 1600);
		// added 180, originally -42.5, 137.5
		//changed from -35 to -40 (4/7/17)
		drive.turnToAngle(-40);

		// drive towards peg
		drive.driveDirection(180, 750);

		// wait for the robot to stop rolling
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

		// second move forward at halfspeed
		drive.driveDirection(180, 900, 0.25);

		// try {
		// Thread.sleep(3000);
		// } catch (Exception e) {
		// }

		// move back slightly
		// drive.driveDirection(0, 150);

		// waits for 5 seconds for the pilot to pick up the gear
		try {
			Thread.sleep(GEAR_LIFT_TIME);
		} catch (Exception e) {
		}
	}

	/**
	 * Moves the robot from the wall on the left to the gear peg by moving
	 * forward, turning, moving forward again, waiting 3 seconds, then moving
	 * back slightly
	 * 
	 */
	public static void leftToGear() {
		// 8.85 seconds
		drive.driveDirection(180, 1400);
		// TODO subtracted 180, originally 48, -137.5
		drive.turnToAngle(48);

		// drive towards peg
		drive.driveDirection(180, 750);

		// wait for the robot to stop rolling
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

		// drive forward at half speed
		drive.driveDirection(160, 1500, 0.25);

		// try {
		// Thread.sleep(3000);
		// } catch (Exception e) {
		// }

		// move back slightly
		// drive.driveDirection(0, 150);

		// waits for 5 seconds for the pilot to pick up the gear
		try {
			Thread.sleep(GEAR_LIFT_TIME);
		} catch (Exception e) {
		}
	}

	/**
	 * Moves the robot from the wall on the left or right to the gear peg by
	 * moving forward, turning to an angle depending on side, moving forward
	 * again, then auto aligning twice
	 * 
	 * @param side
	 *            *PROBABLY* UNUSED String "left" or "right" that tells you
	 *            which side peg the robot is on. Used to determine which angle
	 *            to turn to
	 */
	public static void wallToGear(String side) {
		if (side.equals("right")) {
			drive.driveDirection(180, 1600);
			// TODO added 180, originally -42.5, 137.5
			drive.turnToAngle(-35);
		} else if (side.equals("left")) {
			drive.driveDirection(180, 1450);
			// TODO subtracted 180, originally 48, -137.5
			drive.turnToAngle(48);
		}

		// drive towards peg
		drive.driveDirection(180, 750);

		// wait for the robot to stop rolling
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

		// auto align twice, moves forward slightly after each auto align
		// Vision.setRunAutoAlign(true);
		// Vision.setRunAutoAlign(true);
		if (side.equals("right")) {
			drive.driveDirection(180, 750);
		} else if (side.equals("left")) {
			drive.driveDirection(160, 750);
		}

		// try {
		// Thread.sleep(3000);
		// } catch (Exception e) {
		// }

		// move back slightly
		// drive.driveDirection(0, 150);

		// waits for 5 seconds for the pilot to pick up the gear
		try {
			Thread.sleep(GEAR_LIFT_TIME);
		} catch (Exception e) {
		}
	}

	/**
	 * Moves the robot from the center wall to the gear by moving forward (3.5
	 * secs), waiting (2 seconds), moving backwards 3 inches (50 ms), and then
	 * stopping until auto ends.
	 * 
	 */
	public static void centerToGear() {
		// 7.1 seconds
		// WHERE ISAIAH WROTE HIS NAME NEEDS TO BE LINED UP WITH THE DAVIT
		// drive forward
		drive.driveDirection(180, 1600);
		// drive forward at half speed
		drive.driveDirection(180, 500, 0.25);

		// waits 5 seconds for driver to get peg
		try {
			Thread.sleep(GEAR_LIFT_TIME);
		} catch (InterruptedException e) {
		}
		// TODO we have a solid 5 seconds of doing nothing after this is done.
		// we could possibly shoot
	}

	public void gearToBoiler(StartPosition startPosition, String team) {
		// the boiler is on the left if blue and right if red
		if (team.equals("blue")) {
			if (startPosition.equals("left")) {
				gearToAngle(90.0);
				drive.driveDirection(270, 750, 1);
			} else if (startPosition.equals("right")) {
				gearToAngle(90.0);
				drive.driveDirection(270, 800, 1);
			} else {
				drive.driveDirection(0, 462, 1);
				drive.turnToAngle(90);
				drive.driveDirection(270, 463, 1);
			}
		} else if (team.equals("red")) {
			if (startPosition.equals("left")) {
				gearToAngle(270.0);
				drive.driveDirection(90, 750, 1);
			} else if (startPosition.equals("right")) {
				gearToAngle(270.0);
				drive.driveDirection(90, 800, 1);
			} else {
				drive.driveDirection(0, 462, 1);
				drive.turnToAngle(270.0);
				drive.driveDirection(90, 463, 1);
			}
		} else {
		}
		wallToBoiler(startPosition, team);
	}

	public void dumpBalls() {
		Climber.climb(() -> Robot.getIsAutonomous());
		Climber.setForwards(true);
	}

	public void wallToBoiler(StartPosition startPosition, String team) {
		if (team.equals("blue")) {
			if (startPosition.equals("left")) {
				drive.driveDirection(0, 475, 1);
			} else if (startPosition.equals("right")) {
				drive.driveDirection(0, 1450, 1);
			} else {
				drive.driveDirection(0, 850, 1);
			}
			// move left to get to low goal
			drive.driveDirection(90, 300);
		} else if (team.equals("red")) {
			if (startPosition.equals("left")) {
				drive.driveDirection(0, 1450, 1);
			} else if (startPosition.equals("right")) {
				drive.driveDirection(0, 500, 1);
			} else {
				drive.driveDirection(0, 1000, 1);
			}
			// move right to get to low goal
			drive.driveDirection(270, 300);
		} else {
		}
	}

	public void turnByWall() {
		// blue boiler left, red boiler right
		if (team.equals("blue")) {
			drive.driveDirection(180, 500);
			drive.turnToAngle(90);
			drive.driveDirection(270, 500, 1);
		} else if (team.equals("red")) {
			drive.driveDirection(180, 500);
			drive.turnToAngle(270);
			drive.driveDirection(90, 500, 1);
		}
	}

	public static void setKillAuto(boolean killAuto) {
		Autonomous.killAuto = killAuto;
	}

	public static void straighten() {
		drive.driveDirection(0, 500);
		drive.turnToAngle(180);
	}
}
