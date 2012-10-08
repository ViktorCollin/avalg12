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

	private BufferedReader in;
	private BufferedWriter out;
	private Vector<BigInteger> factors;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Factorizer f = new Factorizer();
		f.start();

	}

	private Factorizer(){
		in = new BufferedReader(new InputStreamReader(System.in));
		out = new BufferedWriter(new OutputStreamWriter(System.out));
	}

	private void start(){
		BigInteger number;

		try {
			while((number = new BigInteger(in.readLine()))!= null){
				factors = new Vector<BigInteger>();
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
							}
						}else{
							if(DEBUGLEVEL >= 1){
								System.err.println("###########################################################");
								System.err.println("NO non-trivial factor was found of " + number);
								System.err.println("###########################################################\n");
							}
							failed = true;
							out.write("fail\n");
						}

					}
					if(!failed){
						for(BigInteger factor : factors){
							out.write(factor.toString() + "\n");
						}
					}
					out.write('\n');
					out.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private BigInteger pollardsRoh(BigInteger number){
		if(number.isProbablePrime(CERTAINTY)){
			if(DEBUGLEVEL >= 1){
				System.err.println("###########################################################");
				System.err.println(number + " Was found to be a PRIME!");
				System.err.println("###########################################################\n");
			}
			factors.add(number);
			return BigInteger.ZERO;
		}else{
			BigInteger x = new BigInteger("2");
			BigInteger y = new BigInteger("2");
			BigInteger d = BigInteger.ONE;
			while(d.compareTo(BigInteger.ONE) == 0){
				x = pollardsF(x).mod(number);
				y = pollardsF(pollardsF(y).mod(number)).mod(number);
				d = euclidsGCD(x.subtract(y).abs(), number);
			}
			if(d.compareTo(number) == 0){
				return null;
			}
			return d;
		}

	}

	private BigInteger euclidsGCD(BigInteger a, BigInteger b){
		if(b.compareTo(BigInteger.ZERO) == 0){
			return a;
		}
		return euclidsGCD(b, a.subtract(b.multiply(a.divide(b))));
	}

	private BigInteger pollardsF(BigInteger x) {
		return x.pow(2).add(BigInteger.ONE); 
	}


}
