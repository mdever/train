package traintracks.graph.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import traintracks.graph.Graph;
import traintracks.graph.Node;

public class GraphParser {

	private String graphAsString;
	private static Pattern nodeRegex = Pattern.compile("[a-zA-Z][a-zA-Z][0-9]+");
	private Map<String, Node> nodes;
	
	/**
	 * Text of csv input (eg. AB3, CD5, AG3). This isn't very robust parsing.
	 * @param graphAsString
	 */
	public GraphParser(String graphAsString) {
		this.graphAsString = graphAsString;
		nodes = new HashMap<String, Node>();
	}
	
	public Graph parse() {
		Graph graph = new Graph();
		
		String[] nodes = graphAsString.split(",");
		for (String nodeString : nodes) {
			nodeString = nodeString.trim();
			validateNode(nodeString);
			
			String firstNodeName = String.valueOf(nodeString.charAt(0));
			String secondNodeName = String.valueOf(nodeString.charAt(1));
			Integer distance = Integer.parseInt(nodeString.substring(2));
			
			Node firstNode = null;
			Node secondNode = null;
			if (!nodeExists(firstNodeName)) {
				firstNode = new Node(firstNodeName);
				this.nodes.put(firstNodeName, firstNode);
			} else {
				firstNode = this.nodes.get(firstNodeName);
			}
			
			if (!nodeExists(secondNodeName)) {
				secondNode = new Node(secondNodeName);
				this.nodes.put(secondNodeName, secondNode);
			} else {
				secondNode = this.nodes.get(secondNodeName);
			}
			
			graph.addEdge(firstNode, secondNode, distance);
		}
		
		return graph;
	}
	
	private boolean nodeExists(String nodeName) {
		return nodes.containsKey(nodeName);
	}

	private void validateNode(String node) {
		Matcher m = nodeRegex.matcher(node);
		if (!m.matches()) {
			throw new RuntimeException("Node description " + node + " does not fit the expected format. Must be two characters followed by a number with no spaces or special characters in between. Distance must be an integer.");
		}
		
	}
}
