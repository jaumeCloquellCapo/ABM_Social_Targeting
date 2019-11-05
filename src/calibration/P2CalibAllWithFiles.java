package calibration;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSourceDGS;

import model.Model;


/** 
 * P2CalibAllWithFiles class
 * 
 * Class that extends Parameters2Calib in order to specify the parameters
 * for calibration. In this case, we read the SN from files and the first 
 * config. parameters is an integer selecting one of the SNs.
 * 7 parameters if just one segment (1 integer and 6 reals)
 * 
 * Attention: this is the most ad-hoc class. When changing calibration parameters
 * you need to include fields, getters and setters for the new parameters.
 * You will also need to change the public methods such as refreshModel() and
 * specifyParams()
 * 
 * @author mchica
 * 
 */
public class P2CalibAllWithFiles extends Parameters2Calib {

	private ArrayList<Graph> graphsFromFiles;		// graphs read from file	
	
	private int selectedSN;		   // the index of the selected SN from the set	
		
	private String []fileNamesGraphs;		// the file of the graph (just for exporting properties)
	
	
	// ########################################################################	
	// Methods/Functions 	
	// ########################################################################

	/**
	 * @param _graph to set
	 * @throws IOException 
	 */
	private void readGraphsFromFiles()  {


 		try {	
			// where to find SN files
			String fileName = "./networks/SN_20000";
			int numberSNs = 5;
			
			// TODO CHANGE! IT IS JUST FOR TESTING. OUT OF MEMORY
			fileNamesGraphs = new String[numberSNs];
			fileNamesGraphs [0]= fileName + "_0.001.dgs";
			fileNamesGraphs [1]= fileName + "_0.002.dgs";
			fileNamesGraphs [2]= fileName + "_0.003.dgs";
			fileNamesGraphs [3]= fileName + "_0.004.dgs";
			fileNamesGraphs [4]= fileName + "_0.005.dgs";
			//files [5]= fileName + "_0.001.dgs";
			//files [6]= fileName + "_0.001.dgs";
			//files [7]= fileName + "_0.001.dgs";
			//files [8]= fileName + "_0.001.dgs";
			//files [9]= fileName + "_0.001.dgs";

	        long time1 = System.currentTimeMillis( );

			// first create a list of Graphs
			this.graphsFromFiles = new ArrayList<Graph>();	
			
			for (int i = 0; i < numberSNs; i++) {
				Graph graphFromFile;
				graphFromFile = new SingleGraph("SN" + i);
				this.graphsFromFiles.add(graphFromFile);
			}
		
			FileSourceDGS fileSource = new FileSourceDGS();
					
			for (int i = 0; i < numberSNs; i++) {
				fileSource.addSink(this.graphsFromFiles.get(i));				
				fileSource.readAll(fileNamesGraphs[i]);
				fileSource.removeSink(this.graphsFromFiles.get(i));			
			}		
			
			long  time2  = System.currentTimeMillis( );
			System.out.println("readGraphsFromFiles: " + (double)(time2 - time1)/1000 
					+ "s for reading the SN files");
	        
			
		} catch (IOException e) {
		    	System.out.println("Error when reading SN files");
		}
					
	}
		
	/**
	 * @return the graphsFromFiles
	 */
	public ArrayList<Graph> getGraphsFromFiles() {
		return graphsFromFiles;
	}

	/**
	 * @return the selectedSN
	 */
	public int getSelectedSNIndex() {
		return selectedSN;
	}

	/**
	 * @return the selectedSN (Graph object)
	 */
	public Graph getSelectedSN() {
		return graphsFromFiles.get(selectedSN);		
	}

	/**
	 * @param selectedSN the selectedSN to set
	 */
	public void setSelectedSNIndex(int selectedSN) {
		this.selectedSN = selectedSN;
	}
	/**
	 * @param selectedSN the selectedSN to set
	 */
	public void setSelectedSN(int selectedSN) {
		this.selectedSN = selectedSN;
	}

	/**
	 * @return the fileGraph
	 */
	public String getFileGraph(int _index) {
		return fileNamesGraphs[_index];
	}

	/**
	 * @param fileGraph the fileGraph to set
	 */
	public void setFileGraph(String _fileNamesGraphs[]) {
		this.fileNamesGraphs = _fileNamesGraphs;
	}

	/**
	 * @param graphsFromFiles the graphsFromFiles to set
	 */
	public void setGraphsFromFiles(ArrayList<Graph> graphsFromFiles) {
		this.graphsFromFiles = graphsFromFiles;
	}

	
	public P2CalibAllWithFiles(int _numParameters2Calib) {
		super (_numParameters2Calib);
	
		// read graphs from a directory of DGS files
		readGraphsFromFiles();
	}	
	

	/**
	 * Print in a writer all the parameters of the class
	 * Redefines the function 
	 */
	public void printParamters2Calib (PrintWriter writer){
		
		super.printParamters2Calib(writer);

		writer.println("Graphs read from file: "  
				+ this.graphsFromFiles.size());
					
		for (int i = 0; i < this.graphsFromFiles.size(); i++) {
			
			writer.println("SN#" + i + " with " + this.graphsFromFiles.get(i).getNodeCount()
					+ " nodes. Density: " + Toolkit.density(this.graphsFromFiles.get(i))
					+ " and avg. degree of " + Toolkit.averageDegree(this.graphsFromFiles.get(i)));
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
		
		// first decode the SN to set
		int numberSN = (int)Math.round(_params[i++]);
		
		if (numberSN >= this.graphsFromFiles.size() || numberSN < 0) {
			System.err.println(
					"Error when getting the SN integer parameter. Mismatch between SNs "
					+ "and the gene: params = " + numberSN + " and we have " +
					this.graphsFromFiles.size() + " SNs loaded from files\n");
		}
		
		// set the specific graph to the parameters
		setSelectedSN(numberSN);
						
		// then, the parameters for the segments

		for (int k = 0; k < this.getNumSegments(); k++) {

			// THIS PARAMETERS HAS NO EFFECT: -1 
			setSegmentConnectivity(k, -1);
			
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
			
		}
					
		if (controller.CalibrationController.DEBUG_CALIB) {
			
			PrintWriter writer = new PrintWriter(System.out);
			this.printParamters2Calib(writer);
			writer.close();
					
		}
	}
	

	/**
	 *  Redefines the method of the super class as we have to also refresh
	 *  the SN involved.
	 *  
	 * @param model
	 */
	public void refreshModelWithParams(Model model) {
		
		super.refreshModelWithParams(model);
		
		// assigning the selected graph and the file
		model.getParametersObject().setGraph(this.getSelectedSN());
		model.getParametersObject().setNetworkFilesPattern
				(this.fileNamesGraphs[this.getSelectedSNIndex()]);
	}
	
			
}
