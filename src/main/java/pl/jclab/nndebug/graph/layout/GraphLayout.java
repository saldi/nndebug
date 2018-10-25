package pl.jclab.nndebug.graph.layout;


import pl.jclab.nndebug.graph.Graph;
import pl.jclab.nndebug.graph.draws.Cell;
import pl.jclab.nndebug.graph.draws.Layer;

import java.util.List;
import java.util.Random;


public class GraphLayout extends Layout{

	private int startX = 100;
	private int startY = 100;

	private int stepX = 200;
	private int stepY = 120;

	private int addY = 0;

	Graph graph;

	Random rnd = new Random();

	public GraphLayout(Graph graph){

		this.graph = graph;

	}

	public void execute(){

		List<Cell> cells = graph.getModel().getAllCells();
		List<Layer> layers = graph.getModel().getAllLayer();

		//
		// Design Node
		//
		int lastLayer = -1;
		int nodeCount = 0;
		int addStepY = 0;
		int localStepY = stepY;

		boolean layerRelocate = false;

		for (Cell cell : cells){

			if (cell.getLayer() != lastLayer){
				lastLayer = cell.getLayer();
				nodeCount = 0;
				addStepY = 0;

				if (lastLayer > 0){
					addStepY = addStepY + addY;
				}
				if (layers.size() == lastLayer - 1){
					addStepY = 0;
				}

				layerRelocate = true;
			}

			double x = startX + cell.getLayer() * stepX;
			double y = startY + nodeCount * (localStepY + addStepY);

			cell.relocate(x, y);

			if (layerRelocate){
				layerRelocate = false;
				if (lastLayer < layers.size()){
					layers.get(lastLayer).relocate(x, y - 30);
				}
			}

			nodeCount++;
		}

	}

}