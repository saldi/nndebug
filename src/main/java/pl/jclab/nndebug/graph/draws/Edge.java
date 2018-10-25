package pl.jclab.nndebug.graph.draws;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import pl.jclab.nndebug.models.NNEdge;

public class Edge extends Group{

	private int fontSize = 16;

	protected NNEdge nnEdge;
	protected Cell source;
	protected Cell target;

	protected Line line;
	protected Color lineColor;
	protected Text weight;

	public Edge(Cell source, Cell target, NNEdge nnEdge){

		this.source = source;
		this.target = target;
		this.nnEdge = nnEdge;

		source.addCellChild(target);
		target.addCellParent(source);

		line = new Line();


		line.startXProperty().bind(source.layoutXProperty().add(source.getBoundsInLocal().getWidth() / 2.0));
		line.startYProperty().bind(source.layoutYProperty().add(source.getBoundsInLocal().getHeight() / 2.0));

		line.endXProperty().bind(target.layoutXProperty().add(target.getBoundsInLocal().getWidth() / 2.0));
		line.endYProperty().bind(target.layoutYProperty().add(target.getBoundsInLocal().getHeight() / 2.0));

		lineColor = Color.color(0.8, 0.8, 0.8);

		line.setStroke(Color.LIGHTGRAY);

		weight = new Text();
		weight.setFill(Color.RED);
		weight.setFont(Font.font(fontSize));
		weight.setText("" + nnEdge.getWeight());
		weight.setTextAlignment(TextAlignment.CENTER);
		weight.setVisible(true);

		weight.layoutXProperty().bind(line.endXProperty().subtract(line.endXProperty().subtract(line.startXProperty()).divide(2)));
		weight.layoutYProperty().bind(line.endYProperty().subtract(line.endYProperty().subtract(line.startYProperty()).divide(2).add(5)));

		getChildren().add(line);
		getChildren().add(weight);

	}

	public Cell getSource(){

		return source;
	}

	public Cell getTarget(){

		return target;
	}

	public void update(){

		weight.setText("" + nnEdge.getWeight());

		if (nnEdge.getWeight() > 0.01){
			lineColor = Color.hsb(10 * nnEdge.getWeight(), 1.0, 1.0, 1.0);
		} else if (nnEdge.getWeight() < -0.01){
			lineColor = Color.hsb(128 + -10 * nnEdge.getWeight(), 1.0, 1.0, 1.0);
		} else {
			lineColor = Color.LIGHTGRAY;
		}

		line.setStroke(lineColor);
	}

	public void setShowWeight(boolean show){

		weight.setVisible(show);

	}
}
