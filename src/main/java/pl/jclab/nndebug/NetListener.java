package pl.jclab.nndebug;

import javafx.application.Application;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.BaseTrainingListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import pl.jclab.nndebug.config.AppCfg;
import pl.jclab.nndebug.models.NNEdge;
import pl.jclab.nndebug.models.NNGraph;
import pl.jclab.nndebug.models.NNLayer;
import pl.jclab.nndebug.models.NNNode;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Jaroslaw Czub - https://czub.info
 */
public class NetListener extends BaseTrainingListener{

	protected static Logger logger = Logger.getLogger(NetListener.class.getName());

	protected int iterCount = 0;

	protected NNGraph netGraph;
	protected int currentNodeId = 0;
	protected int currentEdgeId = 0;

	protected MultiLayerNetwork multiLayerNetwork;
	protected NetDebugApp netDebugApp;
	protected MultiLayerConfiguration layerConfiguration;

	protected String appName;

	protected int printIterations;

	protected double minIn;
	protected double maxIn;

	protected double minOut;
	protected double maxOut;

	protected MultiLayerConfiguration multiLayerConfiguration;
	protected MultiLayerNetwork net;
	protected DataSet trainData;
	protected DataSet testData;

	public NetListener(String appName, MultiLayerConfiguration multiLayerConfiguration, MultiLayerNetwork net, DataSet trainData, DataSet testData,
					   int printIterations,
					   double minIn, double maxIn,
					   double minOut, double maxOut){

		if (printIterations <= 0){
			printIterations = 1;
		}
		this.appName = appName;
		this.printIterations = printIterations;
		this.multiLayerConfiguration = multiLayerConfiguration;
		this.multiLayerNetwork = net;
		this.trainData = trainData;
		this.testData = testData;

		this.minIn = minIn;
		this.maxIn = maxIn;

		this.minOut = minOut;
		this.maxOut = maxOut;

		initializeModel(net);
		initializeDebug();
	}


	@Override
	public void iterationDone(Model model, int iteration, int epoch){

		if (iteration % printIterations == 0){

			updateModel(model);

			final double score = model.score();
			final int batchSize = model.batchSize();

			netGraph.setScore(score);
			netGraph.setIterate(iteration);
			netGraph.setEpoch(epoch);

			System.out.println("Epoch: " + epoch + " Iteration: " + iteration + " iterCount: " + iterCount + " Score: " + score);
			updateDebugApp();

			iterCount++;
		}
	}

	@Override
	public void onEpochStart(Model model){

	}

	@Override
	public void onEpochEnd(Model model){

	}

	@Override
	public void onForwardPass(Model model, List<INDArray> activations){

	}

	@Override
	public void onForwardPass(Model model, Map<String, INDArray> activations){

	}

	@Override
	public void onGradientCalculation(Model model){

	}

	@Override
	public void onBackwardPass(Model model){

	}


	private synchronized void initializeDebug(){

		if (netDebugApp != null){
			return;
		}

		NetDebugApp.initalize(this);

		new Thread(() -> Application.launch(NetDebugApp.class)).start();
		int count = 0;
		int sleepMls = 20;
		while (NetDebugApp.instance == null){
			count++;
			try{
				Thread.sleep(sleepMls);
			} catch (InterruptedException exc){
				Thread.interrupted();
			}
		}

		double startInSec = (count * sleepMls) / 1000.0;
		netDebugApp = NetDebugApp.instance;
		System.out.println("NetDebug started in " + startInSec + " sec");
	}

