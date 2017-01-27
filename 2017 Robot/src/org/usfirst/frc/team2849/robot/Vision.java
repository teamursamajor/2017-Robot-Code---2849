package org.usfirst.frc.team2849.robot;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
//why can't I own a Canadian?
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.SPI;

import com.kauailabs.navx.frc.AHRS;

public class Vision {

// VISION II: ELECTRIC BOOGALOO
// **cue Star Wars music**

	private static Thread visionThread;
	private static List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	private static List<MatOfPoint> maxContours = new ArrayList<MatOfPoint>();
	
	//we don't actually know what this does
	private static Mat hierarchy = new Mat();
	
	private static double maxArea = 0;
	private static double almostMaxArea = 0;
	private static double area;
	private static boolean threadRunning = true;
	private static int maxIndex = 0;
	private static int almostMaxIndex = 0;
	
	//AHRS stands for something (according to Charlie) but we don't know what
	//Its for the IMU sensor NavX MXP
	private static AHRS ahrs = new AHRS(SPI.Port.kMXP);
	
	//the angle the robot needs to turn to be perpendicular to the peg
	private static double angle;
	
	//coordinates of the center between the two tapes (peg location)
	private static double centerOfTapes;
	
	//perceived length between outside edges of tape (in pixels)
	private static int perceivedPx;

	//center of the camera
	private static double centerOfFrame;
	
	//distance we need to move once perpendicular to the peg
	private static double distance;
	
	//conversion factor from pixels to inches
	private static double conversion;
	
	//Arrays of Arrays used to hold the frames we grab from the camera
	private static Mat source = new Mat();
	private static Mat output = new Mat();
	private static Mat temp = new Mat();
	
	static {

		threadRunning = true;

		visionThread = new Thread(() ->

		{

			/*
			 * This code creates a USBCamera (for some reason) and then starts the
			 * automatic capture. CvSink forwards frames, CvSource obtains the
			 * frames and provides name/resolution.
			 */

			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			CvSink cvSink = CameraServer.getInstance().getVideo();
			//test different resolutions
			CvSource outputStream = CameraServer.getInstance().putVideo("BC", 160, 120);

			/*
			 * This uses the IMU sensor Navx MXP to find the angle the robot is facing
			 * relative to a default angle (0 degrees) set at robotInit. We take that 
			 * angle and do % 360 because getAngle() is accumulative, not just heading.
			 * ahrs.getCompassHeading requires calibration of the sensor for every test 
			 * location, so this is easier.
			 */
			
			//get the angle and turn to be perpendicular with the peg
			angle = ahrs.getAngle() % 360;
			angle *= -1;
//figure out how to make the robot turn clockwise
			
			//doesn't need to be a while loop in competition, only for testing
			/*
			 * when not while loop:
			 * 1. getDistance
			 * 2. move that distance 
			 * 3. check getDistance again to make sure were aligned w/in a margin of error 
			 * 4. move forward 
			 */
			while (threadRunning) {
				
				//returns distance that the robot needs to move 
				distance = getDistance(cvSink, outputStream);
				
				//converts distance to meters (thats the unit the drive code is in)
				distance *= 0.0254;
				
				if(distance > 0) {
					//if the tapes are to the right of the center, then move right
					Drive.mechDriveDistance(distance, 90);
				}
				else {
					// if the tapes are to the left of center, then move left
					Drive.mechDriveDistance(distance, 270);
				}

				//only for testing purposes; delete for competition
				outputStream.putFrame(output);
			} // end while loop
			
			outputStream.free();
			//be free outputStream!!!

		}); //end of Thread

		visionThread.start();
	}

