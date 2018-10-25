package pl.jclab.nndebug.graph.draws;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import pl.jclab.nndebug.models.NNNode;

public class NodeCell extends Cell{

	protected int radius = 30;
	protected int fontSize = 12;
	protected int biasFontSize = 20;

	protected NNNode node;
	protected Text bias;
	protected Text nodeId;
	protected Text activationFunction;
	protected Circle circle;
	protected Color strokeColor;

	public NodeCell(NNNode node){

		super(node.getId());

		this.node = node;

		Group group = new Group();

		strokeColor = Color.BLACK;

		circle = new Circle(radius);
		circle.setStroke(strokeColor);
		circle.setFill(Color.LIGHTGOLDENRODYELLOW);
		circle.setCenterX(radius);
		circle.setCenterY(radius);
		circle.relocate(20, 0);

		nodeId = new Text();
		nodeId.setFill(Color.RED);
		nodeId.setFont(Font.font(fontSize));
		nodeId.setText(node.getId());
		nodeId.setTextAlignment(TextAlignment.CENTER);
		nodeId.setVisible(false);

		bias = new Text();
		bias.setFill(Color.GREEN);
		bias.setFont(Font.font(biasFontSize));
		bias.setText(node.getBias());
		bias.setTextAlignment(TextAlignment.CENTER);
		bias.setVisible(true);

		activationFunction = new Text();
		activationFunction.setFill(Color.BLUE);
		activationFunction.setFont(Font.font(fontSize));
		activationFunction.setText(node.getActivationFunction());
		activationFunction.setTextAlignment(TextAlignment.CENTER);
		activationFunction.setVisible(true);

		group.getChildren().add(circle);
		group.getChildren().add(nodeId);
		group.getChildren().add(bias);
		group.getChildren().add(activationFunction);

		setWidth(radius);
		setHeight(radius);

		setView(group);

		centerText(nodeId, 34, -24);
		centerText(bias, 34, 15);
		centerText(activationFunction, 34, 54);
	}

	private void centerText(Text text, int width, int height){

		final double W = text.getBoundsInLocal().getWidth();
		final double H = text.getBoundsInLocal().getHeight();

		final double layWidth = getLayoutBounds().getWidth();
		final double layHeight = getLayoutBounds().getHeight();

		final double relX = (layWidth - W) / 2;
		final double relY = (layHeight - H) / 2;

		text.relocate(relX + width, relY + height);
	}


	private void centerText2(Text text, int width, int height){

		final double W = text.getBoundsInLocal().getWidth();
		final double H = text.getBoundsInLocal().getHeight();

		final double layWidth = getLayoutBounds().getWidth();
		final double layHeight = getLayoutBounds().getHeight();

		final double relX = (layWidth - W) / 2;
		final double relY = (layHeight - H) / 2;

		text.relocate(relX + width, height);
	}

	@Override
	public void update(){

		bias.setText(node.getBias());
		centerText2(bias, 10, 15);

		if (node.getBias() != null && !node.getBias().isEmpty()){

			float bias = 0.0f;

			try{
				bias = Float.parseFloat(node.getBias());
			} catch (Exception ex){

			}

			if (bias > 0.01){
				strokeColor = Color.hsb(10 * bias, 1.0, 1.0, 1.0);
			} else if (bias < -0.01){
				strokeColor = Color.hsb(128 + -10 * bias, 1.0, 1.0, 1.0);
			} else {
				strokeColor = Color.BLACK;
			}
			circle.setStroke(strokeColor);
		}

	}

	public void setShowBias(boolean show){

		bias.setVisible(show);
	}

	public void setShowId(boolean show){

		nodeId.setVisible(show);
	}

	public void setShowActivation(boolean show){

		activationFunction.setVisible(show);
	}
}
