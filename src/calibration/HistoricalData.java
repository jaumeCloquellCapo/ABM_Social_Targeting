package calibration;

import java.io.IOException;

import util.Functions;

/** 
 * HistoricalData class
 * 
 * Class for allocating and parsing historical data to be used for calibrating
 * the Animal simulation model
 * 
 * @author mchica
 * 
 */
public class HistoricalData {
	
	private double[] historicalData;		// an array with the historical data of the model
	
	private int numSteps;				// the number of steps to run model (= historical length ) 
	
	private String CSVFile;				// file to load historical data
	
	
	// ########################################################################	
	// Methods/Functions 	
	// ########################################################################

        
	//--------------------------- Get/Set methods ---------------------------//
	
	/**
	 * @return the numSteps
	 */
	public int getNumSteps() {
		return numSteps;
	}

	/**
	 * @param _numSteps the numSteps to set
	 */
	public void setNumSteps(int _numSteps) {
		this.numSteps = _numSteps;
	}

	/**
	 * @return the cSVFile
	 */
	public String getCSVFile() {
		return CSVFile;
	}

	/**
	 * @param cSVFile the cSVFile to set
	 */
	public void setCSVFile(String _CSVFile) {
		CSVFile = _CSVFile;
	}

	/**
	 * @return the historicalData
	 */
	public double[] getHistoricalDataArray() {
		return historicalData;
	}

	/**
	 * @return the historicalData of a position
	 */
	public double getHistoricalDataArray(int index) {
		return historicalData[index];
	}

	/**
	 * @param historicalData the historicalData to set
	 */
	public void setHistoricalDataArray(double[] _historicalData) {
		this.historicalData = _historicalData;
	}

	//--------------------------- Auxiliary methods ---------------------------//
	
	/**
	 * Function to read from CSV the historical data
	 */
	private void parseFromCSVFile () throws IOException {

		double temp [][];		// = new double[1][this.numSteps];

		if (controller.CalibrationController.DEBUG_CALIB) {
			System.out.print(this.CSVFile);
			System.out.println();				
		}	
		
		temp = Functions.readHistoryFromCSV(this.CSVFile);
		
		// get the number of columns as the necessary steps for running the model
		this.numSteps = (temp[0]).length;
				
		// get just the first column
		this.historicalData = temp[0];
			
	}
	
	//--------------------------- CONSTRUCTOR ---------------------------//
	
	/**
	 * Constructor that automatically loads data from the CSV file passed as an argument
	 * @param _CSVFile the file to be loaded
	 */
	public HistoricalData (String _CSVFile) throws IOException {
		
		CSVFile = _CSVFile;
		parseFromCSVFile ();		
		
	}
	
					
}
