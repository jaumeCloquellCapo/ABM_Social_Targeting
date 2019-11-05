package calibration;

import model.*;

import java.io.PrintWriter;

/** 
 * Parameters2Calib class
 * 
 * Class for allocating, setting and refreshing the Animal Jam model
 * with the parsed parameters.
 * 
 * Attention: this is the most ad-hoc class. When changing calibration parameters
 * you need to include fields, getters and setters for the new parameters.
 * You will also need to change the public methods such as refreshModel() and
 * parseParamsFromArgs()
 * 
 * @author mchica
 * 
 */
public class Parameters2Calib {

	private int numSegments;
	
	private int numParameters2Calib;			// the total number of params to calibrate
	
	private double[] segmentConnectivity;				// connectivity of the network
	
	private double[] segmentDailyProbNewFriend;			// probability to have a new friend
	
	private double[] segmentDailyProbLoseFriend;			// probability to lose an old friend
	
	private double[] segmentDailyProbObtainSubscription;	// probability  to become a premium subscriptor without friends info
	
	private double[] segmentDailyProbPlayWeekend;				// probability  to play during the weekend
	
	private double[] segmentDailyProbPlayNoWeekend;				// probability to play during weekdays
	
	private double[] segmentSocialAdoptionParam;					// prob or threshold for simple or cascade contagion depending on the experiment
		
	// ########################################################################	
	// Methods/Functions 	
	// ########################################################################


	//--------------------------- Get/Set methods ---------------------------//	

    /**
	 * @return the numSegments
	 */
	public int getNumSegments() {
		return numSegments;
	}


	/**
	 * @param numSegments the numSegments to set
	 */
	public void setNumSegments(int numSegments) {
		this.numSegments = numSegments;
	}

	/**
	 * @return the segmentDailyProbPlayWeekend
	 */
	public double[] getSegmentDailyProbPlayWeekend() {
		return segmentDailyProbPlayWeekend;
	}
	
	/**
	 * Gets the segment prob new friend probabilities.
	 * @return
	 */
	public double[] getSegmentDailyProbNewFriend() {
		return segmentDailyProbNewFriend;
	}
	
	/**
	 *  Sets the segment prob new friend probabilities.
	 * @param segmentTalkingProbabilities
	 */
	public void setSegmentDailyProbNewFriend(double[] _segmentProbNewFriend) {
		this.segmentDailyProbNewFriend = _segmentProbNewFriend;
	}
	
	/**
	 * Gets the prob. of losing a friend 
	 * @return
	 */
	public double[] getSegmentDailyProbLoseFriend() {
		return segmentDailyProbLoseFriend;
	}
	
	/**
	 * Sets the prob. of losing a friend 
	 * @param segmentAwarenessDecay
	 */
	public void setSegmentDailyProbLoseFriend(double[] _segmentProbLoseFriend) {
		this.segmentDailyProbLoseFriend = _segmentProbLoseFriend;
	}
	
	/**
	 * Gets the segment prob for becoming a premium subscriptor without looking at friends
	 * @param _index 
	 * @return
	 */
	public double getSegmentDailyProbObtainSubscription(int _index) {
		return segmentDailyProbObtainSubscription[_index];
	}
	
	/**
	 * Gets the segment prob for a new friend 
	 * @param _index 
	 * @return
	 */
	public double getSegmentDailyProbNewFriend(int _index) {
		return segmentDailyProbNewFriend[_index];
	}
	
	/**
	 * Gets the segment prob for losing a friend 
	 * @param _index 
	 * @return
	 */
	public double getSegmentDailyProbLoseFriend(int _index) {
		return segmentDailyProbLoseFriend[_index];
	}
	
	/**
	 * Sets the segment prob for becoming a premium subscriptor for an index
	 * @param _index 
	 * @return
	 */
	public void setSegmentDailyProbObtainSubscription(int _index, double _value) {
		segmentDailyProbObtainSubscription[_index] = _value;
	}
	
	/**
	 * Sets the segment prob for a new friend for an index
	 * @param _index 
	 * @return
	 */
	public void setSegmentDailyProbNewFriend(int _index, double _value) {
		segmentDailyProbNewFriend[_index] = _value;
	}
	
