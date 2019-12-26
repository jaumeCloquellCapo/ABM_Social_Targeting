package model;

import sim.engine.*;

import java.util.*;
import org.apache.commons.lang3.CharSetUtils;
import static org.apache.commons.lang3.time.DateUtils.round;
import static util.Functions.sumDoubleArray;

/**
 * Defines a gamer for ABM model.
 *
 * @author mchica
 *
 */
public class GamerAgent implements Steppable {

    // ########################################################################
    // Variables
    // ########################################################################
    private static final long serialVersionUID = 1L;

    // --------------------------------- Fixed -------------------------------//
    // behaviours of the agent
    // A unique gamer Id
    int gamerAgentId;

    // A unique segment Id to access to their segments parameters
    int segmentId;

    // ------------------------------- Dynamic -------------------------------//
    // Current state of the user (premium, free or non user (initialization))
    int subscriptionState = Model.NON_USER;

    double[] preferences;

    int purchasedBrands[]; // array with the brand obtained at each step
    int strategy[]; // array with strategy used
    int currentStep;
    double utility[];

    // ########################################################################
    // Constructors
    // ########################################################################
    /**
     * Initializes a new instance of the ClientAgent class.
     *
     * @param _gamerId
     * @param _segId
     * @param _subscriptionState
     * @param _preferences
     * @param _maxSteps
     */
    public GamerAgent(int _gamerId, int _segId, int _subscriptionState, int _preferences, int _maxSteps, int brands) {

        this.gamerAgentId = _gamerId;
        this.segmentId = _segId;
        this.subscriptionState = _subscriptionState;
        this.preferences = new double[(_preferences)];
        this.purchasedBrands = new int[_maxSteps];
        this.strategy = new int[_maxSteps];

        for (int i = 0; i < _maxSteps; i++) {
            this.purchasedBrands[i] = -1;
            this.strategy[i] = -1;
        }

        this.utility = new double[brands];

        // PropertyConfigurator.configure(Model.getLogFileName());
    }

    public double[] getUtility() {
        return utility;
    }

    public void setUtility(int index, double utility) {

        double aux = (double) Math.round(utility * 100) / 100;

        if (aux < 0.0 || aux > 1.0) {
            System.err.println("Error: Invalid utility: " + aux);
        }
        this.utility[index] = aux;
    }

    public int[] getPurchasedBrands() {
        return purchasedBrands;
    }

    public void setPurchasedBrands(int _index, int brandId) {
        this.purchasedBrands[_index] = brandId;
    }

    public void setStrategy(int _index, int strategy) {
        this.strategy[_index] = strategy;
    }

    public int getPurchasedBrandsBySpecificStep(int _index) {
        return this.purchasedBrands[_index];
    }

    public double[] getPreferences() {
        return preferences;
    }

    public void setPreferences(int _preference, double _value) {
        double aux = (double) Math.round(_value * 100) / 100;
        if (aux < 0.0 || aux > 1.0) {
            System.err.println("Error :Invalid preference: " + aux);
        }
        this.preferences[_preference] = aux;
    }

    // ########################################################################
    // Methods/Functions
    // ########################################################################
    // --------------------------- Get/Set methods ---------------------------//
    /**
     * Gets the id of the user gamer.
     *
     * @return
     */
    public int getGamerAgentId() {
        return gamerAgentId;
    }

    /**
     * Sets the id of the gamer
     *
     * @param _gamerAgentId
     */
    public void setGamerAgentId(int _gamerAgentId) {
        this.gamerAgentId = _gamerAgentId;
    }

    /**
     * Gets the id of the social group.(segment Id)
     *
     * @return - the id of the social group (segment Id) it belongs to.
     */
    public int getSegmentId() {
        return segmentId;
    }

    /**
     * Sets the id of the social group (segment Id)
     *
     * @param _socialGroupId - the id of the social group (segment Id) it
     * belongs to.
     */
    public void setSegmentId(int _segId) {
        this.segmentId = _segId;
    }

    /**
     * Gets the state of the user subscription
     *
     * @return - the state of the membership user
     */
    public int getSubscriptionState() {
        return subscriptionState;
    }

    /**
     * Sets the membership state of the user
     *
     * @param _subscriptionState - the state of the subscription
     */
    public void setSubscriptionState(int _subscriptionState) {
        this.subscriptionState = _subscriptionState;
    }

    /**
     * Sets the new list of neighbors
     *
     * @param _neighbors - the new list of neighbors
     */
    /*
     * public void setNeighbors(ArrayList<Integer> _neighbors) { this.neighbors =
     * _neighbors; }
     */
    /**
     * Gets the new list of neighbors
     *
     * @param _neighbors - the new list of neighbors
     */

