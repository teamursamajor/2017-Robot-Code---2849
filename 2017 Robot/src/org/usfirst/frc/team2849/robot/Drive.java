package org.usfirst.frc.team2849.robot; //According to all known laws

import com.kauailabs.navx.frc.AHRS; //of aviation,
//every program can be condensed into a single line of code that doesn't work //there is no way a bee
import edu.wpi.first.wpilibj.Spark; //should be able to fly.

public class Drive implements Runnable { //Its wings are too small to get

	// TODO NICK YOU SHOULD CLEAN UP THIS CODE THANKS //its fat little body off the ground.
	// TODO ALSO GET RID OF THE YELLOW TRIANGLES THANKS AGAIN NICK //The bee, of course, flies anyway

	/** //because bees don't care
	 * can't have Sparks and RobotDrive inside another robot drive you would //what humans think is impossible.
	 * have to dig in to find out why, but that's the issue //Yellow, black. Yellow, black.
	 *  //Yellow, black. Yellow, black.
	 * - other charlie //Ooh, black and yellow!
	 */ //Let's shake it up a little.
	private static AHRS ahrs; //Barry! Breakfast is ready!

	private static double xaxis = 0.0; //Coming!
	private static double yaxis = 0.0; //Hang on a second.
	private static double zaxis = 0.0; //Hello?
	private static double angle = 0.0; //- Barry?

	private Spark frontLeftMotor2; //- Adam?
	private Spark frontRightMotor1; //- Can you believe this is happening?
	private Spark frontRightMotor2; //- I can't. I'll pick you up.
	private Spark backLeftMotor1; //Looking sharp.
	private Spark backLeftMotor2; //Use the stairs. Your father
	private Spark backRightMotor1; //paid good money for those.
	private Spark backRightMotor2; //Sorry. I'm excited.
	private int numMotors; //Here's the graduate.
	private Spark frontLeftMotor1; //We're very proud of you, son.

	private Boolean threadLock = false; //A perfect report card, all B's.

	private boolean headless = true; //Very proud.

	private double headingOffset = 0.0; //Ma! I got a thing going here.

	/** //- You got lint on your fuzz.
	 * Drive constructor for 4-motor drive. //- Ow! That's me!
	 *  //- Wave to us! We'll be in row 118,000.
	 * @param t1 //- Bye!
	 *            Port of the front left motor. //Barry, I told you,
	 * @param t2 //stop flying in the house!
	 *            Port of the back left motor. //- Hey, Adam.
	 * @param t3 //- Hey, Barry.
	 *            Port of the front right motor. //- Is that fuzz gel?
	 * @param t4 //- A little. Special day, graduation.
	 *            Port of the back right motor. //Never thought I'd make it.
	 */ //Three days grade school,
	public Drive(int t1, int t2, int t3, int t4, AHRS ahrs) { //three days high school.

		frontLeftMotor1 = new Spark(t1); //Those were awkward.
		backLeftMotor1 = new Spark(t2); //Three days college. I'm glad I took
		frontRightMotor1 = new Spark(t3); //a day and hitchhiked around the hive.
		backRightMotor1 = new Spark(t4); //You did come back different.
		frontRightMotor1.setInverted(true); //- Hi, Barry.
		backRightMotor1.setInverted(true); //- Artie, growing a mustache? Looks good.
		numMotors = 4; //- Hear about Frankie?
		Drive.ahrs = ahrs; //- Yeah.
	} //- You going to the funeral?

