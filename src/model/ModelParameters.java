package model;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSourceDGS;

import socialnetwork.GraphStreamer;
import socialnetwork.GraphStreamer.NetworkType;
import util.*;

/**
 * Parameters of the model
 *
 * @author mchica
 */
public class ModelParameters {

    // Read config file
    ConfigFileReader config;

    // FOR THE TYPE OF EXPERIMENT
    public static int THRESHOLD = 1;
    public static int BASS = 2;
    public static int COMPLEX = 3;
    public static int BASS_WEIGHTS = 4;
    public static int BASS_WEIGHTS_INITIAL_BASICS = 5;

    // FOR THE TYPE of POLICY
    public final static int RANDOM = 1;
    public final static int MORE_LIKELY = 2;
    public final static int LESS_LIKELY = 3;

    // FOR THE TYPE of TARGETING
    public final static int TARGETING_RANDOM = 1;
    public final static int TARGETING_DEGREE = 2;
    public final static int TARGETING_PREFERENCES = 3;
    public final static int TARGETING_PREFERENCES_DEGREE = 4;

    // ########################################################################
    // Variables
    // ########################################################################
    Date startDate;

    int nrAgents;

    int ratioAgentsPop;

    // NOTE: SF ONLY!!
    // Choose between several social networks:
    // RANDOM_NETWORK_SEGMENTS, random network concentric
    // SCALE_FREE_NETWORK, scale-free (Barabasi)
    // FILE_NETWORK for loading a set of files DGS with the networks
    NetworkType typeOfNetwork;

    String networkFilesPattern;

    int maxKDegree; // max average degree of the network <k> (=density*nAgents)
    double maxDensity; // max density

    boolean staticSN; // make SN static for efficiency purposes
    // (it will ignore prob. add/remove)

    // for the weigths variant
    double weight; // weight for edges of the SN after marketing action
    int weightDuration; // days the weight effect
    double obtainSubscriptionIncrease; // the increase in p (obtain subscription)
    int minPremFriendsToBelongSet; // the minimum number of friends (premium) to belong to the set of users with
    // reward
    int seededUsers; // the users we are rewarding
    int rewardApproach; // the users we are rewarding

    // Type of the experiment
    int experimentType = 0;

    // SEGMENTS
    int nrSegments;

    double[] segmentSizes;
    double[] segmentInitialPercentagePremium; // percentage of premium subscribers see Gamer::{NON_USER, BASIC_USER,
    // PREMIUM_USER}

    // for the social network
    double[] segmentsConnectivity;

    // for the animal gamers (dynamic network)
    double[] segmentDailyProbNewFriend; // probability to have a new friend
    double[] segmentDailyProbLoseFriend; // probability to lose an old friend

    // probs to obtain and leave subscriptions
    double[] segmentDailyProbObtainSubscription; // probability to become a premium subscriptor without friends info
    // double[] segmentDailyProbLeaveSubscription; // probability to leave a premium
    // subscription

    // daily probs to play during the weekend and weekdays
    double[] segmentDailyProbPlayNoWeekend; // probability to play one day (working day, no weekend)
    double[] segmentDailyProbPlayWeekend; // probability to play one day (weekend)

    // rates for the threshold premium contagion (cascade model)
    double[] segmentSocialAdoptionParam;

    // graph read from file
    Graph graphFromFile;

    // Brands params
    int maxDrivers;

    double[] preferences;

    int brands;

    int brandToGive;

    double[][] brandDrivers;

    String[] brandName;

    double[] segmentDailyProbBuy; // probability to buy

    int targetingStrategy; // formas de regalar los productos a los influencers

    double[] initialPercentagePremium;

    double brandStdev;

    double minimunSatisfactionAgend;

    double uncertaintyToleranceLevel;

    double socialPeerfInfluence;

    // --------------------------- Get/Set methods ---------------------------//
    public double getSocialPeerfInfluence() {
        return socialPeerfInfluence;
    }

    public void setSocialPeerfInfluence(double socialPeerfInfluence) {
        this.socialPeerfInfluence = socialPeerfInfluence;
    }

    public double getMinimunSatisfactionAgend() {
        return minimunSatisfactionAgend;
    }

    public void setMinimunSatisfactionAgend(double minimunSatisfactionAgend) {
        this.minimunSatisfactionAgend = minimunSatisfactionAgend;
    }