    /*
     * public ArrayList<Integer> getNeighbors() { return this.neighbors; }
     */
    // -------------------------- Diffusion methods --------------------------//
    /**
     * TODO check this adoption Models the simple contagion of the premium
     * subscription (cascade but checking all the contacts every day).
     *
     * @param state - a simulation model object (SimState).
     * @return true if the agent adopted subscription
     */
    public boolean simpleAdoptionFromFriends(SimState state) {

        Model model = (Model) state;

        ArrayList<Integer> neighbors = (ArrayList<Integer>) model.socialNetwork.getNeighborsOfNode(this.gamerAgentId);

        // Iterate over neighbors
        for (int i = 0; i < neighbors.size(); i++) {

            GamerAgent neighbor = (GamerAgent) (model.getAgents()).get(neighbors.get(i));

            if (neighbor.getSubscriptionState() == Model.PREMIUM_USER) {

                // check probability for contagion
                double probs[] = ((ModelParameters) model.getParametersObject()).getSegmentSocialAdoptionParam();

                double r = model.random.nextDouble(Model.INCLUDEZERO, Model.INCLUDEONE);

                // System.out.println("Another premium neighbour of agent " + this.gamerAgentId
                // +
                // ". Prob is " + r + " and the probability is "+ simpleProbs[this.segmentId]);
                // Check if the agent becomes a premium subscriptor
                if (r < probs[this.segmentId]) {

                    subscriptionState = Model.PREMIUM_USER;

                    // update the number of current subscriptors
                    // model.increasePremium();
                    // System.out.println("Changed to premium!");
                    // Model.logger.info("[++sS] simpleAdoptionFromFriends() new premium gamer
                    // because of friends (Simple)!");
                    return true;
                }
            }
        }

        return false;

    }

    /**
     * Models the thresholds contagion of the premium subscription. It is based
     * on the rate of friends of the agents. If the agent has a higher rate of
     * friends than the threshold --> it is converted
     *
     * @param state - a simulation model object (SimState).
     * @return true if the agent adopted subscription
     */
    public boolean thresholdAdoptionFromFriends(SimState state) {

        Model model = (Model) state;

        ArrayList<Integer> neighbors = (ArrayList<Integer>) model.socialNetwork.getNeighborsOfNode(this.gamerAgentId);

        // Iterate over neighbors
        int noOfSubscribedFriends = 0;
        for (int i = 0; i < neighbors.size(); i++) {

            GamerAgent neighbor = (GamerAgent) (model.getAgents()).get(neighbors.get(i));

            if (neighbor.getSubscriptionState() == Model.PREMIUM_USER) {
                noOfSubscribedFriends++;
            }
        }

        // calculate rate of subscribed friends
        double rateFriends = 0.;
        rateFriends = (double) noOfSubscribedFriends / neighbors.size();

        double thresholds[] = ((ModelParameters) model.getParametersObject()).getSegmentSocialAdoptionParam();

        /*
         * System.out.println("The rate of premium neighbour of agent " +
         * this.gamerAgentId + " is " + rateFriends + " because there were " +
         * noOfSubscribedFriends + " and the size is " + neighbors.size());
         */
        // Check if the agent becomes a premium subscriptor
        if (rateFriends >= thresholds[this.segmentId]) {
            subscriptionState = Model.PREMIUM_USER;

            // update the number of current subscriptors
            // model.increasePremium();
            // System.out.println("Changed to premium!");
            // Model.logger.info("[++tS] thresholdAdoptionFromFriends() new premium gamer
            // because of friends (THRESHOLD)!");
            return true;
        }

        return false;
    }

    /**
     * Models the Bass model adoption for the premium subscription. It is based
     * on the rate of friends of the agents and an adoption coefficient.
     *
     * @param state - a simulation model object (SimState).
     * @return true if the agent adopted subscription
     */
    public boolean bassAdoptionFromFriends(SimState state) {

        Model model = (Model) state;

        ArrayList<Integer> neighbors = (ArrayList<Integer>) model.socialNetwork.getNeighborsOfNode(this.gamerAgentId);

        // Iterate over neighbors
        int noOfSubscribedFriends = 0;
        for (int i = 0; i < neighbors.size(); i++) {

            GamerAgent neighbor = (GamerAgent) (model.getAgents()).get(neighbors.get(i));

            if (neighbor.getSubscriptionState() == Model.PREMIUM_USER) {
                noOfSubscribedFriends++;
            }
        }

        // calculate rate of subscribed friends
        double rateFriends = (double) noOfSubscribedFriends / neighbors.size();

        // get the coefficient of imitation
        double coefficients[] = ((ModelParameters) model.getParametersObject()).getSegmentSocialAdoptionParam();

        // obtain probability by multiplying coefficient & rate of friends
        double prob = rateFriends * coefficients[this.segmentId];

        /*
         * System.out.println("The rate of premium neighbour of agent " +
         * this.gamerAgentId + " is " + rateFriends + " because there were " +
         * noOfSubscribedFriends + " and the size is " + neighbors.size());
         */
        double r = model.random.nextDouble(Model.INCLUDEZERO, Model.INCLUDEONE);

        // Check if the agent becomes a premium subscriptor
        if (r < prob) {
            subscriptionState = Model.PREMIUM_USER;

            // update the number of current subscriptors
            // model.increasePremium();
            // System.out.println("Changed to premium!");
            // Model.logger.info("[++bS] bassAdoptionFromFriends() new premium gamer because
            // of friends (BASS)!");
            return true;
        }

        return false;
    }

