import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;


public class TspMainRoundTwo {
	private static final boolean USE_RANDOM_IN_INIT = true;
	private static final boolean USE_NEIGHBORS = true;
	private static final boolean USE_RANDOM = false;
	protected static boolean DEBUG = false;
	private static int NUMBER_OF_TRIES_NN = 20;
	private static int NUMBER_OF_TRIES_RND = 200;
	private int NUMBER_OF_NIEGHBORS = 20;
	private static boolean BENCHMARK = false;
	private static final long START_TIME = System.currentTimeMillis();
	private static final long END_TIME = START_TIME + 1700L;
	private static final boolean ALL = false;
	private static final boolean GREEDY = true;
	private static final boolean STUPID_FAST = false;
	private static final boolean USE_TWO_OPT = false;
	
	
	private Edge[] edges;
	
	
	// Random things
	
	private static Random RND = new Random(2);

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
			
			int edges_x = 0;
			if (GREEDY || ALL) 
				edges = new Edge[numNodes * (numNodes-1) / 2];
			
			for (int i = 0; i < numNodes; i++) {
				for (int j = i+1; j < numNodes; j++) {
					distanceMatrix[i][j] = calcDistance(nodesX[i], nodesY[i], nodesX[j], nodesY[j]);
					distanceMatrix[j][i] = distanceMatrix[i][j];

					if (GREEDY || ALL) {
						edges[edges_x++] = new Edge(i, j, distanceMatrix[i][j]);
					}

				}
			}

