import java.util.Arrays;
import java.util.Random;

public class SwapTest {
	private static Random random = new Random();

	public static void oldSwap(int[] tour, int x, int y) {
		int tmp = 0;
		if(x > y){
			x--;
			y++;
			
			tmp = x;
			x = y;
			y = tmp;
		}
		
		while(x<y){
			tmp = tour[x];
			tour[x] = tour[y];
			tour[y] = tmp;
			x++;
			y--;
		}
		//TODO go the other way if x-y > tour.length/2 
	}
	
	public static void swap(int[] tour, int x, int y) {
		if (x > y) {
			x--;
			y++;
			
			int tmp = x;
			x = y;
			y = tmp;
		}
		swapHelper(tour, x, y, (y - x > tour.length/2));
	}
	
	public static void swapHelper(int[] tour, int x, int y, boolean crazyMode) {
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

			System.out.println("nLeft = " + nLeft);
			System.out.println("nRight = " + nRight);
			System.out.println("nShared = " + nShared);

			// delade
			for (int i = 0; i < nShared; i++) {
				System.out.println(i);
				tmp = tour[y + 1 + i];
				tour[y + 1 + i] = tour[x - 1 - i];
				tour[x - 1 - i] = tmp;
			}
			
			System.out.println("Step 1: " + Arrays.toString(tour));

			if (nLeft > nRight + 1) {
				swapHelper(tour, 0, x - nShared - 1, false);
			} else if (nLeft + 1 < nRight) {
				swapHelper(tour, y + nShared + 1, tour.length - 1, false);
			}

		}
	}
	
	public static int[] stupidSwap(int[] g, int x1, int y1) {
		int[] g2 = Arrays.copyOf(g, g.length);
		int p = x1;
		g2[p] = y1;
		
		while ((p = g[p]) != y1) {
			
			g2[g[p]] = p;
			
		}
		
		g2[g[x1]] = g[y1];
				
		
		return g2;
	}
	
	public static int[] smartSwap(int[] g, int x1, int y1){
		//System.out.print("SMART SWAP START g: ");
		//System.out.println(Arrays.toString(g));
		int p = x1;
		//System.out.println("x1 = " + x1 + ", y1 = " +y1);
		int oldP = g[p];
		g[p] = y1;
		//System.out.println("f1: g[" +p + "] = "+y1);
		p = oldP;
		oldP = g[oldP];
		g[p] = g[y1];
		//System.out.println("f2: g[" +p + "] = "+g[y1]);
		int newP = g[oldP];
		while(p != y1){
			g[oldP] = p;
		//	System.out.println("w: g[" +g[oldP] + "] = "+p);
			p = oldP;
			oldP = newP;
			newP = g[newP];
			
		}
		//g[g[x1]] = y1;
		//System.out.println("l: g[" +g[x1] + "] = "+y1);
		//System.out.print("SMART SWAP SLUT  g: ");
		//System.out.println(Arrays.toString(g));
		return g;
	}

	public static boolean isEqual(int[] xs, int[] ys) {
		int offset = Integer.MAX_VALUE;

		for (int i = 0; i < xs.length; i++) {
			if (xs[0] == ys[i]) {
				offset = i;
				break;
			}
		}

		for (int i = 0; i < xs.length; i++) {
			if (xs[i] != ys[(ys.length - i + offset) % ys.length]) {
				if (Arrays.equals(xs, ys))
					return true;
				
				return false;
			}
		}

		return true;
	}

	private static int[] randomList() {
		int length = random.nextInt(100) + 1;
		
		
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[] xs = new int[] {1, 4, 0, 2, 6, 3, 5};
		int[] ys = Arrays.copyOf(xs, xs.length);

		int x = 1;
		int y = 3;
		
		stupidSwap(xs, x, y);
		smartSwap(ys, x, y);
		
		System.out.println("STUPID: "+ Arrays.toString(xs));
		System.out.println("SMART : "+ Arrays.toString(ys));
		
		System.out.println(isEqual(xs, ys));
	}

}
