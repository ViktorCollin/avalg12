import java.math.BigInteger;
import java.util.Vector;


public class SmallNumbers implements FactoringMethod {
	@Override
	public Vector<BigInteger> factorize(BigInteger number) {
		if(number.compareTo(Constants.PrecalculatedSize) < 1){
			Vector<BigInteger> factors = new Vector<BigInteger>();
			for(int factor : Constants.PrecalculatedFactors[number.intValue()]){
				
				factors.add(new BigInteger(factor+""));
			}
			if(DEBUGLEVEL > 0){
				System.err.println("###########################################################");
				System.err.println(number + " Was found to be a small number and it's factors are " + factors.toString());
				System.err.println("###########################################################\n");
			}
			return factors;
		}
		return null;
	}

	@Override
	public String methodName() {
		return "Small Numbers method";
	}
}