	/**
	 * Uses contours to find centerOfTapes and centerOfFrame, 
	 * then calculates and returns distance between them in inches. Might return Double.NaN
	 * 
	 * @param cvSink
	 * @param outputStream
	 * @return double distance
	 */
	public static double getDistance(CvSink cvSink, CvSource outputStream){
		
		// clear stuff
		maxArea = 0;
		almostMaxArea = 0;
		maxIndex = 0;
		almostMaxIndex = 0;
		maxContours.clear();
		contours.clear();


		if (cvSink.grabFrame(source) == 0) {

			// Send the output the error.
			outputStream.notifyError(cvSink.getError());
			// skip the rest of the current iteration			
			return Double.NaN;
		}
		
			/*
			 * Theres a light shining on green reflective tape & we need to
			 * find the location of the tape: Does stuff to the frames
			 * captured. Temp exists so that the original source can be
			 * preserved and outputted after changes have been made but we
			 * didn't use that Color changes it to HSV, extract channel
			 * makes it only show one of those channels (H, S, or V) we
			 * don't know which one Threshold makes it only show stuff at a
			 * certain brightness Canny makes it only show outlines find
			 * contours finds the info for those lines
			 */

			Imgproc.cvtColor(source, temp, Imgproc.COLOR_BGR2HSV);
			Core.extractChannel(temp, temp, 2);
			Imgproc.threshold(temp, temp, 200, 600, Imgproc.THRESH_BINARY);
			Imgproc.Canny(temp, output, 210, 215);
			Imgproc.findContours(output, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

			/*
			 * Goes through each of the contours and finds the largest and
			 * second largest areas and their index in the matrix
			 * contourArea finds the area i is the index number of the
			 * contour in the matrix maxContours.add adds the areas of the
			 * contours into a matrix which gives you the contours you can
			 * use for auto align
			 */

			for (int i = 0; i < contours.size(); i++) {

				area = Imgproc.contourArea(contours.get(i));

				if (area > maxArea) {
					almostMaxArea = maxArea;
					maxArea = area;
					maxIndex = i;
				} else if (area > almostMaxArea) {
					almostMaxArea = area;
					almostMaxIndex = i;
				}

			}

			if (contours.size() == 0) {
				outputStream.putFrame(output);
				return Double.NaN;
			}

			maxContours.add(contours.get(maxIndex));
			maxContours.add(contours.get(almostMaxIndex));

			for (int i = 0; i < maxContours.size(); i++) {
				Imgproc.drawContours(output, maxContours, i, new Scalar(942.0d));
			}
			
			// draw rectangles around the max contours
			Rect rec1 = Imgproc.boundingRect(maxContours.get(0));
			Imgproc.rectangle(output, new Point(rec1.x, rec1.y),
					new Point(rec1.x + rec1.width, rec1.y + rec1.height), new Scalar(500.0d));
			Rect rec2 = Imgproc.boundingRect(maxContours.get(1));
			Imgproc.rectangle(output, new Point(rec2.x, rec2.y),
					new Point(rec2.x + rec2.width, rec2.y + rec2.height), new Scalar(500.0d));
			
			// conversion factor for pixels to inches
			// 2 inches over average of widths (simplify to get 4)
			 conversion = 4.0 / (rec1.width + rec2.width);
			
			// if rec1 is on the right
			if (rec1.x > rec2.x) {
				perceivedPx = (rec1.x + rec1.width) - rec2.x;
			}
			// if rec2 is on the right
			else {
				perceivedPx = (rec2.x + rec2.width) - rec1.x;
			}


			// find center of 2 tapes by adding half the distance to the
			// left x coordinate
			if (rec1.x > rec2.x) {
				centerOfTapes = rec2.x + (perceivedPx / 2.0);
			} else {
				centerOfTapes = rec1.x + (perceivedPx / 2.0);
			}

			// find center of the frame
			centerOfFrame = output.width() / 2.0;

			//finds distance you need to move by subtracting frame from center
			return (centerOfTapes - centerOfFrame) * conversion;
		}
	}
// VISION III: PLEASE HELP ME coming to theaters near you January 2018 (tentative name)
// Announcing VISION IV: ROBOT NEVERMORE for an expected January 2019 release (tentative name)