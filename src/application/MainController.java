package application;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import model.DataSetsLoader;
import model.Processor;


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
	@FXML TextField HiddenLayersCountTF;
	@FXML TextField NeurounsCountPerHlayerTF;
	@FXML TextArea InputsVectorTA;
	@FXML TextArea WeightsVectorTA;
	@FXML ScrollPane AllInputsPreviewPN;
	@FXML TextField EpochsTF;
	@FXML TextField LearningRateTF;
	@FXML VBox LearningCurveBox;
	@FXML TextField MomentumTF;
	@FXML VBox SnapShotPreviewVB;
	private Main main;
	public DataSetsLoader myDataLoader = new DataSetsLoader();
	private int totalEpochsCount				= 0;
	private LinkedHashMap<Integer, Double> learningcurve=new LinkedHashMap<>();
	private Processor myModel;			
	public void setMain(Main main) {
		this.main = main;

//        loadDataSet();
		myModel							= new Processor(this);
		myDataLoader.loadIrisDataSet(System.getProperty("user.dir").replace('\\', '/') + "/iris-training.txt",myDataLoader.trainingDataSetList);
		myDataLoader.writeIrisDataSetForNN();
        myModel.buildNNTest();        
        
	}
	public void buildNNFromUiAttrs() {
		myModel.buildNNTest2(Integer.parseInt(HiddenLayersCountTF.getText()),Integer.parseInt(NeurounsCountPerHlayerTF.getText()));
	}
	public double getLearningRate() {
		return Double.parseDouble(LearningRateTF.getText());
	}
	
	public double getMomentum() {
		return Double.parseDouble(MomentumTF.getText());
	}
	public int getEpochs() {
		return Integer.parseInt(EpochsTF.getText());
	}
	public void runNN() {
		myModel.run();
	}
	public void appendToStatusText(String message) {
		StatusTA.setText(message+"\n");
	}
	public void loadDataSet() {
		// Loads Images From Disk 
		totalEpochsCount			= 0;
		learningcurve.clear();
//		System.out.println("Loading Images DataSet");
		String trainingFilePath		= System.getProperty("user.dir").replace('\\', '/') + "/image.data";
		myDataLoader.clear();
		myDataLoader.dataSetName	= "ImageDataSet";
		StatusTA.setText(myDataLoader.toString());

	
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
		Bounds bounds 							= SnapShotPreviewVB.getLayoutBounds();
		WritableImage image 					= new WritableImage(
	            (int) Math.round(bounds.getWidth() * scale),
	            (int) Math.round(bounds.getHeight() * scale));
		
		SnapshotParameters snapshotParams   	= new SnapshotParameters();
		snapshotParams.setFill(javafx.scene.paint.Color.rgb(40, 40, 40, 1));
		snapshotParams.setTransform(javafx.scene.transform.Transform.scale(scale, scale));
		
//	    WritableImage image2 					= TreeP.snapshot(snapshotParams,null);
    	
	    ImageView view 							= new ImageView(SnapShotPreviewVB.snapshot(snapshotParams, image));
	    File file = new File(fileName+".png");
	    
	    try {
	        ImageIO.write(SwingFXUtils.fromFXImage(view.getImage(), null), "png", file);
	    } catch (IOException e) {
	        
	    }
	}	
	public void saveSnapShot() {
		saveAsPng("LR_" + LearningRateTF.getText()+"_TotalEpochs_"+totalEpochsCount);
	}
		
}
