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
	} //end run 

	public void cross(AutoMode previousMode) {
		if (isKilled())
			return;
		if (previousMode == AutoMode.GEAR) {
			if (position == StartPosition.LEFT) {
				/*
				 * in gear position, straighten out by backing up 1000, 
				 * turning to face the field, and drive forward 100
				 */
				//TODO ****NOTE THAT THIS ONLY WORKS IF TURNTOANGLE(180) WORKS LIKE I THINK IT DOES****
				gearToStraight();
//				drive.driveDirection(0, 1000);
				//TODO should the turnAngle be turnToAngle?
//				drive.turnAngle(-210);
//				drive.driveDirection(0, 100);
			} else if (position == StartPosition.RIGHT) {
				/*
				 * in gear position, straighten out by backing up 1000, 
				 * turning to face the field, and drive forward 100
				 */
				//TODO ****NOTE THAT THIS ONLY WORKS IF TURNTOANGLE(180) WORKS LIKE I THINK IT DOES****
				gearToStraight();
//				drive.driveDirection(0, 1000);
//				drive.turnAngle(225.2);
//				drive.driveDirection(0, 100);
			} else {
				// TODO these angles and times arent tested
				// TODO when is this actually used? aren't we always running other auto code? 
				if (team.equals("blue")) {
					/*
					 * i think this backs up from the center peg 
					 * and turns and runs into the boiler and turns
					 * and runs forward for a while
					 */
					drive.driveDirection(0, 1900);
					drive.turnAngle(-100);
					drive.driveDirection(180, 1700);
					drive.turnAngle(90);
					drive.driveDirection(0, 2000);
				} else {
					//see above
					drive.driveDirection(0, 1900);
					drive.turnAngle(100);
					drive.driveDirection(180, 1700);
					drive.turnAngle(-90);
					drive.driveDirection(0, 2000);
				}
			}
			/*
			 * TODO i think this is for cross on the first time,
			 * and and only left or right. It just drives forward
			 * for a while. Does this need to be turned around
			 * as well? I think we should make it turn around
			 * or do something
			 */
			
		} else if (previousMode == AutoMode.CROSS) {
			if (position != StartPosition.CENTER) {
				//moves forward straight from the wall on the left or right side, no gear
				drive.driveDirection(180, 1800);
			}
		}
		
		//TODO what was this used for? want to know in case anything useful comes up (low priority question)
		// headless is default true
		// drive.switchHeadless();
		// drive.driveDirection(-180, 500);
	} //end cross

	//not used
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
					 * drive forward 1750, turn 45 degrees clockwise
					 * and drive forward again so we are close to the peg
					 * and on the right angle. Then we wait so the robot 
					 * doesn't roll and run auto align twice, then wait
					 * so gear can be pulled.
					 */
