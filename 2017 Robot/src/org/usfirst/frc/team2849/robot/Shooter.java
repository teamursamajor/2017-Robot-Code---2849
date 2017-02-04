package org.usfirst.frc.team2849.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Spark;

public class Shooter implements Runnable {

	private static Spark upperShooter = new Spark(5);
	private static Spark lowerShooter = new Spark(6);

	private static Spark intake = new Spark(8);

	// AnalogInput encoder = new AnalogInput(0);

	// PIDController pid = new PIDController(.1, .1, .1, encoder, upperShooter);

	private static double power = 1.0;

	private static AHRS ahrs = new AHRS(SPI.Port.kMXP);

	private static boolean shooting = false;

	/**
	 * Initialize a new shooter. Only use inside the Shooter class to pass into
	 * a new Thread().
	 */
	private Shooter() {
	}

	/**
	 * Run method for Shooter. Will run only ONCE after
	 * Shooter.shoot(EndCondition ending) is called.
	 */
	@Override
	public void run() {
		while (true) {
			if (shooting) {
				upperShooter.set(power);
				lowerShooter.set(-power);
			} else {
				upperShooter.set(0);
				lowerShooter.set(0);
			}
		}
	}

	/**
	 * Start the shooter thread. Automatically runs run() after starting the
	 * thread.
	 */
	public static void startShoot() {
		new Thread(new Shooter(), "shooter").start();
	}

	/**
	 * Call to shoot.
	 * 
	 * @param shooting
	 *            true = powers shooter. false = no shooter.
	 */
	public static void shoot(boolean shooting) {
		Shooter.shooting = shooting;
	}

	/**
	 * Sets the power of the shooter.
	 * 
	 * @param power
	 *            Power of the shooter. Should be in the range 0-1.
	 */
	public static void setPower(double power) {
		// System.out.println(power);
		Shooter.power = power;
	}

	/**
	 * TODO: Write better documentation.
	 * 
	 * @param xaxis
	 *            Reading from the x-axis of the joystick.
	 * @param yaxis
	 *            Reading from the y-axis of the joystick.
	 */
	public static void ballIntake(double xaxis, double yaxis) {
		if (Math.abs(joystickAngle(xaxis, yaxis) - (ahrs.getAngle() % 360)) <= 30) {

			intake.set(1.0);

		} else {

			intake.set(0.0);

		}

	}

	/**
	 * TODO: Write better documentation.
	 * 
	 * @param joyX
	 * @param joyY
	 * @return
	 */
	public static double joystickAngle(double joyX, double joyY) {
		// gets joystick's x/y values and does arc tan, then converts to degrees
		// from radians
		return Math.atan((-joyY / joyX) * (180 / Math.PI));
	}

	/**
	 * Clears the intake in case of a ball problem.
	 */
	public static void clearIntake() {
		// TODO: Integrate this into the shooter thread. This won't work as-is.
		intake.set(-1.0);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		intake.set(0.0);
	}
}