package socialnetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

import model.ModelParameters;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.algorithm.ConnectedComponents;

import util.Colors;
import ec.util.MersenneTwisterFast;

/**
 * Incorporates a social network graph from the GraphStream framework.
 * 
 * @author ktrawinski
 * 
 */
public class GraphStreamer {

	// ########################################################################
	// Static
	// ########################################################################
	private static final int DEFAULT_K_DEGREE_MAX = 16;

	public static final boolean STATIC_EFFICIENCY = true;
	
	public static final int FIRST_NODE = 0;
	
	private boolean staticSN;
	
	//Types of Networks
	public static enum NetworkType { 
		SCALE_FREE_NETWORK, RANDOM_NETWORK_SEGMENTS, FILE_NETWORK
	}
	
	// ########################################################################
	// Variables
	// ########################################################################	
	
	private Graph graph;	
	
	// treemap with the neighbours of the nodes 
	private TreeMap<Integer, ArrayList<Integer>> mapNeighbours;	
	
	@SuppressWarnings("unused")
	private View view;
	private int indexNodesSegments[];
	
	     
	// ########################################################################
	// Constructors
	// ######################################################################## 	
	
	/**
	 * JUST FOR ONE SEGMENT!
	 * 
	 * @param nrNodes
	 * @param _graph
	 * 
	 */
	public GraphStreamer(int nrNodes, ModelParameters params) {
			
		this.staticSN = params.isStaticSN();		
		
		final int FIRST_SEG = 0;

		this.indexNodesSegments = new int[nrNodes];
		
		graph = new SingleGraph("SNFromFile");
		
		graph.addAttribute("ui.antialias");
		graph.addAttribute("stylesheet", "graph {padding : 20px;}" 
				+ "node {fill-mode: dyn-plain; fill-color: black;}"
				+ "node.company "
				+ "{fill-mode: dyn-plain; fill-color: red, green, blue;}");
				
		for (int i = 0; i < nrNodes; i++)
			indexNodesSegments[i] = FIRST_SEG;		
				
	}
	
	/**
	 * @return the staticSN
	 */
	public boolean isStaticSN() {
		return staticSN;
	}

	/**
	 * Set static to true and if it wasn't static before we create
	 * a treemap with neighbours!
	 * 
	 * @param staticSN the staticSN to set
	 */
	public void setStaticSN(boolean _staticSN) {

		if (this.staticSN == false && STATIC_EFFICIENCY && graph != null) {		

			System.out.println("SN changed to static for efficiency");
			
			this.mapNeighbours = new TreeMap <Integer, ArrayList<Integer>> ();
		
			for (int i = 0; i < graph.getNodeCount(); i++) {
				// get neighbours of the i-th node
				ArrayList<Integer> neighbors = getNeighborsOfNodeFromGS(i);			
				this.mapNeighbours.put(i, neighbors);		
			}				
		}

		this.staticSN = _staticSN;
	}

	/** 
	 * with this function we copy the initial network read from a file
	 * in order not to have edges removed and added
	 * 
	 * @param params
	 */
	public void setGraph (ModelParameters params) {

		graph.clear();
		
		Graphs.mergeIn(graph, params.getGraph());		
		
		// while read we create a hash map with the neighbours
		if (this.staticSN && STATIC_EFFICIENCY) {
			this.mapNeighbours = new TreeMap <Integer, ArrayList<Integer>> ();
	
			for (int i = 0; i < graph.getNodeCount(); i++) {

				// get neighbours of the i-th node
				ArrayList<Integer> neighbors = getNeighborsOfNodeFromGS(i);			
				this.mapNeighbours.put(i, neighbors);						
			}			
		}
	}
	
