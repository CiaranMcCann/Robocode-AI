package itc;

import java.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import robocode.*;


/**
 * Solomon - a robot by IT Carlow students Ciaran McCann and Carl Lange.
 */
public class solomon extends Robot
{
	private byte status;
	public byte getStatus()
	{
		return status;
	}
	public void setStatus(byte status) 
	{
		this.status = status;
	}

	private int currentTacticIndex;
	private CTactic tacticLibrary[][];
	
	private double healthBeforeTactic;
	
	private final int E_AGGRESSIVE = 85;
	private final int AGGRESSIVE= 65;
	private final int DEFENSIVE = 45;
	private final int E_DEFENSIVE = 25;
	
	long maxduration = 900; //0.9 seconds
	long endtime = 0;



	public solomon()
	{
		
		status = 0;
		currentTacticIndex = 0;
		
		populateLibrary();
		
		healthBeforeTactic = 0;
	}
	
	/**
	 * 
	 */
	private void populateLibrary() {
		tacticLibrary = new CTactic[4][4];
		
		for (int j = 0; j < tacticLibrary.length; j++) {
			for (int i = 0; i < tacticLibrary[j].length; i++) {
					tacticLibrary[i][j] = new CTactic_ea1(); 			
			}
		}
		// Uncomment these before committing!		
		//tacticLibrary[0][0] = new CTactic_ea0();
		//tacticLibrary[1][0] = new CTactic_a0();
		//tacticLibrary[1][2] = new CTactic_a2();
		//tacticLibrary[2][0] = new CTactic_d0();
		//tacticLibrary[3][0] = new CTactic_ed0();
	}
		
	/**
	 * run: Solomon's default behavior
	 */

