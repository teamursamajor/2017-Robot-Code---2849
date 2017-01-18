package org.usfirst.frc.team2849.robot;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Core;
// why can't I own a Canadian?
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import org.opencv.imgproc.Moments;
		// 1/17- PIXELS TO INCHES
		/*
		 *  idea: we keep perceived as pixels b/c it can't be converted
		 *  but we find distance instead & use that to convert
		 *  inches of known into pixels?
		 */

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Robot extends IterativeRobot 
{
	
//VISION II: ELECTRIC BOOGALOO
//OPENING CREDITS: DEFINING VARIABLES
// **cue Star Wars music**
	
	Thread visionThread;
	private List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	private List<MatOfPoint> maxContours = new ArrayList<MatOfPoint>();
	private Mat hierarchy = new Mat();
	private double maxArea = 0;
	private double almostMaxArea = 0;
	private double area;
	private boolean threadRunning = true;
	private int maxIndex = 0;
	private int almostMaxIndex = 0;
	private double perceived;
	private double known;
	private double angle;
	
	// runs when the robot is disabled
	// public void disabledInit() {
	// threadRunning = false;
	// }

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	
//PART II: SETTING UP THE CAMERA
	
	public void robotInit() 
	{
		threadRunning = true;
		// System.out.println("*****************************************************************1");

		visionThread = new Thread(() -> 
		{
			/*
			 * This code creates a USBCamera for some reason and then starts
			 * the automatic capture. CvSink forwards frames, CvSource obtains
			 * the frames and provides name/resolution.
			 * Then we define a bunch of mats for the frame inputs
			 */
			
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			// camera.setResolution(640, 480);
			// System.out.println("*****************************************************************2");
			CvSink cvSink = CameraServer.getInstance().getVideo();
			// System.out.println("*****************************************************************3");
			CvSource outputStream = CameraServer.getInstance().putVideo("BC", 160, 120);
			// System.out.println("*****************************************************************4");
	
			Mat source = new Mat();
			Mat output = new Mat();
			Mat temp = new Mat();

			// System.out.println("*****************************************************************4.5" + threadRunning);
			while (threadRunning) 
			{
				// System.out.println("*****************************************************************5");
				if (cvSink.grabFrame(source) == 0) 
				{
					// Send the output the error.
					outputStream.notifyError(cvSink.getError());
					// skip the rest of the current iteration
					continue;
				}
				
//PART III: THE FINDING OF THE RECTANGLES
			
				/* Theres a light shining on green reflective tape & we need to find
				 * the location of the tape:
				 * Does stuff to the frames captured. Temp exists so that
				 * the original source can be preserved and outputted after
				 * changes have been made but we didn't use that
				 * Color changes it to HSV, extract channel 
				 * makes it only show one of those channels (H, S, or V)
				 * we don't know which one
				 * Threshold makes it only show stuff at a certain brightness
				 * Canny makes it only show outlines
				 * find contours finds the info for those lines
				 */
				
				Imgproc.cvtColor(source, temp, Imgproc.COLOR_BGR2HSV);
				Core.extractChannel(temp, temp, 2);
				Imgproc.threshold(temp, temp, 200, 600, Imgproc.THRESH_BINARY);
				Imgproc.Canny(temp, output, 210, 215);
				Imgproc.findContours(output, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
				
				/*
				 * Goes through each of the contours and finds the largest
				 * and second largest areas and their index in the matrix
				 * contourArea finds the area
				 * i is the index number of the contour in the matrix
				 * maxContours.add adds the areas of the contours into 
				 * a matrix which gives you the contours you can use for
				 * auto align
				 */
				
				for (int i = 0; i < contours.size(); i++) 
				{

					area = Imgproc.contourArea(contours.get(i));
					
					if (area > maxArea)
					{
						almostMaxArea = maxArea;
						maxArea = area;
						maxIndex = i;
						
					} 
					else if (area > almostMaxArea)
					{
						almostMaxArea = area;
						almostMaxIndex = i;
					}
					
				}
				
				maxContours.add(contours.get(maxIndex));
				maxContours.add(contours.get(almostMaxIndex));
				System.out.println(maxArea);
				System.out.println(almostMaxArea);

				// System.out.println("pls work ");'
				// puts the frame out to the camera, only for testing the code tbh
				outputStream.putFrame(output);
			}
			//I don't know what this does but java isn't mad at us soooooo
			outputStream.free();
			
		});
		
		visionThread.start();
		
//PART IV: AUTO ALIGN
		//it may be off slightly but we should have enough lee-way for it to work
		
		/* Possibility:
		 * find one target's length in pixels, you know that that length = 2 inches, 
		 * so you know that 2/pixel length = pixels per inch
		 * use that to get length of distance b/w centers of both tapes
		 * use charlie's formula thing with the value we get being perceived?
		 */
		
		// the length of one tape to the other tape in the camera in pixels: 
		//needs to be converted to inches or inches converted to pixels for formula
		perceived = 0;
		Imgproc.boundingRect(contours.get(maxIndex));
		//this is the inches from center of one tape to center of other tape
		known = 8.25;
		angle = Math.acos(perceived / known);
			
//PART V: ???
//PART VI: PROFIT
//PART VII: ENDING CREDITS
//VISION III: PLEASE HELP ME coming to theaters near you January 2018 (tentative name) 
//Announcing VISION IV: ROBOT NEVERMORE for an expected January 2019 release (tentative name)
	
	}

	/*
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro You
	 * can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the + *
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	
	public void autonomousInit() {

	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {

	}

	/**
	 * This function is called periodically during operator control public void
	 * teleopPeriodic() { }
	 * 
	 * /** This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

}