    /**
     * Models the Bass model adoption for the premium subscription BUT
     * considering the weights of the friends.
     *
     * It is based on the rate of friends of the agents and an adoption
     * coefficient.
     *
     * @param state - a simulation model object (SimState).
     * @return true if the agent adopted subscription
     */
    public boolean bassAdoptionFromFriendsUsingWeights(SimState state) {

        Model model = (Model) state;

        ArrayList<Integer> neighbors = (ArrayList<Integer>) model.socialNetwork.getNeighborsOfNode(this.gamerAgentId);

        // Iterate over neighbors
        double noOfSubscribedFriends = 0;
        for (int i = 0; i < neighbors.size(); i++) {

            GamerAgent neighbor = (GamerAgent) (model.getAgents()).get(neighbors.get(i));

            if (neighbor.getSubscriptionState() == Model.PREMIUM_USER) {

                // check if there is a weight between these 2 nodes
                // and if it is still valid (duration)
                if (model.socialNetwork.getAttributeEdge(this.gamerAgentId, neighbors.get(i), "duration") != null
                        && (int) model.socialNetwork.getAttributeEdge(this.gamerAgentId, neighbors.get(i),
                                "duration") >= model.schedule.getSteps()) {

                    // get the weight to amplify the ratio of subscribed friends
                    double weight = (double) model.socialNetwork.getAttributeEdge(neighbors.get(i), this.gamerAgentId,
                            "weight");

                    noOfSubscribedFriends = noOfSubscribedFriends + weight;

                    /*
                     * System.out.println("Neighs of " + this.gamerAgentId + ". Duration is " +
                     * (int)model.socialNetwork.getAttributeEdge (this.gamerAgentId,
                     * neighbors.get(i), "duration") + " and today is " +
                     * model.schedule.getSteps());
                     * 
                     * System.out.println("noOfSubscribedFriends is now " + noOfSubscribedFriends);
                     */
                } else {

                    // no weight, just 1 count
                    noOfSubscribedFriends = noOfSubscribedFriends + 1;
                }
            }
        }

        // to check if it is greater than 1
        if (noOfSubscribedFriends > neighbors.size()) {
            noOfSubscribedFriends = neighbors.size();
        }

        // calculate rate of subscribed friends
        double rateFriends = (double) noOfSubscribedFriends / neighbors.size();

        // get the coefficient of imitation
        double coefficients[] = ((ModelParameters) model.getParametersObject()).getSegmentSocialAdoptionParam();

        // obtain probability by multiplying coefficient & rate of friends
        double prob = rateFriends * coefficients[this.segmentId];

        /*
         * System.out.println("The rate of premium neighbour of agent " +
         * this.gamerAgentId + " is " + rateFriends + " because there were " +
         * noOfSubscribedFriends + " and p=" + coefficients[this.segmentId]);
         */
        double r = model.random.nextDouble(Model.INCLUDEZERO, Model.INCLUDEONE);

        // Check if the agent becomes a premium subscriptor
        if (r < prob) {
            subscriptionState = Model.PREMIUM_USER;

            // update the number of current subscriptors
            // model.increasePremium();
            // System.out.println("Changed to premium!");
            // Model.logger.info("[++bS] bassAdoptionFromFriends() new premium gamer because
            // of friends (BASS)!");
            return true;
        }

        return false;
    }

