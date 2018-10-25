package pl.jclab.nndebug.models;

public class NNNode{

	private String id;
	private String name;
	private String bias;
	private String activationFunction;
	private int layer;

	public NNNode(){

		id = "";
		name = "";
		bias = "";
		activationFunction = "";
	}

	public String getId(){

		return id;
	}

	public void setId(String id){

		this.id = id;
	}

	public String getName(){

		return name;
	}

	public void setName(String name){

		this.name = name;
	}

	public String getBias(){

		return bias;
	}

	public void setBias(String bias){

		this.bias = bias;
	}

	public String getActivationFunction(){

		return activationFunction;
	}

	public void setActivationFunction(String activationFunction){

		this.activationFunction = activationFunction;
	}

	public int getLayer(){

		return layer;
	}

	public void setLayer(int layer){

		this.layer = layer;
	}

	@Override
	public String toString(){

		return "NNNode{" +
		"id='" + id + '\'' +
		", name='" + name + '\'' +
		", bias='" + bias + '\'' +
		", activationFunction='" + activationFunction + '\'' +
		", layer='" + layer + '\'' +
		'}';
	}
}
