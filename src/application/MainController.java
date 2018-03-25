package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import model.DataSetsLoader;
import model.KnnClassifier;
import model.LabelledDataInstance;

public class MainController {
	//UI ELEMENTS
	@FXML private  Label MainLabel;
	@FXML private  TextArea StatusTA;
	@FXML private  HBox ChartBox;
	@FXML private  TextField KnnTF;
	@FXML private AnchorPane RootAP;
	private Main main;
	private DataSetsLoader myDataLoader = new DataSetsLoader();
	
	public ScatterChart<Number,Number> scatterChart ;
	public List<String> featuresList = new ArrayList<String>();
	public String xAxisFeature; 
	public String yAxisFeature ;	
	
	
	@SuppressWarnings("static-access")
	public void setMain(Main main) {
		this.main		= main;
		System.out.println(myDataLoader);
		featuresList.add("Petal Width");
		featuresList.add("Petal Length");
		featuresList.add("Sepal Width");
		featuresList.add("Sepal Length");	
		xAxisFeature = "Petal Length";
		yAxisFeature = "Sepal Length";
		final NumberAxis xAxis = new NumberAxis(0, 0, 0.1);
		final NumberAxis yAxis = new NumberAxis(0, 0, 0.1);
		scatterChart			= new ScatterChart<Number,Number>(xAxis,yAxis);

		scatterChart.setAnimated(true);
		scatterChart.getXAxis().setAutoRanging(true);
		scatterChart.getYAxis().setAutoRanging(true);
		scatterChart.setVerticalGridLinesVisible(true);
		ChartBox.setHgrow(scatterChart, Priority.ALWAYS);
		ChartBox.getChildren().add(1, scatterChart);
		LoadIrisDataSet();
		
	}
	

	public void computeKnn2() {
		StatusTA.setText("");
		myDataLoader.myclassifier	= new KnnClassifier(myDataLoader);
		myDataLoader.myclassifier.knnNumber		= Integer.parseInt(KnnTF.getText());
		LabelledDataInstance testInstance = myDataLoader.testDataSetList.get(74);
		myDataLoader.myclassifier.predictClassForTestInstance(testInstance);
	}
	public void computeKnn() {
		StatusTA.setText("");
		
		myDataLoader.myclassifier 							= new KnnClassifier(myDataLoader);
		myDataLoader.myclassifier.knnNumber					= Integer.parseInt(KnnTF.getText());		
		XYChart.Series<Number, Number> correctPredictions 	= scatterChart.getData().get(3);
		XYChart.Series<Number, Number> wrongPredictions 	= scatterChart.getData().get(4);
		correctPredictions.getData().clear();
		wrongPredictions.getData().clear();

		int count					= 1;
		int correctPrediction		= 0;		
		int trainingInstancesCount	= myDataLoader.trainingDataSetList.size();
		for (LabelledDataInstance testInstance : myDataLoader.testDataSetList) {
			float testXfeature		= testInstance.featureListAsValues.get(featuresList.indexOf(xAxisFeature));
			float testYfeature		= testInstance.featureListAsValues.get(featuresList.indexOf(yAxisFeature));
			
			
			String predictedClass	= myDataLoader.myclassifier.predictClassForTestInstance(testInstance);
			testInstance.predictedClass = predictedClass;
			if (predictedClass.equals(testInstance.labelName)) {
//				StatusTA.appendText("Sucess!! TestID:"+count+" Label="+testInstance.labelName
//									+" Prediction="+predictedClass+"\n");
				correctPrediction++;
				correctPredictions.getData().add(new Data<Number,Number>(testXfeature,testYfeature));
				}
			else{
				StatusTA.appendText("FAIL! TestID:"+count+" "+testInstance.labelName
						+"!="+predictedClass+"\n");
				wrongPredictions.getData().add(new Data<Number,Number>(testXfeature,testYfeature));
			}
//			saveAsPng("TestInstance_" + count);
			
//			testSeries.getData().clear();
			
			count++;
		}
		myDataLoader.myclassifier.accuracy					= ((double)correctPrediction/(double)trainingInstancesCount)*100.0;
				
		StatusTA.appendText("Accuracy @K" + myDataLoader.myclassifier.knnNumber + "NN="+myDataLoader.myclassifier.accuracy) ;
		scatterChart.setTitle(myDataLoader.myclassifier.knnNumber + "NN Accuracy =" + myDataLoader.myclassifier.accuracy);
	}
	public void LoadIrisDataSet() {
		System.out.println("Loading IrisDataset");
		myDataLoader.dataSetName = "Iris Dataset";
		myDataLoader.loadIrisDataSet("src/model/iris-training.txt",myDataLoader.trainingDataSetList);		
		myDataLoader.computeRangeForFeaturesInDataSet();
		StatusTA.setText(myDataLoader.toString());
		myDataLoader.loadIrisDataSet("src/model/iris-test.txt",myDataLoader.testDataSetList);
		StatusTA.appendText("Test Data Set Size = " + myDataLoader.testDataSetList.size());
		MainLabel.setText("Iris DataSet Loaded");
		plotIrisTrainingSetOnChart(xAxisFeature,yAxisFeature);
//		System.out.println(this.MainLabel);
	}
	