    public double getUncertaintyToleranceLevel() {
        return uncertaintyToleranceLevel;
    }

    public void setUncertaintyToleranceLevel(double uncertaintyToleranceLevel) {
        this.uncertaintyToleranceLevel = uncertaintyToleranceLevel;
    }

    public double getBrandStdev() {
        return brandStdev;
    }

    public void setBrandStdev(double brandStdev) {
        this.brandStdev = brandStdev;
    }

    public int getBrandToGive() {
        return brandToGive;
    }

    public void setBrandToGive(int brandToGive) {
        this.brandToGive = brandToGive;
    }

    public double[] getInitialPercentagePremium() {
        return initialPercentagePremium;
    }

    public void setInitialPercentagePremium(double[] initialPercentagePremium) {
        this.initialPercentagePremium = initialPercentagePremium;
    }

    public double[] getSegmentDailyProbBuy() {
        return segmentDailyProbBuy;
    }

    public int getTargetingStrategy() {
        return targetingStrategy;
    }

    public void setTargetingStrategy(int targetingStrategy) {
        this.targetingStrategy = targetingStrategy;
    }

    public void setSegmentDailyProbBuy(double[] segmentDailyProbBuy) {
        this.segmentDailyProbBuy = segmentDailyProbBuy;
    }

    public int getMaxDrivers() {
        return maxDrivers;
    }

    public void setMaxDrivers(int maxDrivers) {
        this.maxDrivers = maxDrivers;
    }

    public int getBrands() {
        return brands;
    }

    public void setBrands(int brands) {
        this.brands = brands;
    }

    public void setBrandDrivers(int brand, double[] drivers) {

        this.brandDrivers[brand] = drivers;
    }

    public void setBrandNames(int brand, String name) {

        this.brandName[brand] = name;
    }

    public void setPreferences(double[] _preferences) {
        this.preferences = _preferences;
    }

    public double[] getPreferences() {
        return this.preferences;
    }

    public double[] getBrandDrivers(int brand) {

        return this.brandDrivers[brand];
    }

    public String getBrandNames(int brand) {

        return this.brandName[brand];
    }

    //
    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * @return the weightDuration
     */
    public int getWeightDuration() {
        return weightDuration;
    }

    /**
     * @param weightDuration the weightDuration to set
     */
    public void setWeightDuration(int weightDuration) {
        this.weightDuration = weightDuration;
    }

    /**
     * @return the seededUsers
     */
    public int getSeededUsers() {
        return seededUsers;
    }

    /**
     * @param seededUsers the seededUsers to set
     */
    public void setSeededUsers(int seededUsers) {
        this.seededUsers = seededUsers;
    }

    /**
     * @return the rewardApproach
     */
    public int getRewardApproach() {
        return rewardApproach;
    }

    /**
     * @param rewardApproach the rewardApproach to set
     */
    public void setRewardApproach(int rewardApproach) {
        this.rewardApproach = rewardApproach;
    }

    /**
     * @return the typeOfNetwork
     */
    public NetworkType getTypeOfNetwork() {
        return typeOfNetwork;
    }

    /**
     * @param typeOfNetwork the typeOfNetwork to set
     */
    public void setTypeOfNetwork(NetworkType typeOfNetwork) {
        this.typeOfNetwork = typeOfNetwork;
    }

    /**
     * @return the maxDensity
     */
    public double getMaxDensity() {
        return maxDensity;
    }

    /**
     * @param maxDensity the maxDensity to set
     */
    public void setMaxDensity(double maxDensity) {
        this.maxDensity = maxDensity;
    }

    public double getObtainSubscriptionIncrease() {
        return obtainSubscriptionIncrease;
    }

    public void setObtainSubscriptionIncrease(double obtainSubscriptionIncrease) {
        this.obtainSubscriptionIncrease = obtainSubscriptionIncrease;
    }

    public int getMinPremFriendsToBelongSet() {
        return minPremFriendsToBelongSet;
    }

    public void setMinPremFriendsToBelongSet(int minPremFriendsToBelongSet) {
        this.minPremFriendsToBelongSet = minPremFriendsToBelongSet;
    }

    /**
     * @return the staticSN
     */
    public boolean isStaticSN() {
        return staticSN;
    }

    /**
     * @param staticSN the staticSN to set
     */
    public void setStaticSN(boolean staticSN) {
        this.staticSN = staticSN;
    }

