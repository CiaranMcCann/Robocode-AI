package itc.tactic;

import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import itc.CTactic;
import itc.solomon;

public class CTactic_ea1 extends CTactic {
	
	double angleToRotateBy = 0.0;
	
	@Override
	public void run_(solomon s)
	{
		s.turnRadarRight(30.0);
	
		s.turnGunLeft(angleToRotateBy);
		
		s.fire(1);
			
	}

	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
		 
		 
		 //s.setAdjustRadarForRobotTurn(s.getRadarHeading()
				 
		System.out.println("e.getBearingRadins = " + this.convertToDegress(e.getBearingRadians()) + "  s.getGunHeading = " + s.getGunHeading());
		
		 angleToRotateBy = this.convertToDegress(e.getBearingRadians());
	
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
		
	}
	
	
}
