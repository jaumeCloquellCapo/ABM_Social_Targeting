package view;

import java.io.IOException;
import calibration.HistoricalData;
import controller.Controller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.text.SimpleDateFormat;

//----------------------------- MAIN FUNCTION -----------------------------//
/**
 * Main function.
 *
 * @param args
 */
public class ConsoleSimulation {

    public static void main(String[] args) throws FileNotFoundException {

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

            controller = new Controller(fileName, seed, maxSteps);

            RunStats stats = new RunStats(NRUNS, (int) maxSteps, controller.getModelParameters().getBrands());

            for (int i = 0; i < NRUNS; i++) {
                controller.runModel();
                //int[][] compras = controller.getNewPuchasesOfEveryBrand();   
//                for (int j = 0; j < controller.getModelParameters().getBrands(); j++) {
//                    stats.setDataPurchases(i, j, controller.getNewPuchasesOfEveryBrand()[j]);
//
//                }
//                stats.setDeliberation_strategy_Agents(i, controller.getDeliberation_strategy_Agents());
//                stats.setImitation_strategy_Agents(i, controller.getImitation_strategy_Agents());
//                stats.setRepetition_strategy_Agents(i, controller.getRepetition_strategy_Agents());
//                stats.setSocial_strategy_Agents(i, controller.getSocial_strategy_Agents());
//                stats.setUtility_strategy_Agents(i, controller.getUtility_strategy_Agents());

//                for (int j = 0; j < results2.length; j++) {;
//                    stats.setDataPurchases(i, j, results2[j]);
//                    //stats.setStrategyChanges(i, j, results2[j]);
//                }
            }

            stats.calcAllStats();

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

//                System.out.print(">> Purchases obtained at every step by the macro-level simulation:\n");
//                System.out.println();
//                for (int k = 0; k < NRUNS; k++) {
//                    for (int j = 0; j < maxSteps; j++) {
//                        System.out.println("** NRUNS " + k);
//                        System.out.println("**** Step  " + j);
//                        for (int i = 0; i < controller.getModelParameters().getBrands(); i++) {
//                            System.out.println("****** Brand " + i + ": " + stats.getData()[k][i][j]);
//                            //System.out.println();
//                        }
//                        System.out.println();
//                        System.out.println();
//
//                    }
//                }
                String outputFile = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());

                File fileAllMC = new File("./logs/" + "AllMCruns_" + outputFile + ".csv");
                File fileSummaryMC = new File("./logs/" + "SummaryMCruns_" + outputFile + ".csv");
                //File fileAllMCLQ = new File("./logs/" + "AllMCrunsLQ_" + outputFile + ".txt");
                File fileSummaryMCLQ = new File("./logs/" + "SummaryDeliberatio" + outputFile + ".csv");
                File fileTimeSeriesMC = new File("./logs/" + "TimeSeriesMCruns_" + outputFile + ".csv");
                PrintWriter printWriter;

                //PrintWriter out = new PrintWriter(System.out, true);
                printWriter = new PrintWriter(fileAllMC);
                stats.printAllStats(printWriter, true);
                
                printWriter = new PrintWriter(fileSummaryMC);
                stats.printSummaryStats(printWriter, true);
                
                printWriter = new PrintWriter(fileSummaryMCLQ);
                stats.printSummaryDeliberation(printWriter, true);

                printWriter = new PrintWriter(fileTimeSeriesMC);
                stats.printAllDeliberation(printWriter, true);

            }

        } else {

            System.out.println(">> Error with parameters when calling the sofware. Please check README file...\n");
            System.out.println("Try: -noHist -configFile XXX -seed Y -runs R -until Z");
            System.out.println("Or: -hist -configFile XXX -seed Y -runs R -historicalFile ZZZ");

        }

    }

}
