import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.math.BigInteger;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * 
 */

/**
 * @author Viktor Collin & Anton Lindstršm
 *
 */
public class Factorizer implements Settings{
	private static long TIMEOUT = 14*1000;

	private static long timeout;

	private int NUMBERS = 100;
	private boolean RUNINFILEMODE = false;
	private Vector<Vector<BigInteger>> calculatedFactors;
	private Vector<FactoringMethod> methods;





	/**
	 * @param args
	 */
	public static void main(String[] args) {
		timeout = System.currentTimeMillis() + TIMEOUT;
		if(args.length > 0 && args[0].equalsIgnoreCase("interactive")) {
			new Factorizer().interactive();
		}else if(args.length == 1){
			new Factorizer().fileMode(args[0]);
		}else{
			new Factorizer().start(new InputStreamReader(System.in));
		}
	}

	public Factorizer(){
		calculatedFactors = new Vector<Vector<BigInteger>>(NUMBERS);
		methods = new Vector<FactoringMethod>();
		if(PRECALCULATED){
			if(DEBUGLEVEL > 0) System.err.println("Using "+Constants.PrecalculatedSize+" precalculated numbers");
		}
		if(SMALL > -1){
			if(DEBUGLEVEL > 0) System.err.println("Using the Small Numbers method");
			methods.add(SMALL,new SmallNumbers());
		}
		if(POLLARDS > -1){
			if(DEBUGLEVEL > 0) System.err.println("Using the Pollard's Rho method");
			methods.add(POLLARDS,new PollardsRho());
		}
		if(NAIVE > -1){
			if(DEBUGLEVEL > 0) System.err.println("Using the Naive method");
			//methodes.add(NAIVE,new navie());
		}

	}

	private void interactive(){
		System.err.println("Running in interactive mode");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
			while(true){
				Vector<BigInteger> factors;
				if(POLLARDS > -1){
					factors = methods.get(POLLARDS).factorize(new BigInteger(in.readLine()));
				}
				if(factors != null){
					for(BigInteger f : factors){
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
			System.exit(1);
		}
	}
	private void fileMode(String fileName) {
		System.err.println("Running in readFromFile mode");
		RUNINFILEMODE = true;
		try {
			File file = new File(fileName);
			LineNumberReader  lnr = new LineNumberReader(new FileReader(file));
			lnr.skip(Long.MAX_VALUE);
			NUMBERS = lnr.getLineNumber();
			lnr.close();
			calculatedFactors = new Vector<Vector<BigInteger>>(NUMBERS);
			System.err.println("number of numbers in "+file.getName()+" is: "+NUMBERS);
			start(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("File not found; java Factorizer <filename>");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	private void start(Reader reader){
		BigInteger[] numbers;
		try {
			numbers = readNumbers(reader);
			for(int m=0; m<methods.size(); m++){
				FactoringMethod method = methods.get(m);
				if(EVALUATED){
					if(DEBUGLEVEL > 2){
						System.err.println("###########################################################");
						System.err.println("###########################################################");
						System.err.println(" Start evaluate numbers");
						System.err.println("###########################################################");
						System.err.println("###########################################################\n");
					}
					PriorityQueue<EvaluatedNumber> q = new PriorityQueue<EvaluatedNumber>();
					for(int i = 0;i<NUMBERS;i++){
						if(calculatedFactors.get(i) != null) continue;
						q.add(new EvaluatedNumber(i,numbers[i]));
					}
					if(DEBUGLEVEL > 2){
						System.err.println("###########################################################");
						System.err.println("###########################################################");
						System.err.println(" Start factoring evaluated numbers");
						System.err.println("###########################################################");
						System.err.println("###########################################################\n");
					}
					while(!q.isEmpty()){
						if(System.currentTimeMillis() > timeout && !RUNINFILEMODE) break;
						EvaluatedNumber evalNum = q.remove();

						if(DEBUGLEVEL > 1){
							System.err.println("###########################################################");
							System.err.println(" factoring the "+evalNum.order+":th number: "+numbers[evalNum.order]+" ("+evalNum.evaluation+"bits) with the "+method.methodName());
							System.err.println("###########################################################\n");
						}
						Vector<BigInteger> factors = method.factorize(numbers[evalNum.order]);
						if(factors == null) continue;
						calculatedFactors.get(evalNum.order).addAll(factors);
					}
				}else{
					if(DEBUGLEVEL > 2){
						System.err.println("###########################################################");
						System.err.println("###########################################################");
						System.err.println(" Start factoring numbers (NOT evaluated)");
						System.err.println("###########################################################");
						System.err.println("###########################################################\n");
					}
					for(int i = 0;i<NUMBERS;i++){
						if(System.currentTimeMillis() > timeout) break;
						if(calculatedFactors.get(i) != null) continue;
						if(DEBUGLEVEL > 1){
							System.err.println("###########################################################");
							System.err.println(" factoring the "+i+":th number: "+numbers[i]+" with the "+method.methodName());
							System.err.println("###########################################################\n");
						}
						Vector<BigInteger> factors = method.factorize(numbers[i]);
						if(factors == null) continue;
						calculatedFactors.get(i).addAll(factors);
					}
				}
			}
			printFactors();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BigInteger[] readNumbers(Reader reader) throws IOException{
		if(DEBUGLEVEL > 2){
			System.err.println("###########################################################");
			System.err.println("###########################################################");
			System.err.println(" Start reading in numbers");
			System.err.println("###########################################################");
			System.err.println("###########################################################\n");
		}
		BufferedReader in = new BufferedReader(reader);
		BigInteger[] numbers = new BigInteger[NUMBERS];
		for(int i = 0; i<NUMBERS;i++){

			numbers[i] = new BigInteger(in.readLine());

		}
		in.close();

		return numbers;

	}

	private void printFactors() throws IOException{
		if(DEBUGLEVEL > 2){
			System.err.println("###########################################################");
			System.err.println("###########################################################");
			System.err.println(" Start printing factors");
			System.err.println("###########################################################");
			System.err.println("###########################################################\n");
		}
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
		for(int i = 0; i<NUMBERS;i++){
			if(RUNINFILEMODE) out.write("number "+(i+1)+"\n");
			if(calculatedFactors.get(i) == null){
				out.write("fail\n");
			}else{
				if(RUNINFILEMODE) Collections.sort(calculatedFactors.get(i));
				for(BigInteger f : calculatedFactors.get(i)){
					out.write(f+"\n");
				}
			}
			out.write("\n");
			out.flush();
		}

	}



	private class EvaluatedNumber implements Comparable<EvaluatedNumber>{
		public int order;
		public int evaluation;

		public EvaluatedNumber(int order, BigInteger number){
			this.order = order;
			this.evaluation = evaluate(number);
			if(DEBUGLEVEL > 1){
				System.err.println("###########################################################");
				System.err.println("Evaluated the "+order+":th number: "+ number +" and found that it was " + evaluation+"bits");
				System.err.println("###########################################################\n");
			}
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
