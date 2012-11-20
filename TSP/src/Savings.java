
public class Savings implements Comparable<Savings> {
	TspNode from;
	TspNode to; 
	int saving;

	public Savings(TspNode from, TspNode to, int saving) {
		this.from = from;
		this.to = to;
		this.saving = saving;
	}

	@Override
	public int compareTo(Savings o) {
		return saving - o.saving;
	}

}
