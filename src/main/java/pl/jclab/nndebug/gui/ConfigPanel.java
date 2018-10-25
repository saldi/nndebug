package pl.jclab.nndebug.gui;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import pl.jclab.nndebug.config.AppCfg;
import pl.jclab.nndebug.graph.Model;
import pl.jclab.nndebug.graph.draws.Cell;
import pl.jclab.nndebug.graph.draws.Edge;
import pl.jclab.nndebug.graph.draws.Layer;
import pl.jclab.nndebug.graph.draws.NodeCell;

import java.util.List;

public class ConfigPanel extends GridPane{

	protected Model model;

	public ConfigPanel(Model model){

		this.model = model;

		initaializeStyle();
		initialize();
	}

	protected void initaializeStyle(){

		setHgap(5);
		setVgap(10);
	}

	protected void initialize(){

		CheckBox checkBoxShowNodeId = new CheckBox("Node Id");
		checkBoxShowNodeId.setSelected(AppCfg.showId);
		checkBoxShowNodeId.setOnAction(event -> {
			AppCfg.showId = checkBoxShowNodeId.isSelected();
			final List<Cell> cells = model.getAllCells();
			if (cells != null){
				for (Cell cell : cells){
					if (cell instanceof NodeCell){
						((NodeCell) cell).setShowId(AppCfg.showId);
					}
				}
			}
		});

		CheckBox checkBoxShowNodeActivation = new CheckBox("Node Activation");
		checkBoxShowNodeActivation.setSelected(AppCfg.showActivation);
		checkBoxShowNodeActivation.setOnAction(event -> {
			AppCfg.showActivation = checkBoxShowNodeActivation.isSelected();
			final List<Cell> cells = model.getAllCells();
			if (cells != null){
				for (Cell cell : cells){
					if (cell instanceof NodeCell){
						((NodeCell) cell).setShowActivation(AppCfg.showActivation);
					}
				}
			}
		});

		CheckBox checkBoxShowNodeBias = new CheckBox("Node Bias");
		checkBoxShowNodeBias.setSelected(AppCfg.showBias);
		checkBoxShowNodeBias.setOnAction(event -> {
			AppCfg.showBias = checkBoxShowNodeBias.isSelected();
			final List<Cell> cells = model.getAllCells();
			if (cells != null){
				for (Cell cell : cells){
					if (cell instanceof NodeCell){
						((NodeCell) cell).setShowBias(AppCfg.showBias);
					}
				}
			}
		});

		CheckBox checkBoxShowEdgeWeight = new CheckBox("Edge Weight");
		checkBoxShowEdgeWeight.setSelected(AppCfg.showWeight);
		checkBoxShowEdgeWeight.setOnAction(event -> {
			AppCfg.showWeight = checkBoxShowEdgeWeight.isSelected();
			List<Edge> edges = model.getAllEdges();
			if (edges != null){
				for (Edge edge : edges){
					edge.setShowWeight(AppCfg.showWeight);
				}
			}
		});

		CheckBox checkBoxShowLayer = new CheckBox("Layers");
		checkBoxShowLayer.setSelected(AppCfg.showLayers);
		checkBoxShowLayer.setOnAction(event -> {
			AppCfg.showLayers = checkBoxShowLayer.isSelected();
			List<Layer> layers = model.getAllLayer();
			if (layers != null){
				for (Layer layer : layers){
					layer.setShow(AppCfg.showLayers);
				}
			}
		});

		add(checkBoxShowNodeId, 0, 0);
		add(checkBoxShowNodeActivation, 0, 1);
		add(checkBoxShowNodeBias, 0, 2);
		add(checkBoxShowEdgeWeight, 0, 3);
		add(checkBoxShowLayer, 0, 4);
	}
}