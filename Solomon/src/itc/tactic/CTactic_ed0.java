package itc.tactic;

import java.awt.Color;

import robocode.*;
import itc.*;

/**
 * This needs documentation and better commenting.
 * TODO: Make this better, damnit.
 * @author Carl Lange
 *
 */
public class CTactic_ed0 extends CTactic {
	
	boolean scannedRobotYet = false;
	double enemyDistance = -1;
	double furthestPossibleDistance = -1;
	
	
	@Override
	public void run_(solomon s) {
		
		getFurthestPossibleDistance(s);
		
		if (scannedRobotYet == false) {
			s.turnGunRight(360);
		}
		
		while (scannedRobotYet == true) {
			// If distance from enemy isn't within an acceptable margin, move to a spot that is.
			
			if (acceptableDist() != true) {
				// TODO: move to a better location.
				// TODO: try the next position, (eg x+10, y+10 or something), and figure out whether the distance there is further.
				// Even better, x+rand, y+rand.
				
				if(acceptableDist(nextPosition(s))==true)
				{
					// TODO: move
				}
				
				s.turnGunLeft(360);
				
			}
			else {
				s.turnGunRight(360);
			}
		}
	}

	

	private double nextPosition(solomon s) {
		// TODO Auto-generated method stub
		return -1;
	}
	
	private boolean acceptableDist(double nextPosition) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private boolean acceptableDist() {
		// This could probably be done with bias...
		boolean n = false;
		if (enemyDistance > (furthestPossibleDistance/2))
		{
			n=true;
		}
		return n;
	}

	@Override
	public void onScannedRobot_(solomon s, ScannedRobotEvent e) {
		// TODO: This needs to be sure that it's only working for enemy AI, rather than sentries.
		// MAYBE: That should actually be in CTactic, figuring out a target or something...
		enemyDistance = e.getDistance();
		scannedRobotYet = true;
	}
	
	private void getFurthestPossibleDistance(solomon s) {
		double h = s.getBattleFieldHeight();
		double w = s.getBattleFieldWidth();
		
		double hyp = Math.sqrt((h*h) + (w*w));
		
		furthestPossibleDistance = hyp;
	}

	@Override
	public void onHitByBullet_(solomon s, HitByBulletEvent e) {
		
	}
}
