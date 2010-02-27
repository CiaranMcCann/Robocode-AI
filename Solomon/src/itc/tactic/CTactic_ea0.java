package itc.tactic;
import itc.*;
import robocode.*;


/**
 * THIS COMMENT IS BULLSHIT. =]
 * (Extrememly?) aggressive tactic.
 * 
 * Rams the opponent, then moves as far away as possible, firing as it retreats.
 * Then rams the enemy again.
 * Implements linear targeting.
 * 
 * Some open-source code used for linear targeting implementation.
 * 
 * @author C00138612
 *
 */

public class CTactic_ea0 extends CTactic 
{	
	@Override
	public void run_(solomon s)
	{
		s.turnRight(360);
	
		
	}

	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
		double enemyDist = e.getDistance();
		
		double absoluteBearing = getHeadingRadians(s) + e.getBearingRadians();
		turnGunRightRadians(s,
		    robocode.util.Utils.normalRelativeAngle(absoluteBearing - 
		        getGunHeadingRadians(s)));

		fire(s, enemyDist);
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
		s.turnRight(getRandom(360));
		s.ahead(getRandom(150));
	}
}
