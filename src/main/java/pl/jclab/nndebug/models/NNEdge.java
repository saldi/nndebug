package pl.jclab.nndebug.models;

public class NNEdge{

	private String id;
	private String from;
	private String to;
	private String direction;
	private Double weight;

	public NNEdge(){

	}

	public NNEdge(String from, String to, String direction, Double weight){

		this.from = from;
		this.to = to;
		this.direction = direction;
		this.weight = weight;
	}

	public NNEdge(NNNode node1, NNNode node2, Double weight){

		this.from = node1.getId();
		this.to = node2.getId();
		this.weight = weight;

	}

	public String getFrom(){

		return from;
	}

	public void setFrom(String from){

		this.from = from;
	}

	public String getTo(){

		return to;
	}

	public void setTo(String to){

		this.to = to;
	}

	public String getDirection(){

		return direction;
	}

	public void setDirection(String direction){

		this.direction = direction;
	}

	public Double getWeight(){

		return weight;
	}

	public void setWeight(Double weight){

		this.weight = weight;
	}

	public String getId(){

		return id;
	}

	public void setId(String id){

		this.id = id;
	}

	@Override
	public String toString(){

		return "Edge{" +
		"from='" + from + '\'' +
		", to='" + to + '\'' +
		", direction='" + direction + '\'' +
		", weight='" + weight + '\'' +
		", id='" + id + '\'' +
		'}';
	}
}
