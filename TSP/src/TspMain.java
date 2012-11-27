import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class TspMain {
	private static boolean DEBUG = false;
	public static final int RUNTIME = 1990;
	public static final long breakTime = System.currentTimeMillis() + RUNTIME;
	public static final boolean CW = false;
	public static final boolean NN = true;
	private static final int NUMBER_OF_TRIES = 10;
	private int NUMBER_OF_NIEGHBORS = 20;
	
	
	private static final float P = 0.1F;
	private static Random RND = new Random();

	
	
	int[][] distanceMatrix;
	int[][] neighbors;
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
		
		if (visulize)
			DEBUG = true;
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			numNodes = Integer.parseInt(in.readLine());
			NUMBER_OF_NIEGHBORS = NUMBER_OF_NIEGHBORS < numNodes ? NUMBER_OF_NIEGHBORS : numNodes;
			float[] nodesX = new float[numNodes];
			float[] nodesY = new float[numNodes];
			
			if(CW){
				this.nodesX = nodesX;
				this.nodesY = nodesY;
			}
			
			distanceMatrix = new int[numNodes][numNodes];
			neighbors = new int[numNodes][NUMBER_OF_NIEGHBORS];
			for (int i = 0; i < numNodes; i++) {
				String line = in.readLine();
				int index = line.indexOf(" ");
				nodesX[i] = Float.parseFloat(line.substring(0, index));
				nodesY[i] = Float.parseFloat(line.substring(index + 1));
				
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
				int[] tmp = distanceMatrix[i].clone();
				Arrays.sort(tmp);
				int max = tmp[NUMBER_OF_NIEGHBORS];
				int index = 0;
				for(int j=0;j<numNodes;j++){
					if(i==j) continue;
					if(distanceMatrix[i][j] <= max){
						neighbors[i][index] = j;
						index++;
					}
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
		int[] bestTour = null;
		int bestCost = Integer.MAX_VALUE;
		
		for (int n = 0; n < NUMBER_OF_TRIES; n++) {
			//Step 1 - Initial tour
			int[] tour = initGuess();
			if (visulize){
				if(NN) graph.drawEdges(tour, "Initial guess: NN");
				if(CW) graph.drawEdges(tour, "Initial guess: CW");
			}
				
			// Step 2 - Optimizations
			twoOpt(tour);
			
			// Step 3 - Better than before?
			int cost = calculateCost(tour);
			if (DEBUG) {
				System.out.println("Cost: " + cost);
				
			}
			
			if (cost < bestCost) {
				bestTour = tour;
				bestCost = cost;
				
				if (DEBUG) {
					System.out.println("Better!");
				}
				
			}	
		}
		
		// Step 4 - Print the best tour
		if (!DEBUG)
			for (int x : bestTour)
				System.out.println(x);
	}
	
	public int[] initGuess(){
		if(CW) return clarkWright();
		if(NN) return nerestNeighbor();
		return null;
		
	}
	
	public int[] nerestNeighbor() {
		int[] tour = new int[numNodes];
		boolean[] used = new boolean[numNodes];
		int start = new Random().nextInt(numNodes);
		
		if (DEBUG)
			System.out.println("Start node: " + start);
		
		tour[0] = start;
		used[start] = true;
		
		int i = start;
		
		for (int k = 1; k < numNodes; k++) {
			int best = -1;
			int bestDistance = -1;
			
			for (int j = 0; j < numNodes; j++) {
				if (i == j)
					continue;
				
				if (!used[j] && (best == -1 || distanceMatrix[i][j] < bestDistance)) {
					best = j;
					bestDistance = distanceMatrix[i][j];
				}
			}
			
			tour[k] = best;
			used[best] = true;
			i = best;
		}
		
		return tour;
	}
		
	public int[] clarkWright() {
		int hub = findHubIndex();
		
		if (visulize) {
			int[] tour = new int[2 * numNodes - 2];
			LinkedList<Integer> tmp = new LinkedList<Integer>();
			for (int i = 0; i < numNodes; i++) {
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
				int from = edge.from;
				int to = edge.to;
				
				if (edgeCount[from] < 2 && edgeCount[to] < 2) {
					
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
		
		
		int[] result = new int[numNodes + 1];
		int j = 0;

		// Walk!
		int x = 0; // Start
		result[j] = x;
		j++;
		
		do {
			for (int i = 0; i < edges.size(); i++) {
				Savings edge = edges.get(i);				
				
				if (edge.contains(x)) {
					int next = edge.getVertex(x);
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
	
	private boolean findGraphLoop(int x, int y, List<Savings> edges, int color) {
		
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

	public void twoOpt(int[] tour) {
		int numTwoOpts = 0;
		int start = 0;

		while (start != -1 && (System.currentTimeMillis() < breakTime || visulize)) {
			start = makeOneTwoOpt(tour, start);

			if (visulize) {
				numTwoOpts++;
				graph.drawEdges(tour, "Number of 2-Opts: " + numTwoOpts);
			}
		}
	}
	
	public int makeOneTwoOpt(int[] tour, int start){
		for (int i = start; i < numNodes - 1; i++) {
			int x1 = tour[i];
			int x2 = tour[i+1];
			int visited = 0;
			
			int j = i + 2;
			
			while (visited <= numNodes - 3) { // Testa alla kanter - 3
				
				if (j == numNodes) {
					j = 0;
				}
				
				int y1 = tour[j];
				int y2 = j+1 == numNodes ? tour[0] : tour[j+1];
				
				visited++;
				
				int old_cost = distanceMatrix[x1][x2] + distanceMatrix[y1][y2];
				int new_cost = distanceMatrix[x1][y1] + distanceMatrix[y2][x2];
				
				if (new_cost < old_cost) {
					oldSwap(tour,(i+1),j);
					return i;
				} /* else if (RND.nextFloat() < 0.000004F) {
					swap(tour, (i+1), j);
					return i+1;
				} */
				
				j++;
			}
		}
		
		return -1;
	}
	
	public void oldSwap(int[] tour, int x, int y) {
		int tmp = 0;
		if(x > y){
			x--;
			y++;
			
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
	
	public static void swap(int[] tour, int x, int y) {
		if(x > y){
			x--;
			y++;
			
			int tmp = x;
			x = y;
			y = tmp;
		}
		
		
		swapHelper(tour, x, y, (y - x > tour.length/2));
	}
	
	public static void swapHelper(int[] tour, int x, int y, boolean crazyMode) {
		int tmp;
		if (!crazyMode) {
			while (x < y) {
				tmp = tour[x];
				tour[x] = tour[y];
				tour[y] = tmp;
				x++;
				y--;
			}
		} else {
			int nLeft = x;
			int nRight = tour.length - 1 - y;
			// x < y
			int nShared = Math.min(nLeft, nRight);

			// delade
			for (int i = 0; i < nShared; i++) {
//				System.out.println(i);
				tmp = tour[y + 1 + i];
				tour[y + 1 + i] = tour[x - 1 - i];
				tour[x - 1 - i] = tmp;
			}
			
//			System.out.println("Step 1: " + Arrays.toString(tour));

			if (nLeft > nRight + 1) {
				swapHelper(tour, 0, x - nShared, false);
			} else if (nLeft + 1 < nRight) {
//				System.out.println(String.format("swap(tour, %d, %d, false);", y + nShared + 1, tour.length -1));
				swapHelper(tour, y + nShared + 1, tour.length - 1, false);
			}

		}
	}

	
	public int calculateCost(int[] tour) {
		int cost = 0;

		for (int i = 1; i < tour.length; i++) {
			cost += distanceMatrix[tour[i - 1]][tour[i]];
		}
		
		cost += distanceMatrix[tour[0]][tour[tour.length - 1]];

		return cost;
	}
	
	private int calcDistance(float x1, float y1, float x2, float y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		
		return (int) Math.round(Math.sqrt(dx*dx + dy*dy)); 
	}
}
