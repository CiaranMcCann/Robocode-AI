package itc;
import java.io.IOException;

import robocode.*;


public class CFile {
	
	 
	static public void logInfo(String info) 
	{	     

		RobocodeFileWriter in;
		try {
			in = new RobocodeFileWriter("log.txt");
			in.append(info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		


	}
	   
		
	  }

	  

