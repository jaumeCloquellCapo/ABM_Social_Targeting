package controller;

import java.util.ArrayList;

import model.*;

/**
 * Controller
 *
 * This class is the wrapper to call the model without using the GUI. It calls
 * the model, run it to get all the steps and returns a list of simulated values
 *
 * @author mchica
 *
 */
public class Controller {

    protected long maxSteps;

    protected long seed;

    protected Model model = null;

    protected ArrayList<Integer> newPremiumsArray;
    protected int[][] newPurchasesArray;

    /**
     * Clone the object
     */
    /*public Object clone() {
    	Controller m = (Controller)(this.clone());
        m.model = (Model)(model.clone());
        
        return m;        
    }*/
    /**
     * @return the ModelParameters object where all the parameters are defined
     */
    public ModelParameters getModelParameters() {
        return model.getParametersObject();
    }

    /**
     * @return the ratio of agents and pop
     */
    public int getRatioAgentsPop() {
        return model.getParametersObject().getRatioAgentsPop();
    }

    /**
     * @return the maxSteps
     */
    public long getMaxSteps() {
        return maxSteps;
    }

    /**
     * sets the maxSteps
     */
    public void setMaxSteps(long maxSteps) {
        this.maxSteps = maxSteps;
    }

    /**
     * Constructor setting config and seed
     */
    public Controller(String _config, long _seed) {

        seed = _seed;

        Model.setConfigFileName(_config);

        //PropertyConfigurator.configure(Model.getLogFileName());
        this.model = new Model(seed);

    }

    /**
     * Constructor having config, seed and maxSteps
     */
    public Controller(String _config, long _seed, long _maxSteps) {
        seed = _seed;

        this.maxSteps = _maxSteps;

        Model.setConfigFileName(_config);

        //PropertyConfigurator.configure(Model.getLogFileName());
        this.model = new Model(seed);

    }

    /**
     * Run the model one time
     */
    public void runModel() {
        long time1 = 0, time2 = 0;

        // starting and looping the mode
        if (CalibrationController.DEBUG_CALIB) {
            time1 = System.currentTimeMillis();
        }

        model.start();

        if (CalibrationController.DEBUG_CALIB) {
            time2 = System.currentTimeMillis();
            System.out.println((double) (time2 - time1) / 1000 + "s for starting model");
        }

        ArrayList<Integer> results = new ArrayList<Integer>();
        int tmpArray[];

        int tmpBrandArray[][];

        if (CalibrationController.DEBUG_CALIB) {
            time1 = System.currentTimeMillis();
        }

        try {

            do {

                if (!model.schedule.step(model)) {
                    break;
                }

            } while (model.schedule.getSteps() < maxSteps);

            // Todo: remove it
            tmpArray = model.getNewPremiumsArray();

            for (int i = 0; i < model.schedule.getSteps(); i++) {
                results.add(tmpArray[i]);
            }

            this.newPurchasesArray = new int[model.getBrands().length][(int) model.schedule.getSteps()];

            // int[][] results2 = new [model.getBrands().length][model.schedule.getSteps()];
            this.newPurchasesArray = model.getNewPurchasesOfEveryBrand();

//            for (int i = 0; i < model.getBrands().length; i++) {
//                for (int j = 0; j < model.schedule.getSteps(); j++) {
//                    ArrayList<Integer> aux = new ArrayList<>();
//                    aux.add(tmpBrandArray[i][j]);
//                    results2[i][j] = tmpBrandArray[i][j]
//                }
//            }
            model.finish();

        } catch (Exception e) {

            System.err.println("Controller: Error when running model, execution is aborted.\n"
                    + e.getMessage());
        }

        if (CalibrationController.DEBUG_CALIB) {
            time2 = System.currentTimeMillis();
            System.out.println((double) (time2 - time1) / 1000 + "s for running the model");
        }
        this.newPremiumsArray = results;
        // this.newPurchasesArray = tmpBrandArray;

    }

    public ArrayList<Integer> getNewPremiumsArray() {

        return this.newPremiumsArray;
    }

    public int[][] getNewPuchasesOfEveryBrand() {

        return this.newPurchasesArray;
    }

}
