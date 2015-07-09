package traintracks.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Container class for all the nodes. Doesn't hold a reference to edges, as those are held by the nodes themselves. 
 * Although it is possible to construct new edges between nodes directly, it is recommended that all edges be created
 * through the parent Graph, as it will handle all the housekeeping.
 * 
 * It should be noted that creating all new connections in the graph doesn't support unconnected subgraphs. For example, the following graph (graphs, really)
 * could not be made, because the E -> F segment, is not connected to the rest of the graph. That is a simplifying assumption that I made, but a safe one.
 * 
 * A -> B
 * A -> C
 * B -> C
 * C -> D
 * E -> F
 * 
 * @author mark
 */

public class Graph {

	private List<Node> allNodes = new ArrayList<Node>();
	
	// So that we don't do shortest paths calculation if we don't have to
	private Map<Node, Map<Node, Route>> shortestRoutesCache = new HashMap<Node, Map<Node, Route>>();
	
	// To indicate whether we need to perform shortest paths calculation, or we can look in the cache
	private Boolean dirty = Boolean.TRUE;
	
	public Graph() {}

	public Graph addEdge(Node source, Node dest, Integer weight) {
		dirty = Boolean.TRUE;
		shortestRoutesCache.clear();
		
		if (!allNodes.contains(source)) 
			allNodes.add(source);
		
		if (!allNodes.contains(dest))
			allNodes.add(dest);
		
		if (source.hasNeighbor(dest)) {
			return this;
		}
		
		new Edge(source, dest, weight); 
		
		return this;
	}
	
	// Pass through method with default weight of 1
	public Graph addEdge(Node source, Node dest) {
		this.addEdge(source, dest, 1);
		return this;
	}
	
	public Route shortestPath(Node from, Node to) {
		return computeShortestRoutes(from).get(to);
	}
	
	/**
	 * Simple A*. Computes shortest paths to all reachable nodes from the source node given.
	 * This method will cache the result so that if it is called later on the same node and nothing
	 * has been changed (no nodes added or removed), it will not compute the result again, 
	 * but will return the cached value.
	 * 
	 * @param source
	 * @return
	 */
	public Map<Node, Route> computeShortestRoutes(Node source) {
		
		// Don't perform calculation if it's been done before and nothings changed 
		if (!dirty && (shortestRoutesCache.get(source) != null)) {
			return shortestRoutesCache.get(source);
		}

		List<Node>         nodes     = new LinkedList<Node>();
		Map<Node, Integer> weights   = new HashMap<Node, Integer>();
		Map<Node, Node>    prevNodes = new HashMap<Node, Node>();
		
		weights.put(source, 0);
		
		// Initialize all nodes to have effectively infinite distance
		for (Node node : allNodes) {
			if (!node.equals(source)) {
				weights.put(node, Integer.MAX_VALUE);
				prevNodes.put(node, null);
			}
			nodes.add(node);
		}
		
		// A*
		while (!nodes.isEmpty()) {
			Node node = findMinimum(nodes, weights);
			
			for (Node neighbor : node.getNeighbors()) {
				Integer alternateWeight = weights.get(node) + node.getEdgeForNeighbor(neighbor).getWeight();
				if (alternateWeight < weights.get(neighbor)) {
					weights.put(neighbor, alternateWeight);
					prevNodes.put(neighbor, node);
				}
			}
		}
		
		Map<Node, Route> routeMap = makeRouteMap(source, weights, prevNodes);
		
		// Cache result
		shortestRoutesCache.put(source, routeMap);
		
		dirty = Boolean.FALSE;
		
		return routeMap;
	}
	
	/**
	 * Nice public interface which passes through to the method that does the hard work.
	 * 
	 * @param source
	 * @return
	 */
	public List<ArrayList<Node>> findPaths(Node source) {
		return findPaths(source, new ArrayList<ArrayList<Node>>(), new Stack<Node>(), 0);
	}
	
