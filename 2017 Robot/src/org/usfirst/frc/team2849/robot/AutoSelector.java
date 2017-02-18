package org.usfirst.frc.team2849.robot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
//my code works 10x as well as it used to, meaning it doesn't
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoSelector {

	public SendableChooser<String> autoChooser0;
	public SendableChooser<String> autoChooser1;

	public void initialize(){
		autoChooser0 = new SendableChooser<String>();
		autoChooser0.addDefault("Cross", "0");
		autoChooser0.addObject("Shoot", "0");
		autoChooser0.addObject("Gear", "0");
		autoChooser0.addObject("None", "0");
		SmartDashboard.putData("Auto Mode Chooser", autoChooser0);
		autoChooser1 = new SendableChooser<String>();
		autoChooser1.addDefault("Left", "0");
		autoChooser1.addObject("Center", "0");
		autoChooser1.addObject("Right", "0");
		SmartDashboard.putData("Side Chooser", autoChooser1);
	}
}
