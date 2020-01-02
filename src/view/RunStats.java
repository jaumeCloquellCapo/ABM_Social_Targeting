/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.PrintWriter;
import java.util.Arrays;
import model.Model;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author jaime
 */
public class RunStats {

    private int runs;				// number of MC simulations
    private int steps;			// number of steps simulation
    private int[][][] data_purchases;			// runs / brands / step

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
    public void setRepetition_strategy_Agents(int _run, int _step, int _data) {
        this.repetition_strategy_Agents[_run][_step] = _data;
    }

    public void setDeliberation_strategy_Agents(int _run, int _step, int _data) {
        this.deliberation_strategy_Agents[_run][_step] = _data;
    }

    public void setImitation_strategy_Agents(int _run, int _step, int _data) {
        this.imitation_strategy_Agents[_run][_step] = _data;
    }

    public void setSocial_strategy_Agents(int _run, int _step, int _data) {
        this.social_strategy_Agents[_run][_step] = _data;
    }

    public void setUtility_strategy_Agents(int _run, int _step, int _data) {
        this.utility_strategy_Agents[_run][_step] = _data;
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

    public RunStats(int _runs, int _steps, int _brands) {

        this.runs = _runs;
        this.steps = _steps;
        this.brands = _brands;

        this.data_purchases = new int[this.runs][this.brands][this.steps];
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
            //DescriptiveStatistics purchasesBrands = new DescriptiveStatistics();
            // DescriptiveStatistics strategies = new DescriptiveStatistics();
            for (int j = 0; j < this.steps; j++) {
                DescriptiveStatistics purchasesBrands = new DescriptiveStatistics();
                for (int i = 0; i < this.runs; i++) {
                    purchasesBrands.addValue(this.data_purchases[i][b][j]);
                    // strategies.addValue(this.strategies[i][b][j]);
                }

                this.avg_purchases[b][j] = purchasesBrands.getMean();
                this.std_purchases[b][j] = purchasesBrands.getStandardDeviation();
                this.min_purchases[b][j] = purchasesBrands.getMin();
                this.max_purchases[b][j] = purchasesBrands.getMax();
            }
        }

        for (int i = 0; i < this.steps; i++) {

            DescriptiveStatistics del = new DescriptiveStatistics();
            DescriptiveStatistics imi = new DescriptiveStatistics();
            DescriptiveStatistics rep = new DescriptiveStatistics();
            DescriptiveStatistics so = new DescriptiveStatistics();
            DescriptiveStatistics ut = new DescriptiveStatistics();

            for (int j = 0; j < this.runs; j++) {
                del.addValue(this.deliberation_strategy_Agents[j][i]);
                imi.addValue(this.imitation_strategy_Agents[j][i]);
                rep.addValue(this.repetition_strategy_Agents[j][i]);
                so.addValue(this.social_strategy_Agents[j][i]);
                ut.addValue(this.utility_strategy_Agents[j][i]);
                // strategies.addValue(this.strategies[i][b][j]);
            }

            this.avg_deliberation[i] = del.getMean();
            this.avg_imitation[i] = imi.getMean();
            this.avg_repetition[i] = rep.getMean();
            this.avg_social[i] = so.getMean();
            this.avg_utility[i] = ut.getMean();

            this.std_deliberation[i] = del.getStandardDeviation();
            this.std_imitation[i] = imi.getStandardDeviation();
            this.std_repetition[i] = rep.getStandardDeviation();
            this.std_social[i] = so.getStandardDeviation();
            this.std_utility[i] = ut.getStandardDeviation();

            this.min_deliberation[i] = del.getMin();
            this.min_imitation[i] = imi.getMin();
            this.min_repetition[i] = rep.getMin();
            this.min_social[i] = so.getMin();
            this.min_utility[i] = ut.getMin();

            this.max_deliberation[i] = del.getMax();
            this.max_imitation[i] = imi.getMax();
            this.max_repetition[i] = rep.getMax();
            this.max_social[i] = so.getMax();
            this.max_utility[i] = ut.getMax();

        }

    }

    public void printAllStats(PrintWriter writer, boolean append) {

        StringBuffer csvHeader = new StringBuffer("");
        StringBuffer csvData = new StringBuffer("");
        csvHeader.append("Run;step;brand;purchase\n");
        // write header
        writer.write(csvHeader.toString());

        for (int i = 0; i < this.runs; i++) {
            for (int b = 0; b < this.brands; b++) {
                for (int j = 0; j < this.steps; j++) {
                    csvData.append(i);
                    csvData.append(';');
                    csvData.append(j);
                    csvData.append(';');
                    csvData.append(b);
                    csvData.append(';');
                    csvData.append(String.format("%03d", this.data_purchases[i][b][j]));
                    csvData.append('\n');

                }
            }
        }
        writer.write(csvData.toString());
        writer.close();
    }