			if (GREEDY || ALL)
				Arrays.sort(edges);
			if(USE_NEIGHBORS){
				if(GREEDY || ALL){
					int[] indexes = new int[numNodes];
					int x1, x2;
					for(Edge e : edges){
						x1 = e.x1;
						x2 = e.x2;
						if(indexes[x1] < NUMBER_OF_NIEGHBORS){
							neighbors[x1][indexes[x1]] = x2;
							indexes[x1]++;
						}
						if(indexes[x2] < NUMBER_OF_NIEGHBORS){
							neighbors[x2][indexes[x2]] = x1;
							indexes[x2]++;
						}
						if(allDone(indexes)) break;
					}
				}else{
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
		
		int[] bestTour = null;
		int bestCost = Integer.MAX_VALUE;
		int[] g = null;
		int tries = 0;
		
		
		if (ALL) {
			bestTour = twoOpt(greedy());
			bestCost = calculateCostVer2(bestTour);
			if(DEBUG) System.out.println("Cost for GR: "+bestCost);
			
			g = twoOpt(nerestNeighbor());
			int cost = calculateCostVer2(g);
			if(DEBUG) System.out.println("Cost for NN: "+cost);
			if (cost < bestCost) {
				bestTour = g;
				bestCost = cost;
			}
			g = twoOpt(stupidfast());
			cost = calculateCostVer2(g);
			if(DEBUG) System.out.println("Cost for SF: "+cost);
			if (cost < bestCost) {
				bestTour = g;
				bestCost = cost;
			}
		}else if(GREEDY){
			bestTour = greedy();
			if (visulize) {
				graph.drawEdges(bestTour, "Initial guess: GR");
			}
			if (USE_TWO_OPT)
				bestTour = twoOpt(bestTour);
			bestCost = calculateCostVer2(bestTour);
		} else if(STUPID_FAST){
			bestTour = stupidfast();
			if (visulize) {
				graph.drawEdges(bestTour, "Initial guess: SF");
			}
			
			if (USE_TWO_OPT)
				bestTour = twoOpt(bestTour);
			bestCost = calculateCostVer2(bestTour);
		} else {
			//while (System.currentTimeMillis() < FIRST_THRES || (visulize && NUMBER_OF_TRIES_NN > tries++)) {
				for(int i = 0; i<NUMBER_OF_TRIES_NN;i++){
			// Step 1 - Initial tour
				g = nerestNeighbor();
				
				if (visulize) {
					graph.drawEdges(g, "Initial guess: NN");
				}
		
				// Step 2 - 2-opt
				if (USE_TWO_OPT)
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
		}
		
		
		
		if (DEBUG)
			System.out.println(" -- STEP 4 --");
		
		// Step 4 - Random swap + 2opt
		if(USE_RANDOM){
			tries = 0;
			int betters = 0;
			while (System.currentTimeMillis() < END_TIME || (visulize && NUMBER_OF_TRIES_RND > tries++)) {
				g = twoOpt(randomSwap(Arrays.copyOf(bestTour, bestTour.length)));
				int cost = calculateCostVer2(g);

				if (cost < bestCost) {
					bestTour = g;
					bestCost = cost;
					betters++;

					if (DEBUG) {
						System.out.println("Better! ");
					}
				}

			}
			if(visulize){
				graph.drawEdges(bestTour, "Best so far "+betters+"/"+NUMBER_OF_TRIES_RND+ " randoms was succesful");
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
		
		return smartSwap(g, x1, y1);
	}
	
	private int[] greedy() {
		int[] visited = new int[numNodes];
		
		ArrayList<ArrayList<Integer>> components = new ArrayList<ArrayList<Integer>>(numNodes / 2);
		
		OUTER_LOOP:
		for (Edge edge : edges) {
			int x1 = edge.x1;
			int x2 = edge.x2;
			
			if (visited[x1] == 0 && visited[x2] == 0) {
				ArrayList<Integer> subtour = new ArrayList<Integer>();
				subtour.add(x1);
				subtour.add(x2);
				components.add(subtour);
				visited[x1] = 1;
				visited[x2] = 1;
			} else if (visited[x1] == 1 && visited[x2] == 0) {
				
				for (ArrayList<Integer> subtour : components) {
					int last = subtour.size() - 1;
					
					if (subtour.get(0) == x1) {
						subtour.add(0, x2);
						break;
					} else if (subtour.get(last) == x1) {
						subtour.add(x2);
						break;
					}
				}
				visited[x1]++;
				visited[x2]++;
			} else if (visited[x1] == 0 && visited[x2] == 1) {
				for (ArrayList<Integer> subtour : components) {
					int last = subtour.size() - 1;
					
					if (subtour.get(0) == x2) {
						subtour.add(0, x1);
						break;
					} else if (subtour.get(last) == x2) {
						subtour.add(x1);
						break;
					}
				}
				visited[x1]++;
				visited[x2]++;
			
			} else if (visited[x1] == 1 && visited[x2] == 1) {
				ArrayList<Integer> firstSubTour = null;
				int firstSubTourIndex = -1;
				ArrayList<Integer> secondSubTour = null;
				int secondSubTourIndex = -1;
				
				for (int i = 0; i < components.size(); i++) {
					ArrayList<Integer> subtour = components.get(i);
					int last = subtour.size() - 1;
					
					if ((subtour.get(0) == x1 && subtour.get(last) == x2) || (subtour.get(last) == x1 && subtour.get(0) == x2)) {
						continue OUTER_LOOP;
					}
					
					if (subtour.get(0) == x1 || subtour.get(last) == x1) {
						firstSubTour = subtour;
						firstSubTourIndex = i;
						
						if (subtour.get(0) == x1)
							subtour.add(0, x2);
						else
							subtour.add(x2);
						
						if (secondSubTour != null)
							break;
					} else if (subtour.get(0) == x2 || subtour.get(last) == x2) {
						secondSubTour = subtour;
						secondSubTourIndex = i;
						
						if (firstSubTour != null)
							break;
					}
				}
								
				if (firstSubTourIndex < secondSubTourIndex) {
					components.remove(secondSubTourIndex);
					components.remove(firstSubTourIndex);
				} else {
					components.remove(firstSubTourIndex);
					components.remove(secondSubTourIndex);
				}
				visited[x1]++;
				visited[x2]++;
//				System.out.println("--");
//				System.out.println(components);
//				System.out.println(firstSubTour);
//				System.out.println(secondSubTour);
				components.add(mergeSubTours(firstSubTour, secondSubTour));
				
				if (components.get(0).size() == numNodes)
					break;
			}
		}
				
		int[] g = new int[numNodes];
	
		ArrayList<Integer> tour = components.get(0);
			
		for (int i = 0; i < tour.size() - 1; i++) {	
			g[tour.get(i)] = tour.get(i+1);
		}
		
		g[tour.get(tour.size() - 1)] = tour.get(0);
		return g;
	}
	
	private ArrayList<Integer> mergeSubTours(ArrayList<Integer> xs, ArrayList<Integer> ys) {
		int xs_first = xs.get(0);
		int xs_last = xs.get(xs.size() - 1);
		int ys_first = ys.get(0);
		int ys_last = ys.get(ys.size() - 1);
		
		if (xs_first == ys_last) {
			ys.remove(ys.size() - 1);
			ys.addAll(xs);
			return ys;
		} else if (xs_last == ys_first) {
			xs.remove(xs.size() - 1);
			xs.addAll(ys);
			return xs;
		} else if (xs_first == ys_first) {
			ArrayList<Integer> zs = new ArrayList<Integer>();
			for (int i = xs.size() - 1; i > 0; i--)
				zs.add(xs.get(i));
			
			zs.addAll(ys);
			return zs;
		} else if (xs_last == ys_last) {
			for (int i = ys.size() - 2; i >= 0; i--) {
				xs.add(ys.get(i));
			}
			return xs;
		} else {
			throw new RuntimeException("FEEEL");
		}
	}
	
	private int[] stupidfast(){
		int[] g = new int[numNodes];
		int i=0;
		for(;i<(numNodes-1)%5;){
			g[i] = ++i;
		}
		for(;i<numNodes-1;){
			g[i] = ++i;
			g[i] = ++i;
			g[i] = ++i;
			g[i] = ++i;
			g[i] = ++i;
		}
		//g[numNodes-1] = 0; 'r redan noll vid initsiering
		return g;
	}

	private int[] nerestNeighbor() {
		int[] g = new int[numNodes];
		boolean[] used = new boolean[numNodes];
		int start = USE_RANDOM_IN_INIT ? RND.nextInt(numNodes) : 0;
		
		int i = start;
		used[i] = true;
		
		int best = -1;

		for (int k = 1; k < numNodes; k++) {
			best = -1;
			int bestDistance = Integer.MAX_VALUE;
			
			// Check in the neighbour list first:
			for (int n = 0; n < NUMBER_OF_NIEGHBORS; n++) {
				int j = neighbors[i][n];
				
				if (i == j)
					continue;
				
				if (!used[j] && distanceMatrix[i][j] < bestDistance) {
					best = j;
					bestDistance = distanceMatrix[i][j];
				}
			}
			
			if (best == -1) {
				for (int j = 0; j < numNodes; j++) {
					if (j == i)
						continue;
					
					if (!used[j] && distanceMatrix[i][j] < bestDistance) {
						best = j;
						bestDistance = distanceMatrix[i][j];
					}
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
			if(USE_NEIGHBORS){
				for (int j = 0; j < NUMBER_OF_NIEGHBORS; j++) {
					int y1 = neighbors[x1][j];
					int y2 = g[y1];

					if (x2 == y1 || y2 == x1 || x1 == y1) {
						continue;
					}

					int old_cost = distanceMatrix[x1][x2] + distanceMatrix[y1][y2];
					int new_cost = distanceMatrix[x1][y1] + distanceMatrix[y2][x2];

					if (new_cost < old_cost) {
						return smartSwap(g, x1, y1);
					}
				}
			} else{
				for(int j=0;j<numNodes;j++){
					int y1 = g[j];
					int y2 = g[y1];
					if (x2 == y1 || y2 == x1 || x1 == y1) {
						continue;
					}

					int old_cost = distanceMatrix[x1][x2] + distanceMatrix[y1][y2];
					int new_cost = distanceMatrix[x1][y1] + distanceMatrix[y2][x2];

					if (new_cost < old_cost) {
						return smartSwap(g, x1, y1);
					}
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
	public int[] smartSwap(int[] g, int x1, int y1){
		int p = x1;
		int oldP = g[p];
		g[p] = y1;
		p = oldP;
		oldP = g[oldP];
		g[p] = g[y1];
		int newP = g[oldP];
		while(p != y1){
			g[oldP] = p;
			p = oldP;
			oldP = newP;
			newP = g[newP];
			
		}
		return g;
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
	
	private boolean allDone(int[] indexes) {
		for(int i : indexes){
			if(i != NUMBER_OF_NIEGHBORS) return false;
		}
		return true;
	}
	
	@SuppressWarnings("unused")
	private static void printArray(int[] xs) {
		System.out.println(Arrays.toString(xs));
	}
}
