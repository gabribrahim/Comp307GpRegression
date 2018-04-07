package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FullImage{
	public int featuresCount			= 50;
	public int pixelsCount				= 4 ;
	public int[][] imagePixels 			= new int[10][10];	
	
	public List<String> featuresListAsStrings;
	public String labelName;
	public ArrayList<Integer> featureListAsValues = new ArrayList<>();	
	public String predictedClass;
	
	public FullImage(List<String> featuresList, String labelName) {
		super();
		this.featuresListAsStrings 			= featuresList;
		this.labelName 						= labelName;
		
	}
	
	public void parseInformationToValues() {
		// Will take image list of 100 values and create an array list of integers
		for (String linepart : featuresListAsStrings) {
			for (int i=0; i<linepart.length();i++) {
				String substring 			= linepart.substring(i, i+1);
				if (substring==null) {continue;}
				featureListAsValues.add(Integer.parseInt(substring));
			}
		}
		
		int row								= 0;
		int col								= 0;
		for (int pixelValue : featureListAsValues) {
			imagePixels[row][col]			=pixelValue;			
			col ++;
			if (col==10) {col=0;row++;}
			
		}
		
	}
	
	public ArrayList<ArrayList<Integer>> getPixels(){
		ArrayList<ArrayList<Integer>> pixels = new ArrayList<>();
		for (int row=0; row<10; row++) {
			for (int col=0; col<10; col++) {
				ArrayList<Integer> pixel     = new ArrayList<>();
				pixel.add(row);
				pixel.add(col);
				pixel.add(imagePixels[row][col]);
				pixels.add(pixel);			
				}
		}
				
		return pixels;
		
	}
	public void createProxyFeatures() {
		// Creates Features For The image that resolve to True using the function of sums>pixelCount-1 ? 1:0
	}

}