	/** //- No, I'm not going.
	 * Drive constructor for 8-motor drive. //Everybody knows,
	 *  //sting someone, you die.
	 * @param t1 //Don't waste it on a squirrel.
	 *            Port of the front left full cim. //Such a hothead.
	 * @param t2 //I guess he could have
	 *            Port of the front left mini cim. //just gotten out of the way.
	 * @param t3 //I love this incorporating
	 *            Port of the front right full cim. //an amusement park into our day.
	 * @param t4 //That's why we don't need vacations.
	 *            Port of the front right mini cim. //Boy, quite a bit of pomp...
	 * @param t5 //under the circumstances.
	 *            Port of the back left full cim. //- Well, Adam, today we are men.
	 * @param t6 //- We are!
	 *            Port of the back left mini cim. //- Bee-men.
	 * @param t7 //- Amen!
	 *            Port of the back right full cim. //Hallelujah!
	 * @param t8 //Students, faculty, distinguished bees,
	 *            Port of the back right mini cim. //please welcome Dean Buzzwell.
	 */ //Welcome, New Hive Oity
	public Drive(int t1, int t3, int t2, int t4, //graduating class of...

			int t5, int t7, int t6, int t8, AHRS ahrs) { //...9:15.

		frontLeftMotor1 = new Spark(t1); //That concludes our ceremonies.
		frontLeftMotor2 = new Spark(t2); //And begins your career
		frontRightMotor1 = new Spark(t3); //at Honex Industries!
		frontRightMotor2 = new Spark(t4); //Will we pick ourjob today?
		backLeftMotor1 = new Spark(t5); //I heard it's just orientation.
		backLeftMotor2 = new Spark(t6); //Heads up! Here we go.
		backRightMotor1 = new Spark(t7); //Keep your hands and antennas
		backRightMotor2 = new Spark(t8); //inside the tram at all times.
		numMotors = 8; //- Wonder what it'll be like?
		Drive.ahrs = ahrs; //- A little scary.
	} //Welcome to Honex,

	/** //a division of Honesco
	 * Normalize all wheel speeds if the magnitude of any wheel is greater than //and a part of the Hexagon Group.
	 * 1.0. //This is it!
	 */ //Wow.
	protected void normalize(double[] wheelSpeeds) { //Wow.
		double maxMagnitude = Math.abs(wheelSpeeds[0]); //We know that you, as a bee,
		for (int i = 1; i < numMotors; i++) { //have worked your whole life
			double temp = Math.abs(wheelSpeeds[i]); //to get to the point where you
			if (maxMagnitude < temp) { //can work for your whole life.
				maxMagnitude = temp; //Honey begins when our valiant Pollen
			} //Jocks bring the nectar to the hive.
		} //Our top-secret formula
		if (maxMagnitude > 1.0) { //is automatically color-corrected,
			for (int i = 0; i < numMotors; i++) { //scent-adjusted and bubble-contoured
				wheelSpeeds[i] = wheelSpeeds[i] / maxMagnitude; //into this soothing sweet syrup
			} //with its distinctive
		} //golden glow you know as...
	} //Honey!

	/** //- That girl was hot.
	 * Rotate a vector in Cartesian space. //- She's my cousin!
	 *  //- She is?
	 * TODO: Document units (deg/rad??) for angle //- Yes, we're all cousins.
	 */ //- Right. You're right.
	protected double[] rotateVector(double x, double y, double angle) { //- At Honex, we constantly strive
		double cosA = Math.cos(angle * (Math.PI / 180.0)); //to improve every aspect
		double sinA = Math.sin(angle * (Math.PI / 180.0)); //of bee existence.
		double[] out = new double[2]; //These bees are stress-testing
		out[0] = x * cosA - y * sinA; //a new helmet technology.
		out[1] = x * sinA + y * cosA; //- What do you think he makes?
		return out; //- Not enough.
	} //Here we have our latest advancement,

