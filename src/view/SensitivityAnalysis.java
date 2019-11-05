package view;

import java.io.IOException;
import java.util.ArrayList;

import calibration.CalibrationStats;
import calibration.HistoricalData;
import controller.Controller;

//----------------------------- MAIN FUNCTION -----------------------------//
/**
 * Main function.
 *
 * @param args
 */
public class SensitivityAnalysis {

    public static void run(String[] args) {

        if (args.length == 15 || args.length == 19) {

            Controller controller = null;

            long seed = Long.parseLong(args[4]);
            String fileName = args[2];

            int NRUNS = Integer.parseInt(args[6]);

            HistoricalData data = null;

            /* PARAMETERS
			-configFile ./config/policy_analysis/baseline.properties -seed 1 -runs 5 -historicalFile
			./data/set2/premiums_60days_2012_TRA.csv -numIncreases 3 -increase 0.005 -investment 1 
			
			-configFile ./config/policy_analysis/baselineTst.properties -seed 1 -runs 5 -historicalFile ./data/set2/premiums_31days_2012_TST.csv -numIncreasesWeight 5 
			-increaseWeight 0.005 -investment 1 -numIncreasesInnovation 5 -increaseInnovation 0.0025
             */
            // historical data
            String dataFile = args[8];

            System.out.println(">> Macro and micro-level DSS. Sensitivity analysis for social influence after targeting"
                    + "\n\nSetting seed to " + seed
                    + "; " + NRUNS + " number of runs"
                    + "; \nhistorical data file provided by " + dataFile
                    + " ; \nand config file " + fileName);

            // load historical data
            try {

                data = new HistoricalData(dataFile);

            } catch (IOException e) {
                e.printStackTrace();
            }

            // run sensitivity simulation
            double investmentPerAgent = Double.parseDouble(args[14]);
            double increaseWeight = Double.parseDouble(args[12]);
            int numIncreasesWeight = Integer.parseInt(args[10]);
            double increaseInnovation = -1.;
            int numIncreasesInnovation = -1;
            if (args.length == 19) {
                increaseInnovation = Double.parseDouble(args[18]);
                numIncreasesInnovation = Integer.parseInt(args[16]);
            }

            System.out.println("\nSpecified sensitivity analysis with " + numIncreasesWeight
                    + " social weight (\\tau) increases\nand " + numIncreasesInnovation + " external influence coefficient"
                    + " increases. \nAlso, rewarding " + investmentPerAgent + "$ of investment for each agent"
                    + "\n\nRunning Agent-based DSS. Please wait...\n");

            double cumSimulatedBaseline[] = new double[NRUNS];

            for (int l = 0; l < numIncreasesInnovation; l++) {

                for (int k = 0; k < numIncreasesWeight; k++) {

                    // we create the controller for each increase of the sensitivity analysis
                    controller = new Controller(fileName, seed, data.getNumSteps());

                    // calculate the weight of social influence
                    double weight = 1. + (k * increaseWeight * investmentPerAgent);
                    (controller.getModelParameters()).setWeight(weight);

                    // if set, set the innovation increase
                    if (increaseInnovation != -1) {
                        double innovation = l * increaseInnovation * investmentPerAgent;
                        (controller.getModelParameters()).
                                setObtainSubscriptionIncrease(innovation);
                    }

                    double simulated[][];
                    double cumSimulated[];

                    simulated = new double[NRUNS][data.getNumSteps()];
                    cumSimulated = new double[NRUNS];

                    for (int i = 0; i < NRUNS; i++) {
                        controller.runModel();
                        ArrayList<Integer> results = controller.getNewPremiumsArray();

                        cumSimulated[i] = 0;

                        // convert from arrayList integer to double array
                        for (int j = 0; j < results.size(); j++) {
                            simulated[i][j] = results.get(j);
                            cumSimulated[i] += (simulated[i][j] * controller.getRatioAgentsPop());
                        }

                        // set the baseline cumulative new premiums of this run
                        if (k == 0) {
                            cumSimulatedBaseline[i] = cumSimulated[i];
                        }

                    }

                    double minCum = 10000000, maxCum = 0, avgCum = 0;
                    // calculate min, max, avg cumulative new users for all the runs
                    for (int i = 0; i < NRUNS; i++) {

                        double diff = cumSimulated[i] - cumSimulatedBaseline[i];

                        if (diff < minCum) {
                            minCum = diff;
                        }
                        if (diff > maxCum) {
                            maxCum = diff;
                        }

                        avgCum += diff;
                    }

                    avgCum /= NRUNS;

                    System.out.println("SocialInfluenceWeightPerDollar;" + (k * increaseWeight)
                            + ";IncreaseExternalInfluenceCoefPerDollar;" + (l * increaseInnovation)
                            + ";Avg_diff_premiums;" + avgCum + ";Min_diff_premiums;" + minCum
                            + ";Max_diff_premiums;" + maxCum);

                    CalibrationStats stats = new CalibrationStats(controller.getRatioAgentsPop(),
                            NRUNS, data.getNumSteps());

                    stats.setSimulated(simulated);

                    stats.calcAllStats(data.getHistoricalDataArray());

                    /*PrintWriter writer = new PrintWriter(System.out);
					stats.printStats(writer, 
							data.getHistoricalDataArray());
					
					writer.flush();*/
                }
            }

            System.out.println("\nFinished!! Micro and macro agent-based DSS simulation ended successfully. "
                    + "\n---------------\n");

        } else {

            System.out.println("Error with the sensitivity analysis parameters:");
            System.out.println(" -configFile XXX "
                    + "-seed Y -runs R -historicalFile ZZZ "
                    + "-numIncreasesWeight 5 -increaseWeight 0.005 -investment 1 "
                    + "[OPTIONAL -numIncreasesExternalInfluence 5 -increaseExternalInfluence 0.0025  ] ");

        }

    }

}
