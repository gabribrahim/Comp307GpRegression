package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class FeatureSelectionMap {
	// responsible creating the Random Pixel Selection for All Images
	public int randomSeed					= 0;
	public int pixelsCountPerFeature		= 4;
	public int featuresCount				= 50;
	public LinkedHashMap <Integer,ArrayList<Pixel>> featuresMap	= new LinkedHashMap<>();

	public FeatureSelectionMap(int randomSeed, int pixelsCountPerFeature, int featuresCount) {
		super();
		this.randomSeed = randomSeed;
		this.pixelsCountPerFeature = pixelsCountPerFeature;
		this.featuresCount = featuresCount;
	}
	
	@Override
	public String toString() {
		String rep 							= "";
		Iterator iter						= featuresMap.entrySet().iterator();
		while(iter.hasNext()) {			
			Map.Entry<Integer,Double> feature= (Map.Entry)iter.next();
			rep								+= "Feature Index = "+feature.getKey()+"\n\t"
											+ feature.getValue()+"\n";
		}
		
		return rep;
	}

	public Pixel getValidRandomPixel(Random randomGen) {
		// Cast Random column, row,boolean in a pixel
//		boolean valid						= false;
		boolean sign 						= randomGen.nextBoolean();
		int row 							= randomGen.nextInt(10);
		int col 							= randomGen.nextInt(10);		
		Pixel pixel							= new Pixel(row,col,sign);
		return pixel;
	}
	
	public void generatePixelSelectionSet(){
		// Creates a LinkHashMap to contain Key Feature Index
		// Value ArrayList of Pixels Representing the Feature
		Random randomGen;
		if (randomSeed==0) {randomGen= new Random();}
		else {randomGen= new Random(randomSeed);}
		
		for (int featureIndex=0; featureIndex<featuresCount;featureIndex++) {
			ArrayList<Pixel> pixels				= new ArrayList<>();

			for (int i=0; i<pixelsCountPerFeature; i++) {
				Pixel randomValidPixel		= getValidRandomPixel(randomGen);
				pixels.add(randomValidPixel);
			}			
			featuresMap.put(featureIndex, pixels);
			
		}
	}
}
