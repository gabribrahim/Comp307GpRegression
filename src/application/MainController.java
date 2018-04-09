package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
	@FXML TextField PixelCountTF;
	@FXML TextField FeaturesCountTF;
	@FXML TextArea InputsVectorTA;
	@FXML TextArea WeightsVectorTA;
	
	private Main main;
	private DataSetsLoader myDataLoader = new DataSetsLoader();
	private Canvas imageCanvas 				= new Canvas(10, 10);
	private GraphicsContext imageGc 			= imageCanvas.getGraphicsContext2D();
	private Canvas featureCanvas 				= new Canvas(10, 10);
	private GraphicsContext featureGc 			= featureCanvas.getGraphicsContext2D();
	private BasePNode perceptronNode;
	public void setMain(Main main) {
		this.main = main;
		setupCanvases();
        loadDataSet();
        myDataLoader.trainingDataSetList.get(0).generateFeatures();
	}
	
	public void createPerceptron() {
		perceptronNode							= new BasePNode();
		perceptronNode.inputs					= new ArrayList<>(myDataLoader.trainingDataSetList.get(0).imageFeatures);
		perceptronNode.generateRandomWeights();
		WeightsVectorTA.setText(perceptronNode.getWeightVectorSring());
		
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
	}
	public void drawFeature(int imageIndex ,int featureIndex) {
		
		featureGc.clearRect(0, 0, 10, 10);
		featureGc.setFill(Color.BLACK);		
		featureGc.fillRect(0, 0, 10, 10);	
		PixelWriter pw2					= featureGc.getPixelWriter();
		FullImage imageInstance			= myDataLoader.trainingDataSetList.get(imageIndex);
		FeatureNode	featureOfImage		= imageInstance.imageFeatures.get(featureIndex);
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
				if (pixel.sign) {
					pw2.setColor(pixel.x, pixel.y, Color.BLUE);
				}
				else {
					pw2.setColor(pixel.x, pixel.y, Color.RED);
				}
		}
	}
	
	public void changeImageInWin() {
		
		int imageIndex					= (int)ImageSelectorSL.getValue();
		StatusLB.setText("Previewing Image "+imageIndex);
		drawImage(imageIndex);
		
		}
		
	public void changeFeatureInWin() {
		int imageIndex					= (int)ImageSelectorSL.getValue();
		int featureIndex				= (int)FeatureSelectorSL.getValue();
		StatusLB.setText("Previewing Feature "+featureIndex);
		drawFeature(imageIndex, featureIndex);
		
		
		
				
	}
	
	public void loadDataSet() {
		// Loads Images From Disk 
		System.out.println("Loading Images DataSet");
		int pixelCount				= Integer.parseInt(PixelCountTF.getText());
		int featureCount			= Integer.parseInt(FeaturesCountTF.getText());
		String trainingFilePath		= System.getProperty("user.dir").replace('\\', '/') + "/image.data";
		myDataLoader.clear();
		myDataLoader.featureCountPerImage= featureCount;
		myDataLoader.pixelCountPerFeature=pixelCount;
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
