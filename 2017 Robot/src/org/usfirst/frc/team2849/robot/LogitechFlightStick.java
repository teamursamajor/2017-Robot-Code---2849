package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.Joystick;

public class LogitechFlightStick extends Joystick {

	public static final int BUTTON_Trigger = 1;
	public static final int BUTTON_Side = 2;
	public static final int BUTTON_BL3 = 3;
	public static final int BUTTON_BR4 = 4;
	public static final int BUTTON_TL5 = 5;
	public static final int BUTTON_TR6 = 6;
	public static final int BUTTON_Side7 = 7;
	public static final int BUTTON_Side8 = 8;
	public static final int BUTTON_Side9 = 9;
	public static final int BUTTON_Side10 = 10;
	public static final int BUTTON_Side11 = 11;
	public static final int BUTTON_Side12 = 12;

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
	
	private boolean button1Pressed = false;
	private boolean button2Pressed = false;
	private boolean button3Pressed = false;
	private boolean button4Pressed = false;
	private boolean button5Pressed = false;
	private boolean button6Pressed = false;
	private boolean button7Pressed = false;
	private boolean button8Pressed = false;
	private boolean button9Pressed = false;
	private boolean button10Pressed = false;
	private boolean button11Pressed = false;
	private boolean button12Pressed = false;
	
	private final double MAX_XY = 1;
	private final double MAX_Z = .5;

	public LogitechFlightStick (int port) {
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
	public double getXAxis() {
		return Math.pow(this.getAxisGreaterThan(AXIS_TILT_X, .1), 2) * Math.signum(this.getAxis(AXIS_TILT_X)) * MAX_XY;
	}
	public double getYAxis() {
		return Math.pow(this.getAxisGreaterThan(AXIS_TILT_Y, .1), 2) * Math.signum(this.getAxis(AXIS_TILT_Y)) * MAX_XY;
	}
	public double getZAxis() {
		return Math.pow(this.getAxisGreaterThan(AXIS_ROTATE_Z, .1), 2) * Math.signum(this.getAxis(AXIS_ROTATE_Z))*MAX_Z;
	}
	public boolean getAxisLessThan(int axisNumber, double lessThan) {
		return this.getRawAxis(axisNumber) < lessThan;
	}

	public boolean getDPad(int dPadNumber) {
		return this.getPOV(0) == dPadNumber;
	}
	public boolean getSingleButtonPress(int buttNum){
		switch(buttNum){
		case 1:
			if(!button1Pressed && this.getButton(buttNum)){
				button1Pressed = true;
				return true;
			} else if(button1Pressed && !this.getButton(buttNum)){
				button1Pressed = false;
				return false;
			} else {
				return false;
			}
		case 2:
			if(!button2Pressed && this.getButton(buttNum)){
				button2Pressed = true;
				return true;
			} else if(button2Pressed && !this.getButton(buttNum)){
				button2Pressed = false;
				return false;
			} else {
				return false;
			}
		case 3:
			if(!button3Pressed && this.getButton(buttNum)){
				button3Pressed = true;
				return true;
			} else if(button3Pressed && !this.getButton(buttNum)){
				button3Pressed = false;
				return false;
			} else {
				return false;
			}
		case 4:
			if(!button4Pressed && this.getButton(buttNum)){
				button4Pressed = true;
				return true;
			} else if(button4Pressed && !this.getButton(buttNum)){
				button4Pressed = false;
				return false;
			} else {
				return false;
			}
		case 5:
			if(!button5Pressed && this.getButton(buttNum)){
				button5Pressed = true;
				return true;
			} else if(button5Pressed && !this.getButton(buttNum)){
				button5Pressed = false;
				return false;
			} else {
				return false;
			}
		case 6:
			if(!button6Pressed && this.getButton(buttNum)){
				button6Pressed = true;
				return true;
			} else if(button6Pressed && !this.getButton(buttNum)){
				button6Pressed = false;
				return false;
			} else {
				return false;
			}
		case 7:
			if(!button7Pressed && this.getButton(buttNum)){
				button7Pressed = true;
				return true;
			} else if(button7Pressed && !this.getButton(buttNum)){
				button7Pressed = false;
				return false;
			} else {
				return false;
			}
		case 8:
			if(!button8Pressed && this.getButton(buttNum)){
				button8Pressed = true;
				return true;
			} else if(button8Pressed && !this.getButton(buttNum)){
				button8Pressed = false;
				return false;
			} else {
				return false;
			}
		case 9:
			if(!button9Pressed && this.getButton(buttNum)){
				button9Pressed = true;
				return true;
			} else if(button9Pressed && !this.getButton(buttNum)){
				button9Pressed = false;
				return false;
			} else {
				return false;
			}
		case 10:
			if(!button10Pressed && this.getButton(buttNum)){
				button10Pressed = true;
				return true;
			} else if(button10Pressed && !this.getButton(buttNum)){
				button10Pressed = false;
				return false;
			} else {
				return false;
			}
		case 11:
			if(!button11Pressed && this.getButton(buttNum)){
				button11Pressed = true;
				return true;
			} else if(button11Pressed && !this.getButton(buttNum)){
				button11Pressed = false;
				return false;
			} else {
				return false;
			}
		case 12:
			if(!button12Pressed && this.getButton(buttNum)){
				button12Pressed = true;
				return true;
			} else if(button12Pressed && !this.getButton(buttNum)){
				button12Pressed = false;
				return false;
			} else {
				return false;
			}
		}
			return false;
		
		
	}
}