    /**
     * Models the Bass model adoption for the premium subscription BUT
     * considering the weights of the friends of just an initial set of basic
     * users to be rewarded.
     *
     * It is based on the rate of friends of the agents and an adoption
     * coefficient.
     *
     * @param state - a simulation model object (SimState).
     * @return true if the agent adopted subscription
     */
    public boolean bassAdoptionFromFriendsUsingWeightsForInitialUsers(SimState state) {

        Model model = (Model) state;

        ArrayList<Integer> neighbors = (ArrayList<Integer>) model.socialNetwork.getNeighborsOfNode(this.gamerAgentId);

        // Iterate over neighbors
        double noOfSubscribedFriends = 0;
        for (int i = 0; i < neighbors.size(); i++) {

            GamerAgent neighbor = (GamerAgent) (model.getAgents()).get(neighbors.get(i));

            if (neighbor.getSubscriptionState() == Model.PREMIUM_USER) {

                // check if there is a weight between these 2 nodes
                // we just need to see if the neighbour was included in the set
                if (model.isAnInitialRewardedBasicUser(neighbors.get(i))) {

                    // get the weight to amplify the ratio of subscribed friends
                    double weight = model.getParametersObject().getWeight();

                    noOfSubscribedFriends = noOfSubscribedFriends + weight;

                    /*
                     * System.out.println("Neigh " + neighbors.get(i) +
                     * " was initially rewarded so its weight is " + weight);
                     * 
                     * System.out.println("noOfSubscribedFriends is now " + noOfSubscribedFriends);
                     */
                } else {

                    // no weight, just 1 count
                    noOfSubscribedFriends = noOfSubscribedFriends + 1;
                }
            }
        }

        // to check if it is greater than 1
        if (noOfSubscribedFriends > neighbors.size()) {
            noOfSubscribedFriends = neighbors.size();
        }

        // calculate rate of subscribed friends
        double rateFriends = (double) noOfSubscribedFriends / neighbors.size();

        // get the coefficient of imitation
        double coefficients[] = ((ModelParameters) model.getParametersObject()).getSegmentSocialAdoptionParam();

        // obtain probability by multiplying coefficient & rate of friends
        double prob = rateFriends * coefficients[this.segmentId];

        /*
         * System.out.println("The rate of premium neighbour of agent " +
         * this.gamerAgentId + " is " + rateFriends + " because there were " +
         * noOfSubscribedFriends + " and p=" + coefficients[this.segmentId]);
         */
        double r = model.random.nextDouble(Model.INCLUDEZERO, Model.INCLUDEONE);

        // Check if the agent becomes a premium subscriptor
        if (r < prob) {
            subscriptionState = Model.PREMIUM_USER;

            // update the number of current subscriptors
            // model.increasePremium();
            // System.out.println("Changed to premium!");
            // Model.logger.info("[++bS] bassAdoptionFromFriends() new premium gamer because
            // of friends (BASS)!");
            return true;
        }

        return false;
    }

    /**
     * Models the complex model adoption (Chandola 2007) for the premium
     * subscription. It is based on the need of multiple sources to finally
     * adopt. Basically, a user converts if there are at least 'a' premium
     * friends. Otherwise, it does not convert.
     *
     * @param state - a simulation model object (SimState).
     * @return true if the agent adopted subscription
     */
    public boolean complexAdoptionFromFriends(SimState state) {

        Model model = (Model) state;

        ArrayList<Integer> neighbors = (ArrayList<Integer>) model.socialNetwork.getNeighborsOfNode(this.gamerAgentId);

        // Iterate over neighbors
        int noOfSubscribedFriends = 0;
        for (int i = 0; i < neighbors.size(); i++) {

            GamerAgent neighbor = (GamerAgent) (model.getAgents()).get(neighbors.get(i));

            if (neighbor.getSubscriptionState() == Model.PREMIUM_USER) {
                noOfSubscribedFriends++;
            }
        }

        // get the coefficients from the parameters of the model
        double coefficients[] = ((ModelParameters) model.getParametersObject()).getSegmentSocialAdoptionParam();

        double minimumFriends = coefficients[this.segmentId];

        // System.out.println("Agent " + this.gamerAgentId +
        // " has " + noOfSubscribedFriends + " prems of " + neighbors.size()
        // + ". And the minimum is " + minimumFriends);
        // Check if the agent becomes a premium subscriptor
        if (noOfSubscribedFriends >= minimumFriends) {

            subscriptionState = Model.PREMIUM_USER;

            // update the number of current subscriptors
            // model.increasePremium();
            // System.out.println("--> Changed to premium!" );
            // Model.logger.info("[++bS] bassAdoptionFromFriends() new premium gamer because
            // of friends (BASS)!");
            return true;
        }

        return false;
    }

