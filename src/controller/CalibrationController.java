package controller;

import java.io.IOException;
import java.util.ArrayList;

import calibration.CalibrationStats;
import calibration.HistoricalData;
import calibration.Parameters2Calib;

/**
 * CalibrationController
 *
 * This class is the controller when calibrating. It is in charge of running the
 * model n-times, load historical data and compute stats
 *
 * @see Controller
 *
 * @author mchica
 *
 */
public class CalibrationController extends Controller {

    public static boolean DEBUG_CALIB = false;

    int numberOfRuns;							// number of runs for the algorithm

    HistoricalData data;						// object to allocate historical data

    //ErrorCalculator calculator;				// object for measuring metrics when comparing data
    Parameters2Calib parameters;				// object for allocating input calib parameters

    //CalibrationStats stats;						// to store stats about the metrics
    // ########################################################################	
    // Methods/Functions 	
    // ########################################################################
    /**
     * Clone the object
     */
    /*public Object clone() {
    	

    	CalibrationController m = (CalibrationController)(super.clone());
        m.data = (HistoricalData)(data.clone());
        m.parameters = (Parameters2Calib)(parameters.clone());
        
        return m;  
    }*/
    //--------------------------- Constructor and public methods ---------------------------//
    /**
     * Constructor for the calibration controller
     */
    public CalibrationController(String dataFile, String _config, long _seed,
            Parameters2Calib _parameters, int _numberOfRuns)
            throws IOException {

        super(_config, _seed);

        // load historical data
        data = new HistoricalData(dataFile);

        // set the max. number of steps of the simulation from data
        this.maxSteps = data.getNumSteps();

        //calculator = new ErrorCalculator();		
        parameters = _parameters;

        this.numberOfRuns = _numberOfRuns;

        // stats creation
        //stats = new CalibrationStats(model.ratioAgentsPop, 
        //		this.numberOfRuns, this.maxSteps);
    }

    /**
     * It returns the object with the calibration parameters
     *
     * @return the object with the parameters
     */
    public Parameters2Calib getParametersFromCalib() {
        return parameters;
    }

    /**
     * It returns the ratio of agents-pop
     *
     * @return the ratio
     */
    public int getRatioAgents() {
        return model.getParametersObject().getRatioAgentsPop();
    }

    /**
     * Sets the object with the historical data. It also updates the number of
     * maximum steps needed
     *
     * @param _hist the object with the data
     */
    public void setHistoricalData(HistoricalData _hist) {
        data = _hist;
        this.maxSteps = data.getNumSteps();
    }

    /**
     * It returns the object with the historical data
     *
     * @return the object with the data
     */
    public HistoricalData getHistoricalData() {
        return data;
    }

    /**
     * To run the model n times, compare with
     */
    public void runModelWithCalib(CalibrationStats stats) {

        // first we change the input parameters of the model
        parameters.refreshModelWithParams(model);

        // set the defined seed for all the runs of this evaluation
        this.model.setSeed(this.model.seed());

        // when changed, we will run the model the needed times and compute stats
        // every time
        ArrayList<Integer> results;

        double simulated[][];
        simulated = new double[this.numberOfRuns][(int) this.maxSteps];

        for (int i = 0; i < this.numberOfRuns; i++) {

            //this.model.setSeed(1);   // JUST FOR CHECKING PURPOSES (ALL RUNS =)
            // get the results
            runModel();
            results = getNewPremiumsArray();

            if (DEBUG_CALIB) {
                System.out.print("Run " + i);
                System.out.println();
            }

            // convert from arrayList integer to double array
            for (int j = 0; j < results.size(); j++) {
                simulated[i][j] = results.get(j);

                if (DEBUG_CALIB) {
                    System.out.print("Step " + j + ": SIM: "
                            + results.get(j) * model.getParametersObject().getRatioAgentsPop()
                            + "; HIST: " + data.getHistoricalDataArray(j));

                    System.out.println();
                }
            }
        }

        // when all the runs are finished and data saved we calculate stats
        stats.setSimulated(simulated);
        stats.calcAllStats(data.getHistoricalDataArray());

        //return stats;
    }

}
