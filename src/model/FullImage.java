package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FullImage{
	// This class represents the full pixels count of the image
	// Hard Coded to be 10 X 10 for now
	// Future Get that number from the DataSetsLoaderClass at time of construction
	public int featuresCount			= 50;
	public int pixelsCountPerFeature	= 4 ;
	public int[][] imagePixels 			= new int[10][10];
	public boolean[][] imagePixelsBools	= new boolean[10][10];
	public ArrayList<FeatureNode> imageFeatures = new ArrayList<>();
	public List<String> featuresListAsStrings;
	public String labelName;
	public ArrayList<Integer> featureListAsValues = new ArrayList<>();
	public ArrayList<Boolean> featureListAsBools  = new ArrayList<>();	
	public String predictedClass;
	public int randomSeed				= 0;
	
	public FullImage(List<String> featuresList, String labelName) {
		super();
		this.featuresListAsStrings 			= featuresList;
		this.labelName 						= labelName;
		
	}
	
	public String getInputVector() {
		String inputVector					= "";
		for (BasePNode featureNode :imageFeatures) {
			if (featureNode.output==1.0) {inputVector+= featureNode.output + "\n";}
			if (featureNode.output==0.0) {inputVector+= "_\n";}
		}
		return inputVector;
	}
	public void parseInformationToValues() {
		// Will take image list of 100 values and create an array list of integers
		for (String linepart : featuresListAsStrings) {
			for (int i=0; i<linepart.length();i++) {
				String substring 			= linepart.substring(i, i+1);
				if (substring==null) {continue;}
				featureListAsValues.add(Integer.parseInt(substring));				
				if (substring.equals("1")) {featureListAsBools.add(true);}
				if (substring.equals("0")) {featureListAsBools.add(false);}
			}
		}

		// Create 2D Array Of Integers for Image Pixels
		int row								= 0;
		int col								= 0;
		for (int pixelValue : featureListAsValues) {
			imagePixels[col][row]			=pixelValue;			
			col ++;
			if (col==10) {col=0;row++;}
			
		}
		// Create 2D Array of Booleans For Image Pixels
		row									= 0;
		col									= 0;
		for (boolean pixelValue : featureListAsBools) {
			imagePixelsBools[col][row]			=pixelValue;			
			col ++;
			if (col==10) {col=0;row++;}
			
		}
		
		
	}
	

	public void generateFeatures(FeatureSelectionMap selection) {
		// Generate Feature Nodes For Image
		// Given The predefined Feature Selection Map
	imageFeatures.clear(); 
	for (int featureIndex : selection.featuresMap.keySet()){
			ArrayList<Pixel> pixels			= selection.featuresMap.get(featureIndex);
			FeatureNode imageFeature		= new FeatureNode(pixels);
			for (int i=0; i<pixels.size();i++) {
				BasePNode featurePixelInput = new BasePNode(); // Always Adding A dummy Pixel Node with output to 1
				featurePixelInput.setOutput(1);
				imageFeature.inputs.add(featurePixelInput);				
			}
			imageFeature.originalImage		= this;
			imageFeature.getOutput();  // Runs the activation function in the hand out more details in function
			imageFeatures.add(imageFeature);
		}
	}
	public ArrayList<ArrayList<Integer>> getPixels(){
		// get list of pixels to draw to canvas [x,y,1/0] 1 white 0 black
		ArrayList<ArrayList<Integer>> pixels = new ArrayList<>();
		for (int row=0; row<10; row++) {
			for (int col=0; col<10; col++) {
				ArrayList<Integer> pixel     = new ArrayList<>();
				pixel.add(col);
				pixel.add(row);
				pixel.add(imagePixels[col][row]);
				pixels.add(pixel);			
				}
		}
				
		return pixels;
		
	}


}
