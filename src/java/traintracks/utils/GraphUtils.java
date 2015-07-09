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

import traintracks.graph.Graph;
import traintracks.graph.Node;
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
	
	public static String printPaths(List<ArrayList<Node>> paths) {
		
		StringBuilder sb = new StringBuilder();
		
		for (List<Node> path : paths) {
			sb.append("Path: ");
			for (Node step : path) {
				sb.append(step + " ");
			}
			sb.append("\n");
		}
		
		return sb.toString();
		
	}
	
}