    /**
     * @return the graph
     */
    public Graph getGraph() {
        return graphFromFile;
    }

    /**
     * @param _graph to set
     */
    public void setGraph(Graph _graph) {
        this.graphFromFile = _graph;
    }

    public String getNetworkFilesPattern() {
        return networkFilesPattern;
    }

    public void setNetworkFilesPattern(String networkFilesPattern) {
        this.networkFilesPattern = networkFilesPattern;
    }

    /**
     * @param _graph to set
     * @throws IOException
     */
    public void readGraphFromFile(String fileNameGraph) throws IOException {

        FileSourceDGS fileSource = new FileSourceDGS();
        graphFromFile = new SingleGraph("SNFromFile");

        fileSource.addSink(graphFromFile);
        fileSource.readAll(fileNameGraph);

        fileSource.removeSink(graphFromFile);
    }

    /**
     * @return the ratioAgentsPop
     */
    public int getRatioAgentsPop() {
        return ratioAgentsPop;
    }

    /**
     * @param ratioAgentsPop the ratioAgentsPop to set
     */
    public void setRatioAgentsPop(int ratioAgentsPop) {
        this.ratioAgentsPop = ratioAgentsPop;
    }

    /**
     * Gets the max average degree K for the social network
     *
     * @return
     */
    public int getMaxKDegree() {
        return maxKDegree;
    }

    /**
     * Sets the max average degree K for the social network
     *
     * @return
     */
    public void setMaxKDegree(int _maxDegree) {
        maxKDegree = _maxDegree;
    }

    /**
     * Gets the type of the graph.
     *
     * @return
     */
    public NetworkType getTypeOfGraph() {
        return typeOfNetwork;
    }

    /**
     * Sets the type of the graph.
     *
     * @param typeOfGraph
     */
    public void setTypeOfGraph(NetworkType typeOfGraph) {
        this.typeOfNetwork = typeOfGraph;
    }

    /**
     * Gets the number of agents.
     *
     * @return
     */
    public int getNrAgents() {
        return nrAgents;
    }

    /**
     * Sets the number of agents
     *
     * @param nrAgents
     */
    public void setNrAgents(int nrAgents) {
        if (nrAgents > 0) {
            this.nrAgents = nrAgents;
        }
    }

    /**
     * Gets the experiment type.
     *
     * @return
     */
    public int getExperimentType() {
        return experimentType;
    }

    /**
     * Sets the experiment type.
     *
     * @param experimentType
     */
    public void setExperimentType(int experimentType) {
        this.experimentType = experimentType;
    }

    /**
     * Gets the number of segments.
     *
     * @return
     */
    public int getNrSegments() {
        return nrSegments;
    }

    /**
     * Sets the number of segments.
     *
     * @param nrSegments
     */
    public void setNrSegments(int nrSegments) {
        this.nrSegments = nrSegments;
    }

    /**
     * Gets the segment sizes.
     *
     * @return
     */
    public double[] getSegmentSizes() {
        return segmentSizes;
    }

    /**
     * Sets the segment sizes.
     *
     * @param segmentSizes
     */
    public void setSegmentSizes(double[] segmentSizes) {
        this.segmentSizes = segmentSizes;
    }

    /**
     * Gets the segments connectivity.
     *
     * @return
     */
    public double[] getSegmentsConnectivity() {
        return segmentsConnectivity;
    }

    /**
     * Sets the segments connectivity.
     *
     * @param segmentsConnectivity
     */
    public void setSegmentsConnectivity(double[] segmentsConnectivity) {
        this.segmentsConnectivity = segmentsConnectivity;
    }

    /**
     * Gets the segment prob new friend probabilities.
     *
     * @return
     */
    public double[] getSegmentDailyProbNewFriend() {
        return segmentDailyProbNewFriend;
    }

    /**
     * Sets the segment prob new friend probabilities.
     *
     * @param segmentTalkingProbabilities
     */
    public void setSegmentDailyProbNewFriend(double[] _segmentDailyProbNewFriend) {
        this.segmentDailyProbNewFriend = _segmentDailyProbNewFriend;
    }

    /**
     * Gets the prob. of losing a friend weekly
     *
     * @return
     */
    public double[] getSegmentDailyProbLoseFriend() {
        return segmentDailyProbLoseFriend;
    }

