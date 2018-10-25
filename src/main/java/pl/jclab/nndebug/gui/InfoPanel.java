package pl.jclab.nndebug.gui;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import pl.jclab.nndebug.models.NNGraph;

public class InfoPanel extends GridPane{

	protected Text epoch;
	protected Text iterate;
	protected Text score;

	protected Text learningRate;
	protected Text activation;
	protected Text regularization;
	protected Text regularizationRate;
	protected Text problemType;

	protected NNGraph netGraph;

	public InfoPanel(NNGraph netGraph){

		this.netGraph = netGraph;
		initaializeStyle();
		initialize();
	}

	public void update(){

		if (netGraph == null){
			return;
		}

		score.setText("Score: " + round(netGraph.getScore()));
		epoch.setText("Epoch: " + netGraph.getEpoch());
		iterate.setText("Iterate: " + netGraph.getIterate());

	}

	protected void initaializeStyle(){

		setHgap(5);
		setVgap(10);
		setAlignment(Pos.CENTER_LEFT);
	}

	private void initialize(){

		epoch = new Text("Epoch: 0");
		iterate = new Text("Iterate: 0");
		score = new Text("Score: 0.0");

		learningRate = new Text("Learning rate");
		activation = new Text("Activation");
		regularization = new Text("Regularization");
		regularizationRate = new Text("Regularization rate");
		problemType = new Text("Problem type");

		add(epoch, 1, 0);
		add(iterate, 2, 0);
		add(score, 3, 0);
	}

	protected double round(double value){

		return Math.round(value * 1000) / 1000d;
	}
}