	protected synchronized void initializeModel(Model model){

		currentEdgeId = 0;

		multiLayerNetwork = (MultiLayerNetwork) model;
		layerConfiguration = multiLayerNetwork.getLayerWiseConfigurations();

		netGraph = new NNGraph();
		netGraph.setNetworkParams(multiLayerNetwork.numParams());

		//
		// Read Neutral Network structure and params
		//
		final Map<String, INDArray> paramTable = model.paramTable();
		for (Map.Entry<String, INDArray> entry : paramTable.entrySet()){

			String param = entry.getKey();
			INDArray value = entry.getValue().dup();

			if (Character.isDigit(param.charAt(0))){

				if ('_' == param.charAt(1) && ('W' == param.charAt(2) || 'b' == param.charAt(2))){

					boolean containsWeights = 'W' == param.charAt(2);

					int layerIdx = Character.getNumericValue(param.charAt(0)) + 1;
					final NeuralNetConfiguration conf = layerConfiguration.getConf(layerIdx - 1);
					final Layer layer = layerConfiguration.getConf(layerIdx - 1).getLayer();

					if (iterCount == 0){
						if (layerIdx == 1 && containsWeights){
							readInputLayer(multiLayerNetwork, netGraph, layer, value);
						}
						if (containsWeights){
							readLayer(multiLayerNetwork, netGraph, layer, layerIdx, value);
						} else {
							updateBias(multiLayerNetwork, netGraph, layer, layerIdx, value);
						}
						// System.out.println("Param: " + param + " ContainsWeights: " + containsWeights + " LayerIdx: " + layerIdx + " Type: " + layer.getClass().getSimpleName()+ " conf: " + conf.toString());
					}
				}
			}
		}
	}

	private void readInputLayer(MultiLayerNetwork multiLayerNetwork, NNGraph netGraph, Layer layer, INDArray value){


		String activation = "input";
		String layerName = multiLayerNetwork.getLayer(0).conf().getLayer().getLayerName();

		NNLayer inputLayer = new NNLayer();
		inputLayer.setIndex(0);
		inputLayer.setName("Input");
		inputLayer.setParams(multiLayerNetwork.getLayer(0).numParams());
		netGraph.getLayers().add(inputLayer);

		final int numOfNodes = value.rows();
		netGraph.setInputNeuron(numOfNodes);

		for (int i = 0; i < numOfNodes; i++){

			NNNode node = new NNNode();
			node.setId("" + currentNodeId++);
			node.setBias("");
			node.setName(layerName + "_" + i);
			node.setActivationFunction(activation);
			node.setLayer(0);

			netGraph.getNodes().add(node);
			inputLayer.getNodes().add(node);
		}

	}

	private void readLayer(MultiLayerNetwork multiLayerNetwork, NNGraph netGraph, Layer layer, int layerIdx, INDArray value){

		//
		// Add node to graph
		//
		String activation = "";
		if (layer instanceof DenseLayer){
			activation = ((DenseLayer) layer).getActivationFn().toString();
		} else if (layer instanceof OutputLayer){
			activation = ((OutputLayer) layer).getActivationFn().toString();
		}
		String layerName = multiLayerNetwork.getLayer(layerIdx - 1).conf().getLayer().getLayerName();

		NNLayer currentLayer = new NNLayer();
		currentLayer.setIndex(layerIdx);
		currentLayer.setName(layerName);
		currentLayer.setParams(multiLayerNetwork.getLayer(layerIdx - 1).numParams());

		netGraph.getLayers().add(currentLayer);

		final int numOfNodes = value.columns();
		netGraph.setOutputNeuron(numOfNodes);

		for (int i = 0; i < numOfNodes; i++){
			NNNode node = new NNNode();
			node.setId("" + currentNodeId++);
			node.setActivationFunction(activation);
			node.setBias("");
			node.setName(layerName + "_" + i);
			node.setLayer(layerIdx);

			currentLayer.getNodes().add(node);
			netGraph.getNodes().add(node);
		}

		//
		// Add edge to graph
		//
		NNLayer prevLayer = netGraph.getLayers().get(layerIdx - 1);
		int numOfPreviousLayerNodes = prevLayer.getNodes().size();

		System.out.println(
		"ReadLayer - layerIdx: " + layerIdx + " Nodes: " + numOfNodes + " previousLayerNodes: " + numOfPreviousLayerNodes
		+ " nodes: " + currentLayer.getNodes().size() + " prev nodes: " + prevLayer.getNodes().size());


		for (int row = 0; row < numOfPreviousLayerNodes; row++){
			for (int col = 0; col < numOfNodes; col++){
				NNEdge edge = new NNEdge();
				edge.setDirection("to");
				edge.setFrom(prevLayer.getNodes().get(row).getId());
				edge.setTo(currentLayer.getNodes().get(col).getId());
				edge.setId("" + currentEdgeId);
				edge.setWeight(Math.round(value.getDouble(row, col) * 100) / 100d);
				netGraph.getEdges().add(edge);
				currentEdgeId++;
			}
		}
	}

