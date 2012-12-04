

public class VisWrapper extends newVisulizer {

	public VisWrapper(float[] nodesX, float[] nodesY, int[][] distanceMatrix) {
		super(nodesX, nodesY, distanceMatrix);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void drawEdges(int[] g, String str) {		
		int[] tour = new int[g.length];
		
		int p = 0;
		
		for (int i = 0; i < g.length; i++) {
			tour[i] = g[p];
			p = g[p];
		}
		
		super.drawEdges(tour, str);
	}
}
