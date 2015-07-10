package traintracks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import traintracks.graph.*;
import static traintracks.utils.GraphUtils.readFromInputStream;
import static traintracks.utils.GraphUtils.readFromFile;
import static traintracks.utils.GraphUtils.permutationsUnderThreshold;

public class Main {
	
	private static final Graph  defaultGraph;
	
	static {
		defaultGraph = new Graph();
		
		Node A = new Node("A");
		Node B = new Node("B");
		Node C = new Node("C");
		Node D = new Node("D");
		Node E = new Node("E");
		
		defaultGraph.addEdge(A, B, 5);
		defaultGraph.addEdge(B, C, 4);
		defaultGraph.addEdge(C, D, 8);
		defaultGraph.addEdge(D, C, 8);
		defaultGraph.addEdge(D, E, 6);
		defaultGraph.addEdge(A, D, 5);
		defaultGraph.addEdge(C, E, 2);
		defaultGraph.addEdge(E, B, 3);
		defaultGraph.addEdge(A, E, 7);
	}

	@SuppressWarnings({ "static-access", "deprecation" })
	public static void main(String[] args) throws ParseException {
		
		/**** Graph to operate on ****/
		Graph graph = null;
		
		/**** Set up Command Line options ****/
		CommandLineParser cliParser = new DefaultParser();
		Options options = new Options();


		options.addOption(OptionBuilder.withArgName("file")
		                               .hasArg()
		                               .withDescription("The relative path to the input file")
		                               .create("f"));
		
		options.addOption("p", "pipe", false, "pipe input to stdin");
		options.addOption("v", "verbose", false, "view verbose solutions");
		options.addOption("h", "help", false, "display this text");
		
		CommandLine cmd = cliParser.parse(options, args);
		
		/***** Parse Command Line options to determine graph source *****/
		if (cmd.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("train-tracks", options);
			return;
		}
		
		if (cmd.hasOption("f") && cmd.hasOption("p")) {
			System.out.println("Cannot pipe and read from a file");
			return;
		}
		
		if (cmd.hasOption("f")) {
			if (cmd.getOptionValue("f") == null) {
				System.out.println("Please enter the filename relative to the executable if you wish to read in a graph from a file");
			}
			System.out.println("Reading graph from file " + cmd.getOptionValue("f"));
			
			String pathName = Paths.get("").toAbsolutePath().resolve(args[1]).toString();
			System.out.println("Reading from file " + pathName + "...\n");
			
			try {
				graph = readFromFile(pathName);
			} catch (FileNotFoundException e) {
				System.out.println("Sorry, I wasn't able to find that file: " + pathName);
				e.printStackTrace();
				return;
			}
		} else if (cmd.hasOption("p")) {
			System.out.println("Reading graph from stdin");
			graph = readFromInputStream(System.in);		
		} else {
			System.out.println("Using default graph");
			graph = defaultGraph;
		}

		if (cmd.hasOption("v")) {
			Answer.setVerbose(Boolean.TRUE);
			System.out.println("\nYour graph in dot file format:");
			System.out.println(graph.toDotFormat());
			System.out.println("------------------------\n");
		}
		
		/**** Answer Questions ****/
		doQuestions(graph);
	}

	private static void doQuestions(Graph graph) {
		
		Node A = graph.getNodeByName("A");
		Node B = graph.getNodeByName("B"); 
		Node C = graph.getNodeByName("C");
		Node D = graph.getNodeByName("D");
		Node E = graph.getNodeByName("E");
		
		Answer answer1 = doQuestion1(graph, A, B, C, D, E);
		
		Answer answer2 = doQuestion2(graph, A, B, C, D, E);
		
		Answer answer3 = doQuestion3(graph, A, B, C, D, E);
		
		Answer answer4 = doQuestion4(graph, A, B, C, D, E);
		
		Answer answer5 = doQuestion5(graph, A, B, C, D, E);

		Answer answer6 = doQuestion6(graph, A, B, C, D, E);

		Answer answer7 = doQuestion7(graph, A, B, C, D, E);
		
		Answer answer8 = doQuestion8(graph, A, B, C, D, E);

		Answer answer9 = doQuestion9(graph, A, B, C, D, E);

		Answer answer10 = doQuestion10(graph, A, B, C, D, E);
		
		System.out.println("Output #1: " + answer1);
		System.out.println("Output #2: " + answer2);
		System.out.println("Output #3: " + answer3);
		System.out.println("Output #4: " + answer4);
		System.out.println("Output #5: " + answer5);
		System.out.println("Output #6: " + answer6);
		System.out.println("Output #7: " + answer7);
		System.out.println("Output #8: " + answer8);
		System.out.println("Output #9: " + answer9);
		System.out.println("Output #10 " + answer10);
	}

