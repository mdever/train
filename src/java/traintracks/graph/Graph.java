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
	
	// Simple A*
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
		
		shortestRoutesCache.put(source, routeMap);
		dirty = Boolean.FALSE;
		
		return routeMap;
	}
	
	/**
	 * Simple DFS. Choosing to do this iteratively. It just makes it easier to keep
	 * the routesMap data structure rather than pass it along through a bunch 
	 * of recursive calls.
	 * 
	 * @param from
	 * @return a map from Node, to a list of all routes that can be taken to that node
	 */
	public Map<Node, List<List<Node>>> computeAllRoutes(Node root) {
		
		Map<Node, List<List<Node>>> routesMap = new HashMap<Node, List<List<Node>>>();
		List<Node> visitedNodes = new ArrayList<Node>();
		Stack<Node> nodesToVisit = new Stack<Node>();
		Stack<Node> currentPath = new Stack<Node>();
		nodesToVisit.push(root);
		Node cursor = root;
		int timesToBacktrack = 0;
		
		while (!nodesToVisit.isEmpty()) {
			cursor = nodesToVisit.pop();
			currentPath.push(cursor);
			
			if (!cursor.hasBeenVisited()) {
				timesToBacktrack++;
				cursor.markVisited();
				visitedNodes.add(cursor);
				if (routesMap.get(cursor) == null) {
					routesMap.put(cursor, new ArrayList<List<Node>>());
				}
				routesMap.get(cursor).add(new ArrayList<Node>(currentPath));
				for (Node neighbor : cursor.getNeighbors()) {
					nodesToVisit.add(neighbor);
				}
			} else { // We're in a cycle				
				routesMap.get(cursor).add(new ArrayList<Node>(currentPath));
				for (int i = 0; i < timesToBacktrack; i++) {
					currentPath.pop();
				}
				timesToBacktrack = 0;
			}
		}
		
		// Make our nodes clean again
		for (Node node : visitedNodes) {
			node.markClean();
		}
		
		// Clean up the trivial path (i.e. C -> C)
		List<Node> trivialRoute = null;
		for (List<Node> route : routesMap.get(root)) {
			if (route.size() == 1) {
				trivialRoute = route;
			}
		}
		
		routesMap.get(root).remove(trivialRoute);
		
		
		return routesMap;
	}
	
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
	
}