	private String promptUserForChoice(List<String> dialogData,String message) {		
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
	public void changeAxisFeature() {
	
		xAxisFeature 							= promptUserForChoice(featuresList,"Please Choose X-Axis Feature");
		yAxisFeature 							= promptUserForChoice(featuresList,"X-Axis = " + xAxisFeature +"\nPlease Choose Y-Axis Feature");
		System.out.println(xAxisFeature + yAxisFeature);
		scatterChart.getData().clear();
		plotIrisTrainingSetOnChart(xAxisFeature, yAxisFeature);
		
	}
	
	
	public void saveAsPng(String fileName) {
		SnapshotParameters snapshotParams   	= new SnapshotParameters();
		snapshotParams.setFill(Color.rgb(40, 40, 40, 1));

		
	    WritableImage image 					= scatterChart.snapshot(snapshotParams,null);
    	
	    
	    File file = new File(fileName+".png");

	    try {
	        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	    } catch (IOException e) {
	        
	    }
	}	
	public void saveSnapShot() {
		saveAsPng("Result_" + myDataLoader.myclassifier.knnNumber + "NN_" + xAxisFeature+"_"+yAxisFeature);
	}
	
	
	public void plotIrisTrainingSetOnChart(String xAxisFeature, String yAxisFeature) {
		for (String dataSetClass : myDataLoader.dataSetClasses) {
			
		}
		XYChart.Series<Number,Number> setosa	= new XYChart.Series<>();		
		XYChart.Series<Number,Number> virginica = new XYChart.Series<>();
		XYChart.Series<Number,Number> versicolor= new XYChart.Series<>();
		XYChart.Series<Number,Number> correctPredictions	= new XYChart.Series<>();
		XYChart.Series<Number,Number> wrongPredictions		= new XYChart.Series<>();
		correctPredictions.setName("Correct Predictions");	
		wrongPredictions.setName("Wrong Predictions");	
		versicolor.setName("Versicolor");
		virginica.setName("Virginica");
		setosa.setName("Setosa");
		HashMap<String,Integer> axisOptions		= new HashMap<String,Integer>();
		axisOptions.put("Petal Length", 0);
		axisOptions.put("Petal Width", 1);
		axisOptions.put("Sepal Length", 2);
		axisOptions.put("Sepal Width", 3);
		
		int xAxisFeatureIndex					= axisOptions.get(xAxisFeature);
		int yAxisFeatureIndex					= axisOptions.get(yAxisFeature);
		
		
		// 5 attributes - Petal Length , Petal Width , Sepal Length , Sepal width
		System.out.println( "Training Data Set"+myDataLoader.trainingDataSetList.size());
		for (LabelledDataInstance dataInstance : myDataLoader.trainingDataSetList) {			
			if (dataInstance.labelName.equals("Iris-versicolor")){				
				float floatx = dataInstance.featureListAsValues.get(xAxisFeatureIndex);
				float floaty = dataInstance.featureListAsValues.get(yAxisFeatureIndex);
				versicolor.getData().add(new Data<Number,Number>(floatx,floaty));
			}
			if (dataInstance.labelName.equals("Iris-virginica")){				
				float floatx = dataInstance.featureListAsValues.get(xAxisFeatureIndex);
				float floaty = dataInstance.featureListAsValues.get(yAxisFeatureIndex);
				virginica.getData().add(new Data<Number,Number>(floatx,floaty));
			}			
			if (dataInstance.labelName.equals("Iris-setosa")){				
				float floatx = dataInstance.featureListAsValues.get(xAxisFeatureIndex);
				float floaty = dataInstance.featureListAsValues.get(yAxisFeatureIndex);
				setosa.getData().add(new Data<Number,Number>(floatx,floaty));
			}			
			
		}
		correctPredictions.getData().add(new Data<Number,Number>(0,0));
		wrongPredictions.getData().add(new Data<Number,Number>(0,0));
		scatterChart.getData().add(versicolor);
		scatterChart.getData().add(virginica);
		scatterChart.getData().add(setosa);
		scatterChart.getData().add(correctPredictions);
		scatterChart.getData().add(wrongPredictions);
		scatterChart.getXAxis().setLabel(xAxisFeature);
		scatterChart.getYAxis().setLabel(yAxisFeature);
		
	}
}