//					drive.driveDirection(180, 1750);
//					drive.turnToAngle(45);
//					drive.driveDirection(180, 750);
//					try {
//						Thread.sleep(100);
//					} catch (Exception e) {
//					}
//					Vision.setRunAutoAlign(true);
//					Vision.setRunAutoAlign(true);
//					try {
//						Thread.sleep(GEAR_LIFT_TIME);
//					} catch (Exception e) {
//					}
					wallToGear("left");

					// TODO these numbers were all changed
				} else if (position == StartPosition.RIGHT) {
					
					/*
					 * drive forward 1750, turn 42.5 degrees counterclockwise
					 * and drive forward again so we are close to the peg
					 * and on the right angle. Then we wait so the robot 
					 * doesn't roll and run auto align twice, then wait
					 * so gear can be pulled.
					 */
					
//					// was 1850, made 1800
//					// TODO just made it 1750
//					drive.driveDirection(180, 1750);
//					// was -40
//					drive.turnToAngle(-42.5);
//					// was 300, changed to 900
//					drive.driveDirection(180, 750);
//					try {
//						// was 1000 is now 100
//						Thread.sleep(100);
//					} catch (Exception e) {
//					}
//					Vision.setRunAutoAlign(true);
//					Vision.setRunAutoAlign(true);
//					try {
//						Thread.sleep(GEAR_LIFT_TIME);
//					} catch (Exception e) {
//					}

					wallToGear("right");
					
				} else if (position == StartPosition.CENTER) {
//					/*
//					 * UPDATE: new plan: drive forward a ton,
//					 * wait two seconds, move back three inches
//					 */
//					// robot isnt driving straight. it was 190 and the robot was
//					// too
//					// far right, i wasnt sure if i should go up or down
//					
//					//back of the robot moves forward for 3.5 seconds
//					drive.driveDirection(180, 3500);
//					// drive.driveDirection(190, 700);
//					// drive.driveDirection(200, 700);
//					try {
//						//waits two seconds so pilot can get gear or wait for readjustment
//						Thread.sleep(2000);
//					} catch (Exception e) {
//					}
//					// Vision.setRunAutoAlign(true);
//					// Vision.setRunAutoAlign(true);
//					// drive.driveDirection(180, 300);
//					
//					//back of robot moves backwards for 50 ms, should be about 3 in
//					drive.driveDirection(0, 50);
//				}
//				try {
//					Thread.sleep(GEAR_LIFT_TIME);
//				} catch (InterruptedException e) {
//				}
					centerToPeg();
				// if we arent running gear twice, straighten
				// TODO this isnt tested
				//TODO make this a function so its easier to understand, may not be necessary
				if (mode.size() > 1) {
					if (mode.get(1) != AutoMode.GEAR && position != StartPosition.CENTER) {
					//if we aren't in center, drive back and straighten
						drive.driveDirection(0, 1000);
						//TODO can we make this turnToAngle?
						if (position == StartPosition.RIGHT) {
							drive.turnAngle(222.5);
						} else {
							// TODO is this the right angle?
							drive.turnAngle(-210);
						}
					}
				}
			} else if (i == 1) {
				// TODO all of this is completely untested
				/*
				 * this is supposed to back the robot up and redo autoalign if
				 * we call gear twice for autonomous
				 */
				
				/*
				 * TODO is any of this necessary? we aren't using auto
				 * align for it so we just move forward and back.
				 * Seems like a waste of time
				 */
				drive.driveDirection(0, 300);
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				// Vision.setRunAutoAlign(true);
				// Vision.setRunAutoAlign(true);
				drive.driveDirection(180, 300);
				
				try{
					Thread.sleep(100);
				} catch (Exception e) {
				}
				drive.driveDirection(0, 1000);

				if (position == StartPosition.RIGHT) {
					drive.turnAngle(222.5);
				} else if (position == StartPosition.LEFT) {
					// TODO is this the right angle?
					drive.turnAngle(-210);
				}
			}

		} else if (previousMode == AutoMode.SHOOT) {
			//TODO we never shoot, can we just delete this?
			//In fact,is it harder for us to just remove shoot entirely from the code?
			drive.driveDirection(180, 2000);
		} else if (previousMode == AutoMode.CROSS) {
		//TODO why is this empty?
		}
	} //end of previousmode = gear
} //end of gear

	private Autonomous(EndCondition ending, List<AutoMode> mode, StartPosition position, String team, Drive drive) {
		//TODO im assuming this is just assigning the variables the values from the auto selector?
		Autonomous.ending = ending;
		Autonomous.mode = mode;
		Autonomous.position = position;
		Autonomous.drive = drive;
		Autonomous.team = team;
	}

	public static void auto(EndCondition ending, List<AutoMode> mode, StartPosition position, String team,
			Drive drive) {
		// TODO ??????? how does this work?(low priority question)
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
		//TODO do we ever use this?
		return ending.done();
	}
	
	public static void gearToStraight(){
		drive.driveDirection(0, 1000);
		//TODO should the turnAngle be turnToAngle?
		//this value may not be correct, but 0 would be when facing DS, so 180 should be field (?)
		drive.turnToAngle(180);
		//i think this should be 180 and not 0 bc 0 would move you closer to DS, not field (?)
		drive.driveDirection(180, 100);
	}
	
	public static void wallToGear(String side){
		//TODO explain this to Jess
		//TODO INCOMPLETE
		
		//initial drive
		drive.driveDirection(180, 1750);
		//turn, angle depends on side: right or left
		if(side.equals("right")){
			drive.turnToAngle(45);
		} else if(side.equals("left")){
			drive.turnToAngle(-42.5);
		}
		//second drive towards peg
		drive.driveDirection(180, 750);
		
		//wait for the robot to stop rolling
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		
		//auto align twice, moves forward slightly after each auto align
		Vision.setRunAutoAlign(true);
		Vision.setRunAutoAlign(true);
		
		//waits for 5 seconds for the pilot to pick up the gear
		try {
			Thread.sleep(GEAR_LIFT_TIME);
		} catch (Exception e) {
		}
		
	}
	
	public static void centerToPeg(){
		
		//initial drive forward from wall for 3.5 seconds
		drive.driveDirection(180, 3500);
		try {
			//waits two seconds so pilot can get gear or wait for readjustment
			Thread.sleep(2000);
		} catch (Exception e) {
		}
		
		//back of robot moves backwards for 50 ms, should be about 3 in
		/*
		 * this is in case the gear is put in too slanted and needs to get
		 * lifted up a bit by having the robot move back and letting the peg 
		 * pull the gear forward
		 */
		drive.driveDirection(0, 50);
		
		//waits 5 seconds for driver to get peg
		try {
			Thread.sleep(GEAR_LIFT_TIME);
		} catch (InterruptedException e) {
		}
	}
}