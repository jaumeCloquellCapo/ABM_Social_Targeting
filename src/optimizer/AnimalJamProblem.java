
package optimizer; 

import ec.*; 
import ec.util.*; 
import ec.vector.*;
import ec.simple.*;
import ec.multiobjective.MultiObjectiveFitness;

import java.io.*;

import calibration.*;
import controller.CalibrationController;


/** 
 * AnimalJamEvaluator
 * 
 * This class launches the controller (AnimalJamModel) for evaluating an 
 * individual. It gets the error measure and assigns it to the individual fitness
 * 
 * @see CalibrationController
 * 
 * @author mchica
 *
 */
public class AnimalJamProblem extends Problem implements SimpleProblemForm {
	
	private static final long serialVersionUID = -621353009165306695L;
	
	public static final String P_DATA_FILE = "data-file";
    public static final String P_PARAMS_FILE = "parameter-file";
    public static final String P_PARAMS_FILE_TEST = "parameter-file-test";
    public static final String P_OUTPUT_FILE = "output-file";
    public static final String P_TST_FILE = "tst-file";
    
    public static final String P_NUMBER_RUNS = "num-runs";
    public static final String P_SEED = "seed";
    public static final String P_ERROR_MEASURE = "error-measure";
    public static final String P_TYPE_OF_MODEL = "type-model";
        
    public CalibrationController controller;			
    public CalibrationStats stats;

	public long seed  = 1;
    public int numRuns = 1;				// number of times the model is run (Monte Carlo sim)
		
    public String parametersFile; 		// = "./config/configExternalCallBass.properties";
    public String parametersFileTst; 		// = "./config/configExternalCallBass.properties";
    public String data; 				// = "./data/premiums_98days_2012_TRA.csv";
    public String test; 				// = "./data/premiums_33days_2012_TEST.csv";
    public String outputFile; 		
    
    public String typeOfAnimalModel; 		
    public String errorMeasure; 	
    

	Parameters2Calib parameters;
		    
