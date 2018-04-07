package model;


import java.util.ArrayList;
import java.util.List;

public class LabelledDataInstance {
	public List<String> featuresListAsStrings;
	public String labelName;
	public ArrayList<Boolean> featureListAsValues;	
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




	

}