    /**
     * Sets the prob. of losing a friend weekly
     *
     * @param segmentAwarenessDecay
     */
    public void setSegmentDailyProbLoseFriend(double[] _segmentDailyProbLoseFriend) {
        this.segmentDailyProbLoseFriend = _segmentDailyProbLoseFriend;
    }

    /**
     * Gets the segment prob for becoming a premium subscriptor without looking
     * at friends
     *
     * @return
     */
    public double[] getSegmentDailyProbObtainSubscription() {
        return segmentDailyProbObtainSubscription;
    }

    /**
     * Sets the segment prob for becoming a premium subscriptor without looking
     * at friends
     *
     * @param segmentDailyProbObtainSubscription
     */
    public void setSegmentDailyProbObtainSubscription(double[] _segmentDailyProbObtainSubscription) {
        this.segmentDailyProbObtainSubscription = _segmentDailyProbObtainSubscription;
    }

    /**
     * Gets the segment prob leaving a subscription
     *
     * @return
     */
    /*
     * public double[] getSegmentWeeklyProbLeaveSubscription() { return
     * segmentWeeklyProbLeaveSubscription; }
     */
    /**
     * Sets the segment prob leaving a subscription
     *
     * @param segmentWeeklyProbLeaveSubscription
     */

    /*
     * public void setSegmentWeeklyProbLeaveSubscription(double[]
     * _segmentWeeklyProbLeaveSubscription) {
     * this.segmentWeeklyProbLeaveSubscription =
     * _segmentWeeklyProbLeaveSubscription; }
     */
    /**
     * @return the segmentDailyProbPlayNoWeekend
     */
    public double[] getSegmentDailyProbPlayNoWeekend() {
        return segmentDailyProbPlayNoWeekend;
    }

    /**
     * @param segmentDailyProbPlayNoWeekend the segmentDailyProbPlayNoWeekend to
     * set
     */
    public void setSegmentDailyProbPlayNoWeekend(double[] segmentDailyProbPlayNoWeekend) {
        this.segmentDailyProbPlayNoWeekend = segmentDailyProbPlayNoWeekend;
    }

    /**
     * @return the segmentDailyProbPlayWeekend
     */
    public double[] getSegmentDailyProbPlayWeekend() {
        return segmentDailyProbPlayWeekend;
    }

    /**
     * @param segmentDailyProbPlayWeekend the segmentDailyProbPlayWeekend to set
     */
    public void setSegmentDailyProbPlayWeekend(double[] segmentDailyProbPlayWeekend) {
        this.segmentDailyProbPlayWeekend = segmentDailyProbPlayWeekend;
    }

    /**
     * @return the segmentSocialAdoptionParam
     */
    public double[] getSegmentSocialAdoptionParam() {
        return segmentSocialAdoptionParam;
    }

    /**
     * @param segmentSocialAdoptionParam the segmentSocialAdoptionParam to set
     */
    public void setSegmentSocialAdoptionParam(double[] segmentProbSimpleSocialAdoption) {
        this.segmentSocialAdoptionParam = segmentProbSimpleSocialAdoption;
    }

    /**
     * Gets the initial subscription state of the agents
     *
     * @return
     */
    public double[] getSegmentInitialPercentagePremium() {
        return segmentInitialPercentagePremium;
    }

    /**
     * Sets the initial subscription state of the agents
     *
     * @param _segmentInitialPercentagePremium
     */
    public void setSegmentInitialPercentagePremium(double[] _segmentInitialPercentagePremium) {
        this.segmentInitialPercentagePremium = _segmentInitialPercentagePremium;
    }

    public double getSegmentDailyProbPlayWeekend(int k) {

        return this.segmentDailyProbPlayWeekend[k];
    }

    public double getSegmentDailyProbPlayNoWeekend(int k) {

        return this.segmentDailyProbPlayNoWeekend[k];
    }

    public double getSegmentSocialAdoptionParam(int k) {
        return this.segmentSocialAdoptionParam[k];
    }

    public double getSegmentDailyProbLoseFriend(int k) {
        return this.segmentDailyProbLoseFriend[k];
    }

    public double getSegmentDailyProbObtainSubscription(int k) {
        return this.segmentDailyProbObtainSubscription[k];
    }

    public double getSegmentConnectivity(int k) {
        return this.segmentsConnectivity[k];
    }

