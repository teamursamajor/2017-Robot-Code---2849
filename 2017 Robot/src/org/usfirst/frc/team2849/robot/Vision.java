package org.usfirst.frc.team2849.robot;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.wpilibj.CameraServer;

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
	private static Mat distanceTemp = new Mat();

	// String for the peg side the robot is going to auto align to
	private static String pegSide = "right";

	private static CvSink cvSink;
	private static CvSource outputStream;

	private static Thread visionRun = null;

	// if true, run auto align
	private static boolean runAutoAlign = false;
	// if true the camera is on shooter cam, if false gear cam
	private static boolean isSwitched = false;

	// TODO do we need server still? if not, delete it
	@SuppressWarnings("unused")
	private static VideoSink server;

	// Gear cam
	private static UsbCamera camera0;
	// shooter cam
	private static UsbCamera camera1;

	private static Drive drive;

	// starts with gear cam
	private static int cameraNumber = 0;
	// TODO if bandwidth issues persist, comment out shooterCam & fix errors
	private static int shooterCam = 1;
	private static int gearCam = 0;
	private static PrintWriter file;

	private final double DIST_MARGIN_ERROR = 0.1;

	public Vision(Drive drive) {

		try {
			file = new PrintWriter("/home/lvuser/vision.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// default peg side to middle
		// TODO why is this right instead of middle? testing?
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
		cvSink.grabFrame(source);
		Imgproc.cvtColor(source, distanceTemp, Imgproc.COLOR_BGR2GRAY);
	}

	public static void visionInit(Drive drive) {
		visionRun = new Thread(new Vision(drive), "visionThread");
		visionRun.start();
	}

	public void run() {

		while (true) {
			// displays shooter cam
			switchCamera(1);
			cvSink.grabFrame(source);
			// getDistance(cvSink, outputStream);
			if (runAutoAlign) {
				// displays gear cam
				/*
				 * not sure if we need to switch for auto align to work, so I
				 * put it in just in case
				 */
				switchCamera(0);
				System.out.println("Running Auto Align");
				// System.out.println(getDistance(cvSink, outputStream));
				autoAlign();
				runAutoAlign = false;
				// displays shooter cam
				switchCamera(1);
			}

			// TODO test and see if code works without this
			// we have a putVideo line 98, do we need putFrame?
			try {
				if (cameraNumber == gearCam) {
					outputStream.putFrame(source);
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
		Drive.setAutoDrive(true);
		/*
		 * This uses the IMU sensor Navx MXP to find the angle the robot is
		 * facing relative to a default angle (0 degrees) set at robotInit and
		 * set it to a specific angle depending on which side we are on
		 */

		distance = getDistance(cvSink, outputStream);

		/*
		 * checks to see if the horizontal distance we need to move is less than
		 * DIST_MARGIN_ERROR, if so, moves forward, if not moves left or right
		 * based on the sign of distance
		 */
		if (Math.abs(distance) < DIST_MARGIN_ERROR) {
			// move forward
			drive.driveDirection(180, 750);
		} else {
			if (distance > 0) {
				// if the tapes are to the right of the center, then move right
				if (pegSide.equals("right") || pegSide.equals("left")) {
					drive.driveDirection(270, 400);
				} else {
					// we don't want to drive as far if we're in the center
					drive.driveDirection(270, 200);
				}
			} else {
				// if the tapes are to the left of center, then move left
				if (pegSide.equals("right") || pegSide.equals("left")) {
					drive.driveDirection(90, 400);
				} else {
					// we don't want to drive as far if we're in the center
					drive.driveDirection(90, 200);
				}
			}

			// recalculate the distance to see if it's better
			distance = getDistance(cvSink, outputStream);

			/*
			 * if new distance is less that DIST_MARGIN_ERROR, move forward if
			 * not, recalculate distance and move again, each time decreasing
			 * the length of time the robot drives by 50 miliseconds if, at the
			 * end of any of these iterations the distance is less than
			 * DIST_MARGIN_ERROR, it ends the loop and moves forward, if not it
			 * moves forward after 3 iterations
			 * 
			 */
			if (Math.abs(distance) < DIST_MARGIN_ERROR) {
				drive.driveDirection(180, 750);
			} else {
				int i;
				int time = 200;
				for (i = 3; i > 0; i--) {
					distance = getDistance(cvSink, outputStream);
					if (Math.abs(distance) < DIST_MARGIN_ERROR) {
						break;
					} else if (distance > 0) {
						// if the tapes are right of the center, then move right
						drive.driveDirection(270, time);
					} else {
						// if the tapes are left of center, then move left
						drive.driveDirection(90, time);
					}
					time -= 50;
				}
				if (i == 0) {
					System.out.println("ERROR: AUTO ALIGN FAILED :( ");
				}
				// should move robot halfway to peg, autoAlign is called twice
				drive.driveDirection(180, 100);
			}
		}

		// TODO is this only for testing
		System.out.printf("Auto align distance: %f", distance);
		Drive.setAutoDrive(false);
	} // end autoAlign

	/**
	 * Uses contours to find centerOfTapes (peg location) and
	 * centerOfFrame(robot location), then calculates and returns distance
	 * between them in meters. If the distance is negative, the tapes are to the
	 * left of center and you need to move left. If positive, move right. Might
	 * return Double.NaN
	 * 
	 * @param cvSink
	 * @param outputStream
	 * @return double distance
	 */
	public static double getDistance(CvSink cvSink, CvSource outputStream) {
		String str = "";

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
		Imgproc.Canny(temp, output, 200, 255);
		// finds the information for those lines which we can use for autoalign
		Imgproc.findContours(output, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		// if there are no contours, return nothing
		if (contours.size() == 0) {
			str += " size is 0 ";
			return Double.NaN;
		}

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

		str += "\nmax area: " + maxArea + " almost max area: " + almostMaxArea;

		maxContours.add(contours.get(maxIndex));
		maxContours.add(contours.get(almostMaxIndex));

		// draw rectangles around the max contours
		Rect rec1 = Imgproc.boundingRect(maxContours.get(0));
		Imgproc.rectangle(output, new Point(rec1.x, rec1.y), new Point(rec1.x + rec1.width, rec1.y + rec1.height),
				new Scalar(500.0d));
		Rect rec2 = Imgproc.boundingRect(maxContours.get(1));
		Imgproc.rectangle(output, new Point(rec2.x, rec2.y), new Point(rec2.x + rec2.width, rec2.y + rec2.height),
				new Scalar(500.0d));

		str += "\nrec1 x: " + rec1.x + " rec1 y: " + rec1.y + "rec2 x: " + rec2.x + " rec2 y: " + rec2.y;

		output.copyTo(distanceTemp);

		// pixels to inches conversion factor -> 2 inches over average of widths
		conversion = 4.0 / (rec1.width + rec2.width);

		// calculate percieved distance between outer edges of tapes in pixels
		// if rec1 is on the right
		if (rec1.x > rec2.x) {
			perceivedPx = (rec1.x + rec1.width) - rec2.x;
		}
		// if rec2 is on the right
		else {
			perceivedPx = (rec2.x + rec2.width) - rec1.x;
		}

		// adds 1/2 the percieved distance to the left edge of the left tape
		// find center of tapes
		if (rec1.x > rec2.x) {
			centerOfTapes = rec2.x + (perceivedPx / 2.0);
		} else {
			centerOfTapes = rec1.x + (perceivedPx / 2.0);
		}

		// divides the width of the frame by 2 to find center of the frame
		centerOfFrame = output.width() / 2.0;

		str += "\ncenter of tapes: " + centerOfTapes + " center of frame: " + centerOfFrame + "\n\n";

		try {
			file.println(str);
			file.flush();
			Imgcodecs.imwrite("/home/lvuser/vision.jpg", distanceTemp);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * subtractes centerOfTapes from centerOfFrame to get distance to move
		 * in pixels multiply that by conversion factor to get it in inches
		 * multiply that by 0.0254 to get it in meters
		 */
		return ((centerOfTapes - centerOfFrame) * conversion) * 0.0254;
	}// end getDistance

	public static void setPegSide(String pegSide) {
		Vision.pegSide = pegSide;
	}

	public static void setRunAutoAlign(boolean runAutoAlign) {
		Vision.runAutoAlign = runAutoAlign;
		// switchCamera(0);
	}

	// changes which camera is showing
	public static void switchCamera(int cameraNum) {

		switch (cameraNum) {
		case 1:
			// displays the shooter camera
			if (shooterCam == 0) {
				cvSink = CameraServer.getInstance().getVideo(camera0);
			} else {
				cvSink = CameraServer.getInstance().getVideo(camera1);
			}
			// System.out.println("shooter cam");
			cameraNumber = shooterCam;
			isSwitched = true;
			break;
		case 0:
			// displays the gear camera
			if (gearCam == 0) {
				cvSink = CameraServer.getInstance().getVideo(camera0);
			} else {
				cvSink = CameraServer.getInstance().getVideo(camera1);
			}
			// System.out.println("gear cam");
			cameraNumber = gearCam;
			isSwitched = false;
			break;
		default:
			cvSink = CameraServer.getInstance().getVideo(camera0);
			// System.out.println("gear cam");
			cameraNumber = gearCam;
			isSwitched = false;
		}
	}

	// flips the cameras
	public static void setCameras(int shooterCam, int gearCam) {
		Vision.shooterCam = shooterCam;
		Vision.gearCam = gearCam;
		switchCamera(0);
	}

	public static boolean getIsSwitched() {
		return isSwitched;
	}

	public static void closeFile() {
		try {
			file.close();
		} catch (Exception e) {

		}

	}
}
// VISION III: PLEASE HELP ME coming to theaters near you January 2018
// (tentative name)
// Announcing VISION IV: ROBOT NEVERMORE for an expected January 2019 release
// (tentative name)
