import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;


public class TspMainRoundTwo {
	private static final boolean USE_RANDOM_IN_INIT = true;
	protected static boolean DEBUG = false;
	private static final int NUMBER_OF_TRIES = 20;
	private int NUMBER_OF_NIEGHBORS = 25;
	private static boolean BENCHMARK = false;
	
	// Random things
	
	private static Random RND = new Random();

	int[][] distanceMatrix;
	int[][] neighbors;
	int numNodes;
	int[] indexes;
	newVisulizer graph;
	boolean visulize = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		BENCHMARK = (System.getenv("BENCHMARK") != null);
		
		new TspMainRoundTwo(args.length != 0).run();
	}

	public TspMainRoundTwo(boolean visulize) {
		this.visulize = visulize;

		if (visulize)
			DEBUG = true;


		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			numNodes = Integer.parseInt(in.readLine());
			
			if (numNodes == 1) {
				System.out.println(0);
				System.exit(0);
			}
			
			
			NUMBER_OF_NIEGHBORS = NUMBER_OF_NIEGHBORS < numNodes ? NUMBER_OF_NIEGHBORS : numNodes-1;
			float[] nodesX = new float[numNodes];
			float[] nodesY = new float[numNodes];

			distanceMatrix = new int[numNodes][numNodes];
			neighbors = new int[numNodes][NUMBER_OF_NIEGHBORS];
			
			for (int i = 0; i < numNodes; i++) {
				String line = in.readLine();
				int index = line.indexOf(" ");
				nodesX[i] = Float.parseFloat(line.substring(0, index));
				nodesY[i] = Float.parseFloat(line.substring(index + 1));
			}

			in.close();
			
			for (int i = 0; i < numNodes; i++) {
				for (int j = i+1; j < numNodes; j++) {
					distanceMatrix[i][j] = calcDistance(nodesX[i], nodesY[i], nodesX[j], nodesY[j]);
					distanceMatrix[j][i] = distanceMatrix[i][j];	
				}
			}
			
			for(int i=0;i<numNodes;i++){
				PriorityQueue<Node> q = new PriorityQueue<Node>();
				int j = 0;
				while(q.size()<NUMBER_OF_NIEGHBORS){
					if (i != j){
						q.add(new Node(distanceMatrix[i][j], j));
					}
					j++;
				}
				while(j<numNodes){
					if (i != j){
						if(q.peek().distance > distanceMatrix[i][j]){
							q.remove();
							q.add(new Node(distanceMatrix[i][j], j));
						}
					}
					j++;
				}
				for (int x=NUMBER_OF_NIEGHBORS-1; x >= 0 ; x--) {
					neighbors[i][x] = q.remove().number;
				}
			}
	
			if (visulize) {
				graph = new VisWrapper(nodesX, nodesY, distanceMatrix);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void run() {
		long START_TIME = System.currentTimeMillis();
		long FIRST_THRESHOLD = START_TIME + 800L;
		long END_TIME = FIRST_THRESHOLD + 800L;
		
		int[] bestTour = null;
		int bestCost = Integer.MAX_VALUE;
		int[] g = null;

//		for (int n = 0; n < NUMBER_OF_TRIES; n++) {
		while (System.currentTimeMillis() < FIRST_THRESHOLD) {
			// Step 1 - Initial tour
			g = nerestNeighbor();
			
			if (visulize) {
				graph.drawEdges(g, "Initial guess: NN");
			}
			
			// Step 2 - 2-opt
			g = twoOpt(g);

			
			// Step 3 - Better than before?
			int cost = calculateCostVer2(g);

			if (cost < bestCost) {
				bestTour = g;
				bestCost = cost;

				if (DEBUG) {
					System.out.println("Better!");
				}
			}
		}
		
		
		if (DEBUG)
			System.out.println(" -- STEP 4 --");
		
		// Step 4 - Random swap + 2opt
		while (System.currentTimeMillis() < END_TIME) {
			g = twoOpt(randomSwap(Arrays.copyOf(bestTour, bestTour.length)));
			int cost = calculateCostVer2(g);
			
			if (cost < bestCost) {
				bestTour = g;
				bestCost = cost;

				if (DEBUG) {
					System.out.println("Better! ");
				}
			}
			
		}
		
		if (BENCHMARK) {
			System.out.println(bestCost);
		} else {
			printTour(bestTour);
			System.err.println(bestCost);
		}
	}
	
	private int[] randomSwap(int[] g) {
		
		int x1 = RND.nextInt(g.length);
		int x2 = g[x1];
		
		int y1 = RND.nextInt(g.length);
		int y2 = g[y1];
		
		while (x2 == y1 || y2 == x1 || x1 == y1) {
			y1 = RND.nextInt(g.length);
			y2 = g[y1];
		}
		
		return stupidSwap(g, x1, y1);
	}

	private int[] nerestNeighbor() {
		int[] g = new int[numNodes];
		boolean[] used = new boolean[numNodes];
		int start = USE_RANDOM_IN_INIT ? RND.nextInt(numNodes) : 0;
		
		int i = start;
		
//		if (DEBUG)
//			System.out.println("Start node: " + i);

		used[i] = true;
		
		int best = -1;

		for (int k = 1; k < numNodes; k++) {
			best = -1;
			int bestDistance = Integer.MAX_VALUE;
			
			for (int j = 0; j < numNodes; j++) {
				if (j == i)
					continue;
				
				if (!used[j] && distanceMatrix[i][j] < bestDistance) {
					best = j;
					bestDistance = distanceMatrix[i][j];
				}
			}
			
			g[i] = best;
			used[best] = true;
			i = best;
		}
		
		g[best] = start;
		
		return g;
	}
	
	private int[] twoOpt(int[] g) {
		int numTwoOpts = 0;
		
		while (true) {
			int[] x = makeOneTwoOpt(g);
			if (x == null)
				break;
			
			g = x;
			
			if (visulize) {
				numTwoOpts++;
				graph.drawEdges(g, "Number of 2-Opts: " + numTwoOpts);
			}
		}
		
		return g;
	}
	
	private int[] makeOneTwoOpt(int[] g) {
		int p = 0;
		
		for (int k = 0; k < g.length; k++) {
			int x1 = p;
			int x2 = g[x1];

			for (int j = 0; j < NUMBER_OF_NIEGHBORS; j++) {
				int y1 = neighbors[x1][j];
				int y2 = g[y1];
				
				if (x2 == y1 || y2 == x1 || x1 == y1) {
					continue;
				}
				
				int old_cost = distanceMatrix[x1][x2] + distanceMatrix[y1][y2];
				int new_cost = distanceMatrix[x1][y1] + distanceMatrix[y2][x2];
				
				if (new_cost < old_cost) {
					return stupidSwap(g, x1, y1);
				}
			}

			p = x2;
		}

		return null;
	}
	
	public int[] stupidSwap(int[] g, int x1, int y1) {
		int[] g2 = Arrays.copyOf(g, g.length);
		int p = x1;
		g2[p] = y1;
		
		while ((p = g[p]) != y1) {
			
			g2[g[p]] = p;
			
		}
		
		g2[g[x1]] = g[y1];
				
		
		return g2;
	}
	
	private void printTour(int[] g) {
		int p = 0;
		
		for (int i = 0; i < g.length; i++) {
			System.out.println(g[p]);
			p = g[p];
		}	
		
	}
	

	private int calculateCostVer2(int[] g) {
		int cost = 0;
		int p = 0;
		
		for (int i = 0; i < g.length; i++) {
			cost += distanceMatrix[p][g[p]];
			p = g[p];
		}
		
		return cost;
	}

	private int calcDistance(float x1, float y1, float x2, float y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;

		return (int) Math.round(Math.sqrt(dx * dx + dy * dy));
	}
	
	@SuppressWarnings("unused")
	private static void printArray(int[] xs) {
		System.out.println(Arrays.toString(xs));
	}
}
