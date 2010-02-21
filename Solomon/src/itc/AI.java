package itc;
import itc.CTactic;

/**
 * This class is Solomon's decision making compent.
 * Here it assess the passed expereinces and judges weather
 * the tactic was sucessful in the passed.
 * @author C00134748
 *
 */
public class AI {

	/**
	 * This is the percentage amount of negtive change which
	 * is require or greater to get a negtive result on the effecncey
	 */
	public static byte gauagingThreshold = 10;
	
	public static byte getGauagingThreshold() {
		return gauagingThreshold;
	}
	public static void setGauagingThreshold(byte gauagingThreshold) {
				
		AI.gauagingThreshold = gauagingThreshold;
	}
	
	
	/**
	 * Picks a tactic for the robot based on the current status its in
	 * i.e defensive, aggressive etc. It then calls another method isGoodTactic()
	 * which will find if the tactic as been sucessful in the passed
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
	 * a tactic was used and after to determin if its a sucessful tactic
	 * @param healthBefore
	 * @param currentHealth
	 * @return
	 */
	public static byte gaugeTactic(double healthBefore, double currentHealth)
	{
		byte tacticGauage = 1;
		double changeInHealth = 0;
		
		if(healthBefore > currentHealth)
		{
		
			changeInHealth = (((healthBefore/currentHealth)*100.0F)-100);	
			System.out.print("changeInHealth = "+ changeInHealth);
			if(changeInHealth > 5)
			{
				tacticGauage = 0;
			}
			
		}
	
		return tacticGauage;
	}
}
