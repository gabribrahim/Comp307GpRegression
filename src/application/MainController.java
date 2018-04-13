package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.imageio.ImageIO;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.BasePNode;
import model.DataSetsLoader;
import model.FeatureNode;
import model.FullImage;

public class MainController {
	// UI ELEMENTS
	@FXML TextArea StatusTA;
	@FXML Label MainLabel;	
	@FXML SwingNode TreeP;
	@FXML HBox ChartBox;
	@FXML TextField GeniThresholdTF;
	@FXML Label StatusLB;
	@FXML VBox TreeSnapShot;
	@FXML Slider ImageSelectorSL;
	@FXML Slider FeatureSelectorSL;
	@FXML HBox PreviewHB;
	@FXML Group ImagePrevGRP;
	@FXML Group FeaturePrevGRP;
	@FXML Group AllInputsPreviewGRP;
	@FXML TextField PixelCountTF;
	@FXML TextField FeaturesCountTF;
	@FXML TextArea InputsVectorTA;
	@FXML TextArea WeightsVectorTA;
	@FXML ScrollPane AllInputsPreviewPN;
	@FXML TextField EpochsTF;
	@FXML TextField LearningRateTF;
	@FXML VBox LearningCurveBox;
	@FXML TextField RandomTF;
	private Main main;
	private DataSetsLoader myDataLoader = new DataSetsLoader();
	private Canvas imageCanvas 				= new Canvas(10, 10);
	private GraphicsContext imageGc 			= imageCanvas.getGraphicsContext2D();
	private Canvas featureCanvas 				= new Canvas(10, 10);
	private GraphicsContext featureGc 			= featureCanvas.getGraphicsContext2D();
	private Canvas inputLayerCanvas				= new Canvas(100, 50);
	private GraphicsContext inputLayerGc		= inputLayerCanvas.getGraphicsContext2D();	
	private BasePNode perceptronNode;
	private int totalEpochsCount				= 0;
	private LinkedHashMap<Integer, Double> learningcurve=new LinkedHashMap<>();
    private NumberAxis xAxis 					= new NumberAxis();
    private NumberAxis yAxis 					= new NumberAxis();
    private LineChart<Number,Number> lineChart  = new LineChart<Number,Number>(xAxis,yAxis);
	private int debugImageIndex					= 0;
	public void setMain(Main main) {
		this.main = main;
		setupCanvases();
        loadDataSet();
        changeImageInWin();
        drawFeature(0, 0);
        drawAllFeatures();    
        createPerceptron();
        Collections.shuffle(myDataLoader.trainingDataSetList);
	}
	
	public void createPerceptron() {
		perceptronNode							= new BasePNode();
		perceptronNode.setInputs(myDataLoader.trainingDataSetList.get(0).imageFeatures);
		perceptronNode.generateWeights();
		WeightsVectorTA.setText(perceptronNode.getWeightVectorSring());
		StatusTA.setText("Create New Preceptron & Assigned Random Weights");
	}
	
