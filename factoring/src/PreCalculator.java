import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;


public class PreCalculator {
	private Vector<Vector<Integer>> factorizedNumbers;
	private PrintWriter intOut;
	private PrintWriter bigIntOut;
	private PrintWriter primesOut;
	private Vector<Integer> primes;
	private static int max;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PreCalculator calc = new PreCalculator();
		if(args.length == 0){
			max = Integer.MAX_VALUE;
		}else if(args.length == 1){
			max = Integer.parseInt(args[0]);
		}else{
			System.err.println("Iligal argument run with 'java Precalculator' or 'java Precalculator <max>'");
			System.exit(1);
		}
		calc.start();

	}

	private PreCalculator(){
		try {
			factorizedNumbers = new Vector<Vector<Integer>>();
			primes = new Vector<Integer>();
			File intFile = new File("../helpfiles/integers");
			File bigIntFile = new File("../helpfiles/bigIntegers");
			File primesFile = new File("../helpfiles/primes");
			bigIntOut = new PrintWriter(new BufferedWriter(new FileWriter(bigIntFile)));
			intOut = new PrintWriter(new BufferedWriter(new FileWriter(intFile)));
			primesOut = new PrintWriter(new BufferedWriter(new FileWriter(primesFile)));
			Vector<Integer> fact = new Vector<Integer>();
			fact.add(-1);
			factorizedNumbers.add(fact);
			printFactors(fact);
			if(primes.size() == 0){
				primes.add(2);
				printPrime(2);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void start(){
		for(int number = factorizedNumbers.size()+1; number <= max; number++){
			Vector<Integer> factors = factor(number);
			foundFactors(factors);
		}
	}

	private Vector<Integer> factor(int number){
		Vector<Integer> factors = new Vector<Integer>();
		for(int i = 0; i < primes.size();i++){
			if(number < 2) return factors;
			if(number%primes.get(i) == 0){
				factors.add(primes.get(i));
				number /= primes.get(i);
				i--;
			}
		}
		// prime was found
		factors.add(number);
		return factors;
	}

	private void foundFactors(Vector<Integer> factors){
		if(factors.size()==1){
			int prime = factors.get(0);
			printPrime(prime);
			primes.add(prime);
		}
		factorizedNumbers.add(factors);
		printFactors(factors);
		System.out.println("finished factoring "+factorizedNumbers.size()+" numbers");
	}

	private void printFactors(Vector<Integer> factors){
		StringBuilder sbInt = new StringBuilder();
		sbInt.append("{");
		for(Integer num : factors){
			sbInt.append(num+","); 
		}
		sbInt.delete(sbInt.length()-1, sbInt.length());
		sbInt.append("},");
		intOut.println(sbInt.toString());
		intOut.flush();
		printBigInts(factors);

	}

	private void printBigInts(Vector<Integer> factors){
		StringBuilder sbBigInt = new StringBuilder("{");
		for(Integer num : factors){
			sbBigInt.append("new BigInteger(\""+num+"\"),");
		}
		sbBigInt.delete(sbBigInt.length()-1, sbBigInt.length());
		sbBigInt.append("},");
		bigIntOut.println(sbBigInt.toString());
		bigIntOut.flush();
	}

	private void printPrime(int prime){
		primesOut.println(prime);
		primesOut.flush();
	}
}
