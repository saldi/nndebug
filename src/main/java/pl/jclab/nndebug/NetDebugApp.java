package pl.jclab.nndebug;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import pl.jclab.nndebug.config.AppCfg;
import pl.jclab.nndebug.graph.Graph;
import pl.jclab.nndebug.graph.Model;
import pl.jclab.nndebug.graph.draws.Cell;
import pl.jclab.nndebug.graph.draws.Edge;
import pl.jclab.nndebug.graph.draws.NodeCell;
import pl.jclab.nndebug.graph.layout.GraphLayout;
import pl.jclab.nndebug.graph.layout.Layout;
import pl.jclab.nndebug.gui.*;
import pl.jclab.nndebug.models.NNGraph;

import java.net.URL;
import java.util.List;

import static pl.jclab.nndebug.config.StyleCfg.*;

/**
 * @author Jaroslaw Czub - https://czub.info
 */
public class NetDebugApp extends Application{

	static String appName;

	static NNGraph netGraph;
	static MultiLayerConfiguration multiLayerConfiguration;
	static MultiLayerNetwork network;
	static DataSet trainData;
	static DataSet testData;
	static NetDebugApp instance;

	public static double minIn = -0.1;
	public static double maxIn = 1.1;

	public static double minOut = -0.1;
	public static double maxOut = 1.1;

	protected Graph graph = new Graph();
	protected Model model;

	protected ScrollPane centerScrollPane = new ScrollPane();
	protected GridPane topGridPane = new GridPane();
	protected GridPane leftGridPane = new GridPane();
	protected GridPane rightGridPane = new GridPane();
	protected GridPane bottomGridPane = new GridPane();

	protected ControllPanel controllPanel;
	protected InfoPanel infoPanel;

	protected EvalInfo evalInfoTest;
	protected EvalInfo evalInfoTrain;
	protected ConfigPanel configPanel;

	protected Evaluation evalTest;
	protected Evaluation evalTrain;

	protected NetworkSpace networkSpace;
	protected TrendLineChart trendLineChart;
	protected DataChart dataChart;

	public NetDebugApp(){

	}

	public static void initalize(NetListener netListener){

		NetDebugApp.netGraph = netListener.netGraph;
		NetDebugApp.multiLayerConfiguration = netListener.multiLayerConfiguration;
		NetDebugApp.network = netListener.multiLayerNetwork;
		NetDebugApp.trainData = netListener.trainData;
		NetDebugApp.testData = netListener.testData;
	}

	@Override
	public void start(Stage primaryStage) throws Exception{

		BorderPane root = initializeMainPane();

		AppCfg.updateProcess = true;

		initializeStage(primaryStage, root);
		initializeGuiComponents();

		instance = this;

		AppCfg.updateProcess = false;
	}

	public void initializeGuiComponents(){

		model = graph.getModel();
		graph.beginUpdate();
		model.addGraph(netGraph);
		graph.endUpdate();

		Layout layout = new GraphLayout(graph);
		layout.execute();

		ControllPanel controllPanel = new ControllPanel();
		infoPanel = new InfoPanel(netGraph);

		evalInfoTest = new EvalInfo("Test");
		evalInfoTrain = new EvalInfo("Train");
		configPanel = new ConfigPanel(model);

		//
		// Left Control
		//

		dataChart = new DataChart("DataChart", netGraph, network, testData);

		//
		// Area image
		//
		evalTest = new Evaluation(netGraph.getOutputNeuron());
		evalTrain = new Evaluation(netGraph.getOutputNeuron());

		networkSpace = new NetworkSpace("Network Space", netGraph, network);
		trendLineChart = new TrendLineChart("Iteration vs Score", netGraph, network);

		//
		// Top
		//
		topGridPane.add(controllPanel, 0, 0);
		topGridPane.add(infoPanel, 1, 0);

		//
		// Left
		//
		leftGridPane.add(evalInfoTest, 0, 0);
		leftGridPane.add(evalInfoTrain, 0, 1);
		leftGridPane.add(configPanel, 0, 4);

		//
		// Right
		//
		rightGridPane.add(dataChart, 0, 0);

		//
		// Bottom
		//
		bottomGridPane.add(networkSpace, 0, 0);
		bottomGridPane.add(trendLineChart, 1, 0);
	}