    public double getSegmentDailyProbNewFriend(int k) {
        return this.segmentDailyProbNewFriend[k];
    }

    // ########################################################################
    // Constructors
    // ########################################################################
    /**
     *
     */
    public ModelParameters() {

    }

    // ########################################################################
    // Export methods
    // ########################################################################
    public String export() {

        String values = "";

        values += exportGeneral();

        if (this.experimentType == BASS_WEIGHTS || this.experimentType == BASS_WEIGHTS_INITIAL_BASICS) {
            values += exportWeights();
        }

        values += exportSN();

        values += exportSegments();

        values += exportInitialPremiums();

        // values += exportConnectivity();
        values += exportProbNewFriend();
        values += exportProbLoseFriend();

        values += exportProbPlayWeekend();
        values += exportProbPlayNoWeekend();

        values += exportProbObtainSubscription();
        values += exportSocialAdoptionParam();

        return values;
    }

    private String exportInitialPremiums() {
        String result = "";

        result += "segmentInitialPercentagePremium=";

        int k;
        for (k = 0; k < segmentSizes.length - 1; k++) {
            result += segmentInitialPercentagePremium[k] + ",";
        }

        result += segmentInitialPercentagePremium[k] + "\n";

        return result;
    }

    private String exportConnectivity() {
        String result = "";

        result += "segmentsConnectivity=";

        int k;
        for (k = 0; k < segmentSizes.length - 1; k++) {
            result += this.segmentsConnectivity[k] + ",";
        }

        result += this.segmentsConnectivity[k] + "\n";

        return result;
    }

    private String exportProbNewFriend() {
        String result = "";

        result += "segmentDailyProbNewFriend=";

        int k;
        for (k = 0; k < segmentSizes.length - 1; k++) {
            result += this.segmentDailyProbNewFriend[k] + ",";
        }

        result += this.segmentDailyProbNewFriend[k] + "\n";

        return result;
    }

    private String exportProbLoseFriend() {
        String result = "";

        result += "segmentDailyProbLoseFriend=";

        int k;
        for (k = 0; k < segmentSizes.length - 1; k++) {
            result += this.segmentDailyProbLoseFriend[k] + ",";
        }

        result += this.segmentDailyProbLoseFriend[k] + "\n";

        return result;
    }

    private String exportProbPlayWeekend() {
        String result = "";

        result += "segmentDailyProbPlayWeekend=";

        int k;
        for (k = 0; k < segmentSizes.length - 1; k++) {
            result += this.segmentDailyProbPlayWeekend[k] + ",";
        }

        result += this.segmentDailyProbPlayWeekend[k] + "\n";

        return result;
    }

    private String exportProbPlayNoWeekend() {
        String result = "";

        result += "segmentDailyProbPlayNoWeekend=";

        int k;
        for (k = 0; k < segmentSizes.length - 1; k++) {
            result += this.segmentDailyProbPlayNoWeekend[k] + ",";
        }

        result += this.segmentDailyProbPlayNoWeekend[k] + "\n";

        return result;
    }

    private String exportProbObtainSubscription() {
        String result = "";

        result += "segmentDailyProbObtainSubscription=";

        int k;
        for (k = 0; k < segmentSizes.length - 1; k++) {
            result += this.segmentDailyProbObtainSubscription[k] + ",";
        }

        result += this.segmentDailyProbObtainSubscription[k] + "\n";

        return result;
    }

    private String exportSocialAdoptionParam() {
        String result = "";

        result += "segmentSocialAdoptionParam=";

        int k;
        for (k = 0; k < segmentSizes.length - 1; k++) {
            result += this.segmentSocialAdoptionParam[k] + ",";
        }

        result += this.segmentSocialAdoptionParam[k] + "\n";

        return result;
    }

    private String exportSegments() {

        String result = "";

        result += "nrSegments=" + this.nrSegments + "\n";

        result += "segmentSizes=";
        int k;
        for (k = 0; k < segmentSizes.length - 1; k++) {
            result += segmentSizes[k] + ",";
        }

        result += segmentSizes[k] + "\n";

        return result;
    }

