import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;

public class TspMain {
	protected static boolean DEBUG = false;
//	public static final int RUNTIME = 1990;
//	public static final long breakTime = System.currentTimeMillis() + RUNTIME;
//	public static final boolean CW = false;
//	public static final boolean NN = true;
	private static final int NUMBER_OF_TRIES = 10;
	private int NUMBER_OF_NIEGHBORS = 10;
	
	private static final boolean PRINT_COST = false;

	// Random things
	private boolean USE_RANDOM = false;
	private static final int RANDOM_MIN_DISTANCE = 10;
	private static final float P = 0.2F;
	
	private static Random RND = new Random();

	int[][] distanceMatrix;
	int[][] neighbors;
	int numNodes;
	newVisulizer graph;
	boolean visulize = false;
	// if CW
	float[] nodesX;
	float[] nodesY;


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
				for (int j = i + 1; j < numNodes; j++) {
					distanceMatrix[i][j] = calcDistance(nodesX[i], nodesY[i], nodesX[j], nodesY[j]);
					distanceMatrix[j][i] = distanceMatrix[i][j];
					
				}
				int[] tmp = Arrays.copyOf(distanceMatrix[i],distanceMatrix[i].length);
				Arrays.sort(tmp);
				int max = tmp[NUMBER_OF_NIEGHBORS];
				int index = 0;
				for(int j=0;j<numNodes;j++){
					if(i==j) continue;
					if(distanceMatrix[i][j] <= max){
						neighbors[i][index] = j;
						if(++index == NUMBER_OF_NIEGHBORS) break;
						
					}
				}
				if(DEBUG){
					System.out.println(i +": "+Arrays.toString(neighbors[i]));
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
		
		USE_RANDOM = true;
		
		for (int i = 0; i < 10; i++) {
			int[] tour_copy = Arrays.copyOf(bestTour, bestTour.length);
			twoOpt(tour_copy);
			int copyCost = calculateCost(tour_copy);
			if (copyCost < bestCost) {
				bestTour = tour_copy;
				bestCost = copyCost;
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

				if (!used[j]
						&& (best == -1 || distanceMatrix[i][j] < bestDistance)) {
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

	private void twoOpt(int[] tour) {
		int numTwoOpts = 0;
		int start = 0;

		while (start != -1) {
				//&& (System.currentTimeMillis() < breakTime || visulize)) {
			start = makeOneTwoOpt(tour, start);

			if (visulize) {
				numTwoOpts++;
				graph.drawEdges(tour, "Number of 2-Opts: " + numTwoOpts);
			}
		}
	}

	private int makeOneTwoOpt(int[] tour, int start) {
		for (int i = start; i < numNodes - 1; i++) {
			int x1 = tour[i];	
			int x2 = tour[i + 1];
		
			int visited = 0;

			int j = i + 2;

			while (visited <= numNodes - 3) { // Testa alla kanter - 3

				if (j == numNodes) {
					j = 0;
				}

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
				 

				j++;
			}
		}

		return -1;
	}

	private static void oldSwap(int[] tour, int x, int y) {
		int tmp = 0;
		if (x > y) {
			x--;
			y++;

			tmp = x;
			x = y;
			y = tmp;
		}

		while (x < y) {
			tmp = tour[x];
			tour[x] = tour[y];
			tour[y] = tmp;
			x++;
			y--;
		}
		// TODO go the other way if x-y > tour.length/2
	}

	private static void swap(int[] tour, int x, int y) {
		if (x > y) {
			x--;
			y++;
			
			int tmp = x;
			x = y;
			y = tmp;
		}
		
		swapHelper(tour, x, y, (y - x > tour.length/2));
	}
	
	private static void swapHelper(int[] tour, int x, int y, boolean crazyMode) {
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
				tmp = tour[y + 1 + i];
				tour[y + 1 + i] = tour[x - 1 - i];
				tour[x - 1 - i] = tmp;
			}

			if (nLeft > nRight + 1) {
				swapHelper(tour, 0, x - nShared - 1, false);
			} else if (nLeft + 1 < nRight) {
				swapHelper(tour, y + nShared + 1, tour.length - 1, false);
			}
		}
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
}
