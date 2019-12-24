package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTextField;

import au.com.bytecode.opencsv.CSVReader;
import ec.util.MersenneTwisterFast;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.stream.IntStream;
import model.Model;

/**
 * Function class contains support and utility functions used on several
 * segments of the code.
 *
 * @author imoya
 *
 */
public class Functions {
    // Include zero when generating random value from 0 to 1

    public static final boolean INCLUDE_ZERO = true;
    // Include one when generating random value from 0 to 1	
    public static final boolean INCLUDE_ONE = true;
    /**
     * Used for some methods that offers scaling. This constant represent the
     * willing for not modifying the values.
     */
    public static final double IDENTITY_SCALE = 1.0;

    /**
     * Scale value usually used by the perception related components. Because
     * same sliders are used to represent values between 0 and 100 or 0 and 10
     * (like perceptions), some scale is needed for adapting every component.
     */
    public static final double PERCEPTION_MULTIPLIER_SCALE = 10.0;

    /**
     * The precision value used for comparing double values.
     */
    public static final double DOUBLE_EQUALS_DELTA = 0.01;

    /**
     * The goal value that the elements to be normalized should satisfy.
     */
    public static final double TOTAL_AMOUNT_NORMALIZABLE_VALUE = 1.0;

    public static int findIndex(int[] a, int target) {
        return IntStream.range(0, a.length)
                .filter(i -> target == a[i])
                .findFirst()
                .orElse(-1);	// return -1 if target is not found
    }

    public static <K, V extends Comparable> Map<K, V> sortHashMapByValue(Map<K, V> tempMap, Boolean descending) {
        TreeMap<K, V> map = new TreeMap<>(buildComparator(tempMap, descending));
        map.putAll(tempMap);
        return map;
    }

    public static <K, V extends Comparable> Comparator<? super K> buildComparator(final Map<K, V> tempMap, Boolean descending) {
        if (descending) {
            return (o1, o2) -> -tempMap.get(o1).compareTo(tempMap.get(o2));
        } else {
            return (o1, o2) -> tempMap.get(o1).compareTo(tempMap.get(o2));
        }
    }

    /**
     * Compares two double values. They will be considered as equals if the
     * difference between their values is less than delta.
     *
     * @param a The first double value.
     * @param b The second double value.
     * @param delta The threshold where the values are considered as equal.
     * @return wherever the difference between both values is lesser than delta.
     */
    public static boolean equals(double a, double b, double delta) {
        double diff = Math.abs(a - b);
        return diff <= delta;
    }

    /**
     * Calculates the multiplier for marketing plans with the given quality
     * value.
     *
     * @param input the quality input value.
     * @return the quality multiplier.
     */
    public static double qualityFunction(double input) {
        if (input >= 1.0) {
            return 2.0;
        } else if (input <= 0.0) {
            return 0.5;
        } else {
            double b = input * input;
            double a = input * 0.5;
            double output = b + a + 0.5;
            return output;
        }
    }

    /**
     * Auxiliar function to sum all the values belongs to array of doubles
     *
     * @param m
     * @return
     */
    public static double sumDoubleArray(double[] m) {

        double result = 0;

        for (double value : m) {
            result += value;
        }

        return result;
    }

    /**
     * Calculates utility ( 1 - |Driv.j - Pref.i| ) of product j for consumer
     * (utility) agent i
     *
     * @param drivers
     * @param preferences
     * @return
     */
    public static double utilityFunction(double[] drivers, double[] preferences) {

        double utility[] = new double[drivers.length];

        Arrays.setAll(utility, i -> 1 - Math.abs(drivers[i] - preferences[i]));

        double result = (double) sumDoubleArray(utility) / drivers.length;

        // Return the sum of all the values calculated
        return result;
    }

    public static double deliberationFunction(double biasedProductUtility, double[] biasedProductUtilities) {

        double aux[] = new double[biasedProductUtilities.length];

        Arrays.setAll(aux, i -> Math.exp(biasedProductUtilities[i]));

        return Math.exp(biasedProductUtility) / sumDoubleArray(aux);
    }

