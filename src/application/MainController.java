package application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JPanel;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import model.DataSetsLoader;
import model.DtNode;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.demos.TreeView;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;

public class MainController {
	// UI ELEMENTS
	@FXML TextArea StatusTA;
	@FXML Label MainLabel;	
	@FXML SwingNode TreeP;
	@FXML HBox ChartBox;
	private Main main;
	private DataSetsLoader myDataLoader = new DataSetsLoader();
	public List<String> featuresList 	= new ArrayList<String>();
	public Node treeRootVisualNode;
	public Tree decisionTreeModel;
	public TreeView decisionTreeView;
	public void setMain(Main main) {
		this.main = main;
//		loadDataSet();		
		decisionTreeModel					= new Tree();
		
		treeRootVisualNode					= decisionTreeModel.addRoot();
		decisionTreeModel.addColumn("label",String.class,"Root");
		decisionTreeView           			= new TreeView(decisionTreeModel,"label");
		JPanel panel 						= new JPanel(new BorderLayout());		
		panel.add(decisionTreeView);
		decisionTreeView.setBackground(Color.darkGray);
		TreeP.setContent(panel);
		decisionTreeView.getVisualization().run("repaint");
		decisionTreeView.getVisualization().run("color");
		decisionTreeView.getVisualization().run("layout");
		decisionTreeView.getVisualization().run("fullPaint");
//		buildTree();
//		TestSN.setContent(graphComponent);

		
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

	
	}
	public void zoomToFitTree() {
    	
		Display display = (Display)decisionTreeView;
        Visualization vis = display.getVisualization();
        Rectangle2D bounds = vis.getBounds("_all_");
        GraphicsLib.expand(bounds, 50 + (int)(1/display.getScale()));
        DisplayLib.fitViewToBounds(display, bounds, 100);
        System.out.println(display);
        		
	}
	public void expandAllNode() {
		decisionTreeView.getVisualization().setValue("_all_", null, VisualItem.EXPANDED, true);
		decisionTreeView.setOrientation(2);
		decisionTreeView.getVisualization().run("treeLayout");
		decisionTreeView.getVisualization().run("repaint");
		decisionTreeView.getVisualization().run("repaint");
		decisionTreeView.getVisualization().run("color");
		decisionTreeView.getVisualization().run("layout");
		decisionTreeView.getVisualization().run("fullPaint");
		decisionTreeView.getVisualization().run("subLayout");
		decisionTreeView.getVisualization().run("textColor");
		decisionTreeView.getVisualization().run("animatePaint");
		decisionTreeView.getVisualization().run("animate");
		decisionTreeView.getVisualization().run("edgeColor");
		System.out.println("AA");
	}

	public void buildTree() {
		ArrayList<String> attrs						= new ArrayList<String>(myDataLoader.dataSetAttrsLabels);
		ArrayList<String> originalAttrs				= new ArrayList<String>(myDataLoader.dataSetAttrsLabels);
		
		DtNode rootNode								= new DtNode(myDataLoader.trainingDataSetList,attrs,originalAttrs);
		rootNode.branchNode();
		rootNode.visualNode(treeRootVisualNode,decisionTreeModel);
		expandAllNode();
		zoomToFitTree();
//		decisionTreeView.setOrientation(2);

		StatusTA.setText(rootNode.report());
		
		
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
