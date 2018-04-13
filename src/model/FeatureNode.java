package model;

import java.util.ArrayList;

public class FeatureNode extends BasePNode{
	
	public ArrayList<Pixel> inputPixels; // might prove useful while debugging & visualizing features
	public FullImage originalImage;
	
	
	@Override
	public String toString() {
		String rep						= "";
		for (BasePNode input : inputs) {
			int index					= inputs.indexOf(input);
			Pixel pixel					= inputPixels.get(index);
			
			rep 						+= "Input "+index+" Value = "+input.output+" Weight = "+weights.get(index)+"\n";
			rep 						+= pixel.toString()+"\n";
			rep 						+= "               "+originalImage.imagePixelsBools[pixel.x][pixel.y]+"\n";
		}
		rep 							+= "Weighted Sum = "+weightedSum+" \n Output = "+output+"\n";
		return rep;
	}

	public FeatureNode(ArrayList<Pixel> inputPixels) {
		super();
		this.inputPixels 				= inputPixels;
	}
	
	public void getOutput() {
		//Run the activation function on the WieghtedSum of Inputs
		// In This case the weightedSum of the feature node is dictated differently as per assignment
		computedWeightedSum();
		if (weightedSum>=inputs.size()-1) {
			output						= 1.0;					
		}
		else {
			output						= 0.0;
		}
	}
	
	public void computedWeightedSum() {
		// Check whether the input pixel randomly assigned sign matches 
		// the original pixel sign of the original image
		// Weights here have no relationship to the weight of the perceptron
		// They are purely for visual representation on the UI
		weightedSum						= 0.0;
		for (Pixel input : inputPixels) {
			int index					= inputPixels.indexOf(input);
			if (originalImage.imagePixelsBools[input.x][input.y] == input.sign) {
				weightedSum++;
				weights.add(index, 1.0);
				
			}
			else {
				weights.add(index, 0.0);
			}
			
		}
	}
	
}