    /**
	 * Set-up the controller, read historical data and create objects for evaluating
	 * individuals
	 */
    public void setup(EvolutionState state, Parameter base) {
    	
        super.setup(state, base); 
                
        // get from the parameter file        
        this.data = state.parameters.getString(base.push(P_DATA_FILE), null);
        this.test = state.parameters.getString(base.push(P_TST_FILE), null);
        this.outputFile = state.parameters.getString(base.push(P_OUTPUT_FILE), null);
        this.parametersFile = state.parameters.getString(base.push(P_PARAMS_FILE), null);
        this.parametersFileTst = state.parameters.getString(base.push(P_PARAMS_FILE_TEST), null);
        this.numRuns = state.parameters.getInt(base.push(P_NUMBER_RUNS), null, 1);
        this.seed = state.parameters.getInt(base.push(P_SEED), null, 1);
        this.typeOfAnimalModel = state.parameters.getString(base.push(P_TYPE_OF_MODEL), null);
        this.errorMeasure = state.parameters.getString(base.push(P_ERROR_MEASURE), null);      

 		try {	
 			 			
 			// depending on the problem we set one parameters object or another
 			switch (this.typeOfAnimalModel) {
 		         case "allCalib":
 		        	 
 		        	// all parameters
 	 				this.parameters = (P2CalibAll) 	
 	 					new P2CalibAll(7);
 	 				
 		             break;
 		         case "staticNetwork":
 		        	 
 		        	// no remove or add edge, just 4 parameters (fixedSN)
 	 				this.parameters = (P2CalibStaticNetwork) 
 	 					new P2CalibStaticNetwork(4); 	
 	 				
 		             break;
 		         case "withInitialPrems":
 		        	 
 		        	// 8 parameters to calibrate, also the initial premiums
 	 				this.parameters = (P2CalibAllWithInitialPrems) 
 	 					new P2CalibAllWithInitialPrems(8); 
 	 		
 		             break;
 		         case "allCalibWithFiles":
 		        	 
 		        	// 7 parameters to calibrate and reading SNs from files
  	 				this.parameters = (P2CalibAllWithFiles) 
  	 					new P2CalibAllWithFiles(7); 
  	 		
 		             break;
 		        case "fixedNetwork":
		        	 
 		        	// 6 parameters to calibrate (SN is fixed and read from file)
  	 				this.parameters = (P2CalibFixedSN) 
  	 					new P2CalibFixedSN(6); 
  	 				
 		            break;
 		        case "complexFixedNetwork":
		        	 
		        	// 5 parameters to calibrate (SN is fixed and read from file). 
 		    	    // Just social adoption
 	 				this.parameters = (P2CalibComplexFixedSN) 
 	 					new P2CalibComplexFixedSN(3); 
 	 				
		            break;
 		        case "complexFixedNetworkWithInnov":
		        	 
		        	// 4 parameters to calibrate (SN is fixed, static and read from file). 
 		    	    // Just social adoption but with prob. of innovation
 	 				this.parameters = (P2CalibComplexWithInnovFixedSN) 
 	 						new P2CalibComplexWithInnovFixedSN(4); 
 	 				
		            break;
 		        case "thresholdsFixedNetwork":
		        	 
		        	// 3 parameters to calibrate (SN is fixed, static and read from file). 
 		    	    // Just social adoption using thresholds
 	 				this.parameters = (P2CalibThresholdsFixedSN) 
 	 						new P2CalibThresholdsFixedSN(3); 
 	 				
		            break;
		            
 		       case "thresholdsWithInnovFixedNetwork":
		        	 
		        	// 4 parameters to calibrate (SN is fixed, static and read from file). 
		    	    // Just social adoption using thresholds plus innovation prob.
	 				this.parameters = (P2CalibThresholdsWithInnovFixedSN) 
	 						new P2CalibThresholdsWithInnovFixedSN(4); 
	 				
		            break;
		               
 		         
 		         default: throw new IllegalArgumentException();
 		     }
 			
 		} catch (IllegalArgumentException e) {
 	    	state.output.fatal("Invalid option for type of model: " + this.typeOfAnimalModel);
 		} 
 		 
 		try {	
 			// creating the calibration controller from the parameters to calib
 			this.controller = new CalibrationController (this.data, this.parametersFile
 							, this.seed, this.parameters, this.numRuns);

 			stats = new CalibrationStats(controller.getRatioAgents(), 
 					this.numRuns, controller.getMaxSteps());
 			
 	    } catch (IOException e) {
 	    	state.output.fatal("setup(): "
 	    			+ "Error when reading historical files (@ calibration controller) \n");
 		} 
 		  			    		
        
    }
        
