package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class DataSetsLoader {
	public ArrayList<LabelledDataInstance> trainingDataSetList= new ArrayList<LabelledDataInstance>();
	ArrayList<Float> minValuesForFeatures			= new ArrayList<Float>();
	ArrayList<Float> maxValuesForFeatures			= new ArrayList<Float>();
	public ArrayList<LabelledDataInstance> testDataSetList= new ArrayList<LabelledDataInstance>();
	public HashSet<String> dataSetClasses			= new HashSet<>();
	public BaseClassifier myclassifier;
	public String dataSetName;
	
	
	@Override
	public String toString() {
		String strRep								= dataSetName + " [dataSetListSize=" + trainingDataSetList.size() + "]\n";
		strRep									   += "               [MinimumRange = " + minValuesForFeatures + "]\n";
		strRep									   += "               [MaximumRange = " + maxValuesForFeatures + "]\n";
		strRep									   += "               [DataSetLabels= " + dataSetClasses + "]\n";
		strRep									   += "               [LabelsCount  = " + dataSetClasses.size() + "]\n";

		return strRep;
	}

	
	
	public void computeRangeForFeaturesInDataSet() {
		// Fill Minimum & Max Values For Features with Zero Lists Matching Count of Features
		for (int i=0 ; i<trainingDataSetList.get(0).featureListAsValues.size(); i++){
			minValuesForFeatures.add((float)100000000000000000000000000000.0);
			maxValuesForFeatures.add((float)0.0);
		}
		// Iterate Through the DataSet to Populate the minimum and maximum ranges
		for (LabelledDataInstance dataInstance: trainingDataSetList) {
			for (int i=0 ; i<dataInstance.featureListAsValues.size(); i++){
				//Minimum
				if(dataInstance.featureListAsValues.get(i)<minValuesForFeatures.get(i)) {
					minValuesForFeatures.set(i, dataInstance.featureListAsValues.get(i));					
				}
				//Maximum
				if(dataInstance.featureListAsValues.get(i)>maxValuesForFeatures.get(i)) {
					maxValuesForFeatures.set(i, dataInstance.featureListAsValues.get(i));					
				}				
			}
			
		}
		System.out.println(toString());
	}
	public String loadIrisDataSet(String filePath, ArrayList<LabelledDataInstance> dataSetList) {		
		
		File fileObj 								= new File(filePath);
		//Reading File Contents & Creating Java Representation Objects for DataSet For Classifier Classes//
		try (FileReader fileReader = new FileReader(fileObj);
				BufferedReader bufferedReader		= new BufferedReader(fileReader);){
			String line 							= bufferedReader.readLine();
			
			while (line != null) {
				String[] lineParts 					= line.split("  ");
				if (lineParts.length < 5) {
					break;
				}				

				List<String> lineList 				= Arrays.asList(lineParts);
				String featureLabel 				= lineList.get(lineList.size() - 1);
				List<String> featuresList 			= lineList.subList(0, 4);
				LabelledDataInstance dataInstance 	= new LabelledDataInstance(featuresList, featureLabel);
				dataSetClasses.add(featureLabel);
				dataInstance.parseInformationToValues(); // Converts Strings to Floats
				dataSetList.add(dataInstance);
				line 								= bufferedReader.readLine();
			}			
		} catch (IOException e) {
			System.out.println("FILE NOT FOUND !!");
		}
		
		String report								= toString();
		return report;
	}
}
