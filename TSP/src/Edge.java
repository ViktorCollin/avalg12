
public class Edge implements Comparable<Edge> {
	public int x1, x2, distance;
	
	public Edge a, b;
	
	public int numberOfNeighbourz() {
		int n = 0;
		
		if (a != null)
			n++;
		
		if (b != null)
			n++;
		
		return n;
	}
	
	public void addNeigbour(Edge x) {
		if (a == null)
			a = x;
		else 
			b = x;
	}
	
	public Edge(int x1, int x2, int distance) {
		super();
		this.x1 = x1;
		this.x2 = x2;
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "(" + x1 + "," + x2 + "): " + distance;
	}
	
	@Override
	public int compareTo(Edge o) {
		return distance - o.distance;
	}	
}
