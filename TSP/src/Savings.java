
public class Savings implements Comparable<Savings> {
	short from;
	short to; 
	int saving;
	int color;

	public Savings(short from, short to, int saving) {
		this.from = from;
		this.to = to;
		this.saving = saving;
	}
	
	public boolean contains(short node) {
		return from == node || to == node;
	}
	
	public short getVertex(short node) {
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
