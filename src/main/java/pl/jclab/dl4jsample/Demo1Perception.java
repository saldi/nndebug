package pl.jclab.dl4jsample;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
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

public class Demo1Perception{

	public static void main(String[] args){

		int epoch = 500;
		int inputFeatures = 2;
		int hiddenNeurons = 1;
		int numOutputs = 1;
		int seed = 123;

		INDArray inputs = Nd4j.create(new float[][]{
		{0, 0},
		{0, 1},
		{1, 0},
		{1, 1}
		});

		INDArray outputs = Nd4j.create(new float[][]{
		{0},
		{1},
		{1},
		{0}
		});

		final DataSet trainDataSet = new DataSet(inputs, outputs);
		final DataSet testDataSet = new DataSet(inputs, outputs);

		MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
		.seed(seed)
		.weightInit(WeightInit.RELU)
		.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
		.updater(new Sgd(0.1))
		.list()
		.layer(new DenseLayer.Builder().nIn(inputFeatures)
									   .nOut(hiddenNeurons)
									   .activation(Activation.RELU)
									   .build())
		.layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
			   .activation(Activation.IDENTITY)
			   .nIn(hiddenNeurons)
			   .nOut(numOutputs)
			   .build())
		.pretrain(false)
		.backprop(true)
		.build();


		MultiLayerNetwork net = new MultiLayerNetwork(configuration);
		net.init();

		net.setListeners(NetListenerBuilder.Builder()
										   .name("Demo1 Perception")
										   .data(configuration, net, trainDataSet, testDataSet)
										   .build());

		NnUtil.debugNN(net);

		System.out.println("Fit Network:");
		for (int i = 0; i < epoch; i++){
			net.fit(trainDataSet);
		}


		NnUtil.hr("FeatureMatrix:");
		INDArray output = net.output(testDataSet.getFeatureMatrix());
		System.out.println(output);

		NnUtil.hr("Evaluation:");
		Evaluation eval = new Evaluation(1);
		eval.eval(testDataSet.getLabels(), output);
		System.out.println(eval.stats());
	}
}