    /**
     * Models the premium conversion by innovation in the subscription state It
     * does not consider the social network of agents
     *
     * @param state - a simulation model object (SimState).
     * @return true if new subscription
     */
    public boolean obtainSubscriptionByInnovation(SimState state) {

        Model model = (Model) state;

        double[] weeklyProbObtainSubscription = ((ModelParameters) model.getParametersObject())
                .getSegmentDailyProbObtainSubscription();

        double p = (weeklyProbObtainSubscription[this.segmentId]);

        // if we have rewarded some initial users we increase the prob. of obtaining the
        // subscription (p of the Bass model = innovation)
        if (((Model) state).getParametersObject().getExperimentType() == 5
                && ((Model) state).isAnInitialRewardedBasicUser(this.gamerAgentId)) {

            p = p + ((Model) state).getParametersObject().getObtainSubscriptionIncrease();

            /*
             * System.out.println("Increased prob. of innovation from " +
             * (weeklyProbObtainSubscription[this.segmentId]) + " to " + p
             * +" because agent " + this.gamerAgentId + " was initially rewarded.");
             */
        }

        double r = model.random.nextDouble(Model.INCLUDEZERO, Model.INCLUDEONE);

        if (r < p) {

            // update the number of current subscriptors
            // model.increasePremium();
            // Model.logger.info("[++] obtainSubscriptionByInnovation() "
            // + "new premium gamer by innovation!");
            // leaving the subscription
            subscriptionState = Model.PREMIUM_USER;

            return true;
        }

        return false;

    }

    /**
     * Calc utility of all the brand
     *
     * @param model
     * @return
     */
    /*public double[] Utility(SimState state) {

        Model model = (Model) state;

        double utility[] = new double[model.brands.length];

        for (int i = 0; i < model.brands.length; i++) {
            utility[i] = util.Functions.utilityFunction(model.getBrands()[i].getDrivers(), this.getPreferences());
        }

        // Return the sum of all the values calculated
        return utility;

    }*/
    /**
     * Decision making and heuristic rules: Repetition
     *
     * @param state
     * @return product ID
     */
    public int Repetition(SimState state) {

        return this.purchasedBrands[this.currentStep - 1];

    }

    /**
     * Decision making and heuristic rules: Deliberation
     *
     * @param state
     * @return product ID
     */
    public double[] Deliberation(SimState state, double[] biasedProductUtilities) {
        Model model = (Model) state;

        double result[] = new double[model.getParametersObject().brands];

        double[] biasedProductsUtilities = this.BiasedProductUtility(model);

        for (int i = 0; i < model.getParametersObject().brands; i++) {

            result[i] = util.Functions.deliberationFunction(biasedProductsUtilities[i], biasedProductsUtilities);
        }

        return result;

    }

    /**
     * Decision making and heuristic rules: Deliberation
     *
     * @param state
     * @return product ID
     */
    public double[] Imitation(SimState state, double[] utilities) {
        Model model = (Model) state;

        double result[] = new double[model.getParametersObject().brands];

        for (int i = 0; i < model.getParametersObject().brands; i++) {

            result[i] = util.Functions.imitationFunction(utilities[i], utilities);
        }

        // Return the sum of all the values calculated
        return result;

    }

    /**
     * Decision making and heuristic rules: Deliberation
     *
     * @param state
     * @return product ID
     */
    public double[] SocialComparison(SimState state, double[] biasedProductUtilities) {
        Model model = (Model) state;

        double result[] = new double[model.getParametersObject().brands];

        double[] biasedProductsUtilities = this.BiasedProductUtility(model);

        // Arrayq que utilizaremos para ver que productos no ha comprado nadie
        Boolean[] purchasedBrands = new Boolean[model.getParametersObject().brands];
        Arrays.fill(purchasedBrands, Boolean.FALSE);

        ArrayList<Integer> neighbors = (ArrayList<Integer>) model.socialNetwork.getNeighborsOfNode(this.gamerAgentId);
        // Iterate over neighbors
        for (int i = 0; i < neighbors.size(); i++) {
            GamerAgent neighbor = (GamerAgent) (model.getAgents()).get(neighbors.get(i));
            //
            int p = neighbor.getPurchasedBrandsBySpecificStep(this.currentStep - 1);
            purchasedBrands[p] = true;

        }

        for (int i = 0; i < model.getParametersObject().brands; i++) {
            // si el producto no lo ha comprado nadie entonces no lo consideramos
            if (!purchasedBrands[i]) {
                result[i] = 0;
            } else {
                result[i] = util.Functions.socialComparisonFunction(biasedProductsUtilities[i], biasedProductsUtilities);
            }

        }

        // Return the sum of all the values calculated
        return result;

    }