	public void testPerceptron() {
		//Train the neuron Giving it the current Image & its features list as inputs
		int imageIndex					= (int)ImageSelectorSL.getValue();		
		FullImage currentImage			= myDataLoader.trainingDataSetList.get(imageIndex);
		perceptronNode.originalImage	= currentImage;
		perceptronNode.setInputs(currentImage.imageFeatures);
		perceptronNode.getOutput();
//		perceptronNode.tweakWeights();		
		WeightsVectorTA.setText(perceptronNode.getWeightVectorSring());
		StatusTA.setText("Trained Perceptron On Image Index " + imageIndex + "\nImage Class = " + currentImage.labelName
						+"\nWeightedSum = "+perceptronNode.weightedSum
						+"\nOutput = "+perceptronNode.output
						+"\nError = "+perceptronNode.error);
	}
	public void debug() {
		int hits = 0;		
		System.out.println("Image "+debugImageIndex+",Error,Input,weightBeforeChange,Weight,W * I,LR,New Weight");
		FullImage currentImage			= myDataLoader.trainingDataSetList.get(debugImageIndex);
		perceptronNode.learningRate		= Double.parseDouble(LearningRateTF.getText());
		perceptronNode.originalImage	= currentImage;
		perceptronNode.debug			= true;
		perceptronNode.setInputs(currentImage.imageFeatures);
		perceptronNode.getOutput();
		perceptronNode.tweakWeights();
		WeightsVectorTA.setText(perceptronNode.getWeightVectorSring());
		if (perceptronNode.error==0) {hits++;}
		System.out.println();
		System.out.println(debugImageIndex + " " + currentImage.labelName + " Error= "+perceptronNode.error
				+" Output = "+perceptronNode.output
				+" Weighted Sum = " + perceptronNode.weightedSum);
		
		debugImageIndex++;
		
	}
	public void trainPerceptronForEpochs() {
		int numberOfEpochs						= Integer.parseInt(EpochsTF.getText());
		perceptronNode.learningRate				= Double.parseDouble(LearningRateTF.getText());
		perceptronNode.debug					= false;
		double minAccuracy						= 1.0;
		double maxAccuracy						= 0.0;
		for (int k=0; k<numberOfEpochs; k++) {
			int hits							= 0;
			int totalError						= 0;
			totalEpochsCount = totalEpochsCount+1;
			Collections.shuffle(myDataLoader.trainingDataSetList);
			for (int i=0; i<myDataLoader.trainingDataSetList.size();i++) {
				StatusLB.setText("Input Vector Length = "+perceptronNode.inputs.size()
								+" - Wieghts Vector Length = "+ perceptronNode.weights.size());
				FullImage currentImage			= myDataLoader.trainingDataSetList.get(i);
				perceptronNode.setInputs(currentImage.imageFeatures);
				perceptronNode.originalImage	= currentImage;
				perceptronNode.getOutput();
				perceptronNode.tweakWeights();
				totalError						+= Math.abs(perceptronNode.error);
				if( perceptronNode.error == 0) {hits++;}
				WeightsVectorTA.setText(perceptronNode.getWeightVectorSring());
				StatusTA.setText("Trained Perceptron On Image Index " + i + "\nImage Class = " + currentImage.labelName
								+"\nWeightedSum = "+perceptronNode.weightedSum
								+"\nOutput = "+perceptronNode.output
								+"\nError = "+perceptronNode.error);
			}			
			double accuracy						= (double)hits / (double)myDataLoader.trainingDataSetList.size();
			double learningError				= 1-accuracy;
			if (accuracy>maxAccuracy) {maxAccuracy=accuracy;}
			if (accuracy<minAccuracy) {minAccuracy=accuracy;}
			
			StatusTA.setText("Accuracy @ Epoch " + totalEpochsCount + " = " + accuracy + "\nHits = " + hits+"\n"
								+"Total Error = "+totalError+"\n"
								+"Best Pass   = "+ maxAccuracy+"\n"
								+"Worst Pass  = "+ minAccuracy);
			if (hits==myDataLoader.trainingDataSetList.size()) {break;}
			System.out.println(accuracy+","+learningError);
			learningcurve.put(totalEpochsCount, learningError);
			
		}
		drawLearningCurve(0.01);
	}
	public void setupLearningCurveChart() {
        xAxis.setLabel("Epoch");
        yAxis.setLabel("Error");        
//        lineChart.setTitle("Learning Curve");
        lineChart.setCreateSymbols(false);
        lineChart.setAnimated(false);
        lineChart.getXAxis().setAutoRanging(true);
		lineChart.getYAxis().setAutoRanging(true);
		lineChart.setVerticalGridLinesVisible(true);        

	}
	public void resampleLearningCurve() {
		TextInputDialog dialog = new TextInputDialog("0.01");
		dialog.setTitle("Resample Learning Curve");
		dialog.setHeaderText("fraction of points to Skip");
		dialog.setContentText("double fraction = ");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			drawLearningCurve(Double.parseDouble(result.get()));
		}		
		
	}
	public void drawLearningCurveUI(){
		drawLearningCurve(0.01);
	}
	public void drawLearningCurve(double fractionOfPointsToKeep) {
		lineChart.getData().clear();
		Map.Entry<Integer,Double> firstEntry					= (Map.Entry<Integer,Double>) learningcurve.entrySet().toArray()[0];		
		XYChart.Series<Number,Number> series 	= new XYChart.Series<>();
		series.getData().add(new XYChart.Data(firstEntry.getKey(), firstEntry.getValue()));
		double pointsToSkip						= learningcurve.size()*fractionOfPointsToKeep;
		System.out.println(pointsToSkip);
		Iterator iter							=learningcurve.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<Integer,Double> point		= (Map.Entry)iter.next();
			series.getData().add(new XYChart.Data(point.getKey(), point.getValue()));
			for(int i=0; i<(int)pointsToSkip;i++) {
				if(iter.hasNext()) {iter.next();}
			}
		}
		
		Map.Entry<Integer,Double> lastEntry					= (Map.Entry<Integer,Double>) learningcurve.entrySet().toArray()[0];		
		series.getData().add(new XYChart.Data(lastEntry.getKey(), lastEntry.getValue()));

		lineChart.getData().add(series);
		
	}
	public void setupCanvases() {
		// Image Preview Canvas
		imageCanvas.setScaleX(15);
		imageCanvas.setScaleY(15);
		imageGc.scale(15, 15);
		ImagePrevGRP.getChildren().add(0, imageCanvas);
		imageGc.clearRect(0, 0, 10, 10);
		imageGc.setFill(Color.BLACK);		
		imageGc.fillRect(0, 0, 10, 10);		
		
		// Feature Preview Canvas
		featureCanvas.setScaleX(15);
		featureCanvas.setScaleY(15);
		featureGc.scale(15, 15);
		FeaturePrevGRP.getChildren().add(0, featureCanvas);
		
		featureGc.clearRect(0, 0, 10, 10);
		featureGc.setFill(Color.BLACK);		
		featureGc.fillRect(0, 0, 10, 10);		
		
		// Input Layer Preview Canvas

		inputLayerCanvas.setScaleX(3.5);
		inputLayerCanvas.setScaleY(3.5);
		inputLayerGc.scale(3.5, 3.5);
		AllInputsPreviewGRP.getChildren().add(0, inputLayerCanvas);
		AllInputsPreviewPN.setContent(AllInputsPreviewGRP);
		AllInputsPreviewPN.setPannable(true);		

		inputLayerGc.clearRect(0, 0, 100, 50);
		inputLayerGc.setFill(Color.BLACK);		
		inputLayerGc.fillRect(0, 0, 100, 50);	
		
		setupLearningCurveChart();
		LearningCurveBox.getChildren().add(lineChart);
		
		

		
		
	}
	public void trainAll() {
		Task<Void> task = new Task<Void>() {
		    @Override public Void call() {
		        int max = 100;
		        for (int i=1; i<=max; i++) {
		            if (isCancelled()) {
		               break;
		            }
		            drawImage(i);		            
		            try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
		        }
		        return null;
		    }
		};
		
		new Thread(task).start();		
	}
	
	public void drawImage(int imageIndex) {
		imageGc.clearRect(0, 0, 100, 100);
		imageGc.setFill(Color.BLACK);		
		imageGc.fillRect(0, 0, 10, 10);
		featureGc.clearRect(0, 0, 10, 10);
		featureGc.setFill(Color.BLACK);		
		featureGc.fillRect(0, 0, 10, 10);		
		
		FullImage imageInstance			= myDataLoader.trainingDataSetList.get(imageIndex);
		PixelWriter pw					= imageGc.getPixelWriter();
		for (int y=0;y<10;y++) {
			for  (int x=0;x<10;x++){
				boolean pixelSign		= imageInstance.imagePixelsBools[x][y];
				if (pixelSign) {System.out.print("X");}
				else {System.out.print("O");}
			}
			System.out.println();
			
		}
		System.out.println("_____________________________________________");
		System.out.println(imageInstance.featureListAsValues+"\n"+imageInstance.featureListAsValues.size()
		+"\n"+imageInstance.labelName);
		// SHOW INPUT VECTOR FOR IMAGE FEATURES
		InputsVectorTA.setText(imageInstance.getInputVector());
		// DRAW IMAGE IN IMAGE PREVIEW CANVAS
		for (ArrayList<Integer>pixel :imageInstance.getPixels()) {
			int pixelValue				= pixel.get(2);
			if (pixelValue==1) {
				pw.setColor(pixel.get(0), pixel.get(1), Color.WHITE);				
				}
		
			}
		// DRAW FEATURE & IMAGE IN FEATURE PREVIEW CANVAS
		if (imageInstance.imageFeatures.size()>0) {
			int featureIndex				= (int)FeatureSelectorSL.getValue();
			drawFeature(imageIndex, featureIndex);
			}
		}
	
	public void drawAllInputsOfDataSet() {
		int canvasHeight				= Integer.parseInt(FeaturesCountTF.getText());
		int canvasWidth					= myDataLoader.trainingDataSetList.size() ;
		inputLayerCanvas.setHeight(canvasHeight);
		inputLayerCanvas.setWidth(canvasWidth);
		inputLayerGc.clearRect(0, 0, canvasWidth, canvasHeight);
		inputLayerGc.setFill(Color.BLACK);		
		inputLayerGc.fillRect(0, 0, canvasWidth, canvasHeight);		
		
//		FullImage imageInstance			= myDataLoader.trainingDataSetList.get(imageIndex);
		PixelWriter pw					= inputLayerGc.getPixelWriter();
				
		for (FullImage image :myDataLoader.trainingDataSetList) {
			int imageIndex				= myDataLoader.trainingDataSetList.indexOf(image);
			for (int j=0; j<Integer.parseInt(FeaturesCountTF.getText());j++) {
				if(image.imageFeatures.get(j).output==1.0) {
					pw.setColor(imageIndex,j, Color.WHITE);
				}
			}
		}
	}
	public void drawAllFeatures() {
		Task<Void> task = new Task<Void>() {
		    @Override public Void call() {
		        int max = 50;
		        for (int i=1; i<=max; i++) {
		            if (isCancelled()) {
		               break;
		            }
		            int imageIndex					= (int)ImageSelectorSL.getValue();
		            drawFeature(imageIndex,i);		            
		            try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
		        }
		        return null;
		    }
		};
		
		new Thread(task).start();
		drawAllInputsOfDataSet();
	}
	public void checkCordinates() {
		TextInputDialog dialog = new TextInputDialog("5,5");
		dialog.setTitle("Draw Pixel On Image");
		dialog.setHeaderText("Please provide a pixel seperated by a comma Example Below:\n 9,1");
		dialog.setContentText("x,y= ");

		// Traditional way to get the response value.
		int imageIndex					= (int)ImageSelectorSL.getValue();
		FullImage imageInstance			= myDataLoader.trainingDataSetList.get(imageIndex);
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){			
			PixelWriter pw2					= imageGc.getPixelWriter();
			String[] pixel						= result.get().split(",");
			int x							= Integer.parseInt(pixel[0]);
			int y							= Integer.parseInt(pixel[1]);
			pw2.setColor(x, y, Color.GREEN);
			System.out.println(imageInstance.imagePixelsBools[x][y]);
			System.out.println(imageInstance.imagePixels[x][y]);
			
		}			
	}
	public void drawFeature(int imageIndex ,int featureIndex) {
		
		featureGc.clearRect(0, 0, 10, 10);
		featureGc.setFill(Color.BLACK);		
		featureGc.fillRect(0, 0, 10, 10);	
		PixelWriter pw2					= featureGc.getPixelWriter();
		FullImage imageInstance			= myDataLoader.trainingDataSetList.get(imageIndex);
		FeatureNode	featureOfImage		= imageInstance.imageFeatures.get(featureIndex);
//		System.out.println(featureOfImage);
		// DRAW IMAGE
		
		for (ArrayList<Integer>pixel :imageInstance.getPixels()) {
			int pixelValue				= pixel.get(2);
			if (pixelValue==1) {
				if (featureOfImage.output==0) {
						pw2.setColor(pixel.get(0), pixel.get(1), Color.WHITE);
					}
				else {
					pw2.setColor(pixel.get(0), pixel.get(1), Color.GREEN);
				}
			}
		
			}		
		// Draw Feature Pixels
		StatusTA.setText(featureOfImage.toString());
		for (model.Pixel pixel : featureOfImage.inputPixels) {
				if (pixel.sign== imageInstance.imagePixelsBools[pixel.x][pixel.y]) {
//				if (pixel.sign) {	
					pw2.setColor(pixel.x, pixel.y, Color.BLUE);
				}
				else {
					pw2.setColor(pixel.x, pixel.y, Color.RED);
				}
		}
	}
	
	public void changeImageInWin() {
		
		int imageIndex					= (int)ImageSelectorSL.getValue();
		int featureIndex				= (int)FeatureSelectorSL.getValue();
		StatusLB.setText("Previewing Image "+imageIndex);
		drawImage(imageIndex);
		drawAllInputsOfDataSet();
		PixelWriter pw					= inputLayerGc.getPixelWriter();
		pw.setColor(imageIndex,featureIndex, Color.rgb(255, 102, 0, 1));
		}
		
	public void changeFeatureInWin() {
		int imageIndex					= (int)ImageSelectorSL.getValue();
		int featureIndex				= (int)FeatureSelectorSL.getValue();
		StatusLB.setText("Previewing Feature "+featureIndex);
		drawFeature(imageIndex, featureIndex);
		drawAllInputsOfDataSet();
		PixelWriter pw					= inputLayerGc.getPixelWriter();
		pw.setColor(imageIndex,featureIndex, Color.rgb(255, 102, 0, 1));		
		
		
				
	}
	
	public void loadDataSet() {
		// Loads Images From Disk 
		totalEpochsCount			= 0;
		learningcurve.clear();
//		System.out.println("Loading Images DataSet");
		int pixelCount				= Integer.parseInt(PixelCountTF.getText());
		int featureCount			= Integer.parseInt(FeaturesCountTF.getText());
		int randomSeed				= Integer.parseInt(RandomTF.getText());
		String trainingFilePath		= System.getProperty("user.dir").replace('\\', '/') + "/image.data";
		myDataLoader.clear();
		myDataLoader.featureCountPerImage= featureCount;
		myDataLoader.pixelCountPerFeature=pixelCount;
		myDataLoader.randomSeed		= randomSeed;
		myDataLoader.loadImageDataSet(trainingFilePath, myDataLoader.trainingDataSetList);
		myDataLoader.dataSetName	= "ImageDataSet";
		StatusTA.setText(myDataLoader.toString());
		StatusTA.appendText("Test Data Set Size = " + myDataLoader.testDataSetList.size());

	
	}
	public void createFeatureProxiesFromLoadedImages() {
		loadDataSet(); //PicksUp
		int pixelCount				= Integer.parseInt(PixelCountTF.getText());
		int featureCount			= Integer.parseInt(FeaturesCountTF.getText());
		
		FeatureSelectorSL.setMax(featureCount-1);
		for (FullImage imageInstance : myDataLoader.trainingDataSetList) {			
//			System.out.println(imageInstance.labelName+imageInstance.imageFeatures.size());			
		}
		StatusTA.insertText(0, "Changed Attributes of DataSet\nPixelCountPerFeature = "+pixelCount
				+"\nFeaturesCountPerImage = "+featureCount+"\n\n\n");
	}
	private String promptUserForChoice(List<String> dialogData, String message) {
		ChoiceDialog<String> dialog = new ChoiceDialog<String>(dialogData.get(0), dialogData);
		dialog.setTitle("");
		dialog.setHeaderText(message);
		Optional<String> result = dialog.showAndWait();
		String selected = "cancelled.";

		if (result.isPresent()) {

			selected = result.get();
		}

		return selected;
	}
	
	public void saveAsPng(String fileName) {
		double scale							= 5;
		Bounds bounds 							= TreeSnapShot.getLayoutBounds();
		WritableImage image 					= new WritableImage(
	            (int) Math.round(bounds.getWidth() * scale),
	            (int) Math.round(bounds.getHeight() * scale));
		
		SnapshotParameters snapshotParams   	= new SnapshotParameters();
		snapshotParams.setFill(javafx.scene.paint.Color.rgb(40, 40, 40, 1));
		snapshotParams.setTransform(javafx.scene.transform.Transform.scale(scale, scale));
		
//	    WritableImage image2 					= TreeP.snapshot(snapshotParams,null);
    	
	    ImageView view 							= new ImageView(TreeSnapShot.snapshot(snapshotParams, image));
	    File file = new File(fileName+".png");
	    
	    try {
	        ImageIO.write(SwingFXUtils.fromFXImage(view.getImage(), null), "png", file);
	    } catch (IOException e) {
	        
	    }
	}	
	public void saveSnapShot() {
		saveAsPng("DTree_" + GeniThresholdTF.getText()+"_geniThreshold");
	}
		
}
