import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class TspMain {
	private static final boolean DEBUG = false;
	public static final int RUNTIME = 1740;
	public static final long breakTime = System.currentTimeMillis() + RUNTIME;
	public static final boolean CW = false;
	public static final boolean NN = true;
	
	
	int[][] distanceMatrix;
	int numNodes;
	newVisulizer graph;
	boolean visulize = false;
	// if CW
	float[] nodesX;
	float[] nodesY;
	private float maxX = Float.MIN_VALUE;
	private float minX = Float.MAX_VALUE;
	private float maxY = Float.MIN_VALUE;
	private float minY = Float.MAX_VALUE;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TspMain(args.length != 0).run();
		
	}

	public TspMain(boolean visulize) {
		this.visulize = visulize;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			numNodes = Integer.parseInt(in.readLine());
			float[] nodesX = new float[numNodes];
			float[] nodesY = new float[numNodes];
			if(CW){
				this.nodesX = nodesX;
				this.nodesY = nodesY;
			}
			
			distanceMatrix = new int[numNodes][numNodes];
			for (int i = 0; i < numNodes; i++) {
				String line = in.readLine();
				String[] xy = line.split(" ");
				nodesX[i] = Float.parseFloat(xy[0]);
				nodesY[i] = Float.parseFloat(xy[1]);
				if (CW){
					minX = nodesX[i] < minX ? nodesX[i] : minX;
					maxX = nodesX[i] > maxX ? nodesX[i] : maxX;
					minY = nodesY[i] < minY ? nodesY[i] : minY;
					maxY = nodesY[i] > maxY ? nodesY[i] : maxY;
				}
			}
			
			in.close();
			for (int i = 0; i < numNodes; i++) {
				for (int j = i + 1; j < numNodes; j++) {
					distanceMatrix[i][j] = calcDistance(nodesX[i], nodesY[i], nodesX[j], nodesY[j]);
					distanceMatrix[j][i] = distanceMatrix[i][j];
				}
			}
			if (visulize) {
				graph = new newVisulizer(nodesX, nodesY, distanceMatrix);

			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		//Step 1 - Initial tour
		short[] tour = initGuess();
		if (visulize){
			if(NN) graph.drawEdges(tour, "Initial guess: NN");
			if(CW) graph.drawEdges(tour, "Initial guess: CW");
		}
			
		// Step 2 - Optimizations
		twoOpt(tour);
		
		// Step 3 - Print
		if (!DEBUG)
			for (short x : tour)
				System.out.println(x);
	}
	
	public short[] initGuess(){
		if(CW) return clarkWright();
		if(NN) return nerestNeighbor();
		return null;
		
	}
	
	public short[] nerestNeighbor() {
		short[] tour = new short[numNodes];
		boolean[] used = new boolean[numNodes];
		tour[0] = 0;
		used[0] = true;
		
		for (int i = 1; i < numNodes; i++) {
			short best = -1;
			for (short j = 0; j < numNodes; j++) {
				if (!used[j] && (best == -1 || distanceMatrix[tour[i-1]][j] < distanceMatrix[tour[i-1]][best])) {
					best = j;
				}
			}
			tour[i] = best;
			used[best] = true;
		}
		return tour;
	}
		
	public short[] clarkWright() {
		short hub = findHubIndex();
		
		if (visulize) {
			short[] tour = new short[2 * numNodes - 2];
			LinkedList<Short> tmp = new LinkedList<Short>();
			for (short i = 0; i < numNodes; i++) {
				if (i != hub)
					tmp.add(i);
			}
			int ith = 0;
			while (!tmp.isEmpty()) {
				tour[ith] = tmp.poll();
				tour[ith + 1] = hub;
				ith += 2;
			}
			graph.drawEdges(tour, "Hub of CW");
		}
		
		if (DEBUG)
			System.out.println("Savnings... begin");
		
		LinkedList<Savings> queue = new LinkedList<Savings>();
		for (short i = 0; i < numNodes; i++) {
			if (i == hub)
				continue;
			for (short j = (short)(i+1); j < numNodes; j++) {
				if (j == hub)
					continue;
				int saving = distanceMatrix[hub][i] + distanceMatrix[hub][j]
						- distanceMatrix[j][i];
				Savings edge;
				if (i > j)
					edge = new Savings(j, i, saving);
				else
					edge = new Savings(i, j, saving);
				
				queue.add(edge);
			}
		}

		Collections.sort(queue);
		

		if (DEBUG)
			System.out.println("Savnings... DONE!");

		ArrayList<Savings> edges = new ArrayList<Savings>();
		
		
		byte[] edgeCount = new byte[numNodes];
		
		int color = 0;
		
		while (edges.size() < numNodes - 2) {			
			for (int i = queue.size() - 1; i >= 0; i--) {
				Savings edge = queue.get(i);
				short from = edge.from;
				short to = edge.to;
				
				if (edgeCount[from] < (short) 2 && edgeCount[to] < (short) 2) {
					
					if (
						findGraphLoop(from, to, edges, ++color) ||
						findGraphLoop(to, from, edges, ++color)
					) {
						queue.remove(i);
						break;
					}
						
					edgeCount[from]++;
					edgeCount[to]++;
					edges.add(edge);

					
					queue.remove(i);
					break;
				} else if (edgeCount[from] == 2 && edgeCount[to] == 2) {
					queue.remove(i);
					break;
				}
			}
		}
		

		
		// Add the hub
		for (short node = 0; node < numNodes; node++) {
			if (node == hub)
				continue;
			
			if (edgeCount[node] == 1)
				edges.add(new Savings(node, hub, 0));
		}
		
		
		short[] result = new short[numNodes + 1];
		int j = 0;

		// Walk!
		short x = 0; // Start
		result[j] = x;
		j++;
		
		do {
			for (int i = 0; i < edges.size(); i++) {
				Savings edge = edges.get(i);				
				
				if (edge.contains(x)) {
					short next = edge.getVertex(x);
					x = next;
					result[j] = x;
					j++;
					
					edges.remove(i);
					break;
				}
			}
			
		} while (x != 0);
			
		return result;
	}

	public short findHubIndex() {
		short hub = 0;
		int maxDistance = 0;
		float middleX = (maxX-minX)/2;
		float middleY = (maxY-minY)/2;
		for (short i = 0; i < numNodes; i++) {
			int newDist = calcDistance(middleX, middleY, nodesX[i], nodesY[i]);
			if (maxDistance > newDist) {
				hub = i;
				maxDistance = newDist;
			}
		}
		
		return hub;
	}
	
	private boolean findGraphLoop(short x, short y, List<Savings> edges, int color) {
		
		while (x != -1 && x != y) {
			boolean progress = false;
			
			for (Savings e : edges) {
				if (e.color != color && e.contains(x)) {
					x = e.getVertex(x);
					e.color = color;
					progress = true;
					break;
				}
			}
			
			if (!progress)
				break;
		}
		
		return x == y;
	}

	public void twoOpt(short[] tour) {	
		int numTwoOpts = 0;
		while (makeOneTwoOpt(tour) && (System.currentTimeMillis() < breakTime || visulize)) {
			if(visulize){
				numTwoOpts++;
				graph.drawEdges(tour, "Number of 2-Opts: "+numTwoOpts);
			}
		}
	}
	
	public boolean makeOneTwoOpt(short[] tour){
		for (int i = 0; i < numNodes-1; i++) {
			for (int j = (i+2); j < numNodes-1; j++) {
				int x1 = tour[i];
				int x2 = tour[i+1];
				int y1 = tour[j];
				int y2 = tour[j+1];
				
				int old_cost = distanceMatrix[x1][x2] + distanceMatrix[y1][y2];
				int new_cost = distanceMatrix[x1][y1] + distanceMatrix[y2][x2];
				
				if (new_cost < old_cost) {
					swap(tour, (short)(i+1), (short)j);	
					return true;
				}
			}
			int x1 = tour[i];
			int x2 = tour[i+1];
			int y1 = tour[numNodes-1];
			int y2 = tour[0];
			
			int old_cost = distanceMatrix[x1][x2] + distanceMatrix[y1][y2];
			int new_cost = distanceMatrix[x1][y1] + distanceMatrix[y2][x2];
			
			if (new_cost < old_cost) {
				swap(tour, (short)(i+1), (short)(numNodes-1));	
				return true;
			}
		}
		return false;
	}
	
	public void swap(short[] tour, short x, short y) {
		short tmp = 0;
		if(x > y){
			tmp = x;
			x = y;
			y = tmp;
		}
		while(x<y){
			tmp = tour[x];
			tour[x] = tour[y];
			tour[y] = tmp;
			x++;
			y--;
		}
		//TODO go the other way if x-y > tour.length/2 
	}
	
	public int calcDistance(float x1, float y1, float x2, float y2) {
		float dx = Math.abs(x1 - x2);
		float dy = Math.abs(y1 - y2);
		return (int) Math.round(Math.hypot(dx, dy));
	}
}
