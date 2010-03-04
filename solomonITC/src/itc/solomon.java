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
		 tacticLibrary[i][j] = new CTactic_ea0(); 			
		 }
		}
		
			// Uncomment these before committing!		
		/*
		tacticLibrary[0][0] = new CTactic_ea0();
		tacticLibrary[0][1] = new CTactic_ea0();
		tacticLibrary[0][2] = new CTactic_ea0();
		tacticLibrary[0][3] = new CTactic_ea0();
		
		tacticLibrary[1][0] = new CTactic_a0();
		tacticLibrary[1][1] = new CTactic_a2();
		tacticLibrary[1][2] = new CTactic_a0();
		tacticLibrary[1][3] = new CTactic_a2();
		
		tacticLibrary[2][0] = new CTactic_d1();
		tacticLibrary[2][1] = new CTactic_d1();
		tacticLibrary[2][2] = new CTactic_d0();
		tacticLibrary[2][3] = new CTactic_d1();
		
		tacticLibrary[3][0] = new CTactic_ed0();
		tacticLibrary[3][1] = new CTactic_ed1();
		tacticLibrary[3][2] = new CTactic_ed0();
		tacticLibrary[3][3] = new CTactic_ed1();
		*/
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
				   //System.out.println("\n\n [status][currentTactics] = [" + this.status +"]["+this.currentTacticIndex+"]\n\n");
			   tacticLibrary[status][currentTacticIndex].run_(this);
			}		
		   
		   /*for(int i =0; i < tacticLibrary[status][currentTacticIndex].gaugingList.size(); i++)
			{
			System.out.println(tacticLibrary[status][currentTacticIndex].gaugingList.get(i));
			}*/
		   
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
			if (s.getStatus() >= 1)
			{
				firePower = 0.1;
			}
		 }
		else if (enemyDist < 100)
		 {
			firePower = 5.0;
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
	//START OF CTactic_ea0
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


/**
 * This tactic is based on the sample robot ramFire
 * In that it rams the enemy robot, but it also trys just as hard to hit them with bullets
 */
class CTactic_ea0 extends CTactic 
{	
	private int direction = 1;
	
	@Override
	public void run_(solomon s)
	{		
		s.turnRight(5 * direction);			
	}
	
	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
	   if (!(e.getName().startsWith("example.Sentry") || e.getName().startsWith("example.sentry"))) 
		{		
			if (e.getBearing() >= 0) {
				direction = 1;
			} else {
				direction = -1;
			}
			
			s.turnRight(e.getBearing());
			
			fire(s, e.getDistance());
			
			s.ahead(e.getDistance() + 5);
		}
	   
	}
	
	public void onHitRobot_(solomon s,HitRobotEvent e) {
		
		if (e.getBearing() >= 0) 
		 {
			this.direction = 1;
		 } 
		else 
		 {
			this.direction = -1;
		 }
		s.turnRight(e.getBearing());		
		s.ahead(50);
	}	
}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//END OF CTactic_ea0
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//START OF CTactic_a0
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class CTactic_a0 extends CTactic {
	
	
	@Override
	public void run_(solomon s)
	{
	   s.ahead(100);
	   if (s.getVelocity()==0) s.turnRight(90);
	   s.turnRight(360);
	}
	
	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
	   double enemyDist = e.getDistance();
	   
	   fire(s, enemyDist);
	   
	   double absoluteBearing = getHeadingRadians(s) + e.getBearingRadians();
	   
	   turnRightRadians(s, robocode.util.Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians(s)));
	   
	   s.ahead(50);
	   if (!(e.getName().startsWith("example.Sentry") || e.getName().startsWith("example.sentry"))) 
		{
		   fire(s, enemyDist);
		}
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
	   s.turnRight(getRandom(360));
	   s.ahead(getRandom(150));
	}
	
}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//END OF CTactic_a0
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//START OF CTactic_a1
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class CTactic_a1 extends CTactic {
	
	int angleOfRadar = 0;
	double enemyDirectionConsideration = 50;
	String targetName;
	
	
	@Override
	public void run_(solomon s)
	{
	   
		   //System.out.println("angleOfGun = " + s.getGunHeading() + "  angleOfRadar = " + angleOfRadar);		
	   
		   //int angleOfGun = (int)s.getGunHeading();
	   
	   if(!((int)s.getGunHeading() == (int)(angleOfRadar)))
		{
		   int diff = ((int)s.getGunHeading()-(int)angleOfRadar);
		   
		   if(diff < 0)
			   diff *= -1;
		   
		   s.turnGunLeft(15);
		   
			   //				if(diff >= 180)
			   //				{
			   //					System.out.println(" left ");
			   //					s.turnGunLeft(15);
			   //				}
			   //				else
			   //				{
			   //					System.out.println(" right ");
			   //					s.turnGunRight(15);
			   //				}
		   
			   //System.out.println(" diff = " + diff + "\n");
		   
		}
	   
	   
	   int gunHeading = (int)s.getGunHeading();
	   
	   System.out.println("( " + (gunHeading-20) + " <= " + angleOfRadar + " )&&( " + gunHeading +" > " + (angleOfRadar-20) + " ) )");		
	   
	   
	   if((gunHeading-15 <= angleOfRadar)&&(gunHeading > angleOfRadar-15))
		{
		   s.fire(2);
			   //enemyDirectionConsideration;
		}
	   
	   s.turnRadarLeft(15);
	   
	}
	
	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
	   if(targetName == null)
		{
		   targetName = e.getName();
		}
	   
		System.out.println(/*"angleOfGun = " + angleOfGun +*/ "  angleOfRadar = " + angleOfRadar);		
	   
	   if(!(e.getName().isEmpty())&&(targetName == e.getName()))
		{	
			angleOfRadar = (int)s.getRadarHeading();			
			if(((int)e.getBearing() >= 0)&&((int)e.getBearing() <= 45))
			 {
				enemyDirectionConsideration = 0;
			 }
			
			if(((int)e.getBearing() >= 180)&&((int)e.getBearing() <= 270))
			 {
				enemyDirectionConsideration = 10;
			 }
		}
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
	}
}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//END OF CTactic_a1
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//START OF CTactic_a2
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Stops on enemy and shoots. Works.
 */