	private static Answer doQuestion1(Graph graph, Node A, Node B, Node C, Node D, Node E) {
		Answer answer = new Answer();
		
		Integer distance = graph.from(A).to(B).to(C).getDistance();
		String answerString = distance == -1 ? "NO SUCH ROUTE" : String.valueOf(distance);
		
		answer.setBaseAnswer(answerString);
		
		if (answerString.equals("NO SUCH ROUTE")) {
			return answer;
		}
		
		String verboseAnswer = "\tA -> B : " + A.getEdgeForNeighbor(B).getWeight() + "\n";
		verboseAnswer += "\tB -> C : " + B.getEdgeForNeighbor(C).getWeight();
		
		answer.setVerboseAnswer(verboseAnswer);
		
		return answer;
	}
	
	private static Answer doQuestion2(Graph graph, Node A, Node B, Node C, Node D, Node E) {
		Answer answer = new Answer();
		
		Integer distance = graph.from(A).to(D).getDistance();
		String answerString = distance == -1 ? "NO SUCH ROUTE" : String.valueOf(distance);
		
		answer.setBaseAnswer(answerString);
		
		if (answerString.equals("NO SUCH ROUTE")) {
			return answer;
		}
		
		String verboseAnswer = "\tA -> D : " + A.getEdgeForNeighbor(D).getWeight();
		
		answer.setVerboseAnswer(verboseAnswer);
		
		return answer;
	}
	
	private static Answer doQuestion3(Graph graph, Node A, Node B, Node C, Node D, Node E) {
		Answer answer = new Answer();
		
		Integer distance = graph.from(A).to(D).to(C).getDistance();
		String answerString = distance == -1 ? "NO SUCH ROUTE" : String.valueOf(distance);
		
		answer.setBaseAnswer(answerString);
		
		if (answerString.equals("NO SUCH ROUTE")) {
			return answer;
		}
		
		String verboseAnswer = "\tA -> D : " + A.getEdgeForNeighbor(D).getWeight() + "\n";
		      verboseAnswer += "\tD -> C : " + D.getEdgeForNeighbor(C).getWeight();
		answer.setVerboseAnswer(verboseAnswer);
		
		return answer;
	}
	
	private static Answer doQuestion4(Graph graph, Node A, Node B, Node C, Node D, Node E) {
		Answer answer = new Answer();
		
		Integer distance = graph.from(A).to(E).to(B).to(C).to(D).getDistance();
		String answerString = distance == -1 ? "NO SUCH ROUTE" : String.valueOf(distance);
		
		answer.setBaseAnswer(answerString);
		
		if (answerString.equals("NO SUCH ROUTE")) {
			return answer;
		}
		
		String verboseAnswer = "\tA -> E : " + A.getEdgeForNeighbor(E).getWeight() + "\n";
		      verboseAnswer += "\tE -> B : " + E.getEdgeForNeighbor(B).getWeight() + "\n";
		      verboseAnswer += "\tB -> C : " + B.getEdgeForNeighbor(C).getWeight() + "\n";
		      verboseAnswer += "\tC -> D : " + C.getEdgeForNeighbor(E).getWeight();
		answer.setVerboseAnswer(verboseAnswer);
		
		return answer;
	}
	
	private static Answer doQuestion5(Graph graph, Node A, Node B, Node C, Node D, Node E) {
		Answer answer = new Answer();
		
		Integer distance = graph.from(A).to(E).to(D).getDistance();
		String answerString = distance == -1 ? "NO SUCH ROUTE" : String.valueOf(distance);
		
		answer.setBaseAnswer(answerString);
		
		if (answerString.equals("NO SUCH ROUTE")) {
			return answer;
		}
		
		String verboseAnswer = "\tA -> E : " + A.getEdgeForNeighbor(E).getWeight() + "\n";
		      verboseAnswer += "\tE -> D : " + E.getEdgeForNeighbor(D).getWeight();

		answer.setVerboseAnswer(verboseAnswer);
		
		return answer;
	}
	
	private static Answer doQuestion6(Graph graph, Node A, Node B, Node C, Node D, Node E) {
		Answer answer = new Answer();
		
		List<ArrayList<Node>> allCPaths = graph.findPaths(C);
		allCPaths.remove(0);
		List<ArrayList<Node>> validCPaths = new ArrayList<ArrayList<Node>>();
		for (ArrayList<Node> path : allCPaths) {
			if (path.size() <= 3 && path.get(path.size() - 1).equals(C)) {
				validCPaths.add(path);
			}
		}
		
		answer.setBaseAnswer(String.valueOf(validCPaths.size()));
		
		if (validCPaths.size() != 0) {
			String verboseAnswer = "\tRoutes: ";
			for (List<Node> path : validCPaths) {
				verboseAnswer += "[";
				verboseAnswer += StringUtils.join(path, ", ");
				verboseAnswer += "] ";
			}
			
			answer.setVerboseAnswer(verboseAnswer);
		}
		
		return answer;
	}
	