	public void run() 
	{
		this.setColors(Color.black, Color.gray, Color.white);
		
		while(true) {
				
			status = this.assessHealth();
			currentTacticIndex = AI.pickTactic(status, currentTacticIndex, tacticLibrary);
			healthBeforeTactic =  this.getEnergy();
			endtime = System.currentTimeMillis() + maxduration;
			
			while(System.currentTimeMillis() < endtime)
			{
				System.out.println("\n\n [status][currentTactics] = [" + this.status +"]["+this.currentTacticIndex+"]\n\n");
				tacticLibrary[status][currentTacticIndex].run_(this);
			}		
			
			for(int i =0; i < tacticLibrary[status][currentTacticIndex].gaugingList.size(); i++)
			{
				System.out.println(tacticLibrary[status][currentTacticIndex].gaugingList.get(i));
			}
			
			endtime = System.currentTimeMillis() + maxduration;
			tacticLibrary[status][currentTacticIndex].gaugingList.add(AI.gaugeTactic(healthBeforeTactic, this.getEnergy()));		
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		tacticLibrary[status][currentTacticIndex].onScannedRobot_(this, e);
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		tacticLibrary[status][currentTacticIndex].onHitByBullet_(this, e);
	}
	
	/**
	 * onHitRobot: What to do when you hit another robot.
	 */
	public void onHitRobot(HitRobotEvent e) {
		tacticLibrary[status][currentTacticIndex].onHitRobot_(this, e);
	}
	
	
	/**
	 * Assess the health of Solomon and returns a status number
	 * which will be used as an index in accessing the 2D array of tactics.
	 * @return
	 */
	private byte assessHealth()
	{
		double health =  this.getEnergy();
		byte status = 0;
		
		if(health >= E_AGGRESSIVE)
		{
			if(status == 0)
			{
				status = 0;
			}
			else
			{
				currentTacticIndex = 0;
				status = 0;
			}
		}
		else if((health >= AGGRESSIVE)&&(health < E_AGGRESSIVE))
		{
			if(status == 1)
			{
				status = 1;
			}
			else
			{
				currentTacticIndex = 0;
				status = 1;
			}
		}
		else if((health >= DEFENSIVE)&&(health < AGGRESSIVE))
		{
			if(status == 2)
			{
				status = 2;
			}
			else
			{
				currentTacticIndex = 0;
				status = 2;
			}
		}
		else if(health <= E_DEFENSIVE)
		{
			if(status == 3)
			{
				status = 3;
			}
			else
			{
				currentTacticIndex = 0;
				status = 3;
			}
		}
		
		return status;
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	END OF SOLOMON.JAVA
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// START OF AI.JAVA
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


/**
 * This class is Solomon's decision making component.
 * Here it assesses the past experiences and judges whether
 * the tactic was successful in the past.
 * @author C00134748
 *
 */
class AI {

	/**
	 * This is the percentage amount of negative change which
	 * is required or greater to get a negative result on the efficiency
	 */
	public static byte gaugingThreshold = 3;
	
	public static byte getGaugingThreshold() {
		return gaugingThreshold;
	}
	public static void setGaugingThreshold(byte gaugingThreshold) {
				
		AI.gaugingThreshold = gaugingThreshold;
	}
	
	
	/**
	 * Picks a tactic for the robot based on the current status it's in
	 * i.e defensive, aggressive etc. It then calls another method isGoodTactic()
	 * which will find if the tactic as been successful in the past
	 * @param status
	 * @param currentTacticIndex
	 * @param tacticLibrary
	 * @return Tactic to be used
	 */
	public static int pickTactic(int status, int currentTacticIndex, CTactic tacticLibrary[][])
	{		
		int currentTactic = currentTacticIndex;		
		
		for(int i = 0; i <  tacticLibrary[status].length; i++)
		{
			if(tacticLibrary[status][currentTactic].isGoodTactic(status))
			{
				currentTactic = i;
				i = tacticLibrary[status].length;
			}
			else
			{
				if(currentTactic < (tacticLibrary[status].length-1))
				{
				currentTactic++;
				}				
			}
		}						
		return currentTactic;
	}
	
	
	/**
	 * Returns a one or a zero based on the health change in the time before
	 * a tactic was used and after to determine if it's a successful tactic
	 * @param healthBefore
	 * @param currentHealth
	 * @return
	 */
	public static byte gaugeTactic(double healthBefore, double currentHealth)
	{
		byte tacticGauge = 1;
		double changeInHealth = 0;
		
		if(healthBefore > currentHealth)
		{	
			changeInHealth = (((healthBefore/currentHealth)*100.0F)-100);
			if(changeInHealth > gaugingThreshold)
			{
				tacticGauge = 0;
			}		
		}	
		return tacticGauge;
	}
}



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//END OF AI.JAVA
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//START OF CTactic.JAVA
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


 class CTactic {
	
	//This is the success threshold that a tactic needs to be labelled as effective 
	protected final double GAUGING_THRESHOLD = 0.7; 
	//protected List gaugingList = new ArrayList();
	protected List<Byte> gaugingList  = new ArrayList<Byte>();

	protected Random r = new Random();
	
	public void run_(solomon s)
	{

	}
	

	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
		
	}
	
	
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
		
	}
	
	public void onHitRobot_(solomon s, HitRobotEvent e)
	{
		
	}
	
	/**
	 * Uses the distance of the enemy robot to figure out how
	 * much energy to expend when firing. The bias varies, depending on the
	 * robot's status. If it's very aggressive, it'll be more likely
	 * to fire with full strength. If very defensive, it'll almost never
	 * do that, and so on, so forth.
	 * 
	 * @param s
	 * @param enemyDist
	 */
	protected void fire(solomon s, double enemyDist) {
		// TODO: This can be simplified (probably very easily so it doesn't require a case statement);
	
		// Case statement to pick bias.
		int bias = 0;
		switch (s.getStatus()) {
			case 0 :
				bias = 400;
				break;
			case 1 :
				bias = 300;
				break;
			case 2 :
				bias = 200;
				break;
			case 3 :
				bias = 100;
				break;
			default :
				break;
		}
		
		double firePower = 0;
		
		// This if statement is so that if the enemy is more than 500 units away, it won't even bother with the bias
		// or if it's closer than 100 units, it'll go straight to full power.
		if (enemyDist > 500)
		{
			firePower = 0.1;
		}
		else if (enemyDist < 100)
		{

			firePower = 5.0;
			
			s.fire(firePower);
			s.fire(firePower);
			s.fire(firePower);
		}
		else
		{
			firePower = bias/enemyDist;
		}
		
		
		s.fire(firePower);
	}
	
	
	/**
	 * Calculates the efficiency of the tactic using the gaugingList
	 * and returns true or false
	 * @return
	 */
	public boolean isGoodTactic(int status)
	{
		//TODO: Change this in the design doc. Design doc's description is old.
		boolean result = false;
		double sumOfGauging = 0;	
		double sumOfArray = 0;
		double sumOfElements = (double)gaugingList.size();
		
		for(int i = 0; i < sumOfElements; i++)
		{
		  sumOfArray += gaugingList.get(i);
		}
		
		if(sumOfElements != 0)
		{
		   sumOfGauging = (sumOfArray/sumOfElements);
		   //System.out.println("sumOfGauging = " + sumOfGauging + " ( " + sumOfArray + " / " + sumOfElements);
		}
		else
		{
			sumOfGauging = 1;
		}
		
		if(sumOfGauging > GAUGING_THRESHOLD)
		{
			result = true;
		}
		
		return result;
	}
	
	// Returns a random number.
	protected double getRandom()
	{
		return r.nextDouble();
	}
	// Returns a number, between zero and input.
	protected double getRandom(int n)
	{
		return (double)(r.nextInt(n));
	}
	
	// What follows are radian translations of calculations that return degrees. Done for compatability with Math.*;
	protected void turnGunRightRadians(solomon s, double amountToRotateRadians) {
		s.turnGunRight((amountToRotateRadians/180)*Math.PI);
	}
	
	protected void turnRadarRightRadians(solomon s, double amountToRotateRadians) {
		s.turnRadarRight((amountToRotateRadians/180)*Math.PI);
	}
	
	protected void turnRightRadians(solomon s, double amountToRotateRadians) {
		s.turnRight((amountToRotateRadians/180)*Math.PI);
	}

	protected double getHeadingRadians(solomon s) 
	{
		return (s.getHeading() * (Math.PI/180));
	}
	
	protected double getGunHeadingRadians(solomon s) 
	{
		return (s.getGunHeading() * (Math.PI/180));
	}
	
	protected double getRadarHeadingRadians(solomon s)
	{
		return (s.getRadarHeading() * (Math.PI/180));
	}
	
	/**
	 * Takes in amount in degress and returns it in radians
	 * @param degrees
	 * @return
	 */
	protected double convertToRadians(double degrees)
	{
		return (degrees * (Math.PI/180));
	}
	
	protected double convertToDegress(double radians)
	{
		return (radians * (180/Math.PI));
	}
}



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//END OF CTactic.JAVA
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//START OF ALL THE TACTICS
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


class CTactic_ea1 extends CTactic {
 	int count = 0; // Keeps track of how long we've
 	// been searching for our target
 	double gunTurnAmt; // How much to turn our gun when searching
 	String trackName; // Name of the robot we're currently tracking

