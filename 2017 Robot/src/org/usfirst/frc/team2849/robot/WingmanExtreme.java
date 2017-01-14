package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.Joystick;

public class WingmanExtreme extends Joystick {

	public static final int BUTTON_1 = 1;
	public static final int BUTTON_2 = 2;
	public static final int BUTTON_3 = 3;
	public static final int BUTTON_4 = 4;
	public static final int BUTTON_5 = 5;
	public static final int BUTTON_6 = 6;
	public static final int BUTTON_7 = 7;

	public static final int AXIS_TILT_X = 0;
	public static final int AXIS_TILT_Y = 1;
	public static final int AXIS_ROTATE_Z = 2;
	public static final int AXIS_SLIDE = 3;

	public static final int POV_NONE = -1;
	public static final int POV_UP = 0;
	public static final int POV_DIAUPRIGHT = 45;
	public static final int POV_RIGHT = 90;
	public static final int POV_DIADOWNRIGHT = 135;
	public static final int POV_DOWN = 180;
	public static final int POV_DIADOWNLEFT = 225;
	public static final int POV_LEFT = 270;
	public static final int POV_DIAUPLEFT = 315;

	public WingmanExtreme(int port) {
		super(port);
	}

	public boolean getButton(int buttonNumber) {
		return this.getRawButton(buttonNumber);
	}

	public double getAxis(int axisNumber) {
		return this.getRawAxis(axisNumber);
	}

	public double getAxisGreaterThan(int axisNumber, double greaterThan) {
		if(Math.abs(this.getRawAxis(axisNumber)) > greaterThan){
			return this.getRawAxis(axisNumber);
		}else{
			return 0;
		}
	}

	public boolean getAxisLessThan(int axisNumber, double lessThan) {
		return this.getRawAxis(axisNumber) < lessThan;
	}

	public boolean getDPad(int dPadNumber) {
		return this.getPOV(0) == dPadNumber;
	}
}