package model;

//import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import org.graphstream.graph.Node;

import sim.engine.*;
import sim.util.*;

import socialnetwork.*;
import util.*;

/**
 * Simulation core, responsible for scheduling agents.
 *
 * @author mchica
 */
public class Model extends SimState {

    // ########################################################################
    // Variables
    // ########################################################################
    static final long serialVersionUID = 1L;

    static boolean INCLUDEZERO = true;
    static boolean INCLUDEONE = true;

    // LOGGING
    //public static Logger logger = LoggerFactory.getLogger(Model.class);
    //static String LOGFILENAME = "./config/log4j.properties";
    static String CONFIGFILENAME;

    // MODEL VARIABLES
    static int MAX_STEPS = 800;

    static int daysOfWeek[];		// this array is for allocating the day of the week for each step
    // notice that calendar.getInstance and setTime are time-consuming
    // instead, we pre-calculate the days of the week in advance

    int currentDay;

    ModelParameters params;

    Bag agents;

    // SEGMENTS OF CLIENTS
    GamersSegments segment;

    // SUMMARY VALUES FOR THE SUBSCRIPTION STATUS OF AGENTS
    public static int NON_USER = -1;

    public static int BASIC_USER = 0;

    public static int PREMIUM_USER = 1;

    int newPremiumAgents[]; //  to count new premium agents every day
    int cumPremiumAgents[]; //  to count the cumulative number of premium agents every day
    int newPurchases[][]; //  to count new purchases of every brand every day
    int cumPurchases[][]; //  to count the cumulative purchases of every brand every day

    // SOCIAL NETWORK
    GraphStreamer socialNetwork;

    HashSet<Integer> initialPrems;					// IDs of agents to be initial premiums 

    HashSet<Integer> initialNoPremsRewarded;		// IDs of agents to be rewarded to become prem & influence people 

    Brand[] brands;

    //--------------------------- Clone method ---------------------------//	
    /**
     * Clone the object
     */
    /* TODO public Object clone() {
    	Model m = (Model)(super.clone());
        m.socialNetwork = (GraphStreamer)(socialNetwork.clone());
        m.segment = (GamersSegments)(segment.clone());			
        return m;        
    }*/
    //--------------------------- Get/Set methods ---------------------------//	
    //
    /*public static String getLogFileName() {
		return LOGFILENAME;
	}
	
	public static void setLogFileName(String _logFileName) {
		LOGFILENAME = _logFileName;
	}*/
    public static String getConfigFileName() {
        return CONFIGFILENAME;
    }

    public static void setConfigFileName(String _configFileName) {
        CONFIGFILENAME = _configFileName;
    }

    /**
     * this function returns if the specified gamer was included as initial
     * rewarded basic gamer
     *
     * @param _gamerId
     * @return
     */
    public boolean isAnInitialRewardedBasicUser(int _gamerId) {
        return this.initialNoPremsRewarded.contains(_gamerId);
    }

    public GraphStreamer getSocialNetwork() {
        return socialNetwork;
    }

    /**
     * @return the currentDate
     */
    /*public Date getCurrentDate() {
		return currentDate;
	}

	/**
	 * @param currentDate the currentDate to set
     */
 /*public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}*/
    /**
     * Gets the bag of agents.
     *
     * @return
     */
    public Bag getAgents() {
        return agents;
    }

    /**
     * Sets the bag of agents
     *
     * @param nrAgents
     */
    public void setAgents(Bag _agents) {
        this.agents = _agents;
    }

    //--------------------------- Auxiliary methods --------------------------//
    /**
     * Get the number of CUMULATIVE premium members at a given step - time
     *
     * @return the number of premiums
     */
    public int getCumPremiumsAtStep(int _position) {
        return cumPremiumAgents[_position];
    }

    /**
     * Get the number of CUMULATIVE premium members for all the period of time
     *
     * @return an ArrayList with the evolution of the premium members
     */
    public int[] getCumPremiumsArray() {
        return cumPremiumAgents;
    }