	/**
	 * The nitty gritty implementation of finding the paths for each node. Pretty much
	 * a recursive DFS that tracks it currentPath at every step. This implementation will
	 * not continue to hop through cycles.
	 * 
	 * @param source
	 * @param sharedPathsList
	 * @param currentPath
	 * @param count
	 * @return A List of all the "Paths" taken from this node.
	 */
	public List<ArrayList<Node>> findPaths(Node source, List<ArrayList<Node>> sharedPathsList, Stack<Node> currentPath, int count) {

		currentPath.push(source);
		source.markAsVisiting();
		
		// Copy current path and add it to list of all paths
		ArrayList<Node> path = new ArrayList<Node>(currentPath);
		sharedPathsList.add(sharedPathsList.size(), path);
		
		if (++count >= 10) {
			return sharedPathsList;
		}
		
		for (Node neighbor : source.getNeighbors()) {
			if (neighbor.isBeingVisited()) {
				ArrayList<Node> temp = new ArrayList<Node>(currentPath);
				temp.add(temp.size(), neighbor);
				sharedPathsList.add(sharedPathsList.size(), temp);
			} else {
				findPaths(neighbor, sharedPathsList, currentPath, count);
			}
		}
		
		currentPath.pop();
		source.markAsVisited();
		return sharedPathsList;
	}
	
	/**
	 * Helper method to turn a Collection of "Paths" into a Collection of Routes
	 * @param source
	 * @param weights
	 * @param prevNodes
	 * @return
	 */
	private Map<Node, Route> makeRouteMap(Node source, Map<Node, Integer> weights,
			                            Map<Node, Node> prevNodes) {
		
		Map<Node, Route> routes = new HashMap<Node, Route>();
		
		for (Map.Entry<Node, Integer> entry : weights.entrySet()) {
			Integer hops = 1;
			Node node = entry.getKey();
			if (node.equals(source)) {
				continue;
			}
			Route route = new Route();
			route.setDistance(entry.getValue());
			
			Node nextHop = node;
			route.getPath().add(0, nextHop);
			while ((nextHop = prevNodes.get(nextHop)) != null) {
				if (nextHop.equals(source)) {
					continue;
				}
				hops++;
				route.getPath().add(0, nextHop);
			}
			route.setHops(hops);
			routes.put(node, route);
		}
		
		return routes;
	}

	/**
	 * Finds the node which has the lowest weight in the weights Map which is in the nodesLeft list.
	 * After it finds it, it removes it from the nodesLeft list
	 * Pretty much a helper for A*
	 * 
	 * @param nodes
	 * @param weights
	 * @return
	 */
	private Node findMinimum(List<Node> nodesLeft, Map<Node, Integer> weights) {
		List<Map.Entry<Node, Integer>> sortedNodes = new ArrayList<Map.Entry<Node, Integer>>();
		sortedNodes.addAll(weights.entrySet());
		
		Collections.sort(sortedNodes, new Comparator<Map.Entry<Node, Integer>>() {

			@Override
			public int compare(java.util.Map.Entry<Node, Integer> e1,
					java.util.Map.Entry<Node, Integer> e2) {
				
				if (e1.getValue() > e2.getValue()) {
					return 1;
				} else if (e1.getValue() < e2.getValue()) {
					return -1;
				} else {
					return 0;
				}
			}
			
		});
		
		// Find the first node in sortedNodes that is in the list
		for (int i = 0; i < sortedNodes.size(); i++) {
			Node node = sortedNodes.get(i).getKey();
			if (nodesLeft.contains(node)) {
				nodesLeft.remove(node);
				return node;
			}
		}
		
		// We didn't find it
		return null;
		
	}
	
	public Route from(Node source) {
		Route route = new Route();
		route.getPath().add(source);
		route.setDistance(0);
		route.setHops(0);
		return route;
	}
	
	public Node getNodeByName(String name) {
		for (Node node : allNodes) {
			if (name.equals(node.getLabel())) {
				return node;
			}
		}
		
		return null;
	}
	
}
