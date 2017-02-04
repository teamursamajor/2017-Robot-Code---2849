package org.usfirst.frc.team2849.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Spark;

public class Shooter implements Runnable {

	private static Spark leftShooter = new Spark(5);
	private static Spark rightShooter = new Spark(6);

	private static Spark intake = new Spark(7);

	private EndCondition ending = null;
	
	// TODO please clean up your code!!! -Sheldon
	// AnalogInput encoder = new AnalogInput(0);

	// PIDController pid = new PIDController(.1, .1, .1, encoder, upperShooter);

	private static double leftPower = 0.5;
	private static double rightPower = 0.5;

	private static boolean powerSet = true;

	private static AHRS ahrs = new AHRS(SPI.Port.kMXP);

	private static boolean shooting = false;
	
	private static Boolean shooterLock = false;

	/**
	 * Initialize a new shooter. Only use inside the Shooter class to pass into
	 * a new Thread().
	 */
	private Shooter(EndCondition ending) {
		rightShooter.setInverted(true);
		this.ending = ending;
	}

	/**
	 * Run method for Shooter. Will run only ONCE after
	 * Shooter.shoot(EndCondition ending) is called.
	 */
	@Override
	public void run() {
		while (!ending.done()) {
			leftShooter.set(leftPower);
			rightShooter.set(rightPower);
		}
		leftShooter.set(0);
		rightShooter.set(0);
		synchronized (shooterLock) {
			shooterLock = false;
		}
	}

	/**
	 * Start the shooter thread. Automatically runs run() after starting the
	 * thread.
	 */
	public static void startShoot(EndCondition ending) {
		synchronized (shooterLock) {
			if (shooterLock) {
				return;
			}
			shooterLock = true;
		}
		new Thread(new Shooter(ending), "shooter").start();
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
	 * Sets power to one side according to the last call to switchPower().
	 * 
	 * @param power
	 *            Power to set the motor.
	 */
	public static void setPowerSided(double power) {
		if (powerSet) {
			setLeftPower(power);
		} else {
			setRightPower(power);
		}
	}
	
	public static void switchPower(boolean switched) {
		if (switched) powerSet = !powerSet;
	}

	/**
	 * Sets power to both motors.
	 * 
	 * @param power
	 *            Power to set the motors.
	 */
	public static void setPower(double power) {
		setLeftPower(power);
		setRightPower(power);
	}

	/**
	 * Sets the power of the left shooter motor.
	 * 
	 * @param leftPower
	 *            Power of the left motor. Should be in the range 0-1.
	 */
	public static void setLeftPower(double leftPower) {
		System.out.println("Left: " + leftPower);
		Shooter.leftPower = leftPower;
	}

	/**
	 * Sets the power of the right shooter motor.
	 * 
	 * @param rightPower
	 *            Power of the right motor. Should be in range 0-1.
	 */
	public static void setRightPower(double rightPower) {
		System.out.println("Right: " + rightPower);
		Shooter.rightPower = rightPower;
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
		// TODO _WHY_ SO MANY NEWLINES???? -Sheldon
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
