package pl.jclab.nndebug;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.DataSet;

/**
 * @author Jaroslaw Czub - https://czub.info
 */
public class NetListenerBuilder{

	protected String appName = "Debug";

	protected int printIterations = 1;

	protected double minIn = -0.1;
	protected double maxIn = 1.1;

	protected double minOut = -0.1;
	protected double maxOut = 1.1;

	protected MultiLayerConfiguration multiLayerConfiguration;
	protected MultiLayerNetwork net;
	protected DataSet trainData;
	protected DataSet testData;

	public static NetListenerBuilder Builder(){

		return new NetListenerBuilder();
	}

	public static NetListenerBuilder Builder(MultiLayerConfiguration multiLayerConfiguration, MultiLayerNetwork net, DataSet trainData, DataSet testData){

		return new NetListenerBuilder()
		.data(multiLayerConfiguration, net, trainData, testData);
	}

	private NetListenerBuilder(){

	}


	public NetListenerBuilder data(MultiLayerConfiguration multiLayerConfiguration, MultiLayerNetwork net, DataSet trainData, DataSet testData){

		this.multiLayerConfiguration = multiLayerConfiguration;
		this.net = net;
		this.trainData = trainData;
		this.testData = testData;

		return this;
	}

	public NetListenerBuilder name(String appName){

		this.appName = appName;
		return this;
	}

	public NetListenerBuilder print(int iterations){

		this.printIterations = iterations;
		return this;
	}

	public NetListenerBuilder range(double min, double max){

		this.minIn = min;
		this.maxIn = max;
		this.minOut = min;
		this.maxOut = max;
		return this;
	}

	public NetListenerBuilder inRange(double min, double max){

		this.minIn = min;
		this.maxIn = max;
		return this;
	}

	public NetListenerBuilder outRange(double min, double max){

		this.minOut = min;
		this.maxOut = max;
		return this;
	}

	public NetListener build(){

		if (multiLayerConfiguration == null){
			throw new IllegalArgumentException("MultiLayerConfiguration can't be null");
		}
		if (net == null){
			throw new IllegalArgumentException("MultiLayerNetwork can't be null");
		}
		if (trainData == null){
			throw new IllegalArgumentException("Train Data can't be null");
		}
		if (testData == null){
			throw new IllegalArgumentException("Test Data can't be null");
		}

		return new NetListener(appName, multiLayerConfiguration, net, trainData, testData, printIterations, minIn, maxIn, minOut, maxOut);
	}
}
