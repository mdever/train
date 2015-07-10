package traintracks.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Struct style class to represent the route from one node to another 
 * @author mark
 *
 */

public class Route {

	private Integer distance;
	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	
	public Integer calculateDistance() {
		Integer distance = 0;
		for (int i = 0; i < path.size(); i++) {
			if (i != path.size() - 1) {
				distance += path.get(i).getEdgeForNeighbor(path.get(i + 1)).getWeight();
			}
		}
		
		this.distance = distance;
		return distance;
	}

	public Integer getHops() {
		return hops;
	}

	public void setHops(Integer hops) {
		this.hops = hops;
	}

	public List<Node> getPath() {
		return path;
	}

	public void setPath(List<Node> path) {
		this.path = path;
	}

	private Integer hops;
	private List<Node> path;
	
	public Route() {
		path = new ArrayList<Node>();
	}
	
	public Route addHop(Node node) {
		path.add(path.size(), node);
		
		return this;
	}
	
	public Route to(Node dest) {
		// Indicates that no such route exists
		if (distance == -1) {
			return this;
		}
		if (path.get(hops).getEdgeForNeighbor(dest) == null) {
			distance = -1;
			return this;
		}
		distance += path.get(hops).getEdgeForNeighbor(dest).getWeight();
		path.add(dest);
		hops++;
		return this;
	}
	
	
}
