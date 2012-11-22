import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class TspMain {
	private static final boolean DEBUG = false;
	public static final int RUNTIME = 1900;
	public static final long breakTime = System.currentTimeMillis() + RUNTIME;
	public static final boolean CW = false;
	public static final boolean NN = true;
	
	
	int[][] distanceMatrix;
	int numNodes;
	Visulizer graph;
	int cost;
	boolean visulize = false;
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
		boolean visulize = args.length != 0;
		TspMain main = new TspMain(visulize);
		main.run();
		
	}

	public TspMain(boolean visulize) {
		this.visulize = visulize;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			numNodes = Integer.parseInt(in.readLine());
		//	nodes = new TspNode[numNodes];
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
				graph = new Visulizer(nodesX, nodesY);

			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run(){
		//Step 1 - Initial tour
		int[] tour = initGuess();
		if (visulize)
			graph.drawEdges(tour, cost);
		// Step 2 - Optimizations
		twoOpt(tour);
		System.out.println(Arrays.toString(tour));
		
		// Step 3 - Print
		if (!DEBUG)
			for (int x : tour)
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
		tour[0] = 0;
		cost = 0;
		used[0] = true;
		
		for (int i = 1; i < numNodes; i++) {
			int best = -1;
			
			for (int j = 0; j < numNodes; j++) {
				if (!used[j] && (best == -1 || distanceMatrix[tour[i-1]][j] < distanceMatrix[tour[i-1]][best])) {
					best = j;
				}
			}
			
			tour[i] = best;
			used[best] = true;
			cost += distanceMatrix[i-1][i];
		}
		
		return tour;
	}
	
	public int findHubIndex() {
		int hub = 0;
		int maxDistance = 0;
		float middleX = (maxX-minX)/2;
		float middleY = (maxY-minY)/2;
		for (int i = 0; i < numNodes; i++) {
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
	
	public int[] clarkWright() {
		int hub = findHubIndex();
		
		if (visulize) {
			int[] order = new int[2 * numNodes - 2];
			LinkedList<Integer> tmp = new LinkedList<Integer>();
			for (int i = 0; i < numNodes; i++) {
				if (i != hub)
					tmp.add(i);
			}
			int ith = 0;
			while (!tmp.isEmpty()) {
				order[ith] = tmp.poll();
				order[ith + 1] = hub;
				ith += 2;
			}
			graph.drawEdges(order, 0);
		}
		
		if (DEBUG)
			System.out.println("Savnings... begin");
		
		LinkedList<Savings> queue = new LinkedList<Savings>();
		for (int i = 0; i < numNodes; i++) {
			if (i == hub)
				continue;
			for (int j = i + 1; j < numNodes; j++) {
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
		for (int node = 0; node < numNodes; node++) {
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
		cost = 0;
		
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

	
	public void twoOpt(int[] tour) {
		boolean change = true;
		
		while (change) {
			change = false;
			X:
			for (int i = 0; i < numNodes; i++) {
				for (int j = i + 2; j < numNodes-2; j++) {
					
					int x1 = i;
					int x2 = (i+1);
					int y1 = j;
					int y2 = (j + 1) ;
					
					if (x2 == numNodes || y2 == numNodes)
						continue;
					
					
					int x1_x2_cost = distanceMatrix[x1][x2];
					int y1_y2_cost = distanceMatrix[y1][y2];
					int old_cost = x1_x2_cost + y1_y2_cost;
					
					int x1_y1_cost = distanceMatrix[x1][y1];
					int y2_x2_cost = distanceMatrix[y2][x2];
					int new_cost = x1_y1_cost + y2_x2_cost;
					
					if (new_cost < old_cost) {
						if(DEBUG){
						System.out.println(String.format("x1 = %d, x2 = %d, y1 = %d, y2 = %d", x1,x2,y1,y2));
						System.out.println("New cost: " + new_cost);
						System.out.println("Old cost: " + old_cost);
						System.out.println("SWAP(" +i + ", " + j + ")");
							
						System.out.println("Before: " + calcCost(tour));
						System.out.println(Arrays.toString(tour));
						}
						swap(tour, i, j);
						if(DEBUG){
						System.out.println("After: " + calcCost(tour));
						System.out.println(Arrays.toString(tour));
						System.out.println();
						}
						graph.drawEdges(tour, new_cost);
						change = true;
						break X;
					}
				}
			}
		}
	}
	
	public int calcCost(int[] tour) {
		int cost = 0;
		
		for (int i = 0; i < numNodes - 1; i++) {
			cost += distanceMatrix[tour[i]][tour[i+1]];
		}
		
		cost += distanceMatrix[tour[numNodes-1]][tour[0]];
		
		return cost;
	}
	
	public static void swap(int[] tour, int x, int y) {
		int offset = x + 1;
		for (int i = 0; i < (y-x)/2; i++) {
			int tmp = tour[offset + i];
			tour[offset + i] = tour[y - i];
			tour[y - i] = tmp;
		}		
	}
	
	public int calcDistance(TspNode a, TspNode b) {
		float dx = Math.abs(a.xPos - b.xPos);
		float dy = Math.abs(a.yPos - b.yPos);
		return (int) Math.round(Math.hypot(dx, dy));
	}
	
	public int calcDistance(float x1, float y1, float x2, float y2) {
		float dx = Math.abs(x1 - x2);
		float dy = Math.abs(y1 - y2);
		return (int) Math.round(Math.hypot(dx, dy));
	}
}
