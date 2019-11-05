package socialnetwork;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ec.util.MersenneTwisterFast;
import model.Model;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.PropertyConfigurator;

import util.Functions;

/**
 * Scale-free graph generator with SEGMENTS using the preferential attachment rule as 
 * defined in the Barabási-Albert model.
 * 
 * <p>
 * This is a very simple graph generator that generates a graph using the
 * preferential attachment rule defined in the Barabási-Albert model: nodes are
 * generated one by one, and each time attached by one or more edges other
 * nodes. The other nodes are chosen using a biased random selection giving more
 * chance to a node if it has a high degree.
 * </p>
 * 
 * <p>
 * The more this generator is iterated, the more nodes are generated. It can
 * therefore generate graphs of any size. One node is generated at each call to
 * {@link #nextEvents()}. At each node added at least one new edge is added. The
 * number of edges added at each step is given by the
 * {@link #getMaxLinksPerStep()}. However by default the generator creates a
 * number of edges per new node chosen randomly between 1 and
 * {@link #getMaxLinksPerStep()}. To have exactly this number of edges at each
 * new node, use {@link #setExactlyMaxLinksPerStep(boolean)}.
 * </p>
 * 
 * @reference Albert-László Barabási & Réka Albert
 *            "Emergence of scaling in random networks", Science 286: 509–512.
 *            October 1999. doi:10.1126/science.286.5439.509.
 */
public class ScaleFreeSegments extends BaseGenerator {
	/**
	 * Degree of each node.
	 */
	protected List<Integer> degrees;

	/**
	 * The maximum number of links created when a new node is added.
	 */
	protected int maxLinksPerStep;

	/**
	 * Does the generator generates exactly {@link #maxLinksPerStep}.
	 */
	protected boolean exactlyMaxLinksPerStep = false;
	
	/**
	 * The sum of degrees of all nodes
	 */
	protected int sumDeg;
	
	/**
	 * The sum of degrees of nodes not connected to the new node
	 */
	protected int sumDegRemaining;
	
	/**
	 * Set of indices of nodes connected to the new node
	 */
	protected Set<Integer> connected;
	
	/**
	 * Array pointing a segment of each node 
	 */
	private int indexNodesSegments[];
	
	private Logger logger = LoggerFactory.getLogger(ScaleFreeSegments.class);

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
	 * New generator.
	 */
	public ScaleFreeSegments() {
		this(1, false);
	}

	/**
	 * 
	 * @param maxLinksPerStep
	 */
	public ScaleFreeSegments(int maxLinksPerStep) {
		this(maxLinksPerStep, false);
	}

	/**
	 * 
	 * @param exactlyMaxLinksPerStep
	 */
	public ScaleFreeSegments(boolean exactlyMaxLinksPerStep) {
		this(1, exactlyMaxLinksPerStep);
	}
	

	public ScaleFreeSegments(int maxLinksPerStep,
			boolean exactlyMaxLinksPerStep) {
		this.directed = false;
		this.maxLinksPerStep = maxLinksPerStep;
		this.exactlyMaxLinksPerStep = exactlyMaxLinksPerStep;
	}

	/**
	 * Maximum number of edges created when a new node is added.
	 * 
	 * @return The maximum number of links per step.
	 */
	public int getMaxLinksPerStep() {
		return maxLinksPerStep;
	}

	/**
	 * True if the generator produce exactly {@link #getMaxLinksPerStep()}, else
	 * it produce a random number of links ranging between 1 and
	 * {@link #getMaxLinksPerStep()}.
	 * 
	 * @return Does the generator generates exactly
	 *         {@link #getMaxLinksPerStep()}.
	 */
	public boolean produceExactlyMaxLinkPerStep() {
		return exactlyMaxLinksPerStep;
	}

	/**
	 * Set how many edge (maximum) to create for each new node added.
	 * 
	 * @param max
	 *            The new maximum, it must be strictly greater than zero.
	 */
	public void setMaxLinksPerStep(int max) {
		maxLinksPerStep = max > 0 ? max : 1;
	}

