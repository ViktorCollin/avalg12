
public class Savings implements Comparable<Savings> {
	int from;
	int to; 
	int saving;
	int color;

	public Savings(int from, int to, int saving) {
		this.from = from;
		this.to = to;
		this.saving = saving;
	}
	
	public boolean contains(int node) {
		return from == node || to == node;
	}
	
	public int getVertex(int node) {
		if (from == node)
			return to;
		else if (to == node)
			return from;
		else
			return -1;
	}

	@Override
	public int compareTo(Savings o) {
		return saving - o.saving;
	}

	@Override
	public String toString() {
		return from + " - " + to;
	}
}
