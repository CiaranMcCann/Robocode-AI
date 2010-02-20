package itc.tactic;
import itc.*;
import java.util.Random;

import robocode.*;
import robocode.util.*;

/**
 * Defensive tactic.
 * 
 * @author C00138612
 *
 */

public class CTactic_d0 extends CTactic 
{	
	Random r = new Random();
	boolean dir = false;
	
	@Override
	public void run_(solomon s)
	{
		while(true)
		{
			if (s.getHeading() % 90 != 0) s.turnLeft(s.getHeading() % 90);
			if (dir == false)
			{
				//s.ahead(Double.POSITIVE_INFINITY);
			}
			else
			{
				//s.back(Double.POSITIVE_INFINITY);
			}
			if (s.getVelocity()==0) s.turnRight(90);
			
			s.turnGunRight(360);
		}
	}

	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
		double enemyDist = e.getDistance();
		
		//double absoluteBearing = getHeadingRadians(s) + e.getBearingRadians();
		//turnGunRightRadians(s,
		    //robocode.util.Utils.normalRelativeAngle(absoluteBearing - 
		        //getGunHeadingRadians(s)));
		double absoluteBearing = getHeadingRadians(s) + e.getBearingRadians();
		turnGunRightRadians(s,Utils.normalRelativeAngle(absoluteBearing - 
		    getGunHeadingRadians(s) + (e.getVelocity() * Math.sin(e.getHeadingRadians() - 
		    absoluteBearing) / 13.0)));
		
		fire(s, enemyDist);
	}
	
	
	private void turnGunRightRadians(solomon s, double q) {
		s.turnGunRight((q/180)*Math.PI);
	}

	private double getHeadingRadians(solomon s) 
	{
		return (s.getHeading() * (Math.PI/180));
	}
	
	private double getGunHeadingRadians(solomon s) 
	{
		return (s.getGunHeading() * (Math.PI/180));
	}

	/**
	 * Uses the distance of the enemy robot to figure out how
	 * much energy to expend when firing. If it's close, it
	 * fires with strength 3, and far away, less.
	 * 
	 * @param s
	 * @param enemyDist
	 */
	
	
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
		if (dir == false)
		{
			dir = true;
		}
		else
		{
			dir = false;
		}
	}
}