	/**
	 * 
	 * @param nrNodes
	 * @param typeOfGraph
	 * @param connectivitySegments
	 * @param sizeSegmentsPercentage
	 * @param sizeSegments
	 * @param random
	 * @param k_degreeMax
	 * @param seed
	 */
	public GraphStreamer(int nrNodes,
			NetworkType typeOfGraph, 
			double connectivitySegments[],
			double sizeSegmentsPercentage[],
			int sizeSegments[], 
			MersenneTwisterFast random,
			int k_degreeMax) {
		
		switch(typeOfGraph) {
		case SCALE_FREE_NETWORK:
			// scale-free (Barabasi) concentric
			genScaleFreeNetworkSegments(connectivitySegments, 
					sizeSegmentsPercentage,
					sizeSegments,
					nrNodes,
					random,
					k_degreeMax);
			break;
		case RANDOM_NETWORK_SEGMENTS:
			// random network concentric
			genRandomNetworkSegments(connectivitySegments, 
					sizeSegmentsPercentage, 
					sizeSegments, 
					nrNodes, 
					random, 
					k_degreeMax);
			break;	
		default:
			// scale-free (Barabasi) concentric
			genScaleFreeNetworkSegments(connectivitySegments, 
					sizeSegmentsPercentage,
					sizeSegments,
					nrNodes,
					random,
					k_degreeMax);
		}
	}
	
	/**
	 * 
	 * @param nrNodes
	 * @param typeOfGraph
	 * @param connectivitySegments
	 * @param sizeSegmentsPercentage
	 * @param sizeSegments
	 * @param random
	 * @param seed
	 */
	public GraphStreamer(int nrNodes,
			NetworkType typeOfGraph, 
			double connectivitySegments[],
			double sizeSegmentsPercentage[],
			int sizeSegments[], 
			MersenneTwisterFast random) 
	{
		this(nrNodes,
			typeOfGraph,
			connectivitySegments,
			sizeSegmentsPercentage,
			sizeSegments,
			random,
			GraphStreamer.DEFAULT_K_DEGREE_MAX);
	}
	
	// ########################################################################	
	// Methods/Functions
	// ########################################################################

