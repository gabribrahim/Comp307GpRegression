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
			rep 						+= "Input "+index+" Value = "+input.output+" Weight = "+weights.get(index)+"\n";
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
		computedWeightedSum();
		if (weightedSum>=inputs.size()-1) {
			output						= 1.0;					
		}
		else {
			output						= 0.0;
		}
	}
	
	public void computedWeightedSum() {
		// Calculate the sum of weighted inputs	
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
