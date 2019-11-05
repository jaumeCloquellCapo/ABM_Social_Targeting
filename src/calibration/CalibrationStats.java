package calibration;

// package for std and mean

import java.io.PrintWriter;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.*;
import org.apache.commons.math3.stat.StatUtils;

//for euclidean, manhattan and chebyshev
import org.apache.commons.math3.ml.distance.*;


/** 
 * CalibrationStats
 * 
 * This class will store all the results for the MonteCarlo simulation.
 * It will also update the stats and error metrics w.r.t. the historical data.
 * 
 * @author mchica
 *
 */
public class CalibrationStats {

	private int ratioAgentsPop;			// ratio to correlate historical and simulated data

	private int numberRuns;				// number of MC simulations

	private long numberSteps;			// number of steps simulation
	
	private double simulated[][];			// where to allocate simulated data
	private double ratedSimulated[][];			// same as simulated (by rated by agents ratio)
	
	
	// fields for corr Pearson results  
	private double corr[];			// all the corr Pearson metrics (each for run)
	private double avgCorr;			// average over all the runs
	private double stdCorr;			// std over all the runs
	
	// fields for Euclidean (L2) results  = MSE or RMSE
	private double eucl[];			// all the Euclidean metrics (each for run)
	private double avgEucl;			// average over all the runs
	private double stdEucl;			// std over all the runs
	
	// fields for Manhattan (L1) results  = MAE
	private double manhattan[];			// all the Manhattan metrics (each for run)
	private double avgManhattan;			// average over all the runs
	private double stdManhattan;			// std over all the runs

	// fields for Chebyshev (LINF) results
	private double chebyshev[];			// all the Chebyshev metrics (each for run)
	private double avgChebyshev;			// average over all the runs
	private double stdChebyshev;			// std over all the runs
	
	// fields for L0 results
	private double L0[];			// all the L0 metrics (each for run)
	private double avgL0;			// average over all the runs
	private double stdL0;			// std over all the runs

	
	// fields for Euclidean (L2) results  = MSE or RMSE for cumulative trends
	private double euclCum[];			// all the Euclidean metrics (each for run)
	private double avgEuclCum;			// average over all the runs
	private double stdEuclCum;			// std over all the runs
	
	
	// ########################################################################	
	// Methods/Functions 	
	// ########################################################################

	//--------------------------- Getters and setters ---------------------------//

	/**
	 * @return the stdChebyshev
	 */
	public double getStdChebyshev() {
		return stdChebyshev;
	}

	/**
	 * @param stdChebyshev the stdChebyshev to set
	 */
	public void setStdChebyshev(double _stdChebyshev) {
		this.stdChebyshev = _stdChebyshev;
	}

	/**
	 * @return the l0
	 */
	public double[] getL0() {
		return L0;
	}

	/**
	 * @param l0 the l0 to set
	 */
	public void setL0(double[] _l0) {
		L0 = _l0;
	}

	/**
	 * @return the avgL0
	 */
	public double getAvgL0() {
		return avgL0;
	}

	/**
	 * @param avgL0 the avgL0 to set
	 */
	public void setAvgL0(double _avgL0) {
		this.avgL0 = _avgL0;
	}

	/**
	 * @return the stdL0
	 */
	public double getStdL0() {
		return stdL0;
	}

	/**
	 * @param stdL0 the stdL0 to set
	 */
	public void setStdL0(double _stdL0) {
		this.stdL0 = _stdL0;
	}

	/**
	 * @return the avgChebyshev
	 */
	public double getAvgChebyshev() {
		return avgChebyshev;
	}

	/**
	 * @param avgChebyshev the avgChebyshev to set
	 */
	public void setAvgChebyshev(double _avgChebyshev) {
		this.avgChebyshev = _avgChebyshev;
	}

	/**
	 * @return the chebyshev
	 */
	public double[] getChebyshev() {
		return chebyshev;
	}

	/**
	 * @param chebyshev the chebyshev to set
	 */
	public void setChebyshev(double _chebyshev[]) {
		this.chebyshev = _chebyshev;
	}

