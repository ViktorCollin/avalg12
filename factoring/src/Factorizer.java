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
	private static final int DEBUGLEVEL = 0;
	private static final int CERTAINTY = 20;
	private static long TIMEOUT = 14*1000;
	private static final int PRECALCULATED = 0;
	private static final int NUMBERS = 100;

	private BufferedReader in;
	private BufferedWriter out;
	private static long timeout;
	private BigInteger[] numbers;
	private BigInteger[][] factors;
	private BigInteger[][] preCalc;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length > 0 && args[0].equalsIgnoreCase("interactive")) {
			System.err.println("Running in interactive mode");
			new Factorizer().interactive();
		}else{
			timeout = System.currentTimeMillis() + TIMEOUT;
			new Factorizer().start();
		}
	}

	public Factorizer(){
		in = new BufferedReader(new InputStreamReader(System.in));
		out = new BufferedWriter(new OutputStreamWriter(System.out));
		numbers = new BigInteger[NUMBERS];
		factors = new BigInteger[NUMBERS][];
		preCalc = new BigInteger[PRECALCULATED][0];
	}
	
	private void interactive(){
		try {
			while(true){
				BigInteger[] result;
				if(POLLARDS){
					result = factorPollard(new BigInteger(in.readLine()));
				}
				if(result != null){
					for(BigInteger f : result){
						out.write(f+"\n");
					}
				} else {
					out.write("fail\n");
				}
				out.write("\n");
				out.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void start(){
		try {
			readNumbers();
			calculateSmallNumbers();
			BigInteger[] result;
			if(POLLARDS){
				for(int i = 0;i<NUMBERS;i++){
					if(System.currentTimeMillis() > timeout) break;
					if(numbers[i] == null) continue;
					result = factorPollard(numbers[i]);
					if(result == null) continue;
					factors[i] = result;
					numbers[i] = null;
				}
			}
			printFactors();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void readNumbers() throws IOException{
		for(int i = 0; i<NUMBERS;i++){
			numbers[i] = new BigInteger(in.readLine());
		}
	}
	private void printFactors() throws IOException{
		for(int i = 0; i<NUMBERS;i++){
			if(factors[i] == null){
				out.write("fail\n");
			}else{
				for(BigInteger f : factors[i]){
					out.write(f+"\n");
				}
			}
			out.write("\n");
		}
		out.flush();
	}
	
	public BigInteger[] factorPollard(BigInteger number){
		Vector<BigInteger> factors = new Vector<BigInteger>();
		Queue<BigInteger> q = new LinkedList<BigInteger>();
		q.add(number);
		BigInteger nonTrivial;
		boolean failed = false;
		while(!q.isEmpty() && !failed){
			number = q.poll();
			nonTrivial = pollardsRoh(number);
			if(nonTrivial != null){
				if(!nonTrivial.equals(BigInteger.ZERO)){
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
		return factors.toArray(new BigInteger[1]);
	}


	private BigInteger pollardsRoh(BigInteger number){
		if(number.isProbablePrime(CERTAINTY)){
			return BigInteger.ZERO;
		}else{
			if(number.and(BigInteger.ONE).equals(BigInteger.ZERO)) return new BigInteger("2");
			BigInteger x = BigInteger.ONE;
			BigInteger y = BigInteger.ONE;
			BigInteger d = BigInteger.ONE;
			while(d.equals(BigInteger.ONE)){
				x = pollardsF(x).mod(number);
				y = pollardsF(pollardsF(y).mod(number)).mod(number);
				d = x.subtract(y).gcd(number);
			}
			if(d.equals(number)){
				return null;
			}
			return d;
		}

	}

	private BigInteger pollardsF(BigInteger x) {
		return x.pow(2).add(BigInteger.ONE); 
	}
	
	private void calculateSmallNumbers(){
		BigInteger numPreCalc = new BigInteger(Long.toString(PRECALCULATED));
		for(int i = 0; i<NUMBERS;i++){
			if(numbers[i].compareTo(numPreCalc) < 1){
				factors[i] = preCalc[i];
				numbers[i] = null;
			}
		}
	}

}
