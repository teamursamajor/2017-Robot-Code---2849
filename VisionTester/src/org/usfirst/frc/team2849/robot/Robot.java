//START WITH SKIPPING FRAMES OR DIFF WAY TO DO THE CODE WE TOOK FROM LAST YEARS CODE
package org.usfirst.frc.team2849.robot;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.MatOfPoint;
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
import java.util.concurrent.Semaphore;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	Thread visionThread;
	private List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	private Mat hierarchy = new Mat();
	private double maxArea;
	private double area;
	private int maxContour;
	//private Semaphore sem;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
			visionThread = new Thread(() -> {

			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(640, 480);

			CvSink cvSink = CameraServer.getInstance().getVideo();
			CvSource outputStream = CameraServer.getInstance().putVideo("Brightness Cam", 640, 480);

			Mat source = new Mat();
			Mat output = new Mat();
			Mat temp = new Mat();
			//sem = new Semaphore(1);

			while (!Thread.interrupted()) {
		
<<<<<<< HEAD
				if (cvSink.grabFrame(source) == 0)
				{
					// Send the output the error.
					outputStream.notifyError(cvSink.getError());
					// skip the rest of the current iteration
					continue;
				}
				
				Imgproc.cvtColor(source, temp, Imgproc.COLOR_BGR2HSV);
				Core.extractChannel(temp, temp, 2);
				Imgproc.threshold(temp, temp, 200, 600, Imgproc.THRESH_BINARY);
				Imgproc.Canny(temp, output, 210, 215);
				Imgproc.findContours(output, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
				/*
				  If we want to be able to see the camera feed itself, change the second source in cvtColor to 
				  something else so that source is unchanged, then display source
				*/
				
				//if no contours, end processing
//				if (contours.size() == 0) {
//					System.out.println("No contours found");
//					try {
//						sem.acquire();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					done = true;
//					shouldCheck = false;
//					sem.release();
//				}

				//Look for contours
				maxArea = -1;
				maxContour = -1;
				area = 0;
				for (int i = 0; i < contours.size(); i++)
				{
					area = Imgproc.contourArea(contours.get(i));
					if (area > maxArea) 
					{
						maxArea = area;
						maxContour = i;
					}
				}
				// can remove later if needed vvvvv
				Imgproc.drawContours(output, contours, maxContour, new Scalar(0, 0, 255), 1);
				
				outputStream.putFrame(output);
				
			}

		});
		visionThread.setDaemon(true);
		visionThread.start();
	}

=======
		CameraServer.getInstance().startAutomaticCapture();
    */
    	 new Thread(() -> {
             UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
             System.out.println("Hello World!");
             camera.setResolution(640, 480);
             
             CvSink cvSink = CameraServer.getInstance().getVideo();
             CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);
             
             Mat source = new Mat();
             Mat output = new Mat();
             
             while(true) {
                 cvSink.grabFrame(source);
                 Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
                 outputStream.putFrame(output);
             } 
         }).start();
 
    	}
    
>>>>>>> f73c72ad8e506de9b82c821d53dbef4419010e24
	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
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
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {

	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

}