    /**
     * Get the number of NEW premium members at a given step - time
     *
     * @return the number of premiums
     */
    public int getNewPremiumsAtStep(int _position) {
        return newPremiumAgents[_position];
    }

    /**
     * Get the number of NEW premium members for all the period of time
     *
     * @return an ArrayList with the evolution of the premium members
     */
    public int[] getNewPremiumsArray() {
        return newPremiumAgents;
    }

    public int[][] getNewPurchasesOfEveryBrand() {
        return newPurchases;
    }

    /**
     * Get the parameter object with all the input parameters
     */
    public ModelParameters getParametersObject() {
        return this.params;
    }

    /**
     * Set the parameteres
     *
     * @param _params the object to be assigned
     */
    public void setParametersObject(ModelParameters _params) {
        this.params = _params;
    }

    /**
     * DONT USE YET! Triggered when new premium member. It increases the number
     * of new premium members for the current step arrays.
     */
    /*private void increasePremium () {
		
		int currentStep = (int) this.schedule.getSteps();		
		newPremiumAgents[currentStep] ++;		
	}*/
    /**
     * DONT USE YET! Triggered when losing premium member. It decreases the
     * number of new premium members for the current step arrays
     */
    /*private void decreasePremium () {
		
		int currentStep = (int) this.schedule.getSteps();		
		newPremiumAgents[currentStep] --;
	}*/
    /**
     * This function create an array for allocating the days of the week
     *
     * @param _start
     */
    private void createSeasonality(Date _start) {

        daysOfWeek = new int[MAX_STEPS];

        // creating the calendar
        GregorianCalendar calendar = new GregorianCalendar();

        // set the current date to the initial date
        calendar.setTime(_start);

        daysOfWeek[0] = calendar.get(Calendar.DAY_OF_WEEK);

        for (int i = 1; i < MAX_STEPS; i++) {
            daysOfWeek[i] = (daysOfWeek[i - 1] + 1) % 8;
            if (daysOfWeek[i] == 0) {
                daysOfWeek[i] = 1;
            }
            /*System.out.println("Yesterday was " +daysOfWeek[i-1] + 
					" and today is " + daysOfWeek[i] + " being Sunday " + Calendar.SUNDAY +
					" and saturday " + Calendar.SATURDAY);*/
        }

    }

    private void createBrands(int _brands, int _drivers) {

        this.brands = new Brand[_brands];

        for (int i = 0; i < _brands; i++) {
            Brand b = new Brand(i, _drivers);
            for (int j = 0; j < _drivers; j++) {
                b.setDrivers(j, this.random.nextDouble(true, true));
                this.setBrands(i, b);
            }
        }

    }

    public Brand[] getBrands() {
        return brands;
    }

    public void setBrands(int _index, Brand brand) {
        this.brands[_index] = brand;
    }

    /**
     * This function returns the current day of week (1-sunday....7-saturday) as
     * the Java Calendar constant fields
     *
     * @param _start
     */
    public int getDayOfWeek() {
        return daysOfWeek[currentDay];
    }

