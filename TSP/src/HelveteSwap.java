import java.util.Arrays;


public class HelveteSwap {
	
	private static void printArray(int[] xs) {
		System.out.println(Arrays.toString(xs));
	}
	
	private static void validIndexes(int tour[], int indexes[]) {
		for (int i = 0; i < tour.length; i++) {
			if (indexes[tour[i]] != i) {
				throw new RuntimeException(""+i);
			}
		}
	}
	
	private static void oldSwap(int[] tour, int[] indexes, int x, int y) {
		System.out.println("SWAP");
		printArray(tour);
		
		System.out.println("x = " + x + ", y = " + y);
		
		
		int tmp = 0;
		if (x > y) {
			x--;
			y++;

			tmp = x;
			x = y;
			y = tmp;
		}

		while (x < y) {
			tmp = indexes[tour[x]];
			indexes[tour[x]] = indexes[tour[y]];
			indexes[tour[y]] = tmp;
			tmp = tour[x];
			tour[x] = tour[y];
			tour[y] = tmp;
			printArray(tour);
			
			System.out.println("x = " + x + ", y = " + y);
			x++;
			y--;
		}
		// TODO go the other way if x-y > tour.length/2
	}
	
	public static int[] generateIndexes(int tour[]){
		int[] indexes = new int[tour.length];
		
		for (int i = 0; i < tour.length; i++)
			indexes[tour[i]] = i;
		
		return indexes;
	}
	
	public static void main(String[] args) {

		int[] tour = new int[]{2, 9, 0, 4, 1, 6, 7, 3, 8, 5};
		int[] indexes = new int[]{2, 4, 0, 7, 3, 9, 5, 6, 8, 1};
		validIndexes(tour, indexes);
		
		int i = 0;
		int tmp = 9;
		
		oldSwap(tour, indexes, i+1, tmp);
		//oldSwap(indexes, tour[i+1], tour[tmp]);
		printArray(indexes);
		printArray(generateIndexes(tour));		
		validIndexes(tour, indexes);

		
	}

}
