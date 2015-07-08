package traintracks.graph;

public class Edge {

	private Node source;
	private Node destination;
	private Integer weight;
	
	// Default weight: 1
	public Edge(Node source, Node destination) {
		this.source = source;
		this.destination = destination;
		this.setWeight(1);
	}
	
	public Edge(Node source, Node destination, Integer weight) {
		this.source = source;
		source.addEdge(this);
		this.destination = destination;
		this.setWeight(weight);
	}
	
	public Node getDest() {
		return destination;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
}
