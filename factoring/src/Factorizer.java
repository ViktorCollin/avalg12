import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

/**
 * 
 */

/**
 * @author Viktor Collin & Anton Lindstršm
 *
 */
public class Factorizer {
	private static final boolean POLLARDS = true;
	private static final int DEBUGLEVEL = 1;
	private static final int CERTAINTY = 20;
	private long TIMEOUT = 14*1000;

	private BufferedReader in;
	private BufferedWriter out;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Factorizer f = new Factorizer();
		if(args.length > 0 && args[0].equalsIgnoreCase("notimeout")) {
			System.err.println("Running in to the infinity");
			f.start(false);
		}else{
			f.start(true);
		}

	}

	public Factorizer(){
		in = new BufferedReader(new InputStreamReader(System.in));
		out = new BufferedWriter(new OutputStreamWriter(System.out));
	}

	private void start(boolean timeOut){
		long timeout;
		timeout = System.currentTimeMillis() + TIMEOUT;
		if(!timeOut) timeout = Long.MAX_VALUE;
		String number;
		Vector<BigInteger> factors = null;
		int remaining = 100;
		try {
			while((number = in.readLine())!= null && System.currentTimeMillis() < timeout){
				factors = factor(number);
				if(factors == null){
					out.write("fail\n");
				}else{
					for(BigInteger factor : factors){
						out.write(factor + "\n");
					}
				}
				out.write('\n');
				out.flush();
				remaining--;
			}
			for(;remaining >= 0; remaining--){
				out.write("fail\n\n");
				out.flush();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Vector<BigInteger> factor(String num){
		BigInteger number = new BigInteger(num); 
		Vector<BigInteger> factors = new Vector<BigInteger>();
		if(POLLARDS){
			Queue<BigInteger> q = new LinkedList<BigInteger>();
			q.add(number);
			BigInteger nonTrivial;
			boolean failed = false;
			while(!q.isEmpty() && !failed){
				number = q.poll();
				nonTrivial = pollardsRoh(number);
				if(nonTrivial != null){
					if(nonTrivial.compareTo(BigInteger.ZERO) != 0){
						if(DEBUGLEVEL >= 1){
							System.err.println("###########################################################");
							System.err.println(nonTrivial + " Was found to be a non trivial factor of " + number);
							System.err.println("###########################################################\n");
						}
						q.add(nonTrivial);
						q.add(number.divide(nonTrivial));
					} else {
						if(DEBUGLEVEL >= 1){
							System.err.println("###########################################################");
							System.err.println(number + " Was found to be a PRIME!");
							System.err.println("###########################################################\n");
						}
						factors.add(number);
					}
				}else{
					if(DEBUGLEVEL >= 1){
						System.err.println("###########################################################");
						System.err.println("NO non-trivial factor was found of " + number);
						System.err.println("###########################################################\n");
					}
					failed = true;
					return null;
				}
			}
		}
		return factors;
	}


	private BigInteger pollardsRoh(BigInteger number){
		if(number.isProbablePrime(CERTAINTY)){
			return BigInteger.ZERO;
		}else{
			if(number.and(BigInteger.ONE).compareTo(BigInteger.ONE) != 0) return new BigInteger("2");
			BigInteger x = new BigInteger("2");
			BigInteger y = new BigInteger("2");
			BigInteger d = BigInteger.ONE;
			while(d.compareTo(BigInteger.ONE) == 0){
				x = pollardsF(x).mod(number);
				y = pollardsF(pollardsF(y).mod(number)).mod(number);
				d = x.subtract(y).gcd(number);
			}
			if(d.compareTo(number) == 0){
				return null;
			}
			return d;
		}

	}

	private BigInteger pollardsF(BigInteger x) {
		return x.pow(2).add(BigInteger.ONE); 
	}


}
