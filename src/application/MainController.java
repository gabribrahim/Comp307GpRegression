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
import model.DataSetsLoader;
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
	@FXML HBox PreviewHB;
	
	
	private Main main;
	private DataSetsLoader myDataLoader = new DataSetsLoader();
	private Canvas canvas 				= new Canvas(10, 10);
	private GraphicsContext gc 			= canvas.getGraphicsContext2D();
	
	public void setMain(Main main) {
		this.main = main;
		PreviewHB.getChildren().add(0, canvas);
		canvas.setScaleX(20);
		canvas.setScaleY(20);
		canvas.setTranslateX(100);
		canvas.setTranslateY(100);		
		gc.clearRect(0, 0, 10, 10);
		gc.setFill(Color.BLACK);		
		gc.fillRect(0, 0, 10, 10);		
		System.out.println(canvas.getHeight());
        loadDataSet();
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
		gc.clearRect(0, 0, 100, 100);
		gc.setFill(Color.BLACK);		
		gc.fillRect(0, 0, 10, 10);			
		StatusLB.setText("Previewing Image "+imageIndex);
		FullImage imageInstance			= myDataLoader.trainingDataSetList.get(imageIndex-1);
		PixelWriter pw					= gc.getPixelWriter();
//		System.out.println(imageInstance.getPixels());
		for (ArrayList<Integer>pixel :imageInstance.getPixels()) {
			int pixelValue				= pixel.get(2);
			if (pixelValue==1) {
				pw.setColor(pixel.get(0), pixel.get(1), Color.WHITE);
				}
		
			}
		}
	public void changeImageInWin() {
		
		int imageIndex					= (int)ImageSelectorSL.getValue();
		drawImage(imageIndex);
		
		}
		

	
	public void loadDataSet() {
		System.out.println("Loading Images DataSet");
		String trainingFilePath		= System.getProperty("user.dir").replace('\\', '/') + "/image.data";
		
		myDataLoader.loadImageDataSet(trainingFilePath, myDataLoader.trainingDataSetList);
		myDataLoader.dataSetName	= "ImageDataSet";
		StatusTA.setText(myDataLoader.toString());
		StatusTA.appendText("Test Data Set Size = " + myDataLoader.testDataSetList.size());

	
	}
	public void createFeatureProxiesFromLoadedImages() {
		for (FullImage imageInstance : myDataLoader.trainingDataSetList) {			
			System.out.println(imageInstance.labelName+imageInstance.featureListAsValues.size());			
		}
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