	/**
	 * Set if the generator produce exactly {@link #getMaxLinksPerStep()}
	 * (true), else it produce a random number of links ranging between 1 and
	 * {@link #getMaxLinksPerStep()} (false).
	 * 
	 * @param on
	 *            Does the generator generates exactly
	 *            {@link #getMaxLinksPerStep()}.
	 */
	public void setExactlyMaxLinksPerStep(boolean on) {
		exactlyMaxLinksPerStep = on;
	}

	/**
	 * Start the generator. Two nodes connected by edge are added.
	 * 
	 * @see org.graphstream.algorithm.generator.Generator#begin()
	 */
	public void begin() {
		//PropertyConfigurator.configure(Model.getLogFileName());
		
		addNode("0");
		addNode("1");
		addEdge("0_1", "0", "1");
		degrees = new ArrayList<Integer>();
		degrees.add(1);
		degrees.add(1);
		sumDeg = 2;
		connected = new HashSet<Integer>();
	}

	/**
	 * Step of the generator. Add a node and try to connect it with some others.
	 * 
	 * The number of links is randomly chosen between 1 and the maximum number
	 * of links per step specified in {@link #setMaxLinksPerStep(int)}.
	 * 
	 * The complexity of this method is O(n) with n the number of nodes if the
	 * number of edges created per new node is 1, else it is O(nm) with m the
	 * number of edges generated per node.
	 * 
	 * @see org.graphstream.algorithm.generator.Generator#nextEvents()
	 */
	public boolean nextEvents() {
		// Empoty method, not used
		return true;
	}
	
	/**
	 * ------------------------------------------------------------------------
	 * KT 2014
	 * ------------------------------------------------------------------------	 
	 * Step of the generator. Add a node and try to connect it with some others.
	 * 
	 * The number of links is randomly chosen between 1 and the maximum number
	 * of links (PROVIDED FOR EACH NODE OF THE GIVEN SEGMENT!!!) 
	 * per step specified in {@link #setMaxLinksPerStep(int)}.
	 * 
	 * The complexity of this method is O(n) with n the number of nodes if the
	 * number of edges created per new node is 1, else it is O(nm) with m the
	 * number of edges generated per node.
	 * 
	 * @see org.graphstream.algorithm.generator.Generator#nextEvents()
	 */
	public boolean nextEvents(int maxLinksInThisStep, MersenneTwisterFast random) {
		this.maxLinksPerStep = maxLinksInThisStep;
		//this.exactlyMaxLinksPerStep = true;
		// Generate a new node.
		int nodeCount = degrees.size();
		String newId = nodeCount + "";
		addNode(newId);

		// Attach to how many existing nodes?
		int n = maxLinksInThisStep;
		if (!exactlyMaxLinksPerStep) {
			n = random.nextInt(n) + 1;
			//logger.debug("nextEvents() random.nextInt(n)+1" + n);
		}

		n = Math.min(n, nodeCount);

		// Choose the nodes to attach to.
		sumDegRemaining = sumDeg;
		for (int i = 0; i < n; i++) {
			chooseAnotherNode(random);
			//logger.debug("nextEvents() chooseAnotherNode(random)");			
		}
			
		for (int i : connected) {
			addEdge(newId + "_" + i, newId, i + "");
			degrees.set(i, degrees.get(i) + 1);
		}
		connected.clear();
		degrees.add(n);
		sumDeg += 2 * n;

		// It is always possible to add an element.
		return true;
	}	
	
	/**
	 * Choose randomly one of the remaining nodes 
	 */
	protected void chooseAnotherNode(MersenneTwisterFast random) {
		int r = random.nextInt(sumDegRemaining);
		//logger.debug("chooseAnotherNode() random.nextInt(sumDegRemaining) " + r);	
		int runningSum = 0;
		int i = 0;
		while (runningSum <= r) {
			if (!connected.contains(i))
				runningSum += degrees.get(i);
			i++;
		}
		i--;
		connected.add(i);
		sumDegRemaining -= degrees.get(i);
	}
		
