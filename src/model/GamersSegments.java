package model;

/**
 * Defines a division of the gamers agents into the segments 
 * (social vs. non-social for instance)
 * 
 * @author mchica
 *
 */
public class GamersSegments {

	// ########################################################################
	// Variables
	// ########################################################################
	
	//-------------------------------- Static -------------------------------//
	
	static int EMPTY = 0;
	static int FIRSTSENTITY = 0;
	static double[] EMPTYVECTOR = null;
	static double[][] EMPTYARRAY = null;
	static int[] EMPTYVECTORINT = null;	
		
	private int[] segmentSizesInt;
				
	private int nodeSegmentCount;
	private int segmentCount;
	
	// ########################################################################
	// Constructors
	// ########################################################################
	
	public GamersSegments() {
		
		this.segmentSizesInt = null;		
		
		this.nodeSegmentCount = FIRSTSENTITY;
		this.segmentCount = FIRSTSENTITY;
	}
	
	
	// ########################################################################
	// Methods/Functions
	// ########################################################################

	//--------------------------- Get/Set methods ---------------------------//
		
	
	/**
	 * Gets the segment sizes in Integer.
	 * @return
	 */
	public int[] getSegmentSizesInt() {
		return segmentSizesInt;
	}

	/**
	 * Sets the segment sizes in Integer.
	 * @param segmentSizesInt
	 */
	public void setSegmentSizesInt(int[] segmentSizesInt) {
		this.segmentSizesInt = segmentSizesInt;
	}
	
	
	//-------------------------- Auxiliary methods --------------------------//

	/**
	 * Generates the sizes of the segments in Integer.
	 * @param nrNodes
	 * @param segmentSizes
	 * @return
	 */
	public void genSegmentSizesInt(double[] segmentSizes, int nrNodes) {
		int[] tmpInt = new int [segmentSizes.length];
		for(int i=0; i<segmentSizes.length; i++) {
			tmpInt[i] = (int) (segmentSizes[i] * nrNodes);
		}
		segmentSizesInt = tmpInt;
	}	
	
	/**
	 * Reset values used to assign segments.
	 */
	public void beginSegmentAssignation() {
		nodeSegmentCount = FIRSTSENTITY;
		segmentCount = FIRSTSENTITY;
	}
	
	/**
	 * Assigns a segment to a node.
	 * @return - the segment index.
	 */
	public int assignSegment() {
				
		// Check the segment
		if(nodeSegmentCount < segmentSizesInt[segmentCount]) {
			nodeSegmentCount++;
		} else {
			nodeSegmentCount = FIRSTSENTITY;
			segmentCount++;
			nodeSegmentCount++;
		}
		return segmentCount;
	}
	
	/**
	 * Generates the initial state of the agent
	 * @param segmentInitialPercentagePremium 
	 * @return
	 */
	public int genInitialSubscriptionState (double[] segmentInitialPercentagePremium) {
		
		int tmp;
		
		double val = segmentInitialPercentagePremium[segmentCount] 
				* segmentSizesInt[segmentCount];
		
		// Ensure at least 1 node with awareness
		// NO NEEDED FOR THIS MODEL if(0.01 < val && val < 1) val = 1;
		
		if(nodeSegmentCount <= (int) val) {
			tmp = Model.PREMIUM_USER;
		} else {
			tmp = Model.BASIC_USER;
		}			
		
		return tmp;
	}
	
	/**
	 * Generates gamers initial subscription state with all being premium users.
	 * @return
	 */
	public int genInitialSubscriptionState2Premium () {
		return Model.PREMIUM_USER;
	}
	
	/**
	 * Generates gamers initial subscription state with all being basic users.
	 * @return
	 */
	public int genInitialSubscriptionState2Basic() {
		return Model.BASIC_USER;
	}	
	
	/**
	 * Checks if the sum of the all segments is 1 (100%).
	 * @param nrSegments the number of segments
	 * @param segmentSizes an array with the real double sizes
	 * @return - true if it is correct; false if not.
	 */
	public boolean checkSegmentSizesConsistency(int nrSegments, double[] segmentSizes) {
		
		final double TOTALSUM = 1;
		double sum = 0;
		
		for(int i=1; i < nrSegments; i++) {
			sum += segmentSizes[i];
		}
		
		if((sum - TOTALSUM) > 0.001) {
			return false;
		} else {
			return true;
		}
	}
	
}
