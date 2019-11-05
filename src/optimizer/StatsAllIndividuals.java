
package optimizer;
import ec.*;
import ec.util.*;

import java.io.*;

import ec.vector.*;

public class StatsAllIndividuals extends Statistics {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 3919615684136863190L;
	
	// The parameter string and log number of the file for our readable population
    public static final String P_POPFILE = "pop-file";
    public int popLog;

    public void setup(final EvolutionState state, final Parameter base) {
    	
        // DO NOT FORGET to call super.setup(...) !!
        super.setup(state,base);

        // set up popFile
        File popFile = state.parameters.getFile(
            base.push(P_POPFILE),null);
        if (popFile!=null) try
                               {
                               popLog = state.output.addLog(popFile,true);
                               }
            catch (IOException i)
                {
                state.output.fatal("An IOException occurred while trying to create the log " + 
                    popFile + ":\n" + i);
                }

        }

    public void postEvaluationStatistics(final EvolutionState state) {
    	
        // be certain to call the hook on super!
        super.postEvaluationStatistics(state);

        // write out a warning that the next generation is coming 
        //state.output.println("-----------------------\nGENERATION " + 
        //    state.generation + "\n-----------------------", popLog);

        for(int j = 0; j < state.population.subpops[0].individuals.length; j++) {

            // print fitness
            double fit = 
            		((DoubleVectorIndividual)state.population.subpops[0].individuals[j]).
            		fitness.fitness();
            
            state.output.print( fit + "; ",  popLog);
            
            // print genome
        	double[] genome = 
            		((DoubleVectorIndividual)state.population.subpops[0].individuals[j]).genome;
            
            for(int i=0; i < genome.length; i++) 
            	state.output.print(genome[i] + "; ",  popLog);
            	
        	// print new line
            state.output.println("", popLog);
        }
    }
    
}