    // ########################################################################
    // Constructors
    // ########################################################################
    /**
     * Initializes a new instance of the AnimalGameSimulation class.
     *
     * @param seedIn - a seed provided for the given simulation.
     */
    public Model(long seed) {

        super(seed);

        //PropertyConfigurator.configure(LOGFILENAME);
        //logger.info("AnimalGameSimulation() read configuration file");
        // get parameters
        params = new ModelParameters();
        params.readParameters(CONFIGFILENAME);

        // store an IDs array with the agents to be premium at the beginning of the sim
        // TODO only valid for 1 segment. make it for more than 1 if needed
        this.initialPrems = new HashSet<Integer>();

        if (params.getExperimentType() == ModelParameters.BASS_WEIGHTS_INITIAL_BASICS) {
            // store an IDs array with the agents to be rewarded at the beginning of the run
            // they are no premiums agents with a mininum no. of prem friends and then likely to convert
            this.initialNoPremsRewarded = new HashSet<Integer>();

        }

        // set an array with the days of the week for seasonality
        // create an array with the days of the week
        createSeasonality(params.startDate);

        // TODO [Jaume] Configurar los driver de los productos en el archivo de configuracion
        // Creamos una lista de marcas de productos y que ademas contienen un listado de drivers para cada producto.
        // En un principio estos drivers son inicializados aleatoriamente aunque la idea es poder configurarlo mas adelante
        createBrands(params.getBrands(), params.getMaxDrivers());

        printSumamryBrandScreen();

        segment = new GamersSegments();

        // generate int values for sizes to be consistent with the number of agents
        // of the segments
        segment.genSegmentSizesInt(params.getSegmentSizes(), params.nrAgents);

        // Initialisation of the subscription summary
        newPremiumAgents = new int[MAX_STEPS];
        cumPremiumAgents = new int[MAX_STEPS];

        newPurchases = new int[params.brands][MAX_STEPS];
        cumPurchases = new int[params.brands][MAX_STEPS];

        for (int i = 0; i < params.brands; i++) {
            for (int j = 0; j < MAX_STEPS; j++) {
                newPurchases[i][j] = 0;
            }
        }

        for (int i = 0; i < MAX_STEPS; i++) {
            newPremiumAgents[i] = cumPremiumAgents[i] = 0;
        }

        socialNetwork = new GraphStreamer(params.nrAgents, params);
        socialNetwork.setGraph(params);

        //logger.info("AnimalGameSimulation() end");
    }

    //--------------------------- SimState methods --------------------------//
    /**
     * Sets up the simulation. The first method called when the simulation.
     */
    public void start() {

        super.start();

        // first init currentDay
        currentDay = 0;

        // at random, create the IDs they are going to be premium
        // this is done here to change every MC simulation without changing the SN
        // IMPORTANT! IDs of the nodes and gamers must be the same to avoid BUGS!!
        // clear the initial premiums
        this.initialPrems.clear();

        int numberOfInitPremiums = (int) (params.nrAgents * (params.getSegmentInitialPercentagePremium())[0]);

        // the set has the initial premiums
        while (this.initialPrems.size() < numberOfInitPremiums) {
            this.initialPrems.add(this.random.nextInt(params.nrAgents));
        }

        final int FIRST_SCHEDULE = 0;

        int scheduleCounter = FIRST_SCHEDULE;

        // Initialize social network 
        if (params.typeOfNetwork == GraphStreamer.NetworkType.FILE_NETWORK) {

            if (!params.isStaticSN()) {

                // if the SN evolves we reset it to the parameters
                socialNetwork.setGraph(params);
            }

        } else {

            //long time1 = System.currentTimeMillis( );
            socialNetwork = new GraphStreamer(params.nrAgents,
                    params.typeOfNetwork,
                    params.getSegmentsConnectivity(),
                    params.getSegmentSizes(),
                    segment.getSegmentSizesInt(),
                    this.random,
                    params.maxKDegree);

            //long  time2  = System.currentTimeMillis( );
            //System.out.println((double)(time2 - time1)/1000 + "s for creating the SN");
            //System.out.println("degree: " + Toolkit.averageDegree(socialNetwork.getGraph()));
        }
        // Add anonymous agent to calculate statistics

        // Todo: Remove it
        setAnonymousAgentApriori(scheduleCounter);

        setNewAnonymousAgentApriori(scheduleCounter);

        scheduleCounter++;

        // Initialization of the gamer agents
        agents = new Bag();

        // start the number of agents with premium subscriptions
        newPremiumAgents[(int) schedule.getSteps()] = 0;
        cumPremiumAgents[(int) schedule.getSteps()] = 0;

        // start the number of new purchases of every brand
        for (int i = 0; i < params.brands; i++) {
            newPurchases[i][(int) schedule.getSteps()] = 0;
            cumPurchases[i][(int) schedule.getSteps()] = 0;
        }

        // changed to have agents with random initial states (shuffle IDs which link them with nodes)		
        segment.beginSegmentAssignation();

        for (int i = 0; i < params.nrAgents; i++) {

            GamerAgent cl = generateAgent(i);

            // Add agent to the list and schedule
            agents.push(cl);

            // Add agent to the schedule
            schedule.scheduleRepeating(Schedule.EPOCH, scheduleCounter, cl);
        }

        // calculate the set of non premium agents to be rewarded
        if (params.getExperimentType() == ModelParameters.BASS_WEIGHTS_INITIAL_BASICS) {
            createRewardedUsers();
        }

        // check the status to count initial premium agents (not the new)
        cumPremiumAgents[0] = calcNrInfectedPremiums();

        scheduleCounter++;

        setAnonymousAgentAposteriori(scheduleCounter);

        setNewAnonymousAgentAposteriori(scheduleCounter);

        // logging  test
        //java.util.Date date= new java.util.Date();
        //logger.info("Seed value: " + this.seed() + "\nSimulation ready!\n");
        //logger.info("Model starts (real machine time) @ " + 
        //		(new java.sql.Timestamp(date.getTime())) + "\n");
    }