class CTactic_a2 extends CTactic {
	
	@Override
	public void run_(solomon s)
	{
	   s.turnGunRight(15);
	}
	
	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
	   
	   fire(s, e.getDistance());
	   
	   s.scan();
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
		s.turnLeft(getRandom());
	   s.ahead(200);
	}
}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//END OF CTactic_a2
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//START OF CTactic_d0
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Maybe we should remove this one. It's pretty crappy.
 * 
 * Random movement based on distance.
 * 
 */
class CTactic_d0 extends CTactic
{
	boolean scannedRobotYet = false;
	double enemyDistance = -1;
	double furthestPossibleDistance = -1;
	
	@Override
	public void run_(solomon s) {
		
		getFurthestPossibleDistance(s);
		
		if (scannedRobotYet == false) {
			s.turnGunRight(360);
		}
		
		while (scannedRobotYet == true) {
				// If distance from enemy isn't within an acceptable margin, move to a spot that is.
			if (acceptableDist() != true) {
				s.turnLeft(getRandom(360));
				s.ahead(100);
				if (s.getVelocity()==0) s.turnRight(90);
				
			}
			else {
				s.turnGunRight(360);
			}
		}
	}
	
	private boolean acceptableDist() {
			// This could probably be done with bias...
		boolean n = false;
		if (enemyDistance != -1)
		 {
			if (enemyDistance > (furthestPossibleDistance/2))
			 {
				n = true;
			 }
			return n;
		 }
		else return false;
	}
	
	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e) {
		enemyDistance = e.getDistance();
		scannedRobotYet = true;
		
		fire(s, enemyDistance);
		
		if (acceptableDist() == true)
		 {
			for (int i = 0; i < 5; i++) 
			{
				
				if (!(e.getName().startsWith("example.Sentry") || e.getName().startsWith("example.sentry"))) 
				 {
					fire(s, enemyDistance);
				 }
				s.turnGunRight(10);
			}
		 }
		else if (enemyDistance <150)
		 {
			fire(s, enemyDistance);
		 }
	}
	
	private void getFurthestPossibleDistance(solomon s) {
		double h = s.getBattleFieldHeight();
		double w = s.getBattleFieldWidth();
		
		double hyp = Math.sqrt((h*h) + (w*w));
		
		furthestPossibleDistance = hyp;
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e) {
		s.turnLeft(getRandom(360));
		s.ahead(100);
	}	
}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//END OF CTactic_d0
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//START OF CTactic_d1
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class CTactic_d1 extends CTactic {
	byte dir; // 1 when going up, 0 when going down.
	
	@Override
	public void run_(solomon s)
	{
		if (s.getHeading() % 180 != 0) // If he's not pointing up or down,
		{
			s.turnLeft(s.getHeading() % 180); // point him up or down.
		}

		if (dir==1)
		{
			s.ahead(128);
		}
		else if (dir==0)
		{
			s.ahead(128);
		}
		
		s.turnGunLeft(60);
		
		switchDir(s, s.getY()); // This method will change direction if y<128 or >battleFieldHeight-128.
	}
	
	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
	   fire(s, e.getDistance());
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
		if (s.getY() > s.getBattleFieldWidth()/2)
		{
			if (dir == 0) {
				s.turnLeft(90);
			}
			else if (dir == 1)
			{
				s.turnRight(90);
			}
		}
		else if (s.getY() < s.getBattleFieldWidth()/2)
		{
			if (dir == 0) {
				s.turnRight(90);
			}
			else if (dir == 1){
				s.turnLeft(90);
			}
		}
		s.ahead(200);
	}
	
	private void switchDir(solomon s, double y)
	{
		if (y<128) 
		{
			dir = 0;
			s.turnLeft(180);
		}
		else if (y>s.getBattleFieldHeight()-128)
		{
			dir = 1;
			s.turnLeft(180);
		}
	}
}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//END CTactic_d1
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//START OF ALL THE CTactic_ed0
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


