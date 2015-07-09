package traintracks;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import traintracks.graph.*;
import static traintracks.utils.GraphUtils.readFromInputStream;
import static traintracks.utils.GraphUtils.readFromFile;

public class Main {
	
	private static final Graph  defaultGraph;
	private static final String FROM_STDIN_INDICATOR = "-p";
	private static final String FROM_FILE_INDICATOR  = "-f";
	private static final String USAGE_TEXT = "Hi. You can choose to execute this program on the default graph provided, or you can give your own\n"
			                               + "input either as a file, or piped into the program. If you want to run the program on the test input\n"
			                               + "data provided, run it with no command line arguments. To give file input, use the -f switch and enter\n"
			                               + "the filepath relative to this executable. To pipe in input through stdin, use the -p flag. The format\n"
			                               + "should be a well-formed comma seperated list with each entry consisting of two letters and a number"
			                               + "As per the instructions, the program expects a graph consisting of nodes A, B, C, D and E.";
	
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

	public static void main(String[] args) {
		
		Graph graph = null;
		
		/***** Parse Command Line options to get graph source *****/
		if (args.length == 0) {
			graph = defaultGraph;
		} else if (args[0].equalsIgnoreCase(FROM_STDIN_INDICATOR)) {
			System.out.println("Reading from stdin");
			graph = readFromInputStream(System.in);			
		} else if (args[0].equalsIgnoreCase(FROM_FILE_INDICATOR)) {
			if (args.length != 2) {
				System.out.println("Please enter the filename relative to the executable if you wish to read in a graph from a file");
				return;
			}	
			String pathName = Paths.get("").toAbsolutePath().resolve(args[1]).toString();
			System.out.println("Reading from file " + pathName + "...\n");
			try {
				graph = readFromFile(pathName);
			} catch (FileNotFoundException e) {
				System.out.println("Sorry, I wasn't able to find that file: " + pathName);
				e.printStackTrace();
				return;
			}
		} else {
			System.out.println("Sorry, I didn't recognize that option. Check out the usage below\n");
			System.out.println(USAGE_TEXT);
		}
		
		
		doQuestions(graph);
	}

	private static void doQuestions(Graph graph) {
		
		Node A = graph.getNodeByName("A");
		Node B = graph.getNodeByName("B");
		Node C = graph.getNodeByName("C");
		Node D = graph.getNodeByName("D");
		Node E = graph.getNodeByName("E");
		
		Integer distance1 = graph.from(A).to(B).to(C).getDistance();
		Integer distance2 = graph.from(A).to(D).getDistance();
		Integer distance3 = graph.from(A).to(D).to(C).getDistance();
		Integer distance4 = graph.from(A).to(E).to(B).to(C).to(D).getDistance();
		Integer distance5 = graph.from(A).to(E).to(D).getDistance();
		
		String answer1 = distance1 == -1 ? "NO SUCH ROUTE" : String.valueOf(distance1);
		String answer2 = distance2 == -1 ? "NO SUCH ROUTE" : String.valueOf(distance2);
		String answer3 = distance3 == -1 ? "NO SUCH ROUTE" : String.valueOf(distance3);
		String answer4 = distance4 == -1 ? "NO SUCH ROUTE" : String.valueOf(distance4);
		String answer5 = distance5 == -1 ? "NO SUCH ROUTE" : String.valueOf(distance5);

		List<ArrayList<Node>> allCPaths = graph.findPaths(C);
		int validCPaths = 0;
		for (ArrayList<Node> path : allCPaths) {
			if (path.size() <= 3 && path.get(path.size() - 1).equals(C)) {
				validCPaths++;
			}
		}
		
		String answer6 = String.valueOf(validCPaths);
		
		List<ArrayList<Node>> allAPaths = graph.findPaths(A);
		int validAPaths = 0;
		for (ArrayList<Node> path : allAPaths) {
			if (path.size() <= 4 && path.get(path.size() - 1).equals(C)) {
				validAPaths++;
			}
		}
		
		String answer7 = String.valueOf(validAPaths);
		
		Map<Node, Route> shortestRoutesFromA = graph.computeShortestRoutes(A);
		Route shortestRouteFromAtoC = shortestRoutesFromA.get(C);
		String answer8 = String.valueOf(shortestRouteFromAtoC.getDistance());
		
		List<ArrayList<Node>> allPathsFromB = graph.findPaths(B);
		List<ArrayList<Node>> pathsFromBtoB = new ArrayList<ArrayList<Node>>();
		for (ArrayList<Node> path : allPathsFromB) {
			if (path.get(path.size() - 1).equals(B)) {
				pathsFromBtoB.add(path);
			}
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
		
		String answer9 = String.valueOf(shortestRoute.getDistance()); 
		
		
		ArrayList<ArrayList<Node>> pathsFromCtoC = new ArrayList<ArrayList<Node>>();
		for (ArrayList<Node> path : allCPaths) {
			if (path.get(path.size() - 1).equals(C)) {
				path.remove(0);
				pathsFromCtoC.add(path);
			}
		}
		
		List<Route> routesFromCtoCLessThan30 = new ArrayList<Route>();
		for (List<Node> path : pathsFromCtoC) {
			Route route = graph.from(C);
			for (Node node : path) {
				route = route.to(node);
			}
			
			if (route.getDistance() < 30) {
				routesFromCtoCLessThan30.add(route);
			}
		}
		
		String answer10 = String.valueOf(routesFromCtoCLessThan30.size());
		
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


}
