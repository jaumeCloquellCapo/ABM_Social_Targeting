package calibration;

import java.io.PrintWriter;

import model.Model;


/** 
 * P2CalibAllWithInitialPrems class
 * 
 * Class that extends Parameters2Calib in order to specify the parameters
 * for calibration. In this case, this class define a problem when all the
 * parameters are calibrated and also the initial percentage of premium user
 * 8 parameters if just one segment
 * 
 * Attention: this is the most ad-hoc class. When changing calibration parameters
 * you need to include fields, getters and setters for the new parameters.
 * You will also need to change the public methods such as refreshModel() and
 * specifyParams()
 * 
 * @author mchica
 * 
 */
public class P2CalibAllWithInitialPrems extends Parameters2Calib {


	double[] segmentInitialPercentagePremium;		// percentage of premium subscribers  see Gamer::{NON_USER, BASIC_USER, PREMIUM_USER}
	
	
	// ########################################################################	
	// Methods/Functions 	
	// ########################################################################

	public P2CalibAllWithInitialPrems(int _numParameters2Calib) {
		super (_numParameters2Calib);

		this.segmentInitialPercentagePremium = new double[this.getNumSegments()];
		
	}	
	
	/**
	 * @return the segmentInitialPercentagePremium
	 */
	public double[] getSegmentInitialPercentagePremium() {
		return segmentInitialPercentagePremium;
	}
	
	/**
	 * @return the segmentInitialPercentagePremium
	 */
	public double getSegmentInitialPercentagePremium(int _k) {
		return segmentInitialPercentagePremium[_k];
	}

	/**
	 * @param segmentInitialPercentagePremium the segmentInitialPercentagePremium to set
	 */
	public void setSegmentInitialPercentagePremium(
			double[] _segmentInitialPercentagePremium) {
		this.segmentInitialPercentagePremium = _segmentInitialPercentagePremium;
	}

	/**
	 * @param segmentInitialPercentagePremium the segmentInitialPercentagePremium to set
	 */
	public void setSegmentInitialPercentagePremium(int _k,
			double _segmentInitialPercentagePremium) {
		this.segmentInitialPercentagePremium[_k] = _segmentInitialPercentagePremium;
	}

	/**
	 * Print in a writer all the parameters of the class
	 * Redefines the function 
	 */
	public void printParamters2Calib (PrintWriter writer){
		
		super.printParamters2Calib(writer);
				
		for (int k = 0; k < this.getNumSegments(); k++) {
			writer.print("Initial percentage or premiums" + k + ": "  
				+ getSegmentInitialPercentagePremium(k));
			writer.println();
		}
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
			
			// loading connectivities
			setSegmentConnectivity(k, _params[i++]);
			
			// loading probability for new friends 
			setSegmentDailyProbNewFriend(k,  _params[i++]);
			
			// loading probability for losing friends 
			setSegmentDailyProbLoseFriend(k,  _params[i++]);

			// loading probability for obtain subscription
			setSegmentDailyProbObtainSubscription(k,  _params[i++]);

			// loading probability for playing during the weekend 
			setSegmentDailyProbPlayWeekend(k, _params[i++]);
			
			// loading probability for playing during weekdays
			setSegmentDailyProbPlayNoWeekend(k,  _params[i++]);

			// loading social parameter for adoption 
			setSegmentSocialAdoptionParam(k,  _params[i++]);

			// loading initial premiums 
			setSegmentInitialPercentagePremium(k,  _params[i++]);
			
		}
					
		if (controller.CalibrationController.DEBUG_CALIB) {
			
			PrintWriter writer = new PrintWriter(System.out);
			this.printParamters2Calib(writer);
			writer.close();
					
		}
	}
	
	/**
	 *  Redefines the method of the super class as we have to also refresh
	 *  the initial number of premium members.
	 *  
	 * @param model
	 */
	public void refreshModelWithParams(Model model) {
		super.refreshModelWithParams(model);
		
		model.getParametersObject().setSegmentInitialPercentagePremium(
				this.segmentInitialPercentagePremium);
		
	}
	
			
}