class CTactic_ed0 extends CTactic {
	
	
	
	@Override
	public void run_(solomon s)
	{	
		if (s.getHeading() % 90 != 0) s.turnLeft(s.getHeading() % 90);
		
		s.ahead(Double.POSITIVE_INFINITY);
		
		if (s.getVelocity() == 0) s.turnRight(90);
		
		s.turnGunRight(360);
	}
	
	@Override    
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
	   double enemyDist = e.getDistance();
	   
	   if ((e.getName().contains("example.Sentry") || e.getName().startsWith("example.sentry")))
		{
		   if (s.getGunHeading() % 90 != 0) s.turnGunLeft(s.getGunHeading() % 90);
		   
		   fire(s, enemyDist);
		}
	   else if (!((e.getName().contains("example.Sentry") || e.getName().startsWith("example.sentry"))))
		{
		   double absoluteBearing = getHeadingRadians(s) + e.getBearingRadians();
		   turnGunRightRadians(s, robocode.util.Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians(s)));
		   
		   fire(s, enemyDist);
		}
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
	   
	}	
}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//END OF CTactic_ed0
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//START OF CTactic_ed1
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class CTactic_ed1 extends CTactic {
	
	private double diagonalOfBattlefield = -1; // The diagonal of the battlefield.
	private double amtToTurnBy = 0; // The amount to turn by.
	
	
	@Override
	public void run_(solomon s)
	{	
		if (diagonalOfBattlefield==-1) // If the diagonal hasn't been properly initialised yet, do that
		{
			diagonalOfBattlefield = Math.sqrt((s.getBattleFieldHeight()*s.getBattleFieldWidth())+(s.getBattleFieldWidth()*s.getBattleFieldWidth()));
		}
		
		
	    if ((s.getX()<128) && (s.getY()<128)) // Bottom left corner
		{
			if(s.getHeading() !=45) // If solomon isn't pointing north-east,
			{
				amtToTurnBy = (s.getHeading()-45); // point him north-east.
			}
		}
		else if ((s.getX()<128) && (s.getY()>(s.getBattleFieldHeight()-128))) // Top left corner
		{
			if(s.getHeading() !=135) // If solomon's not pointing south-west,
			{
				amtToTurnBy = (s.getHeading()-135); // point him that way.
			}	
		}
		else if ((s.getX()>s.getBattleFieldWidth()-128) && (s.getY()<(128))) // Bottom right corner.
		{
			if(s.getHeading() !=315) // If solomon's not pointing north-west,
			{
				amtToTurnBy = (s.getHeading()-315); // point him that way.
			}
		}
		else if ((s.getX()>(s.getBattleFieldWidth()-128)) && (s.getY()>(s.getBattleFieldHeight()-128))) // Top right corner
		{
			if(s.getHeading() !=225) // If solomon's not pointing south-east,
			{
				amtToTurnBy = (s.getHeading()-225); // point him that way.
			}
		}
		else
		{
			s.turnGunLeft(360); // If solomon's close enough to the centre of the ring, he'll spin around and fire like a madman.
			return; // Return here. I know it's terrible practice. //TODO: Don't let this return here.
		}
	    
		if ((amtToTurnBy > 0) && (amtToTurnBy <360))
		{
			// Do nothing with it.
		}
		else if (amtToTurnBy < 0)
		{
			// amtToTurnBy+=360;
			// He'll actually just turn right. Hooray!
		}
		else if (amtToTurnBy > 360)
		{
			amtToTurnBy-=360;
		}
		
		s.turnLeft(amtToTurnBy);
		
	    s.ahead(diagonalOfBattlefield/2); // Should move to the centre. Should. Probably doesn't.
	    
	    //HIDEOUS HACK
	    if (s.getVelocity() == 0) // Sometimes, he'll hit a wall but not within the confines of the corners. This'll just fix that a little.
	    {
	    	s.back(100);
	    }
	}

	@Override    
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
	   fire(s, e.getDistance()); // Really simple. Just fires.
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
	   
	}
}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//END OF CTactic_ed0
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////