	private void genScaleFreeNetworkSegments(double connectivitySegments[], 
			   double sizeSegmentsPercentage[],
			   int sizeSegments[],
			   int nrNodes,
			   MersenneTwisterFast random,
			   int k_degreeMax) {
		
		// Specify which viewer is used 
		System.setProperty("org.graphstream.ui.renderer", 
				"org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		
		graph = new SingleGraph("Scale-Free Network Segments");		
		graph.addAttribute("ui.antialias");
		graph.addAttribute("stylesheet", "graph {padding : 20px;}" 
				+ "node {fill-mode: dyn-plain; fill-color: black;}"
				+ "node.company "
				+ "{fill-mode: dyn-plain; fill-color: red, green, blue;}");
	
		Generator gen = new ScaleFreeSegments(true);
		gen.addSink(graph);
		gen.begin();
		((ScaleFreeSegments) gen).genNetwork(connectivitySegments, 
				   sizeSegmentsPercentage,
				   sizeSegments, 
				   nrNodes, 
				   random, 
				   k_degreeMax);

		indexNodesSegments = ((ScaleFreeSegments) gen).getIndexNodesSegments();
		// TODO:gen.end() should be here? In theory ends the generation of graph
//		genView();
		gen.end();		
	}
	
	/**
	 * 
	 * @param connectivitySegments
	 * @param sizeSegments
	 * @param nrNodes
	 * @param random
	 * @param biasDensity
	 */	
	private void genRandomNetworkSegments(double connectivitySegments[], 
			   double sizeSegmentsPercentage[],
			   int sizeSegments[],
			   int nrNodes,
			   MersenneTwisterFast random,
			   int k_degreeMax) {
		
		// Specify which viewer is used 
		System.setProperty("org.graphstream.ui.renderer", 
				"org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		
		graph = new SingleGraph("Random Network Segments");		
		graph.addAttribute("ui.antialias");
		graph.addAttribute("stylesheet", "graph {padding : 20px;}" 
				+ "node {fill-mode: dyn-plain; fill-color: black;}"
				+ "node.company "
				+ "{fill-mode: dyn-plain; fill-color: red, green, blue;}");		

		Generator gen = new RandomNetworkSegments();
		gen.addSink(graph);
	    
		((RandomNetworkSegments) gen).genNetwork(connectivitySegments, 
													 sizeSegmentsPercentage,
													 sizeSegments, 
													 nrNodes, 
													 random, 
													 k_degreeMax);
		
		indexNodesSegments = ((RandomNetworkSegments) gen).getIndexNodesSegments();
		// TODO:gen.end() should be here? In theory ends the generation of graph
		
		gen.end();
	}		
		
	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public int[] getIndexNodesSegments() {
		return indexNodesSegments;
	}

	public void setIndexNodesSegments(int[] indexNodesSegments) {
		this.indexNodesSegments = indexNodesSegments;
	}

	/**
	 * e. 
	 * @param ind - index of the node.
	 * @return - the neighbors in the ArrayList.
	 */
	public ArrayList<Integer> getNeighborsOfNode(int ind) {
		
		if (this.staticSN && STATIC_EFFICIENCY) {
						
			return this.mapNeighbours.get(ind);
			
		} else {	
				
			return getNeighborsOfNodeFromGS (ind);
			
		}
	}	
	
	/**
	 * TODO BOTTLENECK Gets the neighbors of the given node. 
	 * @param ind - index of the node.
	 * @return - the neighbors in the ArrayList.
	 */
	private ArrayList<Integer> getNeighborsOfNodeFromGS (int ind) {
		
		ArrayList<Integer> neighbors = new ArrayList<Integer>();
		
		Iterator<Node> it = graph.getNode(ind).getNeighborNodeIterator();
		
		//int posValue = -1, value = -1;
		while(it.hasNext()) {
			
			int tmpInd = it.next().getIndex();
			neighbors.add(tmpInd);
			
			/*
			// insert in the correct position to have an ordered list
			if (neighbors.size() > 0) {
				posValue = neighbors.size();
				value = neighbors.get(posValue - 1);

				while (posValue > 0 && tmpInd < value) {
					posValue --;
					if (posValue > 0)
						value = neighbors.get(posValue - 1);
				}
									
				// System.out.println("to insert " + tmpInd + ", trying to put in pos " + posValue 
				//	+ ". Previous element is " + value);
								
			} else {
				posValue = 0;
			}
						
			neighbors.add(posValue, tmpInd);
			
			//System.out.println("printing neighbors: " + neighbors.toString()); */
			
		}
		
		// Sort the neighbors as their ids are returned in the arbitrary order.
		Collections.sort(neighbors);
		
		return neighbors;
	} 

	/**
	 * set a value to the attribute of an edge given by two nodes
	 * (it creates it if it doesn't exist)
	 * 
	 * @param ind1 - index of the first node.
	 * @param ind2 - index of the second node.
	 * @param key - the string key of the attribute of the edge
	 * @param value - the object value to be set for the attribute of the edge
	 */
	public void setAttributeEdge(int ind1, int ind2, String key, Object value ) {
		
		Edge edgeNodes = (graph.getNode(ind1)).getEdgeBetween(ind2);
		
		if (edgeNodes == null) {
			System.err.println("Error (setAttributeEdge) when getting an edge between node "+
						ind1 + " and node " + ind2);
		}
		
		edgeNodes.setAttribute(key, value);		
	}
	
	/**
	 * set the same value to the attributes of the edges
	 * of all the neighbors of the given node
	 * (it creates it if it doesn't exist)
	 * 
	 * @param ind - index of the first node.
	 * @param key - the string key of the attribute of the edges
	 * @param value - the object value to be set for the attribute of the edges
	 */
	public void setAttributeEdgeAllNeighbours(int ind, String key, Object value ) {
				
		Iterator<Node> it = graph.getNode(ind).getNeighborNodeIterator();
		
		while(it.hasNext()) {
			
			// get the edge 
			int indNeighbor = it.next().getIndex();			
			Edge edgeNodes = (graph.getNode(ind)).getEdgeBetween(indNeighbor );
			
			if (edgeNodes == null) {
				System.err.println("Error (setAttributeEdgeAllNeighbours) when getting an "
						+ "edge between node " +	ind + " and node " + indNeighbor);
			}
			
			// set the attribute value
			edgeNodes.setAttribute(key, value);								
		}		
	}
	

	/**
	 * get a value of the attribute of an edge given by two nodes
	 * (it creates it if it doesn't exist)
	 * 
	 * @param ind1 - index of the first node.
	 * @param ind2 - index of the second node.
	 * @param key - the string key of the attribute of the edge
	 * @return the object value of the attribute of the edge
	 */
	public Object getAttributeEdge(int ind1, int ind2, String key) {

		Edge edgeNodes = (graph.getNode(ind1)).getEdgeBetween(ind2);
		
		if (edgeNodes == null) {
			System.err.println("Error (getAttributeEdge) when getting an edge between node "+
						ind1 + " and node " + ind2);
		}
		
		return edgeNodes.getAttribute(key);	
	}		
	
	
	/**
	 * This function add a new edge with another node which has no previous edge with ind
	 * 
	 * @param ind - index of the source node.
	 * @return the id of the target node for the new edge. 
	 * 			Return -1 if not created (already has all the possible edges)
	 */
	public int addNewNeighborForNode(int ind, MersenneTwisterFast random) {
		
		if (graph.getNode(ind).getOutDegree() == (graph.getNodeCount() - 1)) {
			// the node has edges with all the remaining nodes
			return -1;
		}
		
		// at random, get a new node to be connected. It cannot be already connected
		// or be the node itself
		int node2Connect = random.nextInt(graph.getNodeCount());
		
		while (graph.getNode(ind).hasEdgeBetween(node2Connect) || (node2Connect == ind)) {
			node2Connect = random.nextInt(graph.getNodeCount());
		}
		
		// till that point we have to nodes to link (it is not efficient but works...)
		
		String idEdge = String.valueOf(ind) + "_" + String.valueOf(node2Connect);
		graph.addEdge(idEdge, ind, node2Connect, false);

		return node2Connect;
	} 
	
	/**
	 *  Remove an edge between the source node given by ind and one of its neighbors at random
	 * @param ind - index of the node.
	 * @return the other node of the removed edge. Returns -1 if not removed (no edges before)
	 */
	public int removeNeighborForNode(int ind, MersenneTwisterFast random) {
		
		if (graph.getNode(ind).getOutDegree() == 0) {
			// the node has no edges so we cannot remove anything
			return -1;
		}
		
		ArrayList<Integer> neighbors = getNeighborsOfNode(ind);
				
		int randomPos = random.nextInt(neighbors.size());
		
		int node2Disconnect = neighbors.get(randomPos);
		
		graph.removeEdge(ind, node2Disconnect);
		
		return node2Disconnect;
	} 
	
	/**
	 * Sets node's color in case of buying a product 
	 * or changing the company (product).
	 * @param ind - the index of the node.
	 * @param val - the current index of the company.
	 * @param tot - the total nr of companes.
	 */
	public void setNodeColor(int ind, int val, int tot) {
		
		Node n = graph.getNode(ind);
		// Colors vary between 0 and 1; we have to normalize the value: val/tot
		double tmp = (double) val/tot;
		n.setAttribute("ui.class", "company");
		n.setAttribute("ui.color", tmp );
	}

	/**
	 * Sets node's color in case of buying a product or changing the brand.
	 * @param ind - the index of the node.
	 * @param val - the current index of the company.
	 * @param col - the color to be set.
	 */
	public void setNodeColor(int ind, int val, Colors col) {
		Node n = graph.getNode(ind);
		
		// Use directly rgb function
		n.addAttribute("ui.style", "fill-color: rgb(" 
				+ col.getPaletteColorRed(val) + "," 
				+ col.getPaletteColorGreen(val) + "," 
				+ col.getPaletteColorBlue(val) + ");");
		// Example usage of rgb
		//n.addAttribute("ui.style", "fill-color: rgb(0,100,255);");
	}

	/**
	 * Removes node's color in case of not buying anything.
	 * @param ind - the index of the node.
	 */
	public void removeNodeColor(int ind) {
		
		Node n = graph.getNode(ind);
		// PREVIOUS IMPLEMENTATION
		//n.removeAttribute("ui.class");
		// Set to black
		n.addAttribute("ui.style", "fill-color: rgb(0, 0, 0);");
	}
	
	public void cleanGraphSteamer() {
		this.graph = null;
		this.view = null;
		this.indexNodesSegments = null;
	}

	public int getSegmentId(int id) {
		return indexNodesSegments[id];
	}
	
	public double getAvgDegree() {
		return Toolkit.averageDegree(graph);
	}
	
	public double getDensity() {
		return Toolkit.density(graph);
	}
	
	public int getConnectedComponents() {
		ConnectedComponents cc = new ConnectedComponents();
		cc.init(this.graph);
		return cc.getConnectedComponentsCount();
	}	
	
}