	/**
	 * @return the stdManhattan
	 */
	public double getStdManhattan() {
		return stdManhattan;
	}

	/**
	 * @param stdManhattan the stdManhattan to set
	 */
	public void setStdManhattan(double _stdManhattan) {
		this.stdManhattan = _stdManhattan;
	}

	/**
	 * @return the avgManhattan
	 */
	public double getAvgManhattan() {
		return avgManhattan;
	}

	/**
	 * @param avgManhattan the avgManhattan to set
	 */
	public void setAvgManhattan(double _avgManhattan) {
		this.avgManhattan = _avgManhattan;
	}

	/**
	 * @return the manhattan
	 */
	public double[] getManhattan() {
		return manhattan;
	}

	/**
	 * @param manhattan the manhattan to set
	 */
	public void setManhattan(double _manhattan[]) {
		this.manhattan = _manhattan;
	}

	/**
	 * @return the stdEucl
	 */
	public double getStdEucl() {
		return stdEucl;
	}

	/**
	 * @param stdEucl the stdEucl to set
	 */
	public void setStdEucl(double _stdEucl) {
		this.stdEucl = _stdEucl;
	}

	/**
	 * @return the avgEucl
	 */
	public double getAvgEucl() {
		return avgEucl;
	}

	/**
	 * @param avgEucl the avgEucl to set
	 */
	public void setAvgEucl(double _avgEucl) {
		this.avgEucl = _avgEucl;
	}

	/**
	 * @return the eucl
	 */
	public double[] getEucl() {
		return eucl;
	}

	/**
	 * @param eucl the eucl to set
	 */
	public void setEucl(double _eucl[]) {
		this.eucl = _eucl;
	}

	/**
	 * @return the stdEuclCum
	 */
	public double getStdEuclCum() {
		return stdEuclCum;
	}

	/**
	 * @param stdEuclCum the stdEuclCum to set
	 */
	public void setStdEuclCum(double _stdEuclCum) {
		this.stdEuclCum = _stdEuclCum;
	}

	/**
	 * @return the avgEuclCum
	 */
	public double getAvgEuclCum() {
		return avgEuclCum;
	}

	/**
	 * @param avgEuclCum the avgEuclCum to set
	 */
	public void setAvgEuclCum(double _avgEuclCum) {
		this.avgEuclCum = _avgEuclCum;
	}

	/**
	 * @return the euclCum
	 */
	public double[] getEuclCum() {
		return euclCum;
	}

	/**
	 * @param eucl the euclCum to set
	 */
	public void setEuclCum(double _euclCum[]) {
		this.euclCum = _euclCum;
	}
	
	/**
	 * @return the stdCorr
	 */
	public double getStdCorr() {
		return stdCorr;
	}

	/**
	 * @param stdCorr the stdCorr to set
	 */
	public void setStdCorr(double _stdCorr) {
		this.stdCorr = _stdCorr;
	}

	/**
	 * @return the avgCorr
	 */
	public double getAvgCorr() {
		return avgCorr;
	}

	/**
	 * @param avgCorr the avgCorr to set
	 */
	public void setAvgCorr(double _avgCorr) {
		this.avgCorr = _avgCorr;
	}

	/**
	 * @return the corr
	 */
	public double[] getCorr() {
		return corr;
	}

	/**
	 * @param corr the corr to set
	 */
	public void setCorr(double _corr[]) {
		this.corr = _corr;
	}

	/**
	 * @return the simulation results
	 */
	public double[][] getSimulated() {
		return simulated;
	}

	/**
	 * @return the simulated results rated by agents ratio
	 */
	public double[][] getRatedSimulated() {
		return ratedSimulated;
	}
	
	/**
	 * also, during this function call, we rate the simulated 
	 * data with the agent-pop ratio. The first dimension of simulated represents the
	 * runs, the second represents the steps
	 * 
	 * @param simulated the results to set
	 */
	public void setSimulated(double _simulated[][]) {
		this.simulated = _simulated;

		// we multiply the simulated values by the ratio
		this.ratedSimulated = _simulated;
		
		for (int i = 0; i < this.ratedSimulated.length; i++) 
			for (int j = 0; j < this.ratedSimulated[i].length; j++) {

				this.ratedSimulated[i][j] = 
				Math.round(this.ratedSimulated[i][j] * this.getRatioAgentsPop());

			}
		
	}

