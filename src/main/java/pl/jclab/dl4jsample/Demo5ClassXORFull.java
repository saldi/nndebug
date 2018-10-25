package pl.jclab.dl4jsample;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import pl.jclab.dl4jsample.utils.DataUtil;
import pl.jclab.dl4jsample.utils.NnUtil;
import pl.jclab.nndebug.NetListenerBuilder;

public class Demo5ClassXORFull{

	public static void main(String[] args){

		int epoch = 1000;
		int seed = 123;
		int inputFeatures = 2;
		int hiddenNeuron = 5;
		int hiddenLayer = 0;
		int subtractionNeuron = 0;
		int outputLabels = 2;

		int samples = 500;
		double noise = 0;
		double testTrainFactor = 0.5;

		DataUtil.setSeed(500);

		final DataSet dataSet = DataUtil.classifyXor(samples, noise);

		SplitTestAndTrain testAndTrain = dataSet.splitTestAndTrain(testTrainFactor);

		DataSet trainDataSet = testAndTrain.getTrain();
		DataSet testDataSet = testAndTrain.getTest();

		MultiLayerConfiguration multiLayerConfiguration = NnUtil.getClassificationMultiLayerConfiguration(seed, inputFeatures, hiddenNeuron, hiddenLayer,
																										  subtractionNeuron, outputLabels);

		MultiLayerNetwork net = new MultiLayerNetwork(multiLayerConfiguration);
		net.init();
		net.setListeners(NetListenerBuilder.Builder()
										   .name("Demo5 Class XOR Full Network")
										   .data(multiLayerConfiguration, net, trainDataSet, testDataSet)
										   .inRange(-6, 6)
										   .build()
		);

		NnUtil.debugNN(net);

		System.out.println("Fit Network:");
		for (int i = 0; i < epoch; i++){
			net.fit(trainDataSet);
		}

		NnUtil.hr("FeatureMatrix:");
		INDArray output = net.output(testDataSet.getFeatureMatrix());
		System.out.println(output);

		NnUtil.hr("Evaluation:");
		Evaluation eval = new Evaluation(2);
		eval.eval(testDataSet.getLabels(), output);
		System.out.println(eval.stats());
	}

}