    public void printAllDeliberation(PrintWriter writer, boolean append) {

        StringBuffer csvHeader = new StringBuffer("");
        StringBuffer csvData = new StringBuffer("");
        csvHeader.append("Run;step;repetition;deliberation;imitation;social\n");
        // write header
        writer.write(csvHeader.toString());

        for (int i = 0; i < this.runs; i++) {
            for (int j = 0; j < this.steps; j++) {
                csvData.append(i);
                csvData.append(';');
                csvData.append(j);
                csvData.append(';');
                csvData.append(this.repetition_strategy_Agents[i][j]);
                csvData.append(';');
                csvData.append(this.deliberation_strategy_Agents[i][j]);
                csvData.append(';');
                csvData.append(this.imitation_strategy_Agents[i][j]);
                csvData.append(';');
                csvData.append(this.social_strategy_Agents[i][j]);
//                csvData.append(';');
//                csvData.append(this.utility_strategy_Agents[i][j]);
                csvData.append('\n');

            }
        }
        writer.write(csvData.toString());
        writer.close();
    }

    public void printSummaryStats(PrintWriter writer, boolean append) {

        StringBuffer csvHeader = new StringBuffer("");
        StringBuffer csvData = new StringBuffer("");
        csvHeader.append("Step");

        for (int b = 0; b < this.brands; b++) {
            csvHeader.append(';');
            csvHeader.append("brand_" + b);
            csvHeader.append(';');
            csvHeader.append("avg_purchases_" + b);
            csvHeader.append(';');
            csvHeader.append("std" + b);
            csvHeader.append(';');
            csvHeader.append("min" + b);
            csvHeader.append(';');
            csvHeader.append("max" + b);
            csvData.append('\n');
        }

        // write header
        writer.write(csvHeader.toString());

        for (int i = 0; i < this.steps; i++) {
            csvData.append(i);
            for (int b = 0; b < this.brands; b++) {

                csvData.append(';');
                csvData.append(b);
                csvData.append(';');
                csvData.append(this.avg_purchases[b][i]);
                csvData.append(';');
                csvData.append(String.format("%.4f", this.std_purchases[b][i]));
                csvData.append(';');
                csvData.append(String.format("%.4f", this.min_purchases[b][i]));
                csvData.append(';');
                csvData.append(String.format("%.4f", this.max_purchases[b][i]));

            }
            csvData.append('\n');
        }
        writer.write(csvData.toString());
        writer.close();
    }

    public void printSummaryDeliberation(PrintWriter writer, boolean append) {

        StringBuffer csvHeader = new StringBuffer("");
        StringBuffer csvData = new StringBuffer("");
        csvHeader.append("Step;avg_deliberation;avg_imitation;avg_repetition;avg_social;std_deliberation;std_imitation;std_repetition;std_social\n");
        // write header
        writer.write(csvHeader.toString());

        for (int i = 0; i < this.steps; i++) {
            csvData.append(i);
            csvData.append(';');
            csvData.append(this.avg_deliberation[i]);
            csvData.append(';');
            csvData.append(this.avg_imitation[i]);
            csvData.append(';');
            csvData.append(this.avg_repetition[i]);
            csvData.append(';');
            csvData.append(this.avg_social[i]);
//            csvData.append(';');
//            csvData.append(this.avg_utility[i]);
            csvData.append(';');
            csvData.append(String.format("%.4f", this.std_deliberation[i]));
            csvData.append(';');
            csvData.append(String.format("%.4f", this.std_imitation[i]));
            csvData.append(';');
            csvData.append(String.format("%.4f", this.std_repetition[i]));
            csvData.append(';');
            csvData.append(String.format("%.4f", this.std_social[i]));
//            csvData.append(';');
//            csvData.append(String.format("%.4f", this.std_utility[i]));
            csvData.append('\n');
            //}
        }
        writer.write(csvData.toString());
        writer.close();
    }

    public void printTotalResults(PrintWriter writer, boolean append) {

        StringBuffer csvHeader = new StringBuffer("");
        StringBuffer csvData = new StringBuffer("");
        csvHeader.append("brand;total\n");
        // write header
        writer.write(csvHeader.toString());

        for (int b = 0; b < this.brands; b++) {

            csvData.append(b);
            csvData.append(';');
            csvData.append(Arrays.stream(this.avg_purchases[b]).sum());
            csvData.append('\n');

        }
        writer.write(csvData.toString());
        writer.close();
    }

    public void printTotalDeliberations(PrintWriter writer, boolean append) {

        StringBuffer csvHeader = new StringBuffer("");
        StringBuffer csvData = new StringBuffer("");
        csvHeader.append("type;total\n");
        // write header
        writer.write(csvHeader.toString());

        // for (int b = 0; b < this.steps; b++) {
        csvData.append("DELIBERATION");
        csvData.append(';');
        csvData.append(Arrays.stream(this.avg_deliberation).sum());
        csvData.append('\n');
        csvData.append("Model.IMITATION");
        csvData.append(';');
        csvData.append(Arrays.stream(this.avg_imitation).sum());
        csvData.append('\n');
        csvData.append("REPETITION");
        csvData.append(';');
        csvData.append(Arrays.stream(this.avg_repetition).sum());
        csvData.append('\n');
        csvData.append("SOCIALCOMPARISION");
        csvData.append(';');
        csvData.append(Arrays.stream(this.avg_social).sum());
//        csvData.append('\n');
//        csvData.append(Model.UTILITY);
//        csvData.append(';');
//        csvData.append(Arrays.stream(this.avg_utility).sum());
//        csvData.append('\n');

        // }
        writer.write(csvData.toString());
        writer.close();
    }

}
