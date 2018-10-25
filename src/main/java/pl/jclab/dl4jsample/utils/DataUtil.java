package pl.jclab.dl4jsample.utils;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Random;

public class DataUtil{

	private static long seedRand = 120;
	private static Random random = new Random();


	public static void setSeed(long seed){

		seedRand = seed;
		random.setSeed(seedRand);
	}


	public static DataSet classifyXor(int numSamples, double noise){

		double[][] features = new double[numSamples][2];
		double[][] labels = new double[numSamples][2];

		for (int i = 0; i < numSamples; i++){

			double x = randUniform(-5, 5);
			double padding = 0.3;
			x += x > 0 ? padding : -padding;
			double y = randUniform(-5, 5);
			y += y > 0 ? padding : -padding;
			double noiseX = randUniform(-5, 5) * noise;
			double noiseY = randUniform(-5, 5) * noise;
			double label = getXORLabel(x + noiseX, y + noiseY);

			features[i][0] = x;
			features[i][1] = y;
			setLabel(labels[i], label);
		}

		final INDArray indFeatures = Nd4j.create(features);
		final INDArray indLabels = Nd4j.create(labels);

		final DataSet dataSet = new DataSet(indFeatures, indLabels);
		dataSet.shuffle();
		return dataSet;
	}

	public static DataSet classifyXor(int numSamples, double noise, int numInput, int numOutput){

		double[][] features = new double[numSamples][numInput];
		double[][] labels = new double[numSamples][numOutput];

		double randScale = 1f - 1f / numOutput;

		for (int i = 0; i < numSamples; i++){

			for (int j = 0; j < numInput; j++){
				double x = randUniform(-5, 5);
				double padding = 0.3;
				x += x > 0 ? padding : -padding;
				double noiseX = randUniform(-5, 5) * noise;
				features[i][j] = x;
			}

			for (int j = 0; j < numOutput; j++){
				labels[i][j] = 0;
			}

			int index = (int) (random.nextDouble() * numOutput);
			labels[i][index] = 1;
		}

		final INDArray indFeatures = Nd4j.create(features);
		final INDArray indLabels = Nd4j.create(labels);

		final DataSet dataSet = new DataSet(indFeatures, indLabels);
		dataSet.shuffle();
		return dataSet;
	}

	public static DataSet classifyCircle(int numSamples, double noise){

		double[][] features = new double[numSamples][2];
		double[][] labels = new double[numSamples][2];

		double radius = 5;

		for (int i = 0; i < numSamples / 2; i++){

			double r = randUniform(0, radius * 0.5);
			double angle = randUniform(0, 2 * Math.PI);
			double x = r * Math.sin(angle);
			double y = r * Math.cos(angle);
			double noiseX = randUniform(-radius, radius) * noise;
			double noiseY = randUniform(-radius, radius) * noise;
			double label = getCircleLabel(x + noiseX, y + noiseY, 0, 0, radius);

			features[i][0] = x;
			features[i][1] = y;
			setLabel(labels[i], label);

		}

		for (int i = numSamples / 2; i < numSamples; i++){

			double r = randUniform(radius * 0.7, radius);
			double angle = randUniform(0, 2 * Math.PI);
			double x = r * Math.sin(angle);
			double y = r * Math.cos(angle);
			double noiseX = randUniform(-radius, radius) * noise;
			double noiseY = randUniform(-radius, radius) * noise;
			double label = getCircleLabel(x + noiseX, y + noiseY, 0, 0, radius);

			features[i][0] = x;
			features[i][1] = y;
			setLabel(labels[i], label);

		}

		final INDArray indFeatures = Nd4j.create(features);
		final INDArray indLabels = Nd4j.create(labels);

		final DataSet dataSet = new DataSet(indFeatures, indLabels);
		dataSet.shuffle();
		return dataSet;
	}

	public static DataSet classifySpiral(int numSamples, double noise){

		double[][] features = new double[numSamples][2];
		double[][] labels = new double[numSamples][2];

		genSpiral(0, 1, features, labels, 0, numSamples / 2, noise);
		genSpiral(Math.PI, 0, features, labels, numSamples / 2, numSamples / 2, noise);

		final INDArray indFeatures = Nd4j.create(features);
		final INDArray indLabels = Nd4j.create(labels);

		final DataSet dataSet = new DataSet(indFeatures, indLabels);
		dataSet.shuffle();
		return dataSet;
	}


	protected static void genSpiral(double deltaT, double label, double[][] features, double[][] labels, int start, double samples, double noise){

		for (int i = 0; i < samples; i++){
			double r = i / samples * 5;
			double t = 1.75 * i / samples * 2 * Math.PI + deltaT;
			double x = r * Math.sin(t) + randUniform(-1, 1) * noise;
			double y = r * Math.cos(t) + randUniform(-1, 1) * noise;

			features[start + i][0] = x;
			features[start + i][1] = y;
			setLabel(labels[start + i], label);
		}
	}

	private static void setLabel(double[] labels, double label){

		if (label > 0.5){
			labels[0] = 0;
			labels[1] = 1;
		} else {
			labels[0] = 1;
			labels[1] = 0;
		}

	}


	protected static double getXORLabel(double x, double y){

		return x * y >= 0 ? 1 : 0;
	}

	protected static double getCircleLabel(double x, double y, double centerX, double centerY, double radius){

		return (dist(x, y, centerX, centerY) < (radius * 0.5)) ? 1 : 0;
	}

	protected static double randUniform(double a, double b){

		return random.nextDouble() * (b - a) + a;
	}

	protected static double dist(double x, double y, double centerX, double centerY){

		double dx = x - centerX;
		double dy = y - centerY;
		return Math.sqrt(dx * dx + dy * dy);
	}
}
