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
	
	
	
}
