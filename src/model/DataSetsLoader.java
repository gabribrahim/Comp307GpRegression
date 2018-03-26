package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;


public class DataSetsLoader {
	public ArrayList<LabelledDataInstance> trainingDataSetList= new ArrayList<LabelledDataInstance>();
	ArrayList<Float> minValuesForFeatures			= new ArrayList<Float>();
	ArrayList<Float> maxValuesForFeatures			= new ArrayList<Float>();
	public ArrayList<LabelledDataInstance> testDataSetList= new ArrayList<LabelledDataInstance>();
	public HashSet<String> dataSetClasses			= new HashSet<>();
	public BaseClassifier myclassifier;
	public String dataSetName;
	public HashSet<String> dataSetAttrsLabels		= new HashSet<>();
	
	@Override
	public String toString() {
		String strRep								= dataSetName + " [dataSetListSize=" + trainingDataSetList.size() + "]\n";
		strRep									   += "               [DataSetLabels= " + dataSetClasses + "]\n";
		strRep									   += "               [LabelsCount  = " + dataSetClasses.size() + "]\n";

		return strRep;
	}

	
	public void clear() {
		this.trainingDataSetList.clear();
		this.testDataSetList.clear();
		this.minValuesForFeatures.clear();
		this.maxValuesForFeatures.clear();
	}
	
	

	public String loadHepDataSet(String filePath, ArrayList<LabelledDataInstance> dataSetList) {		
		
		File fileObj 								= new File(filePath);
		//Reading File Contents & Creating Java Representation Objects for DataSet For Classifier Classes//
		try (FileReader fileReader = new FileReader(fileObj);
				BufferedReader bufferedReader		= new BufferedReader(fileReader);){
			String line 							= bufferedReader.readLine(); // Class Labels
			for (Scanner s = new Scanner(line); s.hasNext();) dataSetClasses.add(s.next());
			line 									= bufferedReader.readLine(); // Attributes Labels
			for (Scanner s = new Scanner(line); s.hasNext();) dataSetAttrsLabels.add(s.next());
			line 									= bufferedReader.readLine();
			List <String>lineParts				    = new ArrayList<>(); 
			while(line!= null) {
				for (Scanner s = new Scanner(line); s.hasNext();) lineParts.add(s.next());
				
				line 								= bufferedReader.readLine();
				LabelledDataInstance dataInstance   = new LabelledDataInstance(lineParts.subList(1, lineParts.size()),lineParts.get(0));
				dataInstance.parseInformationToValues();
				dataSetList.add(dataInstance);
				lineParts.clear();
//				System.out.println(dataInstance);
			}
		} catch (IOException e) {
			System.out.println("FILE NOT FOUND !!");
		}
		
		String report								= toString();
		return report;
	}
}
