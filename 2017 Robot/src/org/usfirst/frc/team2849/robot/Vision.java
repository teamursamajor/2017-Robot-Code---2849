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
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.wpilibj.CameraServer;

//TODO test, test, and more testing. Also cut filters (not snapchat)
public class Vision implements Runnable {
	// code is basically clean!
	// UPDATE: not clean anymore lol
	// VISION II: ELECTRIC BOOGALOO
	// **cue Star Wars music**

	private static List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	private static List<MatOfPoint> maxContours = new ArrayList<MatOfPoint>();

	// we don't actually know what this does
	private static Mat hierarchy = new Mat();

	private static double maxArea = 0;
	private static double almostMaxArea = 0;
	private static double area;
	private static int maxIndex = 0;
	private static int almostMaxIndex = 0;

	// coordinates of the center between the two tapes (peg location)
	private static double centerOfTapes;

	// perceived length between outside edges of tape (in pixels)
	private static int perceivedPx;

	// center of the camera
	private static double centerOfFrame;

	// distance we need to move once perpendicular to the peg
	private static double distance;

	// conversion factor from pixels to inches
	private static double conversion;

	// Arrays of Arrays used to hold the frames we grab from the camera
	private static Mat source = new Mat();
	private static Mat output = new Mat();
	private static Mat temp = new Mat();

	// String for the peg side the robot is going to auto align to
	private static String pegSide = "middle";

	// Declares CvSink and CvSource so that they can be passed values in Vision
	// constructor
//	private static CvSink cvSink;
//	private static CvSource outputStream;
	private static CvSink cvSink1;
	private static CvSource outputStream1;
//	private static CvSink cvSink2;
//	private static CvSource outputStream2;

	private static Thread visionRun = null;
	private static boolean runAutoAlign = false;
	private static boolean isSwitched = false;
//	private static VideoSink server;
	private static VideoSink server1;
//	private static VideoSink server2;

//	private static UsbCamera camera0;
//	private static UsbCamera camera1;
	private static UsbCamera camera2;
	private static UsbCamera camera0;
	private static UsbCamera camera1;

	private static Drive drive;

//	private static Mat image = new Mat();
	//may not need this if switching cameras works
//	private static Mat image2 = new Mat();

	private static int cameraNumber = 1;

	public Vision(Drive drive) {
//		/*
//		 * Creates 3 cameras for use. Because of bandwidth issues, only 1 is
//		 * always active (gear cam) and we switch beween the other two (one for
//		 * shooter one for main/climber)
//		 */

		/*
		 * Creates 3 cameras for use. Because of bandwidth issues, only 1 is
		 * always active (gear cam) and we switch beween the other two (one for
		 * shooter one for main/climber)
		 */
		pegSide = "middle";
		//gear camera
		camera0 = new UsbCamera("USB Camera 0", 0);
		//front camera
		camera1 = new UsbCamera("USB Camera 1", 1);
		//shooter camera
		camera2 = new UsbCamera("USB Camera 2", 1);

		camera0.setResolution(160, 120);
		camera1.setResolution(160, 120);
		camera2.setResolution(160, 120);

		CameraServer.getInstance().addCamera(camera0);
		CameraServer.getInstance().addCamera(camera1);
		CameraServer.getInstance().addCamera(camera2);
		// use one set of cvSink/outputStream and redefine in methods as necessary
		cvSink1 = CameraServer.getInstance().getVideo(camera1);
		outputStream1 = new CvSource("Camera 1", VideoMode.PixelFormat.kMJPEG, 160, 120, 30);
		//i dont think we need this, camera worked without it
		CameraServer.getInstance().addCamera(outputStream1);
		server1 = CameraServer.getInstance().addServer("serve_USB Camera 1");
		server1.setSource(outputStream1);
	}

	public static void visionInit(Drive drive) {
		visionRun = new Thread(new Vision(drive), "visionThread");
		visionRun.start();
	}

