package itc;
import itc.CTactic;

/**
 * This class is Solomon's decision making component.
 * Here it assesses the past experiences and judges whether
 * the tactic was successful in the past.
 * @author Ciaran McCann
 *
 */
public class AI {

	/**
	 * This is the percentage amount of negative change which
	 * is required or greater to get a negative result on the efficiency
	 */
	public static byte gaugingThreshold = 3;
	
	public static byte getGaugingThreshold() {
		return gaugingThreshold;
	}
	public static void setGaugingThreshold(byte gaugingThreshold) {
				
		AI.gaugingThreshold = gaugingThreshold;
	}
	
	
	/**
	 * Picks a tactic for the robot based on the current status it's in
	 * i.e defensive, aggressive etc. It then calls another method isGoodTactic()
	 * which will find if the tactic as been successful in the past
	 * @param status
	 * @param currentTacticIndex
	 * @param tacticLibrary
	 * @return Tactic to be used
	 */
	public static int pickTactic(int status, int currentTacticIndex, CTactic tacticLibrary[][])
	{		
		int currentTactic = currentTacticIndex;		
		
		for(int i = 0; i <  tacticLibrary[status].length; i++)
		{
			if(tacticLibrary[status][currentTactic].isGoodTactic(status))
			{
				currentTactic = i;
				i = tacticLibrary[status].length;
			}
			else
			{
				if(currentTactic < (tacticLibrary[status].length-1))
				{
				currentTactic++;
				}				
			}
		}						
		return currentTactic;
	}
	
	
	/**
	 * Returns a one or a zero based on the health change in the time before
	 * a tactic was used and after to determine if it's a successful tactic
	 * @param healthBefore
	 * @param currentHealth
	 * @return
	 */
	public static byte gaugeTactic(double healthBefore, double currentHealth)
	{
		byte tacticGauge = 1;
		double changeInHealth = 0;
		
		if(healthBefore > currentHealth)
		{	
			changeInHealth = (((healthBefore/currentHealth)*100.0F)-100);
			if(changeInHealth > gaugingThreshold)
			{
				tacticGauge = 0;
			}		
		}	
		return tacticGauge;
	}
}