    /**
     * Calc biased temporal utility of all the brand
     *
     * @param model
     * @return
     */
    public double[] BiasedProductUtility(SimState state) {

        Model model = (Model) state;

        double biasedProductUtility[] = new double[model.getParametersObject().brands];

        for (int i = 0; i < model.getParametersObject().brands; i++) {

            double fractionDirectContactsPurchaseBrand = this.fractionDirectContactsPurchaseBrand(state, i);

            biasedProductUtility[i] = util.Functions.biasedProductUtilityFunction(this.utility[i], fractionDirectContactsPurchaseBrand, model.getParametersObject().getSocialPeerfInfluence());

            double aux = (double) Math.round(biasedProductUtility[i] * 100) / 100;

            if (aux < 0.0 || aux > 1.0) {
                System.err.println("Error: Invalid BiasedProductUtility: " + aux);
            }

        }

        return biasedProductUtility;

    }

    public double[] UncertaintyAboutDecision(SimState state) {

        Model model = (Model) state;

        double uncertaintyAboutDecision[] = new double[model.getParametersObject().brands];

        for (int i = 0; i < model.getParametersObject().brands; i++) {

            double fractionDirectContactsNotPurchaseBrand = this.fractionDirectContactsNotPurchaseBrand(state, i);

            uncertaintyAboutDecision[i] = util.Functions.UncertaintyAboutDecisionFunction(fractionDirectContactsNotPurchaseBrand, model.getParametersObject().getSocialPeerfInfluence());

            double aux = (double) Math.round(uncertaintyAboutDecision[i] * 100) / 100;

            if (aux < 0.0 || aux > 1.0) {
                System.err.println("Error: Invalid uncertaintyAboutDecision: " + aux);
            }
        }

        return uncertaintyAboutDecision;

    }

    /**
     * Fraction of direct contacts of agent i in the social network who consumed
     * product productId in the last choise
     *
     * @param state
     * @param productId
     * @return
     */
    public double fractionDirectContactsPurchaseBrand(SimState state, int productId) {

        Model model = (Model) state;

        int count = 0;

        ArrayList<Integer> neighbors = (ArrayList<Integer>) model.socialNetwork.getNeighborsOfNode(this.gamerAgentId);
        // Iterate over neighbors
        for (int i = 0; i < neighbors.size(); i++) {
            GamerAgent neighbor = (GamerAgent) (model.getAgents()).get(neighbors.get(i));
            if (neighbor.getPurchasedBrandsBySpecificStep(this.currentStep - 1) == productId) {
                count = count + 1;
            }
        }

        return (double) count / neighbors.size();

    }

    /**
     * Fraction of direct contacts of agent i in the social network who not
     * consumed product productId in the last choise
     *
     * @param state
     * @param productId
     * @return
     */
    public double fractionDirectContactsNotPurchaseBrand(SimState state, int productId) {

        Model model = (Model) state;

        int count = 0;

        ArrayList<Integer> neighbors = (ArrayList<Integer>) model.socialNetwork.getNeighborsOfNode(this.gamerAgentId);

        // Iterate over neighbors
        for (int i = 0; i < neighbors.size(); i++) {
            GamerAgent neighbor = (GamerAgent) (model.getAgents()).get(neighbors.get(i));
            // System.out.println("this.currentStep " + this.currentStep + " compro el producto  " + neighbor.getPurchasedBrandsBySpecificStep(this.currentStep - 1);
            int p = neighbor.getPurchasedBrandsBySpecificStep(this.currentStep - 1);

            // TODO: y si el agente no ha comprado ningun producto en el anterior o ha comprado un producto distinto
            if (p != productId) {
                count = count + 1;
            }
        }

        double aux = (double) Math.round((double) count / neighbors.size() * 100) / 100;

        if (aux < 0.0 || aux > 1.0) {
            System.err.println("Error: Invalid fractionDirectContactsNotPurchaseBrand: " + aux);
        }

        return aux;

    }

    /**
     * Function to get a product
     *
     * @param state
     * @param probs
     * @return
     */
    public int obtainBrand(SimState state, double[] probs) {

        Model model = (Model) state;

        int brandId = 0; // el indice coincide con el id de la marca

        // double probBuy[] = new double[model.brands.length];
        double deliberation[] = new double[model.getParametersObject().brands];

        double r = model.random.nextDouble(Model.INCLUDEZERO, Model.INCLUDEONE);

        for (int i = 0; i < probs.length; i++) {
            // deliberation[i] = util.Functions.deliberationFunction(probs[i], probs);
            deliberation[i] = probs[i] / sumDoubleArray(probs);
        }

        // Array con el listado de probabilidad de compra
        double[] probBuy = util.Functions.sumOfDigitsFrom1ToN(deliberation);
        // System.out.println("sumOfDigitsFrom1ToN " + Arrays.toString(probBuy));

        for (brandId = 0; brandId < probs.length; brandId++) {
            if (r < probBuy[brandId]) {
                break;
            }
        }

        return brandId;

        // return model.getBrands()[brandId];
    }

