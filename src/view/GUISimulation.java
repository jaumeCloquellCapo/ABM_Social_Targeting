package view;

import java.io.IOException;

import sim.engine.*;
import sim.display.*;

import javax.swing.*;

import model.Model;
import model.GamerAgent;

import org.jfree.data.xy.*;

import calibration.CalibrationStats;
import calibration.HistoricalData;
import sim.portrayal.*;
import sim.util.Bag;
import sim.util.media.chart.*;
import util.Colors;

/**
 * A GUI class, draws also a time serie chart. The code is from MASON
 * "howto.html" -> "How to Add a Chart Programmatically"
 *
 * @author mchica
 *
 */
public class GUISimulation extends GUIState {

    // ########################################################################
    // Variables
    // ######################################################################## 
    public static String configFileName;
    public static boolean showSocialNetwork;

    // values for the state of the agents
    public static int PREMIUM_COLOR = 2;
    public static int BASIC_COLOR = 3;

    // Provide colors for charts and graphs
    public static Colors colorPalette;

    XYSeries series[];
    TimeSeriesChartGenerator chart;
    public JFrame chartFrame;

    XYSeries series2[];
    TimeSeriesChartGenerator chart2;
    public JFrame chartFrame2;

    public static HistoricalData data;

    // ######################################################################## 	
    // Methods/Functions	
    // ######################################################################## 
    /**
     * Main function for visualization
     *
     * @param args input parameters.
     */
    public static void main(String[] args) {
        // Standard palette (6 - Color-based palette considering colorblind)
        colorPalette = new Colors(6);
        showSocialNetwork = true;

        configFileName = args[0];

        if (args.length > 1 && args[1].compareTo("--noSocialNetwork") == 0) {
            showSocialNetwork = false;
        }

        // setting the properties file with the configuration of the model
        Model.setConfigFileName(GUISimulation.configFileName);

        // Start with seed 1, if not provided
        final long seed = 1;
        GUISimulation vid = new GUISimulation(seed);

        Console c = new Console(vid);
        c.setVisible(true);
        c.setLocation(2500, 620);

        // load historical data
        try {

            data = new HistoricalData("./data/set2/premiums_91days_2012_ALL.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes a new instance of the AnimalGameMarketSimulationWithUI class.
     */
    public GUISimulation(long seed) {
        super(new Model(seed));
    }

    /**
     * Initializes a new instance of the AnimalGameSimulationWithUI class.
     *
     * @param state - a simulation model object (SimState).
     */
    public GUISimulation(SimState state) {
        super(state);
    }

    /**
     * Generates a string with the name of the model.
     *
     * @return - a string with the name of the model
     */
    public static String getName() {
        return "GUISimulation";
    }

    //------------------ Inspector of the simulation model ------------------//
    /**
     * Gets the simulation model to provide model features by Inspector.
     *
     * @param state - a simulation model object (SimState).
     */
    public Object getSimulationInspectedObject() {
        return state;
    }

    /**
     * Gets the Inspector customized to display model features.
     *
     * @return - a volatile Inspector (can be updated every iteration).
     */
    public Inspector getInspector() {
        Inspector i = super.getInspector();
        i.setVolatile(true);
        return i;
    }

    //--------------------------- GUIState methods --------------------------//
    /**
     * Initializes the GUIState with the controller at the start of the GUI.
     *
     * @param - controller which starts, stops, and manipulates the schedule
     */
    public void init(Controller c) {
        super.init(c);

        chart = new TimeSeriesChartGenerator();
        chart.setTitle("Total Subscriptions evolution");
        chart.setYAxisLabel("# Purchases");
        chart.setXAxisLabel("Time");

        chartFrame = chart.createFrame();
        chartFrame.setLocation(1000, 0);
        chartFrame.setVisible(true);
        chartFrame.pack();
        c.registerFrame(chartFrame);

        chart2 = new TimeSeriesChartGenerator();
        chart2.setTitle("New Subscriptions and historical data");
        chart2.setYAxisLabel("# Purchases");
        chart2.setXAxisLabel("Time");

        chartFrame2 = chart2.createFrame();
        chartFrame2.setLocation(1000, 500);
        chartFrame2.setVisible(true);
        chartFrame2.pack();
        c.registerFrame(chartFrame2);

    }

    /**
     * Starts the simulation.
     */
    public void start() {
        // setting the properties file with the configuration of the model
        Model.setConfigFileName(GUISimulation.configFileName);

        super.start();

        chart.removeAllSeries();
        chart2.removeAllSeries();

        series = new XYSeries[Model.getParametersObject().getBrands()];    // cumulative

        series2 = new XYSeries[Model.getParametersObject().getBrands()];   // new and historical data

        if (showSocialNetwork) {
            // show the social network
            Model model = (Model) state;
            //model.getSocialNetwork().getGraph().display();		
            model.getSocialNetwork().getGraph().display().getDefaultView().setLocation(1000, 0);
        }

        // Initialize time serie graph
        Colors col = (Colors) GUISimulation.colorPalette;
        for (int i = 0; i < series.length; i++) {
            XYSeries SeriesTmp = new XYSeries("xxx", false);
            series[i] = SeriesTmp;
            TimeSeriesAttributes TSAttributes
                    = (TimeSeriesAttributes) chart.addSeries(series[i], null);
            TSAttributes.setStrokeColor(col.getPaletteColor(i * 2));
        }

        for (int i = 0; i < series2.length; i++) {
            XYSeries SeriesTmp = new XYSeries(Model.getParametersObject().getBrandNames(i), false);
            series2[i] = SeriesTmp;
            TimeSeriesAttributes TSAttributes
                    = (TimeSeriesAttributes) chart2.addSeries(series2[i], null);
            TSAttributes.setStrokeColor(col.getPaletteColor(i * 2));
        }

        // Schedules a Steppable to be stepped immediately after 
        // every iteration of the model (evolution chart)
        scheduleRepeatingImmediatelyAfter(new Steppable() {

            private static final long serialVersionUID = 3571999823946192125L;

            public void step(SimState state) {

                Model model = (Model) state;

                if (showSocialNetwork) {
                    // first, re-paint the nodes of the graph stream
                    Bag agents = model.getAgents();
                    for (int i = 0; i < agents.size(); i++) {

                        GamerAgent gamer = ((GamerAgent) agents.get(i));

                        if (gamer.getSubscriptionState() == Model.BASIC_USER) {
                            model.getSocialNetwork().setNodeColor(gamer.getGamerAgentId(),
                                    GUISimulation.BASIC_COLOR, GUISimulation.colorPalette);
                        } else {
                            model.getSocialNetwork().setNodeColor(gamer.getGamerAgentId(),
                                    GUISimulation.PREMIUM_COLOR, GUISimulation.colorPalette);
                        }
                    }
                }

                // now we plot the graph XY
                int x[], y1[];
                double y2[];
                x = new int[1];

                y1 = new int[Model.getParametersObject().getBrands()];

                y2 = new double[Model.getParametersObject().getBrands()];

                x[0] = (int) model.schedule.getSteps() - 1;
               

                for (int i = 0; i < Model.getParametersObject().getBrands(); i++) {
                    y1[i] = model.getCumPurchasesToBrandAtStep(x[0], i);

                }

                for (int i = 0; i < Model.getParametersObject().getBrands(); i++) {
                    y2[i] = model.getNewPurchasesForBrandAtStep(x[0], i) * model.getParametersObject().getRatioAgentsPop();

                }

                // eliminado por jaume
                // if (x[0] < data.getNumSteps()) {
                //     y2[1] = data.getHistoricalDataArray(x[0]);
                //}
                // now add the data
                if (x[0] >= Schedule.EPOCH && x[0] < Schedule.AFTER_SIMULATION) {

                    for (int i = 0; i < series.length; i++) {
                        series[i].add(x[0], y1[i], true);
                    }

                    for (int i = 0; i < series2.length; i++) {
                        series2[i].add(x[0], y2[i], true);
                    }

                    chart.updateChartLater(state.schedule.getSteps());
                }
            }
        });
    }

    /**
     * Ends the simulation.
     */
    public void finish() {
        super.finish();

        chart.update(state.schedule.getSteps(), true);
        chart.repaint();
        chart.stopMovie();

        chart2.update(state.schedule.getSteps(), true);
        chart2.repaint();
        chart2.stopMovie();

//        if (data != null) {
//
//            // calculate error performance if historical data available
//            Model model = (Model) state;
//            CalibrationStats stats = new CalibrationStats(model.getParametersObject().getRatioAgentsPop(),
//                    1, data.getNumSteps());
//
//            double simulated[][] = new double[1][data.getNumSteps()];
//            for (int i = 0; i < data.getNumSteps(); i++) {
//                simulated[0][i] = (double) model.getNewPremiumsAtStep(i);
//            }
//
//            stats.setSimulated(simulated);
//
//            stats.calcAllStats(data.getHistoricalDataArray());
//
//            System.out.println("corr: " + stats.getAvgCorr());
//            System.out.println("eucl: " + stats.getAvgEucl());
//            System.out.println("L0: " + stats.getAvgL0());
//        }
    }

    /**
     * Shuts down the GUIState in preparation for quitting the GUI.
     */
    public void quit() {
        super.quit();

        chart.update(state.schedule.getSteps(), true);
        chart.repaint();
        chart.stopMovie();

        chart2.update(state.schedule.getSteps(), true);
        chart2.repaint();
        chart2.stopMovie();

        if (chartFrame != null) {
            chartFrame.dispose();
        }

        if (chartFrame2 != null) {
            chartFrame2.dispose();
        }

        chartFrame = null;
        chartFrame2 = null;
    }

}
