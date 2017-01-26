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

public class Vision 
{

// VISION II: ELECTRIC BOOGALOO
// OPENING CREDITS: DEFINING VARIABLES
// **cue Star Wars music**

	private static Thread visionThread;
	private static List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	private static List<MatOfPoint> maxContours = new ArrayList<MatOfPoint>();
	private static Mat hierarchy = new Mat();
	private static double maxArea = 0;
	private static double almostMaxArea = 0;
	private static double area;
	private static boolean threadRunning = true;
	private static int maxIndex = 0;
	private static int almostMaxIndex = 0; 

// PART II: SETTING UP THE CAMERA

	static 
	{
		
		threadRunning=true;

		visionThread=new Thread(()->

	{

		/*
		 * This code creates a USBCamera for some reason and then starts the
		 * automatic capture. CvSink forwards frames, CvSource obtains the
		 * frames and provides name/resolution. Then we define a bunch of mats
		 * for the frame inputs
		 */

		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		// camera.setResolution(640, 480);
		CvSink cvSink = CameraServer.getInstance().getVideo();
		CvSource outputStream = CameraServer.getInstance().putVideo("BC", 160, 120);

		Mat source = new Mat();
		Mat output = new Mat();
		Mat temp = new Mat();

		while (threadRunning) 
		{

			// clear stuff
			maxArea = 0;
			almostMaxArea = 0;
			maxIndex = 0;
			almostMaxIndex = 0;
			maxContours.clear();
			contours.clear();

			if (cvSink.grabFrame(source) == 0) 
			{

				// Send the output the error.
				outputStream.notifyError(cvSink.getError());
				// skip the rest of the current iteration
				continue;

			}

// PART III: THE FINDING OF THE RECTANGLES

			/*
			 * Theres a light shining on green reflective tape & we need to find
			 * the location of the tape: Does stuff to the frames captured. Temp
			 * exists so that the original source can be preserved and outputted
			 * after changes have been made but we didn't use that Color changes
			 * it to HSV, extract channel makes it only show one of those
			 * channels (H, S, or V) we don't know which one Threshold makes it
			 * only show stuff at a certain brightness Canny makes it only show
			 * outlines find contours finds the info for those lines
			 */

			Imgproc.cvtColor(source, temp, Imgproc.COLOR_BGR2HSV);
			Core.extractChannel(temp, temp, 2);
			Imgproc.threshold(temp, temp, 200, 600, Imgproc.THRESH_BINARY);
			Imgproc.Canny(temp, output, 210, 215);
			Imgproc.findContours(output, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

			/*
			 * Goes through each of the contours and finds the largest and
			 * second largest areas and their index in the matrix contourArea
			 * finds the area i is the index number of the contour in the matrix
			 * maxContours.add adds the areas of the contours into a matrix
			 * which gives you the contours you can use for auto align
			 */

			for (int i = 0; i < contours.size(); i++) 
			{

				area = Imgproc.contourArea(contours.get(i));

				if (area > maxArea) 
				{
					almostMaxArea = maxArea;
					maxArea = area;
					maxIndex = i;
				} else if (area > almostMaxArea) 
				{
					almostMaxArea = area;
					almostMaxIndex = i;
				}

			}

			if (contours.size() == 0) 
			{
				outputStream.putFrame(output);
				continue;
			}

			maxContours.add(contours.get(maxIndex));
			maxContours.add(contours.get(almostMaxIndex));

			for (int i = 0; i < maxContours.size(); i++) 
			{
				Imgproc.drawContours(output, maxContours, i, new Scalar(942.0d));
			}

// PART IV: AUTO ALIGN

			// it may be off slightly but we should have enough lee-way for
			// it to work

			// draw rectangles around the max contours
			Rect rec1 = Imgproc.boundingRect(maxContours.get(0));
			Imgproc.rectangle(output, new Point(rec1.x, rec1.y), new Point(rec1.x + rec1.width, rec1.y + rec1.height), 
					new Scalar(500.0d));
			Rect rec2 = Imgproc.boundingRect(maxContours.get(1));
			Imgproc.rectangle(output, new Point(rec2.x, rec2.y), new Point(rec2.x + rec2.width, rec2.y + rec2.height), 
					new Scalar(500.0d));

			// conversion factor for pixels to inches
			// 2 inches over average of widths (simplify to get 4)
			double conversion = 4.0 / (rec1.width + rec2.width);

			// calculate perceived length between outside edges of tape (in
			// pixels)
			// if rec1 is on the right

			int perceivedPx;

			if (rec1.x > rec2.x) 
			{
				perceivedPx = (rec1.x + rec1.width) - rec2.x;
			}
			// if rec2 is on the right
			else 
			{
				perceivedPx = (rec2.x + rec2.width) - rec1.x;
			}

			// perceived in inches
			double perceivedIn = perceivedPx * conversion;

			// known length (outer edges of tape) in inches
			double knownIn = 10.25;

			// angle robot needs to turn to be parallel to plane of tape
			// i think this is radians?
			double angle = Math.acos(perceivedIn / knownIn);

			// find center of 2 tapes by adding half the distance to the
			// left x coordinate
			double centerOfTapes;

			if (rec1.x > rec2.x) 
			{
				centerOfTapes = rec2.x + (perceivedPx / 2.0);
			} else 
			{
				centerOfTapes = rec1.x + (perceivedPx / 2.0);
			}

			// find center of the frame
			double centerOfFrame = ((double) output.width()) / 2.0;

			double rec1Center = rec1.x / 2;
			double rec2Center = rec2.x / 2;

			if ((rec1Center < centerOfFrame && rec2Center < centerOfFrame)
					|| (rec1Center > centerOfFrame && rec2Center > centerOfFrame)) 
			{
				// make angle value negative or opposite, so 360-angle or 2pi -
				// angle
				// if not run normal code
			}

			// figure out if the center of the tapes of left or right of the
			// center of the frame
			// if the tapes are to the right of center
			if (centerOfTapes > centerOfFrame) 
			{
				// turn counterclockwise & move right

			}
			// if the tapes are to the left of center
			else 
			{
				// turn clockwise & move left

			}

			System.out.println(angle + " is angle");
			// System.out.println(centerOfFrame + " is frame" + centerOfTapes +
			// " is tapes");
			// System.out.println(rec1Center + " is rec1 center");
			// System.out.println(rec2Center + " is rec2 center");
			outputStream.putFrame(output);
		}
		// I don't know what this does but java isn't mad at us soooooo
		outputStream.free();

	});

	visionThread.start();
	}

}
//PART V: ???
//PART VI: PROFIT
//PART VII: ENDING CREDITS
//VISION III: PLEASE HELP ME coming to theaters near you January 2018 (tentative name)
//Announcing VISION IV: ROBOT NEVERMORE for an expected January 2019 release (tentative name)