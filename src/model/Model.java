package model;

//import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.LinkedHashMap;
import java.util.List;
import org.graphstream.graph.Node;
import java.util.*;
import java.lang.*;
import java.util.stream.IntStream;

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
    // Constantes del los tipo de decisiones
    static int REPETITION = 2;
    static int DELIBERATION = 3;
    static int IMITATION = 4;
    static int SOCIALCOMPARISION = 5;
    static int UTILITY = 1;

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

    public static ModelParameters params;

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

    int repetition_strategy_Agents[]; 			// a counter of the k_I agents during the simulation
    int deliberation_strategy_Agents[]; 			// a counter of the k_T agents during the simulation
    int imitation_strategy_Agents[];			// a counter of the k_U agents during the simulation
    int social_strategy_Agents[]; 			// a counter of the k_T agents during the simulation
    int utility_strategy_Agents[];			// a counter of the k_U agents during the simulation
    int strategyChanges[];		// array with the total number of evolutionStrategies changes during the simulation

    //--------------------------- Get/Set methods ---------------------------//	
    
    //

    public int[] getRepetition_strategy_Agents() {
        return repetition_strategy_Agents;
    }

    public void setRepetition_strategy_Agents(int[] repetition_strategy_Agents) {
        this.repetition_strategy_Agents = repetition_strategy_Agents;
    }

    public int[] getDeliberation_strategy_Agents() {
        return deliberation_strategy_Agents;
    }

    public void setDeliberation_strategy_Agents(int[] deliberation_strategy_Agents) {
        this.deliberation_strategy_Agents = deliberation_strategy_Agents;
    }

    public int[] getImitation_strategy_Agents() {
        return imitation_strategy_Agents;
    }

    public void setImitation_strategy_Agents(int[] imitation_strategy_Agents) {
        this.imitation_strategy_Agents = imitation_strategy_Agents;
    }

    public int[] getSocial_strategy_Agents() {
        return social_strategy_Agents;
    }

    public void setSocial_strategy_Agents(int[] social_strategy_Agents) {
        this.social_strategy_Agents = social_strategy_Agents;
    }

    public int[] getUtility_strategy_Agents() {
        return utility_strategy_Agents;
    }

    public void setUtility_strategy_Agents(int[] utility_strategy_Agents) {
        this.utility_strategy_Agents = utility_strategy_Agents;
    }

    public int[] getStrategyChanges() {
        return strategyChanges;
    }

    public void setStrategyChanges(int[] strategyChanges) {
        this.strategyChanges = strategyChanges;
    }
    
    
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

    public int getCumPurchasesToBrandAtStep(int _position, int _brand) {
        return cumPurchases[_brand][_position];
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

    public int getNewPurchasesForBrandAtStep(int _position, int _brand) {
        return newPurchases[_brand][_position];
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
    public static ModelParameters getParametersObject() {
        return params;
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

    private void createBrands(ModelParameters params) {
        int _brands = params.getBrands();
        int _drivers = params.getMaxDrivers();

        this.brands = new Brand[_brands];

        for (int i = 0; i < _brands; i++) {
            Brand b = new Brand(i, _drivers, params.getBrandNames(i));
            for (int j = 0; j < _drivers; j++) {
                b.setDrivers(j, params.getBrandDrivers(i)[j]);
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
        createBrands(params);

        // printSumamryBrandScreen();
        segment = new GamersSegments();

        // generate int values for sizes to be consistent with the number of agents
        // of the segments
        segment.genSegmentSizesInt(params.getSegmentSizes(), params.nrAgents);

        // Initialisation of the subscription summary
        newPremiumAgents = new int[MAX_STEPS];
        cumPremiumAgents = new int[MAX_STEPS];

        strategyChanges = new int[MAX_STEPS];
        repetition_strategy_Agents = new int[MAX_STEPS];
        deliberation_strategy_Agents = new int[MAX_STEPS];
        imitation_strategy_Agents = new int[MAX_STEPS];
        social_strategy_Agents = new int[MAX_STEPS];
        utility_strategy_Agents = new int[MAX_STEPS];

        newPurchases = new int[params.brands][MAX_STEPS];
        cumPurchases = new int[params.brands][MAX_STEPS];

        for (int i = 0; i < params.brands; i++) {
            for (int j = 0; j < MAX_STEPS; j++) {
                newPurchases[i][j] = 0;
            }
        }

        for (int i = 0; i < MAX_STEPS; i++) {
            strategyChanges[i] = newPremiumAgents[i] = cumPremiumAgents[i] = 0;

            repetition_strategy_Agents[0] = deliberation_strategy_Agents[0] = imitation_strategy_Agents[0] = social_strategy_Agents[0] = utility_strategy_Agents[0] = strategyChanges[i] = 0;
        }

        socialNetwork = new GraphStreamer(params.nrAgents, params);
        socialNetwork.setGraph(params);

        System.out.println(this.params.export());

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
        //setAnonymousAgentApriori(scheduleCounter);
        setNewAnonymousAgentApriori(scheduleCounter);

        scheduleCounter++;

        // Initialization of the gamer agents
        agents = new Bag();

        // start the number of agents with premium subscriptions
        //newPremiumAgents[(int) schedule.getSteps()] = 0;
        //cumPremiumAgents[(int) schedule.getSteps()] = 0;
        // start the number of new purchases of every brand
        for (int i = 0; i < params.brands; i++) {
            newPurchases[i][(int) schedule.getSteps()] = 0;
            cumPurchases[i][(int) schedule.getSteps()] = 0;
        }

        // changed to have agents with random initial states (shuffle IDs which link them with nodes)		
        segment.beginSegmentAssignation();

        // Todo [jaume] Remplazar este metodo por una función depeniendo del tipo de targeting a utilizar
        for (int i = 0; i < params.nrAgents; i++) {
            // TODO: [jaume] no podemos asignar premiun o no a un usuario en este paso ya que aún no tiene las preferencias creadas. Por defecto sera de tipo BASIC_USER y luego depeniendo de la politica elegida
            // se modificará a premiun y se asignara un compra en el step 0

            GamerAgent cl = generateAgent(i, Model.BASIC_USER);

            // Add agent to the list and schedule
            agents.push(cl);

            // Add agent to the schedule
            schedule.scheduleRepeating(Schedule.EPOCH, scheduleCounter, cl);
        }

        // elegimos a los influencers
        int strategy = params.getTargetingStrategy();

        switch (strategy) {
            case ModelParameters.TARGETING_RANDOM:
                this.initialPrems = this.generateRandomPremiun();
                break;

            case ModelParameters.TARGETING_DEGREE:
                this.initialPrems = this.generatePremiunsWithMostDegree();
                break;

            case ModelParameters.TARGETING_PREFERENCES:
                this.initialPrems = this.generatePremiunsWithMostTargeting();
                break;

            case ModelParameters.TARGETING_PREFERENCES_DEGREE:
                this.initialPrems = this.generatePremiunsWithMostTargetingAndDegree();
                break;

        }

        for (int i = 0; i < agents.size(); i++) {
            GamerAgent gamerAgend = ((GamerAgent) agents.get(i));

            if (this.initialPrems.contains(gamerAgend.gamerAgentId)) {
                gamerAgend.setSubscriptionState(Model.PREMIUM_USER);
                // Regalamos en el step 0 el producto al influencer seleccionado
                gamerAgend.setPurchasedBrands(0, params.getBrandToGive());
            }

        }

        // calculate the set of non premium agents to be rewarded
        // if (params.getExperimentType() == ModelParameters.BASS_WEIGHTS_INITIAL_BASICS) {
        //    createRewardedUsers();
        // }
        // check the status to count initial premium agents (not the new)
        cumPremiumAgents[0] = calcNrInfectedPremiums();

        // check the status to count initial purchases (not the new)
        for (int i = 0; i < params.brands; i++) {
            Brand b = brands[i];
            cumPurchases[i][0] = calcNrPurchasesOfBrand(b);
        }

        scheduleCounter++;

        //setAnonymousAgentAposteriori(scheduleCounter);
        setNewAnonymousAgentAposteriori(scheduleCounter);

        // logging  test
        //java.util.Date date= new java.util.Date();
        //logger.info("Seed value: " + this.seed() + "\nSimulation ready!\n");
        //logger.info("Model starts (real machine time) @ " + 
        //		(new java.sql.Timestamp(date.getTime())) + "\n");
    }

    //-------------------------- Auxiliary methods --------------------------//	
    /**
     * Se eligen aleatoriamente un porcentaje aleatorio de agentes premiuns
     * donde se les regala un producto
     *
     * @return
     */
    private HashSet<Integer> generateRandomPremiun() {

        HashSet<Integer> initialPrems = new HashSet<>();

        int numberOfInitPremiums = 0;
        //TODO: Pendiente de la respuesta de profe
        //for (int i = 0; i < params.brands; i++) {
        numberOfInitPremiums = (int) (params.nrAgents * (params.getInitialPercentagePremium())[0]);

        //}
        // the set has the initial premiums
        while (initialPrems.size() < numberOfInitPremiums) {
            initialPrems.add(this.random.nextInt(params.nrAgents));
        }

        return initialPrems;
    }

    private HashSet<Integer> generatePremiunsWithMostDegree() {
        HashSet<Integer> initialPrems = new HashSet<>();

        int[] degreeMap = this.orderNodesByDegree();

        int numberOfInitPremiums = (int) (params.nrAgents * (params.getInitialPercentagePremium())[0]);

        for (int i = 0; i < numberOfInitPremiums; i++) {
            //System.out.println("   " + i + "  "+degreeMap.get(i).getId() + "   " + degreeMap.get(i).getDegree() + "   " + degreeMap.get(i).getAttributeCount());
            initialPrems.add(Integer.valueOf(degreeMap[i]));
        }

        return initialPrems;
    }

    private int[] orderNodesByDegree() {
        int[] initialPrems = new int[params.nrAgents];

        ArrayList<Node> degreeMap = socialNetwork.getDegreeMap();

        // var numberOfInitPremiums = (int) (params.nrAgents * (params.getInitialPercentagePremium())[0]);
        for (int i = 0; i < params.nrAgents; i++) {
            //System.out.println("   " + i + "  "+degreeMap.get(i).getId() + "   " + degreeMap.get(i).getDegree() + "   " + degreeMap.get(i).getAttributeCount());
            initialPrems[i] = Integer.valueOf(degreeMap.get(i).getId());
        }

        return initialPrems;
    }

    private HashSet<Integer> generatePremiunsWithMostTargeting() {
        HashSet<Integer> initialPrems = new HashSet<>();

        int[] degreeMap = this.orderNodesByTargeting();

        int numberOfInitPremiums = (int) (params.nrAgents * (params.getInitialPercentagePremium())[0]);

        for (int i = 0; i < numberOfInitPremiums; i++) {
            initialPrems.add(Integer.valueOf(degreeMap[i]));
        }

        return initialPrems;
    }

    private int[] orderNodesByTargeting() {
        int[] initialPrems = new int[params.nrAgents];

        HashMap<Integer, Double> map = new HashMap<Integer, Double>();

        for (int i = 0; i < params.nrAgents; i++) {
            GamerAgent aux = (GamerAgent) agents.get(i);
            map.put(i, util.Functions.utilityFunction(brands[params.getBrandToGive()].getDrivers(), aux.getPreferences()));
        }

        Map<Integer, Double> mapSorted = util.Functions.sortHashMapByValue(map, true);

        int numberOfInitPremiums = (int) (params.nrAgents * (params.getInitialPercentagePremium())[0]);

        int i = 0;

        for (Map.Entry<Integer, Double> en : mapSorted.entrySet()) {
            //System.out.println("Key = " + en.getKey()
            //        + ", Value = " + en.getValue() + ", i = " + i + ", max = " + numberOfInitPremiums);
            initialPrems[i] = en.getKey();
            i++;
        }

        return initialPrems;
    }

    private HashSet<Integer> generatePremiunsWithMostTargetingAndDegree() {
        HashSet<Integer> initialPrems = new HashSet<>();

        int[] degreeMap = this.orderByDegreeAndTargeting();

        int numberOfInitPremiums = (int) (params.nrAgents * (params.getInitialPercentagePremium())[0]);

        for (int i = 0; i < numberOfInitPremiums; i++) {
            initialPrems.add(Integer.valueOf(degreeMap[i]));
        }

        return initialPrems;
    }

    private int[] orderByDegreeAndTargeting() {

        int[] initialPrems = new int[params.nrAgents];

        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

        int[] bestDegre = this.orderNodesByDegree();
        int[] bestTarg = this.orderNodesByTargeting();

        // para calcular que elementos tienen el mejor tarjeting y grado lo que hacemos es comparar la posicion de ellos respecto el resto. Si 
        // el nodo 1 estan en la posicion o en grado (es muy influencer) pero tiene poco targeting (posicion 4) entonces 0 + 4 = 4. Este  valor representa su importancia
        for (int i = 0; i < params.nrAgents; i++) {
            int a = util.Functions.findIndex(bestDegre, i);
            int b = util.Functions.findIndex(bestTarg, i);
            map.put(i, a + b);
        }

        int i = 0;
        for (Map.Entry<Integer, Integer> entry : util.Functions.sortHashMapByValue(map, false).entrySet()) {
            // System.out.println(entry.getKey() + " : " + entry.getValue());
            initialPrems[i] = entry.getKey();
            i++;
        }

        return initialPrems;
    }

    // function to sort hashmap by values 
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
    private GamerAgent generateAgent(int nodeId, int state) {
        int segmentId;

        segmentId = segment.assignSegment();

        GamerAgent cl = new GamerAgent(nodeId, segmentId, state, params.getMaxDrivers(), MAX_STEPS, params.brands);

        Random randomno = new Random();

        for (int j = 0; j < params.getMaxDrivers(); j++) {
            // generamos los valores de las preferencias dando una media y una desviación
            cl.setPreferences(j, Functions.scaleGaussianValue(Model.getParametersObject().getPreferences()[j], randomno.nextGaussian(), Model.getParametersObject().getBrandStdev(), 0.0, 1.0));
            // calculamos la utilidad de cada agente para cada marca
            for (int brand = 0; brand < this.brands.length; brand++) {
                cl.setUtility(brand, util.Functions.utilityFunction(this.getBrands()[brand].getDrivers(), cl.getPreferences()));
            }

        }
        // System.out.println(Arrays.toString(cl.getPreferences()));

        return cl;
    }

    // Todo: Remove it
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

                int currentStep = (int) schedule.getSteps();

                for (int i = 0; i < params.brands; i++) {

                    Brand b = brands[i];

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
                }

            }
        });

    }

    // Todo: Remove it
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
                
                int currentStep = (int) schedule.getSteps();

                for (int i = 0; i < params.nrAgents; i++) {

                    if (((GamerAgent) agents.get(i)).getCurrentStratey(currentStep)
                            == REPETITION) {
                        repetition_strategy_Agents[currentStep]++;
                    }

                    if (((GamerAgent) agents.get(i)).getCurrentStratey(currentStep)
                            == IMITATION) {
                        imitation_strategy_Agents[currentStep]++;
                    }

                    if (((GamerAgent) agents.get(i)).getCurrentStratey(currentStep)
                            == SOCIALCOMPARISION) {
                        social_strategy_Agents[currentStep]++;
                    }

                    if (((GamerAgent) agents.get(i)).getCurrentStratey(currentStep)
                            == UTILITY) {
                        utility_strategy_Agents[currentStep]++;
                    }

                    if (((GamerAgent) agents.get(i)).getCurrentStratey(currentStep)
                            == DELIBERATION) {
                        deliberation_strategy_Agents[currentStep]++;
                    }

                    if (((GamerAgent) agents.get(i)).hasChangedStrategyAtStep(currentStep)) {
                        strategyChanges[currentStep]++;
                    }
                }

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
            System.out.print("  Name: " + b.name);
            System.out.println();

        }

        System.out.println("\n******************* \n\n");

    }

}