    //-------------------------- Auxiliary methods --------------------------//	
    /**
     * Generates a set with the seeded users rewarded by marketing policies
     *
     */
    private void createRewardedUsers() {

        /*System.out.println("Rewarding non premium users: " + params.getSeededUsers() + 
				". Approach: " + params.getRewardApproach());
		
		if (params.getRewardApproach() != ModelParameters.RANDOM) {
			System.out.println("Using " + params.getMinPremFriendsToBelongSet() +
						" prem friends as threshold");
		}*/
        // selecting our seeded users at random is like setting 0 for the min no. of prem friends
        int minThresholdFriends;

        if (params.getRewardApproach() == ModelParameters.RANDOM) {
            minThresholdFriends = 0;
        } else {
            minThresholdFriends = params.getMinPremFriendsToBelongSet();
        }

        // clear rewarded prems from previous MC simulation runs
        this.initialNoPremsRewarded.clear();

        double avgPremFriends = 0;
        for (int i = 0; i < params.nrAgents
                && this.initialNoPremsRewarded.size() < params.getSeededUsers(); i++) {

            // check if the agent is premium
            if (((GamerAgent) agents.get(i)).getSubscriptionState()
                    == Model.BASIC_USER) {

                ArrayList<Integer> neighbors = (ArrayList<Integer>) socialNetwork.getNeighborsOfNode(i);

                // Iterate over neighbors
                int noOfSubscribedFriends = 0;
                for (int j = 0; j < neighbors.size(); j++) {

                    GamerAgent neighbor = (GamerAgent) agents.get(neighbors.get(j));

                    if (neighbor.getSubscriptionState() == Model.PREMIUM_USER) {
                        noOfSubscribedFriends++;
                    }
                }

                if (params.getRewardApproach() == ModelParameters.LESS_LIKELY) {

                    // to get the least likely
                    if (noOfSubscribedFriends < minThresholdFriends) {
                        // add agent to the set
                        this.initialNoPremsRewarded.add(i);

                        avgPremFriends += noOfSubscribedFriends;
                    }

                } else if (noOfSubscribedFriends >= minThresholdFriends) {
                    // add agent to the set
                    this.initialNoPremsRewarded.add(i);

                    avgPremFriends += noOfSubscribedFriends;
                }
            }
        }

        avgPremFriends = (double) avgPremFriends / this.initialNoPremsRewarded.size();

        //System.out.println("We have " + this.initialNoPremsRewarded.size() + " users with"
        //		+ "an average of " + avgPremFriends + " premium friends");				
    }

