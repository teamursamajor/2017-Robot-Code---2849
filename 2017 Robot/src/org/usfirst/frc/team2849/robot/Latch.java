package org.usfirst.frc.team2849.robot;

public class Latch {
	private boolean output = false;
	
	public Latch(){
		
	}
	public boolean buttonPress(boolean valIn){
		boolean press = !output && valIn;
		output = valIn;
		return press;
	}
}
