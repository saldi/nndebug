package pl.jclab.nndebug.graph.draws;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class Cell extends Pane{

	protected String cellId;

	protected List<Cell> children = new ArrayList<>();
	protected List<Cell> parents = new ArrayList<>();

	protected Node view;

	protected int layer;

	public Cell(String cellId){

		this.cellId = cellId;
	}

	public void addCellChild(Cell cell){

		children.add(cell);
	}

	public List<Cell> getCellChildren(){

		return children;
	}

	public void addCellParent(Cell cell){

		parents.add(cell);
	}

	public List<Cell> getCellParents(){

		return parents;
	}

	public void removeCellChild(Cell cell){

		children.remove(cell);
	}

	public void setView(Node view){

		this.view = view;
		getChildren().add(view);
	}

	public Node getView(){

		return this.view;
	}

	public String getCellId(){

		return cellId;
	}

	public int getLayer(){

		return layer;
	}

	public void setLayer(int layer){

		this.layer = layer;
	}

	public void update(){

	}
}