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
	ArrayList<Float> minValuesForFeatures			= new ArrayList<Float>();
	ArrayList<Float> maxValuesForFeatures			= new ArrayList<Float>();	
	public HashSet<String> dataSetClasses			= new HashSet<>();
	public BaseClassifier myclassifier;
	public String dataSetName;
	public HashSet<String> dataSetAttrsLabels		= new HashSet<>();
	public int pixelCountPerFeature					= 4;
	public int featureCountPerImage					= 50;
	public int randomSeed							= 0;
	private ArrayList<LabelledDataInstance> testDataSetList;
	private ArrayList<LabelledDataInstance> trainingDataSetList;
	
	@Override
	public String toString() {
		String strRep								= dataSetName + " [dataSetListSize=" + "]\n";
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
	
	

}