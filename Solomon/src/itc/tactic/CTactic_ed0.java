package itc.tactic;

import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import itc.CTactic;
import itc.solomon;

public class CTactic_ed0 extends CTactic {
	@Override
	public void run_(solomon s)
	{
		s.ahead(getRandom());
		s.turnRight(getRandom(360));
	}

	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
		fire(s, e.getDistance());
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
		s.turnRight(getRandom(360));
		s.ahead(getRandom(150));
	}
}
