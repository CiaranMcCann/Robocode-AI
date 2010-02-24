package itc.tactic;

import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import itc.CTactic;
import itc.solomon;

public class CTactic_a0 extends CTactic {
	@Override
	public void run_(solomon s)
	{
		s.turnRight(Double.POSITIVE_INFINITY);	
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