	public void initializeStage(Stage primaryStage, BorderPane root){

		primaryStage.setTitle(appName);

		Scene scene;

		if (primaryStageMaximized){
			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();
			primaryStage.setWidth(bounds.getWidth());
			primaryStage.setHeight(bounds.getHeight());
			primaryStage.setMaximized(true);
			scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
		} else {
			primaryStage.setWidth(primaryStageWidth);
			primaryStage.setHeight(primaryStageHeight);
			primaryStage.setMaximized(false);
			scene = new Scene(root, primaryStageWidth, primaryStageHeight);
		}

		final URL resource = getClass().getClassLoader().getResource("application.css");
		if (resource != null){
			scene.getStylesheets().add(resource.toExternalForm());
		}

		primaryStage.setScene(scene);
				primaryStage.show();
//		primaryStage.hide();
		primaryStage.setOnCloseRequest(we -> System.exit(0));
		Platform.setImplicitExit(true);
	}

	@NotNull
	public BorderPane initializeMainPane(){

		BorderPane root = new BorderPane();

		topGridPane = new GridPane();
		topGridPane.setHgap(5);
		topGridPane.setVgap(5);
		topGridPane.setPadding(new Insets(5));

		leftGridPane = new GridPane();
		leftGridPane.setHgap(5);
		leftGridPane.setVgap(5);
		leftGridPane.setPadding(new Insets(5));

		rightGridPane = new GridPane();
		rightGridPane.setHgap(5);
		rightGridPane.setVgap(5);
		rightGridPane.setPadding(new Insets(5));

		bottomGridPane = new GridPane();
		bottomGridPane.setHgap(5);
		bottomGridPane.setVgap(5);
		bottomGridPane.setPadding(new Insets(5));
		bottomGridPane.prefHeight(bottomPaneHeight);
		bottomGridPane.minHeight(bottomPaneHeight);
		bottomGridPane.maxHeight(bottomPaneHeight);

		graph = new Graph();
		centerScrollPane = graph.getScrollPane();

		ScrollPane leftScrollPane = new ScrollPane(leftGridPane);
		leftScrollPane.setMinWidth(leftPaneWidth);
		leftScrollPane.setMinHeight(leftPaneHeight);
		leftScrollPane.setPrefWidth(leftPaneWidth);
		leftScrollPane.setPrefHeight(leftPaneHeight);

		ScrollPane rightScrollPane = new ScrollPane(rightGridPane);
		rightScrollPane.setMinWidth(rightPaneWidth);
		rightScrollPane.setMinHeight(rightPaneHeight);
		rightScrollPane.setPrefWidth(rightPaneWidth);
		rightScrollPane.setPrefHeight(rightPaneHeight);

		root.setCenter(centerScrollPane);
		root.setTop(topGridPane);
		root.setLeft(leftScrollPane);
		root.setRight(rightScrollPane);
		//		root.setLeft(leftGridPane);
		//		root.setRight(rightGridPane);
		root.setBottom(bottomGridPane);
		return root;
	}

	public void update(){

		AppCfg.updateProcess  = true;

		Platform.runLater(() -> {

			if (model != null){

				List<Edge> edges = model.getAllEdges();
				if (edges != null){
					for (Edge edge : edges){
						edge.update();
					}
				}

				final List<Cell> cells = model.getAllCells();
				if (cells != null){
					for (Cell cell : cells){
						if (cell instanceof NodeCell){
							((NodeCell) cell).update();
						}
					}
				}


				//
				// Calculate data
				//
				final INDArray features = testData.getFeatureMatrix();
				final INDArray labels = testData.getLabels();
				final INDArray predicted = network.output(features);

				//
				// Evaluation test data
				//
				evalTest = new Evaluation(netGraph.getOutputNeuron());
				evalTest.eval(labels, predicted);

				// Update GUI
				evalInfoTest.update(netGraph, evalTest);

				//
				// Evaluation train data
				//
				final INDArray trainFeatures = trainData.getFeatureMatrix();
				final INDArray trainLabels = trainData.getLabels();
				final INDArray trainPredicted = network.output(trainFeatures);

				evalTrain = new Evaluation(netGraph.getOutputNeuron());
				evalTrain.eval(trainLabels, trainPredicted);

				// Update GUI
				infoPanel.update();

				dataChart.update(testData);
				evalInfoTrain.update(netGraph, evalTrain);

				//
				// All points
				//
				networkSpace.update();

				//
				// Update XYLIne chart
				//
				trendLineChart.update(evalTrain, evalTest);
			}

			AppCfg.updateProcess = false;
		});
	}
}
