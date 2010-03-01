package itc.tactic;

import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;
import itc.CTactic;
import itc.solomon;

public class CTactic_a1 extends CTactic {

	@Override
	public void run_(solomon s) {
			s.turnRight(10000);
			s.ahead(10000);
	}

	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
		double edist = e.getDistance();
		fire(s, edist);
	}

	/**
	 * onHitRobot:  If it's our fault, we'll stop turning and moving,
	 * so we need to turn again to keep spinning.
	 */
	@Override
	public void onHitRobot_(solomon s, HitRobotEvent e)
	{
		if (e.getBearing() > -10 && e.getBearing() < 10) {
			s.fire(3);
		}
		if (e.isMyFault()) {
			s.turnRight(10);
		}
	}
}
