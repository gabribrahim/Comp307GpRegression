package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class BasePNode {
	public Double output;
	public ArrayList<BasePNode> inputs = new ArrayList<>();
	public Double target;
	public ArrayList<Double> weights	= new ArrayList<>();
	public FullImage originalImage;
	public Double learningRate			= 1.0;
	public Double weightedSum			= 0.0;
	public Double error					= 0.0;
	public Double error_afterWtweak		= 0.0; //ErrorAfter tweaking weights
	public Double output_afterWtweak	= 0.0; //Output After tweaking weights
	public boolean debug				= false;
	private HashMap<String, Double> labelMapping = new HashMap<>();
	public BasePNode() {
		super();
		labelMapping.put("#X", 1.0);
		labelMapping.put("#O", 0.0);
		
	}
	
	public void debugPrint(Object message) {
		if (this.debug) {
			System.out.println(message);
		}
	}
	public String getWeightVectorSring() {
		String weightVector				= "";
		for (double weight : weights) {weightVector+=weight+"\n";}
		return weightVector;
	}
	
	public void setInputs(ArrayList<FeatureNode> imageFeatures) {
		// Adds A Bias Node Whenever New Features are passed through
		ArrayList<BasePNode> featurelist= new ArrayList<>(imageFeatures);
		BasePNode biasNode				= new BasePNode();
		biasNode.setOutput(1.0);
		featurelist.add(0, biasNode);
		inputs							= featurelist;
//		System.out.println("Inputs & Bias = "+inputs.size());
	}
	public void getOutput() {
		//Run the activation function on the WieghtedSum of Inputs
		//#Yes &  #other = 1 & 0
		computedWeightedSum();
		if (weightedSum > 0){output=1.0;}
		else {output=0.0;}
		computeError();
	}
	
	public void tweakWeights() {
		// Edit Weights according to the error
		double weight;
		for (int i=0; i<weights.size(); i++) {
			weight						= weights.get(i);
//			System.out.print("weight before"+weight + " LearningR"+learningRate+" >> ");
//			if (error!=0 && labelMapping.get(originalImage.labelName)==1.0)
//				weight						= weight + inputs.get(i).output*learningRate;
////				weight						= weight
//			if (error!=0 && labelMapping.get(originalImage.labelName)==0)
//				weight						= weight - inputs.get(i).output *learningRate;
////				weight						= weight;
			
			weight						= weight + (learningRate*(error*inputs.get(i).output));
			weights.set(i, weight);
//			System.out.println(" weight After"+weights.get(i));
		}
		
	}
	
	public void computeError() {
		//Cost Function implementation output - target
		error							= labelMapping.get(originalImage.labelName) - output;
//		System.out.println("Error = "+error + " "+ originalImage.labelName + " " + weightedSum + " " + output);
//		System.out.println(originalImage.labelName + " predicted as "+ output + " Error = "+ error);
	}
	
	public void computedWeightedSum() {
		// Calculate the sum of weighted inputs
		weightedSum						= 0.0;
//		System.out.println(weights.size());
		for (BasePNode input : inputs) {
			int input_index				= inputs.indexOf(input);
//			input.getOutput();
			weightedSum					+= input.output * weights.get(input_index);
			debugPrint(input.output + "," + weights.get(input_index)+","+learningRate);
		}
	}
	

	
	public void generateWeights() {
		// Clears Current Weights & Assigns a random double between 0 & 1 to each input
		Random randomGen				= new Random(1);
		for(int i =0; i<inputs.size(); i++) {
			double randomValue = -0.1 + (0.1 - -0.1) * randomGen.nextDouble();

			weights.add(randomValue);
		}
	}
	public void getInputs() {
		// checks if the Node's inputs has outputs or not
		// if not it computes each output
		for (BasePNode input : this.inputs) {
			if (input.output == null) {
				input.getOutput();
			}
		}
	}
	
	public void setWeights(ArrayList<Double> bakedWeights) {
		// initializes the weights prior to the learning process
		this.weights				= new ArrayList<>(bakedWeights);
	}
	
	public void setOutput(double bakedOutput) {
		// override function to skip the activation function
		// for bias nodes & other pixel Nodes
		this.output				 	= bakedOutput;
	}
}
