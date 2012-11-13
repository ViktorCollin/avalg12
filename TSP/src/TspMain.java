import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class TspMain {
	float[][] distanceMatrix;
	TspNode[] nodes;
	Visulizer graph;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TspMain(args.length != 0);
	}
	
	public TspMain(boolean visulize){
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			int numNodes = Integer.parseInt(in.readLine());
			nodes = new TspNode[numNodes];
			distanceMatrix = new float[numNodes][numNodes];
			for(int i=0;i<numNodes;i++){
				String line = in.readLine();
				String[] xy = line.split(" ");
				nodes[i] = new TspNode(i, Float.parseFloat(xy[0]), Float.parseFloat(xy[1]));
			}
			if(visulize){
				graph = new Visulizer(nodes);
				int[] order = new int[numNodes];
				for(int i=0;i<numNodes;i++){
					order[i] = i;
				}
				graph.drawEdges(order);
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
