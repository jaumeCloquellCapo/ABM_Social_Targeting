package util;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;


/**
 * Enables reading parameters from the file.
 * 
 * @author ktrawinski
 *
 */
public class ConfigFileReader {

	// ########################################################################
	// Variables
	// ########################################################################
	
	private Properties parameters; 
	private static String FILENAME;
	
	// ######################################################################## 
	// Constructors
	// ########################################################################
	
	/**
	 * Initializes a new instance of the ConfigFileReader class.
	 */
	public ConfigFileReader(String path) {
		parameters = new Properties();
		FILENAME = path;
	}
	
	// ########################################################################
	// Methods/Functions	
	// ########################################################################	
	
	/**
	 * Reads the config file (config.properties) in the config folder.
	 */
	public void readConfigFile() {
		InputStream Input = null;
		
		try {
			Input = new FileInputStream(FILENAME);
			
			// load a properties file
			parameters.load(Input);
	
			
		} catch (IOException ex) {
			ex.printStackTrace();			
		} finally {
			if (Input != null) {
				try {
					Input.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Gets the given parameter.
	 * @param ParameterName - the parameter that we are looking for.
	 * @return - the value of the parameter searched.
	 */
	public String getParameterString(String ParameterName) {
		return parameters.getProperty(ParameterName); 
	}
	
	/**
	 * Gets the given parameter as an integer.
	 * @param ParameterName - the parameter that we are looking for.
	 * @return - the value of the parameter searched.
	 */
	public int getParameterInteger(String ParameterName) {
		return Integer.parseInt(parameters.getProperty(ParameterName));
	}

	/**
	 * Gets the given parameter as a boolean
	 * @param ParameterName - the parameter that we are looking for.
	 * @return - the value of the parameter searched.
	 */
	public boolean getParameterBoolean (String ParameterName) {
		return Boolean.parseBoolean(parameters.getProperty(ParameterName));
	}
	
	/**
	 * Gets the given parameter as java.util.Date. IMPORTANT: parsing for the
	 * following date format: yyyy-MM-dd
	 * @param ParameterName - the parameter that we are looking for.
	 * @return - the value of the parameter searched.
	 */
	public Date getParameterDate(String ParameterName) {
		
		SimpleDateFormat formatDate = new SimpleDateFormat ("yyyy-MM-dd", Locale.ENGLISH);
		
		Date date = null;
		
		try {
			date = formatDate.parse(parameters.getProperty(ParameterName));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
	}
	/**
	 * Gets the given parameter as an double.
	 * @param ParameterName - the parameter that we are looking for.
	 * @return - the value of the parameter searched.
	 */
	public double getParameterDouble(String ParameterName) {
		return Double.parseDouble(parameters.getProperty(ParameterName));
	}
	
	/**
	 * Gets the given parameter as an double array.
	 * @param ParameterName - the parameter that we are looking for.
	 * @return - the value of the parameter searched.
	 */
	public double[] getParameterDoubleArray(String ParameterName) {
		String[] tmpStr;
		double[] tmpDouble;
		
		// Important: we use "," to divide columns and ";" to divide rows
		tmpStr = parameters.getProperty(ParameterName).split(",");
		tmpDouble = new double[tmpStr.length];
		// Transform to double		
		for(int i=0; i<tmpStr.length; i++) {
			tmpDouble[i] = Double.parseDouble(tmpStr[i]);
		}
		return tmpDouble;
	}

	public String[] getParameterStringArray(String ParameterName) {
		String[] tmpStr;
		String[] tmpDouble;
		
		// Important: we use "," to divide columns and ";" to divide rows
		tmpStr = parameters.getProperty(ParameterName).split(",");
		tmpDouble = new String[tmpStr.length];
		// Transform to double		
		for(int i=0; i<tmpStr.length; i++) {
			tmpDouble[i] = tmpStr[i];
		}
		return tmpDouble;
	}
	
	/**
	 * Gets the given parameter as an double two dimensional array.
	 * @param ParameterName - the parameter that we are looking for.
	 * @return - the value of the parameter searched.
	 */
	public double[][] getParameterDoubleArrayTwoDim(String ParameterName) {
		String[] tmpStr;
		String[][] tmpStrTwoDim;
		double[][] tmpDoubleTwoDim;		
		int count;
		
		// Important: we use "," to divide columns and ";" to divide rows
		// Split up by the semicolon
		tmpStr = parameters.getProperty(ParameterName).split(";");
		count = org.apache.commons.lang3.StringUtils.countMatches(tmpStr[0], ",");
		count++;
		tmpStrTwoDim = new String[tmpStr.length][count];
		tmpDoubleTwoDim = new double[tmpStr.length][count];
		
		// Combine split by semicolon and comma (2D)
		for(int i=0; i<tmpStr.length; i++) {
			tmpStrTwoDim[i] = tmpStr[i].split(",");
		}
		// Transform to double
		for(int i=0; i<tmpStr.length; i++) {
			for(int j=0; j<count; j++) {
				tmpDoubleTwoDim[i][j] = Double.parseDouble(tmpStrTwoDim[i][j]);
			}
		}
		return tmpDoubleTwoDim;
	}
	
	/**
	 * Gets the given parameter as an double two dimensional array. 
	 * @param ParameterName - the parameter that we are looking for.
	 * @return - the value of the parameter searched.
	 */
	public double[][][] getParameterDoubleArrayThreeDim(String ParameterName) {
		String[] tmpStr;
		String[][] tmpStrTwoDim;
		String[][][] tmpStrThreeDim;		
		double[][][] tmpDoubleThreeDim;		
		int count1;
		int count2;		
		
		// Important: we use "," to divide columns and ";" to divide rows
		// The ":" is used to separate the third dimension
		// Split up by the semicolon
		tmpStr = parameters.getProperty(ParameterName).split(":");
		count1 = org.apache.commons.lang3.StringUtils.countMatches(tmpStr[0], ";");
		count1++;
		tmpStrTwoDim = new String[tmpStr.length][count1];
		
		// Combine split by semicolon and comma (2D)
		for(int i=0; i<tmpStr.length; i++) {
			tmpStrTwoDim[i] = tmpStr[i].split(";");
		}
		
		count2 = org.apache.commons.lang3.StringUtils.countMatches(tmpStrTwoDim[0][0], ",");
		count2++;
		
		tmpStrThreeDim = new String[tmpStr.length][count1][count2];
		tmpDoubleThreeDim = new double[tmpStr.length][count1][count2];

		// Combine split by colon, semicolon, and comma (3D)
		for(int i=0; i<tmpStr.length; i++) {
			for(int j=0; j<count1; j++) {
				tmpStrThreeDim[i][j] = tmpStrTwoDim[i][j].split(",");
			}
		}
		
		// Transform to double
		for(int i=0; i<tmpStr.length; i++) {
			for(int j=0; j<count1; j++) {
				for(int k=0; k<count2; k++) {
					tmpDoubleThreeDim[i][j][k] = Double.parseDouble(tmpStrThreeDim[i][j][k]);					
				}
			}
		}
		return tmpDoubleThreeDim;
	}

		
}