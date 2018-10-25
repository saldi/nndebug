package pl.jclab.nndebug.gui;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.eval.EvaluationAveraging;
import pl.jclab.nndebug.models.NNGraph;

public class EvalInfo extends GridPane{

	protected String label;
	protected NNGraph nnGraph;
	protected Evaluation eval;

	protected Text titleLabel;
	protected Text accuracyLabel;
	protected Text precisionLabel;
	protected Text recallLabel;
	protected Text f1ScoreLabel;
	protected Text falseNegativeRateLabel;
	protected Text falsePositiveRateLabel;
	protected Text falseAlarmRateLabel;

	protected Text accuracy;
	protected Text precision;
	protected Text recall;
	protected Text f1Score;
	protected Text falseNegativeRate;
	protected Text falsePositiveRate;
	protected Text falseAlarmRate;

	public EvalInfo(String label){

		this.label = label;
		initaializeStyle();
		initaialize();
		update();
	}


	public EvalInfo(String label, NNGraph nnGraph, Evaluation eval){

		this.label = label;
		this.nnGraph = nnGraph;
		this.eval = eval;
		initaializeStyle();
		initaialize();
		update();
	}

	public void update(NNGraph nnGraph, Evaluation eval){

		this.nnGraph = nnGraph;
		this.eval = eval;
		update();
	}

	public void update(){

		if (eval == null){
			return;
		}
		this.accuracy.setText("" + round(eval.accuracy()));
		this.precision.setText("" + round(eval.precision(EvaluationAveraging.Macro)));
		this.recall.setText("" + round(eval.recall(EvaluationAveraging.Macro)));
		this.f1Score.setText("" + round(eval.f1(EvaluationAveraging.Macro)));
		this.falsePositiveRate.setText("" + round(eval.falseNegativeRate()));
		this.falseNegativeRate.setText("" + round(eval.falsePositiveRate()));
		this.falseAlarmRate.setText("" + round(eval.falseAlarmRate()));
	}

	protected void initaializeStyle(){

		setHgap(5);
		setVgap(10);
	}

	protected void initaialize(){

		titleLabel = new Text("--- " + label + " -------");

		accuracyLabel = new Text("Accuracy:");
		precisionLabel = new Text("Precision:");
		recallLabel = new Text("Recall:");
		f1ScoreLabel = new Text("F1 Score:");
		falseNegativeRateLabel = new Text("False negative:");
		falsePositiveRateLabel = new Text("False positive:");
		falseAlarmRateLabel = new Text("False alarm:");

		accuracy = new Text("0.0");
		precision = new Text("0.0");
		recall = new Text("0.0");
		f1Score = new Text("0.0");
		falseNegativeRate = new Text("0.0");
		falsePositiveRate = new Text("0.0");
		falseAlarmRate = new Text("0.0");

		add(titleLabel, 0, 0);
		add(accuracyLabel, 0, 1);
		add(precisionLabel, 0, 2);
		add(recallLabel, 0, 3);
		add(f1ScoreLabel, 0, 4);
		add(falseNegativeRateLabel, 0, 5);
		add(falsePositiveRateLabel, 0, 6);
		add(falseAlarmRateLabel, 0, 7);

		add(new Text("------"), 1, 0);
		add(accuracy, 1, 1);
		add(precision, 1, 2);
		add(recall, 1, 3);
		add(f1Score, 1, 4);
		add(falseNegativeRate, 1, 5);
		add(falsePositiveRate, 1, 6);
		add(falseAlarmRate, 1, 7);
	}

	protected double round(double value){

		return Math.round(value * 1000) / 1000d;
	}
}
