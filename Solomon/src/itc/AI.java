package itc;
import itc.CTactic;

/**
 * This class is Solomon's decision making component.
 * Here it assesses the past experiences and judges whether
 * the tactic was successful in the past.
 * @author C00134748
 *
 */
public class AI {

	/**
	 * This is the percentage amount of negative change which
	 * is required or greater to get a negative result on the efficiency
	 */
	public static byte gaugingThreshold = 10;
	
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
		int currentTactic = 0;		
		
		for(int i = 0; i <  tacticLibrary[status].length; i++)
		{
			if(tacticLibrary[status][currentTacticIndex].isGoodTactic(status))
			{
				System.out.println("solomon things tactic = "+ currentTacticIndex + " is good");
				currentTactic = i;
				i = tacticLibrary.length;
			}
			else
			{
				System.out.println("solomon things tactic = "+ currentTacticIndex + " is bad");
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
			System.out.print("changeInHealth = "+ changeInHealth);
			if(changeInHealth > 10)
			{
				tacticGauge = 0;
			}
			
		}
	
		return tacticGauge;
	}
}