    private String exportWeights() {

        String result = "";

        result += "weight=" + this.weight + "\n";

        result += "weightDuration=" + this.weightDuration + "\n";

        result += "obtainSubscriptionIncrease=" + this.obtainSubscriptionIncrease + "\n";
        result += "minPremFriendsToBelongSet=" + this.minPremFriendsToBelongSet + "\n";

        switch (this.rewardApproach) {
            case MORE_LIKELY:
                result += "rewardApproach=more_likely\n";
                break;
            case LESS_LIKELY:
                result += "rewardApproach=less_likely\n";
                break;
            case RANDOM:
                result += "rewardApproach=random\n";
                break;
        }

        result += "seededUsers=" + this.seededUsers + "\n";

        return result;
    }

    private String exportSN() {

        String result = "";

        if (this.typeOfNetwork == GraphStreamer.NetworkType.RANDOM_NETWORK_SEGMENTS) {
            result += "typeOfNetwork=1\n";
        } else if (this.typeOfNetwork == GraphStreamer.NetworkType.SCALE_FREE_NETWORK) {
            result += "typeOfNetwork=0\n";
        } else if (this.typeOfNetwork == GraphStreamer.NetworkType.FILE_NETWORK) {

            result += "typeOfNetwork=2\n";
            result += "networkFilesPattern=" + this.networkFilesPattern + "\n";
        }

        result += "maxDensity=" + this.maxDensity + "\n";
        result += "staticSN=" + this.staticSN + "\n";

        return result;
    }

    private String exportGeneral() {

        String result = "";

        result += "nrAgents=" + this.nrAgents + "\n";
        ;
        result += "ratioAgentPop=" + this.ratioAgentsPop + "\n";
        ;

        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        result += "startDate=" + formatDate.format(this.startDate) + "\n";

        if (this.experimentType == ModelParameters.BASS) {
            result += "experimentType=2\n";
        } else if (this.experimentType == ModelParameters.THRESHOLD) {
            result += "experimentType=1\n";
        } else if (this.experimentType == ModelParameters.COMPLEX) {
            result += "experimentType=3\n";
        } else if (this.experimentType == ModelParameters.BASS_WEIGHTS) {
            result += "experimentType=4\n";
        } else if (this.experimentType == ModelParameters.BASS_WEIGHTS_INITIAL_BASICS) {
            result += "experimentType=5\n";
        }

        return result;
    }