	/**
	 * Sets the segment prob for losing a friend for an index
	 * @param _index 
	 * @return
	 */
	public void setSegmentDailyProbLoseFriend(int _index, double _value) {
		segmentDailyProbLoseFriend[_index] = _value;
	}
	/**
	 * Gets the segment prob for becoming a premium subscriptor without looking at friends
	 * @return
	 */
	public double[] getSegmentDailyProbObtainSubscription() {
		return segmentDailyProbObtainSubscription;
	}
	
	/**
	 *  Sets the segment prob for becoming a premium subscriptor without looking at friends
	 * @param segmentDailyProbObtainSubscription
	 */
	public void setSegmentDailyProbObtainSubscription(double[] _segmentProbObtainSubscription) {
		this.segmentDailyProbObtainSubscription = _segmentProbObtainSubscription;
	}
	
	/**
	 * @return the segmentDailyPlayNoWeekend
	 */
	public double[] getSegmentDailyProbPlayNoWeekend() {
		return segmentDailyProbPlayNoWeekend;
	}

	/**
	 * @return the segmentDailyPlayNoWeekend
	 */
	public double getSegmentDailyProbPlayNoWeekend(int index) {
		return segmentDailyProbPlayNoWeekend[index];
	}

	/**
	 * @param segmentDailyPlayNoWeekend the segmentDailyPlayNoWeekend to set
	 */
	public void setSegmentDailyProbPlayNoWeekend(double[] segmentDailyPlayNoWeekend) {
		this.segmentDailyProbPlayNoWeekend = segmentDailyPlayNoWeekend;
	}

	/**
	 * Sets the segment prob for playing no weekends
	 * @param _index 
	 * @return
	 */
	public void setSegmentDailyProbPlayNoWeekend(int _index, double _value) {
		segmentDailyProbPlayNoWeekend[_index] = _value;
	}
	
	/**
	 * @param i 
	 * @return the segmentDailyPlayWeekend
	 */
	public double getSegmentDailyProbPlayWeekend(int index) {
		return segmentDailyProbPlayWeekend[index];
	}

	/**
	 * @param segmentDailyPlayWeekend the segmentDailyPlayWeekend to set
	 */
	public void setSegmentDailyProbPlayWeekend(double[] segmentDailyPlayWeekend) {
		this.segmentDailyProbPlayWeekend = segmentDailyPlayWeekend;
	}
	
	/**
	 * Sets the segment prob for playing weekends
	 * @param _index 
	 * @return
	 */
	public void setSegmentDailyProbPlayWeekend(int _index, double _value) {
		segmentDailyProbPlayWeekend[_index] = _value;
	}
	
	/**
	 * @return the numParameters2Calib
	 */
	public int getNumParameters2Calib() {
		return numParameters2Calib;
	}


	/**
	 * @param numParameters2Calib the numParameters2Calib to set
	 */
	public void setNumParameters2Calib(int numParameters2Calib) {
		this.numParameters2Calib = numParameters2Calib;
	}


	/**
	 * @return the segmentSocialAdoptionParam
	 */
	public double[] getSegmentSocialAdoptionParam() {
		return segmentSocialAdoptionParam;
	}

	/**
	 * @param segmentSocialAdoptionParam the segmentSocialAdoptionParam to set
	 */
	public void setSegmentSocialAdoptionParam(
			double[] segmentSocialAdoption) {
		this.segmentSocialAdoptionParam = segmentSocialAdoption;
	}
	
	/**
	 * Sets the segment prob for social adoption (both simple and cascade)
	 * @param _index 
	 * @return
	 */
	public void setSegmentSocialAdoptionParam(int _index, double _value) {
		segmentSocialAdoptionParam[_index] = _value;
	}

	public double getSegmentSocialAdoptionParam(int i) {
		
		return segmentSocialAdoptionParam[i];
	}

	/**
	 * @return the segmentConnectivity
	 */
	public double[] getSegmentConnectivity() {
		return segmentConnectivity;
	}

	/**
	 * @return the segmentConnectivity
	 */
	public double getSegmentConnectivity(int _index) {
		return segmentConnectivity[_index];
	}

	/**
	 * @param segmentConnectivity the segmentConnectivity to set
	 */
	public void setSegmentConnectivity(double[] segmentConnectivity) {
		this.segmentConnectivity = segmentConnectivity;
	}

