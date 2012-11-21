
public class Savings implements Comparable<Savings> {
	TspNode from;
	TspNode to; 
	int saving;
	int color;

	public Savings(TspNode from, TspNode to, int saving) {
		this.from = from;
		this.to = to;
		this.saving = saving;
	}
	
	public boolean contains(int node) {
		return from.nodeNumber == node || to.nodeNumber == node;
	}
	
	public int getVertex(int node) {
		if (from.nodeNumber == node)
			return to.nodeNumber;
		else if (to.nodeNumber == node)
			return from.nodeNumber;
		else
			return -1;
	}

	@Override
	public int compareTo(Savings o) {
		return saving - o.saving;
	}

	@Override
	public String toString() {
		return from.nodeNumber + " - " + to.nodeNumber;
	}
}
