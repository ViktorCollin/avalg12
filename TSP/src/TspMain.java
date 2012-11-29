import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;


public class TspMain {
	private static final boolean USE_NIEGHBOR = true;
	private static final boolean USE_RANDOM_IN_INIT = false;
	private static final boolean SIM_ANN = false;
	private static final boolean PRINT_COST = false;
	protected static boolean DEBUG = false;
	private static final int NUMBER_OF_TRIES = 1;
	private static final int RANDOM_MIN_DISTANCE = 10;
	private static final float P = 0.2F;
	private int NUMBER_OF_NIEGHBORS = 5;
 
	
	


	// Random things
	private boolean USE_RANDOM = false;
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
//		try {
//			Thread.sleep(5000L);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		new TspMain(args.length != 0).run();
	}

	public TspMain(boolean visulize) {
		this.visulize = visulize;

		if (visulize)
			DEBUG = true;


		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			numNodes = Integer.parseInt(in.readLine());
			NUMBER_OF_NIEGHBORS = NUMBER_OF_NIEGHBORS <= numNodes ? NUMBER_OF_NIEGHBORS : numNodes;
			float[] nodesX = new float[numNodes];
			float[] nodesY = new float[numNodes];

			distanceMatrix = new int[numNodes][numNodes];
			if(USE_NIEGHBOR){
				indexes = new int[numNodes];
				neighbors = new int[numNodes][NUMBER_OF_NIEGHBORS];
			}
			for (int i = 0; i < numNodes; i++) {
				String line = in.readLine();
				int index = line.indexOf(" ");
				nodesX[i] = Float.parseFloat(line.substring(0, index));
				nodesY[i] = Float.parseFloat(line.substring(index + 1));
			}
			
			in.close();
			
			// TODO: Optimera detta :-)
			
			for (int i = 0; i < numNodes; i++) {
				PriorityQueue<Node> q = new PriorityQueue<Node>();
				
				for (int j = 0; j < numNodes; j++) {
					distanceMatrix[i][j] = calcDistance(nodesX[i], nodesY[i], nodesX[j], nodesY[j]);
					distanceMatrix[j][i] = distanceMatrix[i][j];
					
					if (i != j)
						q.add(new Node(distanceMatrix[i][j], j));
				}
				
				for (int j = 0; j < NUMBER_OF_NIEGHBORS; j++) {
					neighbors[i][j] = q.remove().number;
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

	private void run() {
		int[] bestTour = null;
		int bestCost = Integer.MAX_VALUE;

		for (int n = 0; n < NUMBER_OF_TRIES; n++) {
			// Step 1 - Initial tour
			int[] tour = nerestNeighbor();
			
			if (visulize) {
				graph.drawEdges(tour, "Initial guess: NN");
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
		
		
		if (SIM_ANN) {
		
			USE_RANDOM = true;
			
			int[] old_indexes = null;
			
			for (int i = 0; i < 10; i++) {
				int[] tour_copy = Arrays.copyOf(bestTour, bestTour.length);
				old_indexes = Arrays.copyOf(indexes, indexes.length);
				twoOpt(tour_copy);
				int copyCost = calculateCost(tour_copy);
				
				if (copyCost < bestCost) {
					bestTour = tour_copy;
					bestCost = copyCost;
					//old_indexes = Arrays.copyOf(indexes, indexes.length);
				} else {
					indexes = old_indexes;
				}
			}
			
		}

		// Step 4 - Print the best tour
		if (!DEBUG)
			for (int x : bestTour)
				System.out.println(x);
		
		if (PRINT_COST)
			System.out.println(bestCost);
	}

	private int[] nerestNeighbor() {
		int[] tour = new int[numNodes];
		boolean[] used = new boolean[numNodes];
		int start = USE_RANDOM_IN_INIT ? RND.nextInt(numNodes) : 0;

		if (DEBUG)
			System.out.println("Start node: " + start);

		tour[0] = start;
		if(USE_NIEGHBOR){
			indexes[start] = 0;
		}
		used[start] = true;
		int i = start;
		for (int k = 1; k < numNodes; k++) {
			if(USE_NIEGHBOR){
				for(int j=0;j<NUMBER_OF_NIEGHBORS;j++){
					int best = neighbors[i][j];
					if(!used[best]){
						tour[k] = best;
						indexes[best] = k;
						used[best] = true;
						k++;
						i = best;
						if(DEBUG){
							System.out.println("Found in neighbors");
						}
					}
				}
				if(k == numNodes) break;
			}
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
			if(USE_NIEGHBOR){
				indexes[best] = k;
			}
			if(DEBUG){
				System.out.println("NOT found in neighbors");
			}
			used[best] = true;
			i = best;
		}

		return tour;
	}

	private void twoOpt(int[] tour) {
		int numTwoOpts = 0;
		int start = 0;
		
		for (int x = 0; x < 2; x++ ) {
		while (start != -1) {
				//&& (System.currentTimeMillis() < breakTime || visulize)) {
			if(USE_NIEGHBOR){
				start = makeOneTwoOptNeighbor(tour, 0);
			}else {
				start = makeOneTwoOpt(tour, 0);
			}
			if (visulize) {
				numTwoOpts++;
				graph.drawEdges(tour, "Number of 2-Opts: " + numTwoOpts);
			}
		}
		}
	}

	private int makeOneTwoOpt(int[] tour, int start) {
		for (int i = start; i < numNodes - 1; i++) {
			int x1 = tour[i];	
			int x2 = tour[i + 1];
		
			int visited = 0;


//			int j = 0;

//			while (visited <= numNodes - 3) { // Testa alla kanter - 3
			for (int j = 0; j < numNodes ; j++) {
				if(Math.abs(i-j) <= 1) continue;
			
//				if (j == numNodes) {
//					j = 0;
//				}

//				System.out.println("(" + i + ", " + j + ")");
				
				int y1 = tour[j];
				int y2 = j + 1 == numNodes ? tour[0] : tour[j + 1];

				visited++;

				int old_cost = distanceMatrix[x1][x2] + distanceMatrix[y1][y2];
				int new_cost = distanceMatrix[x1][y1] + distanceMatrix[y2][x2];

				if (new_cost < old_cost) {					
					oldSwap(tour, (i + 1), j);
					return i;
				} else if (USE_RANDOM && new_cost - old_cost < RANDOM_MIN_DISTANCE && RND.nextFloat() < P){
					oldSwap(tour, (i+1), j);
					return i; 
				}
				 
//				j++;
			}
		}

		return -1;
	}
	
	private int makeOneTwoOptNeighbor(int[] tour, int start){
		for (int i = start; i < numNodes - 1; i++) {
			int x1 = tour[i];	
			int x2 = tour[i + 1];
						
			for (int j = 0; j < NUMBER_OF_NIEGHBORS; j++) {				
				int tmp = indexes[neighbors[i][j]];
				
				if(Math.abs(i-tmp) <= 1) continue;


				int y1 = tour[tmp];
				int y2 = tmp + 1 == numNodes ? tour[0] : tour[tmp + 1];


				int old_cost = distanceMatrix[x1][x2] + distanceMatrix[y1][y2];
				int new_cost = distanceMatrix[x1][y1] + distanceMatrix[y2][x2];

				if (new_cost < old_cost) {					
					oldSwap(tour, (i + 1), tmp);
					return i;
				} else if (USE_RANDOM && new_cost - old_cost < RANDOM_MIN_DISTANCE && RND.nextFloat() < P){
					oldSwap(tour, (i+1), tmp);
					return i; 
				}
			}
		}
		return -1;
	}

	private void oldSwap(int[] tour, int x, int y) {
//		printArray(tour);
//		printArray(indexes);
//		System.out.println("x = " + x +", y = " + y);
		
		int tmp = 0;
		if (x > y) {
			x--;
			y++;

			tmp = x;
			x = y;
			y = tmp;
		}

		while (x < y) {
			if(USE_NIEGHBOR){
				tmp = indexes[tour[x]];
				indexes[tour[x]] = indexes[tour[y]];
				indexes[tour[y]] = tmp;
			}
			tmp = tour[x];
			tour[x] = tour[y];
			tour[y] = tmp;
			x++;
			y--;
		}
		// TODO go the other way if x-y > tour.length/2
	}
	
	private int calculateCost(int[] tour) {
		int cost = 0;

		for (int i = 1; i < tour.length; i++) {
			cost += distanceMatrix[tour[i - 1]][tour[i]];
		}

		cost += distanceMatrix[tour[0]][tour[tour.length - 1]];

		return cost;
	}

	protected int calcDistance(float x1, float y1, float x2, float y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;

		return (int) Math.round(Math.sqrt(dx * dx + dy * dy));
	}
	
	private void validIndexes(int tour[]) {
		for (int i = 0; i < numNodes; i++) {
			if (indexes[tour[i]] != i) {
				
				printArray(indexes);
				printArray(tour);
				
				throw new RuntimeException(""+i);
			}
		}
	}
	
	private static void printArray(int[] xs) {
		System.out.println(Arrays.toString(xs));
	}
}
