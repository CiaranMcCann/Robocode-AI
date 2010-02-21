package itc;
import java.io.IOException;
import java.io.PrintStream;

import robocode.*;
import robocode._AdvancedRobot;


public class CFile {
	
	 
	static public void logInfo(String info) 
	{	     
		
	    PrintStream printstream;
	    printstream = new PrintStream(new RobocodeFileOutputStream(getDataFile("log.txt")));
        printstream.println("foo");		

	}
	   
		
}

	  

