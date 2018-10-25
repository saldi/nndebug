package pl.jclab.nndebug.graph;

import pl.jclab.nndebug.graph.draws.Cell;
import pl.jclab.nndebug.graph.draws.Edge;
import pl.jclab.nndebug.graph.draws.Layer;
import pl.jclab.nndebug.graph.draws.NodeCell;
import pl.jclab.nndebug.models.NNEdge;
import pl.jclab.nndebug.models.NNGraph;
import pl.jclab.nndebug.models.NNLayer;
import pl.jclab.nndebug.models.NNNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model{

	Cell graphParent;

	List<Layer> allLayer;
	List<Layer> addedLayer;
	List<Layer> removedLayer;

	List<Cell> allCells;
	List<Cell> addedCells;
	List<Cell> removedCells;

	List<Edge> allEdges;
	List<Edge> addedEdges;
	List<Edge> removedEdges;

	Map<String, Cell> cellMap;

	public Model(){

		graphParent = new Cell("_ROOT_");
		clear();
	}

	public void clear(){

		allLayer = new ArrayList<>();
		addedLayer = new ArrayList<>();
		removedLayer = new ArrayList<>();

		allCells = new ArrayList<>();
		addedCells = new ArrayList<>();
		removedCells = new ArrayList<>();

		allEdges = new ArrayList<>();
		addedEdges = new ArrayList<>();
		removedEdges = new ArrayList<>();

		cellMap = new HashMap<>();
	}

	public void clearAddedLists(){

		addedLayer.clear();
		addedCells.clear();
		addedEdges.clear();
	}

	public List<Layer> getAllLayer(){

		return allLayer;
	}

	public List<Layer> getAddedLayer(){

		return addedLayer;
	}

	public List<Layer> getRemovedLayer(){

		return removedLayer;
	}

	public List<Cell> getAddedCells(){

		return addedCells;
	}

	public List<Cell> getRemovedCells(){

		return removedCells;
	}

	public List<Cell> getAllCells(){

		return allCells;
	}

	public List<Edge> getAddedEdges(){

		return addedEdges;
	}

	public List<Edge> getRemovedEdges(){

		return removedEdges;
	}

	public List<Edge> getAllEdges(){

		return allEdges;
	}

	public void addGraph(NNGraph graph){

		for (NNLayer layer : graph.getLayers()){
			addLayer(layer);
		}


		for (NNNode node : graph.getNodes()){
			addCell(node);
		}

		for (NNEdge edge : graph.getEdges()){
			addEdge(edge);
		}
	}

	private void addLayer(NNLayer nnLayer){

		Layer layer = new Layer(nnLayer);
		addedLayer.add(layer);
	}

	public void addCell(NNNode node){

		switch (node.getActivationFunction()){
			default:
				NodeCell rectangleCell = new NodeCell(node);
				rectangleCell.setLayer(node.getLayer());
				addCell(rectangleCell);
				break;
		}
	}

	private void addCell(Cell cell){

		addedCells.add(cell);
		cellMap.put(cell.getCellId(), cell);
	}

	public void addEdge(NNEdge nnEdge){

		Cell sourceCell = cellMap.get(nnEdge.getFrom());
		Cell targetCell = cellMap.get(nnEdge.getTo());

		Edge edge = new Edge(sourceCell, targetCell, nnEdge);
		addedEdges.add(edge);
	}

	public void attachOrphansToGraphParent(List<Cell> cellList){

		for (Cell cell : cellList){
			if (cell.getCellParents().size() == 0){
				graphParent.addCellChild(cell);
			}
		}
	}

	public void disconnectFromGraphParent(List<Cell> cellList){

		for (Cell cell : cellList){
			graphParent.removeCellChild(cell);
		}
	}

	public void merge(){

		allLayer.addAll(addedLayer);
		allLayer.removeAll(removedLayer);

		addedLayer.clear();
		removedLayer.clear();

		allCells.addAll(addedCells);
		allCells.removeAll(removedCells);

		addedCells.clear();
		removedCells.clear();

		allEdges.addAll(addedEdges);
		allEdges.removeAll(removedEdges);

		addedEdges.clear();
		removedEdges.clear();
	}

}