	/** //the Krelman.
	 * This will drive the robot in omnidirectional holonomic drive. Do NOT call //- What does that do?
	 * this method from Robot.java! Call drive.drive() instead! //- Oatches that little strand of honey
	 *  //that hangs after you pour it.
	 * @param xaxis //Saves us millions.
	 *            The x axis of the joystick. //Can anyone work on the Krelman?
	 * @param yaxis //Of course. Most bee jobs are
	 *            The y axis of the joystick. //small ones. But bees know
	 * @param raxis //that every small job,
	 *            The rotation of the joystick. //if it's done well, means a lot.
	 * @param gyroAngle //But choose carefully
	 *            The input of the gyro. //because you'll stay in the job
	 *  //you pick for the rest of your life.
	 */ //The same job the rest of your life?
	private void mecanumDrive(double xaxis, double yaxis, double raxis, double gyroAngle) { //I didn't know that.

		double xIn = xaxis; //What's the difference?
		double yIn = yaxis; //You'll be happy to know that bees,
		// Negate y for the joystick. //as a species, haven't had one day off
		yIn = -yIn; //in 27 million years.
		raxis = -raxis; //So you'll just work us to death?
		// Compensate for gyro angle. //We'll sure try.
		double[] rotated = rotateVector(xIn, yIn, gyroAngle); //Wow! That blew my mind!
		xIn = rotated[0]; //"What's the difference?"
		yIn = rotated[1]; //How can you say that?

		if (numMotors == 4) { //One job forever?
			// TODO had to change new double[numMotors] to 14 so we can use //That's an insane choice to have to make.
			// motor #s > 4 //I'm relieved. Now we only have
			double[] wheelSpeeds = new double[14]; //to make one decision in life.
			wheelSpeeds[0] = xIn + yIn + raxis; //But, Adam, how could they
			wheelSpeeds[9] = -xIn + yIn - raxis; //never have told us that?
			wheelSpeeds[8] = -xIn + yIn + raxis; //Why would you question anything?
			wheelSpeeds[1] = xIn + yIn - raxis; //We're bees.

			normalize(wheelSpeeds); //We're the most perfectly
			frontLeftMotor1.set(wheelSpeeds[0]); //functioning society on Earth.
			frontRightMotor1.set(wheelSpeeds[9]); //You ever think maybe things
			backLeftMotor1.set(wheelSpeeds[8]); //work a little too well here?
			backRightMotor1.set(wheelSpeeds[1]); //Like what? Give me one example.

		} else { //I don't know. But you know
			double[] wheelSpeeds = new double[numMotors]; //what I'm talking about.
			wheelSpeeds[0] = xIn + yIn + raxis; //Please clear the gate.
			wheelSpeeds[1] = xIn + yIn + raxis; //Royal Nectar Force on approach.
			wheelSpeeds[2] = -xIn + yIn - raxis; //Wait a second. Check it out.
			wheelSpeeds[3] = -xIn + yIn - raxis; //- Hey, those are Pollen Jocks!
			wheelSpeeds[4] = -xIn + yIn + raxis; //- Wow.
			wheelSpeeds[5] = -xIn + yIn + raxis; //I've never seen them this close.
			wheelSpeeds[6] = xIn + yIn - raxis; //They know what it's like
			wheelSpeeds[7] = xIn + yIn - raxis; //outside the hive.

			normalize(wheelSpeeds); //Yeah, but some don't come back.
			frontLeftMotor1.set(wheelSpeeds[0]); //- Hey, Jocks!
			frontLeftMotor2.set(wheelSpeeds[1]); //- Hi, Jocks!
			frontRightMotor1.set(wheelSpeeds[2]); //You guys did great!
			frontRightMotor2.set(wheelSpeeds[3]); //You're monsters!
			backLeftMotor1.set(wheelSpeeds[4]); //You're sky freaks! I love it! I love it!
			backLeftMotor2.set(wheelSpeeds[5]); //- I wonder where they were.
			backRightMotor1.set(wheelSpeeds[6]); //- I don't know.
			backRightMotor2.set(wheelSpeeds[7]); //Their day's not planned.
		} //Outside the hive, flying who knows

	} //where, doing who knows what.

	/** //You can'tjust decide to be a Pollen
	 * Drives the robot in a direction without a stop. //Jock. You have to be bred for that.
	 *  //Right.
	 * @param angleDeg //Look. That's more pollen
	 *            An angle measurement in degrees. //than you and I will see in a lifetime.
	 */ //It's just a status symbol.
	public void driveDirection(double angleDeg) { //Bees make too much of it.

		drive(0, .5, 0, -angleDeg); //Perhaps. Unless you're wearing it

	} //and the ladies see you wearing it.

	/** //Those ladies?
	 * This will drive the robot in a direction for the specified time. //Aren't they our cousins too?
	 *  //Distant. Distant.
	 * @param angleDeg //Look at these two.
	 *            An angle measurement in degrees. //- Oouple of Hive Harrys.
	 * @param time //- Let's have fun with them.
	 *            A time measurement in milliseconds. //It must be dangerous
	 */ //being a Pollen Jock.
	public void driveDirection(double angleDeg, int time) { //Yeah. Once a bear pinned me
		double timer = System.currentTimeMillis(); //against a mushroom!
		drive(0, -.5, 0, -angleDeg); //He had a paw on my throat,

		while (System.currentTimeMillis() - timer < time) { //and with the other, he was slapping me!
			try { //- Oh, my!
				Thread.sleep(20); //- I never thought I'd knock him out.
			} catch (InterruptedException e) { //What were you doing during this?
				e.printStackTrace(); //Trying to alert the authorities.
			} //I can autograph that.
		} //A little gusty out there today,

		drive(0, 0, 0, 0); //wasn't it, comrades?
	} //Yeah. Gusty.