 	/**
 	 * run:  Tracker's main run function
 	 */	
 	@Override
 	public void run_(solomon s) {
 		
 		
 		// Prepare gun
 		trackName = null; // Initialize to not tracking anyone
 		s.setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
 		gunTurnAmt = 10; // Initialize gunTurn to 10

 		// Loop forever
 		while (true) {
 			// turn the Gun (looks for enemy)
 			s.turnGunRight(gunTurnAmt);
 			// Keep track of how long we've been looking
 			count++;
 			// If we've haven't seen our target for 2 turns, look left
 			if (count > 2) {
 				gunTurnAmt = -10;
 			}
 			// If we still haven't seen our target for 5 turns, look right
 			if (count > 3) {
 				gunTurnAmt = 10;
 			}
 			// If we *still* haven't seen our target after 10 turns, find another target
 			if (count > 11) {
 				trackName = null;
 			}
 			
 		
 		}
 	}
 	
 	/**
 	 * onScannedRobot:  Here's the good stuff
 	 */	
 	@Override
 	public void onScannedRobot_(solomon s,ScannedRobotEvent e) {

 		// If we have a target, and this isn't it, return immediately
 		// so we can get more ScannedRobotEvents.
 		if (trackName != null && !e.getName().equals(trackName)) {
 			return;
 		}

 		// If we don't have a target, well, now we do!
 		if (trackName == null) {
 			trackName = e.getName();
 			//out.println("Tracking " + trackName);
 		}
 		// This is our target.  Reset count (see the run method)
 		count = 0;
 		// If our target is too far away, turn and move torward it.
 		if (e.getDistance() > 150) {
 			gunTurnAmt = normalRelativeAngle(e.getBearing() + (s.getHeading() - s.getRadarHeading()));
 			
 			s.turnGunRight(gunTurnAmt); // Try changing these to setTurnGunRight,
 			s.turnRight(e.getBearing()); // and see how much Tracker improves...
 			// (you'll have to make Tracker an AdvancedRobot)
 			s.ahead(e.getDistance() - 135);
 			return;
 		}

 		// Our target is close.
 		gunTurnAmt = normalRelativeAngle(e.getBearing() + (s.getHeading() - s.getRadarHeading()));
 		s.turnGunRight(gunTurnAmt);
 		s.fire(3);
 		
 		// Our target is too close!  Back up.
 		if (e.getDistance() < 100) {
 			if (e.getBearing() > -90 && e.getBearing() <= 90) {
 				s.back(30);
 			} else {
 				s.ahead(40);
 			}
 		}
 		s.scan();
 	}
 	
