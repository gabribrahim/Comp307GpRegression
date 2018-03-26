package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import model.DataSetsLoader;

public class MainController {
	// UI ELEMENTS
	@FXML TextArea StatusTA;
	@FXML Label MainLabel;
	private Main main;
	private DataSetsLoader myDataLoader = new DataSetsLoader();
	public List<String> featuresList 	= new ArrayList<String>();

	public void setMain(Main main) {
		this.main = main;
		loadDataSet();
//		System.out.println(myDataLoader);

	}
	
	public void loadDataSet() {
		System.out.println("Loading Hep DataSet");
		myDataLoader.dataSetName = "Hepatitis Dataset";
		myDataLoader.clear();

		String trainingFilePath		= System.getProperty("user.dir").replace('\\', '/') + "/hepatitis-training.dat";
		String testFilePath			= System.getProperty("user.dir").replace('\\', '/') + "/hepatitis-test.dat";
		myDataLoader.loadHepDataSet(trainingFilePath, myDataLoader.trainingDataSetList);
		StatusTA.setText(myDataLoader.toString());
		myDataLoader.loadHepDataSet(testFilePath, myDataLoader.testDataSetList);
		StatusTA.appendText("Test Data Set Size = " + myDataLoader.testDataSetList.size());
//		MainLabel.setText("Iris DataSet Loaded");
//
//	
			
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

}