    public void evaluate(
    		final EvolutionState state, 
    		final Individual ind, 
    		final int subpopulation, 
    		final int threadnum){
    	
        if (ind.evaluated) return;
                
        if( !( ind instanceof DoubleVectorIndividual ) )
            state.output.fatal( "The individuals for this problem should be DoubleVectorIndividuals." );

        double[] genome = ((DoubleVectorIndividual)ind).genome;
        
        /*for (int i = 0; i < genome.length; i++)
        	System.out.print("; " + genome[i]);
        System.out.println();*/
        
        // building the parameters from the chromosome  
        
        //long time1 = System.currentTimeMillis( );
                
		// obtain the parameters and change them to the genome values
		((Parameters2Calib) controller.getParametersFromCalib()).
				specifyParams (genome);
		
		// run the model with the new parameters
		controller.runModelWithCalib(stats);

		//long  time2  = System.currentTimeMillis( );
		//System.out.println((double)(time2 - time1)/1000 + "s for running model");
        
		// we finally print out the results of the objectives			
		// double obj1 = stats.getAvgEucl();			
		//double obj1 = stats.getAvgL0();			
		//double obj1 = stats.getAvgManhattan();
		//double obj2 = stats.getAvgChebyshev();
        
        double obj = 0;
        double objectives[] = new double[2];
        
        if (this.errorMeasure.equals("corr")) {   // to maximize, do nothing
        	obj = stats.getAvgCorr();
        }
        else if (this.errorMeasure.equals("eucl")) {  // to minimize, multiply by -1
        	obj = (-1)*stats.getAvgEucl();
        }
        else if (this.errorMeasure.equals("L0")) {  // to minimize, multiply by -1
        	obj = (-1)*stats.getAvgL0();
        }
        else if (this.errorMeasure.equals("cheb")) { // to minimize, multiply by -1
        	obj = (-1)*stats.getAvgChebyshev();
        } 
        else if (this.errorMeasure.equals("man")) {  // to minimize, multiply by -1
        	obj = (-1)*stats.getAvgManhattan();
        } 
        else if (this.errorMeasure.equals("MOEucl")) {
        	
        	// MULTIOBJECTIVE: Euclidean distance for trend and cumulative         	
         	objectives[0] = (-1)*stats.getAvgEucl();
         	objectives[1] = (-1)*stats.getAvgEuclCum();       	
         	
        } 
        
        // setting the calculated fitness
        if (this.errorMeasure.equals("MOEucl")) {
        	
        	((MultiObjectiveFitness)ind.fitness).setObjectives(state, objectives);
        	
        } else {
        	
	        if (this.errorMeasure.equals("corr")){	        
	        	((SimpleFitness)(ind.fitness)).setFitness( state, obj, obj==1.0 );
	        } else {
	        	// for distances, the best fitness is 0
	        	((SimpleFitness)(ind.fitness)).setFitness( state, obj, obj==0 );        	
	        }             	
        }
        	
        // making the individual valid	    
        ind.evaluated = true;                	
       
    }
    
	/** 
	 * special version of evaluation when there is an evaluation print out useful information
	 * to tell the user how the Individual operates
	 */
    
