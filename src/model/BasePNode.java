package model;

import java.util.ArrayList;
import java.util.Random;

public class BasePNode {
	public Double output;
	public ArrayList<BasePNode> inputs = new ArrayList<>();
	public Double target;
	public ArrayList<Double> weights	= new ArrayList<>();
	public Double learningRate			= 1.0;
	public Double weightedSum			= 0.0;
	public BasePNode() {
		super();
	}

	public String getWeightVectorSring() {
		String weightVector				= "";
		for (double weight : weights) {weightVector+=weight+"\n";}
		return weightVector;
	}
	public void getOutput() {
		//Run the activation function on the WieghtedSum of Inputs
	}
	
	public void computedWeightedSum() {
		// Calculate the sum of weighted inputs 
	}
	
	public void tweakWeights() {
		// Edit Weights according to the error
	}
	
	public void generateRandomWeights() {
		// Clears Current Weights & Assigns a random double between 0 & 1 to each input
		Random randomGen				= new Random();
		weights.clear();
		for(int i =0; i<inputs.size(); i++) {
			weights.add(randomGen.nextDouble());
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
