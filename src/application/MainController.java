package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.GPProblem;

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
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import model.DataSetsLoader;
import model.Processor;



public class MainController {
	// UI ELEMENTS
	@FXML TextArea StatusTA;
	@FXML Label MainLabel;		
	@FXML Label StatusLB;
	@FXML HBox PreviewHB;
	@FXML TextField MinDepthTF;
	@FXML TextField MaxDepthTF;
	@FXML TextField EpochsTF;
	@FXML TextField MaxNodesTF;
	@FXML VBox LearningCurveBox;
	@FXML TextField PopSizeTF;
	@FXML TextField CrossOverTF;
	@FXML TextField ReproduceTF;
	@FXML TextField MutateTF;
	@FXML TextField NewChromTF;
	@FXML VBox SnapShotPreviewVB;
	private Main main;
    private NumberAxis xAxis 					= new NumberAxis();
    private NumberAxis yAxis 					= new NumberAxis();
    private LineChart<Number,Number> lineChart  = new LineChart<Number,Number>(xAxis,yAxis);	
    private Processor myModel ;
	public DataSetsLoader myDataLoader = new DataSetsLoader();
	private int totalEpochsCount				= 0;
	public LinkedHashMap<Integer, Double> learningcurve=new LinkedHashMap<>();
		
	
	public void setMain(Main main) {
		this.main = main;
		try {
			myModel									= new Processor();	
			myModel.uiWin							= this;
		} catch (InvalidConfigurationException e) {			
			e.printStackTrace();
		}
//        loadDataSet();
		myModel.getInputsFromFile();
		setupLearningCurveChart();
		LearningCurveBox.getChildren().add(lineChart);
		LearningCurveBox.setVgrow(lineChart, Priority.ALWAYS);
		drawDataSet();
		
     
        
	}
	public int getMaxNodes() {
		int maxNodes				= Integer.parseInt(MaxNodesTF.getText());
		return maxNodes;
	}
	public void evoloveForEpochs() {
		int epochs					= Integer.parseInt(EpochsTF.getText());
		
		myModel.evolveForNEpochs(epochs);
		StatusLB.setText("Ephoch :"+myModel.epochCounter);
		appendToStatusText(myModel.outputSolution(myModel.gp.getAllTimeBest()));

	}
	public void initPopulation() {
		try {
			myModel						= new Processor();
			myModel.uiWin				= this;			
			myModel.config.setMinInitDepth(Integer.parseInt(MinDepthTF.getText()));
			myModel.config.setMaxInitDepth(Integer.parseInt(MaxDepthTF.getText()));
			myModel.config.setPopulationSize(Integer.parseInt(PopSizeTF.getText()));
			myModel.config.setMaxCrossoverDepth(Integer.parseInt(MaxDepthTF.getText()));
			myModel.config.setMutationProb(Float.parseFloat(CrossOverTF.getText()));
			myModel.config.setCrossoverProb(Float.parseFloat(MutateTF.getText()));	
			myModel.config.setReproductionProb(Float.parseFloat(ReproduceTF.getText()));
			myModel.config.setNewChromsPercent(Float.parseFloat(NewChromTF.getText()));			
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myModel.getInputsFromFile();
		myModel.initialisePopulation();
		drawDataSet();
	}
	
	public void resampleLearningCurve() {
		TextInputDialog dialog = new TextInputDialog("0.01");
		dialog.setTitle("Resample Learning Curve");
		dialog.setHeaderText("fraction of points to Skip");
		dialog.setContentText("double fraction = ");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			
		}		
		
	}	

	
	public void updateLearningCurveChart(String Message) {
		StatusLB.setText(Message);
	}
	public void drawDataSet() {
		lineChart.getData().clear();
		
		XYChart.Series<Number,Number> series 	= new XYChart.Series<>();
		series.setName("Input Data Set");
		for(int i=0; i<myModel.INPUT_1.size();i++) {			
			series.getData().add(new XYChart.Data(myModel.INPUT_1.get(i), myModel.OUTPUT.get(i)));
		}
		lineChart.getData().add(series);
		
	}	

	public void drawPredictedSet() {
//		lineChart.getData().clear();
		
		XYChart.Series<Number,Number> series 	= new XYChart.Series<>();
		series.setName("Predicted Data Set");
		ArrayList<Double> predictedOutput		= myModel.generateCheckPoints();
		for(int i=0; i<myModel.INPUT_1.size();i++) {			
			series.getData().add(new XYChart.Data(myModel.INPUT_1.get(i), predictedOutput.get(i)));
		}
		lineChart.getData().add(series);
		
	}	
	
	
	
	public void setupLearningCurveChart() {
        xAxis.setLabel("X");
        yAxis.setLabel("Y");        
//        lineChart.setTitle("Learning Curve");
        lineChart.setCreateSymbols(false);
        lineChart.setAnimated(false);
        lineChart.getXAxis().setAutoRanging(true);        
		lineChart.getYAxis().setAutoRanging(true);
		lineChart.setVerticalGridLinesVisible(true);   
		

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
		Bounds bounds 							= LearningCurveBox.getLayoutBounds();
		WritableImage image 					= new WritableImage(
	            (int) Math.round(bounds.getWidth() * scale),
	            (int) Math.round(bounds.getHeight() * scale));
		
		SnapshotParameters snapshotParams   	= new SnapshotParameters();
		snapshotParams.setFill(javafx.scene.paint.Color.rgb(40, 40, 40, 1));
		snapshotParams.setTransform(javafx.scene.transform.Transform.scale(scale, scale));
		
//	    WritableImage image2 					= TreeP.snapshot(snapshotParams,null);
    	
	    ImageView view 							= new ImageView(LearningCurveBox.snapshot(snapshotParams, image));
	    File file = new File(fileName+".png");
	    
	    try {
	        ImageIO.write(SwingFXUtils.fromFXImage(view.getImage(), null), "png", file);
	    } catch (IOException e) {
	        
	    }
	}	
	public void saveSnapShot() {
		saveAsPng("LR_" + EpochsTF.getText()+"_EPC_");
	}
		
}
