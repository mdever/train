package traintracks.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import traintracks.graph.Graph;
import traintracks.graph.Node;
import traintracks.graph.Route;
import traintracks.graph.parser.GraphParser;

/**
 * Okay so I generally hate "Utility" classes, but when you're in Java land, sometimes you do as the Romans do.
 * @author mark
 *
 */
public class GraphUtils {

	public static Graph readFromInputStream(InputStream in) {
		
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			System.out.println("Could not read stream. Please make sure you use UTF-8 without BOM encoding. "
					          + "If you used notepad to create the file, please create it in another text editor, as Notepad has"
					          + "no option to save UTF-8 without BOM");
			e1.printStackTrace();
		}
		String nextLine = null;
		try {
			while ((nextLine = reader.readLine()) != null) {
				sb.append(nextLine);
			}
		} catch (IOException e) {
			System.out.println("Sorry, I had trouble reading in the file");
		}
		
		GraphParser parser = new GraphParser(sb.toString());
		
		Graph graph = null; 
		
		try {
			 graph = parser.parse();
		} catch (RuntimeException e) {
			System.out.println("Sorry I wasn't able to parse the graph\n");
			System.out.println(e.getMessage());
		}
		
		return graph;
	}
	
	public static Graph readFromFile(String filePath) throws FileNotFoundException {
		File inputFile = new File(filePath);
		return readFromInputStream(new FileInputStream(inputFile));
	}
	
	public static Integer permutationsUnderThreshold(List<Route> routes, Integer threshold) {
		int result = 0;
		
		List<List<Route>> routePermutations =  generateRotations(routes);
		
		
		for (List<Route> routePermutation : routePermutations) {
			result += permutationsUnderThreshold(new ArrayList<Route>(routePermutation), threshold, 0);
		}
		return result;
	}
	
	// This should really be a generic function acting on a List of Objects, but I was tight on time.
	private static List<List<Route>> generateRotations(List<Route> routes) {
		
		List<List<Route>> result = new ArrayList<List<Route>>();
		result.add(new ArrayList<Route>(routes));
		
		for (int i = 0; i < routes.size() - 1; i++) {
			Route temp = routes.get(0);
			for (int j = 1; j < routes.size(); j++) {
				routes.set(j - 1, routes.get(j));
			}
			routes.set(routes.size() - 1, temp);
			result.add(new ArrayList<Route>(routes));
		}
		return result;
	}

	// Nother recursive function damn these graphs
	public static int permutationsUnderThreshold(List<Route> stateToPass, Integer threshold, int currentDistance) {
		
		if (stateToPass.isEmpty()) {
			return 0;
		}
		
		Route me = stateToPass.remove(0);
		
		if (me.getDistance() + currentDistance >= threshold) {
			return 0;
		}		
		
		int paths = (int) Math.floor((double) (threshold - currentDistance) / (double) me.getDistance());
		currentDistance += me.getDistance();

		for (Route route : stateToPass) {
			paths += permutationsUnderThreshold(new ArrayList<Route>(stateToPass), threshold, currentDistance);
		}
		
		return paths;
	}
	
}