    public static double imitationFunction(double utility, double[] utilities) {

        double aux[] = new double[utilities.length];

        Arrays.setAll(aux, i -> Math.exp(2 * utilities[i]));

        return Math.exp(2 * utility) / sumDoubleArray(aux);
    }

    public static double socialComparisonFunction(double biasedProductUtility, double[] biasedProductUtilities) {

        double aux[] = new double[biasedProductUtilities.length];

        Arrays.setAll(aux, i -> Math.exp(biasedProductUtilities[i]));

        return Math.exp(biasedProductUtility) / sumDoubleArray(aux);
    }

    /**
     *
     * @param utility: Array with the utility of the agend
     * @param directContacts
     * @param b
     * @param fractionsDirectContacts
     * @return
     */
    public static double biasedProductUtilityFunction(double utility, double fractionsDirectContacts, double b) {
        return (1 - b) * utility + b * fractionsDirectContacts;
    }

    public static double UncertaintyAboutDecisionFunction(double fractionsDirectContacts, double b) {
        return b * (1 - fractionsDirectContacts);
    }

    /**
     * Calculates the probability to buy of product (Deliberation)
     *
     * @param utilities
     * @param productUtility
     * @return
     */
    /*public static double deliberationFunction(double brandUtility, double[] utilities) {

        double a = brandUtility;

        double b = sumDoubleArray(utilities);

        return a / b;
    }*/
    public static double[] sumOfDigitsFrom1ToN(double[] n) {

        double result[] = new double[n.length];

        for (int i = 0; i < n.length; i++) {
            if (i == 0) {
                result[i] = n[i];
            } else {
                result[i] = result[i - 1] + n[i];
            }
        }

        return result;

    }

    /**
     * Applies a normal distribution centered on "value" over [0,interval].
     *
     * @param mean the value used as mean for the distribution.
     * @param gaussian the value used for a standard deviation of one.
     * @param interval the top value used for the bounded distribution.
     * @return the given value normally distributed.
     */
    public static double applyNormalDistribution(
            double mean,
            double gaussian,
            double interval) {
        return Functions.scaleGaussianValue(mean, gaussian, 9.0, 0.0, interval);
    }

    /**
     * Applies a normal distribution centered on "value" with the given standard
     * deviation, fitting all values into the interval [bottom, top].
     *
     * @param mean the value used as mean for the distribution.
     * @param gaussian the value used for a standard deviation of one.
     * @param stDeviation the value used for standard deviation.
     * @param bottom the bottom value for the interval.
     * @param top the top value for the interval.
     * @return
     */
    public static double scaleGaussianValue(
            double mean,
            double gaussian, //TODO [KT] We can use gaussian generator inside to avoid errors 
            double stDeviation,
            double bottom,
            double top) {
        if (gaussian == 0.0) {
            return mean;
        } else {
            double result = 0.0;
            if (gaussian > 0) {
                if (mean + stDeviation > top) {
                    stDeviation = top - mean;
                }
            } else {
                if (mean - stDeviation < bottom) {
                    stDeviation = mean - bottom;
                }
            }
            result = (gaussian * stDeviation) + mean;
            return result;
        }
    }

    /**
     * Returns numbers between -1 and 1, normally distributed with 0 as its
     * mean. [KT] Keep in mind that dividing by 3, the stdev is 0.333.
     *
     * @param randomizer.
     * @return the next value from the normal distribution.
     */
    public static double nextGaussian(MersenneTwisterFast randomizer) {
        double gaussian;

        do {
            gaussian = randomizer.nextGaussian() / 3.0;
        } while (gaussian > 1.0 || gaussian < -1.0);

        return gaussian;
    }

