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
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.wpilibj.CameraServer;

//TODO test, test, and more testing
public class Vision implements Runnable {
	// VISION II: ELECTRIC BOOGALOO
	// **cue Star Wars music**

	private static List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	private static List<MatOfPoint> maxContours = new ArrayList<MatOfPoint>();

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

	// Mats used to hold the frames we grab from the camera
	private static Mat source = new Mat();
	private static Mat output = new Mat();
	private static Mat temp = new Mat();

	// String for the peg side the robot is going to auto align to
	private static String pegSide = "right";

	private static CvSink cvSink;
	private static CvSource outputStream;

	private static Thread visionRun = null;

	// if true, run auto align
	private static boolean runAutoAlign = false;
	// if true the camera is on shooter cam, if false gear cam
	private static boolean isSwitched = false;

	private static VideoSink server;

	// Gear cam
	private static UsbCamera camera0;
	// shooter cam
	private static UsbCamera camera1;

	private static Drive drive;

	// starts with gear cam
	private static int cameraNumber = 0;

	public Vision(Drive drive) {
		// default peg side to middle
		pegSide = "right";

		Vision.drive = drive;

		// gear camera
		camera0 = new UsbCamera("USB Camera 0", 0);
		// shooter camera
		camera1 = new UsbCamera("USB Camera 1", 1);

		CameraServer.getInstance().addCamera(camera0);
		CameraServer.getInstance().addCamera(camera1);

		camera0.setResolution(160, 120);
		camera1.setResolution(160, 120);

		cvSink = CameraServer.getInstance().getVideo(camera0);
		outputStream = CameraServer.getInstance().putVideo("Camera 1", 160, 120);
	}

	public static void visionInit(Drive drive) {
		visionRun = new Thread(new Vision(drive), "visionThread");
		visionRun.start();
	}

	public void run() {
		while (true) {
			cvSink.grabFrame(source);
			if (runAutoAlign) {
				System.out.println("Running Auto Align");
				 System.out.println(getDistance(cvSink, outputStream));
//				autoAlign();
				runAutoAlign = false;
			} else {
				Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
			}

			//TODO test and see if code works without this
			//we have a putVideo line 98, do we need putFrame?
			try {
				if (cameraNumber == 0) {
					outputStream.putFrame(output);
				} else {
					outputStream.putFrame(source);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		//TODO fix these numbers, whichw ay is the robot starting?
		switch (pegSide) {
		case "left":
			// turns the robot to angle 240 when the left button is pressed
			drive.turnToAngle(150.0);
			break;
		case "right":
			// turns the robot to angle 120 when the right button is pressed
			drive.turnToAngle(210.0);
			break;
		case "middle":
			// turns the robot to angle 180 when the middle button is pressed
			drive.turnToAngle(0.0);
		default:
			break;
		}
		System.out.println(pegSide);

		// TODO Why is this commented out? Do we need it?
		// returns distance that the robot needs to move
		distance = getDistance(cvSink, outputStream);

		if (distance > 0) {
			// if the tapes are to the right of the center, then move right
			// drive.mechDriveDistance(distance, 270);
			drive.driveDirection(90, 2000);
		} else {
			// if the tapes are to the left of center, then move left
			// drive.mechDriveDistance(distance, 90);
			drive.driveDirection(270, 2000);
		}

		/*
		 * checks to see if the horizontal distance we need to move is greater
		 * than 3.25 inches (.08255 meters) Ends after 3 attempts and stops auto
		 * align
		 */
		if (Math.abs(distance) < 0.0825) {
			// move forward
			drive.mechDriveDistance(1, 0);
//			drive.driveDirection(180, 1000);
		} else {
			for (int i = 3; Math.abs(distance) > 0.08255 && i > 0; i--) {
				distance = getDistance(cvSink, outputStream);
				if (distance > 0) {
					// if the tapes are to the right of the center, then move
					// right
					// drive.mechDriveDistance(distance, 270);
					drive.driveDirection(270, 2000);
				} else {
					// if the tapes are to the left of center, then move left
					// drive.mechDriveDistance(distance, 90);
					drive.driveDirection(90, 2000);
				}
				if (i == 0) {
					System.out.println("ERROR: AUTO ALIGN FAILED :( ");
				}
			}

		}

		// TODO is this only for testing
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
	public static double getDistance(CvSink cvSink, CvSource outputStream) {
		// clear stuff
		maxArea = 0;
		almostMaxArea = 0;
		maxIndex = 0;
		almostMaxIndex = 0;
		maxContours.clear();
		contours.clear();

		cvSink.grabFrame(source);

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
			return Double.NaN;
		}

		maxContours.add(contours.get(maxIndex));
		maxContours.add(contours.get(almostMaxIndex));

		// draw rectangles around the max contours
		Rect rec1 = Imgproc.boundingRect(maxContours.get(0));
		Imgproc.rectangle(output, new Point(rec1.x, rec1.y), new Point(rec1.x + rec1.width, rec1.y + rec1.height),
				new Scalar(500.0d));
		Rect rec2 = Imgproc.boundingRect(maxContours.get(1));
		Imgproc.rectangle(output, new Point(rec2.x, rec2.y), new Point(rec2.x + rec2.width, rec2.y + rec2.height),
				new Scalar(500.0d));

		// pixels to inches conversion factor 2 inches over average of widths
		conversion = 4.0 / (rec1.width + rec2.width);

		// if rec1 is on the right
		if (rec1.x > rec2.x) {
			perceivedPx = (rec1.x + rec1.width) - rec2.x;
		}
		// if rec2 is on the right
		else {
			perceivedPx = (rec2.x + rec2.width) - rec1.x;
		}

		// find center of tapes
		if (rec1.x > rec2.x) {
			centerOfTapes = rec2.x + (perceivedPx / 2.0);
		} else {
			centerOfTapes = rec1.x + (perceivedPx / 2.0);
		}

		// find center of the frame
		centerOfFrame = output.width() / 2.0;

		// find distance to move
		return ((centerOfTapes - centerOfFrame) * conversion) * 0.0254;
	}// end getDistance

	public static void setPegSide(String pegSide) {
		Vision.pegSide = pegSide;
	}

	public static void setRunAutoAlign(boolean runAutoAlign) {
		Vision.runAutoAlign = runAutoAlign;
		// switchCamera(0);
	}

	public static void switchCamera(int cameraNum) {

		switch (cameraNum) {
		case 1:
			// shooter camera
			cvSink = CameraServer.getInstance().getVideo(camera1);
			System.out.println("camera 1");
			cameraNumber = 1;
			isSwitched = true;
			break;
		case 0:
			// gear camera
			cvSink = CameraServer.getInstance().getVideo(camera0);
			System.out.println("gear cam");
			cameraNumber = 0;
			isSwitched = false;
			break;
		default:
			cvSink = CameraServer.getInstance().getVideo(camera1);
			System.out.println("camera 1");
			cameraNumber = 1;
		}
	}

	public static boolean getIsSwitched() {
		return isSwitched;
	}
}
// VISION III: PLEASE HELP ME coming to theaters near you January 2018
// (tentative name)
// Announcing VISION IV: ROBOT NEVERMORE for an expected January 2019 release
// (tentative name)