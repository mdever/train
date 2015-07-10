package traintracks.graph;

public class Edge {

	private Node source;
	private Node destination;
	private Integer weight;
	
	// Default weight: 1
	public Edge(Node source, Node destination) {
		this.setSource(source);
		this.destination = destination;
		this.setWeight(1);
	}
	
	public Edge(Node source, Node destination, Integer weight) {
		this.setSource(source);
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

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}
}
