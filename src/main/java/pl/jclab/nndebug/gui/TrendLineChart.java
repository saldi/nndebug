package pl.jclab.nndebug.gui;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.eval.EvaluationAveraging;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import pl.jclab.nndebug.models.NNGraph;

import static pl.jclab.nndebug.config.StyleCfg.iterationVsScoreLineChartHeight;
import static pl.jclab.nndebug.config.StyleCfg.iterationVsScoreLineChartWidth;

public class TrendLineChart extends GridPane{

	protected String label;
	protected NNGraph nnGraph;
	protected MultiLayerNetwork network;
	protected Evaluation evalTrain;
	protected Evaluation evalTest;

	protected LineChart<Number, Number> iterationVsScoreLineChart;
	protected XYChart.Series scoreSeries;
	protected XYChart.Series trainF1Series;
	protected XYChart.Series testF1Series;

	public TrendLineChart(String label, NNGraph nnGraph, MultiLayerNetwork network){

		this.label = label;
		this.nnGraph = nnGraph;
		this.network = network;
		initaializeStyle();
		initalize();
		update();
	}

	public void update(Evaluation evalTrain, Evaluation evalTest){

		this.evalTrain = evalTrain;
		this.evalTest = evalTest;
		update();
	}

	public void update(){

		if (evalTrain == null || evalTest == null){
			return;
		}
		final XYChart.Data plotData = new XYChart.Data(nnGraph.getIterate(), nnGraph.getScore());
		scoreSeries.getData().add(plotData);
		((StackPane) plotData.getNode()).setVisible(false);

		final XYChart.Data plotData2 = new XYChart.Data(nnGraph.getIterate(), evalTest.f1(EvaluationAveraging.Macro));
		testF1Series.getData().add(plotData2);
		((StackPane) plotData2.getNode()).setVisible(false);

		final XYChart.Data plotData3 = new XYChart.Data(nnGraph.getIterate(), evalTrain.f1(EvaluationAveraging.Macro));
		trainF1Series.getData().add(plotData3);
		((StackPane) plotData3.getNode()).setVisible(false);
	}

	protected void initaializeStyle(){

		setHgap(5);
		setVgap(10);
	}

	protected void initalize(){

		final NumberAxis xAxisLine = new NumberAxis();
		xAxisLine.setTickUnit(10);

		final NumberAxis yAxisLine = new NumberAxis(0, 1.0, 0.1);

		iterationVsScoreLineChart = new LineChart(xAxisLine, yAxisLine);
		iterationVsScoreLineChart.setMinWidth(iterationVsScoreLineChartWidth);
		iterationVsScoreLineChart.setMinHeight(iterationVsScoreLineChartHeight);
		iterationVsScoreLineChart.setMaxWidth(iterationVsScoreLineChartWidth);
		iterationVsScoreLineChart.setMaxHeight(iterationVsScoreLineChartHeight);
		iterationVsScoreLineChart.prefWidth(iterationVsScoreLineChartWidth);
		iterationVsScoreLineChart.prefHeight(iterationVsScoreLineChartHeight);
		iterationVsScoreLineChart.setAnimated(true);

		scoreSeries = new XYChart.Series();
		scoreSeries.setName("Score");

		trainF1Series = new XYChart.Series();
		trainF1Series.setName("Train F1");

		testF1Series = new XYChart.Series();
		testF1Series.setName("Test F1");

		iterationVsScoreLineChart.getData().add(scoreSeries);
		iterationVsScoreLineChart.getData().add(trainF1Series);
		iterationVsScoreLineChart.getData().add(testF1Series);

		final Text textLabel = new Text(label);
		textLabel.getStyleClass().add("title-label");

		add(textLabel, 0, 0);
		add(iterationVsScoreLineChart, 0, 1);
	}

}
