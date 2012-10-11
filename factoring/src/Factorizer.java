import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
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
	private static final int DEBUGLEVEL = 1;
	private static final int CERTAINTY = 20;
	private static long TIMEOUT = 14*1000;
	private int NUMBERS = 100;
	private static final boolean PRECALCULATED = true;
	private static final boolean CALCULATESMALL = true;
	private static final boolean EVALUATED = true;
	private static final boolean POLLARDS = true;

	private BufferedReader in;
	private BufferedWriter out;
	private static long timeout;
	private BigInteger[][] factors;
	private int[][] preCalc;
	private BigInteger numPreCalc;
	private boolean runInFileMode = false;
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		timeout = System.currentTimeMillis() + TIMEOUT;
		if(args.length > 0 && args[0].equalsIgnoreCase("interactive")) {
			System.err.println("Running in interactive mode");
			new Factorizer().interactive();
		}else if(args.length == 1){
			System.err.println("Running in readFromFile mode");
			new Factorizer().fileMode(args[0]);
		}else{
			new Factorizer().start();
		}
	}

	public Factorizer(){
		in = new BufferedReader(new InputStreamReader(System.in));
		out = new BufferedWriter(new OutputStreamWriter(System.out));
		factors = new BigInteger[NUMBERS][];
		preCalc = new int[0][0];
		numPreCalc = BigInteger.ZERO;
		if(PRECALCULATED){
			preCalc = Constants.getPrecalculatedFactors();
			numPreCalc = new BigInteger(preCalc.length-1+"");
			if(DEBUGLEVEL > 0) System.err.println("Using "+numPreCalc+" precalculated numbers");
		}
		
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
			e.printStackTrace();
		}
	}
	private void fileMode(String fileName) {
		runInFileMode = true;
		try {
			File file = new File(fileName);
			LineNumberReader  lnr = new LineNumberReader(new FileReader(file));
			lnr.skip(Long.MAX_VALUE);
			NUMBERS = lnr.getLineNumber();
			lnr.close();
			factors = new BigInteger[NUMBERS][];
			System.err.println("number of numbers in "+file.getName()+" is: "+NUMBERS);
			in = new BufferedReader(new FileReader(fileName));
			
		} catch (FileNotFoundException e) {
			System.err.println("File not found; java Factorizer <filename>");
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		start();
	}

	private void start(){
		try {
			BigInteger[] numbers = readNumbers();
			in.close();
			if(CALCULATESMALL){
				calculateSmallNumbers(numbers);
			}
			if(EVALUATED){
				PriorityQueue<EvaluatedNumber> q = new PriorityQueue<EvaluatedNumber>();
				for(int i = 0;i<NUMBERS;i++){
					if(numbers[i] == null) continue;
					q.add(new EvaluatedNumber(i,numbers[i]));
				}
				while(!q.isEmpty()){
					if(System.currentTimeMillis() > timeout && !runInFileMode) break;
					EvaluatedNumber evalNum = q.remove();
					if(POLLARDS){
						factors[evalNum.order] = factorPollard(evalNum.number);
						if(factors[evalNum.order] == null) continue;
						numbers[evalNum.order] = null;
					}
				}
			}else{
				for(int i = 0;i<NUMBERS;i++){
					if(System.currentTimeMillis() > timeout) break;
					if(numbers[i] == null) continue;
					if(POLLARDS){
						factors[i] = factorPollard(numbers[i]);
						if(factors[i] == null) continue;
						numbers[i] = null;
					}
				}
			}
			printFactors();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private BigInteger[] readNumbers() throws IOException{
		BigInteger[] numbers = new BigInteger[NUMBERS];
		for(int i = 0; i<NUMBERS;i++){
			numbers[i] = new BigInteger(in.readLine());
		}
		return numbers;
	}
	
	private void printFactors() throws IOException{
		for(int i = 0; i<NUMBERS;i++){
			if(factors[i] == null){
				out.write("fail\n");
			}else{
				if(runInFileMode) Arrays.sort(factors[i]);
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
		if(number.compareTo(numPreCalc) < 1){
			for(int factor : preCalc[number.intValue()]){
				factors.add(new BigInteger(factor+""));
			}
			return factors.toArray(new BigInteger[0]);
		}
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
		return factors.toArray(new BigInteger[0]);
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
	
	private void calculateSmallNumbers(BigInteger[] numbers){
		for(int i = 0; i<NUMBERS;i++){
			if(numbers[i].compareTo(numPreCalc) < 1){
				int number = numbers[i].intValue();
				factors[i] = new BigInteger[preCalc[number].length];
				for(int j=0;j<preCalc[number].length;j++){
					factors[i][j] = new BigInteger(preCalc[number][j]+"");
				}
				numbers[i] = null;
			}
		}
	}

	private class EvaluatedNumber implements Comparable<EvaluatedNumber>{
		public int order;
		public BigInteger number;
		public int evaluation;
		
		public EvaluatedNumber(int order, BigInteger number){
			this.order = order;
			this.number = number;
			this.evaluation = evaluate(number);
		}
		
		private int evaluate(BigInteger number){
			return number.bitLength();
		}

		@Override
		public int compareTo(EvaluatedNumber o) {
			return evaluation - o.evaluation;
		}
		
	}
}