	private static Answer doQuestion7(Graph graph, Node A, Node B, Node C, Node D, Node E) {
		Answer answer = new Answer();
		
		List<ArrayList<Node>> allAPaths = graph.findPaths(A);
		allAPaths.remove(0);
		List<ArrayList<Node>> AtoCPaths = new ArrayList<ArrayList<Node>>();
		for (ArrayList<Node> path : allAPaths) {
			if (path.size() <= 3 && path.get(path.size() - 1).equals(C)) {
				AtoCPaths.add(path);
			}
		}
		
		answer.setBaseAnswer(String.valueOf(AtoCPaths.size()));
		
		if (AtoCPaths.size() != 0) {
			String verboseAnswer = "\tRoutes: ";
			for (List<Node> path : AtoCPaths) {
				verboseAnswer += "[";
				verboseAnswer += StringUtils.join(path, ", ");
				verboseAnswer += "] ";
			}
			
			answer.setVerboseAnswer(verboseAnswer);
		}
		
		return answer;
	}

	private static Answer doQuestion8(Graph graph, Node A, Node B, Node C, Node D, Node E) {
		Answer answer = new Answer();
		
		Map<Node, Route> shortestRoutesFromA = graph.computeShortestRoutes(A);
		Route shortestRouteFromAtoC = shortestRoutesFromA.get(C);
		
		if (shortestRouteFromAtoC != null) {
			answer.setBaseAnswer(String.valueOf(shortestRouteFromAtoC.getDistance()));
		} else {
			answer.setBaseAnswer("NO SUCH ROUTE");
			return answer;
		}
		
		String verboseAnswer = "\tRoute: [" + StringUtils.join(shortestRouteFromAtoC.getPath(), ", ") + "]";
		
		answer.setVerboseAnswer(verboseAnswer);
		
		return answer;
		
	}

	private static Answer doQuestion9(Graph graph, Node A, Node B, Node C, Node D, Node E) {

		Answer answer = new Answer();
		
		List<ArrayList<Node>> allPathsFromB = graph.findPaths(B);
		List<ArrayList<Node>> pathsFromBtoB = new ArrayList<ArrayList<Node>>();
		for (ArrayList<Node> path : allPathsFromB) {
			if (path.get(path.size() - 1).equals(B)) {
				pathsFromBtoB.add(path);
			}
		}
		
		if (pathsFromBtoB.size() == 0) {
			answer.setBaseAnswer("NO SUCH PATH");
			return answer;
		}
		
		Route shortestRoute = new Route();
		shortestRoute.setDistance(Integer.MAX_VALUE);
		for (ArrayList<Node> path : pathsFromBtoB) {
			Route route = graph.from(path.remove(0));
			for (Node hop : path) {
				route = route.to(hop);
			}
			if (route.getDistance() == 0) {
				continue;
			}
			if (route.getDistance() < shortestRoute.getDistance()) {
				shortestRoute = route;
			}
		}
		
		answer.setBaseAnswer(String.valueOf(shortestRoute.getDistance()));
		
		String verboseAnswer = "\tRoute: [" + StringUtils.join(shortestRoute.getPath(), ", ") + "]";
		
		answer.setVerboseAnswer(verboseAnswer);
		
		return answer;
	}
	
	private static Answer doQuestion10(Graph graph, Node A, Node B, Node C, Node D, Node E) {
		Answer answer = new Answer();
		
		List<ArrayList<Node>> allCPaths = graph.findPaths(C);
		allCPaths.remove(0);
		List<Route> pathsFromCtoC = new ArrayList<Route>();
		
		for (ArrayList<Node> path : allCPaths) {
			if (path.get(path.size() - 1).equals(C)) { // If this path ends on itself (forms a cycle)
				Route route = new Route();
				for (Node node : path) {
					route.addHop(node);
				}
				route.calculateDistance();
				route.setHops(route.getPath().size() - 1);
				pathsFromCtoC.add(route);
			}
		}
		
		if (pathsFromCtoC.isEmpty()) {
			answer.setBaseAnswer("NO SUCH PATH");
			return answer;
		}
		
		
		// pathsFromCtoC contains all the cyclic routes which lead back to C. We need to find all the permutations of those paths that are under a distance of 30.
		int routesUnder30 = permutationsUnderThreshold(pathsFromCtoC, 30);
		
		answer.setBaseAnswer(String.valueOf(routesUnder30));
		
		return answer;
		
	}
	
}
