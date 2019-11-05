package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ValueRecorder {

	private Map<Integer, String[][][]> values;
	
	int brands;
	int atts;
	int weeks;
	
	String name;
	
	public ValueRecorder(int brands, int atts, int weeks, String name) {
		this.brands=brands;
		this.atts=atts;
		this.weeks=weeks;
		this.name=name;
		
		values = new HashMap<Integer, String[][][]>();
	}
	
	public void putValues(int index, int brand, int att, int step, String value) {
		String[][][] matrix;
		if(values.containsKey(index)) {
			matrix=values.get(index);
		} else {
			matrix = new String[brands][atts][weeks]; 
		}
		matrix[brand][att][step]=value;
		
		values.put(index, matrix);
	}
	
	public void toFile() {
		String fileName="./stupid"+name+"Log.txt";
		try {
			FileWriter fw = new FileWriter(new File(fileName));
			Iterator<Integer> itMatrixes = values.keySet().iterator();
			while (itMatrixes.hasNext()) {
				String[][][] matrix = values.get(itMatrixes.next());
				for (int i=0; i<matrix.length; i++) {
					for (int j=0; j<matrix[i].length; j++) {
						for (int k=0; k<matrix[i][j].length; k++) {
							fw.write(matrix[i][j][k]+" ");
						}
						fw.write("\n");
					}
					fw.write("\n");
				}
				fw.write("\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