    /**
     * Returns numbers between -boundary and boundary, normally distributed with
     * 0 as its mean. TODO [KT] I guess that stdev is set to 1.0 ??? Then, when
     * we set the boundary to 3 (3 times then stdev) we cover 99.7% of the
     * values. [KT] Keep in mind that dividing by boundary, the stdev is
     * boundary.
     *
     * @param randomizer
     * @param boundary - bounds the normal distribution from both sides.
     * @return - the next value from the normal distribution.
     */
    public static double nextGaussian(MersenneTwisterFast randomizer, double boundary) {
        double gaussian;

        do {
            gaussian = randomizer.nextGaussian();
        } while (gaussian > boundary || gaussian < -boundary);

//		return gaussian/boundary;
        return gaussian;
    }

    /**
     * Random Weighted Selection (like tournament selection in GA)
     *
     * @param ProbVec - the vector of probability weights.
     * @param r - the random value.
     * @return - the value selected
     */
    public static int randomWeightedSelection(double ProbVec[], double r) {
        // Variables
        final double NOTCONSIDERED = 0.000;
        double totalWeight = 0;
        int VecLength = ProbVec.length;
        double randValue;

        // Calculate total weight
        for (int i = 0; i < VecLength; i++) {
            totalWeight += ProbVec[i];
        }

        randValue = r * totalWeight;

        // Select the output
        for (int i = 0; i < VecLength; i++) {
            if (randValue < ProbVec[i]) {
                if (ProbVec[i] == NOTCONSIDERED) {
                    System.err.println("Node connection prob. is: "
                            + ProbVec[i] + " randValue is: " + randValue);
                }
                return i;
            }
            randValue = randValue - ProbVec[i];
        }
        // A robust code, return the last value
        return (VecLength - 1);
    }

    /**
     * Random Weighted Selection (like tournament selection in GA) with
     * restricted entries provided by a boolean array. Those entries marked by
     * the boolean array are not used in calculation.
     *
     * @param ProbVec - the vector of probability weights.
     * @param restricted - the vector of restricted entries.
     * @param r - the random value.
     * @return - the value selected
     */
    public static int randomWeightedSelectionRestricted(
            double ProbVec[], boolean restricted[], double r
    ) {
        // Variables
        final double NOTCONSIDERED = 0.000;
        double totalWeight = 0;
        int VecLength = ProbVec.length;
        double randValue;

        // Calculate total weight
        for (int i = 0; i < VecLength; i++) {
            if (!restricted[i]) {
                totalWeight += ProbVec[i];
            }
        }

        randValue = r * totalWeight;
        // Select the output
        for (int i = 0; i < VecLength; i++) {
            if (!restricted[i]) {
                if (randValue < ProbVec[i]) {
                    if (ProbVec[i] == NOTCONSIDERED) {
                        System.err.println("Node connection prob. is: "
                                + ProbVec[i] + " randValue is: " + randValue);
                    }
                    return i;
                }
                randValue = randValue - ProbVec[i];
            }
        }
        // A robust code, return the last value
        return (VecLength - 1);
    }

    /*
	 * Generic to String
     */
    /**
     * Concatenates several values from the map into a string.
     *
     * @param map The map containing the values.
     * @return the values separated by ','.
     */
    public static String mapToString(Map<Integer, ?> map) {
        String result = "";
        int size = map.size();
        if (size >= 1) {
            result += map.get(0);
        }
        int count = 1;
        while (count < size) {
            result += "," + map.get(count);
            count++;
        }
        return result;
    }

    /**
     * Concatenates a list populated with other lists.
     *
     * @param listOfLists The list containing the other lists.
     * @return the list values separated by ',' and ';'
     */
    public static String twoLevelListToString(List<List<?>> listOfLists) {
        String result = "";
        Iterator<List<?>> itList = listOfLists.iterator();
        if (itList.hasNext()) {
            result += Functions.listToString(itList.next());
        }
        while (itList.hasNext()) {
            result += ";" + itList.next();
        }
        return result;
    }

