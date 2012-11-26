import java.util.Arrays;


public class SwapTest {

	public static void swap(short[] tour, short x, short y) {
		int l = Math.abs(x-y);
		short tmp = tour[x];
		tour[x] = tour[y];
		tour[y] = tmp;
		
		if(l > tour.length/2 && y > x){
			System.out.println(Arrays.toString(tour));
			y++;
			x--;
			if(x >= y) return;
			if(y >= tour.length) y = 0;
			if(x < 0) x = (short)(tour.length-1);
			swap(tour, x, y);
		} else {
			y--;
			x++;
			if(y <= x) return;
			if(x >= tour.length) x = 0;
			if(y < 0) y = (short)(tour.length-1);
			swap(tour, x, y);
		}
		
		//TODO go the other way if x-y > tour.length/2 
	}
	/**
	 * @param args
	 */
	public static void main(String[] args){
		String[] v = args[0].split(",");
		short[] tour = new short[v.length];
		for(int i=0;i<tour.length;i++){
			tour[i] = Short.parseShort(v[i]);
		}
		System.out.println(Arrays.toString(tour));
		swap(tour, Short.parseShort(args[1]),Short.parseShort(args[2]));
		System.out.println(Arrays.toString(tour));
	}

}
