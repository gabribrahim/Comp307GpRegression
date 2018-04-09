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
	public ArrayList<FullImage> trainingDataSetList= new ArrayList<FullImage>();
	ArrayList<Float> minValuesForFeatures			= new ArrayList<Float>();
	ArrayList<Float> maxValuesForFeatures			= new ArrayList<Float>();
	public ArrayList<FullImage> testDataSetList= new ArrayList<FullImage>();
	public HashSet<String> dataSetClasses			= new HashSet<>();
	public BaseClassifier myclassifier;
	public String dataSetName;
	public HashSet<String> dataSetAttrsLabels		= new HashSet<>();
	public int pixelCountPerFeature					= 4;
	public int featureCountPerImage					= 50;
	
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
	
	

	public String loadImageDataSet(String filePath, ArrayList<FullImage> dataSetList) {		
		
		File fileObj 								= new File(filePath);
		//Reading File Contents & Creating Java Representation Objects for DataSet For Classifier Classes//
		try (FileReader fileReader = new FileReader(fileObj);
				BufferedReader bufferedReader		= new BufferedReader(fileReader);){
			String line 							= "";
			String labelName						= "";
			List <String>lineParts				    = new ArrayList<>(); 
			while(line!= null) {
				line								= bufferedReader.readLine();
//				System.out.println(line);
				while (!line.startsWith("P1")) { //Keeps Iterating & Fills LineParts with 0s & 1s till theline is P1					
					for (Scanner s = new Scanner(line); s.hasNext();) lineParts.add(s.next());
					line							= bufferedReader.readLine();
					if (line==null) {break;}					
				}
				if (lineParts.size()>0) {					
					FullImage imageInstance			= new FullImage(new ArrayList<String>(lineParts),labelName);
//					System.out.println(lineParts);
					imageInstance.parseInformationToValues();
					imageInstance.featuresCount		= featureCountPerImage;
					imageInstance.pixelsCountPerFeature= pixelCountPerFeature;
					imageInstance.generateFeatures();
					dataSetList.add(imageInstance);
					lineParts.clear();
				}				
				if (line==null) {break;}
				if (line.startsWith("P1")) { // if Line is P1 Then Get Label Name
					labelName						= bufferedReader.readLine();
					dataSetClasses.add(labelName);
//					System.out.println(labelName);
					line							= bufferedReader.readLine(); // Read Count of Rows & Columns Lines Which I am hard coding
//					System.out.println(line);
				}


			}
		} catch (IOException e) {
			System.out.println("FILE NOT FOUND !!");
		}
		
		String report								= toString();
		return report;
	}
}
