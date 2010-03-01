package itc.tactic;
import itc.*;
import robocode.*;


/**
 * Moves as close as possible, firing as it retreats.
 * Implements linear targeting and bullet strength modulation.
 * 
 * Some open-source code used for linear targeting implementation.
 * 
 * @author Carl Lange
 *
 */

public class CTactic_ea0 extends CTactic 
{	
	@Override
	public void run_(solomon s)
	{

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
		fire(s, enemyDist);
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
		s.turnRight(getRandom(360));
		s.ahead(getRandom(150));
	}
}