 	/**
 	 * onHitRobot:  Set him as our new target
 	 */	
 	@Override
 	public void onHitRobot_(solomon s,HitRobotEvent e) {
 		// Only print if he's not already our target.
 		if (trackName != null && !trackName.equals(e.getName())) {
 			//out.println("Tracking " + e.getName() + " due to collision");
 		}
 		// Set the target
 		trackName = e.getName();
 		// Back up a bit.
 		// Note:  We won't get scan events while we're doing this!
 		// An AdvancedRobot might use setBack(); execute();
 		gunTurnAmt = normalRelativeAngle(e.getBearing() + (s.getHeading() - s.getRadarHeading()));
 		s.turnGunRight(gunTurnAmt);
 		s.fire(3);
 		s.back(50);
 	}

 	/**
 	 * onWin:  Do a victory dance
 	 */	
 	public void onWin(solomon s,WinEvent e) {
 		for (int i = 0; i < 50; i++) {
 			s.turnRight(30);
 			s.turnLeft(30);
 			s.turnRight(36000);
 		}
 	}
 	
 	// normalAbsoluteAngle is not used in this robot,
 	// but is here for reference.
 	/**
 	 * normalAbsoluteAngle:  Returns angle such that 0 <= angle < 360
 	 */	
 	public double normalAbsoluteAngle(double angle) {
 		if (angle >= 0 && angle < 360) {
 			return angle;
 		}
 		double fixedAngle = angle;

 		while (fixedAngle < 0) {
 			fixedAngle += 360;
 		}
 		while (fixedAngle >= 360) {
 			fixedAngle -= 360;
 		}
 		return fixedAngle;
 	}

 	/**
 	 * normalRelativeAngle:  Returns angle such that -180 < angle <= 180
 	 */	
 	public double normalRelativeAngle(double angle) {
 		if (angle > -180 && angle <= 180) {
 			return angle;
 		}
 		double fixedAngle = angle;

 		while (fixedAngle <= -180) {
 			fixedAngle += 360;
 		}
 		while (fixedAngle > 180) {
 			fixedAngle -= 360;
 		}
 		return fixedAngle;
 	}
 }

