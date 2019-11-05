package view;

import java.io.IOException;
import java.util.ArrayList;

import calibration.HistoricalData;
import controller.Controller;

//----------------------------- MAIN FUNCTION -----------------------------//
/**
 * Main function.
 *
 * @param args
 */
public class ConsoleSimulation {

    public static void main(String[] args) {

        System.out.println("Agent-based Mk DSS running software.\n******************* "
                + "\nLinked to paper 'Building Agent-Based Decision Support Systems \nfor "
                + "Word-Of-Mouth Programs. A Freemium Application'. \nCo-authored by M. Chica and W. Rand "
                + "and published in the \nJournal of Marketing Research, 2016.\n\n"
                + "Please check README.txt for more running details.\n*******************\n\n");

        if (("-SA".compareTo(args[0]) == 0)) {

            // RUNNING SA 
            SensitivityAnalysis.run(args);

        } else if (args.length == 9) {

            Controller controller = null;

            long seed = Long.parseLong(args[4]);
            String fileName = args[2];

            int NRUNS = Integer.parseInt(args[6]);

            HistoricalData data = null;
            long maxSteps = 0;

            if ("-hist".compareTo(args[0]) == 0) {

                //{"-hist -configFile", "xxx", "-seed", "1" , 
                // "-runs", "1" "-historicalFile", "xxx", };
                // historical data
                String dataFile = args[8];

                System.out.println(">> Setting seed to " + seed
                        + "; " + NRUNS + " number of runs"
                        + "; \nhistorical data file provided by " + dataFile
                        + " ; \nand config file " + fileName
                        + "\n\nRunning Agent-based DSS. Please wait...");

                // load historical data
                try {

                    data = new HistoricalData(dataFile);

                    maxSteps = data.getNumSteps();

                    //controller = new Controller (fileName, seed, data.getNumSteps());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {

                // no historical data
                //{"-noHist -configFile", "xxx", "-seed", "1", "-until", "200"};
                maxSteps = Long.parseLong(args[8]);

                System.out.println(">> Setting seed to " + seed
                        + "; " + NRUNS + " number of runs"
                        + "; \nfor a number of " + maxSteps + " days"
                        + " ; \nand config file " + fileName
                        + "\n\nRunning Agent-based DSS. Please wait...");

            }

//            double simulated[][];
//            simulated = new double[NRUNS][(int) maxSteps];
            controller = new Controller(fileName, seed, maxSteps);

            int simulated2[][][] = new int[NRUNS][controller.getModelParameters().getBrands()][(int) maxSteps];

            for (int i = 0; i < NRUNS; i++) {
                controller.runModel();

                ArrayList<Integer> results = controller.getNewPremiumsArray();

                int[][] results2 = controller.getNewPuchasesOfEveryBrand(); // nrums - brand - step

                for (int j = 0; j < controller.getNewPuchasesOfEveryBrand().length; j++) {;
                    simulated2[i][j] = results2[j];
                }
                // System.out.println("simulated2.size() : " + simulated2.size());

                // convert from arrayList integer to double array
//                for (int j = 0; j < results.size(); j++) {
//                    simulated[i][j] = results.get(j);
//                }
            }

            System.out.print("\nFinished!! Agent-based DSS Simulation ended successfully. "
                    + "\nCheck macro-level simulation output for new premium adoptions:"
                    + "\n---------------\n");

            System.out.println();

            if ("-hist".compareTo(args[0]) == 0) {

//                CalibrationStats stats = new CalibrationStats(controller.getRatioAgentsPop(),
//                        NRUNS, data.getNumSteps());
//
//                stats.setSimulated(simulated);
//
//                stats.calcAllStats(data.getHistoricalDataArray());
//
//                PrintWriter writer = new PrintWriter(System.out);
//                stats.printStats(writer,
//                        data.getHistoricalDataArray());
//
//                writer.flush();
//                writer.close();
            } else {

                System.out.print(">> Purchases obtained at every step by the macro-level simulation:\n");
                System.out.println();
                for (int k = 0; k < NRUNS; k++) {
                    for (int j = 0; j < maxSteps; j++) {
                        System.out.print("\nMacro-level simulation output for day (step) " + j + ";\n");

                        for (int i = 0; i < controller.getModelParameters().getBrands(); i++) {
                            //System.out.print("Monte Carlo run " + k + "; " + simulated[j][i] + " new premiums");
                            System.out.println();

                            System.out.print(" NRUNS " + k + "Step  " + j + " Brand " + i + ": " + simulated2[k][i][j] + " new purchases");
                            System.out.println();
                        }
                        System.out.println();
                        System.out.println();

                    }
                }

            }

        } else {

            System.out.println(">> Error with parameters when calling the sofware. Please check README file...\n");
            System.out.println("Try: -noHist -configFile XXX -seed Y -runs R -until Z");
            System.out.println("Or: -hist -configFile XXX -seed Y -runs R -historicalFile ZZZ");

        }

    }

}
