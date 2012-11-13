import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Dictionary;



public class TspMain {
	int[][] distanceMatrix;
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
			distanceMatrix = new int[numNodes][numNodes];
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
			for(int i=0;i<numNodes;i++){
				for(int j=0;j<numNodes;j++){
					if(i == j) continue;
					float dx = Math.abs(nodes[i].xPos - nodes[j].xPos);
					float dy = Math.abs(nodes[i].yPos - nodes[j].yPos);
					distanceMatrix[i][j] = (int) Math.round(Math.hypot(dx, dy));
					
					
				}
			}
			
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
