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
 * @author Viktor Collin & Anton Lindstr�m
 *
 */
public class Factorizer {
	private static final int DEBUGLEVEL = 0;
	private static final int CERTAINTY = 20;
	private static long TIMEOUT = 14*1000;
	private static final int NUMBERS = 100;
	private static final boolean PRECALCULATED = true;
	private static final boolean CALCULATESMALL = true;
	private static final boolean EVALUATED = false;
	private static final boolean POLLARDS = true;

	private BufferedReader in;
	private BufferedWriter out;
	private static long timeout;
	
	private BigInteger[][] factors;
	private int[][] preCalc;
	private BigInteger numPreCalc;
	
	

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
		factors = new BigInteger[NUMBERS][];
		preCalc = new int[0][0];
		numPreCalc = BigInteger.ZERO;
		if(PRECALCULATED){
			numPreCalc = initPreCalc();
			if(DEBUGLEVEL > 0) System.out.println("Using "+numPreCalc+" precalculated numbers");
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

	private void start(){
		try {
			BigInteger[] numbers = readNumbers();
			in.close();
			if(CALCULATESMALL){
				calculateSmallNumbers(numbers);
			}
			if(EVALUATED){

			}else{
				if(POLLARDS){
					for(int i = 0;i<NUMBERS;i++){
						if(System.currentTimeMillis() > timeout) break;
						if(numbers[i] == null) continue;
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
				factors[i] = new BigInteger[preCalc[i].length];
				for(int j=0;j<preCalc[i].length;j++){
					factors[i][j] = new BigInteger(preCalc[i][j]+"");
				}
				numbers[i] = null;
			}
		}
	}
	
	private BigInteger initPreCalc(){
		preCalc = new int[][]{
				{-1},
				//Paste generated precalc here
				{-1},
				{2},
				{3},
				{2,2},
				{5},
				{2,3},
				{7},
				{2,2,2},
				{3,3},
				{2,5},
				{11},
				{2,2,3},
				{13},
				{2,7},
				{3,5},
				{2,2,2,2},
				{17},
				{2,3,3},
				{19},
				{2,2,5},
				{3,7},
				{2,11},
				{23},
				{2,2,2,3},
				{5,5},
				{2,13},
				{3,3,3},
				{2,2,7},
				{29},
				{2,3,5},
				{31},
				{2,2,2,2,2},
				{3,11},
				{2,17},
				{5,7},
				{2,2,3,3},
				{37},
				{2,19},
				{3,13},
				{2,2,2,5},
				{41},
				{2,3,7},
				{43},
				{2,2,11},
				{3,3,5},
				{2,23},
				{47},
				{2,2,2,2,3},
				{7,7},
				{2,5,5},
				{3,17},
				{2,2,13},
				{53},
				{2,3,3,3},
				{5,11},
				{2,2,2,7},
				{3,19},
				{2,29},
				{59},
				{2,2,3,5},
				{61},
				{2,31},
				{3,3,7},
				{2,2,2,2,2,2},
				{5,13},
				{2,3,11},
				{67},
				{2,2,17},
				{3,23},
				{2,5,7},
				{71},
				{2,2,2,3,3},
				{73},
				{2,37},
				{3,5,5},
				{2,2,19},
				{7,11},
				{2,3,13},
				{79},
				{2,2,2,2,5},
				{3,3,3,3},
				{2,41},
				{83},
				{2,2,3,7},
				{5,17},
				{2,43},
				{3,29},
				{2,2,2,11},
				{89},
				{2,3,3,5},
				{7,13},
				{2,2,23},
				{3,31},
				{2,47},
				{5,19},
				{2,2,2,2,2,3},
				{97},
				{2,7,7},
				{3,3,11},
				{2,2,5,5},
				{101},
				{2,3,17},
				{103},
				{2,2,2,13},
				{3,5,7},
				{2,53},
				{107},
				{2,2,3,3,3},
				{109},
				{2,5,11},
				{3,37},
				{2,2,2,2,7},
				{113},
				{2,3,19},
				{5,23},
				{2,2,29},
				{3,3,13},
				{2,59},
				{7,17},
				{2,2,2,3,5},
				{11,11},
				{2,61},
				{3,41},
				{2,2,31},
				{5,5,5},
				{2,3,3,7},
				{127},
				{2,2,2,2,2,2,2},
				{3,43},
				{2,5,13},
				{131},
				{2,2,3,11},
				{7,19},
				{2,67},
				{3,3,3,5},
				{2,2,2,17},
				{137},
				{2,3,23},
				{139},
				{2,2,5,7},
				{3,47},
				{2,71},
				{11,13},
				{2,2,2,2,3,3},
				{5,29},
				{2,73},
				{3,7,7},
				{2,2,37},
				{149},
				{2,3,5,5},
				{151},
				{2,2,2,19},
				{3,3,17},
				{2,7,11},
				{5,31},
				{2,2,3,13},
				{157},
				{2,79},
				{3,53},
				{2,2,2,2,2,5},
				{7,23},
				{2,3,3,3,3},
				{163},
				{2,2,41},
				{3,5,11},
				{2,83},
				{167},
				{2,2,2,3,7},
				{13,13},
				{2,5,17},
				{3,3,19},
				{2,2,43},
				{173},
				{2,3,29},
				{5,5,7},
				{2,2,2,2,11},
				{3,59},
				{2,89},
				{179},
				{2,2,3,3,5},
				{181},
				{2,7,13},
				{3,61},
				{2,2,2,23},
				{5,37},
				{2,3,31},
				{11,17},
				{2,2,47},
				{3,3,3,7},
				{2,5,19},
				{191},
				{2,2,2,2,2,2,3},
				{193},
				{2,97},
				{3,5,13},
				{2,2,7,7},
				{197},
				{2,3,3,11},
				{199},
				{2,2,2,5,5},
				{3,67},
				{2,101},
				{7,29},
				{2,2,3,17},
				{5,41},
				{2,103},
				{3,3,23},
				{2,2,2,2,13},
				{11,19},
				{2,3,5,7},
				{211},
				{2,2,53},
				{3,71},
				{2,107},
				{5,43},
				{2,2,2,3,3,3},
				{7,31},
				{2,109},
				{3,73},
				{2,2,5,11},
				{13,17},
				{2,3,37},
				{223},
				{2,2,2,2,2,7},
				{3,3,5,5},
				{2,113},
				{227},
				{2,2,3,19},
				{229},
				{2,5,23},
				{3,7,11},
				{2,2,2,29},
				{233},
				{2,3,3,13},
				{5,47},
				{2,2,59},
				{3,79},
				{2,7,17},
				{239},
				{2,2,2,2,3,5},
				{241},
				{2,11,11},
				{3,3,3,3,3},
				{2,2,61},
				{5,7,7},
				{2,3,41},
				{13,19},
				{2,2,2,31},
				{3,83},
				{2,5,5,5},
				{251},
				{2,2,3,3,7},
				{11,23},
				{2,127},
				{3,5,17},
				{2,2,2,2,2,2,2,2},
				{257},
				{2,3,43},
				{7,37},
				{2,2,5,13},
				{3,3,29},
				{2,131},
				{263},
				{2,2,2,3,11},
				{5,53},
				{2,7,19},
				{3,89},
				{2,2,67},
				{269},
				{2,3,3,3,5},
				{271},
				{2,2,2,2,17},
				{3,7,13},
				{2,137},
				{5,5,11},
				{2,2,3,23},
				{277},
				{2,139},
				{3,3,31},
				{2,2,2,5,7},
				{281},
				{2,3,47},
				{283},
				{2,2,71},
				{3,5,19},
				{2,11,13},
				{7,41},
				{2,2,2,2,2,3,3},
				{17,17},
				{2,5,29},
				{3,97},
				{2,2,73},
				{293},
				{2,3,7,7},
				{5,59},
				{2,2,2,37},
				{3,3,3,11},
				{2,149},
				{13,23},
				{2,2,3,5,5},
				{7,43},
				{2,151},
				{3,101},
				{2,2,2,2,19},
				{5,61},
				{2,3,3,17},
				{307},
				{2,2,7,11},
				{3,103},
				{2,5,31},
				{311},
				{2,2,2,3,13},
				{313},
				{2,157},
				{3,3,5,7},
				{2,2,79},
				{317},
				{2,3,53},
				{11,29},
				{2,2,2,2,2,2,5},
				{3,107},
				{2,7,23},
				{17,19},
				{2,2,3,3,3,3},
				{5,5,13},
				{2,163},
				{3,109},
				{2,2,2,41},
				{7,47},
				{2,3,5,11},
				{331},
				{2,2,83},
				{3,3,37},
				{2,167},
				{5,67},
				{2,2,2,2,3,7},
				{337},
				{2,13,13},
				{3,113},
				{2,2,5,17},
				{11,31},
				{2,3,3,19},
				{7,7,7},
				{2,2,2,43},
				{3,5,23},
				{2,173},
				{347},
				{2,2,3,29},
				{349},
				{2,5,5,7},
				{3,3,3,13},
				{2,2,2,2,2,11},
				{353},
				{2,3,59},
				{5,71},
				{2,2,89},
				{3,7,17},
				{2,179},
				{359},
				{2,2,2,3,3,5},
				{19,19},
				{2,181},
				{3,11,11},
				{2,2,7,13},
				{5,73},
				{2,3,61},
				{367},
				{2,2,2,2,23},
				{3,3,41},
				{2,5,37},
				{7,53},
				{2,2,3,31},
				{373},
				{2,11,17},
				{3,5,5,5},
				{2,2,2,47},
				{13,29},
				{2,3,3,3,7},
				{379},
				{2,2,5,19},
				{3,127},
				{2,191},
				{383},
				{2,2,2,2,2,2,2,3},
				{5,7,11},
				{2,193},
				{3,3,43},
				{2,2,97},
				{389},
				{2,3,5,13},
				{17,23},
				{2,2,2,7,7},
				{3,131},
				{2,197},
				{5,79},
				{2,2,3,3,11},
				{397},
				{2,199},
				{3,7,19},
				{2,2,2,2,5,5},
				{401},
				{2,3,67},
				{13,31},
				{2,2,101},
				{3,3,3,3,5},
				{2,7,29},
				{11,37},
				{2,2,2,3,17},
				{409},
				{2,5,41},
				{3,137},
				{2,2,103},
				{7,59},
				{2,3,3,23},
				{5,83},
				{2,2,2,2,2,13},
				{3,139},
				{2,11,19},
				{419},
				{2,2,3,5,7},
				{421},
				{2,211},
				{3,3,47},
				{2,2,2,53},
				{5,5,17},
				{2,3,71},
				{7,61},
				{2,2,107},
				{3,11,13},
				{2,5,43},
				{431},
				{2,2,2,2,3,3,3},
				{433},
				{2,7,31},
				{3,5,29},
				{2,2,109},
				{19,23},
				{2,3,73},
				{439},
				{2,2,2,5,11},
				{3,3,7,7},
				{2,13,17},
				{443},
				{2,2,3,37},
				{5,89},
				{2,223},
				{3,149},
				{2,2,2,2,2,2,7},
				{449},
				{2,3,3,5,5},
				{11,41},
				{2,2,113},
				{3,151},
				{2,227},
				{5,7,13},
				{2,2,2,3,19},
				{457},
				{2,229},
				{3,3,3,17},
				{2,2,5,23},
				{461},
				{2,3,7,11},
				{463},
				{2,2,2,2,29},
				{3,5,31},
				{2,233},
				{467},
				{2,2,3,3,13},
				{7,67},
				{2,5,47},
				{3,157},
				{2,2,2,59},
				{11,43},
				{2,3,79},
				{5,5,19},
				{2,2,7,17},
				{3,3,53},
				{2,239},
				{479},
				{2,2,2,2,2,3,5},
				{13,37},
				{2,241},
				{3,7,23},
				{2,2,11,11},
				{5,97},
				{2,3,3,3,3,3},
				{487},
				{2,2,2,61},
				{3,163},
				{2,5,7,7},
				{491},
				{2,2,3,41},
				{17,29},
				{2,13,19},
				{3,3,5,11},
				{2,2,2,2,31},
				{7,71},
				{2,3,83},
				{499},
				{2,2,5,5,5},
				{3,167},
				{2,251},
				{503},
				{2,2,2,3,3,7},
				{5,101},
				{2,11,23},
				{3,13,13},
				{2,2,127},
				{509},
				{2,3,5,17},
				{7,73},
				{2,2,2,2,2,2,2,2,2},
				{3,3,3,19},
				{2,257},
				{5,103},
				{2,2,3,43},
				{11,47},
				{2,7,37},
				{3,173},
				{2,2,2,5,13},
				{521},
				{2,3,3,29},
				{523},
				{2,2,131},
				{3,5,5,7},
				{2,263},
				{17,31},
				{2,2,2,2,3,11},
				{23,23},
				{2,5,53},
				{3,3,59},
				{2,2,7,19},
				{13,41},
				{2,3,89},
				{5,107},
				{2,2,2,67},
				{3,179},
				{2,269},
				{7,7,11},
				{2,2,3,3,3,5},
				{541},
				{2,271},
				{3,181},
				{2,2,2,2,2,17},
				{5,109},
				{2,3,7,13},
				{547},
				{2,2,137},
				{3,3,61},
				{2,5,5,11},
				{19,29},
				{2,2,2,3,23},
				{7,79},
				{2,277},
				{3,5,37},
				{2,2,139},
				{557},
				{2,3,3,31},
				{13,43},
				{2,2,2,2,5,7},
				{3,11,17},
				{2,281},
				{563},
				{2,2,3,47},
				{5,113},
				{2,283},
				{3,3,3,3,7},
				{2,2,2,71},
				{569},
				{2,3,5,19},
				{571},
				{2,2,11,13},
				{3,191},
				{2,7,41},
				{5,5,23},
				{2,2,2,2,2,2,3,3},
				{577},
				{2,17,17},
				{3,193},
				{2,2,5,29},
				{7,83},
				{2,3,97},
				{11,53},
				{2,2,2,73},
				{3,3,5,13},
				{2,293},
				{587},
				{2,2,3,7,7},
				{19,31},
				{2,5,59},
				{3,197},
				{2,2,2,2,37},
				{593},
				{2,3,3,3,11},
				{5,7,17},
				{2,2,149},
				{3,199},
				{2,13,23},
				{599},
				{2,2,2,3,5,5},
				{601},
				{2,7,43},
				{3,3,67},
				{2,2,151},
				{5,11,11},
				{2,3,101},
				{607},
				{2,2,2,2,2,19},
				{3,7,29},
				{2,5,61},
				{13,47},
				{2,2,3,3,17},
				{613},
				{2,307},
				{3,5,41},
				{2,2,2,7,11},
				{617},
				{2,3,103},
				{619},
				{2,2,5,31},
				{3,3,3,23},
				{2,311},
				{7,89},
				{2,2,2,2,3,13},
				{5,5,5,5},
				{2,313},
				{3,11,19},
				{2,2,157},
				{17,37},
				{2,3,3,5,7},
				{631},
				{2,2,2,79},
				{3,211},
				{2,317},
				{5,127},
				{2,2,3,53},
				{7,7,13},
				{2,11,29},
				{3,3,71},
				{2,2,2,2,2,2,2,5},
				{641},
				{2,3,107},
				{643},
				{2,2,7,23},
				{3,5,43},
				{2,17,19},
				{647},
				{2,2,2,3,3,3,3},
				{11,59},
				{2,5,5,13},
				{3,7,31},
				{2,2,163},
				{653},
				{2,3,109},
				{5,131},
				{2,2,2,2,41},
				{3,3,73},
				{2,7,47},
				{659},
				{2,2,3,5,11},
				{661},
				{2,331},
				{3,13,17},
				{2,2,2,83},
				{5,7,19},
				{2,3,3,37},
				{23,29},
				{2,2,167},
				{3,223},
				{2,5,67},
				{11,61},
				{2,2,2,2,2,3,7},
				{673},
				{2,337},
				{3,3,3,5,5},
				{2,2,13,13},
				{677},
				{2,3,113},
				{7,97},
				{2,2,2,5,17},
				{3,227},
				{2,11,31},
				{683},
				{2,2,3,3,19},
				{5,137},
				{2,7,7,7},
				{3,229},
				{2,2,2,2,43},
				{13,53},
				{2,3,5,23},
				{691},
				{2,2,173},
				{3,3,7,11},
				{2,347},
				{5,139},
				{2,2,2,3,29},
				{17,41},
				{2,349},
				{3,233},
				{2,2,5,5,7},
				{701},
				{2,3,3,3,13},
				{19,37},
				{2,2,2,2,2,2,11},
				{3,5,47},
				{2,353},
				{7,101},
				{2,2,3,59},
				{709},
				{2,5,71},
				{3,3,79},
				{2,2,2,89},
				{23,31},
				{2,3,7,17},
				{5,11,13},
				{2,2,179},
				{3,239},
				{2,359},
				{719},
				{2,2,2,2,3,3,5},
				{7,103},
				{2,19,19},
				{3,241},
				{2,2,181},
				{5,5,29},
				{2,3,11,11},
				{727},
				{2,2,2,7,13},
				{3,3,3,3,3,3},
				{2,5,73},
				{17,43},
				{2,2,3,61},
				{733},
				{2,367},
				{3,5,7,7},
				{2,2,2,2,2,23},
				{11,67},
				{2,3,3,41},
				{739},
				{2,2,5,37},
				{3,13,19},
				{2,7,53},
				{743},
				{2,2,2,3,31},
				{5,149},
				{2,373},
				{3,3,83},
				{2,2,11,17},
				{7,107},
				{2,3,5,5,5},
				{751},
				{2,2,2,2,47},
				{3,251},
				{2,13,29},
				{5,151},
				{2,2,3,3,3,7},
				{757},
				{2,379},
				{3,11,23},
				{2,2,2,5,19},
				{761},
				{2,3,127},
				{7,109},
				{2,2,191},
				{3,3,5,17},
				{2,383},
				{13,59},
				{2,2,2,2,2,2,2,2,3},
				{769},
				{2,5,7,11},
				{3,257},
				{2,2,193},
				{773},
				{2,3,3,43},
				{5,5,31},
				{2,2,2,97},
				{3,7,37},
				{2,389},
				{19,41},
				{2,2,3,5,13},
				{11,71},
				{2,17,23},
				{3,3,3,29},
				{2,2,2,2,7,7},
				{5,157},
				{2,3,131},
				{787},
				{2,2,197},
				{3,263},
				{2,5,79},
				{7,113},
				{2,2,2,3,3,11},
				{13,61},
				{2,397},
				{3,5,53},
				{2,2,199},
				{797},
				{2,3,7,19},
				{17,47},
				{2,2,2,2,2,5,5},
				{3,3,89},
				{2,401},
				{11,73},
				{2,2,3,67},
				{5,7,23},
				{2,13,31},
				{3,269},
				{2,2,2,101},
				{809},
				{2,3,3,3,3,5},
				{811},
				{2,2,7,29},
				{3,271},
				{2,11,37},
				{5,163},
				{2,2,2,2,3,17},
				{19,43},
				{2,409},
				{3,3,7,13},
				{2,2,5,41},
				{821},
				{2,3,137},
				{823},
				{2,2,2,103},
				{3,5,5,11},
				{2,7,59},
				{827},
				{2,2,3,3,23},
				{829},
				{2,5,83},
				{3,277},
				{2,2,2,2,2,2,13},
				{7,7,17},
				{2,3,139},
				{5,167},
				{2,2,11,19},
				{3,3,3,31},
				{2,419},
				{839},
				{2,2,2,3,5,7},
				{29,29},
				{2,421},
				{3,281},
				{2,2,211},
				{5,13,13},
				{2,3,3,47},
				{7,11,11},
				{2,2,2,2,53},
				{3,283},
				{2,5,5,17},
				{23,37},
				{2,2,3,71},
				{853},
				{2,7,61},
				{3,3,5,19},
				{2,2,2,107},
				{857},
				{2,3,11,13},
				{859},
				{2,2,5,43},
				{3,7,41},
				{2,431},
				{863},
				{2,2,2,2,2,3,3,3},
				{5,173},
				{2,433},
				{3,17,17},
				{2,2,7,31},
				{11,79},
				{2,3,5,29},
				{13,67},
				{2,2,2,109},
				{3,3,97},
				{2,19,23},
				{5,5,5,7},
				{2,2,3,73},
				{877},
				{2,439},
				{3,293},
				{2,2,2,2,5,11},
				{881},
				{2,3,3,7,7},
				{883},
				{2,2,13,17},
				{3,5,59},
				{2,443},
				{887},
				{2,2,2,3,37},
				{7,127},
				{2,5,89},
				{3,3,3,3,11},
				{2,2,223},
				{19,47},
				{2,3,149},
				{5,179},
				{2,2,2,2,2,2,2,7},
				{3,13,23},
				{2,449},
				{29,31},
				{2,2,3,3,5,5},
				{17,53},
				{2,11,41},
				{3,7,43},
				{2,2,2,113},
				{5,181},
				{2,3,151},
				{907},
				{2,2,227},
				{3,3,101},
				{2,5,7,13},
				{911},
				{2,2,2,2,3,19},
				{11,83},
				{2,457},
				{3,5,61},
				{2,2,229},
				{7,131},
				{2,3,3,3,17},
				{919},
				{2,2,2,5,23},
				{3,307},
				{2,461},
				{13,71},
				{2,2,3,7,11},
				{5,5,37},
				{2,463},
				{3,3,103},
				{2,2,2,2,2,29},
				{929},
				{2,3,5,31},
				{7,7,19},
				{2,2,233},
				{3,311},
				{2,467},
				{5,11,17},
				{2,2,2,3,3,13},
				{937},
				{2,7,67},
				{3,313},
				{2,2,5,47},
				{941},
				{2,3,157},
				{23,41},
				{2,2,2,2,59},
				{3,3,3,5,7},
				{2,11,43},
				{947},
				{2,2,3,79},
				{13,73},
				{2,5,5,19},
				{3,317},
				{2,2,2,7,17},
				{953},
				{2,3,3,53},
				{5,191},
				{2,2,239},
				{3,11,29},
				{2,479},
				{7,137},
				{2,2,2,2,2,2,3,5},
				{31,31},
				{2,13,37},
				{3,3,107},
				{2,2,241},
				{5,193},
				{2,3,7,23},
				{967},
				{2,2,2,11,11},
				{3,17,19},
				{2,5,97},
				{971},
				{2,2,3,3,3,3,3},
				{7,139},
				{2,487},
				{3,5,5,13},
				{2,2,2,2,61},
				{977},
				{2,3,163},
				{11,89},
				{2,2,5,7,7},
				{3,3,109},
				{2,491},
				{983},
				{2,2,2,3,41},
				{5,197},
				{2,17,29},
				{3,7,47},
				{2,2,13,19},
				{23,43},
				{2,3,3,5,11},
				{991},
				{2,2,2,2,2,31},
				{3,331},
				{2,7,71},
				{5,199},
				{2,2,3,83},
				{997},
				{2,499},
				{3,3,3,37},
				{2,2,2,5,5,5},
				{7,11,13},
				{2,3,167},
				{17,59},
				{2,2,251},
				{3,5,67},
				{2,503},
				{19,53},
				{2,2,2,2,3,3,7},
				{1009},
				{2,5,101},
				{3,337},
				{2,2,11,23},
				{1013},
				{2,3,13,13},
				{5,7,29},
				{2,2,2,127},
				{3,3,113},
				{2,509},
				{1019},
				{2,2,3,5,17},
				{1021},
				{2,7,73},
				{3,11,31},
				{2,2,2,2,2,2,2,2,2,2},
				{5,5,41},
				{2,3,3,3,19},
				{13,79},
				{2,2,257},
				{3,7,7,7},
				{2,5,103},
				{1031},
				{2,2,2,3,43},
				{1033},
				{2,11,47},
				{3,3,5,23},
				{2,2,7,37},
				{17,61},
				{2,3,173},
				{1039},
				{2,2,2,2,5,13},
				{3,347},
				{2,521},
				{7,149},
				{2,2,3,3,29},
				{5,11,19},
				{2,523},
				{3,349},
				{2,2,2,131},
				{1049},
				{2,3,5,5,7},
				{1051},
				{2,2,263},
				{3,3,3,3,13},
				{2,17,31},
				{5,211},
				{2,2,2,2,2,3,11},
				{7,151},
				{2,23,23},
				{3,353},
				{2,2,5,53},
				{1061},
				{2,3,3,59},
				{1063},
				{2,2,2,7,19},
				{3,5,71},
				{2,13,41},
				{11,97},
				{2,2,3,89},
				{1069},
				{2,5,107},
				{3,3,7,17},
				{2,2,2,2,67},
				{29,37},
				{2,3,179},
				{5,5,43},
				{2,2,269},
				{3,359},
				{2,7,7,11},
				{13,83},
				{2,2,2,3,3,3,5},
				{23,47},
				{2,541},
				{3,19,19},
				{2,2,271},
				{5,7,31},
				{2,3,181},
				{1087},
				{2,2,2,2,2,2,17},
				{3,3,11,11},
				{2,5,109},
				{1091},
				{2,2,3,7,13},
				{1093},
				{2,547},
				{3,5,73},
				{2,2,2,137},
				{1097},
				{2,3,3,61},
				{7,157},
				{2,2,5,5,11},
				{3,367},
				{2,19,29},
				{1103},
				{2,2,2,2,3,23},
				{5,13,17},
				{2,7,79},
				{3,3,3,41},
				{2,2,277},
				{1109},
				{2,3,5,37},
				{11,101},
				{2,2,2,139},
				{3,7,53},
				{2,557},
				{5,223},
				{2,2,3,3,31},
				{1117},
				{2,13,43},
				{3,373},
				{2,2,2,2,2,5,7},
				{19,59},
				{2,3,11,17},
				{1123},
				{2,2,281},
				{3,3,5,5,5},
				{2,563},
				{7,7,23},
				{2,2,2,3,47},
				{1129},
				{2,5,113},
				{3,13,29},
				{2,2,283},
				{11,103},
				{2,3,3,3,3,7},
				{5,227},
				{2,2,2,2,71},
				{3,379},
				{2,569},
				{17,67},
				{2,2,3,5,19},
				{7,163},
				{2,571},
				{3,3,127},
				{2,2,2,11,13},
				{5,229},
				{2,3,191},
				{31,37},
				{2,2,7,41},
				{3,383},
				{2,5,5,23},
				{1151},
				{2,2,2,2,2,2,2,3,3},
				{1153},
				{2,577},
				{3,5,7,11},
				{2,2,17,17},
				{13,89},
				{2,3,193},
				{19,61},
				{2,2,2,5,29},
				{3,3,3,43},
				{2,7,83},
				{1163},
				{2,2,3,97},
				{5,233},
				{2,11,53},
				{3,389},
				{2,2,2,2,73},
				{7,167},
				{2,3,3,5,13},
				{1171},
				{2,2,293},
				{3,17,23},
				{2,587},
				{5,5,47},
				{2,2,2,3,7,7},
				{11,107},
				{2,19,31},
				{3,3,131},
				{2,2,5,59},
				{1181},
				{2,3,197},
				{7,13,13},
				{2,2,2,2,2,37},
				{3,5,79},
				{2,593},
				{1187},
				{2,2,3,3,3,11},
				{29,41},
				{2,5,7,17},
				{3,397},
				{2,2,2,149},
				{1193},
				{2,3,199},
				{5,239},
				{2,2,13,23},
				{3,3,7,19},
				{2,599},
				{11,109},
				{2,2,2,2,3,5,5},
				{1201},
				{2,601},
				{3,401},
				{2,2,7,43},
				{5,241},
				{2,3,3,67},
				{17,71},
				{2,2,2,151},
				{3,13,31},
				{2,5,11,11},
				{7,173},
				{2,2,3,101},
				{1213},
				{2,607},
				{3,3,3,3,3,5},
				{2,2,2,2,2,2,19},
				{1217},
				{2,3,7,29},
				{23,53},
				{2,2,5,61},
				{3,11,37},
				{2,13,47},
				{1223},
				{2,2,2,3,3,17},
				{5,5,7,7},
				{2,613},
				{3,409},
				{2,2,307},
				{1229},
				{2,3,5,41},
				{1231},
				{2,2,2,2,7,11},
				{3,3,137},
				{2,617},
				{5,13,19},
				{2,2,3,103},
				{1237},
				{2,619},
				{3,7,59},
				{2,2,2,5,31},
				{17,73},
				{2,3,3,3,23},
				{11,113},
				{2,2,311},
				{3,5,83},
				{2,7,89},
				{29,43},
				{2,2,2,2,2,3,13},
				{1249},
				{2,5,5,5,5},
				{3,3,139},
				{2,2,313},
				{7,179},
				{2,3,11,19},
				{5,251},
				{2,2,2,157},
				{3,419},
				{2,17,37},
				{1259},
				{2,2,3,3,5,7},
				{13,97},
				{2,631},
				{3,421},
				{2,2,2,2,79},
				{5,11,23},
				{2,3,211},
				{7,181},
				{2,2,317},
				{3,3,3,47},
				{2,5,127},
				{31,41},
				{2,2,2,3,53},
				{19,67},
				{2,7,7,13},
				{3,5,5,17},
				{2,2,11,29},
				{1277},
				{2,3,3,71},
				{1279},
				{2,2,2,2,2,2,2,2,5},
				{3,7,61},
				{2,641},
				{1283},
				{2,2,3,107},
				{5,257},
				{2,643},
				{3,3,11,13},
				{2,2,2,7,23},
				{1289},
				{2,3,5,43},
				{1291},
				{2,2,17,19},
				{3,431},
				{2,647},
				{5,7,37},
				{2,2,2,2,3,3,3,3},
				{1297},
				{2,11,59},
				{3,433},
				{2,2,5,5,13},
				{1301},
				{2,3,7,31},
				{1303},
				{2,2,2,163},
				{3,3,5,29},
				{2,653},
				{1307},
				{2,2,3,109},
				{7,11,17},
				{2,5,131},
				{3,19,23},
				{2,2,2,2,2,41},
				{13,101},
				{2,3,3,73},
				{5,263},
				{2,2,7,47},
				{3,439},
				{2,659},
				{1319},
				{2,2,2,3,5,11},
				{1321},
				{2,661},
				{3,3,3,7,7},
				{2,2,331},
				{5,5,53},
				{2,3,13,17},
				{1327},
				{2,2,2,2,83},
				{3,443},
				{2,5,7,19},
				{11,11,11},
				{2,2,3,3,37},
				{31,43},
				{2,23,29},
				{3,5,89},
				{2,2,2,167},
				{7,191},
				{2,3,223},
				{13,103},
				{2,2,5,67},
				{3,3,149},
				{2,11,61},
				{17,79},
				{2,2,2,2,2,2,3,7},
				{5,269},
				{2,673},
				{3,449},
				{2,2,337},
				{19,71},
				{2,3,3,3,5,5},
				{7,193},
				{2,2,2,13,13},
				{3,11,41},
				{2,677},
				{5,271},
				{2,2,3,113},
				{23,59},
				{2,7,97},
				{3,3,151},
				{2,2,2,2,5,17},
				{1361},
				{2,3,227},
				{29,47},
				{2,2,11,31},
				{3,5,7,13},
				{2,683},
				{1367},
				{2,2,2,3,3,19},
				{37,37},
				{2,5,137},
				{3,457},
				{2,2,7,7,7},
				{1373},
				{2,3,229},
				{5,5,5,11},
				{2,2,2,2,2,43},
				{3,3,3,3,17},
				{2,13,53},
				{7,197},
				{2,2,3,5,23},
				{1381},
				{2,691},
				{3,461},
				{2,2,2,173},
				{5,277},
				{2,3,3,7,11},
				{19,73},
				{2,2,347},
				{3,463},
				{2,5,139},
				{13,107},
				{2,2,2,2,3,29},
				{7,199},
				{2,17,41},
				{3,3,5,31},
				{2,2,349},
				{11,127},
				{2,3,233},
				{1399},
				{2,2,2,5,5,7},
				{3,467},
				{2,701},
				{23,61},
				{2,2,3,3,3,13},
				{5,281},
				{2,19,37},
				{3,7,67},
				{2,2,2,2,2,2,2,11},
				{1409},
				{2,3,5,47},
				{17,83},
				{2,2,353},
				{3,3,157},
				{2,7,101},
				{5,283},
				{2,2,2,3,59},
				{13,109},
				{2,709},
				{3,11,43},
				{2,2,5,71},
				{7,7,29},
				{2,3,3,79},
				{1423},
				{2,2,2,2,89},
				{3,5,5,19},
				{2,23,31},
				{1427},
				{2,2,3,7,17},
				{1429},
				{2,5,11,13},
				{3,3,3,53},
				{2,2,2,179},
				{1433},
				{2,3,239},
				{5,7,41},
				{2,2,359},
				{3,479},
				{2,719},
				{1439},
				{2,2,2,2,2,3,3,5},
				{11,131},
				{2,7,103},
				{3,13,37},
				{2,2,19,19},
				{5,17,17},
				{2,3,241},
				{1447},
				{2,2,2,181},
				{3,3,7,23},
				{2,5,5,29},
				{1451},
				{2,2,3,11,11},
				{1453},
				{2,727},
				{3,5,97},
				{2,2,2,2,7,13},
				{31,47},
				{2,3,3,3,3,3,3},
				{1459},
				{2,2,5,73},
				{3,487},
				{2,17,43},
				{7,11,19},
				{2,2,2,3,61},
				{5,293},
				{2,733},
				{3,3,163},
				{2,2,367},
				{13,113},
				{2,3,5,7,7},
				{1471},
				{2,2,2,2,2,2,23},
				{3,491},
				{2,11,67},
				{5,5,59},
				{2,2,3,3,41},
				{7,211},
				{2,739},
				{3,17,29},
				{2,2,2,5,37},
				{1481},
				{2,3,13,19},
				{1483},
				{2,2,7,53},
				{3,3,3,5,11},
				{2,743},
				{1487},
				{2,2,2,2,3,31},
				{1489},
				{2,5,149},
				{3,7,71},
				{2,2,373},
				{1493},
				{2,3,3,83},
				{5,13,23},
				{2,2,2,11,17},
				{3,499},
				{2,7,107},
				{1499},
				{2,2,3,5,5,5},
				{19,79},
				{2,751},
				{3,3,167},
				{2,2,2,2,2,47},
				{5,7,43},
				{2,3,251},
				{11,137},
				{2,2,13,29},
				{3,503},
				{2,5,151},
				{1511},
				{2,2,2,3,3,3,7},
				{17,89},
				{2,757},
				{3,5,101},
				{2,2,379},
				{37,41},
				{2,3,11,23},
				{7,7,31},
				{2,2,2,2,5,19},
				{3,3,13,13},
				{2,761},
				{1523},
				{2,2,3,127},
				{5,5,61},
				{2,7,109},
				{3,509},
				{2,2,2,191},
				{11,139},
				{2,3,3,5,17},
				{1531},
				{2,2,383},
				{3,7,73},
				{2,13,59},
				{5,307},
				{2,2,2,2,2,2,2,2,2,3},
				{29,53},
				{2,769},
				{3,3,3,3,19},
				{2,2,5,7,11},
				{23,67},
				{2,3,257},
				{1543},
				{2,2,2,193},
				{3,5,103},
				{2,773},
				{7,13,17},
				{2,2,3,3,43},
				{1549},
				{2,5,5,31},
				{3,11,47},
				{2,2,2,2,97},
				{1553},
				{2,3,7,37},
				{5,311},
				{2,2,389},
				{3,3,173},
				{2,19,41},
				{1559},
				{2,2,2,3,5,13},
				{7,223},
				{2,11,71},
				{3,521},
				{2,2,17,23},
				{5,313},
				{2,3,3,3,29},
				{1567},
				{2,2,2,2,2,7,7},
				{3,523},
				{2,5,157},
				{1571},
				{2,2,3,131},
				{11,11,13},
				{2,787},
				{3,3,5,5,7},
				{2,2,2,197},
				{19,83},
				{2,3,263},
				{1579},
				{2,2,5,79},
				{3,17,31},
				{2,7,113},
				{1583},
				{2,2,2,2,3,3,11},
				{5,317},
				{2,13,61},
				{3,23,23},
				{2,2,397},
				{7,227},
				{2,3,5,53},
				{37,43},
				{2,2,2,199},
				{3,3,3,59},
				{2,797},
				{5,11,29},
				{2,2,3,7,19},
				{1597},
				{2,17,47},
				{3,13,41},
				{2,2,2,2,2,2,5,5},
				{1601},
				{2,3,3,89},
				{7,229},
				{2,2,401},
				{3,5,107},
				{2,11,73},
				{1607},
				{2,2,2,3,67},
				{1609},
				{2,5,7,23},
				{3,3,179},
				{2,2,13,31},
				{1613},
				{2,3,269},
				{5,17,19},
				{2,2,2,2,101},
				{3,7,7,11},
				{2,809},
				{1619},
				{2,2,3,3,3,3,5},
				{1621},
				{2,811},
				{3,541},
				{2,2,2,7,29},
				{5,5,5,13},
				{2,3,271},
				{1627},
				{2,2,11,37},
				{3,3,181},
				{2,5,163},
				{7,233},
				{2,2,2,2,2,3,17},
				{23,71},
				{2,19,43},
				{3,5,109},
				{2,2,409},
				{1637},
				{2,3,3,7,13},
				{11,149},
				{2,2,2,5,41},
				{3,547},
				{2,821},
				{31,53},
				{2,2,3,137},
				{5,7,47},
				{2,823},
				{3,3,3,61},
				{2,2,2,2,103},
				{17,97},
				{2,3,5,5,11},
				{13,127},
				{2,2,7,59},
				{3,19,29},
				{2,827},
				{5,331},
				{2,2,2,3,3,23},
				{1657},
				{2,829},
				{3,7,79},
				{2,2,5,83},
				{11,151},
				{2,3,277},
				{1663},
				{2,2,2,2,2,2,2,13},
				{3,3,5,37},
				{2,7,7,17},
				{1667},
				{2,2,3,139},
				{1669},
				{2,5,167},
				{3,557},
				{2,2,2,11,19},
				{7,239},
				{2,3,3,3,31},
				{5,5,67},
				{2,2,419},
				{3,13,43},
				{2,839},
				{23,73},
				{2,2,2,2,3,5,7},
				{41,41},
				{2,29,29},
				{3,3,11,17},
				{2,2,421},
				{5,337},
				{2,3,281},
				{7,241},
				{2,2,2,211},
				{3,563},
				{2,5,13,13},
				{19,89},
				{2,2,3,3,47},
				{1693},
				{2,7,11,11},
				{3,5,113},
				{2,2,2,2,2,53},
				{1697},
				{2,3,283},
				{1699},
				{2,2,5,5,17},
				{3,3,3,3,3,7},
				{2,23,37},
				{13,131},
				{2,2,2,3,71},
				{5,11,31},
				{2,853},
				{3,569},
				{2,2,7,61},
				{1709},
				{2,3,3,5,19},
				{29,59},
				{2,2,2,2,107},
				{3,571},
				{2,857},
				{5,7,7,7},
				{2,2,3,11,13},
				{17,101},
				{2,859},
				{3,3,191},
				{2,2,2,5,43},
				{1721},
				{2,3,7,41},
				{1723},
				{2,2,431},
				{3,5,5,23},
				{2,863},
				{11,157},
				{2,2,2,2,2,2,3,3,3},
				{7,13,19},
				{2,5,173},
				{3,577},
				{2,2,433},
				{1733},
				{2,3,17,17},
				{5,347},
				{2,2,2,7,31},
				{3,3,193},
				{2,11,79},
				{37,47},
				{2,2,3,5,29},
				{1741},
				{2,13,67},
				{3,7,83},
				{2,2,2,2,109},
				{5,349},
				{2,3,3,97},
				{1747},
				{2,2,19,23},
				{3,11,53},
				{2,5,5,5,7},
				{17,103},
				{2,2,2,3,73},
				{1753},
				{2,877},
				{3,3,3,5,13},
				{2,2,439},
				{7,251},
				{2,3,293},
				{1759},
				{2,2,2,2,2,5,11},
				{3,587},
				{2,881},
				{41,43},
				{2,2,3,3,7,7},
				{5,353},
				{2,883},
				{3,19,31},
				{2,2,2,13,17},
				{29,61},
				{2,3,5,59},
				{7,11,23},
				{2,2,443},
				{3,3,197},
				{2,887},
				{5,5,71},
				{2,2,2,2,3,37},
				{1777},
				{2,7,127},
				{3,593},
				{2,2,5,89},
				{13,137},
				{2,3,3,3,3,11},
				{1783},
				{2,2,2,223},
				{3,5,7,17},
				{2,19,47},
				{1787},
				{2,2,3,149},
				{1789},
				{2,5,179},
				{3,3,199},
				{2,2,2,2,2,2,2,2,7},
				{11,163},
				{2,3,13,23},
				{5,359},
				{2,2,449},
				{3,599},
				{2,29,31},
				{7,257},
				{2,2,2,3,3,5,5},
				{1801},
				{2,17,53},
				{3,601},
				{2,2,11,41},
				{5,19,19},
				{2,3,7,43},
				{13,139},
				{2,2,2,2,113},
				{3,3,3,67},
				{2,5,181},
				{1811},
				{2,2,3,151},
				{7,7,37},
				{2,907},
				{3,5,11,11},
				{2,2,2,227},
				{23,79},
				{2,3,3,101},
				{17,107},
				{2,2,5,7,13},
				{3,607},
				{2,911},
				{1823},
				{2,2,2,2,2,3,19},
				{5,5,73},
				{2,11,83},
				{3,3,7,29},
				{2,2,457},
				{31,59},
				{2,3,5,61},
				{1831},
				{2,2,2,229},
				{3,13,47},
				{2,7,131},
				{5,367},
				{2,2,3,3,3,17},
				{11,167},
				{2,919},
				{3,613},
				{2,2,2,2,5,23},
				{7,263},
				{2,3,307},
				{19,97},
				{2,2,461},
				{3,3,5,41},
				{2,13,71},
				{1847},
				{2,2,2,3,7,11},
				{43,43},
				{2,5,5,37},
				{3,617},
				{2,2,463},
				{17,109},
				{2,3,3,103},
				{5,7,53},
				{2,2,2,2,2,2,29},
				{3,619},
				{2,929},
				{11,13,13},
				{2,2,3,5,31},
				{1861},
				{2,7,7,19},
				{3,3,3,3,23},
				{2,2,2,233},
				{5,373},
				{2,3,311},
				{1867},
				{2,2,467},
				{3,7,89},
				{2,5,11,17},
				{1871},
				{2,2,2,2,3,3,13},
				{1873},
				{2,937},
				{3,5,5,5,5},
				{2,2,7,67},
				{1877},
				{2,3,313},
				{1879},
				{2,2,2,5,47},
				{3,3,11,19},
				{2,941},
				{7,269},
				{2,2,3,157},
				{5,13,29},
				{2,23,41},
				{3,17,37},
				{2,2,2,2,2,59},
				{1889},
				{2,3,3,3,5,7},
				{31,61},
				{2,2,11,43},
				{3,631},
				{2,947},
				{5,379},
				{2,2,2,3,79},
				{7,271},
				{2,13,73},
				{3,3,211},
				{2,2,5,5,19},
				{1901},
				{2,3,317},
				{11,173},
				{2,2,2,2,7,17},
				{3,5,127},
				{2,953},
				{1907},
				{2,2,3,3,53},
				{23,83},
				{2,5,191},
				{3,7,7,13},
				{2,2,2,239},
				{1913},
				{2,3,11,29},
				{5,383},
				{2,2,479},
				{3,3,3,71},
				{2,7,137},
				{19,101},
				{2,2,2,2,2,2,2,3,5},
				{17,113},
				{2,31,31},
				{3,641},
				{2,2,13,37},
				{5,5,7,11},
				{2,3,3,107},
				{41,47},
				{2,2,2,241},
				{3,643},
				{2,5,193},
				{1931},
				{2,2,3,7,23},
				{1933},
				{2,967},
				{3,3,5,43},
				{2,2,2,2,11,11},
				{13,149},
				{2,3,17,19},
				{7,277},
				{2,2,5,97},
				{3,647},
				{2,971},
				{29,67},
				{2,2,2,3,3,3,3,3},
				{5,389},
				{2,7,139},
				{3,11,59},
				{2,2,487},
				{1949},
				{2,3,5,5,13},
				{1951},
				{2,2,2,2,2,61},
				{3,3,7,31},
				{2,977},
				{5,17,23},
				{2,2,3,163},
				{19,103},
				{2,11,89},
				{3,653},
				{2,2,2,5,7,7},
				{37,53},
				{2,3,3,109},
				{13,151},
				{2,2,491},
				{3,5,131},
				{2,983},
				{7,281},
				{2,2,2,2,3,41},
				{11,179},
				{2,5,197},
				{3,3,3,73},
				{2,2,17,29},
				{1973},
				{2,3,7,47},
				{5,5,79},
				{2,2,2,13,19},
				{3,659},
				{2,23,43},
				{1979},
				{2,2,3,3,5,11},
				{7,283},
				{2,991},
				{3,661},
				{2,2,2,2,2,2,31},
				{5,397},
				{2,3,331},
				{1987},
				{2,2,7,71},
				{3,3,13,17},
				{2,5,199},
				{11,181},
				{2,2,2,3,83},
				{1993},
				{2,997},
				{3,5,7,19},
				{2,2,499},
				{1997},
				{2,3,3,3,37},
				{1999},
				{2,2,2,2,5,5,5},
				{3,23,29},
				{2,7,11,13},
				{2003},
				{2,2,3,167},
				{5,401},
				{2,17,59},
				{3,3,223},
				{2,2,2,251},
				{7,7,41},
				{2,3,5,67},
				{2011},
				{2,2,503},
				{3,11,61},
				{2,19,53},
				{5,13,31},
				{2,2,2,2,2,3,3,7},
				{2017},
				{2,1009},
				{3,673},
				{2,2,5,101},
				{43,47},
				{2,3,337},
				{7,17,17},
				{2,2,2,11,23},
				{3,3,3,3,5,5},
				{2,1013},
				{2027},
				{2,2,3,13,13},
				{2029},
				{2,5,7,29},
				{3,677},
				{2,2,2,2,127},
				{19,107},
				{2,3,3,113},
				{5,11,37},
				{2,2,509},
				{3,7,97},
				{2,1019},
				{2039},
				{2,2,2,3,5,17}
				// remove the last , 
				};
		return new BigInteger(preCalc.length-1+"");
	}

	private class evaluatedNumber{
		public int evaluation;
		public BigInteger number;
		
		public evaluatedNumber(int eval, BigInteger number){
			this.evaluation = eval;
			this.number = number;
		}
	}
}