	public void run() {
		while (true) {
			
//			System.out.println("running auto align");
			cvSink1.grabFrame(source);
//			cvSink.grabFrame(source);
			if (runAutoAlign) {
				System.out.println("Running Auto Align");
				System.out.println(getDistance(cvSink1, outputStream1));
//				 autoAlign();
			} else{
				Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
			}
			
			try {
				if(cameraNumber == 1){
					outputStream1.putFrame(source);
//					System.out.println("camera 1 put");
				} else if(cameraNumber == 0){
					outputStream1.putFrame(output);
					System.out.println("gear cam put");
				} else if(cameraNumber == 2){
					outputStream1.putFrame(source);
					System.out.println("shooter cam put");
				} else{
					outputStream1.putFrame(source);
					System.out.println("default to front cam");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
//			System.out.println("after put frames");
		}
	}

	/**
	 * AutoAligns the robot to the peg between the two reflective tapes by using
	 * the distance from getDistance to move left or right a certain amount and
	 * moving
	 */
	public void autoAlign() {

		/*
		 * This uses the IMU sensor Navx MXP to find the angle the robot is
		 * facing relative to a default angle (0 degrees) set at robotInit and
		 * set it to a specific angle depending on which side we are on
		 */

		// GEAR IS ON THE BACK OF THE ROBOT
		switch (pegSide) {
		case "left":
			// turns the robot to angle 240 when the user presses the button set
			// to left
			drive.driveAngle(240.0);
			break;
		case "right":
			// turns the robot to angle 120 when the user presses the button set
			// to right
			drive.driveAngle(120.0);
			break;
		case "middle":
			// turns the robot to angle 180 when the user presses the button set
			// to middle
			drive.driveAngle(180.0);
		default:

			break;
		}
		System.out.println(pegSide);

		// returns distance that the robot needs to move
		//distance = getDistance(cvSink1, outputStream1);

		if (distance > 0) {
			// if the tapes are to the right of the center, then move right
			drive.mechDriveDistance(distance, 90);
		} else {
			// if the tapes are to the left of center, then move left
			drive.mechDriveDistance(distance, 270);
		}
		/*
		 * checks to see if the horizontal distance we need to move is greater
		 * than 3.25 inches (.08255 meters) Ends after 3 attempts and stops auto
		 * align
		 */

		if (Math.abs(distance) < 0.0825) {
			// move forward
			drive.mechDriveDistance(1, 180);
		}
		for (int i = 3; Math.abs(distance) > 0.08255 && i > 0; i--) {
			//distance = getDistance(cvSink1, outputStream1);

			if (distance > 0) {
				// if the tapes are to the right of the center, then move right
				drive.mechDriveDistance(distance, 90);
			} else {
				// if the tapes are to the left of center, then move left
				drive.mechDriveDistance(distance, 270);
			}
			if (i == 0) {
				System.out.println("ERROR: AUTO ALIGN FAILED :( ");
			}

		}

		System.out.println(distance);

	} // end autoAlign

	/**
	 * Uses contours to find centerOfTapes (peg location) and
	 * centerOfFrame(robot location), then calculates and returns distance
	 * between them in meters. Might return Double.NaN
	 * 
	 * @param cvSink
	 * @param outputStream
	 * @return double distance
	 */
	public static double getDistance(CvSink cvSink1, CvSource outputStream1) {
		// clear stuff
		maxArea = 0;
		almostMaxArea = 0;
		maxIndex = 0;
		almostMaxIndex = 0;
		maxContours.clear();
		contours.clear();
		
		// TODO if any errors come up this grabFrame is a possible suspect
		// we're keeping an eye on you cvSink.grabFrame(source);....
		cvSink1.grabFrame(source);
		/*
		 * We need to find rectangles by shining a green light from our camera
		 * onto the reflective tape next to the peg, so we grab an image and
		 * process it
		 */
		// changes image from BGR to HSV (hue, saturation value)
		Imgproc.cvtColor(source, temp, Imgproc.COLOR_BGR2HSV);
		// extracts one of those values (H, S, or V & idk which is which)
		Core.extractChannel(temp, temp, 2);
		// only shows images of a certain brightness
		Imgproc.threshold(temp, temp, 200, 600, Imgproc.THRESH_BINARY);
		// only shows the outlines of each object seen in the image (rectangles
		// from tape and/or lights)
		Imgproc.Canny(temp, output, 210, 215);
		// finds the information for those lines which we can use for autoalign
		Imgproc.findContours(output, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		// Finds the contours w/ largest and second largest areas and their
		// index in the matrix
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
		// if there are no contours, return nothing
		if (contours.size() == 0) {
			// outputStream.putFrame(output);
			return Double.NaN;
		}

		maxContours.add(contours.get(maxIndex));
		maxContours.add(contours.get(almostMaxIndex));

		// draws the contours so we can see them for testing purposes only
		// for (int i = 0; i < maxContours.size(); i++) {
		// Imgproc.drawContours(output, maxContours, i, new Scalar(942.0d));
		// }

		// draw rectangles around the max contours
		Rect rec1 = Imgproc.boundingRect(maxContours.get(0));
		Imgproc.rectangle(output, new Point(rec1.x, rec1.y), new Point(rec1.x + rec1.width, rec1.y + rec1.height),
				new Scalar(500.0d));
		Rect rec2 = Imgproc.boundingRect(maxContours.get(1));
		Imgproc.rectangle(output, new Point(rec2.x, rec2.y), new Point(rec2.x + rec2.width, rec2.y + rec2.height),
				new Scalar(500.0d));

		// conversion factor for pixels to inches 2 inches over average of
		// widths (simplify to get 4)
		conversion = 4.0 / (rec1.width + rec2.width);

		// if rec1 is on the right
		if (rec1.x > rec2.x) {
			perceivedPx = (rec1.x + rec1.width) - rec2.x;
		}
		// if rec2 is on the right
		else {
			perceivedPx = (rec2.x + rec2.width) - rec1.x;
		}

		// find center of 2 tapes by adding half the distance to the left x
		// coordinate
		if (rec1.x > rec2.x) {
			centerOfTapes = rec2.x + (perceivedPx / 2.0);
		} else {
			centerOfTapes = rec1.x + (perceivedPx / 2.0);
		}

		// find center of the frame
		centerOfFrame = output.width() / 2.0;

		// finds distance you need to move by subtracting frame from center and
		// converts to meters
		return ((centerOfTapes - centerOfFrame) * conversion) * 0.0254;
	}// end getDistance

	public static void setPegSide(String pegSide) {
		Vision.pegSide = pegSide;
	}// end setPegSide

	public static void setRunAutoAlign(boolean runAutoAlign) {
		Vision.runAutoAlign = runAutoAlign;
		switchCamera(0);
	}// end setRunAutoAlign

	//TODO delete if still unused
	public static void switchCamera(int cameraNum) {
		
		switch(cameraNum){
		case 1:
			//front camera
			cvSink1 = CameraServer.getInstance().getVideo(camera1);
			System.out.println("camera 1");
			cameraNumber = 1;
			isSwitched = false;
			break;
		case 2:
			//shooter camera
			cvSink1 = CameraServer.getInstance().getVideo(camera2);
			System.out.println("camera 2");
			cameraNumber = 2;
			isSwitched = true;
			break;
		case 0:
			//gear camera
			cvSink1 = CameraServer.getInstance().getVideo(camera0);
			System.out.println("gear cam");
			cameraNumber = 0;
			break;
		default:
			cvSink1 = CameraServer.getInstance().getVideo(camera1);
			System.out.println("camera 1");
			cameraNumber = 1;
		}
//		cvSink1 = CameraServer.getInstance().getVideo(camera2);
//		System.out.println("camera 2");
//		cameraNumber = 2;
//		isSwitched = true;
	}
//
//	public static void switchBack() {
//		cvSink1 = CameraServer.getInstance().getVideo(camera1);
//		System.out.println("camera 1");
//		cameraNumber = 1;
//		isSwitched = false;
//	}

	public static boolean getIsSwitched() {
		return isSwitched;
	}
}
// VISION III: PLEASE HELP ME coming to theaters near you January 2018
// (tentative name)
// Announcing VISION IV: ROBOT NEVERMORE for an expected January 2019 release
// (tentative name)