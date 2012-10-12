import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;


public class GenerateTestFiles {
	private static BufferedReader primesIn;
	private static Vector<Integer> primes;
	private PrintWriter testOut;
	private PrintWriter answerOut;
	private Random rnd;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		primes = new Vector<Integer>();
		if(args.length == 2){
			primes.addAll(Arrays.asList(2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97));
		}else if(args.length == 3 && args[2].equalsIgnoreCase("primesFromFile")){
			try {
				File primesFile = new File("../helpfiles/primes");
				primesIn = new BufferedReader(new FileReader(primesFile));
				String line;
				while((line = primesIn.readLine()) != null){
					primes.add(Integer.parseInt(line));
				}
				primesIn.close();
				
			} catch (FileNotFoundException e) {
				System.err.println("You have to generate primes first\n" +
						" java preCalculate <max>\n" +
						" will generate all primes up to max");
				e.printStackTrace();
			} catch (NumberFormatException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}else{
			System.err.println("Iligal argument run with 'java GenerateTestFiles <numberOfTests> <maxNumberOfFactors>' or 'java GenerateTestFiles <numberOfTests> <maxNumberOfFactors> primesfromfile'");
			System.exit(1);
		}
		new GenerateTestFiles(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		
	}

	
	
	private GenerateTestFiles(int numberOfTestCases, int maxNumFactors){
		File testFile = new File("../helpfiles/testFile");
		File answerFile = new File("../helpfiles/answer");
		rnd = new Random();
		int numberOfOwerflows = 0;
		try {
			testOut = new PrintWriter(new BufferedWriter(new FileWriter(testFile)));
			answerOut = new PrintWriter(new BufferedWriter(new FileWriter(answerFile)));
			for(int i=1; i<=numberOfTestCases; i++){
				int numFactors = rnd.nextInt(maxNumFactors)+1;
				BigInteger composite = BigInteger.ONE;
				BigInteger[] factors = new BigInteger[numFactors];
				for(int j = 0; j<numFactors;j++){
					int index = rnd.nextInt(primes.size());
					BigInteger factor = new BigInteger(primes.get(index)+"");
					composite = composite.multiply(factor);
					factors[j] = factor;
				}
				if(composite.bitLength() > 100){
					numberOfOwerflows++;
					System.err.println("generated a to big composite, retrys");
					i--;
					continue;
				}
				Arrays.sort(factors);
				answerOut.println("number "+i);
				for(int j = 0; j<numFactors;j++){
					answerOut.println(factors[j]);
				}
				answerOut.println();
				answerOut.flush();
				testOut.println(composite);
				testOut.flush();
				
			}
			System.err.println(numberOfOwerflows + " overflows was done");
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}
