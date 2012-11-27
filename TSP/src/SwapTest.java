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
		int[] xs = new int[] {2, 9, 3, 12, 1, 5, 13};
		int[] ys = Arrays.copyOf(xs, xs.length);

		int x = 6;
		int y = 0;
		
		oldSwap(xs, x, y);
		swapHelper(ys, x, y, true);

		System.out.println(Arrays.toString(ys));
		System.out.println(Arrays.toString(xs));
		System.out.println(isEqual(xs, ys));
	}

}
