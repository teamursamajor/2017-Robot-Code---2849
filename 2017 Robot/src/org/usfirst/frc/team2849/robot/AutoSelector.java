package org.usfirst.frc.team2849.robot;
import org.usfirst.frc.team2849.robot.Autonomous.AutoMode;
import org.usfirst.frc.team2849.robot.Autonomous.StartPosition;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
//my code works 10x as well as it used to, meaning it doesn't
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoSelector {

	public SendableChooser<String> autoChooser0;
	public SendableChooser<String> autoChooser1;
	public SendableChooser<String> cameraChooser;

	public void initialize(){
		autoChooser0 = new SendableChooser<String>();
		autoChooser0.addDefault("Cross", "0");
		autoChooser0.addObject("Shoot", "1");
		autoChooser0.addObject("Gear", "2");
		autoChooser0.addObject("None", "3");
		SmartDashboard.putData("Auto Mode Chooser", autoChooser0);
		
		autoChooser1 = new SendableChooser<String>();
		autoChooser1.addDefault("Left", "0");
		autoChooser1.addObject("Center", "1");
		autoChooser1.addObject("Right", "2");
		SmartDashboard.putData("Side Chooser", autoChooser1);
		
		cameraChooser = new SendableChooser<String>();
		cameraChooser.addDefault("gearCam = 0, shooterCam = 1", "0");
		cameraChooser.addObject("gearCam = 1, shooterCam = 0", "1");
		SmartDashboard.putData("Camera Chooser", cameraChooser);
	}
	
	public AutoMode getAutoMode(){
		String auto = autoChooser0.getSelected();
		if(auto.equals("0")){
			return AutoMode.CROSS;
		} else if(auto.equals("1")){
			return AutoMode.SHOOT;
		} else if(auto.equals("2")){
			return AutoMode.GEAR;
		} else {
			return AutoMode.NONE;
		}
	}
	
	public StartPosition getStartPosition(){
		String auto = autoChooser1.getSelected();
		if(auto.equals("0")){
			return StartPosition.LEFT;
		} else if(auto.equals("1")){
			return StartPosition.CENTER;
		} else {
			return StartPosition.RIGHT;
		}
	}
	
	public int getCameras(){
		String auto = cameraChooser.getSelected();
		if(auto.equals("0")){
			return 0;
		} else {
			return 1;
		}
	}
}
