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

    int repetition_strategy_Agents[];
    int deliberation_strategy_Agents[];
    int imitation_strategy_Agents[];
    int social_strategy_Agents[];
    int utility_strategy_Agents[];
    int strategyChanges[];

    public int[] getRepetition_strategy_Agents() {
        return repetition_strategy_Agents;
    }

    public int[] getDeliberation_strategy_Agents() {
        return deliberation_strategy_Agents;
    }

    public int[] getImitation_strategy_Agents() {
        return imitation_strategy_Agents;
    }

    public int[] getSocial_strategy_Agents() {
        return social_strategy_Agents;
    }

    public int[] getUtility_strategy_Agents() {
        return utility_strategy_Agents;
    }

    /**
     * Clone the object
     */
    /*public Object clone() {
    Controller m = (Controller)(this.clone());
    m.model = (Model)(model.clone());
    return m;        
    }*/
    public int[] getStrategyChanges() {
        return strategyChanges;
    }

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

        if (CalibrationController.DEBUG_CALIB) {
            time1 = System.currentTimeMillis();
        }

//        try {

            do {

                if (!model.schedule.step(model)) {
                    break;
                }
                
            } while (model.schedule.getSteps() < maxSteps);

            this.newPurchasesArray = new int[getModelParameters().getBrands()][(int) maxSteps];

            this.repetition_strategy_Agents = new int[(int) maxSteps];
            this.deliberation_strategy_Agents = new int[(int) maxSteps];
            this.imitation_strategy_Agents = new int[(int) maxSteps];
            this.social_strategy_Agents = new int[(int) maxSteps];
            this.utility_strategy_Agents = new int[(int) maxSteps];
            this.strategyChanges = new int[(int) maxSteps];
            
            for (int i = 0; i < maxSteps; i++) {
                for (int j = 0; j < getModelParameters().getBrands(); j++) {
                    this.newPurchasesArray[j][i] = model.getNewPurchasesForBrandAtStep(i, j);

                }

                this.repetition_strategy_Agents[i] = model.getRepetition_strategy_Agents()[i];
                this.deliberation_strategy_Agents[i] = model.getDeliberation_strategy_Agents()[i];
                this.imitation_strategy_Agents[i] = model.getImitation_strategy_Agents()[i];
                this.social_strategy_Agents[i] = model.getSocial_strategy_Agents()[i];
                this.utility_strategy_Agents[i] = model.getUtility_strategy_Agents()[i];
                this.strategyChanges[i] = model.getStrategyChanges()[i];

            }

            model.finish();

//        } catch (Exception e) {
//
//            System.err.println("Controller: Error when running model, execution is aborted.\n"
//                    + e.getMessage());
//        }

        if (CalibrationController.DEBUG_CALIB) {
            time2 = System.currentTimeMillis();
            System.out.println((double) (time2 - time1) / 1000 + "s for running the model");
        }

    }

    public ArrayList<Integer> getNewPremiumsArray() {

        return this.newPremiumsArray;
    }

    public int[][] getNewPuchasesOfEveryBrand() {

        return this.newPurchasesArray;
    }
}
