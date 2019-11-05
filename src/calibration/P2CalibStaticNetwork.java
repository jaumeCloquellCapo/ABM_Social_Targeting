package calibration;

import java.io.PrintWriter;


/** 
 * P2CalibStaticNetwork class
 * 
 * Class that extends Parameters2Calib in order to specify the parameters
 * for calibration. In this case, this class define a problem where
 * the SN is static (add and remove edges are set to 0).
 * The number of parameters to be calibrated are 4, if just one segment
 * 
 * Attention: this is the most ad-hoc class. When changing calibration parameters
 * you need to include fields, getters and setters for the new parameters.
 * You will also need to change the public methods such as refreshModel() and
 * specifyParams()
 * 
 * @author mchica
 * 
 */
public class P2CalibStaticNetwork extends Parameters2Calib {

	
	// ########################################################################	
	// Methods/Functions 	
	// ########################################################################

     
	public P2CalibStaticNetwork(int _numParameters2Calib) {
		super (_numParameters2Calib);
				
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
		
		int i = 0;

		for (int k = 0; k < this.getNumSegments(); k++) {

			// THIS PARAMETERS HAS NO EFFECT: -1 
			setSegmentConnectivity(k, -1);
			
			// THIS PARAMETER IS ALWAYS 0.  loading probability for new friends 
			setSegmentDailyProbNewFriend(k,  -1);
			
			//  THIS PARAMETER IS ALWAYS 0.  loading probability for losing friends 
			setSegmentDailyProbLoseFriend(k, -1);

			// loading probability for obtain subscription
			setSegmentDailyProbObtainSubscription(k,  _params[i++]);

			// loading probability for playing during the weekend 
			setSegmentDailyProbPlayWeekend(k, _params[i++]);
			
			// loading probability for playing during weekdays
			setSegmentDailyProbPlayNoWeekend(k,  _params[i++]);

			// loading social parameter for adoption 
			setSegmentSocialAdoptionParam(k,  _params[i++]);
		}
					
		if (controller.CalibrationController.DEBUG_CALIB) {
			
			PrintWriter writer = new PrintWriter(System.out);
			this.printParamters2Calib(writer);
			writer.close();
					
		}
	}
			
}
