package calibration;

import java.io.PrintWriter;


/** 
 * P2CalibCThresholdsFixedSN class
 * 
 * Class that extends Parameters2Calib in order to specify the parameters
 * for calibration. In this case, we have a fixed and static SN 
 * (specify in the config file) and then 3 parameters if just one segment (3 reals)
 * 
 * Attention: this is the most ad-hoc class. When changing calibration parameters
 * you need to include fields, getters and setters for the new parameters.
 * You will also need to change the public methods such as refreshModel() and
 * specifyParams()
 * 
 * @author mchica
 * 
 */
public class P2CalibThresholdsWithInnovFixedSN extends Parameters2Calib {

	
	// ########################################################################	
	// Methods/Functions 	
	// ########################################################################
	
	
	public P2CalibThresholdsWithInnovFixedSN(int _numParameters2Calib) {
		super (_numParameters2Calib);
	}	
		
	/**
	 * Print in a writer all the parameters of the class
	 * Redefines the function 
	 */
	public void printParamters2Calib (PrintWriter writer){
		
		super.printParamters2Calib(writer);
		
	}	
	
	/**
	 * ad-hoc function to set the parameters from a list of double values 
	 * 
	 * @_params a double array with the parameters
	 * @parameters the object to allocate these parameters 
	 * to be used by the calibration controller
	 * @override
	 */
	public void specifyParams (double[] _params){
		
		if (_params.length != this.getNumParameters2Calib()) {
			System.err.println(
					"Error when getting parameters from a double array. "
					+ "We don't have + " +  this.getNumParameters2Calib() 
					+ " parameters\n");
		}
										
		// the parameters for the segments

		int i = 0;
		for (int k = 0; k < this.getNumSegments(); k++) {

			// THIS PARAMETERS HAS NO EFFECT: -1 
			setSegmentConnectivity(k, -1);
			
			// THIS PARAMETERS HAS NO EFFECT: -1 
			setSegmentDailyProbNewFriend(k,  -1);
			
			// THIS PARAMETERS HAS NO EFFECT: -1 
			setSegmentDailyProbLoseFriend(k,  -1);

			// innovation probability
			setSegmentDailyProbObtainSubscription(k,  _params[i++]);

			// loading probability for playing during the weekend 
			setSegmentDailyProbPlayWeekend(k, _params[i++]);
			
			// loading probability for playing during weekdays
			setSegmentDailyProbPlayNoWeekend(k,  _params[i++]);

			// loading social parameter for adoption 
			// as it is a complex contagion, it will be an integer
			setSegmentSocialAdoptionParam(k,  _params[i++]);
						
		}
					
		if (controller.CalibrationController.DEBUG_CALIB) {
			
			PrintWriter writer = new PrintWriter(System.out);
			this.printParamters2Calib(writer);			
					
		}
	}
				
}
