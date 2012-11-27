import java.util.Arrays;

public class SwapTest {

	public static void swap(int[] tour, int x, int y) {
		if (x > y) {
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
				swapHelper(tour, 0, x - nShared, false);
			} else if (nLeft + 1 < nRight) {
				System.out.println(String.format("swap(tour, %d, %d, false);", y + nShared + 1, tour.length -1));
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
				return false;
			}
		}

		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[] xs = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 , 12, 13, 14 };
		int[] ys = Arrays.copyOf(xs, xs.length);

		swapHelper(xs, 0, 12, true);
		swapHelper(ys, 0, 12, false);

		System.out.println(Arrays.toString(ys));

		System.out.println(Arrays.toString(xs));

		System.out.println(isEqual(xs, ys));
	}

}
