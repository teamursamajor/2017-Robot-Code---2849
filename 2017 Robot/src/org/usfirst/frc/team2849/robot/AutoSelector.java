package org.usfirst.frc.team2849.robot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
//my code works 10x as well as it used to, meaning it doesn't
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoSelector {

	SendableChooser<String> autoChooser;

	public void initialize(){
		autoChooser = new SendableChooser<String>();
		autoChooser.addDefault("Cross", "0");
		autoChooser.addObject("Shoot", "0");
		autoChooser.addObject("Gear", "0");
		autoChooser.addObject("None", "0");
		SmartDashboard.putData("Auto Mode Chooser", autoChooser);
	}
}
