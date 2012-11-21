import java.util.Random;


public class Generator {
	public static final int MAX = 500; // must be >0
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length == 0){
			new Generator(new String[]{"1000","random"});
		}else if(args.length == 2){
			new Generator(args);
		}else{
			System.out.println("java Generator <numNodes> <algoritm>(random, dense, sparse)");
		}

	}
	

	public Generator(String[] args) {
		int numNodes = Integer.parseInt(args[0]);
		System.out.println(numNodes);
		if(args[1].equalsIgnoreCase("random")){
			random(numNodes);
		} else if(args[1].equalsIgnoreCase("dense")){
			dense(numNodes);
		} else if(args[1].equalsIgnoreCase("sparse")){
			sparse(numNodes);
		} else {
			System.out.println("java Generator <numNodes> <algoritm>(random, dense, sparse)");
		}
	}
	
	


	public void random(int numNodes){
		Random rnd = new Random();
		for(int i=0;i<numNodes;i++){
			System.out.println((rnd.nextFloat()*MAX)+" "+(rnd.nextFloat()*MAX));
		}
	}
	
	public void dense(int numNodes) {
		Random rnd = new Random();
		int size = MAX/2;
		double[] numbers = new double[2*numNodes];
		double max = 0;
		for(int i=0;i<numbers.length;i++){
			numbers[i] = rnd.nextGaussian();
			if(Math.abs(numbers[i]) > max) max = Math.abs(numbers[i]);
		}
		for(int i=0;i<numbers.length;i+=2){
			System.out.println((size*numbers[i]/max + size)+" "+(size*numbers[i+1]/max + size));
		}
		
	}
	
	public void sparse(int numNodes) {
		// TODO Auto-generated method stub
		
	}
}
