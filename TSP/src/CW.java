import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class CW extends TspMain {
	private float maxX = Float.MIN_VALUE;
	private float minX = Float.MAX_VALUE;
	private float maxY = Float.MIN_VALUE;
	private float minY = Float.MAX_VALUE;


	public CW(boolean visulize) {
		super(visulize);
		// TODO Auto-generated constructor stub
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
			for (short j = (short) (i + 1); j < numNodes; j++) {
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

					if (findGraphLoop(from, to, edges, ++color)
							|| findGraphLoop(to, from, edges, ++color)) {
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
		float middleX = (maxX - minX) / 2;
		float middleY = (maxY - minY) / 2;
		for (short i = 0; i < numNodes; i++) {
			int newDist = calcDistance(middleX, middleY, nodesX[i], nodesY[i]);
			if (maxDistance > newDist) {
				hub = i;
				maxDistance = newDist;
			}
		}

		return hub;
	}


}
