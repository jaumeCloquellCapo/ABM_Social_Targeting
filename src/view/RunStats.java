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
    private int data[][][];			// runs / brands / step
    private int brands;

    private double avg_purchases[][];			// average k_I over all the runs for each step
    private double stdk_purchases[][];			// std k_I over all the runs for each step
    private double[][] min_purchases;
    private double[][] max_purchases;

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

    public int[][][] getData() {
        return data;
    }

    public void setData(int _run, int _brand, int[] _data) {
        this.data[_run][_brand] = _data;
    }

    public RunStats(int _runs, int _steps, int _brands) {

        this.runs = _runs;
        this.steps = _steps;
        this.brands = _brands;
        this.data = new int[this.runs][this.brands][this.steps];

        this.stdk_purchases = new double[this.brands][this.steps];
        this.avg_purchases = new double[this.brands][this.steps];
        this.min_purchases = new double[this.brands][this.steps];
        this.max_purchases = new double[this.brands][this.steps];
    }

    public void calcAllStats() {

        for (int b = 0; b < this.brands; b++) {
            DescriptiveStatistics purchasesBrands = new DescriptiveStatistics();
            for (int j = 0; j < this.steps; j++) {
                for (int i = 0; i < this.runs; i++) {
                    purchasesBrands.addValue(this.data[i][b][j]);
                }

                this.avg_purchases[b][j] = purchasesBrands.getMean();
                this.stdk_purchases[b][j] = purchasesBrands.getStandardDeviation();
                this.min_purchases[b][j] = purchasesBrands.getMin();
                this.max_purchases[b][j] = purchasesBrands.getMax();
            }
        }

    }

    public void printAllStats(PrintWriter writer, boolean append) {

        for (int i = 0; i < this.runs; i++) {
            for (int b = 0; b < this.brands; b++) {
                for (int j = 0; j < this.steps; j++) {

                    String toPrint = "run;" + i + ";steps;" + j + ";brand;" + b + ";purchaes;"
                            + String.format("%d", this.data[i][b][j]);

                    if (append) {
                        writer.append(toPrint);
                    } else {
                        writer.print(toPrint);
                    }
                }
            }
        }
    }

    public void printSummaryStats(PrintWriter writer, boolean append) {
        for (int b = 0; b < this.brands; b++) {
            for (int i = 0; i < this.steps; i++) {

                String toPrint = "run;" + i + ";brand;" + b + ";avg_purchases;"
                        + String.format("%.4f", this.avg_purchases[b][i]) + ";stdk_purchases;"
                        + String.format("%.4f", this.stdk_purchases[b][i]) + ";min_purchases;"
                        + String.format("%.4f", this.min_purchases[b][i]) + ";max_purchases;"
                        + String.format("%.4f", this.max_purchases[b][i]) + ";";

                if (append) {
                    writer.append(toPrint);
                    writer.append("\n");
                } else {
                    writer.println(toPrint);
                }
                writer.print(toPrint);

            }
        }
    }

}