	/**
	 * Generates random graph with different probabilities for each segment
	 * @param probabilitySegments
	 * @param sizeSegments
	 * @param nrSegments
	 * @param nrNodes
	 */
	public void genNetwork(double connectivitySegments[], 
						   double sizeSegmentsPercentage[],
						   int sizeSegments[], 
						   int nrNodes, 
						   MersenneTwisterFast random,
						   int k_degreeMax) {
		
		final int FIRST_SEG = 0;
		final int SECOND_SEG = 1;
		final int FIRST_NODE_INDEX = 0;
		final int SECOND_NODE_INDEX = 1;
		final boolean INCLUDE_ZERO = true;
		final boolean EXCLUDE_ZERO = false;
		this.indexNodesSegments = new int[nrNodes];
		int poolNodesSegments[] = new int[sizeSegments.length];
		int indexSegment;
		int initNodes = 2;
		
		System.arraycopy(sizeSegments, 0, poolNodesSegments, 0, sizeSegments.length);
		
		// If there at least 2 segments, assign the first two nodes to the
		// two non-empty segments.
		if(sizeSegments.length > 1) {
			int[] segmentIndexes = new int[2];
			int counter = 0;
			
			// Find two segments with the size greater than 0.
			for(int i=0; i<sizeSegments.length; i++) {
				if(sizeSegments[i] != 0){
					segmentIndexes[counter] = i;
					counter++;
				}
				if(counter == 2) break;
			}

			if(counter == 2) {
				indexNodesSegments[FIRST_NODE_INDEX] = segmentIndexes[FIRST_SEG];
				indexNodesSegments[SECOND_NODE_INDEX] = segmentIndexes[SECOND_SEG];
			
				poolNodesSegments[segmentIndexes[FIRST_SEG]]--;
				poolNodesSegments[segmentIndexes[SECOND_SEG]]--;
			} else if (counter == 1) {
				indexNodesSegments[FIRST_NODE_INDEX] = segmentIndexes[FIRST_SEG];
				indexNodesSegments[SECOND_NODE_INDEX] = segmentIndexes[FIRST_SEG];
				
				poolNodesSegments[segmentIndexes[FIRST_SEG]]--;
				poolNodesSegments[segmentIndexes[FIRST_SEG]]--;
			} else {
				// TODO: Should not happen!!! Throw an exception!
			}
			
			// If there is only one segment, assign the first two nodes to that
			// segment.	
		} else {
			indexNodesSegments[FIRST_NODE_INDEX] = FIRST_SEG;
			indexNodesSegments[SECOND_NODE_INDEX] = FIRST_SEG;
			
			poolNodesSegments[FIRST_SEG]--;
			poolNodesSegments[FIRST_SEG]--;
		}

		// Initialization starts with 2 nodes
		for(int i=0; i<nrNodes-initNodes; i++) {
			do {
				double r = random.nextDouble(INCLUDE_ZERO, EXCLUDE_ZERO);
				//logger.debug("genNetwork() random.nextDouble(INCLUDE_ZERO, EXCLUDE_ZERO) " + r);
				indexSegment = Functions.randomWeightedSelection(sizeSegmentsPercentage, r);				
			} while(poolNodesSegments[indexSegment] == 0);

			// To obtain m, we have to divide the <k> by 2. The equation is
			// following: m = (<k>/2)*connectivitySegment.
			int m = (int) Math.round(((k_degreeMax / 2) * connectivitySegments[indexSegment]));
			
			if(m < 1) 
				m = 1;

			//System.out.println("m: "+ m + ". kmax: " + k_degreeMax + " connec: " + connectivitySegments[indexSegment]);
					
			nextEvents(m, random);
			//logger.debug("genNetwork() nextEvents(m, random)");
			poolNodesSegments[indexSegment]--;
			
			indexNodesSegments[i + initNodes] = indexSegment;
		}
	}

	/**
	 * Clean degrees.
	 * 
	 * @see org.graphstream.algorithm.generator.Generator#end()
	 */
	@Override
	public void end() {
		degrees.clear();
		degrees = null;
		connected = null;
		super.end();
	}
}
