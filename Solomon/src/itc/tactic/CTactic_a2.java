package itc.tactic;

import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import itc.CTactic;
import itc.solomon;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class CTactic_a2 extends CTactic {
	
	@Override
	public void run_(solomon s)
	{
		while (true) {
			s.turnGunRight(5);
		}
	}

	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
		double edist = e.getDistance();
		
		fire(s, edist);
		
		s.scan();
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
		double turnGunAmt = normalRelativeAngleDegrees(e.getBearing() + s.getHeading() - s.getGunHeading());

		s.turnGunRight(turnGunAmt);
		s.fire(5);
	}
}