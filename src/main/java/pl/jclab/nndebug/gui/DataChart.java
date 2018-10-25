package pl.jclab.nndebug.gui;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.indexaccum.IMax;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import pl.jclab.nndebug.models.NNGraph;
import pl.jclab.nndebug.config.StyleCfg;

import static pl.jclab.nndebug.NetDebugApp.*;

public class DataChart extends GridPane{

	protected String label;
	protected NNGraph nnGraph;
	protected MultiLayerNetwork network;
	protected DataSet testData;

	protected ScatterChart<Number, Number> classScatterChart;
	protected ScatterChart<Number, Number> predictedScatterChart;


	public DataChart(String label, NNGraph nnGraph, MultiLayerNetwork network, DataSet testData){

		this.label = label;
		this.nnGraph = nnGraph;
		this.network = network;
		this.testData = testData;

		initaializeStyle();
		initaialize();
		update();
	}

	public void update(DataSet testData){

		this.testData = testData;
		update();
	}

	public void update(){

		if (testData == null){
			return;
		}

		final INDArray features = testData.getFeatureMatrix();
		final INDArray labels = testData.getLabels();
		final INDArray predicted = network.output(features);

		//
		// Plot data
		//
		predictedScatterChart.getData().clear();
		createTrainSeries(predictedScatterChart, labels, labels, "labels: ");
		createTrainSeries(predictedScatterChart, predicted, labels, "predicted: ");
	}

	protected void initaializeStyle(){

		setHgap(5);
		setVgap(10);
	}

	protected void initaialize(){

		//
		// Class Scatter Chart
		//
		final NumberAxis xAxisOutput = new NumberAxis(minIn, maxIn, (maxIn - minIn) / 10f);
		final NumberAxis yAxisOutput = new NumberAxis(minIn, maxIn, (maxIn - minIn) / 10f);

		classScatterChart = new ScatterChart<Number, Number>(xAxisOutput, yAxisOutput);
		classScatterChart.setMinWidth(StyleCfg.classScatterChartWidth);
		classScatterChart.setMinHeight(StyleCfg.classScatterChartHeight);
		classScatterChart.setMaxWidth(StyleCfg.classScatterChartWidth);
		classScatterChart.setMaxHeight(StyleCfg.classScatterChartHeight);
		classScatterChart.setPrefWidth(StyleCfg.classScatterChartWidth);
		classScatterChart.setPrefHeight(StyleCfg.classScatterChartHeight);

		classScatterChart.setAnimated(false);

		createTrainSeries(classScatterChart, testData.getFeatures(), testData.getLabels(), "label: ");

		final NumberAxis xAxis = new NumberAxis(minOut, maxOut, (maxOut - minOut) / 10f);
		final NumberAxis yAxis = new NumberAxis(minOut, maxOut, (maxOut - minOut) / 10f);

		predictedScatterChart = new ScatterChart<Number, Number>(xAxis, yAxis);
		predictedScatterChart.setMinWidth(StyleCfg.predictedScatterChartWidth);
		predictedScatterChart.setMinHeight(StyleCfg.predictedScatterChartHeight);
		predictedScatterChart.setMaxWidth(StyleCfg.predictedScatterChartWidth);
		predictedScatterChart.setMaxHeight(StyleCfg.predictedScatterChartHeight);
		predictedScatterChart.setPrefWidth(StyleCfg.predictedScatterChartHeight);
		predictedScatterChart.setPrefHeight(StyleCfg.predictedScatterChartHeight);
		predictedScatterChart.setAnimated(false);

		add(classScatterChart, 0, 0);
		add(predictedScatterChart, 0, 1);
	}


	private void createTrainSeries(ScatterChart<Number, Number> scatterChart, INDArray features,
								   INDArray labels, String prefix){

		int rows = features.rows();
		int columns = features.columns();

		int nClasses = labels.columns();

		XYChart.Series[] series = new XYChart.Series[nClasses];
		for (int i = 0; i < series.length; i++){

			XYChart.Series serie = new XYChart.Series<>();
			if (prefix == null){
				serie.setName("class" + i);
			} else {
				serie.setName(prefix + i);
			}

			series[i] = serie;
		}

		INDArray argMax = Nd4j.getExecutioner().exec(new IMax(labels), 1);

		double step = ((minIn - maxIn) / (rows - 1)) * -1;
		for (int i = 0; i < rows; i++){
			int classIdx = (int) argMax.getDouble(i);
			final XYChart.Data plotData;
			switch (columns){
				default:
				case 1:
					plotData = new XYChart.Data(0, features.getDouble(i, 0));
					break;
				case 2:
				case 3:
				case 4:
				case 5:
					plotData = new XYChart.Data(features.getDouble(i, 0), features.getDouble(i, 1));
					break;
			}
			series[classIdx].getData().add(plotData);
		}

		scatterChart.getData().addAll(series);
	}

	private void plotDataChart(String name, ScatterChart<Number, Number> scatterChart, INDArray data){

		XYChart.Series series = new XYChart.Series<>();
		series.setName(name);

		final int rows = data.rows();
		final int columns = data.columns();
		double step = (maxIn - minIn) / rows;

		for (int i = 0; i < rows; i++){
			final INDArray row = data.getRow(i);
			final XYChart.Data plotData;
			switch (columns){
				case 1:
					plotData = new XYChart.Data(i * step, row.getDouble(0));
					series.getData().add(plotData);
					break;
				case 2:
				case 3:
				case 4:
				case 5:
					plotData = new XYChart.Data(row.getDouble(0), row.getDouble(1));
					series.getData().add(plotData);
					break;
			}
		}
		scatterChart.getData().add(series);
	}
}
