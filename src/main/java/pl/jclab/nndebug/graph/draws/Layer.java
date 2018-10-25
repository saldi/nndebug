package pl.jclab.nndebug.graph.draws;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import pl.jclab.nndebug.models.NNLayer;

public class Layer extends Group{

	private int blockSizeX = 100;
	private int blockSizeY = 120;
	private int fontSize = 16;

	NNLayer layer;
	private int index;
	private Rectangle box;
	private Text layerName;

	public Layer(NNLayer nnLayer){

		this.layer = nnLayer;
		index = nnLayer.getIndex();

		box = new Rectangle();
		box.setWidth(blockSizeX);
		box.setHeight(blockSizeY * nnLayer.getNodes().size());
		box.setStroke(Color.DARKGREY);
		box.setFill(Color.TRANSPARENT);
		box.setVisible(false);

		layerName = new Text();
		layerName.setFill(Color.RED);
		layerName.setFont(Font.font(fontSize));
		layerName.setTextAlignment(TextAlignment.CENTER);
		layerName.setText(nnLayer.getName());
		layerName.relocate(0, -20);
		layerName.setVisible(false);

		getChildren().add(box);
		getChildren().add(layerName);
	}

	public NNLayer getLayer(){

		return layer;
	}

	public void setLayer(NNLayer layer){

		this.layer = layer;
	}

	public int getIndex(){

		return index;
	}

	public void setIndex(int index){

		this.index = index;
	}

	public void setWidth(float width){

		box.setWidth(width);
	}

	public void setHeight(float height){

		box.setHeight(height);
	}

	public void setShow(boolean show){

		box.setVisible(show);
		layerName.setVisible(show);
	}
}
