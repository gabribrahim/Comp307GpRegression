package model;


import java.util.ArrayList;
import java.util.List;

public class LabelledDataInstance implements Comparable<LabelledDataInstance>{
	public List<String> featuresListAsStrings;
	public String labelName;
	public ArrayList<Boolean> featureListAsValues;
	public double euclideanNormalisedDistance = 0.0 ;
	public String predictedClass;
	public LabelledDataInstance(List<String> featuresList, String labelName) {	
		super();
		this.featuresListAsStrings 			= featuresList;
		this.labelName 						= labelName;
		
	}
	
	@Override
	public String toString() {
		return "LabelledDataInstance [labelName=" + labelName + ", featureListAsValues=" + featureListAsValues + " "
				+ "AttrsCount= "+featureListAsValues.size()+"]";
	}

	public void parseInformationToValues() {
		featureListAsValues					= new ArrayList<Boolean>();
		for (String string : featuresListAsStrings) {
			featureListAsValues.add(Boolean.parseBoolean(string));
		}
	}

	@Override
	public int compareTo(LabelledDataInstance otherLabelledDataInstance) {
		if (this.euclideanNormalisedDistance > otherLabelledDataInstance.euclideanNormalisedDistance) {return 1;}
		if (this.euclideanNormalisedDistance < otherLabelledDataInstance.euclideanNormalisedDistance) {return -1;}
//		System.out.print(this.euclideanNormalisedDistance < otherLabelledDataInstance.euclideanNormalisedDistance);
//		System.out.println(this.euclideanNormalisedDistance + " " + otherLabelledDataInstance.euclideanNormalisedDistance);
		return 0;
	}


	

}