    /**
     * Concatenates several values from the list into a string.
     *
     * @param list The list containing the values.
     * @return the list values separated by ','.
     */
    public static String listToString(List<?> list) {
        String result = "";
        Iterator<?> it = list.iterator();
        if (it.hasNext()) {
            result += it.next();
        }
        while (it.hasNext()) {
            result += "," + it.next();
        }
        return result;
    }

    /*
	 * Non-Generics to String
     */
    /**
     * Concatenates an array of String into a single one, separating them with
     * ','.
     *
     * @param chains - the String containing all the chains.
     * @return The String containing all the chains.
     */
    public static String stringArrayToString(String[] chains) {
        String result = "";

        if (chains.length > 0) {
            result += chains[0];
        }
        for (int i = 1; i < chains.length; i++) {
            result += "," + chains[i];
        }

        return result;
    }

    /**
     * Represents a 3D matrix of double values into a String, separating every
     * element with a ':' character.
     *
     * @param values- the 3D double matrix to be represented as a String
     * @return The String representing the 3D matrix.
     */
    public static String doubleThreeLevelArrayToString(double[][][] values) {
        String result = "";
        if (values.length > 0) {
            result += Functions.doubleTwoLevelArrayToString(values[0]);
        }
        for (int i = 1; i < values.length; i++) {
            result += ":" + Functions.doubleTwoLevelArrayToString(values[i]);
        }
        return result;
    }

    /**
     * Represents a matrix of double values into a String, separating every
     * element with a ';' character.
     *
     * @param values - the double matrix to be represented as a String
     * @return The String representing the matrix.
     */
    public static String doubleTwoLevelArrayToString(double[][] values) {
        String result = "";
        if (values.length > 0) {
            result += Functions.doubleArrayToString(values[0]);
        }
        for (int i = 1; i < values.length; i++) {
            result += ";" + Functions.doubleArrayToString(values[i]);
        }
        return result;
    }

    /**
     * Represents an array of double values into a String, separating every
     * element with a ',' character.
     *
     * @param values - the double array to be represented as a String
     * @return The String representing double the array.
     */
    public static String doubleArrayToString(double[] values) {
        String result = "";
        if (values.length > 0) {
            result += values[0];
        }
        for (int i = 1; i < values.length; i++) {
            result += "," + values[i];
        }
        return result;
    }

    /**
     * Represents an array of int values into a String, separating every element
     * with the given character.
     *
     * @param values - the int array to be represented as a String
     * @param separator - the char selected for separating the arrays values.
     * @return The String representing the int array.
     */
    public static String intArrayToString(int[] values, char separator) {
        String result = "";
        if (values.length > 0) {
            result += values[0];
        }
        for (int i = 1; i < values.length; i++) {
            result += separator + String.valueOf(values[i]);
        }
        return result;
    }

    /**
     * Concatenates text from several JLabels into a string.
     *
     * @param map The map containing the JLabels.
     * @return the values separated by ','.
     */
    public static String mapLabelToString(Map<Integer, JLabel> map) {
        String result = "";
        int size = map.size();
        if (size >= 1) {
            result += map.get(0).getText();
        }
        int count = 1;
        while (count < size) {
            result += "," + map.get(count).getText();
            count++;
        }
        return result;
    }

    /**
     * Concatenates several values from the list into a string.
     *
     * @param list The text field list containing the values.
     * @return the list values separated by ','.
     */
    public static String textFieldListToString(List<JTextField> list) {
        String result = "";
        Iterator<JTextField> it = list.iterator();
        if (it.hasNext()) {
            result += it.next().getText();
        }
        while (it.hasNext()) {
            result += "," + it.next().getText();
        }
        return result;
    }

