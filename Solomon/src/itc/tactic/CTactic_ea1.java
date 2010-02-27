package itc.tactic;

import robocode.*;
import robocode.util.Utils;

import itc.CTactic;
import itc.solomon;

public class CTactic_ea1 extends CTactic {
	
	double angleToRotateBy = 0.0;
	
	@Override
	public void run_(solomon s)
	{
		s.turnRadarRight(30.0);
	
	/*	System.out.println("--- " + this.angleToRotateBy +" ------ " + s.getGunHeading());
		
		if(this.angleToRotateBy != s.getGunHeading())
		{
		s.turnGunLeft(angleToRotateBy);
		}
		*/
		//s.fire(1);
			
	}

	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e)
	{
		 /*double angle = this.convertToDegress( Math.acos((e.getDistance()/s.getX())));
		 angleToRotateBy = angle;*/
		
		 double radarTurn =
		        getHeadingRadians(s) + e.getBearingRadians()
		        // Subtract current radar heading to get turn required
		        - getRadarHeadingRadians(s);

		    s.turnRadarRight(Utils.normalRelativeAngle(radarTurn));
		    
		    s.fire(radarTurn);
		    

		 

	
	}
	
	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
		
	}
	
	
}