	private void updateBias(MultiLayerNetwork multiLayerNetwork, NNGraph netGraph, Layer layer, int layerIdx, INDArray value){

		NNLayer curentLayer = netGraph.getLayers().get(layerIdx);
		int numOfBias = value.columns();
		for (int bIdx = 0; bIdx < numOfBias; bIdx++){
			NNNode neuralNetNode = curentLayer.getNodes().get(bIdx);
			double bias = Math.round(value.getDouble(bIdx) * 100) / 100d;
			neuralNetNode.setBias("" + bias);
		}
	}

	private void updateEdge(MultiLayerNetwork multiLayerNetwork, NNGraph netGraph, Layer layer, int layerIdx, INDArray value){

		int numOfNodes = value.columns();
		NNLayer prevLayer = netGraph.getLayers().get(layerIdx - 1);
		int numOfPreviousLayerNodes = prevLayer.getNodes().size();

		//		System.out.println("UpdateEdge - layerIdx: " + layerIdx + " numOfNodes: " + numOfNodes + " numOfPreviousLayerNodes: " + numOfPreviousLayerNodes);

		for (int row = 0; row < numOfPreviousLayerNodes; row++){
			for (int col = 0; col < numOfNodes; col++){
				NNEdge edge = netGraph.getEdges().get(currentEdgeId);
				edge.setWeight(Math.round(value.getDouble(row, col) * 100) / 100d);
				currentEdgeId++;
			}
		}

	}

	private synchronized void updateModel(Model model){

		currentEdgeId = 0;
		final Map<String, INDArray> paramTable = model.paramTable();
		for (Map.Entry<String, INDArray> entry : paramTable.entrySet()){

			String param = entry.getKey();
			INDArray value = entry.getValue().dup();

			if (Character.isDigit(param.charAt(0))){

				if ('_' == param.charAt(1) && ('W' == param.charAt(2) || 'b' == param.charAt(2))){

					boolean containsWeights = 'W' == param.charAt(2);

					int layerIdx = Character.getNumericValue(param.charAt(0)) + 1;
					final NeuralNetConfiguration conf = layerConfiguration.getConf(layerIdx - 1);
					final Layer layer = layerConfiguration.getConf(layerIdx - 1).getLayer();

					if (iterCount > 0 && layerIdx > 0){
						if (containsWeights){
							updateEdge(multiLayerNetwork, netGraph, layer, layerIdx, value);
						} else {
							updateBias(multiLayerNetwork, netGraph, layer, layerIdx, value);
						}
						//						System.out.println("Param: " + param + " ContainsWeights: " + containsWeights + " LayerIdx: " + layerIdx + " Type: " + layer.getClass().getSimpleName());
					}
				}
			}
		}
	}

	private void updateDebugApp(){

		if (netDebugApp != null){

			netDebugApp.update();

			//
			// Wait for action
			//
			if (AppCfg.pauseMode){

				while (!AppCfg.stepMode && AppCfg.pauseMode){
					try{
						Thread.sleep(100);
					} catch (InterruptedException e){
						e.printStackTrace();
					}
				}
				AppCfg.stepMode = false;
			} else if (AppCfg.speed > 1){
				try{
					Thread.sleep(10 * AppCfg.speed);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
			}

			while (AppCfg.updateProcess){
				try{
					Thread.sleep(10);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
			}

		}
	}

}
