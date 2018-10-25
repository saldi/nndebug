package pl.jclab.dl4jsample;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.jclab.nndebug.NetListenerBuilder;

/**
 * @author Adam Gibson
 */
public class Demo3Iris{

	private static Logger log = LoggerFactory.getLogger(Demo3Iris.class);

	public static void main(String[] args) throws Exception{

		int numLinesToSkip = 0;
		char delimiter = ',';
		RecordReader recordReader = new CSVRecordReader(numLinesToSkip, delimiter);
		recordReader.initialize(new FileSplit(new ClassPathResource("iris.txt").getFile()));

		int labelIndex = 4;
		int numClasses = 3;
		int batchSize = 150;

		int hiddenNeuron = 5;
		int numInputs = 4;
		int outputNum = 3;
		long seed = 123;
		long epoch = 1000;
		double learningRate = 0.1;
		double fractionTrain = 0.5;

		DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, numClasses);
		DataSet allData = iterator.next();
		allData.shuffle();
		SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(fractionTrain);

		DataSet trainingData = testAndTrain.getTrain();
		DataSet testData = testAndTrain.getTest();

		//We need to normalize our data. We'll use NormalizeStandardize (which gives us mean 0, unit variance):
		DataNormalization normalizer = new NormalizerStandardize();
		normalizer.fit(trainingData);           //Collect the statistics (mean/stdev) from the training data. This does not modify the input data
		normalizer.transform(trainingData);     //Apply normalization to the training data
		normalizer.transform(testData);         //Apply normalization to the test data. This is using statistics calculated from the *training* set


		log.info("Build model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
		.seed(seed)
		.activation(Activation.RELU)
		.weightInit(WeightInit.RELU)
		.updater(new Nesterovs(learningRate, 0.9))
		.l2(1e-4)
		.list()
		.layer(new DenseLayer.Builder().nIn(numInputs)
									   .nOut(hiddenNeuron)
									   .build()
		)
		.layer(new DenseLayer.Builder().nIn(hiddenNeuron)
									   .nOut(hiddenNeuron)
									   .build()
		)
		.layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
			   .activation(Activation.SOFTMAX)
			   .nIn(hiddenNeuron)
			   .nOut(outputNum)
			   .build()
		)
		.backprop(true).pretrain(false)
		.build();

		//run the model
		MultiLayerNetwork model = new MultiLayerNetwork(conf);
		model.init();
		//		model.setListeners(new ScoreIterationListener(100));

		model.setListeners(NetListenerBuilder.Builder()
										   .name("Demo2 XOR2")
										   .data(conf, model, trainingData, testData)
										   .inRange(-3,3)
										   .build()
		);
		for (int i = 0; i < epoch; i++){
			model.fit(trainingData);
		}

		//evaluate the model on the test set
		Evaluation eval = new Evaluation(3);
		INDArray output = model.output(testData.getFeatures());
		eval.eval(testData.getLabels(), output);
		log.info(eval.stats());
	}

}