    // MAYBE USEFUL FOR GENERATING THE TEST MEASURE OR EVEN THE GRAPH WITH THE COMPARISON!!
    public void describe(
    		
        final EvolutionState state, 
        final Individual ind, 
        final int threadnum,
        final int subpopulation,
        final int log)  {
    	
    	int jobNum = ((Integer)(state.job[0])).intValue();
    	
        ind.evaluated = false;
        evaluate(state, ind, subpopulation, threadnum);
        
        if( !( ind instanceof DoubleVectorIndividual ) )
            state.output.fatal( "The individuals for this problem should be DoubleVectorIndividuals." );

        double[] genome = ((DoubleVectorIndividual)ind).genome;
        
        // obtain the parameters and change them to the genome values
     
        // depending on the problem we set one parameters object or another
		switch (this.typeOfAnimalModel) {
	         case "allCalib":
	        	 
	        	// all parameters
	 			((P2CalibAll) controller.getParametersFromCalib()).
	 			specifyParams (genome); 	
	 			
	             break;
	         case "staticNetwork":
	        	 
	        	// no remove or edge set, just 4 parameters (fixedSN)
	 			((P2CalibStaticNetwork) controller.getParametersFromCalib()).
	 			specifyParams (genome); 	
	 		
	             break;
	         case "withInitialPrems":
	        	 
	        	// 8 parameters including initial prems
	 			((P2CalibAllWithInitialPrems) controller.getParametersFromCalib())
	 			.specifyParams (genome); 	
	 	
	             break;
	         case "allCalibWithFiles":
	        	 
	        	 // 7 parameters choosing the SN
		 		 ((P2CalibAllWithFiles) controller.getParametersFromCalib())
		 			.specifyParams (genome); 
 				
	             break;
	             
	         case "fixedNetwork":
	 				
	        	 // 6 parameters as the SN is given in the config file
		 		 ((P2CalibFixedSN) controller.getParametersFromCalib())
		 			.specifyParams (genome); 
		 		 
		          break;
		         
	         case "complexFixedNetwork":
	 				
	        	 // 3 parameters as the SN is given in the config file and just social adoption
	        	 // no innovation
		 		 ((P2CalibComplexFixedSN) controller.getParametersFromCalib())
		 			.specifyParams (genome); 
		 		 
		          break;

	         case "complexFixedNetworkWithInnov":
	 				
	        	 // 4 parameters as the SN is given in the config file 
	        	 // social adoption and prob. of innovation
		 		 ((P2CalibComplexWithInnovFixedSN) controller.getParametersFromCalib())
		 			.specifyParams (genome); 
		 		 
		          break;

	         case "thresholdsFixedNetwork":
	 				
	        	 // 3 parameters as the SN is given in the config file 
	        	 // social adoption is the thresholds value
		 		 ((P2CalibThresholdsFixedSN) controller.getParametersFromCalib())
		 			.specifyParams (genome); 
		 		 
		          break;		         

	         case "thresholdsWithInnovFixedNetwork":
	 				
	        	 // 4 parameters as the SN is given in the config file 
	        	 // social adoption is the thresholds value plus innov. prob
		 		 ((P2CalibThresholdsWithInnovFixedSN) controller.getParametersFromCalib())
		 			.specifyParams (genome); 
		 		 
		          break;
		         
	         default:
	             throw new IllegalArgumentException("DESCRIBE): "
	             		+ "Invalid option for type of model: " + this.typeOfAnimalModel);
	     }
        	
		PrintWriter writer = null;	
		
		try {			
			
			// we will print the information into a file	
			String filename = this.outputFile + ".job." + jobNum + ".info";
			System.out.println(filename);
			writer = new PrintWriter (new FileOutputStream(filename, false ));
			
			// first print information about the individual
			ind.printIndividual(state, writer);
			
			// TRA VALIDATION
			
			// run the model with the new parameters		
			controller.runModelWithCalib(stats);	
			
			// print into a file
			writer.println("TRA RESULTS;;;;");
			stats.printStats(writer, controller.getHistoricalData().getHistoricalDataArray());
		
		} catch (IOException e) {
	    	state.output.fatal("describe(): Error when reading and writing " +
	    			"files for summing up the best individual (TRA)\n");
	    	e.printStackTrace(System.err);
		}
		
		try {
			// TST VALIDATION
									
			// get tst data

 			//Parameters2Calib parameters;
 			//parameters = (P2CalibAll) new P2CalibAll(genome.length);						
			
			CalibrationController controller2 = new CalibrationController 
					(this.test, 
					this.parametersFileTst, 
					this.seed, this.parameters, this.numRuns);

			CalibrationStats stats2 = new CalibrationStats
					(controller2.getRatioAgents(), 
					this.numRuns, controller2.getMaxSteps());
			
			// run the model with the new historical data set (TEST)
			controller2.runModelWithCalib(stats2);
					
			writer.println("TST RESULTS;;;;");	
			stats2.printStats(writer, controller2.getHistoricalData().getHistoricalDataArray());
			
	    	writer.close();
	    	
		} catch (IOException e) {
	    	state.output.fatal("describe(): Error when reading and writing " +
	    			"files for summing up the best individual (TST)\n");
	    	e.printStackTrace(System.err);
		}
			
		try {
			
			PrintWriter writer2;
			
	    	// writing best configuration found in a properties file

			String filename = this.outputFile + ".job." + jobNum + ".properties";
			System.out.println(filename);
			
	    	writer2 = new PrintWriter
	    			(new FileOutputStream(filename, false ));
			
	    	controller.getModelParameters().printParameters(writer2);
	    	
	    	writer2.close();
	    	
		} catch (IOException e) {
	    	state.output.fatal("describe(): Error when creating properties file"
	    			+ "from the best individual\n");
	    	e.printStackTrace(System.err);
		}
    	
		
        // making the individual valid	    
        ind.evaluated = true;
                        
    }
    
 }