	/**
	 * @param segmentConnectivity the segmentConnectivity to set
	 */
	public void setSegmentConnectivity(int _index, double _value) {
		this.segmentConnectivity[_index] = _value;
	}
	
	/**
	 * Print in a writer all the parameters of the class
	 */
	public void printParamters2Calib (PrintWriter writer){
		
		writer.print("Parameters changed by the calibration algorithm!!\n");

		for (int k = 0; k < this.getNumSegments(); k++) {
			
			writer.print("Connectivity Seg" + k + ": "  
				+ getSegmentConnectivity(k));
			writer.println();
			
			writer.print("Prob new friend Seg" + k + ": "  
					+ getSegmentDailyProbNewFriend(k));
			writer.println();
	
			writer.print("Prob lose friend Seg" + k + ": "  
					+ getSegmentDailyProbLoseFriend(k));
			writer.println();
	
			writer.print("Prob innovation subscription Seg" + k + ": "  
					+ getSegmentDailyProbObtainSubscription(k));
			writer.println();	

			writer.print("Prob play weekends Seg" + k + ": "  
					+ getSegmentDailyProbPlayWeekend(k));
			writer.println();	

			writer.print("Prob play no weekends Seg" + k + ": " 
					+ getSegmentDailyProbPlayNoWeekend(k));
			writer.println();	

			writer.print("Social adoption Seg" + k + ": " 
					+ getSegmentSocialAdoptionParam(k));
			
			writer.println();					
		}
	}
	
	/**
	 * Constructor
	 */
	public Parameters2Calib(int _numParameters2Calib) {

		this.numSegments = 1;  // ONE SEGMENT
		
		this.setNumParameters2Calib(_numParameters2Calib);
		
		this.segmentDailyProbLoseFriend = new double[numSegments];
		this.segmentDailyProbNewFriend = new double[numSegments];
		this.segmentDailyProbObtainSubscription = new double[numSegments];	
		this.segmentDailyProbPlayWeekend = new double[numSegments];		
		this.segmentDailyProbPlayNoWeekend = new double[numSegments];	
		this.segmentSocialAdoptionParam = new double[numSegments];			
		this.segmentConnectivity = new double[numSegments];	
		
	}
	
	/**
	 *  With this function we refresh the parameters of the model with the
	 *  ones of the object
	 * @param model
	 */
	public void refreshModelWithParams(Model model) {
		
		model.getParametersObject().setSegmentDailyProbObtainSubscription(this.segmentDailyProbObtainSubscription);
		model.getParametersObject().setSegmentDailyProbNewFriend(this.segmentDailyProbNewFriend);
		model.getParametersObject().setSegmentDailyProbLoseFriend(this.segmentDailyProbLoseFriend);
		model.getParametersObject().setSegmentDailyProbPlayWeekend(this.segmentDailyProbPlayWeekend);
		model.getParametersObject().setSegmentDailyProbPlayNoWeekend(this.segmentDailyProbPlayNoWeekend);
		model.getParametersObject().setSegmentsConnectivity(this.segmentConnectivity);
		model.getParametersObject().setSegmentSocialAdoptionParam(this.segmentSocialAdoptionParam);
	}
	

	/**
	 * ad-hoc function to set the parameters from a list of double values 
	 * (e.g. GAindividual)
	 * @_params a double array with the parameters
	 * @parameters the object to allocate these parameters 
	 * to be used by the calibration controller
	 */
	public void specifyParams (double[] _params){
				
	}
	
	/**
	 * function for getting parameters from a string (piping).
	 * It redirects to the double[] function
	 * @_args the arguments from the console
	 * to be used by the calibration controller
	 * @override
	 */
	public void specifyParams (String[] _args){
		
		if (_args.length != this.getNumParameters2Calib()) {
			System.err.println(
					"Error when setting parameters from a string argument. "
					+ "We don't have + " + this.getNumParameters2Calib() 
					+ " parameters\n");
		}
		
		double []params = new double[_args.length];

		// convert from string to double
		for (int i = 0; i < _args.length; i++) {
			params[i] = Double.parseDouble(_args[i]);
		}
		
		// call the parser function that gets a double array
		 specifyParams (params);		
	}
								
}