	/**
	 * @return the numberRuns
	 */
	public int getNumberRuns() {
		return numberRuns;
	}

	/**
	 * @param numberRuns the numberRuns to set
	 */
	public void setNumberRuns(int _numberRuns) {
		this.numberRuns = _numberRuns;
	}

	/**
	 * @return the ratioAgentsPop
	 */
	public int getRatioAgentsPop() {
		return ratioAgentsPop;
	}

	/**
	 * @param ratioAgentsPop the ratioAgentsPop to set
	 */
	public void setRatioAgentsPop(int ratioAgentsPop) {
		this.ratioAgentsPop = ratioAgentsPop;
	}
	
	/**
	 * @return the numberSteps
	 */
	public long getNumberSteps() {
		return numberSteps;
	}

	/**
	 * @param numberSteps the numberSteps to set
	 */
	public void setNumberSteps(long _numberSteps) {
		this.numberSteps = _numberSteps;
	}
	

	//--------------------------- Public methods ---------------------------//
	/**
	 * Calculate the L0 norm (hamming distance) between two vectors and return 
	 * the binary array for each pair of points
	 * @param _simulated
	 * @param _historical
	 * @return a binary array with hamming distances
	 */
	public double computeL0 (double _simulated[], double _historical[]){
		
		if (_simulated.length != _historical.length) {
			return -1;
		}
		
		double result = 0.;
		
		for (int i = 0; i < _simulated.length; i++) 
			
			if (Double.compare(_simulated[i], _historical[i]) != 0) 
				result = result + 1;
		
		return result;		
		
	}

    
	//--------------------------- Constructor ---------------------------//
	/**
	 * constructor of CalibrationStats
	 * @param _nRuns
	 */
	public CalibrationStats (int _ratioAgentsPop, int _nRuns, long _nSteps){
		
		ratioAgentsPop = _ratioAgentsPop;
		numberRuns = _nRuns;
		numberSteps = _nSteps;
		
		// allocating space for metrics
		this.corr = new double[numberRuns];
		this.chebyshev = new double[numberRuns];
		this.manhattan = new double[numberRuns];
		this.eucl = new double[numberRuns];
		this.L0 = new double[numberRuns];
		
		// the same for the cumulative trends
		this.euclCum = new double[numberRuns];
		
	}
	

	/**
	 * This method prints stats to a stream file
	 * @param writer that is opened before calling the function
	 * @param historical a double array with the real values to be printed out
	 */
	public void printStats (PrintWriter writer, double [] historical) {
		DescriptiveStatistics statsRuns = new DescriptiveStatistics();
		
		writer.println("Euclidean; Mean; " + this.avgEucl + "; Std; " + this.stdEucl);	
		writer.println("L0; Mean; " + this.avgL0 + "; Std; " + this.stdL0);	
		writer.println("Manhattan; Mean; " + this.avgManhattan + "; Std; " + this.stdManhattan);	
		writer.println("Chebyshev; Mean; " + this.avgChebyshev + "; Std; " + this.stdChebyshev);	
		writer.println("Correlation; Mean; " + this.avgCorr + "; Std; " + this.stdCorr);	

		writer.println("Euclidean(cum); Mean; " + this.avgEuclCum + "; Std; " + this.stdEuclCum);	
		
		writer.println("; ; ; ; ");	
		writer.print("Step;Hist");
		
		for (int i = 0; i < this.numberRuns; i++) {
			writer.print(";Run_" + i); 
		}

		writer.print(";Mean;Min;Max");
		
		writer.println();
		
		for (int j = 0; j < (this.ratedSimulated[0]).length; j++) {
			writer.print( j +";" + historical[j] + ";"); 
			
			statsRuns.clear();
			
			// first we calculate all the metrics for each run
			for (int i = 0; i < this.numberRuns; i++) {
				writer.print(this.ratedSimulated[i][j] + ";");
				statsRuns.addValue(this.ratedSimulated[i][j]);
			}
			
			// printing avg, ;max and min of the runs
			writer.print(statsRuns.getMean() + ";");
			writer.print(statsRuns.getMin() + ";");
			writer.print(statsRuns.getMax());
			
			writer.println();
		}

		writer.println("; ; ; ; ");	
	}

