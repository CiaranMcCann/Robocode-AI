package itc;


import itc.CTactic;
import itc.AI;
import itc.tactic.*;
import robocode.*;


/**
 * Solomon - a robot by IT Carlow students Ciarán McCann and Carl Lange.
 */
public class solomon extends AdvancedRobot
{
	private byte status;
	public byte getStatus() 
	{
		return status;
	}
	public void setStatus(byte status) 
	{
		this.status = status;
	}

	private int currentTacticIndex;
	private CTactic tacticLibrary[][];
	
	private double healthBeforeTactic;
	
	private final int E_AGGRESSIVE = 85;
	private final int AGGRESSIVE= 65;
	private final int DEFENSIVE = 45;
	//private final int E_DEFENSIVE = 25;
	
	long maxduration = 9000; // 10 seconds.
	long endtime = 0;


	
	public solomon()
	{
		status = 0;
		currentTacticIndex = 0;
		
		tacticLibrary = new CTactic[4][4];
		
		for (int j = 0; j < tacticLibrary.length; j++) {
			for (int i = 0; i < tacticLibrary.length; i++) {
				tacticLibrary[i][j] = new CTactic_d0();
			}
		}
		
		healthBeforeTactic = 0;		
	}
		
	/**
	 * run: Solomon's default behavior
	 */

	public void run() 
	{						
		while(true) {
				
			status = this.assessHealth();
			currentTacticIndex = AI.pickTactic(status, currentTacticIndex, tacticLibrary);			
			healthBeforeTactic =  this.getEnergy();
			endtime = System.currentTimeMillis() + maxduration;
			
			while(System.currentTimeMillis() < endtime)
			{
				System.out.println("\n\n Time now = " + System.currentTimeMillis() + "\n  endtime = " + endtime + "\n [status][currentTactics] = [" + this.status +"]["+this.currentTacticIndex+"]\n\n");
				tacticLibrary[status][currentTacticIndex].run_(this);
				//CFile.logInfo("fuck"); expection 
				System.out.println("energy = " + this.getEnergy());
			}	
		
			
			for(int i =0; i < tacticLibrary[status][currentTacticIndex].gaugingList.size(); i++)
			{
				System.out.println(tacticLibrary[status][currentTacticIndex].gaugingList.get(i));
			}
			
			endtime = System.currentTimeMillis() + maxduration;
			tacticLibrary[status][currentTacticIndex].gaugingList.add(AI.gaugeTactic(healthBeforeTactic, this.getEnergy()));		
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		
		tacticLibrary[status][currentTacticIndex].onScannedRobot_(this, e);
		
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		tacticLibrary[status][currentTacticIndex].onHitByBullet_(this, e);
		
	}
	
	
	/**
	 * Assess the health of the solomon and returns a status number
	 * which will be used as an index in accessing the 2D array of tactics.
	 * @return
	 */
	private byte assessHealth()
	{
		double health =  this.getEnergy();
		byte status = 0;
		
		if(health >= E_AGGRESSIVE)
		{
			status = 0;
		}
		else if((health >= AGGRESSIVE)&&(health < E_AGGRESSIVE))
		{
			status = 1;
		}
		else if((health >= DEFENSIVE)&&(health < AGGRESSIVE))
		{
			status = 2;
		}
		else // TODO: Finish me here.
		{
			status = 3;
		}
		
		return status;
	}
	
	
}
