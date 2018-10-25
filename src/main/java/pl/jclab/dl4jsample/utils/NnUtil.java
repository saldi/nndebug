package pl.jclab.dl4jsample.utils;

import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class NnUtil{

	public static void hr(String name){

		System.out.println();
		System.out.println("############################################################################################");
		System.out.println("# " + name);
		System.out.println("############################################################################################");
		System.out.println();
	}

	public static void debugNN(MultiLayerNetwork net){

		Layer[] layers = net.getLayers();
		int totalNumParams = 0;
		for (int i = 0; i < layers.length; i++){
			int nParams = layers[i].numParams();
			System.out.println("Layer " + i + " parameters: " + nParams);
			totalNumParams += nParams;
		}
		System.out.println("Total parameters: " + totalNumParams);
	}

	public static MultiLayerConfiguration getMultiLayerConfiguration(int inputNeuron, int outputNeuron){

		return getMultiLayerConfiguration(inputNeuron, 0, outputNeuron);
	}

	public static MultiLayerConfiguration getMultiLayerConfiguration(int inputNeuron, int hiddenNeuron, int outputNeuron){

		return getMultiLayerConfiguration(inputNeuron, hiddenNeuron, outputNeuron, 123, 0.1f);
	}

	public static MultiLayerConfiguration getMultiLayerConfiguration(int inputNeuron, int hiddenNeuron, int outputNeuron, int seed, float learningRate
	){

		DenseLayer hidden = null;

		if (hiddenNeuron == 0){
			hiddenNeuron = outputNeuron;
		} else {
			hidden = new DenseLayer.Builder()
			.nIn(hiddenNeuron)
			.nOut(hiddenNeuron)
			.activation(Activation.RELU)
			.weightInit(WeightInit.DISTRIBUTION)
			.dist(new UniformDistribution(0, 1))
			.build();
		}

		final DenseLayer input = new DenseLayer.Builder()
		.nIn(inputNeuron)
		.nOut(hiddenNeuron)
		.activation(Activation.RELU)
		.weightInit(WeightInit.DISTRIBUTION)
		.dist(new UniformDistribution(0, 1))
		.build();

		final OutputLayer output = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
		.nIn(hiddenNeuron)
		.nOut(outputNeuron)
		.activation(Activation.SOFTMAX)
		.weightInit(WeightInit.DISTRIBUTION)
		.dist(new UniformDistribution(0, 1))
		.name("Output")
		.build();

		final NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
		.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
		.updater(new Sgd(learningRate))
		.seed(seed)
		.biasInit(0)
		.miniBatch(false)
		.weightInit(WeightInit.XAVIER);

		final NeuralNetConfiguration.ListBuilder listBuilder = builder.list();

		listBuilder.layer(input);
		if (hidden != null){
			listBuilder.layer(hidden);
		}
		listBuilder.layer(output);

		return listBuilder.pretrain(false)
						  .backprop(true)
						  .build();
	}


	public static MultiLayerConfiguration getClassificationMultiLayerConfiguration(int seed, int inputFeatures, int hiddenNeuron, int hiddenLayer,
																				   int subtractionNeuron, int outputLabels){

		final NeuralNetConfiguration.ListBuilder listBuilder = new NeuralNetConfiguration.Builder()
		.seed(seed)
		.updater(new Nesterovs(0.1, 0.9))
		.list();

		listBuilder.layer(new DenseLayer.Builder().nIn(inputFeatures).nOut(hiddenNeuron)
												  .weightInit(WeightInit.XAVIER)
												  .activation(Activation.RELU)
												  .build());

		if (hiddenLayer > 0){
			int inNeuron = hiddenNeuron;
			int outNeuron = hiddenNeuron - subtractionNeuron;

			for (int i = 0; i < hiddenLayer; i++){
				listBuilder.layer(new DenseLayer.Builder().nIn(inNeuron).nOut(outNeuron)
														  .weightInit(WeightInit.XAVIER)
														  .activation(Activation.RELU)
														  .build());

				if (subtractionNeuron > 0){
					inNeuron = outNeuron;
					hiddenNeuron = outNeuron;
					outNeuron = outNeuron - subtractionNeuron;
				}
			}

		}

		listBuilder.layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
						  .nIn(hiddenNeuron).nOut(outputLabels)
						  .weightInit(WeightInit.XAVIER)
						  .activation(Activation.SOFTMAX)
						  .build()
		);
		return listBuilder.pretrain(false).backprop(true).build();
	}


	public static MultiLayerConfiguration getRegressionMultiLayerConfiguration(int seed, int inputFeatures, int hiddenNeuron, int outputLabels){

		return new NeuralNetConfiguration.Builder()
		.seed(seed)
		.weightInit(WeightInit.XAVIER)
		.updater(new Nesterovs(0.1, 0.9))
		.list()
		.layer(new DenseLayer.Builder().nIn(inputFeatures)
									   .nOut(hiddenNeuron)
									   .activation(Activation.TANH)
									   .build())
		.layer(new DenseLayer.Builder().nIn(hiddenNeuron)
									   .nOut(hiddenNeuron)
									   .activation(Activation.TANH)
									   .build())
		.layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
			   .activation(Activation.IDENTITY)
			   .nIn(hiddenNeuron).nOut(outputLabels).build())
		.pretrain(false).backprop(true).build();
	}

	public static void xorDebug(INDArray inputs, MultiLayerNetwork net, int outputNeuron){

		for (int i = 0; i < 4; i++){
			final INDArray input = inputs.getRow(i);
			final INDArray prediction = net.output(input);

			String out = "";
			switch (outputNeuron){
				default:
					out = "" + prediction.getFloat(0);
					break;
				case 2:
					out = "" + prediction.getFloat(0) + " | " + prediction.getFloat(1);
					break;
			}

			System.out.println(input.getInt(0) + " XOR " + input.getInt(1) + " = " + out);
		}
	}
}