	/**
	 * This method is the core method for calculating all the metrics
	 * between historical and simulated data
	 * @param historical is the historical evolution (size = steps)
	 */
	public void calcAllStats (double [] historical) {
		
		double cumHistorical[] = new double[1];
		double cumSimulated[] = new double[1];
		
		
		PearsonsCorrelation corrCalc = new PearsonsCorrelation();
		EuclideanDistance eucCalc = new EuclideanDistance ();
		ChebyshevDistance chebCalc = new ChebyshevDistance ();
		ManhattanDistance manCalc = new ManhattanDistance ();
				
		// first we calculate all the metrics for each run
		for (int i = 0; i < this.numberRuns; i++) {
			
			// calculating Pearson-product moment correlation (TO BE MAXIMIZED)
			this.corr[i] = corrCalc.correlation(ratedSimulated[i], historical);
			
			// calculating euclidean
			this.eucl[i] = eucCalc.compute(ratedSimulated[i], historical);
			
			// calculating manhattan
			this.manhattan[i] = manCalc.compute(ratedSimulated[i], historical);
			
			// calculating chebyshev
			this.chebyshev[i] = chebCalc.compute(ratedSimulated[i], historical);

			// calculating L0
			this.L0[i] = this.computeL0(ratedSimulated[i], historical);

			// calculating euclidean for cumulative	
			cumHistorical[0] = StatUtils.sum(historical);
			cumSimulated[0] = StatUtils.sum(ratedSimulated[i]);
			
			this.euclCum[i] = eucCalc.compute(cumSimulated, cumHistorical);
								
			if (controller.CalibrationController.DEBUG_CALIB) {
				System.out.println("Stats for run " + i );
				System.out.println("Pearson correlation: " + this.corr[i] );
				System.out.println("L0: " + this.L0[i] );
				System.out.println("Euclidean distance: " + this.eucl[i] );
				System.out.println("Manhattan: " + this.manhattan[i] );
				System.out.println("Chebyshev: " + this.chebyshev[i] );	

				System.out.println("Euclidean distance (cumulative trend): " + this.euclCum[i] );
			}
		}
		
		// Get a DescriptiveStatistics instance
		DescriptiveStatistics statsCorr = new DescriptiveStatistics();
		DescriptiveStatistics statsEucl = new DescriptiveStatistics();
		DescriptiveStatistics statsMan = new DescriptiveStatistics();
		DescriptiveStatistics statsCheb = new DescriptiveStatistics();
		DescriptiveStatistics statsL0 = new DescriptiveStatistics();

		DescriptiveStatistics statsEuclCum = new DescriptiveStatistics();
		
		// Add the data from the array
		for( int i = 0; i < this.numberRuns; i++) {
	        statsCorr.addValue(this.corr[i]);
	        statsEucl.addValue(this.eucl[i]);
	        statsMan.addValue(this.manhattan[i]);
	        statsCheb.addValue(this.chebyshev[i]);
	        statsL0.addValue(this.L0[i]);
	        
	        statsEuclCum.addValue(this.euclCum[i]);
		}

		// calc mean and average for all of them
		
		this.avgCorr = statsCorr.getMean();	
		this.stdCorr = statsCorr.getStandardDeviation();
		
		this.avgManhattan = statsMan.getMean();	
		this.stdManhattan = statsMan.getStandardDeviation();
		
		this.avgEucl = statsEucl.getMean();	
		this.stdEucl = statsEucl.getStandardDeviation();
		
		this.avgChebyshev = statsCheb.getMean();	
		this.stdChebyshev = statsCheb.getStandardDeviation();

		this.avgL0 = statsL0.getMean();	
		this.stdL0 = statsL0.getStandardDeviation();

		this.avgEuclCum = statsEuclCum.getMean();	
		this.stdEuclCum = statsEuclCum.getStandardDeviation();
	}	

}