    /**
     * Generates the agent
     *
     * @param i
     * @return
     */
    private GamerAgent generateAgent(int nodeId) {
        int segmentId;

        segmentId = segment.assignSegment();

        // this code was introduced and the former replaced 
        // after finding an important bug! 
        int state;
        if (this.initialPrems.contains(nodeId)) {
            state = Model.PREMIUM_USER;
        } else {
            state = Model.BASIC_USER;
        }

        //logger.info("Agent " + nodeId + " assigned to segment " + segmentId); 
        //logger.info("Agent " + nodeId + " assigned to subscription status " + state); 
        GamerAgent cl = new GamerAgent(nodeId, segmentId, state, params.getMaxDrivers(), MAX_STEPS);

        // TODO [jaume] Estas preferencias deberan ser definidas en el archivo de configuracion.
        // Inicializamos la preferencias aleatoriamente del agente
        for (int j = 0; j < params.getMaxDrivers(); j++) {
            cl.setPreferences(j, this.random.nextDouble(true, true));
        }

        return cl;
    }

    /**
     * Adds the anonymous agent to schedule (at the beginning of each step),
     * which calculates the statistics.
     *
     * @param scheduleCounter
     */
    private void setAnonymousAgentApriori(int scheduleCounter) {

        // Add to the schedule at the end of each step
        schedule.scheduleRepeating(Schedule.EPOCH, scheduleCounter, new Steppable() {
            /**
             *
             */
            private static final long serialVersionUID = -2837885990121299044L;

            public void step(SimState state) {
                //logNrInfectedApriori = calcNrInfectedPremiums();
                //logAvgDegreeApriori = calcAvgDegreeInfected();

                int currentStep = (int) schedule.getSteps();
                cumPremiumAgents[currentStep] = calcNrInfectedPremiums();
                newPremiumAgents[currentStep] = -1;

                //logger.info("*** Starting Step: " +schedule.getSteps() + 
                //		". Simulated date: " + currentDate.toString());	
            }
        });

    }

    private void setNewAnonymousAgentApriori(int scheduleCounter) {

        // Add to the schedule at the end of each step
        schedule.scheduleRepeating(Schedule.EPOCH, scheduleCounter, new Steppable() {
            /**
             *
             */
            private static final long serialVersionUID = -2837885990121299044L;

            public void step(SimState state) {

                int currentStep = (int) schedule.getSteps();

                for (int i = 0; i < params.brands; i++) {
                    Brand b = brands[i];
                    cumPurchases[b.getBrandId()][currentStep] = calcNrPurchasesOfBrand(b);
                    newPurchases[b.getBrandId()][currentStep] = -1;
                }

                // System.out.println("*** Starting Step: " + schedule.getSteps());
                //logger.info("*** Starting Step: " +schedule.getSteps() + 
                //		". Simulated date: " + currentDate.toString());	
            }
        });

    }

    private void setNewAnonymousAgentAposteriori(int scheduleCounter) {

        // Add to the schedule at the end of each step
        schedule.scheduleRepeating(Schedule.EPOCH, scheduleCounter, new Steppable() {
            /**
             *
             */
            private static final long serialVersionUID = 3078492735754898981L;

            public void step(SimState state) {

                // refresh date
                currentDay++;

                //calendar.setTime(currentDate);
                //calendar.add(Calendar.DATE, 1);
                //currentDate = calendar.getTime();
                // we will update the number of cumulative and new premiums. 		
                int currentStep = (int) schedule.getSteps();

                for (int i = 0; i < params.brands; i++) {
                    Brand b = brands[i];
                    //cumPurchases[b.getBrandId()][currentStep] = calcNrPurchasesOfBrand(b);
                    //newPurchases[b.getBrandId()][currentStep] = -1;

                    if (currentStep == 0) {

                        int tempCum = cumPurchases[b.getBrandId()][currentStep];
                        cumPurchases[b.getBrandId()][currentStep] = calcNrPurchasesOfBrand(b);

                        newPurchases[b.getBrandId()][currentStep]
                                = calcNrNewlyInfectedPremiums(tempCum, cumPurchases[b.getBrandId()][0]);

                    } else {

                        int previousStep = currentStep - 1;

                        cumPurchases[b.getBrandId()][currentStep] = calcNrPurchasesOfBrand(b);

                        newPurchases[b.getBrandId()][currentStep]
                                = calcNrNewlyInfectedPremiums(cumPurchases[b.getBrandId()][previousStep],
                                        cumPurchases[b.getBrandId()][currentStep]);

                    }

//                    System.out.print(">> Purchases obtained at every step by every brand:\n");
//                    System.out.println();
//
//                    System.out.print("Step  " + currentStep + " Brand " + b.getBrandId() + ": " + newPurchases[b.getBrandId()][currentStep] + " new purchases");
//                    System.out.println();
                }

            }
        });

    }

