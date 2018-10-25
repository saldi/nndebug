package pl.jclab.dl4jsample;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import pl.jclab.dl4jsample.utils.NnUtil;
import pl.jclab.nndebug.NetListenerBuilder;

public class Demo2XorTwoOutputNeurons{

	public static void main(String[] args){

		int epoch = 500;
		int inputFeatures = 2;
		int hiddenNeurons = 3;
		int numOutputs = 2;
		int seed = 103;

		// Features
		INDArray inputs = Nd4j.create(new float[][]{
			{0, 0},
			{0, 1},
			{1, 0},
			{1, 1}
		});

		// Labels
		INDArray outputs = Nd4j.create(new float[][]{
			{1, 0},
			{0, 1},
			{0, 1},
			{1, 0}
		});

		final DataSet trainDataSet = new DataSet(inputs, outputs);
		final DataSet testDataSet = new DataSet(inputs, outputs);



		MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
		.seed(seed)
		.updater(new Sgd(0.1))
		.biasInit(0)
		.miniBatch(false)
		.list()
		.layer(new DenseLayer.Builder().nIn(inputFeatures)
									   .nOut(hiddenNeurons)
									   .activation(Activation.RELU)
									   .weightInit(WeightInit.DISTRIBUTION)
									   .dist(new UniformDistribution(0, 1))
									   .name("HiddenLayer")
									   .build())
		.layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
			   .activation(Activation.SOFTMAX)
			   .weightInit(WeightInit.DISTRIBUTION)
			   .dist(new UniformDistribution(0, 1))
			   .nIn(hiddenNeurons)
			   .nOut(numOutputs)
			   .name("Output")
			   .build())
		.pretrain(false)
		.backprop(true)
		.build();

		MultiLayerNetwork net = new MultiLayerNetwork(configuration);
		net.init();

		net.setListeners(NetListenerBuilder.Builder()
										   .name("Demo2 XOR2")
										   .data(configuration, net, trainDataSet, testDataSet)
										   .build());

		NnUtil.debugNN(net);

		System.out.println("Fit Network:");
		for (int i = 0; i < epoch; i++){
			net.fit(trainDataSet);
		}

		NnUtil.hr("FeatureMatrix:");
		INDArray output = net.output(trainDataSet.getFeatureMatrix());
		System.out.println(output);

		NnUtil.hr("Evaluation:");
		Evaluation eval = new Evaluation(2);
		eval.eval(trainDataSet.getLabels(), output);
		System.out.println(eval.stats());

		NnUtil.hr("Result:");
		NnUtil.xorDebug(inputs, net, 2);
	}

}
