package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class KnnClassifier extends BaseClassifier{
	public DataSetsLoader myDataSet;
	public int knnNumber						= 3;
	public double accuracy;
	public KnnClassifier(DataSetsLoader myDataSet) {
		super();
		this.myDataSet = myDataSet;
	}

	public String predictClassForTestInstance(LabelledDataInstance testInstance) {
		for (LabelledDataInstance trainingInstance : myDataSet.trainingDataSetList) {
			double euclideanNormalisedDistance = 0.0;
			for (int featureIndex = 0; featureIndex < trainingInstance.featureListAsValues.size(); featureIndex++) {
				double featureRangeSquared = Math.pow(myDataSet.maxValuesForFeatures.get(featureIndex)
						- myDataSet.minValuesForFeatures.get(featureIndex), 2);

				double offsetBetweenFeatures = Math.pow(trainingInstance.featureListAsValues.get(featureIndex)
						- testInstance.featureListAsValues.get(featureIndex), 2);
				euclideanNormalisedDistance += Math.sqrt(offsetBetweenFeatures / featureRangeSquared);
				trainingInstance.euclideanNormalisedDistance = euclideanNormalisedDistance;
				}
			}
		// Implemented Comparable Interface on LabelledDataInstance to sort by euclidean distance
//		System.out.println("Before Sort" + myDataSet.trainingDataSetList.get(73));
		Collections.sort(myDataSet.trainingDataSetList); 
//		System.out.println("After Sort" + myDataSet.trainingDataSetList.get(73));
		List<LabelledDataInstance> nearestNieghbours= myDataSet.trainingDataSetList.subList(0, myDataSet.myclassifier.knnNumber);
		
		HashMap<String,Integer> votesTally 			= createVotingDict();
		for (LabelledDataInstance nieghbour : nearestNieghbours) {
//			System.out.println(nieghbour.labelName + " " + nieghbour.euclideanNormalisedDistance
//					+ nieghbour.featureListAsValues);
			int classTotalVotes					    = votesTally.get(nieghbour.labelName);			
			classTotalVotes++;
			votesTally.put(nieghbour.labelName, classTotalVotes);

		}
		String predictedClass						= Collections.max(votesTally.entrySet(), (label, voteCount) -> 
														label.getValue() - voteCount.getValue()).getKey();
		
		return predictedClass;
	}
	
	private HashMap<String, Integer> createVotingDict() {
		//Function To Create A HashMap as a Tally of Votes for Each Class in the DataSet
		HashMap<String, Integer> votingDict							= new HashMap<>();
		for (String label :myDataSet.dataSetClasses) {
			votingDict.put(label, 0);
		}
		return votingDict;
	}
}