    /*
	 * Double to String
     */
    /**
     * Concatenates a list populated with lists which contains double values.
     *
     * @param listOfLists The list containing double lists.
     * @param scale The scale value used for scaling the double values.
     * @return the double values separated by ',', ';' and ':'
     */
    public static String threeLevelDoubleListToString(
            List<List<List<Double>>> listOfLists,
            double scale) {
        String result = "";
        Iterator<List<List<Double>>> itList = listOfLists.iterator();
        if (itList.hasNext()) {
            result += Functions.listOfDoubleListToString(itList.next(), scale);
        }
        while (itList.hasNext()) {
            result += ":" + Functions.listOfDoubleListToString(itList.next(), scale);
        }
        return result;
    }

    /**
     * Concatenates a list populated with lists which contains double values.
     *
     * @param listOfLists The list containing double lists.
     * @param scale The scale value used for scaling the double values.
     * @return the double values separated by ',' and ';'
     */
    public static String listOfDoubleListToString(
            List<List<Double>> listOfLists,
            double scale) {
        String result = "";
        Iterator<List<Double>> itList = listOfLists.iterator();
        if (itList.hasNext()) {
            result += Functions.doubleListToString(itList.next(), scale);
        }
        while (itList.hasNext()) {
            result += ";" + Functions.doubleListToString(itList.next(), scale);
        }
        return result;
    }

    /**
     * Concatenates several double values from the list into a string.
     *
     * @param list The list containing the double values.
     * @param scale The scale value used for scaling the double values.
     * @return the double values separated by ','.
     */
    public static String doubleListToString(
            List<Double> list,
            double scale) {
        String result = "";
        Iterator<Double> it = list.iterator();
        if (it.hasNext()) {
            result += it.next() * scale;
        }
        while (it.hasNext()) {
            result += "," + it.next() * scale;
        }
        return result;
    }

    /*
	 * Map to Array
     */
    /**
     * Transform a double Map into an Array. Similar to using collection
     * methods, but avoiding the ordering malfunction of typical Hash functions.
     *
     * @param map - The double map to be translated into an array.
     * @return The given double map as an array.
     */
    public static double[] transformDoubleMapToArray(
            Map<Integer, Double> map
    ) {
        double[] array = new double[map.size()];

        for (int i = 0; i < map.size(); i++) {
            double val = map.get(i);
            array[i] = val;
        }
        return array;
    }

    /**
     * Transform a double Map into an Array with rounded values.
     *
     * @param map - The double map to be translated into an array with rounded
     * values.
     * @return The given double map as an array of rounded values.
     */
    public static double[] transformDoubleMapToRoundedArray(
            Map<Integer, Double> map
    ) {
        double[] array = new double[map.size()];

        for (int i = 0; i < map.size(); i++) {
            double val = map.get(i);
            array[i] = Math.round(val);
        }
        return array;
    }

