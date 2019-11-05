package socialnetwork;

import model.Model;

import org.graphstream.algorithm.generator.BaseGenerator;

import ec.util.MersenneTwisterFast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.PropertyConfigurator;

import util.Functions;

/**
 * Generates a random network with different segments. Each segment has
 * a different connectivity (a probability to connect to the other node). 
 * @author ktrawinski
 *
 */
public class RandomNetworkSegments extends BaseGenerator {

	// ########################################################################
	// Methods/Functions	
	// ########################################################################	

	/**
	 * Array pointing a segment of each node 
	 */
	private int indexNodesSegments[];
	
	private Logger logger = LoggerFactory.getLogger(RandomNetworkSegments.class);

	/**
	 * Gets the indes of nodes of their segments.
	 * @return
	 */
	public int[] getIndexNodesSegments() {
		return indexNodesSegments;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getIndexNodeSegments(int i) {
		return indexNodesSegments[i];
	}	

	/**
	 * 
	 * @param indexNodesSegments
	 */
	public void setIndexNodesSegments(int[] indexNodesSegments) {
		this.indexNodesSegments = indexNodesSegments;
	}

	/**
	 * 
	 * @param indexNodesSegments
	 */
	public void setIndexNodeSegments(int i, int indexNodeSegments) {
		this.indexNodesSegments[i] = indexNodeSegments;
	}
	
	/**
	 * Adds a new edge for the selected two nodes
	 * @param node1 - a first node to be connected
	 * @param node2 - a second node to be connected
	 */
	protected void addNewEdge(int node1, int node2) {
		String nodeId = node1 + "";
		String edgeId = node2 + "_" + nodeId;
		addEdge(edgeId, node2 + "", nodeId);
	}	
	
	/**
	 * Generates random graph with different probabilities for each segment
	 * @param probabilitySegments
	 * @param sizeSegments
	 * @param nrSegments
	 * @param nrNodes
	 */
	public void genNetwork(double probabilitySegments[], 
			double sizeSegmentsPercentage[],
			int sizeSegments[], 
			int nrNodes, 
			MersenneTwisterFast random,
			int k_degreeMaxValues) {
		//PropertyConfigurator.configure(Model.getLogFileName());

		final boolean INCLUDEZERO = true;
		final boolean INCLUDEONE = true;
		double[] probabilityConnection = new double[nrNodes];
		int poolNodesSegments[] = new int[sizeSegments.length];
		int indexSegment;
		this.indexNodesSegments = new int[nrNodes];

		System.arraycopy(sizeSegments, 0, poolNodesSegments, 0, sizeSegments.length);

		// Generates initial nodes
		for(int nodeCount=0; nodeCount<nrNodes; nodeCount++){
			addNode(nodeCount + "");

			// Assign segments randomly			
			do {
				double r = random.nextDouble();
				logger.debug("genNetwork() random.nextDouble() " + r);
				indexSegment = Functions.randomWeightedSelection(sizeSegmentsPercentage, r);				
			} while(poolNodesSegments[indexSegment] == 0);

			poolNodesSegments[indexSegment]--;
			this.indexNodesSegments[nodeCount] = indexSegment;
			probabilityConnection[nodeCount] = probabilitySegments[indexSegment];
		}
		// Gen connections		
		for(int i=0; i<nrNodes; i++) {
			for(int j=i+1; j<nrNodes; j++) {
				// MersenneTwisterFast random
				double r = random.nextDouble(INCLUDEZERO, INCLUDEONE);
				logger.debug("genNetwork() random.nextDouble(INCLUDEZERO, INCLUDEONE) " + r);
				// product
				//	double val = (probabilityConnection[i] 
				//			* probabilityConnection[j] 
				//			* ((double) k_degreeMaxValues / (nrNodes - 1)));
				// average
				double val = (((probabilityConnection[i] + probabilityConnection[j]) / 2) 
						* ((double) k_degreeMaxValues / (nrNodes - 1)));				
				if(r <= val) {
					addNewEdge(i, j);	
				}
			}
		}
	}	


	//-------------------------- Overridden methods -------------------------//	
	
	//TODO: Provide a try catch block for these methods
	@Override
	public void begin() {}

	@Override
	public boolean nextEvents() {return false;}	
	
	@Override
	public void end() {super.end();}
}