    /**
     * Models the algorithm to get a new link for a new friend
     *
     * @param state - a simulation model object (SimState).
     */
    public void obtainNewFriend(SimState state) {

        Model model = (Model) state;

        double[] probNewFriend = ((ModelParameters) model.getParametersObject()).getSegmentDailyProbNewFriend();

        double r = model.random.nextDouble(Model.INCLUDEZERO, Model.INCLUDEONE);

        // Check if the agent must get a new link
        if (r < (probNewFriend[this.segmentId])) {

            int newConnectedNode = model.socialNetwork.addNewNeighborForNode(gamerAgentId, model.random);

            if (newConnectedNode == -1) {

                // Model.logger.info("[-> ++] obtainNewFriend() tried to create a new friend for
                // node "
                // + gamerAgentId + " but we couldn't!");
            } else {

                // Model.logger.info("[-> ++] obtainNewFriend() new friend added between node "
                // + gamerAgentId + " and node " + newConnectedNode);
            }
        }
    }

    /**
     *
     * @param state - a simulation model object (SimState).
     */
    public void loseExistingFriend(SimState state) {

        Model model = (Model) state;

        double[] probLoseFriend = ((ModelParameters) model.getParametersObject()).getSegmentDailyProbLoseFriend();

        double r = model.random.nextDouble(Model.INCLUDEZERO, Model.INCLUDEONE);

        // Check if the agent must get a new link
        if (r < (probLoseFriend[this.segmentId])) {

            int newConnectedNode = model.socialNetwork.removeNeighborForNode(gamerAgentId, model.random);

            if (newConnectedNode == -1) {

                // Model.logger.info("[-> --] leaveExistingFriend() tried to remove an edge but
                // we couldn't!");
            } else {

                // Model.logger.info("[-> --] leaveExistingFriend() " + gamerAgentId +
                // " and node " + newConnectedNode + " are not friends. Edge removed!");
            }

        }
    }

    /**
     * This methods returns true if the player plays today or not
     *
     * @param state - a simulation model object (SimState).
     */
    public boolean playToday(SimState state) {
        double dailyProbPlay[];

        Model model = (Model) state;

        // first get the day of the week to get prob to play
        // Calendar c = Calendar.getInstance();
        // c.setTime(model.getCurrentDate());
        // int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int dayOfWeek = model.getDayOfWeek();

        if (dayOfWeek == (Calendar.SATURDAY) || dayOfWeek == (Calendar.SUNDAY)) {

            dailyProbPlay = ((ModelParameters) model.getParametersObject()).getSegmentDailyProbPlayWeekend();

        } else {

            dailyProbPlay = ((ModelParameters) model.getParametersObject()).getSegmentDailyProbPlayNoWeekend();

        }

        // once we have the prob to play we check if it plays today
        double r = model.random.nextDouble(Model.INCLUDEZERO, Model.INCLUDEONE);

        if (r < dailyProbPlay[this.segmentId]) {

            // we have to play today!
            // Model.logger.info("[P] playToday() being dayOfWeek " + dayOfWeek);
            return true;
        } else {

            // we do not play today!
            return false;
        }

    }

    /**
     * This method refreshes the weights with the neighbors. To be called after
     * converting
     *
     * @param state - a simulation model object (SimState).
     */
    public void refreshWeightsWithNeighbors(SimState state) {

        Model model = (Model) state;

        double weight = ((ModelParameters) model.getParametersObject()).getWeight();
        int duration = ((ModelParameters) model.getParametersObject()).getWeightDuration();

        // to calculate the duration we add the current step
        duration += (model.schedule.getSteps());

        model.socialNetwork.setAttributeEdgeAllNeighbours(this.gamerAgentId, "weight", weight);

        model.socialNetwork.setAttributeEdgeAllNeighbours(this.gamerAgentId, "duration", duration);

    }