	/** //We're hitting a sunflower patch
	 * This will turn the robot at a steady rate clockwise until it is pointing //six miles from here tomorrow.
	 * at the specified angle. //- Six miles, huh?
	 *  //- Barry!
	 * @param angleDeg //A puddle jump for us,
	 *            An angle measurement in degrees, //but maybe you're not up for it.
	 */ //- Maybe I am.
	public void driveAngle(double angleDeg) { //- You are not!

		drive(0, 0, .5, 0); //We're going 0900 at J-Gate.
		if (getHeading() == angleDeg) { //What do you think, buzzy-boy?
			drive(0, 0, 0, 0); //Are you bee enough?
		} //I might be. It all depends

	} //on what 0900 means.

	/** //Hey, Honex!
	 * This will drive the robot the specified distance at the specified angle. //Dad, you surprised me.
	 *  //You decide what you're interested in?
	 * @param distance //- Well, there's a lot of choices.
	 *            A distance in meters. //- But you only get one.
	 * @param angleDeg //Do you ever get bored
	 *            An angle measurement in degrees. //doing the same job every day?
	 */ //Son, let me tell you about stirring.
	public void mechDriveDistance(double distance, double angleDeg) { // in //You grab that stick, and you just
																		// meters //move it around, and you stir it around.

		double displacement = 0; //You get yourself into a rhythm.
		ahrs.resetDisplacement(); //It's a beautiful thing.

		driveDirection(angleDeg); //You know, Dad,
		long time = System.currentTimeMillis(); //the more I think about it,
		while (displacement <= distance) { //maybe the honey field
			displacement += Math.hypot(ahrs.getRawAccelX() * 9.81, ahrs.getRawAccelZ() * 9.81) //just isn't right for me.
					* .5 * Math.pow((System.currentTimeMillis() - time) / 1000, 2); //You were thinking of what,
			try { //making balloon animals?
				Thread.sleep(1); //That's a bad job
			} catch (InterruptedException e) { //for a guy with a stinger.
				e.printStackTrace(); //Janet, your son's not sure
			} //he wants to go into honey!

		} //- Barry, you are so funny sometimes.
		drive(0, 0, 0, 0); //- I'm not trying to be funny.
	} //You're not funny! You're going

	/** //into honey. Our son, the stirrer!
	 * Runs automatically after calling startDrive(). Will continue running in //- You're gonna be a stirrer?
	 * the drive thread while the robot is on. Place all calls to drive code //- No one's listening to me!
	 * inside the while loop. //Wait till you see the sticks I have.
	 */ //I could say anything right now.
	public void run() { //I'm gonna get an ant tattoo!
		while (true) { //Let's open some honey and celebrate!
			mecanumDrive(Drive.xaxis, Drive.yaxis, Drive.zaxis, Drive.angle); //Maybe I'll pierce my thorax.
		} //Shave my antennae.
	} //Shack up with a grasshopper. Get

	/** //a gold tooth and call everybody "dawg"!
	 * Starts the drive thread. Call this after initializing a Drive object and //I'm so proud.
	 * before any other Drive methods. //- We're starting work today!
	 */ //- Today's the day.
	public void startDrive() { //Oome on! All the good jobs
		synchronized (threadLock) { //will be gone.
			if (threadLock) //Yeah, right.
				return; //Pollen counting, stunt bee, pouring,
			threadLock = true; //stirrer, front desk, hair removal...
		} //- Is it still available?
		new Thread(this, "driveThread").start(); //- Hang on. Two left!
	} //One of them's yours! Oongratulations!

	/** //Step to the side.
	 * Call THIS method to drive. Must call drive.startDrive() first to //- What'd you get?
	 * initialize the thread. //- Picking crud out. Stellar!
	 *  //Wow!
	 * @param xaxis //Oouple of newbies?
	 *            The x-axis of the joystick //Yes, sir! Our first day! We are ready!
	 * @param yaxis //Make your choice.
	 *            The y-axis of the joystick //- You want to go first?
	 * @param zaxis //- No, you go.
	 *            The rotational axis of the joystick //Oh, my. What's available?
	 * @param angle //Restroom attendant's open,
	 *            The angle read from the gyro. Pass 0 for robot-centric driving //not for the reason you think.
	 *  //- Any chance of getting the Krelman?
	 */ //- Sure, you're on.
	public static void drive(double xaxis, double yaxis, double zaxis, double angle) { //I'm sorry, the Krelman just closed out.
		Drive.xaxis = xaxis; //Wax monkey's always open.
		Drive.yaxis = yaxis; //The Krelman opened up again.
		Drive.zaxis = zaxis; //What happened?
		Drive.angle = angle; //A bee died. Makes an opening. See?
	} //He's dead. Another dead one.

