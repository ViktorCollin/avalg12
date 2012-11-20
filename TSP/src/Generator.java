import java.util.Random;


public class Generator {
	public static final int min = (int) -Math.pow(10, 6);
	public static final int max = (int) Math.pow(10, 6);
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
			System.out.println((rnd.nextFloat()*2*max-max)+" "+(rnd.nextFloat()*2*max-max));
		}
	}
	
	public void dense(int numNodes) {
		Random rnd = new Random();
		for(int i=0;i<numNodes;i++){
			System.out.println((rnd.nextGaussian()*2*max-max)+" "+(rnd.nextGaussian()*2*max-max));
		}
		
	}
	
	public void sparse(int numNodes) {
		// TODO Auto-generated method stub
		
	}
}
