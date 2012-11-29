
public class Node implements Comparable<Node>{
	public int distance;
	public int number;
	
	
	
	public Node(int distance, int number) {
		this.distance = distance;
		this.number = number;
	}



	@Override
	public int compareTo(Node o) {
		return distance - o.distance;
	}
}
