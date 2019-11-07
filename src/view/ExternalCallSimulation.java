package view;

import java.io.IOException;

import calibration.*;
import controller.*;

//----------------------------- MAIN FUNCTION -----------------------------//

/**
 * Main function.
 * 
 * @param args
 */

public class ExternalCallSimulation {		
	
	/**
	 * MAIN
	 */
	public static void main (String[] args) {
		
		// BEGIN TESTING WITH PARAMETERS
		
		int NUMBER_RUNS = 1;	// number of times the model is run (Monte Carlo sim)
		String parametersFile = "./config/configExternalCallBass.properties";
		//String data = "./data/premiums_98days_2012_TRA.csv";
		String data = "./data/premiums_33days_2012_TEST.csv";
		
		long seed = 1;			// till now we set the seed to 1
		
		// {CONNECT-S1; NEWFR-S1, LOSEFR-S1, OBTAIN-S1,
		//  WEEKEND-S1, NOWEEKEND-S1,  SOCIALADOPTION-S1};
		//String[] _args = {"0.2", "0.2", "0.1",  "0.05", "0.4",
		//		"0.25","0.1"};
		String[] _args = {"0.534952", "0.39844", "0.134553", "0.0997433", 
				"0.086815", "0.0299628", "0.400341"};
		
		args = _args;
				
		// END TESTING WITH PARAMETERS
					
		// get all the parameters from the arguments to allocate them in an object
		
		// we assume it is a complete calibration
		Parameters2Calib parameters = new Parameters2Calib(7);
		parameters.specifyParams (args);
						
		try {

			CalibrationController controller = 
					new CalibrationController (data, parametersFile
							, seed, parameters, NUMBER_RUNS);
			
			CalibrationStats stats = new CalibrationStats(controller.getRatioAgents(), 
					NUMBER_RUNS, controller.getMaxSteps());
				
			controller.runModelWithCalib(stats);
			
			// we finally print out the results of the objectives			
			double obj1 = stats.getAvgEucl();			
			//double obj1 = stats.getAvgL0();		
			double obj2 = stats.getAvgCorr();	
			//double obj1 = stats.getAvgManhattan();
			//double obj2 = stats.getAvgChebyshev();

			System.out.print(obj1 + ";" + obj2 + "\n");
			
		} catch (IOException e) {
			System.err.println("Error when reading from CSV\n");
		} 
						
	}
	
}
