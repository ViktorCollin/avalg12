import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;


public class PollardsRho implements FactoringMethod {
	private SmallNumbers small = null;
	
	@Override
	public Vector<BigInteger> factorize(BigInteger number) {
		if(PRECALCULATED){
			small = new SmallNumbers();
		}
		Vector<BigInteger> factors = new Vector<BigInteger>();
		Queue<BigInteger> q = new LinkedList<BigInteger>();
		q.add(number);
		BigInteger nonTrivial;
		boolean failed = false;
		while(!q.isEmpty() && !failed){
			number = q.poll();
			if(PRECALCULATED){
				Vector<BigInteger> fact = small.factorize(number);
				if(fact != null) {
					factors.addAll(fact);
					continue;
				}

			}
			nonTrivial = pollardsRoh(number);
			if(nonTrivial != null){
				if(!nonTrivial.equals(BigInteger.ZERO)){
					if(DEBUGLEVEL > 0){
						System.err.println("###########################################################");
						System.err.println(nonTrivial +" and "+ number.divide(nonTrivial) +" Was found to be non trivial factors of " + number);
						System.err.println("###########################################################\n");
					}
					q.add(nonTrivial);
					q.add(number.divide(nonTrivial));
				} else {
					if(DEBUGLEVEL  > 0){
						System.err.println("###########################################################");
						System.err.println(number + " Was found to be a PRIME!");
						System.err.println("###########################################################\n");
					}
					factors.add(number);
				}
			}else{
				if(DEBUGLEVEL > 0){
					System.err.println("###########################################################");
					System.err.println("NO non-trivial factor was found of " + number);
					System.err.println("###########################################################\n");
				}
				failed = true;
				return null;
			}

		}
		return factors;

	}
	
	 BigInteger pollardsRoh(BigInteger number){
			if(number.isProbablePrime(CERTAINTY )){
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

		@Override
		public String methodName() {
			return "Pollard's Rho method";
		}

}
