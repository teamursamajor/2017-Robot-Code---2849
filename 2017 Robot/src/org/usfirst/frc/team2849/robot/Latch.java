package org.usfirst.frc.team2849.robot;

public class Latch {
	private boolean lastInput = false;
	
	public Latch(){
		
	}
	
	public boolean buttonPress(boolean button){
		boolean press = !lastInput && button;
		lastInput = button;
		System.out.println("Button: " + button + "Press: " + press);
		return press;
	}
}