    /**
     * Adds the anonymous agent to schedule (at the end of each step), which
     * calculates the statistics.
     *
     * @param scheduleCounter
     */
    private void setAnonymousAgentAposteriori(int scheduleCounter) {

        // Add to the schedule at the end of each step
        schedule.scheduleRepeating(Schedule.EPOCH, scheduleCounter, new Steppable() {
            /**
             *
             */
            private static final long serialVersionUID = 3078492735754898981L;

            public void step(SimState state) {

                // refresh date
                currentDay++;

                //calendar.setTime(currentDate);
                //calendar.add(Calendar.DATE, 1);
                //currentDate = calendar.getTime();
                // we will update the number of cumulative and new premiums. 												
                int currentStep = (int) schedule.getSteps();

                if (currentStep == 0) {

                    int tempCum = cumPremiumAgents[currentStep];
                    cumPremiumAgents[currentStep] = calcNrInfectedPremiums();

                    newPremiumAgents[currentStep]
                            = calcNrNewlyInfectedPremiums(tempCum, cumPremiumAgents[0]);

                } else {

                    int previousStep = currentStep - 1;

                    cumPremiumAgents[currentStep] = calcNrInfectedPremiums();

                    newPremiumAgents[currentStep]
                            = calcNrNewlyInfectedPremiums(cumPremiumAgents[previousStep],
                                    cumPremiumAgents[currentStep]);

                }

            }
        });

    }

    private int calcNrInfectedPremiums() {
        int nrInfectedAgents = 0;

        for (int i = 0; i < params.nrAgents; i++) {
            if (((GamerAgent) agents.get(i)).getSubscriptionState()
                    == Model.PREMIUM_USER) {
                nrInfectedAgents++;
            }
        }

        return nrInfectedAgents;
    }

    /**
     * Calcular el numero de comprar de un producto en cada step
     *
     * @param brand
     * @return
     */
    private int calcNrPurchasesOfBrand(Brand brand) {

        int res = 0;

        for (int i = 0; i < params.nrAgents; i++) {
            int[] brandsPurchases = ((GamerAgent) agents.get(i)).getPurchasedBrands();
            for (int j = 0; j < brandsPurchases.length; j++) {
                if (brand.getBrandId() == brandsPurchases[j]) {
                    res++;
                }
            }

        }

        return res;
    }

    /**
     *
     * @param NrInfectedApriori
     * @param NrInfectedAposteriori
     * @return
     */
    private int calcNrNewlyInfectedPremiums(int NrInfectedApriori,
            int NrInfectedAposteriori) {
        int diffs;

        diffs = NrInfectedAposteriori - NrInfectedApriori;

        return diffs;
    }

    //----------------------------- I/O methods -----------------------------//
    public void printSumamryBrandScreen() {

        System.out.println("Summary brands config \n*******************\n\n");

        for (int i = 0; i < params.brands; i++) {
            Brand b = this.getBrands()[i];
            System.out.print("Brand nº " + i + " :");
            System.out.println();
            System.out.print("  ID " + b.getBrandId());
            System.out.println();
            System.out.print("  Drivers nº " + Arrays.toString(b.getDrivers()));
            System.out.println();

        }

        System.out.println("\n******************* \n\n");

    }

}