    // ------------------------- Config file methods -------------------------//
    /**
     * Reads parameters from the configuration file.
     */
    public void readParameters(String CONFIGFILENAME) {

        try {

            // Read parameters from the file
            config = new ConfigFileReader(CONFIGFILENAME);

            config.readConfigFile();

            // Get global parameters
            experimentType = config.getParameterInteger("experimentType");

            // if (experimentType == TARGETING) {
            setBrands(this.brands = config.getParameterInteger("brands"));
            setMaxDrivers(this.maxDrivers = config.getParameterInteger("drivers"));

            this.brandDrivers = new double[this.brands][this.maxDrivers];

            setTargetingStrategy(config.getParameterInteger("targetingStrategy"));

            this.initialPercentagePremium = new double[this.brands];

            setInitialPercentagePremium(config.getParameterDoubleArray("initialPercentagePremium"));

            setBrandToGive(this.brandToGive = config.getParameterInteger("brandToGive"));

            setBrandStdev(this.brandStdev = config.getParameterDouble("brand.stdev"));

            setMinimunSatisfactionAgend(this.minimunSatisfactionAgend = config.getParameterDouble("minimunSatisfactionAgend"));

            setUncertaintyToleranceLevel(this.uncertaintyToleranceLevel = config.getParameterDouble("uncertaintyToleranceLevel"));

            setSocialPeerfInfluence(this.socialPeerfInfluence = config.getParameterDouble("socialPeerfInfluence"));
            
            this.brandName = new String[this.brands];

            this.preferences = new double[this.maxDrivers];

            if (config.checkIfExist("model.preferences")) {
                setPreferences(config.getParameterDoubleArray("model.preferences"));
            }

            for (int i = 0; i < this.brands; i++) {
                if (config.checkIfExist("brand." + i + ".drivers")) {
                    this.setBrandDrivers(i, config.getParameterDoubleArray("brand." + i + ".drivers"));
                }
                if (config.checkIfExist("brand." + i + ".name")) {
                    this.setBrandNames(i, config.getParameterString("brand." + i + ".name"));
                }
            }

            // }
            startDate = config.getParameterDate("startDate");

            // Get social network parameters
            if (config.getParameterInteger("typeOfNetwork") == 0) {

                typeOfNetwork = NetworkType.SCALE_FREE_NETWORK;

                setSegmentsConnectivity(config.getParameterDoubleArray("segmentsConnectivity"));

                this.maxDensity = config.getParameterDouble("maxDensity");
                this.maxKDegree = (int) (this.maxDensity * this.nrAgents);

            } else if (config.getParameterInteger("typeOfNetwork") == 1) {

                typeOfNetwork = NetworkType.RANDOM_NETWORK_SEGMENTS;

                setSegmentsConnectivity(config.getParameterDoubleArray("segmentsConnectivity"));

                this.maxDensity = config.getParameterDouble("maxDensity");

                this.maxKDegree = (int) (this.maxDensity * this.nrAgents);

            } else if (config.getParameterInteger("typeOfNetwork") == 2) {

                typeOfNetwork = NetworkType.FILE_NETWORK;
                setNetworkFilesPattern(config.getParameterString("networkFilesPattern"));
                this.readGraphFromFile(networkFilesPattern);
            }

            // get the static SN parameter
            setStaticSN(false); // by default
            setStaticSN(config.getParameterBoolean("staticSN"));

            if (experimentType == BASS_WEIGHTS) {

                setWeight(config.getParameterDouble("weight"));
                setWeightDuration(config.getParameterInteger("weightDuration"));

                if (isStaticSN()) {
                    System.out.println("WARNING: SN was set to static. The use of weights needs a dynamic SN option.\n"
                            + "Therefore, staticSN is set to false");

                    setStaticSN(false);
                }
            }

            if (experimentType == BASS_WEIGHTS_INITIAL_BASICS) {

                setWeight(config.getParameterDouble("weight"));
                setObtainSubscriptionIncrease(config.getParameterDouble("obtainSubscriptionIncrease"));
                setMinPremFriendsToBelongSet(config.getParameterInteger("minPremFriendsToBelongSet"));

                if (isStaticSN()) {
                    System.out.println("WARNING: SN was set to static. The use of weights needs a dynamic SN option.\n"
                            + "Therefore, staticSN is set to false");

                    setStaticSN(false);
                }

                setSeededUsers(config.getParameterInteger("seededUsers"));
                setRewardApproach(config.getParameterInteger("rewardApproach"));
            }

            // Set segment parameters
            setNrSegments(config.getParameterInteger("nrSegments"));
            setSegmentSizes(config.getParameterDoubleArray("segmentSizes"));
            setSegmentInitialPercentagePremium(config.getParameterDoubleArray("segmentInitialPercentagePremium"));

            // Segments parameters for the Animal game (dynamic social network)
            setSegmentDailyProbNewFriend(config.getParameterDoubleArray("segmentDailyProbNewFriend"));
            setSegmentDailyProbLoseFriend(config.getParameterDoubleArray("segmentDailyProbLoseFriend"));

            // Segments parameters for the Animal game (simple contagion)
            // setSegmentWeeklyProbLeaveSubscription(config.getParameterDoubleArray("segmentWeeklyProbLeaveSubscription"));
            setSegmentDailyProbObtainSubscription(config.getParameterDoubleArray("segmentDailyProbObtainSubscription"));

            // Segments parameters for playing the Animal game daily
            setSegmentDailyProbPlayNoWeekend(config.getParameterDoubleArray("segmentDailyProbPlayNoWeekend"));
            setSegmentDailyProbPlayWeekend(config.getParameterDoubleArray("segmentDailyProbPlayWeekend"));

            // Segments parameters for the Animal game (simple contagion)
            setSegmentSocialAdoptionParam(config.getParameterDoubleArray("segmentSocialAdoptionParam"));

            nrAgents = config.getParameterInteger("nrAgents");
            ratioAgentsPop = config.getParameterInteger("ratioAgentPop");

            // setSegmentDailyProbBuy(config.getParameterDoubleArray("segmentDailyProbBuy"));
        } catch (IOException e) {

            System.err.println("Error with SN file when loading parameters for the simulation " + CONFIGFILENAME + "\n"
                    + e.getMessage());
            e.printStackTrace(new PrintWriter(System.err));
        }
    }

    // ----------------------------- I/O methods -----------------------------//
    /**
     * Prints simple statistics evolution during the time.
     */
    public void printParameters(PrintWriter writer) {

        // printing general params
        writer.println(this.export());

    }

}
