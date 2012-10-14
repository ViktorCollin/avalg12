import java.math.BigInteger;

public class Tools {
	/**
	 * From GMP: 16.5.4 Perfect Power
	 * 
	 * Detecting perfect powers is required by some factorization algorithms.
	 * Currently mpz_perfect_power_p is implemented using repeated Nth root
	 * extractions, though naturally only prime roots need to be considered.
	 * (See Nth Root Algorithm.)
	 * 
	 * If a prime divisor p with multiplicity e can be found, then only roots
	 * which are divisors of e need to be considered, much reducing the work
	 * necessary. To this end divisibility by a set of small primes is checked.
	 */

	/**
	 * http://gmplib.org/manual/Nth-Root-Algorithm.html#Nth-Root-Algorithm
	 * http://en.wikipedia.org/wiki/Nth_root_algorithm
	 * 
	 * 
	 * 
	 */
	public static BigInteger calculate_nth_root(BigInteger a, int n) {
		
		// TODO:
		// The initial approximation a[1] is generated bitwise by successively
		// powering a trial root with or without new 1 bits, aiming to be just
		// above the true root. The iteration converges quadratically when
		// started from a good approximation. When n is large more initial bits
		// are needed to get good convergence. The current implementation is not
		// particularly well optimized.

		BigInteger x = BigInteger.valueOf(5); // FIXME

		while (true) {
			BigInteger p = x.multiply(BigInteger.valueOf(n - 1));
			BigInteger q = a.divide(x.pow(n - 1));

			BigInteger xNext = p.add(q).divide(BigInteger.valueOf(n));

			if (xNext.equals(x))
				break;

			x = xNext;
		}

		return x;
	}
}
