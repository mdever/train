package traintracks.graph;

import java.util.ArrayList;
import java.util.List;

public class Node {

	private String label;
	private List<Edge> edges;
	
	enum VisitState {
		NOT_VISITED,
		VISITING,
		VISITED
	}
	
	// For use in BFS/DFS
	VisitState state = VisitState.NOT_VISITED;
	
	public Node(String label) {
		this.label = label;
		this.edges = new ArrayList<Edge>();
	}
	
	public Node(String label, Node destNode) {
		this(label, destNode, 1);
		
	}
	
	public Node(String label, Node destNode, Integer weight) {
		this.label = label;
		edges = new ArrayList<Edge>();
		
		Edge edge = new Edge(this, destNode, weight);
		edges.add(edge);
	}
	
	public Node addNeighbor(Node neighbor) {
		return addNeighbor(neighbor, 1);
	}
	
	public Node addNeighbor(Node neighbor, Integer weight) {
		if (neighbor == null) {
			return null;
		}
		
		if (this.hasNeighbor(neighbor)) {
			return this;
		}
		
		Edge edge = new Edge(this, neighbor, weight);
		edges.add(edge);
		return this;
	}
	
	public Boolean hasNeighbor(Node newNeighbor) {
		
		for (Node neighbor : getNeighbors()) {
			if (neighbor == newNeighbor) {
				return true;
			}
		}
		
		return false;
		
	}
	
	public String getLabel() {
		return label;
	}
	
	public String describe() {
		int numNeighbs = getNeighbors().size();
		StringBuilder sb = new StringBuilder();
		
		sb.append("I am ");
		sb.append(label);

		if (numNeighbs == 0) {
			sb.append(" and I have no neighbors.");
			return sb.toString();
		}
		
		sb.append(" and my neighbors are ");
		
		int i = 0;
		for (Node neighbor : getNeighbors()) {
			i++;
			sb.append(neighbor.getLabel());
			if (i == numNeighbs - 1) {
				sb.append(", and ");
			} else if (i < numNeighbs){
				sb.append(", ");
			} else {
				sb.append(".");
			}
		}
		
		return sb.toString();
	}
	
	public void addEdge(Edge edge) {
		edges.add(edge);
	}
	
	public List<Node> getNeighbors() {
		List<Node> neighbors = new ArrayList<Node>();
		for (Edge edge : edges) {
			neighbors.add(edge.getDest());
		}
		
		return neighbors;
	}
	
	public Edge getEdgeForNeighbor(Node neighbor) {
		for (Edge edge : edges) {
			if (edge.getDest().equals(neighbor)) {
				return edge;
			}
		}
		
		return null;
	}
	
	public Boolean hasUnvisitedNeighbors() {
		for (Node neighbor : getNeighbors()) {
			if (!neighbor.hasBeenVisited()) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	public Boolean hasBeenVisited() {
		return (state == VisitState.VISITED);
	}
	
	public void markAsVisited() {
		state = VisitState.VISITED;
	}
	
	public void markAsNotVisited() {
		state = VisitState.NOT_VISITED;
	}
	
	public void markAsVisiting() {
		state = VisitState.VISITING;
	}
	
	public Boolean isBeingVisited() {
		return state == VisitState.VISITING;
	}
	
	public String toString() {
		return this.label;
	}
}
