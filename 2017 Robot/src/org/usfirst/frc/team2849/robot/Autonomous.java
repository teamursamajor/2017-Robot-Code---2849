package org.usfirst.frc.team2849.robot;

public class Autonomous {
	
	private AutoMode mode;
	
	public void autoInit(AutoMode mode) {
		this.mode = mode;
	}
	
	public void runAuto() {
		switch (mode) {
		case CROSS:
			break;
		case SHOOT:
			break;
		case GEAR:
			break;
		case NONE:
		default:
			// do nothing
			break;
		}
	}
	
}

