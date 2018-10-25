package pl.jclab.nndebug.models;

import java.util.ArrayList;
import java.util.List;

public class NNLayer{

	private int index = 0;
	private String name = "";
	private List<NNNode> nodes = new ArrayList<>();

	private int params = 0;


	public NNLayer(){

	}

	public NNLayer(int index, List<NNNode> nodes){

		this.index = index;
		this.nodes = nodes;
	}

	public int getIndex(){

		return index;
	}

	public void setIndex(int index){

		this.index = index;
	}

	public List<NNNode> getNodes(){

		return nodes;
	}

	public void setNodes(List<NNNode> nodes){

		this.nodes = nodes;
	}

	public String getName(){

		return name;
	}

	public void setName(String name){

		this.name = name;
	}

	public int getParams(){

		return params;
	}

	public void setParams(int params){

		this.params = params;
	}


	public NNLayer addNode(NNNode node){

		this.getNodes().add(node);
		return this;
	}

	@Override
	public String toString(){

		return "NNLayer{" +
		"index=" + index +
		", name='" + name + '\'' +
		", params=" + params +
		", nodes=" + nodes +
		'}';
	}
}
