/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.PrintWriter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author jaime
 */
public class RunStats {

    private int runs;				// number of MC simulations
    private int steps;			// number of steps simulation
    private int[][][] data_purchases;			// runs / brands / step
    private int[][][] data_strategy;			// runs / brands / step
    private int brands;

    private double avg_purchases[][];			// average k_I over all the runs for each step
    private double[][] std_purchases;			// std k_I over all the runs for each step
    private double[][] min_purchases;
    private double[][] max_purchases;

    int repetition_strategy_Agents[][]; 			// a counter of the k_I agents during the simulation
    private double avg_repetition[];
    private double std_repetition[];
    private double min_repetition[];
    private double max_repetition[];

    int deliberation_strategy_Agents[][]; 			// a counter of the k_T agents during the simulation
    private double avg_deliberation[];
    private double std_deliberation[];
    private double min_deliberation[];
    private double max_deliberation[];

    int imitation_strategy_Agents[][];
    private double avg_imitation[];
    private double std_imitation[];
    private double min_imitation[];
    private double max_imitation[];

// a counter of the k_U agents during the simulation
    int social_strategy_Agents[][]; 			// a counter of the k_T agents during the simulation
    private double avg_social[];
    private double std_social[];
    private double min_social[];
    private double max_social[];

    int utility_strategy_Agents[][];			// a counter of the k_U agents during the simulation
    private double avg_utility[];
    private double std_utility[];
    private double min_utility[];
    private double max_utility[];

    // int strategyChanges[];		// array with the total number of evolutionStrategies changes during the simulation

    public void setRepetition_strategy_Agents(int _run,  int[] _data) {
        this.repetition_strategy_Agents[_run] = _data;
    }

    public void setDeliberation_strategy_Agents(int _run,  int[] _data) {
        this.deliberation_strategy_Agents[_run] = _data;
    }

    public void setImitation_strategy_Agents(int _run, int[] _data) {
        this.imitation_strategy_Agents[_run] = _data;
    }

    public void setSocial_strategy_Agents(int _run,  int[] _data) {
        this.social_strategy_Agents[_run] = _data;
    }

    public void setUtility_strategy_Agents(int _run, int[] _data) {
        this.utility_strategy_Agents[_run] = _data;
    }
    
    
    
    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int[][][] getData_purchases() {
        return data_purchases;
    }

    public void setDataPurchases(int _run, int _brand, int[] _data) {
        this.data_purchases[_run][_brand] = _data;
    }

    public void setStrategyChanges(int _run, int _brand, int[] _data) {
        this.data_strategy[_run][_brand] = _data;
    }

    public RunStats(int _runs, int _steps, int _brands) {

        this.runs = _runs;
        this.steps = _steps;
        this.brands = _brands;
        this.data_purchases = new int[this.runs][this.brands][this.steps];
        this.data_strategy = new int[this.runs][this.brands][this.steps];

        this.std_purchases = new double[this.brands][this.steps];
        this.avg_purchases = new double[this.brands][this.steps];
        this.min_purchases = new double[this.brands][this.steps];
        this.max_purchases = new double[this.brands][this.steps];

        this.repetition_strategy_Agents = new int[this.runs][this.steps];
        this.avg_repetition = new double[this.steps];
        this.std_repetition = new double[this.steps];
        this.min_repetition = new double[this.steps];
        this.max_repetition = new double[this.steps];

        this.deliberation_strategy_Agents = new int[this.runs][this.steps];
        this.avg_deliberation = new double[this.steps];
        this.std_deliberation = new double[this.steps];
        this.min_deliberation = new double[this.steps];
        this.max_deliberation = new double[this.steps];

        this.imitation_strategy_Agents = new int[this.runs][this.steps];
        this.avg_imitation = new double[this.steps];
        this.std_imitation = new double[this.steps];
        this.min_imitation = new double[this.steps];
        this.max_imitation = new double[this.steps];

        this.social_strategy_Agents = new int[this.runs][this.steps];
        this.avg_social = new double[this.steps];
        this.std_social = new double[this.steps];
        this.min_social = new double[this.steps];
        this.max_social = new double[this.steps];

        this.utility_strategy_Agents = new int[this.runs][this.steps];
        this.avg_utility = new double[this.steps];
        this.std_utility = new double[this.steps];
        this.min_utility = new double[this.steps];
        this.max_utility = new double[this.steps];
    }

    public void calcAllStats() {

        for (int b = 0; b < this.brands; b++) {
            DescriptiveStatistics purchasesBrands = new DescriptiveStatistics();
            // DescriptiveStatistics strategies = new DescriptiveStatistics();
            for (int j = 0; j < this.steps; j++) {
                for (int i = 0; i < this.runs; i++) {
                    purchasesBrands.addValue(this.data_purchases[i][b][j]);
                    // strategies.addValue(this.strategies[i][b][j]);
                }

                this.avg_purchases[b][j] = purchasesBrands.getMean();
                this.std_purchases[b][j] = purchasesBrands.getStandardDeviation();
                this.min_purchases[b][j] = purchasesBrands.getMin();
                this.max_purchases[b][j] = purchasesBrands.getMax();

//                this.avg_strategy_changes = strategies.getMean();
//                this.std_strategy_changes = strategies.getStandardDeviation();
//                this.min_strategy_changes = strategies.getMin();
//                this.max_strategy_changes = strategies.getMax();
            }
        }

    }

    public void printAllStats(PrintWriter writer, boolean append) {

        StringBuffer csvHeader = new StringBuffer("");
        StringBuffer csvData = new StringBuffer("");
        csvHeader.append("Run,step,brand,purchase\n");
        // write header
        writer.write(csvHeader.toString());

        for (int i = 0; i < this.runs; i++) {
            for (int b = 0; b < this.brands; b++) {
                for (int j = 0; j < this.steps; j++) {
                    csvData.append(i);
                    csvData.append(',');
                    csvData.append(j);
                    csvData.append(',');
                    csvData.append(b);
                    csvData.append(',');
                    csvData.append(String.format("%03d", this.data_purchases[i][b][j]));
                    csvData.append('\n');

                }
            }
        }
        writer.write(csvData.toString());
        writer.close();
    }

    public void printSummaryStats(PrintWriter writer, boolean append) {

        StringBuffer csvHeader = new StringBuffer("");
        StringBuffer csvData = new StringBuffer("");
        csvHeader.append("Step,Brand,avg_purchases,std,min,max\n");
        // write header
        writer.write(csvHeader.toString());

        for (int b = 0; b < this.brands; b++) {
            for (int i = 0; i < this.steps; i++) {
                csvData.append(i);
                csvData.append(',');
                csvData.append(b);
                csvData.append(',');
                csvData.append(String.format("%.4f", this.avg_purchases[b][i]));
                csvData.append(',');
                csvData.append(String.format("%.4f", this.std_purchases[b][i]));
                csvData.append(',');
                csvData.append(String.format("%.4f", this.min_purchases[b][i]));
                csvData.append(',');
                csvData.append(String.format("%.4f", this.max_purchases[b][i]));
                csvData.append('\n');
            }
        }
        writer.write(csvData.toString());
        writer.close();
    }

}
