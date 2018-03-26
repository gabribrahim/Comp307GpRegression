package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class Main extends Application {
	private Stage primaryStage;
	
	public void start(Stage primaryStage) {
			this.primaryStage 	= primaryStage;
			MainWindow();
		}
	
	public void MainWindow(){
		
		try {
			FXMLLoader loader 	= new FXMLLoader(Main.class.getResource("dtView.fxml"));
			AnchorPane pane 	= loader.load();
			MainController mainWinController = loader.getController();
			mainWinController.setMain(this);
			Scene winScene = new Scene(pane);
			winScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(winScene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {
		launch(args);
	}
}