    // --------------------------- Steppable method --------------------------//
    /**
     * Step of the simulation (equals to a day of iteration).
     *
     * @param state - a simulation model object (SimState).
     */
    @Override
    public void step(SimState state) {

        Model model = (Model) state;

        currentStep = (int) model.schedule.getSteps();

        if (playToday(state)) {

            if (subscriptionState == Model.PREMIUM_USER && this.currentStep <= 0) {

                // en el step 0 no hacemos nada con los influnecers. Consideramos que si les hemos regalado un producto, no van a comprar ese día
            } else {

                // Calculamos un array de utilidades por cada marca
                // double[] utilities = this.Utility(state);
                int brandId = obtainBrand(state, this.utility);
                this.setStrategy(this.currentStep, model.UTILITY);

                if (this.currentStep > 0 && this.getPurchasedBrandsBySpecificStep(this.currentStep - 1) > -1) {

                    brandId = this.getPurchasedBrandsBySpecificStep(this.currentStep - 1); // Recuperamos el producto que compramos en el step t -1

                    // Calculamos los umbrales
                    double[] biasedProductUtilities = this.BiasedProductUtility(state);
                    double[] uncertaintyAboutDecisions = this.UncertaintyAboutDecision(state);

                    // System.out.println("biasedProductUtilities[brandId] ==" + biasedProductUtilities[brandId]);
                    // System.out.println("uncertaintyAboutDecisions[brandId] ==" + uncertaintyAboutDecisions[brandId]);
                    double[] probs = new double[model.getParametersObject().brands];

                    // solo puedo aplicar las heuristicas si hice una compra en el step -1
                    if (biasedProductUtilities[brandId] >= model.getParametersObject().getMinimunSatisfactionAgend() && uncertaintyAboutDecisions[brandId] <= model.getParametersObject().getUncertaintyToleranceLevel()) {
                        // System.out.println("Repetition");
                        this.setStrategy(this.currentStep, model.REPETITION);
                        this.setPurchasedBrands(this.currentStep, brandId);
                        brandId = this.Repetition(state);

                    } else if (biasedProductUtilities[brandId] < model.getParametersObject().getMinimunSatisfactionAgend() && uncertaintyAboutDecisions[brandId] <= model.getParametersObject().getUncertaintyToleranceLevel()) {
                        // System.out.println("  Deliberation");
                        this.setStrategy(this.currentStep, model.DELIBERATION);
                        probs = Deliberation(state, biasedProductUtilities);
                        brandId = obtainBrand(state, probs);

                    } else if (biasedProductUtilities[brandId] >= model.getParametersObject().getMinimunSatisfactionAgend() && uncertaintyAboutDecisions[brandId] > model.getParametersObject().getUncertaintyToleranceLevel()) {
                        // System.out.println("**Imitation");
                        this.setStrategy(this.currentStep, model.IMITATION);
                        probs = Imitation(state, this.getUtility());
                        brandId = obtainBrand(state, probs);

                    } else if (biasedProductUtilities[brandId] < model.getParametersObject().getMinimunSatisfactionAgend() && uncertaintyAboutDecisions[brandId] > model.getParametersObject().getUncertaintyToleranceLevel()) {
                        // System.out.println("--SocialComparison");
                        this.setStrategy(this.currentStep, model.SOCIALCOMPARISION);
                        probs = SocialComparison(state, biasedProductUtilities);
                        brandId = obtainBrand(state, probs);

                    }
                }
                // Guardamos el resultado
                this.setPurchasedBrands(this.currentStep, brandId);

                obtainNewFriend(state);

                loseExistingFriend(state);
            }
        }

    }

    // Todo: Remove it
    public void step1(SimState state) {

        // first we check if we play today
        if (playToday(state)) {

            if (subscriptionState == Model.PREMIUM_USER) {

                // THE SI MODEL DOES NOT USE IT!
                // leaveSubscription(state);
            } else if (subscriptionState == Model.BASIC_USER) {

                // changing subscription status
                boolean changed = obtainSubscriptionByInnovation(state);

                if (!changed) {

                    // Checking the type of contagion
                    switch (((Model) state).getParametersObject().getExperimentType()) {
                        case 0:
                            // TODO SIMPLE INDEP. CONTAGION (CORRECT??)
                            changed = this.simpleAdoptionFromFriends(state);

                            break;

                        case 1:
                            // THRESHOLD CONTAGION
                            changed = this.thresholdAdoptionFromFriends(state);

                            break;

                        case 2:
                            // BASS MODEL (INDEPENDENT DIFFUSION)
                            changed = this.bassAdoptionFromFriends(state);
                            break;

                        case 3:
                            // BASED ON COMPLEX CONTAGION
                            changed = this.complexAdoptionFromFriends(state);
                            break;

                        case 4:
                            // BASS MODEL BUT USING WEIGHTS (MARKETING ACTIONS)
                            changed = this.bassAdoptionFromFriendsUsingWeights(state);
                            break;

                        case 5:
                            // BASS MODEL BUT USING WEIGHTS FOR A GIVEN SET OF INITIAL USERS
                            // (MARKETING ACTIONS)
                            changed = this.bassAdoptionFromFriendsUsingWeightsForInitialUsers(state);
                            break;
                    }
                }

                // final actions in case the agent converts
                if (changed) {

                    // adjust the weights of their links if we are using weights
                    if (((Model) state).getParametersObject().getExperimentType() == 4) {

                        refreshWeightsWithNeighbors(state);

                    }

                }
            }

            // modifying dynamic social network
            obtainNewFriend(state);
            loseExistingFriend(state);

        }

    }
}
