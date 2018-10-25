package pl.jclab.nndebug.gui;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import pl.jclab.nndebug.models.NNGraph;
import pl.jclab.nndebug.config.StyleCfg;

import static pl.jclab.nndebug.NetDebugApp.*;

public class NetworkSpace extends GridPane{

	protected String label;
	protected NNGraph nnGraph;
	protected MultiLayerNetwork network;

	protected INDArray allXYPoints;
	protected INDArray predictionsAtXYPoints;

	protected WritableImage inputArea;
	protected ImageView inputImage;

	protected WritableImage outputArea;
	protected ImageView outputImage;

	protected Color positiveColor = Color.YELLOW;
	protected Color middleColor = Color.RED;
	protected Color negativeColor = Color.GREEN;

	public NetworkSpace(String label, NNGraph nnGraph, MultiLayerNetwork network){

		this.label = label;
		this.nnGraph = nnGraph;
		this.network = network;
		initaializeStyle();
		initalize();
		update();
	}

	public void update(){

		predictionsAtXYPoints = network.output(allXYPoints);
		plotImage(outputArea, predictionsAtXYPoints, minOut, maxOut);
	}

	protected void initaializeStyle(){

		setHgap(5);
		setVgap(5);
	}

	protected void initalize(){

		allXYPoints = evalPoints();
		inputArea = new WritableImage(StyleCfg.areaWidth, StyleCfg.areaHeight);
		outputArea = new WritableImage(StyleCfg.areaWidth, StyleCfg.areaHeight);

		inputImage = new ImageView();
		inputImage.setImage(inputArea);
		inputImage.setFitWidth(StyleCfg.imageWidth);
		inputImage.setFitHeight(StyleCfg.imageHeight);

		outputImage = new ImageView();
		outputImage.setImage(outputArea);
		outputImage.setFitWidth(StyleCfg.imageWidth);
		outputImage.setFitHeight(StyleCfg.imageHeight);

		plotImage(inputArea, allXYPoints, minIn, maxIn);
		predictionsAtXYPoints = network.output(allXYPoints);
		plotImage(outputArea, predictionsAtXYPoints, minOut, maxOut);

		final Text inputSpace = new Text("Input space");
		inputSpace.getStyleClass().add("title-label");

		final Text outputSpace = new Text("Output space");
		outputSpace.getStyleClass().add("title-label");

		add(inputSpace, 0, 0);
		add(inputImage, 0, 1);

		add(outputSpace, 1, 0);
		add(outputImage, 1, 1);
	}


	protected INDArray evalPoints(){

		final int features = nnGraph.getInputNeuron();

		//Let's evaluate the predictions at every point in the x/y input space
		double[][] evalPoints = new double[StyleCfg.areaWidth * StyleCfg.areaHeight][features];
		int count = 0;

		//		for (int i = 0; i < areaWidth; i++){
		for (int i = StyleCfg.areaWidth - 1; i >= 0; i--){
			for (int j = 0; j < StyleCfg.areaHeight; j++){
				switch (features){
					case 1:
						double value = i * (maxIn - minIn) / (StyleCfg.areaWidth - 1) + minIn;
						evalPoints[count][0] = value;
						break;
					case 2:
						double x = i * (maxIn - minIn) / (StyleCfg.areaWidth - 1) + minIn;
						double y = j * (maxIn - minIn) / (StyleCfg.areaHeight - 1) + minIn;
						evalPoints[count][0] = x;
						evalPoints[count][1] = y;
						break;
					case 3:
						double x2 = i * (maxIn - minIn) / (StyleCfg.areaWidth - 1) + minIn;
						double y2 = j * (maxIn - minIn) / (StyleCfg.areaHeight - 1) + minIn;
						double z2 = ((i + j) / 2.0) * (maxIn - minIn) / (StyleCfg.areaWidth - 1) + minIn;
						evalPoints[count][0] = x2;
						evalPoints[count][1] = y2;
						evalPoints[count][2] = z2;
						break;
					case 4:
						double x3 = i * (maxIn - minIn) / (StyleCfg.areaWidth - 1) + minIn;
						double y3 = j * (maxIn - minIn) / (StyleCfg.areaHeight - 1) + minIn;
						double z3 = ((i + j) / 2.0) * (maxIn - minIn) / (StyleCfg.areaWidth - 1) + minIn;
						double w3 = ((i + j + j) / 3.0) * (maxIn - minIn) / (StyleCfg.areaWidth - 1) + minIn;
						evalPoints[count][0] = x3;
						evalPoints[count][1] = y3;
						evalPoints[count][2] = z3;
						evalPoints[count][3] = w3;
						break;
					default:
				}
				count++;
			}
		}

		return Nd4j.create(evalPoints);
	}


	protected void plotImage(WritableImage image, INDArray points, double min, double max){

		int w = (int) image.getWidth();
		int h = (int) image.getHeight();

		final int rows = points.rows();
		final int columns = points.columns();

		final double stepColor = 1.0 / w;
		final PixelWriter writer = image.getPixelWriter();

		float step = w / columns;

		for (int x = 0; x < w; x++){
			for (int y = 0; y < h; y++){

				int index = x + w * y;

				switch (columns){
					case 1:
						writer.setColor(x, y, color(points.getDouble(index, 0), min, max));
						//						writer.setColor(x, y, colorize(points.getDouble(index, 0)));
						break;
					case 2:
						writer.setColor(x, y, color(points.getDouble(index, 0), points.getDouble(index, 1), min, max));
						//						writer.setColor(x, y, colorize(points.getDouble(index, 0) + points.getDouble(index, 1) * 2));
						break;
					case 3:
					case 4:
						writer.setColor(x, y, color(points.getDouble(index, 0), points.getDouble(index, 1), points.getDouble(index, 2), min, max));
						break;
				}
			}
		}

	}


	private Color color(double value, double min, double max){

		double g = (value - min) / (max - min);
		if (g > 0.5){
			return middleColor.interpolate(positiveColor, (g - 0.5) * 2);
		}
		return negativeColor.interpolate(middleColor, g * 2);
	}

	private Color color(double x, double y, double min, double max){

		double r = clamp((x - min) / (max - min));
		double g = clamp((y - min) / (max - min));
		return Color.color(r, g, 0.0);
	}

	private Color color(double x, double y, double z, double min, double max){

		double r = clamp((x - min) / (max - min));
		double g = clamp((y - min) / (max - min));
		double b = clamp((z - min) / (max - min));
		return Color.color(r, g, b);
	}

	private double clamp(double x){

		double tmp = (x < 0.0) ? 0.0 : x;
		return (tmp > 1.0) ? 1.0 : tmp;
	}

	private Color colorize(double value){

		if (value > 0.5){
			return Color.hsb(10 * value, 1.0, 1.0, 1.0);
		} else if (value < -0.5){
			return Color.hsb(180 + -10 * value, 1.0, 1.0, 1.0);
		} else {
			value = clamp((value + 0.5));
			return Color.color(value, value, value);
		}

	}
}
