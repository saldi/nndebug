package pl.jclab.nndebug.models;

import java.util.ArrayList;
import java.util.List;

public class NNGraph{

	private List<NNNode> nodes = new ArrayList<>();
	private List<NNEdge> edges = new ArrayList<>();
	private List<NNLayer> layers = new ArrayList<>();

	private int epoch = 0;
	private int iterate = 0;
	private double score = 0;
	private double testLoss = 0;
	private double trainingLoss = 0;

	private int inputNeuron = 0;
	private int outputNeuron = 0;
	private int networkParams = 0;

	public NNGraph(){

	}

	public NNGraph(List<NNNode> nodes, List<NNEdge> edges, List<NNLayer> layers){

		this.nodes = nodes;
		this.edges = edges;
		this.layers = layers;
	}

	public List<NNNode> getNodes(){

		return nodes;
	}

	public void setNodes(List<NNNode> nodes){

		this.nodes = nodes;
	}

	public List<NNEdge> getEdges(){

		return edges;
	}

	public void setEdges(List<NNEdge> edges){

		this.edges = edges;
	}

	public List<NNLayer> getLayers(){

		return layers;
	}

	public void setLayers(List<NNLayer> layers){

		this.layers = layers;
	}


	public NNGraph addLayer(int index, NNLayer layer){

		layers.add(index, layer);
		return this;
	}

	public NNGraph addNode(NNNode node){

		nodes.add(node);
		return this;
	}

	public NNGraph addEdges(NNEdge edge){

		edges.add(edge);
		return this;
	}

	public int getEpoch(){

		return epoch;
	}

	public void setEpoch(int epoch){

		this.epoch = epoch;
	}

	public int getIterate(){

		return iterate;
	}

	public void setIterate(int iterate){

		this.iterate = iterate;
	}

	public double getScore(){

		return score;
	}

	public void setScore(double score){

		this.score = score;
	}

	public double getTestLoss(){

		return testLoss;
	}

	public void setTestLoss(double testLoss){

		this.testLoss = testLoss;
	}

	public double getTrainingLoss(){

		return trainingLoss;
	}

	public void setTrainingLoss(double trainingLoss){

		this.trainingLoss = trainingLoss;
	}

	public int getInputNeuron(){

		return inputNeuron;
	}

	public void setInputNeuron(int inputNeuron){

		this.inputNeuron = inputNeuron;
	}

	public int getOutputNeuron(){

		return outputNeuron;
	}

	public void setOutputNeuron(int outputNeuron){

		this.outputNeuron = outputNeuron;
	}

	public int getNetworkParams(){

		return networkParams;
	}

	public void setNetworkParams(int networkParams){

		this.networkParams = networkParams;
	}

	@Override
	public String toString(){

		return "NNGraph{" +
		"epoch=" + epoch +
		", iterate=" + iterate +
		", testScore=" + score +
		", testLoss=" + testLoss +
		", trainingLoss=" + trainingLoss +
		", inputNeuron=" + inputNeuron +
		", outputNeuron=" + outputNeuron +
		", networkParams=" + networkParams +
		"\n, nodes=" + nodes +
		"\n, edges=" + edges +
		"\n, layers=" + layers +
		'}';
	}
}
