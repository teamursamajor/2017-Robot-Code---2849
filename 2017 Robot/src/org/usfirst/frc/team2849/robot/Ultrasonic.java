package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Solenoid;


public class Ultrasonic extends AnalogInput {
	
	private double m = 5.1945;
	private double b = 5.5419;
	private Solenoid enableSelf;
		
	public Ultrasonic(int port){
		super(port);
		enableSelf = new Solenoid(port);
		enableSelf.set(true);
	}
	// returns in inches
	public double getDistance() {
		// y = mx + b
		return super.getVoltage() * m + b;
	}
}
