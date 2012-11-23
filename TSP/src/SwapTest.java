import java.util.Arrays;


public class SwapTest {

	public static void swap(int[] tour, int x, int y) {
		int tmp = 0;
		if(x > y){
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
	/**
	 * @param args
	 */
	public static void main(String[] args){
		String[] v = args[0].split(",");
		int[] tour = new int[v.length];
		for(int i=0;i<tour.length;i++){
			tour[i] = Integer.parseInt(v[i]);
		}
		System.out.println(Arrays.toString(tour));
		swap(tour, Integer.parseInt(args[1]),Integer.parseInt(args[2]));
		System.out.println(Arrays.toString(tour));
	}

}