    public static double[][] transformDoubleListToArrayTwoDim(
            List<List<Double>> list,
            double scale) {
        double[][] array = new double[list.size()][list.get(0).size()];

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(0).size(); j++) {
                double val = list.get(i).get(j);
                array[i][j] = val * scale;
            }
        }
        return array;
    }

    public static double[][] transformDoubleListToArrayTwoDimTranspose(List<List<Double>> list) {
        double[][] array = new double[list.get(0).size()][list.size()];

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(0).size(); j++) {
                double val = list.get(i).get(j);
                array[j][i] = val;
            }
        }
        return array;
    }

    public static double[][] transposeDoubleMatrix(double[][] list) {
        double[][] array = new double[list[0].length][list.length];

        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < list[0].length; j++) {
                double val = list[i][j];
                array[j][i] = val;
            }
        }
        return array;
    }

    //TODO: Make it more generic!
    /**
     * Changes the dimension ordering. The second dimension becomes the first.
     *
     * @param array
     * @return
     */
    public static double[][][] changeDimensionOrderDoubleThreeDim(double[][][] array) {
        double[][][] helpArray = new double[array[0].length][array.length][array[0][0].length];
        for (int i = 0; i < helpArray.length; i++) {
            for (int j = 0; j < helpArray[i].length; j++) {
                for (int k = 0; k < helpArray[i][j].length; k++) {
                    helpArray[i][j][k] = array[j][i][k];
                }
            }
        }
        return helpArray;
    }

    /**
     * Reduces the decimal digits of a double value to two.
     *
     * @param val
     * @return
     */
    public static double setTwoDecimalDigits(double val) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("en", "UK"));
        df.applyPattern("#.##");
        return Double.valueOf(df.format(val));
    }

    public static double truncateValue(double value, int decimalDigits) {
        double scale = Math.pow(10, (double) decimalDigits);
        int newValue = (int) (value *= scale);
        value = newValue / scale;
        return value;
    }

    /**
     * Sub array from begin position, excluding end position.
     *
     * @param array
     * @param begin
     * @param end
     * @return
     */
    public static < E> E[] getSubArray(E[] array, E[] emptyArray, int begin, int end) {
        List<E> subArray = new ArrayList<E>();
        for (int i = begin; i < end; i++) {
            subArray.add(array[i]);
        }
        return subArray.toArray(emptyArray);
    }

    public static void shuffleArray(MersenneTwisterFast r, int[] array) {
        int ind;
        int temp;
        for (int i = array.length - 1; i > 0; i--) {
            ind = r.nextInt(i + 1);
            temp = array[ind];
            array[ind] = array[i];
            array[i] = temp;
        }
    }

    public static double normalizeMinMax(
            double valueIn, double minIn, double maxIn, double minOut, double maxOut
    ) {
        double valueOut = 0;
        valueOut = minOut + ((valueIn - minIn) * (maxOut - minOut)) / (maxIn - minIn);
        return valueOut;
    }

    public static void scaleDouble3DMatrix(double[][][] matrix, double scaleValue) {
        for (int i = 0; i < matrix.length; i++) {
            scaleDoubleMatrix(matrix[i], scaleValue);
        }
    }

    public static void scaleDoubleMatrix(double[][] matrix, double scaleValue) {
        for (int i = 0; i < matrix.length; i++) {
            scaleDoubleArray(matrix[i], scaleValue);
        }
    }

    public static void scaleDoubleArray(double[] array, double scaleValue) {
        for (int i = 0; i < array.length; i++) {
            double a = array[i];
            array[i] = a / scaleValue;
        }
    }

    public static double[][][] scaleCopyOfDouble3DMatrix(double[][][] matrix, double scaleValue) {
        double[][][] copy = new double[matrix.length][][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = scaleCopyOfDoubleMatrix(matrix[i], scaleValue);
        }
        return copy;
    }

    public static double[][] scaleCopyOfDoubleMatrix(double[][] matrix, double scaleValue) {
        double[][] copy = new double[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = scaleCopyOfDoubleArray(matrix[i], scaleValue);
        }
        return copy;
    }

    public static double[] scaleCopyOfDoubleArray(double[] array, double scaleValue) {
        double[] copy = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            double a = array[i];
            copy[i] = a * scaleValue;
        }
        return copy;
    }

    public static int addArraySegment(int[] array, int begin, int end) {
        int sum = 0;
        for (int i = begin; i < end; i++) {
            sum += array[i];
        }
        return sum;
    }

    // TODO [KT] move it to functions?? or leave here and remove argument 
    // which is global variable??
    // [KT] Moved to Functions.java
    /**
     * Generates the array of the indices based on the random weighted order. It
     * uses the values of the array to weight the probabilities of each index to
     * be chosen.
     *
     * @param array - the array of values to be ordered.
     * @param random - the random number generator.
     * @return - the array of the indices.
     */
    public static int[] getIndicesRandomWeightedOrder(
            double[] array, MersenneTwisterFast random
    ) {
        int size = array.length;
        boolean[] used = new boolean[size];
        int[] results = new int[size];
        int index;

        for (int i = 0; i < size; i++) {
            index = Functions.randomWeightedSelectionRestricted(
                    array, used, random.nextDouble(INCLUDE_ZERO, INCLUDE_ONE)
            );
            results[i] = index;
            used[index] = true;
        }
        return results;
    }

    // ########################################################################	
    // Check Methods 	
    // ########################################################################
    /**
     * Checks if the array (three dim. array) are in the range [{
     *
     * @paramref min},{
     * @paramref max}].
     * @param array - the array to be checked.
     * @param min - the minimum value allowed.
     * @param max - the maximum value allowed.
     */
    public static void checkArrayBoundaries(
            double[][][] array, double min, double max
    ) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                for (int k = 0; k < array[i][j].length; k++) {
                    // TODO [KT] Extend the exception thrown by assertion??
                    assert (array[i][j][k] <= max);
                    assert (array[i][j][k] >= min);
                }
            }
        }
    }

    /**
     * Checks if the array (two dim. array) are in the range [{
     *
     * @paramref min},{
     * @paramref max}].
     * @param array - the array to be checked.
     * @param min - the minimum value allowed.
     * @param max - the maximum value allowed.
     */
    public static void checkArrayBoundaries(
            double[][] array, double min, double max
    ) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                // TODO [KT] Extend the exception thrown by assertion??				
                assert (array[i][j] <= max);
                assert (array[i][j] >= min);
            }
        }
    }

    /**
     * Checks if the array (one dim. array) are in the range [{
     *
     * @paramref min},{
     * @paramref max}].
     * @param array - the array to be checked.
     * @param min - the minimum value allowed.
     * @param max - the maximum value allowed.
     */
    public static void checkArrayBoundaries(double[] array, double min, double max) {
        for (int i = 0; i < array.length; i++) {
            // TODO [KT] Extend the exception thrown by assertion??				
            assert (array[i] <= max);
            assert (array[i] >= min);
        }
    }

    /**
     * Checks if the value (one dim. array) are in the range [{
     *
     * @paramref min},{
     * @paramref max}].
     * @param value - the value to be checked.
     * @param min - the minimum value allowed.
     * @param max - the maximum value allowed.
     */
    public static void checkDoubleBoundaries(double value, double min, double max) {
        // TODO [KT] Extend the exception thrown by assertion??				
        assert (value <= max);
        assert (value >= min);
    }

    /**
     * Checks if the values of the array (two dim. array) sum up {
     *
     * @paramref sum} for each row.
     * @param array - the array to be checked
     * @param sum - The array values must sum up to this value.
     */
    public static void checkArraySum(double[][] array, double sum) {
        double[] sumCalc = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                // TODO [KT] Extend the exception thrown by assertion??				
                sumCalc[i] += array[i][j];
            }
            assert (sumCalc[i] == sum);
        }
    }

    /**
     * Checks if the values of the array (one dim. array) sum up {
     *
     * @paramref sum}.
     * @param array - the array to be checked.
     * @param sum - The array values must sum up to this value.
     */
    public static void checkArraySum(double[] array, double sum) {
        double sumCalc = 0.0;
        for (int i = 0; i < array.length; i++) {
            // TODO [KT] Extend the exception thrown by assertion??			
            sumCalc += array[i];
            assert (sumCalc == sum);
        }
    }

    public static double[][] doubleMatrixFromIntMatrix(int[][] intMatrix) {
        double[][] doubleMatrix = new double[intMatrix.length][intMatrix[0].length];
        for (int i = 0; i < intMatrix.length; i++) {
            for (int j = 0; j < intMatrix.length; j++) {
                doubleMatrix[i][j] = intMatrix[i][j];
            }
        }
        return doubleMatrix;
    }

    public static double[][] combineMatrixes(
            double[][] matrixA,
            double[][] matrixB
    ) {
        assert (matrixA.length == matrixB.length);

        double[][] combined = new double[matrixA.length][];
        for (int i = 0; i < matrixA.length; i++) {
            combined[i] = combineArrays(matrixA[i], matrixB[i]);
        }
        return combined;
    }

    public static double[] combineArrays(
            double[] arrayA,
            double[] arrayB
    ) {
        assert (arrayA.length == arrayB.length);

        double[] combined = new double[arrayA.length];
        for (int i = 0; i < arrayA.length; i++) {
            combined[i] = arrayA[i] + arrayB[i];
        }
        return combined;
    }

    public static double[] combineArrays(
            double[][] arrays
    ) {
        double[] combined = null;
        if (arrays.length > 0 && arrays[0].length > 0) {
            combined = arrays[0];
            for (int i = 1; i < arrays.length; i++) {
                combineArrays(combined, arrays[i]);
            }
        }
        return combined;
    }

    // ########################################################################	
    // I-O Methods 	
    // ########################################################################	
    /**
     * Writes to the given file.
     *
     * @param array
     * @param fileName
     * @param model
     */
    public static < E> void writeToFile(E[] array, String fileName, Model model) {
        final int START_SIM = -1;
        final boolean APPEND = true;
        final boolean NEW_FILE = false;
        final String DPATH = "./log/experiments/";
        PrintStream streamFile;
        long currentSeed = model.seed();

        //fileName = fileName + "_Seed-" + currentSeed;
        fileName = "test_Seed-" + currentSeed;

        try {
            // check if the file exists to append or not
            String completePath = DPATH + fileName + ".txt";

            File f1 = new File(DPATH);
            f1.mkdirs();

            // Open the file
            if (model.schedule.getTime() == START_SIM) {
                streamFile = new PrintStream(new FileOutputStream(completePath, NEW_FILE));

            } else {
                streamFile = new PrintStream(new FileOutputStream(completePath, APPEND));
            }

            // Print out the statistics
            streamFile.print(((int) model.schedule.getSteps()) + " --> ");

            // TODO stupid code just to let the exiting code... change it!!!
            streamFile.print("Total Premiums: " + array[0].toString());

            streamFile.println();
            streamFile.close();
        } catch (IOException e) {

            // TODO [KT] Provide an exception
            System.out.print("writeToFile() " + e.getMessage());
        };
        System.out.println();

    }

    public static double[][] readHistoryFromCSV(
            String fileName
    ) throws IOException {
        return readHistoryFromCSV(new File(fileName));
    }

    public static double[][] readHistoryFromCSV(
            File file
    ) throws IOException {

        double[][] historyStatistics = null;

        //Read CSV values
        CSVReader reader = new CSVReader(new FileReader(file), ',');

        try {

            List<String[]> csvValues = reader.readAll();
            Iterator<String[]> csvIterator = csvValues.iterator();

            int rows = csvValues.size();
            int cols = csvValues.get(0).length;

            if (rows == 0 || cols == 0 || csvValues.isEmpty()) {
                throw new IllegalArgumentException("Bad CSV file: " + file.getName());
            }

            historyStatistics = new double[rows][cols];

            /*
			 * A resize call is not supposed to be needed, because
			 * at this level there is not enough information to do
			 * it in a generic way.
             */
            for (int i = 0; csvIterator.hasNext(); i++) {
                String[] csvLine = csvIterator.next();
                assert (csvLine.length == cols);
                for (int j = 0; j < cols; j++) {
                    historyStatistics[i][j] = Double.valueOf(csvLine[j]);
                }
                assert (csvIterator.hasNext() || i == rows);
            }

        } finally {
            reader.close();
        }
        return historyStatistics;
    }

    public static void writeHistoryToCSV(
            String fileName, double[][] history
    ) throws IOException {

        BufferedWriter fileCSV
                = new BufferedWriter(new FileWriter(fileName, false));

        String line;
        for (int i = 0; i < history.length; i++) {
            line = String.valueOf(history[i][0]);
            for (int j = 1; j < history[i].length; j++) {
                line += "," + String.valueOf(history[i][j]);
            }
            fileCSV.write(line);
            fileCSV.newLine();
        }
        fileCSV.close();
    }
}
