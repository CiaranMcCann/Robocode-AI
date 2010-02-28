package itc.tactic;
import itc.*;

import robocode.*;


/**
 * Defensive tactic.
 * 
 * @author C00138612
 *
 */

public class CTactic_d0 extends CTactic 
{	
	@Override
	public void run_(solomon s)
	{
			if (s.getHeading() % 90 != 0) s.turnLeft(s.getHeading() % 90);
			
			s.ahead(Double.POSITIVE_INFINITY);
			
			if (s.getVelocity()==0) s.turnRight(90);
			
			s.turnGunRight(360);
	}

	@Override    
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
		double enemyDist = e.getDistance();
		
		double absoluteBearing = getHeadingRadians(s) + e.getBearingRadians();
		turnGunRightRadians(s, robocode.util.Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians(s)));
		
		fire(s, enemyDist);
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
		
	}
}