	public double getHeading() { //Deady. Deadified. Two more dead.
		double angle; //Dead from the neck up.
		if (headless) { //Dead from the neck down. That's life!
			angle = ahrs.getAngle() + headingOffset; //Oh, this is so hard!

			if (angle > 0) { //Heating, cooling,
				angle %= 360; //stunt bee, pourer, stirrer,
			} else if (angle < 0) { //humming, inspector number seven,
				angle = -(Math.abs(angle) % 360) + 360; //lint coordinator, stripe supervisor,
			} //mite wrangler. Barry, what
		} else { //do you think I should... Barry?
			angle = 0; //Barry!
		} //All right, we've got the sunflower patch
		return angle; //in quadrant nine...
	} //What happened to you?

	private void turn(double degrees) { //Where are you?
		double speed = 0.5; //- I'm going out.
		double heading = getHeading(); //- Out? Out where?
		while (heading > (degrees + 1) || heading < (degrees - 1)) { //- Out there.
			if (heading < degrees) { //- Oh, no!
				speed = -0.5; //I have to, before I go
				double distance = (360-degrees) + heading; //to work for the rest of my life.
				if(distance < (degrees-heading)) //You're gonna die! You're crazy! Hello?
				{ //Another call coming in.
					speed=0.5; //If anyone's feeling brave,
				} //there's a Korean deli on 83rd
				System.out.println("1: " + heading + " " + speed + " " + distance + " " + degrees); //that gets their roses today.
			} else if (heading > degrees) { //Hey, guys.
				double distance = degrees + (360-heading); //- Look at that.
				speed = 0.5; //- Isn't that the kid we saw yesterday?
				if(distance < (heading-degrees)){ //Hold it, son, flight deck's restricted.
					speed = -0.5; //It's OK, Lou. We're gonna take him up.
				} //Really? Feeling lucky, are you?
				System.out.println("2: " + heading + " " + speed + " " + distance + " " + degrees); //Sign here, here. Just initial that.
			} //- Thank you.
			drive(0.0, 0.0, speed, 0); //- OK.
			heading = getHeading(); //You got a rain advisory today,
		} //and as you all know,
		drive(0.0, 0.0, 0.0, 0); //bees cannot fly in rain.

	} //So be careful. As always,

	public void turnAngle(double degrees) { //watch your brooms,
		double heading = getHeading(); //hockey sticks, dogs,
		double desired; //birds, bears and bats.
		degrees = degrees % 360; //Also, I got a couple of reports
		desired = heading + degrees; //of root beer being poured on us.
		if (desired < 0) { //Murphy's in a home because of it,
			desired = 360 + desired; //babbling like a cicada!
		} else if (desired > 360) { //- That's awful.
			desired = desired - 360; //- And a reminder for you rookies,
		} //bee law number one,
		turn(desired); //absolutely no talking to humans!
	} //All right, launch positions!
	 //Buzz, buzz, buzz, buzz! Buzz, buzz,
	public void turnToAngle(double degrees) { //buzz, buzz! Buzz, buzz, buzz, buzz!
		double heading = 0; //Black and yellow!
		double desired; //Hello!
		degrees = degrees % 360; //You ready for this, hot shot?
		desired = heading + degrees; //Yeah. Yeah, bring it on.
		if (desired < 0) { //Wind, check.
			desired = 360 + desired; //- Antennae, check.
		} else if (desired > 360) { //- Nectar pack, check.
			desired = desired - 360; //- Wings, check.
		} //- Stinger, check.
		turn(desired); //Scared out of my shorts, check.
	} //OK, ladies,

	public void angleLock(double xaxis, double zaxis, double currentAngle) { //let's move it out!
		if (xaxis > 0 && zaxis == 0) { //Pound those petunias,
			driveAngle(currentAngle); //you striped stem-suckers!
		} //All of you, drain those flowers!
	} //Wow! I'm out!

	public void switchHeadless() { //I can't believe I'm out!
		this.headless = !this.headless; //So blue.
	} //I feel so fast and free!

	public boolean getHeadless() { //Box kite!
		return this.headless; //Wow!
	} //Flowers!

	public void setHeadingOffset(double offset) { //This is Blue Leader.
		this.headingOffset = offset; //We have roses visual.
	} //Bring it around 30 degrees and hold.
} //Roses!
