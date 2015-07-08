package traintracks;

import java.util.List;
import java.util.Map;

import traintracks.graph.*;

public class Main {

	public static void main(String[] args) {
		
		Graph graph = new Graph();
		
		Node A = new Node("A");
		Node B = new Node("B");
		Node C = new Node("C");
		Node D = new Node("D");
		Node E = new Node("E");
		
		graph.addEdge(A, B, 5);
		graph.addEdge(B, C, 4);
		graph.addEdge(C, D, 8);
		graph.addEdge(D, C, 8);
		graph.addEdge(D, E, 6);
		graph.addEdge(A, D, 5);
		graph.addEdge(C, E, 2);
		graph.addEdge(E, B, 3);
		graph.addEdge(A, E, 7);
		
		
		System.out.println("First Time");
		long start = System.nanoTime();
		

		Map<Node, Route> routes = graph.computeShortestRoutes(A);
		long duration = System.nanoTime() - start;
		System.out.println("Done in " + String.valueOf(duration) + " nanos");
		System.out.println("The Routes Are");
		for (Map.Entry<Node, Route> entry : routes.entrySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Node " + entry.getKey().getLabel() + ": Distance " + entry.getValue().getDistance() + "\n");
			for (Node node : entry.getValue().getPath()) {
				sb.append("Jump to " + node.getLabel() + ". ");
			}
			sb.append("\n");
			sb.append("Total hops: " + entry.getValue().getHops());
			System.out.println(sb.toString() + "\n---------------------");
		}
		System.out.println("Second Time");
		start = System.nanoTime();
		graph.computeShortestRoutes(A);
		long secondDuration = System.nanoTime() - start;
		System.out.println("Done in " + String.valueOf(secondDuration) + " nanoseconds.");
		System.out.println("For " + String.valueOf(duration/secondDuration) + " times improvement");
		
		
		Map<Node, List<List<Node>>> paths = graph.computeAllRoutes(C);
		
		System.out.println(paths);
		
	}
	
}
