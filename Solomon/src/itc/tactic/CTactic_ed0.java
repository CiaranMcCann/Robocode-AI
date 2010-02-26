package itc.tactic;

import robocode.*;
import itc.CTactic;
import itc.solomon;

/**
 * This needs documentation and better commenting.
 * TODO: Make this better, damnit.
 * 
 * @author carllange
 *
 */
public class CTactic_ed0 extends CTactic {
	
	boolean scannedRobotYet = false;
	double enemyDistance = -1;
	double furthestPossibleDistance = -1;
	
	@Override
	public void run_(solomon s)
	{
		getFurthestPossibleDistance(s);
		
		while (scannedRobotYet == false){
			s.ahead(getRandom());
			s.turnRight(getRandom(360));
		}
		
		while (scannedRobotYet == true){
			
			// If distance from enemy isn't within an acceptable margin, move to a spot that is.
			if (acceptableDist() != true)
			{
				// TODO: move to a better location.
			}
		}
	}

	private boolean acceptableDist() {
		// TODO Auto-generated method stub
		// This could probably be done with bias, and put in CTactic...
		return false;
	}

	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e){
		enemyDistance = e.getDistance();
		
		scannedRobotYet = true;
		fire(s, e.getDistance());
	}
	
	private void getFurthestPossibleDistance(solomon s) {
		double h = s.getBattleFieldHeight();
		double w = s.getBattleFieldWidth();
		
		double hyp = Math.sqrt((h*h) + (w*w));
		
		furthestPossibleDistance = hyp;
	}

	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e)
	{
		s.turnRight(getRandom(360));
		s.ahead(getRandom(150));
	}
}
